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
     public ArrayList<Soldier> gamePieces                    = new ArrayList<Soldier>();
     /*
      An array containing all the legal jump positions.
      */     
     public JumpPosition jumpPositions;
     /*
      Also used for the DFS of the legal jump positions
      */     
     public SearchNodeList tempSearchNode = new SearchNodeList(new ArrayList<JumpPosition>());
     /*
      * An array containing taboo points, positions on the map already jumped
      */     
     private ArrayList<Point>      tabooPositions = new ArrayList<Point>();
     
     private ArrayList<OrientationMove>      orientationMoveCombs = new ArrayList<OrientationMove>();
     
    
     

     enum Orientation {UP, DOWN};
     enum Movement {LEFT, RIGHT , FORWARD};     
     
     
     public PanelRules(ArrayList<Soldier> gamePieces){
         this.gamePieces = gamePieces;
         orientationMoveCombs.add(new OrientationMove(Orientation.UP, Movement.LEFT));
         orientationMoveCombs.add(new OrientationMove(Orientation.UP, Movement.RIGHT));
         orientationMoveCombs.add(new OrientationMove(Orientation.UP, Movement.FORWARD));
         orientationMoveCombs.add(new OrientationMove(Orientation.DOWN, Movement.RIGHT));
         orientationMoveCombs.add(new OrientationMove(Orientation.DOWN, Movement.FORWARD));
         orientationMoveCombs.add(new OrientationMove(Orientation.DOWN, Movement.LEFT));
     }
     public void updatePieces(ArrayList<Soldier> gamePieces){
         this.gamePieces = gamePieces;
     }
     public static boolean isValidSquare(int i, int j){
             if( (i==0||i==8) && (j>1 && j<7) 
               ||(i==1||i==7) && (j>0 && j<7)
               ||(i==2||i==6) && (j>0 && j<8)
               ||(i==3||i==5) && (j>=0 && j<8)
               || (i==4) && (j>=0 && j<9))
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
            for (int i=0;i<gamePiece.size();i++)
                if(test.i == gamePiece.get(i).i && test.j == gamePiece.get(i).j && test.C.equals(gamePiece.get(i).C))
                    return true;
            return false;
        }
        public boolean containsSoldier(Soldier test, ArrayList<Soldier> gamePiece , boolean foo){
            for (int i=0;i<gamePiece.size();i++){
                if(test.i == gamePiece.get(i).i && test.j == gamePiece.get(i).j && test.C.equals(gamePiece.get(i).C)){
                    System.out.println(test.C.toString());
                    System.out.println(gamePiece.get(i).C.toString());
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
        public boolean isEmpty(Point p, ArrayList<Soldier> gamePiece,ArrayList<SearchNode> jumplocations){
            Point from = jumplocations.get(0).from;
            for (int i=0;i<gamePiece.size();i++)
                    if(p.x == gamePiece.get(i).i && p.y == gamePiece.get(i).j && !(p.x==from.x &&p.y==from.y) )
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
            if( temp.x!=0 || temp.y!=0) mp.add(temp);
            temp =getMovingPositions(new Point(positionX, positionY), col, Movement.RIGHT);
            if( temp.x!=0 || temp.y!=0) mp.add(temp);
            temp =getMovingPositions(new Point(positionX, positionY), col , Movement.FORWARD);
            if( temp.x!=0 || temp.y!=0) mp.add(temp);
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
         * returns the moves of a king piece
         * */
        public ArrayList<Point> getKingMovingPositions(int positionX, int positionY, Color col){
            ArrayList<Point> mp = new ArrayList<Point>();
            ArrayList<Point> moves  = new ArrayList<Point>();
            for(OrientationMove orM:orientationMoveCombs){
                moves = getKingMovingPositions(new Point(positionX, positionY), col, orM.m , orM.or);
                if( moves.size()>0)    mp.addAll(moves);
            }
            return mp;
        }
        public ArrayList<Point> getKingMovingPositions(Point c , Color color , Movement m, Orientation or){
            ArrayList<Point> validMultiJumpSquares = new ArrayList<Point>();
            Color oposite  = color==Color.black?Color.red:Color.black;
            Point soldierPoint = getXandYgivenOrientation(c, or, m);
            Point currentPos = getXandYgivenOrientation(c, or, m);
            for(int i=0;i<8;i++){
                if(i!=0) currentPos = getXandYgivenOrientation(currentPos, or, m);
                if(!isValidSquare(currentPos.x, currentPos.y)) break;
                Soldier friend = new Soldier(currentPos.x,currentPos.y,color);
                Soldier foe    = new Soldier(currentPos.x,currentPos.y,oposite);
                if(!containsSoldier(friend, gamePieces) && !containsSoldier(foe, gamePieces))
                    validMultiJumpSquares.add(currentPos);
                else return   validMultiJumpSquares;
            }
            return validMultiJumpSquares;
        }
        
        public ArrayList<Point> getKingJumpingPositions(Point c , Color color , Movement m, Orientation or , boolean initial){
            ArrayList<Point> validMultiJumpSquares = new ArrayList<Point>();
            Color oposite  = color==Color.black?Color.red:Color.black;
            Point currentPos = getXandYgivenOrientation(c, or, m);
            for(int i=0;i<8;i++){
                if(i!=0) currentPos = getXandYgivenOrientation(currentPos, or, m);
                if(isValidSquare(currentPos.x, currentPos.y)){
                    Point nextPos  = getXandYgivenOrientation(currentPos, or, m);
                    Soldier foe    = new Soldier(currentPos.x,currentPos.y,oposite);
                    Soldier nextPoint = new Soldier(nextPos.x, nextPos.y, Color.WHITE);
                    if(containsSoldier(foe, gamePieces , true) && isValidSquare(nextPos.x, nextPos.y) && isEmpty(nextPoint, gamePieces)){
                        System.out.println("will go from " + c.toString()  + " to " +nextPos.toString()+ " jumping "+ currentPos.toString() );
                        ArrayList<SearchNode> foo = new ArrayList<SearchNode>();
                        foo.add(new SearchNode(c,nextPos,currentPos));
                        JumpPosition jp = new JumpPosition(foo);
                        jp.setOrientationAndMovement(or, m);
                        tempSearchNode.add(jp);
                        validMultiJumpSquares.add(nextPos);
                        break;
                    }
                }                
                else return  validMultiJumpSquares;
            }
            return validMultiJumpSquares;
        }
        public void addAllRelevantSquares(JumpPosition jumpPosit, Point foo, Point nextPoint, 
                                          Point soldierPoint, Orientation or, Movement m ,
                                          Color oposite ) throws CloneNotSupportedException{
            for(int k=0;k<6;k++){
                if(k==0){
                    JumpPosition newList;
                    newList = (JumpPosition) jumpPositions.clone();
                    newList.setOrientationAndMovement(or, m);
                    tempSearchNode.add(new SearchNode(foo, nextPoint, soldierPoint) , newList);
                    jumpPosit.setSearched(or, m);
                }
                else{
                    nextPoint            = getXandYgivenOrientation(nextPoint, or,m);
                    Soldier emptySquare  = new Soldier(nextPoint.x, nextPoint.y, oposite);
                    if(nextPoint.x != foo.x && nextPoint.y!= foo.y){
                        if(isValidSquare(nextPoint.x, nextPoint.y) && isEmpty(emptySquare, gamePieces)){
                            JumpPosition newList;
                            newList = (JumpPosition) jumpPositions.clone();
                            newList.setOrientationAndMovement(or, m);
                            tempSearchNode.add(new SearchNode(foo, nextPoint, soldierPoint) , newList);
                            jumpPosit.setSearched(or, m);
                        }else break;
                    }
                    else break;
                }
            }
        
        }
         private void getKingJumpPos(Point kingPoint, Orientation or, Movement m, JumpPosition jumpPosit,Color c, boolean b) throws CloneNotSupportedException {
            //Take the last position of the movement as current point
             Point foo = jumpPositions.jumpPosition.get(jumpPositions.jumpPosition.size()-1).to;
             Color oposite  = c==Color.black?Color.red:Color.black;
             Point currentPoint = foo;
             Point soldierPoint , nextPoint;
             
             for(int i=0;i<8;i++){
                if(i!=0) currentPoint = getXandYgivenOrientation(currentPoint, or,m);
                if( !isValidSquare(currentPoint.x, currentPoint.y)) break;
                for(OrientationMove orM:orientationMoveCombs){
                    soldierPoint         = getXandYgivenOrientation(currentPoint, orM.or,orM.m);
                    nextPoint            = getXandYgivenOrientation(soldierPoint, orM.or,orM.m);
                    Soldier foe          = new Soldier(soldierPoint.x, soldierPoint.y, oposite);
                    Soldier emptySquare  = new Soldier(nextPoint.x, nextPoint.y, oposite);
                    
                    if(isJumpable(soldierPoint, orM.or, orM.m, c , jumpPosit))
                        addAllRelevantSquares(jumpPosit,  foo, nextPoint, soldierPoint, orM.or, orM.m ,oposite );
                    
                }
            }
                
                 
                
                //If it is, add it to the movement path
//                if(isJumpable(soldierPoint, or, m, c , jumpPositions)){                       
//                    addOnlyToTempJumpPositions(foo, getXandYgivenOrientation(soldierPoint, or,m) , soldierPoint ,jumpPosit , or, m);
//                }
        
        }
        public ArrayList<JumpPosition> kingJumpPositions(Point kingPoint, Color c , boolean initial) throws CloneNotSupportedException{
             if(initial==true){
                 tempSearchNode.clear();
                 jumpPositions              = new JumpPosition(new ArrayList<SearchNode>());
                 for(OrientationMove orM :orientationMoveCombs)
                    getKingJumpingPositions(kingPoint , c , orM.m, orM.or ,true);
                 //Continue with dfs checking for multi jumps
                 kingJumpPositions(kingPoint,c ,false);
                }else{
                    tabooPositions.clear();
                    jumpPositions   =  getFirstAvailableSearchTree();
                    if(jumpPositions==null)
                        return null;
                    fillTabooPieces();
                    //Get the current index and size before the DFS operation changes ig
                    int removeIndex = getFirstAvailableIndex();
                    int currentSize = tempSearchNode.size();
                     // Make a new DFS with the path as input
                    getKingJumpPos(kingPoint,jumpPositions.or, jumpPositions.m, jumpPositions,c,false);
                    
                    //Check if the size of the search tree has gotten bigger
                    if(currentSize == tempSearchNode.size()){
                        Point foo = new Point(Integer.MAX_VALUE,Integer.MAX_VALUE);
                        SearchNode as = new SearchNode(foo, foo, foo);
                        tempSearchNode.get(removeIndex).append(as);
                        return kingJumpPositions(kingPoint, c,false);
                    }else
                        return kingJumpPositions(kingPoint, c,false);
                
             }
             return tempSearchNode.getMaxPositions();            
        }
        
        public void addJumpAndMultiSquareJump(Point from , Point to, Point jumps, Orientation or, Movement m , Color c ){
            addToJumpPositions(from, getXandYgivenOrientation(jumps, or,m) , jumps);
            Point soldierPoint = to;
            for(int i=0;i<6;i++){
                soldierPoint = getXandYgivenOrientation(soldierPoint, or, m);
                if(!isValidSquare(soldierPoint.x, soldierPoint.y)) break;
            }
        }
        /**
         * A Depth first search for possible jumping positions.
         * Results are passed to jumpPositions list
         * 
         * */
        public int dFSJumps(Point p, Color c, Orientation or, Movement m){
            Point soldierPoint;
            soldierPoint = getXandYgivenOrientation(p, or, m);
            if(isJumpable(soldierPoint, or, m, c))
                addToJumpPositions(p, getXandYgivenOrientation(soldierPoint, or,m) , soldierPoint);            
            return 0;
        }
        public int dFSJumps(Color c, Orientation or, Movement m, JumpPosition jumpPosit, boolean initial) throws CloneNotSupportedException{
            //Take the last position of the movement as current point
            Point foo = jumpPosit.jumpPosition.get(jumpPosit.jumpPosition.size()-1).to;
            //Search if there is a soldier and is jumpble from this position
            Point soldierPoint;
            soldierPoint = getXandYgivenOrientation(foo, or, m);
            //If it is, add it to the movement path
            if(isJumpable(soldierPoint, or, m, c , jumpPosit)){                       
                addOnlyToTempJumpPositions(foo, getXandYgivenOrientation(soldierPoint, or,m) , soldierPoint ,jumpPosit , or, m);
            }
            return 0;
        }
       
        /**
        * 
        * Recursive MultiJump estimator
        * 
        * */        
        public ArrayList<JumpPosition> getJumps(Point p, Color c , boolean initial) throws CloneNotSupportedException {
                if(initial){
                    //For every search, clear the tempSearch and jump positions
                 tempSearchNode.clear();
                 jumpPositions              = new JumpPosition(new ArrayList<SearchNode>());

                 //Make the DFS search clockwise starting from up and left
                 for(OrientationMove orM:orientationMoveCombs)
                    dFSJumps(p,c,orM.or, orM.m);
                 //Continue with dfs checking for multi jumps
                 getJumps(p,c ,false);
                }else{
                    //clean previous taboo positions
                    tabooPositions.clear();
                    // get the current movement path
                    jumpPositions   =  getFirstAvailableSearchTree();
                    //Check if jump positions is null
                    if(jumpPositions==null) return null;
                    
                    //fill the new taboo positions
                    fillTabooPieces();
                    //Get the current index and size before the DFS operation changes ig
                    int removeIndex = getFirstAvailableIndex();
                    int currentSize = tempSearchNode.size();
                     // Make a new DFS with the path as input
                    //System.out.println("New iteration in dfs");
                    for(OrientationMove orM:orientationMoveCombs)
                        dFSJumps(c,orM.or, orM.m, jumpPositions,false);
                    //Check if the size of the search tree has gotten bigger
                    if(currentSize == tempSearchNode.size()){
                        Point foo = new Point(Integer.MAX_VALUE,Integer.MAX_VALUE);
                        SearchNode as = new SearchNode(foo, foo, foo);
                        tempSearchNode.get(removeIndex).append(as);
                        return getJumps(p, c,false);
                    }else
                        return getJumps(p, c,false);
                }
                return tempSearchNode.getMaxPositions();
        }        
        
        public void addToJumpKingPositions(Point from ,Point to ,Point  jumps , Orientation or, Movement m){
            ArrayList<SearchNode> foo = new ArrayList<SearchNode>();
            foo.add(new SearchNode(from,to,jumps));
            JumpPosition jp = new JumpPosition(foo);
            tempSearchNode.add(jp);
            Point nextPoint = to;
            for(int i =0;i<7;i++){
                nextPoint = getXandYgivenOrientation(nextPoint, or, m);
                if(isValidSquare(nextPoint.x, nextPoint.y) && isEmpty(new Soldier(nextPoint.x, nextPoint.y, Color.red), gamePieces)){
                    foo = new ArrayList<SearchNode>();
                    foo.add(new SearchNode(from,nextPoint,jumps));
                    jp = new JumpPosition(foo);
                    tempSearchNode.add(jp);
                }
                else break;
            }   
        }
        
        public void addToJumpPositions(Point from ,Point to ,Point  jumps){
            ArrayList<SearchNode> foo = new ArrayList<SearchNode>();
            foo.add(new SearchNode(from,to,jumps));
            JumpPosition jp = new JumpPosition(foo);
            tempSearchNode.add(jp);
        }
        public void addOnlyToTempJumpPositions(Point from ,Point to ,Point  jumps ,JumpPosition jumpPosit ,Orientation or, Movement m) throws CloneNotSupportedException{
            //modify it for no future or and m movement
            jumpPosit.setSearched(or, m);
            //Clone to pass by value and not let the other values be modified
            JumpPosition snl = (JumpPosition) jumpPosit.clone();
            tempSearchNode.add(new SearchNode(from,to,jumps) , snl);
        }
        
        public boolean isJumpable(Point p , Orientation or, Movement m, Color c){
            Point sP = getXandYgivenOrientation(p, or, m);
            Color oposite = c==Color.black?Color.red:Color.black;
            if(isEmpty(new Soldier(sP.x, sP.y, Color.WHITE), gamePieces)
            && containsSoldier(new Soldier(p.x, p.y,oposite), gamePieces)
            && isValidSquare(sP.x, sP.y)) return true;          
            return false;
        }
        /**
         * @param p defines the soldier point on the board
         * @param or defines the orientation
         * @param m defines the movement
         * @param c defines the color
         * @param jumplocations defines the path that is already searched
         * 
         * */
        public boolean isJumpable(Point p , Orientation or, Movement m, Color c , JumpPosition jumplocations){
            //If the point continuing the movement is empty and the current point is the soldier we are looking for,
            //return true.
            Point sP = getXandYgivenOrientation(p, or, m);
            Color oposite = c==Color.black?Color.red:Color.black;            
            
            if(!jumplocations.isAlreadySearched(or, m)
            && isValidSquare(sP.x, sP.y)
            && isEmpty(sP, gamePieces,jumplocations.jumpPosition)
            && containsSoldier(new Soldier(p.x, p.y,oposite), gamePieces)
            && !isTabooPiece(p , jumplocations.jumpPosition)) return true;
            return false;
        }
        public void fillTabooPieces(){
            for(SearchNode sol:jumpPositions.jumpPosition)
                tabooPositions.add(new Point(sol.jumps.x , sol.jumps.y));
        }
        public boolean isTabooPiece(Point p , ArrayList<SearchNode> jumplocations){
            for(SearchNode sol:jumplocations)
                if(sol.jumps.x==p.x && sol.jumps.y==p.y)
                    return true;
            return false;
        }
        public JumpPosition getFirstAvailableSearchTree(){
            for(int i=tempSearchNode.size()-1;i>=0;i--){
                JumpPosition getz = tempSearchNode.get(i);
                if(getz.jumpPosition.get(getz.jumpPosition.size()-1).from.x<Integer.MAX_VALUE)
                    return tempSearchNode.get(i);
            }
            return null;
        }
        public int getFirstAvailableIndex(){
            for(int i=tempSearchNode.size()-1;i>=0;i--){                
                JumpPosition getz = tempSearchNode.get(i);
                if(getz.jumpPosition.get(getz.jumpPosition.size()-1).from.x<Integer.MAX_VALUE)
                    return i;
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
            }
            return new Point(0,0);
        }
}