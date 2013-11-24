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
 * @author Panos
 */
public class SearchTree{
    private ArrayList<Soldier> solList;
    Node root;
    int bestMoveVal = Integer.MIN_VALUE;
    private ArrayList<Node> bestMoves = new ArrayList<>();
    final static int[ ][ ] sideSquares = {{0,2},{1,1}, {2,1}, {3,0} ,{4,0}, {5,0} , {6,1}, {7,1} ,{8,2},
                                           {0,6}, {1,6}, {2,7}, {3,7} ,{4,8}, {5,7} , {6,7}, {7,6} , {8,6},
                                            {0,3},{0,4},{0,5},{8,3},{8,4},{8,5} };
    
    public SearchTree(ArrayList<Soldier> solList){
        this.solList = (ArrayList<Soldier>) solList.clone();
        this.root    = new Node();
    }
    public static int heuristicValue(Color c , ArrayList<Soldier> solListy){
        int heuristicVal = 0;
        for(Soldier sl:solListy){
            if(sl.C.equals(c))
                heuristicVal = sl.isKing?(heuristicVal+10):(heuristicVal+1);
            if(sl.C.equals(c) && isSideSquare(sl.i, sl.j))
                heuristicVal = heuristicVal+4;
        }
        return heuristicVal;
    }
      public static boolean isSideSquare(int x, int y){
        for(int i=0;i<sideSquares.length;i++)
            if(x==sideSquares[i][0] && y==sideSquares[i][1])
                return true;
        return false;            
    }
    //Correct way to modify soldier list without modifying hexgame gamepiecesr structure
    public void cloneandtwinklewith(){
        ArrayList<Soldier> sL = (ArrayList<Soldier>) solList.clone();
        sL.add(new Soldier(0, 0, Color.RED));
    }
    public ArrayList<Node> initializeAndSearchTree(ArrayList<JumpPosition> jPList , Color c) throws CloneNotSupportedException{
        bestMoveVal = Integer.MIN_VALUE;
        Node next;
        for(JumpPosition jP:jPList){
            next = new Node(jP,root);
            root.next.add(next);
        }
        Color currentSearchColor = c;
        long currentTime         = System.currentTimeMillis();
        //while(currentTime+5000 >System.currentTimeMillis()){
        for(int jk=0;jk<2;jk++){
            searchNodesNextStep(root, currentSearchColor);
            currentSearchColor = toggleColor(currentSearchColor);
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
                for(int j = 0;j<jPList.size();j++){
                    SearchNode sN = jPList.get(j);
                    for(Iterator<Soldier> soldier = fooSold.iterator(); soldier.hasNext();){
                        Soldier fooSol = soldier.next();
                        if ( fooSol.i == sN.from.x && fooSol.j ==sN.from.y){
                                fooSol.i =  sN.to.x;
                                fooSol.j =  sN.to.y;
                                if(PanelRules.isKingSquare(fooSol.i, fooSol.j, fooSol.C)){
                                    if(j+1==jPList.size()){
                                        fooSol.print();
                                        fooSol.isKing = true;
                                    }
                                    else if(j+1<jPList.size()){
                                        if((jPList.get(j+1).from.x!=sN.to.x)&&(jPList.get(j+1).from.y!=sN.to.y)){
                                            fooSol.print();
                                            fooSol.isKing = true;
                                        }
                                    }
                                }
                            }
                        if (fooSol.i == sN.jumps.x && fooSol.j ==sN.jumps.y)
                            soldier.remove();
                    }

                }
                Color oposite = c.equals(Color.RED)?Color.BLACK:Color.RED;
                PanelRules pr = new PanelRules(fooSold);
                ArrayList<JumpPosition> NextNodes = pr.getLegalMoves(oposite, fooSold);
                n.next = new ArrayList<>();
                for(JumpPosition nextNode: NextNodes){
                    JumpPosition jp = new JumpPosition();                    
                    nextNode.jumpPosition.addAll(0 , n.jP.jumpPosition);                    
                    
                    Node newNode =  new Node(nextNode, n);
                    newNode.value = heuristicValue(oposite, fooSold);
                    n.next.add(newNode);
                    if(newNode.value>bestMoveVal)
                        bestMoveVal = newNode.value;
                }
            }
    }
    public void getBestMoves(Node n) {
        Node nextNodez;
        if(n.next!=null)
            for(Node sd: n.next)
                getBestMoves(sd);
        else{
            if(bestMoves.size()>0){
                for(int i=0;i<bestMoves.size();i++){
                    nextNodez =  bestMoves.get(i); 
                    if(nextNodez.value <= n.value+Math.random()*2){
                        bestMoves.remove(i);
                        bestMoves.add(n);
                    }else if(nextNodez.value <= n.value+Math.random()*5){
                        bestMoves.add(n);
                    }
                }
            }else{
                bestMoves.add(n);
            }
        }
    }
  
}