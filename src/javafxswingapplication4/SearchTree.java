/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
/**
 *
 * @author Panos
 */
public class SearchTree{
    private ArrayList<Soldier> solList;
    Node root;
    private ArrayList<Node> bestMoves = new ArrayList<>();
    
    
    public SearchTree(ArrayList<Soldier> solList){
        this.solList = (ArrayList<Soldier>) solList.clone();
        this.root    = new Node();
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
    public ArrayList<Node> initializeAndSearchTree(ArrayList<JumpPosition> jPList , Color c) throws CloneNotSupportedException{
        Node next;
        for(JumpPosition jP:jPList){
            next = new Node(jP,root);
            root.next.add(next);
        }
        Color currentSearchColor = c;
        long currentTime         = System.currentTimeMillis();
        int plycounter = 0;
        while(currentTime+5000 >System.currentTimeMillis()){
            searchNodesNextStep(root, currentSearchColor);
            currentSearchColor = toggleColor(currentSearchColor);
            plycounter++;
            System.out.println("ply" + plycounter);
        }
        bestMoves.clear();
        getBestMoves(root);
        return bestMoves;
    }
    
    public Color toggleColor(Color c){
        return c.equals(Color.black)?Color.red:Color.black;
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
                System.out.println(n.value);
            }        
    }
    public void searchNodesNextStep(Node n, Color c) throws CloneNotSupportedException{
            if(n.next!=null)
                for(Node sd: n.next)
                    searchNodesNextStep(sd , c); 
            else{
                ArrayList<Soldier> fooSold  = new ArrayList<>();
                for(Soldier p : solList) {
                    fooSold.add((Soldier) p.clone());
                }                
                ArrayList<SearchNode> jPList = (ArrayList<SearchNode>) n.jP.jumpPosition.clone();                
                for(Iterator< Soldier > soldier = fooSold.iterator(); soldier.hasNext();){
                    Soldier fooSol = soldier.next();
                    for(SearchNode sN:jPList)
                        if (fooSol.i == sN.jumps.x && fooSol.j ==sN.jumps.y)
                            soldier.remove();                    
                    if (fooSol.i == jPList.get(0).from.x && fooSol.j ==jPList.get(0).from.y){
                            int fromIndex = jPList.size()<=2?0:jPList.size()-2;
                            fooSol.i =  jPList.get(fromIndex).to.x;
                            fooSol.j =  jPList.get(fromIndex).to.y;
                        }                    
                }
                Color oposite = c.equals(Color.RED)?Color.BLACK:Color.RED;
                PanelRules pr = new PanelRules(fooSold);
                ArrayList<JumpPosition> NextNodes = pr.getLegalMoves(oposite, fooSold);
                n.next = new ArrayList<Node>();
                for(JumpPosition nextNode: NextNodes){ 
                    nextNode.jumpPosition.addAll(0 , n.jP.jumpPosition);
                    Node newNode =  new Node(nextNode, n);
                    newNode.value = heuristicValue(oposite, fooSold);                            
                    n.next.add(newNode);
                }
            }
    }

    public void getBestMoves(Node n) {
        if(n.next!=null)
            for(Node sd: n.next)
                getBestMoves(sd);
        else{
            for(Iterator<Node> iterNode = bestMoves.iterator(); iterNode.hasNext();){
                Node nextNodez = iterNode.next();
                if(nextNodez.value < n.value* Math.random()*2){
                    iterNode.remove();
                    bestMoves.add(nextNodez);
                }
            }
        }
        
    }
    
    
}
