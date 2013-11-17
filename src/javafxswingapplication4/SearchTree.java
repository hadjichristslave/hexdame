/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;
import java.awt.Color;
import java.util.ArrayList;
/**
 *
 * @author Panos
 */
public class SearchTree{
    private ArrayList<Soldier> solList;
    Node searchNode;
    Node root;
    
    
    public SearchTree(ArrayList<Soldier> solList){
        this.solList = solList;
    }
    
    public static int heuristicValue(Color c , ArrayList<Soldier> solListy){
        int heuristicVal = 0;
        for(Soldier sl:solListy)
            if(sl.C.equals(c))
                heuristicVal = sl.isKing?(heuristicVal+3):(heuristicVal+1);
        return heuristicVal;
    }
    //Correct way to modify soldier list without modifying hexgame gamepiecesr structure
    public void cloneandtwinklewith(){
        ArrayList<Soldier> sL = (ArrayList<Soldier>) solList.clone();        
        sL.add(new Soldier(0, 0, Color.RED));      
    }
    public void initializeSearchTree(ArrayList<JumpPosition> jPList){
        Node root = new Node();
        Node next;
        for(JumpPosition jP:jPList){
            next = new Node(jP);
            root.next.add(next);
        }
        System.out.println("root node stuff");
        root.next.get(0).jP.print();
        System.out.println("end of that");
    }
    
    public void printNodes(Node n){
        while(n!=null)
            for(Node sd: n.next)
                printNodes(n);
        System.out.println("Printing node");
        if(n!=null)
            if(n.jP!=null)
                n.jP.print();
        System.out.println("End of node");
    }
    
    
}
