/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        Color opositeEvaluation = Color.BLACK;
        Color colorEvaluated = Color.red;
        int heuristicVal = 0;
        
        int soldierCount = 0;
        for(Soldier sl:solListy)
            if(sl.C.equals(opositeEvaluation))
                soldierCount++;
        
        if(soldierCount==0)
            heuristicVal +=100;
        
        int canBeJumped = 0;
        for(Soldier sl:solListy){
            PanelRules pr = new PanelRules(solListy);
            if(sl.C.equals(colorEvaluated))
                heuristicVal = sl.isKing?(heuristicVal+5):(heuristicVal+2);
            if(sl.C.equals(colorEvaluated) && is(sl.i, sl.j , sideSquares))
                heuristicVal+=2;
            if(sl.C.equals(colorEvaluated) && is(sl.i, sl.j , centerSquares) )
                heuristicVal =+2;             
            int[][] kingSquares = redKingSquares;
            if(sl.C.equals(colorEvaluated) && is(sl.i, sl.j , kingSquares))
                heuristicVal+=5;
             
             /*
              * When black is playing, count if pieces can be captured.
              * if they can't,it's a better position for red soldiers
              */
            if(c.equals(opositeEvaluation)){
                if(sl.C.equals(opositeEvaluation)
                && !sl.isKing
                && pr.getJumps(new Point(sl.i, sl.j), opositeEvaluation, true).size()>0)
                    canBeJumped++;
                if( sl.C.equals(opositeEvaluation)
                && sl.isKing
                && pr.getJumps(new Point(sl.i, sl.j), opositeEvaluation, true).size()==0)
                    canBeJumped++;
            }
            ArrayList<JumpPosition> tempJp = new ArrayList<>();
                if(sl.isKing 
                && sl.C.equals(colorEvaluated)
                && sl.C.equals(c)){
                    try {
                        tempJp = new ArrayList<>();
                        tempJp = pr.kingJumpPositions(new Point(sl.i, sl.j), colorEvaluated, true);
                        if(tempJp.size()>0){
                            heuristicVal = heuristicVal+3;
                            JumpPosition jP = tempJp.get(0);                            
                            ArrayList<Soldier> fooSold = setupSoldiersGivenJumpPosition(solList, jP.jumpPosition);
                            boolean opponentRetaliates = false;
                            for(Soldier foosl:fooSold){
                                if(foosl.C.equals(opositeEvaluation)
                                && (pr.kingJumpPositions(new Point(foosl.i, foosl.j), c, true).size()>0
                                || pr.getJumps(new Point(foosl.i, foosl.j), c, true).size()>0) ){
                                    opponentRetaliates = true;
                                }
                            }
                            if(!opponentRetaliates) heuristicVal +=3;
                        
                      }
                  } catch (CloneNotSupportedException ex) {
                      Logger.getLogger(SearchTree.class.getName()).log(Level.SEVERE, null, ex);
                  }
                }else if(sl.C.equals(colorEvaluated)
                      && sl.C.equals(c)
                      && !sl.isKing){
                    try {
                        tempJp = new ArrayList<>();
                        tempJp = pr.getJumps(new Point(sl.i, sl.j),colorEvaluated,true);
                        if(tempJp.size()>0){
                            heuristicVal = heuristicVal+3;    
                            JumpPosition jP = tempJp.get(0);
                            ArrayList<Soldier> fooSold = setupSoldiersGivenJumpPosition(solList, jP.jumpPosition);
                             boolean opponentRetaliates = false;
                            for(Soldier foosl:fooSold){
                                if(foosl.C.equals(opositeEvaluation)
                                && (pr.kingJumpPositions(new Point(foosl.i, foosl.j), c, true).size()>0
                                || pr.getJumps(new Point(foosl.i, foosl.j), c, true).size()>0) )
                                    opponentRetaliates = true;
                            }
                            if(!opponentRetaliates) heuristicVal +=3;
                        
                      }
                  } catch (CloneNotSupportedException ex) {
                      Logger.getLogger(SearchTree.class.getName()).log(Level.SEVERE, null, ex);
                  }
                }
        }
        if(canBeJumped==0)
            heuristicVal += 6;
        
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
        
        //while(currentTime+5000 >System.currentTimeMillis()){

        int guess = 40;
        int delta = 40;
        int alpha,beta,score;
                
        long currentTime         = System.currentTimeMillis();
        //System.out.println("benchmarking started at " + currentTime);
        for(int jk=0;jk<4;jk++){
            searchNodesNextStep(root, currentSearchColor);
            sortNodes(root);
            //NegaAlphaBeta(root,jk+2, Integer.MIN_VALUE, Integer.MAX_VALUE);
            alpha = 0;
            beta = Integer.MAX_VALUE;
            
            score = -NegaAlphaBeta( root.next.get(0),getDepth(root.next.get(0), 0), -beta, -alpha);
            if( score < beta ) {
                for( int i=1;i<root.next.size();i++ ) {
                        int lbound = score>alpha?score:alpha ; int ubound = lbound + 1;
                        int result = -NegaAlphaBeta(root.next.get(i),getDepth(root.next.get(0), 0), -ubound, -lbound);
                        if( result >= ubound && result < beta ) {
                           result = -NegaAlphaBeta( root.next.get(i),getDepth(root.next.get(0), 0), -beta, -result);
                }
                if( result > score ) score = result;
                if( result >= beta ) break;
                }
            }
            
            
//            
//            alpha = guess - delta; beta = guess + delta;
//            score = NegaAlphaBeta(root,jk+2, alpha, beta);
//            score = score<0?-score:score;
//            if( score >= beta ) {
//                    alpha = score; beta = Integer.MAX_VALUE;
//                    score = NegaAlphaBeta(root,jk+2, alpha, beta);
//            } else if( score <= alpha ) {
//                alpha = Integer.MIN_VALUE; beta = score;
//                score = NegaAlphaBeta(root,jk+2, alpha, beta);
//            }
            currentSearchColor = toggleColor(currentSearchColor);
            long tempTime = System.currentTimeMillis()-currentTime;
            //System.out.println(jk + " ply at " + tempTime + " milisecseconds" );
        }
        long tempTime  =System.currentTimeMillis()-currentTime;
        //System.out.println("benchmarking end at " + tempTime);
        
        bestMovesCalculated = new ArrayList<>();
        
        principleVariation(root, true);
        printNodes(root);
        //System.out.println(bestMovesCalculated.size());
        return bestMovesCalculated;
        
        //printNodes(root);
        
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
                if(n.value==bestMoveGrading ) bestMovesCalculated.add(n.jP);
            }
    }
    public void printNodes(Node n){
            if(n.next!=null) for(Node sd: n.next) printNodes(sd);
            else if(n.value >=bestMoveGrading ){
                n.jP.print(true);
                System.out.println(n.value);
                System.out.println("------------------------------------");
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
                    SearchNode sNn = new SearchNode(new Point(Integer.MAX_VALUE,Integer.MAX_VALUE),new Point(Integer.MAX_VALUE,Integer.MAX_VALUE),new Point(Integer.MAX_VALUE,Integer.MAX_VALUE));                    
                    nextNode.jumpPosition.add(sNn);
                    
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
                  if(maxUtil<=sd.value)  
                      maxUtil = sd.value;
                for(Node sd: n.next)
                    if(maxUtil==sd.value/* + (Math.random()*1-1)*/ )
                        principleVariation(sd,false);
            }else{
                int minUtil = Integer.MAX_VALUE;
                for(Node sd: n.next)
                  if(minUtil>=sd.value)  
                      minUtil = sd.value;
                for(Node sd: n.next)
                    if(minUtil==sd.value/*+ (Math.random()*1)*/)
                        principleVariation(sd,true);
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
        else{
            Color currentTurn =  getDepth(n,0)%2==0?Color.BLACK:Color.RED;
            return heuristicValue(currentTurn, setupSoldiersGivenJumpPosition(solList, n.jP.jumpPosition));
        }
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
    public void sortNodes(Node n){
        if(n.next!=null){
            Collections.sort(n.next, new Comparator<Node>() {
                @Override
                public int compare(Node o1, Node o2) {
                    return o2.value-o1.value;
                }
            });
            for(Node sd: n.next) sortNodes(sd);
        }
    }
}