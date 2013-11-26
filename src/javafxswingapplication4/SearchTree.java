/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * @author Panos
 */
public class SearchTree{

    private static ArrayList<JumpPosition> kingJumpPositions(Soldier sol, Color c, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private ArrayList<Soldier> solList;
    Node root;
    final static int[ ][ ] sideSquares = {{0,2},{1,1}, {2,1}, {3,0} ,{4,0}, {5,0} , {6,1}, {7,1} ,{8,2},
                                           {0,6}, {1,6}, {2,7}, {3,7} ,{4,8}, {5,7} , {6,7}, {7,6} , {8,6},
                                            {0,3},{0,4},{0,5},{8,3},{8,4},{8,5} };
    public int bestMoveGrading;
    public ArrayList<JumpPosition> bestMovesCalculated;
    public SearchTree(ArrayList<Soldier> solList){
        this.solList = (ArrayList<Soldier>) solList.clone();
        this.root    = new Node();
        this.root.previous = null;
    }
    public static int heuristicValue(Color c , ArrayList<Soldier> solListy){
        int heuristicVal = 0;
        for(Soldier sl:solListy){
            if(sl.C.equals(c))
                heuristicVal = sl.isKing?(heuristicVal+10):(heuristicVal+1);
            if(sl.C.equals(c) && isSideSquare(sl.i, sl.j))
                heuristicVal = heuristicVal+4;
            PanelRules pr = new PanelRules(solListy);
            for(Soldier sol:solListy){
                ArrayList<JumpPosition> tempJp;
                if(sol.isKing && sl.C.equals(c)){
                    try {
                      tempJp = pr.kingJumpPositions(new Point(sol.i, sol.j), c, true);
                      if(tempJp.size()>0){
                          heuristicVal = heuristicVal+5;
                      }
                  } catch (CloneNotSupportedException ex) {
                      Logger.getLogger(SearchTree.class.getName()).log(Level.SEVERE, null, ex);
                  }
                }else if(sl.C.equals(c)){
                    try {
                      tempJp = pr.getJumps(new Point(sol.i, sol.j),sol.C,true);
                      if(tempJp.size()>0){
                          heuristicVal = heuristicVal+3;
                      }
                  } catch (CloneNotSupportedException ex) {
                      Logger.getLogger(SearchTree.class.getName()).log(Level.SEVERE, null, ex);
                  }
                }
            } 
            
            
            
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
    public ArrayList<JumpPosition> initializeAndSearchTree(ArrayList<JumpPosition> jPList , Color c) throws CloneNotSupportedException{
        Node nextNode;
        for(JumpPosition jP:jPList){
            nextNode = new Node(jP,root);
            ArrayList<Soldier> fooSold = setupSoldiersGivenJumpPosition(solList, jP.jumpPosition);
            nextNode.value = heuristicValue(Color.black, fooSold);
            boolean isLastElement = true;
            for(int jk=0;jk<root.next.size();jk++){
                if(nextNode.value>root.next.get(jk).value){
                    for(int il =root.next.size(); il>jk;il--){
                        if(il==root.next.size())
                            root.next.add(root.next.get(il-1));
                        else
                            root.next.set(il, root.next.get(il-1));
                    }
                    root.next.set(jk, nextNode);
                    isLastElement = false;
                    break;
                }
            }
            if(isLastElement)
                root.next.add(nextNode);
        }
        NegaAlphaBeta(root,2, Integer.MIN_VALUE, Integer.MAX_VALUE);
        Color currentSearchColor = c;
        //long currentTime         = System.currentTimeMillis();
        //while(currentTime+5000 >System.currentTimeMillis()){
        
        for(int jk=0;jk<5;jk++){
            bestMoveGrading = Integer.MIN_VALUE;
            searchNodesNextStep(root, currentSearchColor);
            NegaAlphaBeta(root,jk+2, Integer.MIN_VALUE, Integer.MAX_VALUE);
            currentSearchColor = toggleColor(currentSearchColor);
        }
        bestMovesCalculated = new ArrayList<>();
        populateMoves(root);
        return bestMovesCalculated;
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
    public void populateMoves(Node n){
            if(n.next!=null)
                for(Node sd: n.next) populateMoves(sd);
            else
                if(n.value ==bestMoveGrading) bestMovesCalculated.add(n.jP);                 
    }
    public void printNodes(Node n){
            if(n.next!=null)
                for(Node sd: n.next)
                    printNodes(sd);
            else{
                if(n.value ==bestMoveGrading){
                    System.out.println("best node printing ");
                    System.out.println(n.value);
                    n.jP.print(true);
                    bestMovesCalculated.add(n.jP);
                }                    
            }        
    }
    
    public ArrayList<Soldier> setupSoldiersGivenJumpPosition(ArrayList<Soldier> soList , ArrayList<SearchNode> jP) throws CloneNotSupportedException{
        ArrayList<Soldier> fooSold  = new ArrayList<>();
        for(Soldier p : solList) 
            fooSold.add((Soldier) p.clone());
        ArrayList<SearchNode> jPList = (ArrayList<SearchNode>) jP.clone();
        
        for(int j = 0;j<jPList.size();j++){
                    SearchNode sN = jPList.get(j);
                    for(Iterator<Soldier> soldier = fooSold.iterator(); soldier.hasNext();){
                        Soldier fooSol = soldier.next();
                        if ( fooSol.i == sN.from.x && fooSol.j ==sN.from.y){
                                fooSol.i =  sN.to.x;
                                fooSol.j =  sN.to.y;
                                if(PanelRules.isKingSquare(fooSol.i, fooSol.j, fooSol.C)){
                                    //System.out.println("kingsquare detected ");
                                    if(j+1==jPList.size()){
                                        //System.out.println("Turned to king");
                                        ///fooSol.print();
                                        fooSol.isKing = true;
                                    }
                                    else if(j+1<jPList.size()){
                                        if((jPList.get(j+1).from.x!=sN.to.x)&&(jPList.get(j+1).from.y!=sN.to.y)){
                                            //System.out.println("Turned to king");
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
        return fooSold;
    }
    public void searchNodesNextStep(Node n, Color c) throws CloneNotSupportedException{
            if(n.next!=null)
                for(Node sd: n.next)
                    searchNodesNextStep(sd , c); 
            else{
                ArrayList<Soldier> fooSold  = new ArrayList<>();
                
                for(Soldier p : solList) 
                    fooSold.add((Soldier) p.clone());
                fooSold = setupSoldiersGivenJumpPosition(solList, n.jP.jumpPosition);
                
                Color oposite = c.equals(Color.RED)?Color.BLACK:Color.RED;
                PanelRules pr = new PanelRules(fooSold);
                ArrayList<JumpPosition> NextNodes = pr.getLegalMoves(oposite, fooSold);
                int maxNumberOfMoves = 0;
                for(int i=0;i<NextNodes.size();i++)
                    if(maxNumberOfMoves < NextNodes.get(i).jumpPosition.size())
                        maxNumberOfMoves = NextNodes.get(i).jumpPosition.size();
                
                for(int i=0;i<NextNodes.size();i++){
                    if(NextNodes.get(i).jumpPosition.size()< maxNumberOfMoves){
                        NextNodes.remove(i);
                        i--;
                    }
                }
                
                n.next = new ArrayList<>();
                
                for(JumpPosition nextNode: NextNodes){
                    JumpPosition jp = new JumpPosition();                    
                    nextNode.jumpPosition.addAll(0 , n.jP.jumpPosition);                    
                    
                    Node newNode =  new Node(nextNode, n);
                    newNode.value = heuristicValue(Color.BLACK, fooSold);
                    newNode.previous = n;
                    if(n.next.size()>0){
                        for(Node tempNode:n.next){
                            if(newNode.value >= tempNode.value){
                                n.next.add(newNode);
                                break;
                            }
                        }
                    }else 
                        n.next.add(newNode);
                }
            }
    
    }

    public int NegaAlphaBeta(Node n, int depth, int alpha, int beta) throws CloneNotSupportedException{
        int score;
        if(n.next!=null && depth>0){
            score = Integer.MIN_VALUE;
            for(int il=0;il<n.next.size();il++){
                Node sd = n.next.get(il);
                n.value = -NegaAlphaBeta(sd , depth-1, -beta, -alpha);
                //n.jP.print(false);
                if(n.value>score) {
                    //System.out.println("score modified to " + n.value);
                    score = n.value;
                }
                if(score>alpha){
                    //System.out.println("alpha modified to " + score);
                    alpha = score;
                }
                if(score>=beta) {
                    //System.out.println("pruned because score and beta are  "+ score + " " + beta);
                    int prunecount = 0;
                    while (n.next.size()>il+1){
                        n.next.remove(il+1);   
                        prunecount++;
                    }
                    if (sd.value>bestMoveGrading)
                       bestMoveGrading = sd.value;
                            
                    //System.out.println(prunecount);
                    n.value = -n.value;
                    break;
                }
                n.value = -n.value;
            }
        }
        else{
            n.value = heuristicValue(Color.black, setupSoldiersGivenJumpPosition(solList, n.jP.jumpPosition));
            return n.value;
        }
    
        return score;
    }
    /*
     * TODOS, CREATE PRINCIPLE VARIATION GETTER
     *
     */
    
//    public void getPrincipleVariation(){
//    }
    public int getDepth(Node n , int counter){
        Node newNode = n;
        while(newNode.previous!=null)   { 
            counter++;
            newNode = newNode.previous;
        }
        return counter;
    }    
}