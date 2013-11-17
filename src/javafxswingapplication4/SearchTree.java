/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;
import java.awt.Color;
import java.util.ArrayList;
import javafxswingapplication4.PanelRules.Orientation;
import javafxswingapplication4.PanelRules.Movement;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
/**
 *
 * @author Panos
 */
public class SearchTree{
    private ArrayList<Soldier> solList;
    
    
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
    public void initializeSearchTree(JumpPosition jP){
        Node searchNode;
        Node next = new Node();
        searchNode = new Node(jP , null);
        searchNode.next.add(next);
        searchNode.next.add(next);
        
        
    }
    
    public void searchNodes(Node n){
        while(n!=null)
            for(Node sd: n.next)
                searchNodes(n);
        n.print();
            
        
    }
    
    
}
