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
    final static int[ ][ ] sideSquares = {{0,2},{8,2},{0,6},{8,6},{0,3},{0,5},{8,3},{8,5} };
    final static int[ ][ ] centerSquares = { {2,4} , {3,3} , {4,4},{5,3} , {6,4},
                                             {2,5} , {3,4} , {4,5},{5,4} , {6,5}};
    final static int[ ][ ] redKingSquares = {{0,2},{1,1}, {2,1}, {3,0} ,{4,0}, {5,0} , {6,1}, {7,1} ,{8,2}};
    final static int[ ][ ] blackKingSquares = {{0,6}, {1,6}, {2,7}, {3,7} ,{4,8}, {5,7} , {6,7}, {7,6} , {8,6}};
    final static int[][] kingSquares = redKingSquares;
    TranspTable tT;
    final int tolerance=3;
    final int multiCutTolerance =2;
    long currentTime;
    int timeLimit = 9000;
    
    
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
    public boolean isZugZwag(ArrayList<Soldier> solListy , int nodeVal) throws CloneNotSupportedException{
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
        int canBeJumped = 0;
        int canBeMultiJumped = 0;
        for(Soldier sl:solListy){ 
            if(sl.C.equals(opositeEvaluation))    soldierCount++;
            heuristicVal+=  sl.C.equals(colorEvaluated)?(sl.isKing?6:2):0;
            heuristicVal+= (sl.C.equals(colorEvaluated) && is(sl.i, sl.j , sideSquares))?2:0;
            heuristicVal+= (sl.C.equals(colorEvaluated) && is(sl.i, sl.j , centerSquares))?2:0;
            heuristicVal+= (sl.C.equals(colorEvaluated) && is(sl.i, sl.j , kingSquares))?4:0;             
            /*
              * When black is playing, count if pieces can be captured.
              * if they can't,it's a better position for red soldiers
              */
            PanelRules pr = new PanelRules(solListy);
            if(c.equals(opositeEvaluation)){
                ArrayList<JumpPosition> possiJumps = pr.getJumps(new Point(sl.i, sl.j), opositeEvaluation, true);
                if(sl.C.equals(opositeEvaluation)&& !sl.isKing && possiJumps.size()>0){
                    canBeJumped++;
                    if(possiJumps.get(0).jumpPosition.size()>2)
                        canBeMultiJumped++;
                    if(possiJumps.get(0).jumpPosition.size()>4)
                        return 1;
                }
                possiJumps = pr.kingJumpPositions(new Point(sl.i, sl.j), opositeEvaluation, true);
                if( sl.C.equals(opositeEvaluation)&& sl.isKing&& possiJumps.size()>0){
                    canBeJumped++;
                     if(possiJumps.get(0).jumpPosition.size()>2)
                        canBeMultiJumped++;
                     if(possiJumps.get(0).jumpPosition.size()>4)
                        return 1;
                }
            }else{
                ArrayList<JumpPosition> tempJp;
                if(sl.isKing && sl.C.equals(colorEvaluated)&& c.equals(sl.C)){
                    tempJp =  pr.kingJumpPositions(new Point(sl.i, sl.j), colorEvaluated, true);
                    heuristicVal += tempJp.size()>0?(tempJp.size()>3?5:3):0;
                    if(tempJp.size()>0){
                        boolean canbeRetaliated = false;
                        JumpPosition jP = tempJp.get(0);
                        ArrayList<Soldier> fooSold = setupSoldiersGivenJumpPosition(solList, jP.jumpPosition);
                        for(Soldier foosl:fooSold){
                            if(foosl.C.equals(opositeEvaluation)
                            && !(pr.kingJumpPositions(new Point(foosl.i, foosl.j), c, true).size()>0
                            || pr.getJumps(new Point(foosl.i, foosl.j), c, true).size()>0) ){
                                heuristicVal = heuristicVal+2;
                                for(JumpPosition jPz: tempJp){
                                    ArrayList<Soldier> fooSold3 = setupSoldiersGivenJumpPosition(solList, jPz.jumpPosition);
                                    for(Soldier foosl3:fooSold3){
                                        if(foosl3.C.equals(opositeEvaluation)
                                        && (pr.kingJumpPositions(new Point(foosl3.i, foosl3.j), c, true).size()>0
                                        || pr.getJumps(new Point(foosl3.i, foosl3.j), c, true).size()>0) ){
                                             canbeRetaliated = true;
                                         }
                                    }
                                }
                            }
                        }
                        heuristicVal += !canbeRetaliated?3:0;
                    }
                    
                }else if(sl.C.equals(colorEvaluated)&& c.equals(colorEvaluated)&& !sl.isKing){
                    tempJp = pr.getJumps(new Point(sl.i, sl.j),colorEvaluated,true);
                    heuristicVal += tempJp.size()>0?(tempJp.size()>3?5:3):0;
                    if(tempJp.size()>0){
                        boolean canbeRetaliated = false;
                        JumpPosition jP = tempJp.get(0);
                        ArrayList<Soldier> fooSold = setupSoldiersGivenJumpPosition(solList, jP.jumpPosition);
                        for(Soldier foosl:fooSold){
                            if(foosl.C.equals(opositeEvaluation)
                            && !(pr.kingJumpPositions(new Point(foosl.i, foosl.j), c, true).size()>0
                            || pr.getJumps(new Point(foosl.i, foosl.j), c, true).size()>0) ){
                                heuristicVal = heuristicVal+2;
                                for(JumpPosition jPz: tempJp){
                                    ArrayList<Soldier> fooSold3 = setupSoldiersGivenJumpPosition(solList, jPz.jumpPosition);
                                    for(Soldier foosl3:fooSold3){
                                        if(foosl3.C.equals(opositeEvaluation)
                                        && (pr.kingJumpPositions(new Point(foosl3.i, foosl3.j), c, true).size()>0
                                        || pr.getJumps(new Point(foosl3.i, foosl3.j), c, true).size()>0) ){
                                             canbeRetaliated = true;
                                         }
                                    }
                                }
                            }
                        }
                        heuristicVal += !canbeRetaliated?3:0;
                    }
                 }
            }
        }
        heuristicVal += soldierCount==0?100:0;
        heuristicVal += (canBeJumped==0)?3:0;
        heuristicVal += (!(canBeJumped==0)&&(heuristicVal>3))?-3:0;
        heuristicVal += (canBeMultiJumped==0)?6:0;
        heuristicVal += (!(canBeMultiJumped==0)&&(heuristicVal>6))?-6:0;
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
        int alpha,beta,score;
        tT.hashTable.clear();
        currentTime         = System.currentTimeMillis();
        //System.out.println("benchmarking started at " + currentTime);
        int jk=0;
//        for(int jk=0;jk<5;jk++){
        while(currentTime+8000 >System.currentTimeMillis()){
            System.out.println("----------------");
            searchNodesNextStep(root, currentSearchColor);
            sortNodes(root);
            alpha = 10;
            beta = 70;
            
            
            NextAlphaBeta myNextAB = new NextAlphaBeta(root, currentSearchColor );
            score = -myNextAB.nextAlphaBeta(root, alpha, beta , jk);
            if(score<0) score = -score;
            if( Math.abs(score) < Math.abs(beta) ) {
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
            System.out.println("new it");        
             jk++;
        }
        bestMovesCalculated = new ArrayList<>();
        principleVariation(root, true);
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
        long ourtime = System.currentTimeMillis()-currentTime;
        if( ourtime>timeLimit){
//            System.out.println("returning due to time restrictions");
            return false;
        }
            
            if(n.next!=null)
                for(Node sd: n.next)
                    searchNodesNextStep(sd , c); 
            else{
                
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
                    currentTurn = getDepth(n,0)%2==0?Color.BLACK:Color.RED;
                    newNode.value = heuristicValue(currentTurn, tempSoldAr);                    
                    max = max<newNode.value? newNode.value:max;
                    newNode.previous = n;
                    if(newNode.value >= max)
                        n.next.add(newNode);
                    
                }                
            }
            return false;
    }
    public void getBestMoveGrading(Node n){
        if(n.next!=null)    for(Node sd: n.next) getBestMoveGrading(sd);
        else                if(n.value >=bestMoveGrading ) bestMoveGrading = n.value;         
    }
    public void principleVariation(Node n ,boolean max){   
        if(n.next!=null && n.next.size()>0){
            if(max){
                int maxUtil = Integer.MIN_VALUE;
                for(Node sd: n.next)
                  if(maxUtil<=sd.value)  
                      maxUtil = sd.value;
                for(Node sd: n.next)
                    if(sd.value>= maxUtil-tolerance )
                        principleVariation(sd,false);
            }else{
                int minUtil = Integer.MAX_VALUE;
                for(Node sd: n.next)
                  if(minUtil>=sd.value)  
                      minUtil = sd.value;
                for(Node sd: n.next)
                    if(sd.value<=minUtil+tolerance)
                        principleVariation(sd,true);
            }
        
        }
        else
            bestMovesCalculated.add(n.jP);
    }

    public int NegaAlphaBeta(Node n, int depth, int alpha, int beta) throws CloneNotSupportedException, NoSuchAlgorithmException{
        long ourtime = System.currentTimeMillis()-currentTime;
        if( ourtime>timeLimit){
//            System.out.println("returning due to time restrictions");
            return n.value;
        }
        int score;
        int dd = getDepth(n, 0);
        int thise=232;
        if(n.next!=null && depth>0){
            score = Integer.MIN_VALUE;
            
            /* Multicut part of the nega alpha beta*/
            int c=0;
            for(int il=0;il<n.next.size();il++){
                Node sd = n.next.get(il);
                int value = NegaAlphaBeta(sd, -beta, -alpha, depth-1);
                if(Math.abs(value) <= Math.abs(beta)){
                  c++;
                  if(c >= multiCutTolerance){
                    while (n.next.size()>il+1)    n.next.remove(il+1);
                    return beta;
                  }
                }
                if(isZugZwag(setupSoldiersGivenJumpPosition(solList, n.jP.jumpPosition) , n.value)){                    
                    searchNodesNextStep(n, Color.RED);
                    sortNodes(n);
                }
            }
            
            
            for(int il=0;il<n.next.size();il++){
                /* Transposition table of nega alpha beta*/
                Node sd = n.next.get(il);
                /*Iterate through the hash table first*/
                /*If we have a winning position, noneed to search any other nodes*/
                if(sd.value>100){ 
                    while (n.next.size()>il+1)    n.next.remove(il+1);
                    return sd.value;              
                }
                ArrayList<Soldier> fooSold  = setupSoldiersGivenJumpPosition(solList, sd.jP.jumpPosition);
                
                boolean hash_hit = tT.hashTable.containsKey(gethashvalue(fooSold));
                tableData tD     = tT.hashTable.get(gethashvalue(fooSold));
                if(hash_hit && tD.depth>getDepth(sd, 0)){
                    int actualValue = tD.value<0?-tD.value:tD.value;
                    if(tD.value < beta+tolerance){
                        while (n.next.size()>il+1)    n.next.remove(il+1);
                        return tD.value;
                    }
                  switch(tD.valuetype){
                  case LOWER_BOUND:
                     
                     int value = alpha<0?-actualValue:actualValue;
                     if(alpha>0){
                        if(alpha<value){
                        alpha= alpha<0?-actualValue:actualValue;
                        }
                     }else if(alpha>value){
                        alpha= alpha<0?-actualValue:actualValue;
                        }
                    break;
                  case UPPER_BOUND:
                    value = beta<0?-actualValue:actualValue;
                    if(beta>0){
                        if(beta>value){
                            beta= beta<0?-actualValue:actualValue;
                        }
                    }else{
                        if(beta<value){
                            beta= beta<0?-actualValue:actualValue;
                        }
                    }
                    break;
                  case REAL:
                     while (n.next.size()>il+1)    n.next.remove(il+1); 
                    return actualValue;
                      
                  }
                  if(Math.abs(alpha)>=Math.abs(beta)){
                      while (n.next.size()>il+1)    n.next.remove(il+1); 
                      return tD.value<0?-tD.value:tD.value;
                    }
                }
                /*Iterate through the hash table first*/
                
                /* Regular nega alpha beta */
                n.value = -NegaAlphaBeta(sd , depth-1, -beta, -alpha);
                if(n.value>score) {
                    score = n.value;
                }
                if(score>alpha){
                    alpha = score; 
                }
                if(n.value<0) n.value = -n.value;
                
                if(Math.abs(score)>=Math.abs(beta)) {
                    while (n.next.size()>il+1)    n.next.remove(il+1); 
                }
            }
        }
        else{
            /* Hash table insertions*/
            Color currentTurn = getDepth(n,0)%2==0?Color.RED:Color.BLACK;
            caseType cT=  TranspTable.caseType.LOWER_BOUND;
            int value =heuristicValue(currentTurn, setupSoldiersGivenJumpPosition(solList, n.jP.jumpPosition));
            /*variable depth*/
            boolean goodEnough=false;            
            if(currentTurn.equals(Color.red)){
                if(isGoodEnough(Color.red, setupSoldiersGivenJumpPosition(solList, n.jP.jumpPosition))){                    
                    searchNodesNextStep(n, Color.BLACK);
                    sortNodes(n);
                    if(getDepth(n, 0)<3)
                        NegaAlphaBeta(n , 1, -beta, -alpha);
                }
            }else{
                if(isZugZwag(setupSoldiersGivenJumpPosition(solList, n.jP.jumpPosition) , n.value)){                    
                    searchNodesNextStep(n, Color.RED);
                    sortNodes(n);
                    if(getDepth(n, 0)<3)
                        NegaAlphaBeta(n , 1, -beta, -alpha);
                }
            }
            
            
            int currentDepth = getDepth(n,0);
            
            ArrayList<Soldier> fooSold            = setupSoldiersGivenJumpPosition(solList, n.jP.jumpPosition);
            if(value>beta) cT                     = caseType.LOWER_BOUND;
            else if(value<alpha) cT               = caseType.UPPER_BOUND;
            else if(value>beta && value<alpha) cT = caseType.REAL;
            tableData tD = new tableData(value, cT, currentDepth , goodEnough);
            tT.hashTable.put(gethashvalue(fooSold),tD);
            
            
            /* value return as normal */
            return value;
        }
        return score;
    }
    public String gethashvalue(ArrayList<Soldier> sL) throws NoSuchAlgorithmException{
        int hashvalue=0;
        for(int i=0;i<sL.size();i++){
            Soldier sol = sL.get(i);
            String str = sol.C.equals(Color.BLACK)?"0":"1";
            String val = sol.i +  "" + sol.j + "" + str;
            int newXorVal = Integer.parseInt(val)*i;
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
      public class NextStep extends Thread {
          Node n;
          Color c;
          public NextStep(Node n , Color color){
              this.n = n;
              this.c = color;
          }
        public void run(){
              try {
                  searchNodesNextStep(n, c);
                  sortNodes(n);
              } catch (CloneNotSupportedException ex) {
                  Logger.getLogger(SearchTree.class.getName()).log(Level.SEVERE, null, ex);
              }
        }
        public void nextStep(){
            try {
                  searchNodesNextStep(n, c);
                  sortNodes(root);
              } catch (CloneNotSupportedException ex) {
                  Logger.getLogger(SearchTree.class.getName()).log(Level.SEVERE, null, ex);
              }
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
            return NegaAlphaBeta( n,d+1, -beta, -alpha);
        }
  }
}