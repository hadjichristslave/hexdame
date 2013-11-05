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
     ArrayList<Soldier> gamePieces = new ArrayList<Soldier>();
     enum Orientation {UP, DOWN};
     enum Movement {LEFT, RIGHT , FORWARD};
     
     
     public PanelRules(ArrayList<Soldier> gamePieces){
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
        public static boolean containsSoldier(Soldier test, ArrayList<Soldier> gamePieces){
            Iterator<Soldier> soldIter = gamePieces.iterator();
            while(soldIter.hasNext()){
              Soldier  obj = soldIter.next();
              if(test.i == obj.i && test.j == obj.j && test.C == obj.C)
                    return true;
            }
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
                System.out.println("left move" +getMovingPositions(new Point(positionX, positionY), Color.black, Movement.LEFT));
                System.out.println("right move" +getMovingPositions(new Point(positionX, positionY), Color.black, Movement.RIGHT));
                System.out.println("forward move" +getMovingPositions(new Point(positionX, positionY), Color.black, Movement.FORWARD));
            }else if(c.C == Color.red){
                System.out.println("left move " +getMovingPositions(new Point(positionX, positionY), Color.red, Movement.LEFT));
                System.out.println("right move " +getMovingPositions(new Point(positionX, positionY), Color.red, Movement.RIGHT));
                System.out.println("forward move" +getMovingPositions(new Point(positionX, positionY), Color.red, Movement.FORWARD));
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
            if( temp.x!=0 && temp.y!=0)
                mp.add(temp);    
            temp =getMovingPositions(new Point(positionX, positionY), col, Movement.RIGHT);
            if( temp.x!=0 && temp.y!=0)
                mp.add(temp);
            temp =getMovingPositions(new Point(positionX, positionY), col , Movement.FORWARD);
           
            return mp;
            
        }
        public Point getMovingPositions(Point c , Color color , Movement m){
            Orientation or = color==Color.black?Orientation.DOWN:Orientation.UP;
            Color oposite  = color==Color.black?Color.black:Color.red;
            Point soldierPoint = getXandYgivenOrientation(c, or, m);
            
            Soldier friend = new Soldier(soldierPoint.x,soldierPoint.y,color);
            Soldier foe    = new Soldier(soldierPoint.x,soldierPoint.y,oposite);
            if(!containsSoldier(friend, gamePieces) && !containsSoldier(foe, gamePieces))
                return new Point(soldierPoint.x,soldierPoint.y);
            else if(containsSoldier(friend, gamePieces))
                  return new Point(0,0);
            else if (containsSoldier(foe, gamePieces))
                  return getMovingPositions(new Point(soldierPoint.x,soldierPoint.y), color , m);
            return new Point(0,0);
        }
        public Point getCapturePositions(Point c , Color color , Movement m){
            Orientation or = color==Color.black?Orientation.DOWN:Orientation.UP;
            Color oposite  = color==Color.black?Color.black:Color.red;
            Point soldierPoint = getXandYgivenOrientation(c, or, m);
            
            Soldier friend = new Soldier(soldierPoint.x,soldierPoint.y,color);
            Soldier foe    = new Soldier(soldierPoint.x,soldierPoint.y,oposite);
            if (containsSoldier(foe, gamePieces))
                  return getMovingPositions(new Point(soldierPoint.x,soldierPoint.y), color , m);
            return null;
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
        
}