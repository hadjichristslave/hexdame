/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;
import java.awt.Color;
import java.awt.Point;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafxswingapplication4.TranspTable.caseType;
/**
 * @author Panos
 */
public class SearchTree{

    private ArrayList<Soldier> solList;
    Node root;
    final static int[ ][ ] sideSquares = {{0,6},{8,6}};
    final static int[ ][ ] centerSquares = {{4,4}};
    final static int[ ][ ] redKingSquares = {{0,2},{1,1}, {2,1}, {3,0} ,{4,0}, {5,0} , {6,1}, {7,1} ,{8,2}};
    final static int[ ][ ] blackKingSquares = {{0,6}, {1,6}, {2,7}, {3,7} ,{4,8}, {5,7} , {6,7}, {7,6} , {8,6}};
    final static int[][] kingSquares = redKingSquares;
    TranspTable tT;
    final int tolerance=0;
    final int multiCutTolerance =1;
    long currentTime;
    int timeLimit = 15000;
    public int score =0;
    public int bestMoveGrading;
    public JumpPosition principleVariation;
    public ArrayList<JumpPosition> bestMovesCalculated;
    public SearchTree(ArrayList<Soldier> solList){
        this.solList = (ArrayList<Soldier>) solList.clone();
        this.root    = new Node();
        this.root.previous = null;
        tT = new TranspTable();
    }
    public boolean isGoodEnough(Color c, ArrayList<Soldier> solListy) throws CloneNotSupportedException{
        PanelRules pr = new PanelRules(solListy);
        ArrayList<JumpPosition> tempJp;
        for(Soldier sl:solListy){
            if(sl.C.equals(Color.BLACK)) continue;            
            if(sl.isKing){
                tempJp = pr.kingJumpPositions(new Point(sl.i, sl.j), Color.red, true);
                if(tempJp.size()>0){
                    return true;
                }
            }else if(!sl.isKing){  
                tempJp = pr.getJumps(new Point(sl.i, sl.j),Color.red,true);
                if(tempJp.size()>0)
                    return true;
            }
        }
        return false;
    }
    public boolean checkNullMove(ArrayList<Soldier> solListy , int nodeVal) throws CloneNotSupportedException{
        PanelRules pr = new PanelRules(solListy);
        ArrayList<JumpPosition> tempJp = pr.getLegalMoves(Color.black,solListy);
        ArrayList<Soldier> fooSold;
        int moveSize = 0;
        for(JumpPosition jp:tempJp){
            fooSold = setupSoldiersGivenJumpPosition(solList, jp.jumpPosition);
            if( heuristicValue(Color.RED, fooSold)>nodeVal)
                moveSize++;
            
        }
        return moveSize>=(tempJp.size()-2);
    }
    public boolean tryNullMove(ArrayList<Soldier> solListy , int nodeVal) throws CloneNotSupportedException{
        PanelRules pr = new PanelRules(solListy);
        ArrayList<JumpPosition> tempJp = pr.getLegalMoves(Color.black,solListy);
        ArrayList<Soldier> fooSold;
        int moveSize = 0;
        for(JumpPosition jp:tempJp){
            fooSold = setupSoldiersGivenJumpPosition(solList, jp.jumpPosition);
            if( heuristicValue(Color.BLACK, fooSold)>nodeVal)
                moveSize++;
            
        }
        return moveSize==(tempJp.size()-1);
    }
   public int heuristicValue(Color c , ArrayList<Soldier> solListy) throws CloneNotSupportedException{
       
        Color opositeEvaluation = Color.BLACK;
        Color colorEvaluated = Color.RED;
        int heuristicVal = 0;
        int soldierCount = 0;        
        int oursoldierCount = 0;        
        int canBeJumped = 0;
        int canBeMultiJumped = 0;
        boolean canbeRetaliated = false;
        for(Soldier sl:solListy){ 
            if(sl.C.equals(opositeEvaluation))    soldierCount++;
            else oursoldierCount++;
            heuristicVal+=  sl.C.equals(colorEvaluated)?(sl.isKing?6:3):0;
            heuristicVal-=  sl.C.equals(opositeEvaluation)?(sl.isKing?6:3):0;
            heuristicVal+=  sl.C.equals(colorEvaluated)&&is(sl.i, sl.j , sideSquares)?2:0;
            heuristicVal+=  sl.C.equals(colorEvaluated) && is(sl.i, sl.j , centerSquares)?2:0;
            heuristicVal+=  sl.C.equals(colorEvaluated) && is(sl.i, sl.j , kingSquares)?6:0;             
            /*
              * When black is playing, count if pieces can be captured.
              * if they can't,it's a better position for red soldiers
              */
            PanelRules pr = new PanelRules(solListy);
            ArrayList<JumpPosition> myMoves = pr.getLegalMoves(colorEvaluated, solList);
            ArrayList<JumpPosition> opMoves = pr.getLegalMoves(opositeEvaluation, solList);
            int opjumps;
            opjumps = pr.getJumps(new Point(sl.i,sl.j),opositeEvaluation, true).size()+
            pr.kingJumpPositions(new Point(sl.i,sl.j),opositeEvaluation, true).size();
//            if(myMoves.size()> opMoves.size() && opjumps==0)
//                heuristicVal+=myMoves.size()-opMoves.size();
//            else if(opjumps==0)
//                heuristicVal-=opMoves.size()-myMoves.size();
            
            if(c.equals(opositeEvaluation)){
                heuristicVal-=  sl.C.equals(opositeEvaluation) && is(sl.i, sl.j , blackKingSquares)?1:0;
                ArrayList<JumpPosition> possiJumps = pr.getJumps(new Point(sl.i, sl.j), opositeEvaluation, true);
                if(sl.C.equals(opositeEvaluation)&& !sl.isKing && possiJumps.size()>0){
                    heuristicVal -=4;
                    if(possiJumps.get(0).jumpPosition.size()>3)
                        heuristicVal -=8;
                }
                possiJumps = pr.kingJumpPositions(new Point(sl.i, sl.j), opositeEvaluation, true);
                if( sl.C.equals(opositeEvaluation)&& sl.isKing&& possiJumps.size()>0){
                    heuristicVal -=5;
                     if(possiJumps.get(0).jumpPosition.size()>2)
                        heuristicVal -=8;
                }
            }else{
                ArrayList<JumpPosition> tempJp;
                if(sl.isKing && sl.C.equals(colorEvaluated)&& c.equals(sl.C)){
                    tempJp =  pr.kingJumpPositions(new Point(sl.i, sl.j), colorEvaluated, true);
                    heuristicVal += tempJp.size()>0?(tempJp.size()>3?2:5):0;
                    if(tempJp.size()>0){
                        JumpPosition jP = tempJp.get(0);
                        ArrayList<Soldier> fooSold = setupSoldiersGivenJumpPosition(solList, jP.jumpPosition);
                        for(Soldier foosl:fooSold){
                            if(foosl.C.equals(opositeEvaluation)
                            && !(pr.kingJumpPositions(new Point(foosl.i, foosl.j), opositeEvaluation, true).size()>0
                            || pr.getJumps(new Point(foosl.i, foosl.j), opositeEvaluation, true).size()>0) ){
                                heuristicVal = heuristicVal-5;
                                break;
                            }
                        }
                    }
                    
                }else if(sl.C.equals(colorEvaluated)&& c.equals(colorEvaluated)&& !sl.isKing){
                    tempJp = pr.getJumps(new Point(sl.i, sl.j),colorEvaluated,true);
                    heuristicVal += tempJp.size()>0?(tempJp.size()>3?2:5):0;
                    if(tempJp.size()>0){
                        JumpPosition jP = tempJp.get(0);
                        ArrayList<Soldier> fooSold = setupSoldiersGivenJumpPosition(solList, jP.jumpPosition);
                        for(Soldier foosl:fooSold){
                            if(foosl.C.equals(opositeEvaluation)
                            && !(pr.kingJumpPositions(new Point(foosl.i, foosl.j), c, true).size()>0
                            || pr.getJumps(new Point(foosl.i, foosl.j), c, true).size()>0) ){
                                heuristicVal = heuristicVal-5;
                                break;
                            }
                        }
                    }
                 }
            }
        }
        heuristicVal += soldierCount==0?100:0;
        heuristicVal -= oursoldierCount==0?100:0;
        
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
            jPList , Color c)  throws CloneNotSupportedException, NoSuchAlgorithmException{
        Color currentSearchColor = c;
        root.next = null;
        
        currentTime         = System.currentTimeMillis();
        int alpha ,beta;
            
        for(int jk=0;jk<4;jk++){
            tT.hashTable.clear();
            alpha = 5;
            beta = 30;
            searchNodesNextStep(root, currentSearchColor);
            sortNodes(root);
            score = AlphaBeta(root, jk+1,alpha, beta , true);
            if(score>beta){
                alpha = score ; beta = Integer.MAX_VALUE;
                score = AlphaBeta(root, jk+1,alpha, beta , true);
            }else if(score<=alpha){
                System.out.println("fail low");
                alpha = Integer.MIN_VALUE ; beta = score;
                score = AlphaBeta(root, jk+1,alpha, beta , true);            
            }
            root.value = score;
        }
        bestMovesCalculated = new ArrayList<>();
        principleVariation(root, true , score);
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
                if(n.value==bestMoveGrading ) bestMovesCalculated.add(n.jP);
            }
    }
    public void printNodes(Node n){
            if(n.next!=null) for(Node sd: n.next) printNodes(sd);
            else{
                n.jP.print(true);
                System.out.println(n.value);
                System.out.println("------------------------------------");
            }
    }
    
    public ArrayList<Soldier> setupSoldiersGivenJumpPosition(ArrayList<Soldier> soList , ArrayList<SearchNode> jP) throws CloneNotSupportedException{
        ArrayList<Soldier> fooSold  = new ArrayList<>();
        for(Soldier p : solList) 
            fooSold.add((Soldier) p.clone());
        
        for(int j = 0;j<jP.size();j++){
                    SearchNode sN = jP.get(j);
                    for(Iterator<Soldier> soldier = fooSold.iterator(); soldier.hasNext();){
                        Soldier fooSol = soldier.next();
                        if ( fooSol.i == sN.from.x && fooSol.j ==sN.from.y){
                                fooSol.i =  sN.to.x;
                                fooSol.j =  sN.to.y;
                                if(PanelRules.isKingSquare(fooSol.i, fooSol.j, fooSol.C)){
                                    if(j+1==jP.size() 
                                     ||(j+1<jP.size() && ((jP.get(j+1).from.x!=sN.to.x)&&(jP.get(j+1).from.y!=sN.to.y))) 
                                            )
                                        fooSol.isKing = true;
                                    
                                }
                            }
                        if (fooSol.i == sN.jumps.x && fooSol.j ==sN.jumps.y)
                            soldier.remove();
                    }
                }
        return fooSold;
    }
    public boolean searchNodesNextStep(Node n, Color c) throws CloneNotSupportedException{
            
            if(n.next!=null){
                for(Node sd: n.next){
                    searchNodesNextStep(sd , c);
                }
            }else{
                
                ArrayList<Soldier> fooSold = setupSoldiersGivenJumpPosition(solList, n.jP.jumpPosition);                
                Color currentTurn = getDepth(n,0)%2==0?Color.RED:Color.BLACK;
                //Color oposite = c.equals(Color.RED)?Color.BLACK:Color.RED;
                PanelRules pr = new PanelRules(fooSold);
                ArrayList<JumpPosition> NextNodes = pr.getLegalMoves(currentTurn, fooSold);                
                n.next = new ArrayList<>();
                int max =0;
                int maxJump = 0;
                
                for(JumpPosition jP: NextNodes){
                    if( maxJump< jP.jumpPosition.size())
                        maxJump = jP.jumpPosition.size();
                }
                
                for(JumpPosition nextNode: NextNodes){      
                    if(nextNode.jumpPosition.size()< maxJump) continue;
                    nextNode.jumpPosition.addAll(0 , n.jP.jumpPosition);
                    SearchNode sNn = new SearchNode(new Point(Integer.MAX_VALUE,
                            Integer.MAX_VALUE),new Point(Integer.MAX_VALUE,Integer.MAX_VALUE),
                            new Point(Integer.MAX_VALUE,Integer.MAX_VALUE));
                    nextNode.jumpPosition.add(sNn);
                    
                    /*Create the soldier position for the evaluation function*/
                    ArrayList<Soldier> tempSoldAr  = setupSoldiersGivenJumpPosition(solList, nextNode.jumpPosition);
                    Node newNode =  new Node(nextNode, n);
                    
                    /*Oposite turn assignment as values are to be passed on the next node*/
                    currentTurn = getDepth(n,0)%2==1?Color.BLACK:Color.RED;
                    newNode.somescore = newNode.value = heuristicValue(currentTurn, tempSoldAr);
                    newNode.previous = n;
                    n.next.add(newNode);
                    
                }                
            }
            return false;
    }
    public void getBestMoveGrading(Node n){
        if(n.next!=null)    for(Node sd: n.next) getBestMoveGrading(sd);
        else                if(n.value >=bestMoveGrading ) bestMoveGrading = n.value;         
    }
    public void principleVariation(Node n ,boolean max , int value){   
        
        if(n.next!=null && n.next.size()>0){
            if(max){
                int threshold = Integer.MIN_VALUE;
                for(Node sd: n.next)
                    if(sd.value>= threshold)
                        threshold = sd.value;
                for(Node sd: n.next)
                    if(sd.value>= threshold)
                        principleVariation(sd,false , value);
            }else{
                int threshold = Integer.MAX_VALUE;
                for(Node sd: n.next)
                    if(sd.value<= threshold)
                        threshold = sd.value;
                for(Node sd: n.next)
                    if(sd.value<=threshold)
                        principleVariation(sd,true , value);
            }
        
        }
        else
            bestMovesCalculated.add(n.jP);
    }

    public int AlphaBeta(Node n, int depth, int alpha, int beta , boolean maxPlay) throws CloneNotSupportedException, NoSuchAlgorithmException{
        long ourtime = System.currentTimeMillis()-currentTime;
        if(n.next!=null  && n.next.size()>0  && depth>0){         
            /* Multicut part of the alpha beta*/
            int c=0;
            for(int il=0;il<1;il++){
                Node sd = n.next.get(il);                
                if(n.previous!=null && getDepth(n, 0)%2==0){
                    if(checkNullMove(setupSoldiersGivenJumpPosition(solList, n.previous.jP.jumpPosition) , n.previous.value)){                    
                        while (n.next.size()>il+1)
                            n.next.remove(il+1);
                        break;
                    }
                }
                
                int value = AlphaBeta(sd, 2,alpha, beta,!maxPlay);
                if(value >= beta){
                  c++;
                  if(c >= multiCutTolerance){
                    while (n.next.size()>il+1)
                        n.next.remove(il+1);
                    break;
                  }
                }
                
            }
            
            
            for(int il=0;il<n.next.size();il++){
                
                
                /* Transposition table of nega alpha beta*/
                Node sd = n.next.get(il);
                /*Iterate through the hash table first*/
                /*If we have a winning position, noneed to search any other nodes*/
                ArrayList<Soldier> fooSold  = setupSoldiersGivenJumpPosition(solList, sd.jP.jumpPosition);
                if(fooSold.size()>14) break;
                
                boolean hash_hit = tT.hashTable.containsKey(gethashvalue(fooSold));
                tableData tD     = tT.hashTable.get(gethashvalue(fooSold));
                int dpth = getDepth(sd, 0);
                if(hash_hit && tD.depth>dpth && tD.depth%2==dpth%2){
                  switch(tD.valuetype){
                  case LOWER_BOUND:
                     if(alpha<tD.value)
                         alpha = tD.value;
                    break;
                  case UPPER_BOUND:
                    if(beta> tD.value)
                        beta = tD.value;
                    break;
                  case REAL:
                     while (n.next.size()>il+1)    n.next.remove(il+1); 
                     return tD.value;
                  }
                  if(alpha>=beta){
                      while (n.next.size()>il+1)    n.next.remove(il+1); 
                      return getDepth(sd, 0)%2==0?alpha:beta;
                    }
                }
            }
                /*Iterate through the hash table first*/
                
                /* Regular nega alpha beta */
                Node sd;
                if(maxPlay){
                    for(int ila=0;ila<n.next.size();ila++){
                        sd = n.next.get(ila);
                        int val = AlphaBeta(sd , depth-1, alpha, beta, false);
                        alpha = alpha>val?alpha:val;
                        if(beta<=alpha){
                            while (n.next.size()>ila+1)
                                n.next.remove(ila+1);
                            break;
                        }
                    }
                    n.value = alpha;
                    int currentDepth = getDepth(n,0);
                    caseType cT = caseType.REAL;
                    ArrayList<Soldier> fooSold            = setupSoldiersGivenJumpPosition(solList, n.jP.jumpPosition);
                    if(n.value>beta) cT                     = caseType.LOWER_BOUND;
                    else if(n.value<=alpha) cT               = caseType.UPPER_BOUND;
                    else if(n.value>beta && n.value<alpha) cT = caseType.REAL;
                    tableData tD = new tableData(n.value, cT, currentDepth , false);
                    tT.hashTable.put(gethashvalue(fooSold),tD);
                    return alpha;
                }else{
                    for(int ila=0;ila<n.next.size();ila++){
                        sd = n.next.get(ila);
                        int val = AlphaBeta(sd , depth-1, alpha, beta, true);
                        beta = beta<val?beta:val;
                        if(beta<=alpha){
                            while (n.next.size()>ila+1)
                                n.next.remove(ila+1);
                            break;
                        }
                    }
                    n.value = beta;
                    int currentDepth = getDepth(n,0);
                    caseType cT = caseType.REAL;
                    ArrayList<Soldier> fooSold            = setupSoldiersGivenJumpPosition(solList, n.jP.jumpPosition);
                    if(n.value>beta) cT                     = caseType.LOWER_BOUND;
                    else if(n.value<=alpha) cT               = caseType.UPPER_BOUND;
                    else if(n.value>beta && n.value<alpha) cT = caseType.REAL;
                    tableData tD = new tableData(n.value, cT, currentDepth , false);
                    tT.hashTable.put(gethashvalue(fooSold),tD);
                    return beta;
                }
            
        }
        else{
            /* Hash table insertions*/
            Color currentTurn = getDepth(n,0)%2==0?Color.RED:Color.BLACK;
            n.value =heuristicValue(currentTurn, setupSoldiersGivenJumpPosition(solList, n.jP.jumpPosition));
            n.somescore = n.value;
            
            /*variable depth*/
            boolean goodEnough=false;
            if(currentTurn.equals(Color.red)){
                if(isGoodEnough(Color.red, setupSoldiersGivenJumpPosition(solList, n.jP.jumpPosition))){                    
                    searchNodesNextStep(n, Color.BLACK);
                    if(getDepth(n, 0)<3)
                        AlphaBeta(n , 1, alpha, beta, false);
                }
            }
            
            /* value return as normal */
            return n.value;
        }
    }
    public String gethashvalue(ArrayList<Soldier> sL) throws NoSuchAlgorithmException{
        int hashvalue=0;
        for(int i=0;i<sL.size();i++){
            Soldier sol = sL.get(i);
            String str = sol.C.equals(Color.BLACK)?"0":"1";
            String val = sol.i +  "" + sol.j + "" + str;
            int newXorVal = Integer.parseInt(val);
            hashvalue += newXorVal;
        }
        String plaintext = Integer.toString(hashvalue);
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.reset();
        m.update(plaintext.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        String hashtext = bigInt.toString(16);
        return hashtext;
    }
    public int getDepth(Node n , int counter){
        Node newNode = n;
        int foo = counter;
        while(newNode.previous!=null)   { 
            counter++;
            foo++;
            newNode = newNode.previous;
        }
        return foo;
    }
    public void sortNodes(Node n){
        if(n.next!=null ){
            Collections.sort(n.next, new Comparator<Node>() {
                @Override
                public int compare(Node o1, Node o2) {
                    return o1.value-o2.value;
                }
            });
            for(Node sd:n.next)  sortNodes(sd);
        }
    }
  
      public class NextAlphaBeta extends Thread {
          Node n;
          Color c;
          public NextAlphaBeta(Node n , Color color){
              this.n = n;
              this.c = color;
          }
        public int nextAlphaBeta(Node n , int alpha, int beta , int d) throws CloneNotSupportedException, NoSuchAlgorithmException{
            return AlphaBeta( n,d+1, alpha, beta, true);
        }
  }
}