/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Panos
 */
public class PanelRules {
    /*
     An array containing the positions of all the soldiers of the board     
     */
     ArrayList<Soldier> gamePieces                    = new ArrayList<Soldier>();
     /*
      An array containing all the legal jump positions.
      */     
     ArrayList<SearchNode> jumpPositions              = new ArrayList<SearchNode>();
     /*
      Also used for the DFS of the legal jump positions
      */     
     ArrayList<ArrayList<SearchNode>> tempSearchNode  = new ArrayList<ArrayList<SearchNode>>();
     /*
      * An array containing taboo points, positions on the map already jumped
      */     
     ArrayList<Point>      tabooPositions = new ArrayList<Point>();
     
     enum Orientation {UP, DOWN};
     enum Movement {LEFT, RIGHT , FORWARD};     
     
     
     public PanelRules(ArrayList<Soldier> gamePieces){
         this.gamePieces = gamePieces;
     }
     public void updatePieces(ArrayList<Soldier> gamePieces){
         this.gamePieces = gamePieces;
     }
     public static boolean isValidSquare(int i, int j){
             if( (i==0||i==8) && (j>1 && j<7) 
               ||(i==1||i==7) && (j>0 && j<7)
               ||(i==2||i==6) && (j>0 && j<8)
               ||(i==3||i==5) && (j>=0 && j<8)
               || (i==4))
                return true;
            return false;
        }
     //Need to fix the isValidSoldier method.
     //Must search within the gamePieces List and return if
        public static boolean isValidSoldierSquare(int i, int j){
             if( (j==0||j==8) && (i>1 && i<7) 
               ||(j==1) && (i>0 && i<8)
               ||(j==7) && (i>0 && i<7)
               ||(j==2) && (i>1 && i<7)
               ||(j==6) && (i>0 && i<8)
               ||(j==3) && (i==4)
               ||(j==5) && (i>2 && i <6))
                return true;
            return false;
        }
        /*
         *Self explanatory function
         * Will Return true if it contains a soldier with the given colour
         * Attention!! Will return true if it contains a soldier whose color differs from the test color inserted
         * 
         */
        public boolean containsSoldier(Soldier test, ArrayList<Soldier> gamePiece){
            for (int i=0;i<gamePiece.size();i++){
                if(test.i == gamePiece.get(i).i && test.j == gamePiece.get(i).j && test.C.equals(gamePiece.get(i).C)){
                    return true;
                }
            }
            return false;
        }
        /**
         * For soldier test, arrayList gamepieces, returns true 
         * if the x's and y's are not used in the game piece array
         * 
         * */
        public boolean isEmpty(Soldier test, ArrayList<Soldier> gamePiece){
            for (int i=0;i<gamePiece.size();i++)
                if(test.i == gamePiece.get(i).i && test.j == gamePiece.get(i).j)
                    return false;
            return true;
        }
        public boolean isLegalMove(Point p , ArrayList<Point> poitnlist){
            for (int i=0;i<poitnlist.size();i++)
                if(p.x == poitnlist.get(i).x && p.y == poitnlist.get(i).y)
                    return true;
            return false;
        }
         
