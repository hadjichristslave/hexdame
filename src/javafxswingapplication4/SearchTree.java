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
    final static int[ ][ ] sideSquares = {{0,2},{8,2},{0,6},{8,6},{0,3},{0,4},{0,5},{8,3},{8,4},{8,5} };
    final static int[ ][ ] centerSquares = {{1,3} , {2,4} , {3,3} , {4,4},{5,3} , {6,4} , {7,3},
                                            {1,4} , {2,5} , {3,4} , {4,5},{5,4} , {6,5} , {7,4}};
    final static int[ ][ ] redKingSquares = {{0,2},{1,1}, {2,1}, {3,0} ,{4,0}, {5,0} , {6,1}, {7,1} ,{8,2}};
    final static int[ ][ ] blackKingSquares = {{0,6}, {1,6}, {2,7}, {3,7} ,{4,8}, {5,7} , {6,7}, {7,6} , {8,6}};

    public int bestMoveGrading;
    public JumpPosition principleVariation;
    public ArrayList<JumpPosition> bestMovesCalculated;
    public SearchTree(ArrayList<Soldier> solList){
        this.solList = (ArrayList<Soldier>) solList.clone();
        this.root    = new Node();
        this.root.previous = null;
    }
    public int heuristicValue(Color c , ArrayList<Soldier> solListy) throws CloneNotSupportedException{
        Color oposite = c.equals(Color.RED)?Color.BLACK:Color.RED;
        int heuristicVal = 0;
        for(Soldier sl:solListy){
            PanelRules pr = new PanelRules(solListy);
            if(sl.C.equals(c))
                heuristicVal = sl.isKing?(heuristicVal+6):(heuristicVal+2);
            if(sl.C.equals(c) && is(sl.i, sl.j , sideSquares))
                heuristicVal = heuristicVal+2;
             if(sl.C.equals(c) && is(sl.i, sl.j , centerSquares) )
                heuristicVal = heuristicVal+2;
             
             int[][] kingSquares = sl.C.equals(Color.black)?blackKingSquares:redKingSquares;
             if(sl.C.equals(c) && is(sl.i, sl.j , kingSquares)){
                 System.out.println("Hypothetical king square " + sl.i + " " + sl.j);
                heuristicVal = heuristicVal+6;
             }
             
            if(sl.C.equals(oposite)
            && !(pr.kingJumpPositions(new Point(sl.i, sl.j), c, true).size()>0
            || pr.getJumps(new Point(sl.i, sl.j), c, true).size()>0) )
                heuristicVal = heuristicVal+3;            
            
            ArrayList<JumpPosition> tempJp = new ArrayList<>();
                if(sl.isKing && sl.C.equals(c)){
                    try {
                        tempJp = new ArrayList<>();
                        tempJp = pr.kingJumpPositions(new Point(sl.i, sl.j), c, true);
                        if(tempJp.size()>0){
                            heuristicVal = heuristicVal+4;
//                            JumpPosition jP = tempJp.get(0);                            
//                            ArrayList<Soldier> fooSold = setupSoldiersGivenJumpPosition(solList, jP.jumpPosition);
//                            for(Soldier foosl:fooSold){
//                                if(foosl.C.equals(oposite)
//                                && !(pr.kingJumpPositions(new Point(foosl.i, foosl.j), c, true).size()>0
//                                || pr.getJumps(new Point(foosl.i, foosl.j), c, true).size()>0) ){
//                                    heuristicVal = heuristicVal+2;
//
//                                }
//                            }
                        
                      }
                  } catch (CloneNotSupportedException ex) {
                      Logger.getLogger(SearchTree.class.getName()).log(Level.SEVERE, null, ex);
                  }
                }else if(sl.C.equals(c)){
                    try {
                        tempJp = new ArrayList<>();
                        tempJp = pr.getJumps(new Point(sl.i, sl.j),sl.C,true);
                        if(tempJp.size()>0){
                              heuristicVal = heuristicVal+2;
                              
//                            JumpPosition jP = tempJp.get(0);
//                            ArrayList<Soldier> fooSold = setupSoldiersGivenJumpPosition(solList, jP.jumpPosition);
//                            for(Soldier foosl:fooSold){
//                                if(foosl.C.equals(oposite)
//                                && !(pr.kingJumpPositions(new Point(foosl.i, foosl.j), c, true).size()>0
//                                || pr.getJumps(new Point(foosl.i, foosl.j), c, true).size()>0) )
//                                    heuristicVal = heuristicVal+3;
//                            }
                        
                      }
                  } catch (CloneNotSupportedException ex) {
                      Logger.getLogger(SearchTree.class.getName()).log(Level.SEVERE, null, ex);
                  }
                }
        }
        
        return heuristicVal;
    }
    public static boolean is(int x, int y , int[][] squareArray){
        for(int i=0;i<squareArray.length;i++)
            if(x==squareArray[i][0] && y==squareArray[i][1])
                return true;
        return false;    
    }
    public static boolean isSideSquare(int x, int y , int[][] squareArray){
        for(int i=0;i<squareArray.length;i++)
            if(x==squareArray[i][0] && y==squareArray[i][1])
                return true;
        return false;    
    }
    public static boolean isCenterSquare(int x, int y){
        for(int i=0;i<sideSquares.length;i++)
            if(x==centerSquares[i][0] && y==centerSquares[i][1])
                return true;
        return false;    
    }
    //Correct way to modify soldier list without modifying hexgame gamepiecesr structure
          
    public void cloneandtwinklewith(){
        ArrayList<Soldier> sL = (ArrayList<Soldier>) solList.clone();
        sL.add(new Soldier(0, 0, Color.RED));
    }
    public ArrayList<JumpPosition> initializeAndSearchTree(ArrayList<JumpPosition> 
            jPList , Color c)  throws CloneNotSupportedException{
        Color currentSearchColor = c;
        root.next = null;
//        searchNodesNextStep(root, currentSearchColor);
//        NegaAlphaBeta(root,2, Integer.MIN_VALUE, Integer.MAX_VALUE);
        //long currentTime         = System.currentTimeMillis();
        //while(currentTime+5000 >System.currentTimeMillis()){
        
        for(int jk=0;jk<4;jk++){
            searchNodesNextStep(root, currentSearchColor);
            NegaAlphaBeta(root,jk+2, Integer.MIN_VALUE, Integer.MAX_VALUE);
            currentSearchColor = toggleColor(currentSearchColor);
        }
        
        bestMoveGrading = Integer.MIN_VALUE;
        System.out.println(bestMoveGrading);
        getBestMoveGrading(root);
        System.out.println(bestMoveGrading);
        
        principleVariation = new JumpPosition();
        
        bestMovesCalculated = new ArrayList<>();
        principleVariation(root, true);
        //populateMoves(root);
        System.out.println(bestMovesCalculated.size());
        
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
            else{
                if(n.value >=bestMoveGrading + (Math.random()*2-5) ) bestMovesCalculated.add(n.jP);
            }
    }
    public void printNodes(Node n){
            if(n.next!=null) for(Node sd: n.next)           printNodes(sd);
            else{
                if(n.value >=bestMoveGrading + (Math.random()*2-4) ){
                    n.jP.print();
                    System.out.println(n.value);
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
                                    if(j+1==jPList.size())
                                        fooSol.isKing = true;                                    
                                    else if(j+1<jPList.size())
                                        if((jPList.get(j+1).from.x!=sN.to.x)&&(jPList.get(j+1).from.y!=sN.to.y))
                                            fooSol.isKing = true;
                                    
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
    public void getBestMoveGrading(Node n){
        if(n.next!=null)    for(Node sd: n.next) getBestMoveGrading(sd);
        else                if(n.value >=bestMoveGrading ) bestMoveGrading = n.value;         
    }
    public void principleVariation(Node n ,boolean max){   
        if(n.next!=null){
            if(max){
                int maxUtil = Integer.MIN_VALUE;
                for(Node sd: n.next)
                  if(maxUtil<sd.value)  
                      maxUtil = sd.value;
                for(Node sd: n.next)
                    if(maxUtil>=sd.value + (Math.random()*1-1) )
                        principleVariation(sd , max?false:true);
            }else{
                int minUtil = Integer.MAX_VALUE;
                for(Node sd: n.next)
                  if(minUtil>sd.value)  
                      minUtil = sd.value;
                for(Node sd: n.next)
                    if(minUtil<=sd.value+ (Math.random()*1))
                        principleVariation(sd , max?false:true);
            }
        
        }
        else
            bestMovesCalculated.add(n.jP);
    }

    public int NegaAlphaBeta(Node n, int depth, int alpha, int beta) throws CloneNotSupportedException{
        int score;
        if(n.next!=null && depth>0){
            score = Integer.MIN_VALUE;
            for(int il=0;il<n.next.size();il++){
                Node sd = n.next.get(il);
                n.value = -NegaAlphaBeta(sd , depth-1, -beta, -alpha);
                if(n.value>score) {
                    score = n.value;
                }
                if(score>alpha){
                    alpha = score;
                }
                if(n.value<0) n.value = -n.value;
                
                if(score>=beta) {                    
                    while (n.next.size()>il+1)    n.next.remove(il+1);   
                    break;
                }
            }
        }
        else
            return heuristicValue(Color.black, setupSoldiersGivenJumpPosition(solList, n.jP.jumpPosition));    
        return score;
    }
    
    public int getDepth(Node n , int counter){
        Node newNode = n;
        while(newNode.previous!=null)   { 
            counter++;
            newNode = newNode.previous;
        }
        return counter;
    }
}