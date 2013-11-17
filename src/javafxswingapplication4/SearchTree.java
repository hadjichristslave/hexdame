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
        this.root    = new Node();
        this.searchNode = new Node();
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
        Node next;
        for(JumpPosition jP:jPList){
            next = new Node(jP,root);
            root.next.add(next);
        }
    }
    public void updateAllLeafs(Node n){
        if(n.next!=null)
            for(Node sd: n.next)
                printNodes(sd);
        else{
            JumpPosition jP = new JumpPosition();
            Node newNode = new Node(jP, n);
            n.next.add(newNode);
        }
    }
    
    public void printNodes(Node n){
            if(n.next!=null)
                for(Node sd: n.next)
                    printNodes(sd);
            else{
                System.out.println("single node printing ");
                n.jP.print(true);
                n.printparent();
            }        
    }
    
    
}