        /*
         * All you need for the legal moves is the color of the soldier to be moved
         * if the soldier is a king, then both directions will be checked
         */
        public ArrayList<Point> getLegalMoves(Soldier c){
            ArrayList<Point> legalMoves = new ArrayList<Point>();
            if(!containsSoldier(c, gamePieces)) 
                return legalMoves;
            
            int positionX = c.i;
            int positionY = c.j;
            if(c.C== Color.black){
                getMovingPositions(new Point(positionX, positionY), Color.black, Movement.LEFT);
                getMovingPositions(new Point(positionX, positionY), Color.black, Movement.RIGHT);
                getMovingPositions(new Point(positionX, positionY), Color.black, Movement.FORWARD);
            }else if(c.C == Color.red){
                getMovingPositions(new Point(positionX, positionY), Color.red, Movement.LEFT);
                getMovingPositions(new Point(positionX, positionY), Color.red, Movement.RIGHT);
                getMovingPositions(new Point(positionX, positionY), Color.red, Movement.FORWARD);
            }
            return legalMoves;
        }
         /**
     *
     * @param c a point on the board, regarding the position of the piece
     * @param color describes the orientation of the move, with possible moves UP or DOWN
     * @param m Self explanatory parameter, defining if the move is a LEFT, RIGHT OR FORWARD
     * @return
     */
        public ArrayList<Point> getMovingPositions(int positionX, int positionY, Color col){
            ArrayList<Point> mp = new ArrayList<Point>();
            Point temp;
            temp = getMovingPositions(new Point(positionX, positionY), col, Movement.LEFT);
            if( temp.x!=0 || temp.y!=0)
                mp.add(temp);
            
            temp =getMovingPositions(new Point(positionX, positionY), col, Movement.RIGHT);
            if( temp.x!=0 || temp.y!=0)
                mp.add(temp);
            
            temp =getMovingPositions(new Point(positionX, positionY), col , Movement.FORWARD);
            if( temp.x!=0 || temp.y!=0)
                mp.add(temp);
                 
            return mp;
            
        }
        /**
         * Returns the point of the position given the point of the movement,
         * the color of the piece, and the movement orientation 
         * */
        public Point getMovingPositions(Point c , Color color , Movement m){
            Orientation or = color==Color.black?Orientation.DOWN:Orientation.UP;
            Color oposite  = color==Color.black?Color.red:Color.black;
            Point soldierPoint = getXandYgivenOrientation(c, or, m);
            
            Soldier friend = new Soldier(soldierPoint.x,soldierPoint.y,color);
            Soldier foe    = new Soldier(soldierPoint.x,soldierPoint.y,oposite);
            if(!containsSoldier(friend, gamePieces) && !containsSoldier(foe, gamePieces))
                return new Point(soldierPoint.x,soldierPoint.y);
            else if(containsSoldier(friend, gamePieces))
                return new Point(0,0);
            else if (containsSoldier(foe, gamePieces))
                return new Point(0,0);                  
            return new Point(0,0);
        }
        
