/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;

import java.awt.Point;
import java.util.ArrayList;
import javafxswingapplication4.PanelRules.Movement;
import static javafxswingapplication4.PanelRules.Movement.FORWARD;
import static javafxswingapplication4.PanelRules.Movement.LEFT;
import javafxswingapplication4.PanelRules.Orientation;
/**
 *
 * @author Panos
 */

public class JumpPosition implements Cloneable {
    public ArrayList<SearchNode> jumpPosition  = new ArrayList<SearchNode>();
    public boolean searchedUPandLEFT       = false;
    public boolean searchedUPandRIGHT      = false;
    public boolean searchedUPandFORWARD    = false;
    public boolean searchedDOWNandLEFT     = false;
    public boolean searchedDOWNandRIGHT    = false;
    public boolean searchedDOWNandFORWARD  = false;
    public Orientation or;
    public Movement m;
    
    public JumpPosition(ArrayList<SearchNode> jp){
        this.jumpPosition = jp;
    }
    public JumpPosition(){}
    
    public void append(SearchNode s){
        jumpPosition.add(s);
    }
    public void setOrientationAndMovement(Orientation or , Movement m){
        this.or = or;
        this.m  = m;
    }
    public ArrayList<SearchNode> getPosition(){
        return this.jumpPosition;
    }
    public boolean isAlreadySearched( Orientation or, Movement m){
        switch(or){
            case DOWN:
                switch(m){
                    case FORWARD: return searchedDOWNandFORWARD;
                    case LEFT: return searchedDOWNandLEFT;
                    case RIGHT :return searchedDOWNandRIGHT;
                }
                break;
            case UP:
                switch(m){
                    case FORWARD: return searchedUPandFORWARD;
                    case LEFT: return searchedUPandLEFT;
                    case RIGHT :return searchedUPandRIGHT;
                }
                break;
        }
        return false;
    }
    public void setSearched(Orientation or, Movement m){
        switch(or){
            case DOWN:
                switch(m){
                    case FORWARD: searchedDOWNandFORWARD=true; break;
                    case LEFT:    searchedDOWNandLEFT=true; break;
                    case RIGHT:    searchedDOWNandRIGHT=true;     break;           
                }                
                break;                
            case UP:
                switch(m){
                    case FORWARD: searchedUPandFORWARD=true; break;
                    case LEFT:    searchedUPandLEFT=true; break;
                    case RIGHT:    searchedUPandRIGHT=true; break;
                }
                break;
        }
    }
    public void resetSearched(){
        searchedDOWNandFORWARD=false;
        searchedDOWNandLEFT=false;
        searchedDOWNandRIGHT=false;                
        searchedUPandFORWARD=false;
        searchedUPandLEFT=false;
        searchedUPandRIGHT=false;
        
    }
    public void print(){
        //Mostly used for debugging reasons
        System.out.println("-------------------");
        System.out.println("Printing a single node");
        for(SearchNode s:jumpPosition){
            System.out.println(s.from.toString() + " " + s.to.toString() + " " + s.jumps.toString());
        }
        System.out.println(searchedDOWNandFORWARD);
        System.out.println(searchedDOWNandLEFT);
        System.out.println(searchedDOWNandRIGHT);
        System.out.println(searchedUPandFORWARD);
        System.out.println(searchedUPandLEFT);
        System.out.println(searchedUPandRIGHT);
        System.out.println("End of printing a single node");
        System.out.println("-------------------");
        
    }
     public void print(boolean bool){
        System.out.println("-------------------");
        for(SearchNode s:jumpPosition){
            System.out.println(s.from.toString() + " " + s.to.toString() + " " + s.jumps.toString());
        }
        System.out.println("-------------------");
        
    }
    public boolean exists(ArrayList<SearchNode> we){
        if(jumpPosition.size()==we.size()){
            for(int i=0;i<we.size();i++)
                if(jumpPosition.get(i).from.x!= we.get(i).from.x 
                 ||jumpPosition.get(i).from.y!= we.get(i).from.y
                 ||jumpPosition.get(i).to.x!= we.get(i).to.x
                 ||jumpPosition.get(i).to.y!= we.get(i).to.y
                 ||jumpPosition.get(i).jumps.x!= we.get(i).jumps.x
                 ||jumpPosition.get(i).jumps.y!= we.get(i).jumps.y)
                    return false;        
        }else{
            return false;
        }
        return true;
    }
    public void set(int number){
        Point x = new Point(number,number);
        SearchNode s = new SearchNode(x, x, x);
        append(s);
    }
    
    protected Object clone() throws CloneNotSupportedException {
//        JumpPosition s = new JumpPosition(jumpPosition);
//        return s;
        return super.clone();
    }
    
}