        /**
         * A Depth first search for possible jumping positions.
         * Results are passed to jumpPositions list
         * 
         * 
         * */
        public int dFSJumps(Point p, Color c, Orientation or, Movement m){
            Point soldierPoint;
            soldierPoint = getXandYgivenOrientation(p, or, m);
            if(isJumpable(soldierPoint, or, m, c))
                addToJumpPositions(p, getXandYgivenOrientation(soldierPoint, or,m) , soldierPoint);            
            return 0;
        }
        public int getJumps(Point p, Color c , boolean initial){
            if(initial){
             dFSJumps(p,c,Orientation.UP, Movement.LEFT);
             dFSJumps(p,c,Orientation.UP, Movement.FORWARD);
             dFSJumps(p,c,Orientation.UP, Movement.RIGHT);
             dFSJumps(p,c,Orientation.DOWN, Movement.RIGHT);
             dFSJumps(p,c,Orientation.DOWN, Movement.FORWARD);
             dFSJumps(p,c,Orientation.DOWN, Movement.LEFT);
             
             getJumps(p,c ,false);
            }else{
                // get the initial state of the board
                ArrayList<Soldier>    movedSoldiers = gamePieces;
                //clean previous taboo positions
                tabooPositions.clear();
                // get the current movement path
                jumpPositions   =  getFirstAvailableSearchTree();
                //fill the new taboo positions
                if(jumpPositions==null){
                    return -100000000;
                }
                fillTabooPieces();
                
                
                //get the movement of the soldier so far
                Point from = jumpPositions.get(0).from;
                Point  to  = jumpPositions.get(jumpPositions.size()-1).to;
                moveFrom(from,to);
                
                
                //Get the current index and size before the DFS operation changes ig
                int removeIndex = getFirstAvailableIndex();
                int currentSize = tempSearchNode.size();
                
                dFSJumps(p,c,Orientation.UP, Movement.LEFT);
                dFSJumps(p,c,Orientation.UP, Movement.FORWARD);
                dFSJumps(p,c,Orientation.UP, Movement.RIGHT);
                dFSJumps(p,c,Orientation.DOWN, Movement.RIGHT);
                dFSJumps(p,c,Orientation.DOWN, Movement.FORWARD);
                dFSJumps(p,c,Orientation.DOWN, Movement.LEFT);
                
                if(currentSize == tempSearchNode.size()){
                    Point foo = new Point(Integer.MAX_VALUE,Integer.MAX_VALUE);
                    SearchNode as = new SearchNode(foo, foo, foo);
                    tempSearchNode.get(removeIndex).add(as);
                     getJumps(p, c,false);
                }else{
                    tempSearchNode.remove(removeIndex);
                    getJumps(p, c,false);
                }
                
                
                gamePieces = movedSoldiers;
                jumpPositions.clear();
            }
            return 0;
        }
        public void addToJumpPositions(Point from ,Point to ,Point  jumps){
            System.out.println("Added to jump" +from.toString()  + " " + to.toString() + " " + jumps.toString());
            jumpPositions.add(new SearchNode(from,to,jumps));
            tempSearchNode.add(jumpPositions);
        }
        public boolean isJumpable(Point p , Orientation or, Movement m, Color c){
            Point sP = getXandYgivenOrientation(p, or, m);
            Color oposite = c==Color.black?Color.red:Color.black;
            if(isEmpty(new Soldier(sP.x, sP.y, Color.WHITE), gamePieces)
            && containsSoldier(new Soldier(p.x, p.y,oposite), gamePieces)
            && !isTabooPiece(p)) return true;            
            return false;
        }
        public void fillTabooPieces(){
            for(SearchNode sol:jumpPositions)
                tabooPositions.add(new Point(sol.jumps.x , sol.jumps.y));
        }
        public boolean isTabooPiece(Point p){
            for(Point sol:tabooPositions)
                if(sol.x==p.x && sol.y==p.y)
                    return true;
            return false;
        }
        public ArrayList<SearchNode> getFirstAvailableSearchTree(){
            for(int i=tempSearchNode.size()-1;i<0;i--){
                ArrayList<SearchNode> getz = tempSearchNode.get(i);
                if(getz.get(getz.size()-1).from.x<Integer.MAX_VALUE){
                    return tempSearchNode.get(i);
                }
            }
            return null;
        }
        public int getFirstAvailableIndex(){
            for(int i=tempSearchNode.size()-1;i<0;i--){
                ArrayList<SearchNode> getz = tempSearchNode.get(i);
                if(getz.get(getz.size()-1).from.x<Integer.MAX_VALUE){
                    return i;
                }
            }
            return -1;
        }
        
        /**
     *
     * @param p a point on the board, regarding the position of the piece
     * @param a describes the orientation of the move, with possible moves UP or DOWN
     * @param m Self explanatory parameter, defining if the move is a LEFT, RIGHT OR FORWARD
     * @return
     */
    public Point getXandYgivenOrientation(Point p , Orientation a, Movement m){
            if(a == Orientation.UP){
                switch (m){
                    case LEFT:    return (p.x%2==0)?new Point(p.x-1,p.y-1):new Point(p.x-1,p.y);
                    case RIGHT:   return (p.x%2==0)?new Point(p.x+1,p.y-1):new Point(p.x+1,p.y);
                    case FORWARD: return new Point(p.x, p.y-1);
                }
                        
            }else if(a ==Orientation.DOWN){
                switch (m){
                    case LEFT:    return (p.x%2==0)?new Point(p.x-1,p.y):new Point(p.x-1,p.y+1);
                    case RIGHT:    return (p.x%2==0)?new Point(p.x+1,p.y):new Point(p.x+1,p.y+1);
                    case FORWARD:  return new Point(p.x, p.y+1);
                }
            }else{
                
            }
            return new Point(0,0);
        }
    
        public void moveFrom(Point from , Point to){
        for(int i=0;i<gamePieces.size();i++){
            if(gamePieces.get(i).i== from.x && gamePieces.get(i).j== from.y ){
                gamePieces.get(i).i = to.x;
                gamePieces.get(i).j = to.y;
            }
        }
            
    }

    class SearchNode{
        private Point from;
        private Point to;
        private Point jumps;
        public SearchNode(Point from, Point to , Point jumps){
            this.from  = from;
            this.to    = to;
            this.jumps = jumps;
        }
    
    }   
}