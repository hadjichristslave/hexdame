/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;

/**
 *
 * @author Panos
 */
import java.awt.*;
import javax.swing.*;
import java.awt.event.*; 
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
 
/**********************************
  This is the main class of a Java program to play a game based on hexagonal tiles.
  The mechanism of handling hexes is in the file hexmech.java.
 
  Written by: M.H.
  Date: December 2012
 
 ***********************************/
 
public class hexgame
{
    private ArrayList<Soldier> gamePiecesr = new ArrayList<Soldier>();
    PanelRules pr;
    enum turn {RED, BLACK};
    public turn CurrentTurn;
    ArrayList moves = new ArrayList<Point>();
    Point currentSelection = new Point();
    int moveCounter    = 0;
        private hexgame() {                
		initGame();
		createAndShowGUI();
	}
 
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
				public void run() {
				new hexgame();
				}
				});
	}
 
	//constants and global variables
	final static Color COLOURBACK =  Color.WHITE;
	final static Color COLOURCELL =  Color.ORANGE;	 
	final static Color COLOURGRID =  Color.BLACK;	 
	final static Color COLOURONE = new Color(255,255,255,200);
	final static Color COLOURONETXT = Color.BLUE;
	final static Color COLOURTWO = new Color(0,0,0,200);
	final static Color COLOURTWOTXT = new Color(255,100,255);
	final static int EMPTY = 0;
	final static int BSIZE = 9; //board size.
	final static int HEXSIZE = 60;	//hex size in pixels
	final static int BORDERS = 15;  
	final static int SCRSIZE = HEXSIZE * (BSIZE + 1) + BORDERS*3; //screen size (vertical dimension).
        final static int[ ][ ] redKingSquares = {{0,2},{1,1}, {2,1}, {3,0} ,{4,0}, {5,0} , {6,1}, {7,1} ,{8,2}};
        final static int[ ][ ] blackKingSquares = {{0,6}, {1,6}, {2,7}, {3,7} ,{4,8}, {5,7} , {6,7}, {7,6} , {8,6}};
        final static int[] hashvalues = new int[1952];
        private ArrayList<Soldier>      captureSoldierPositionAndColor = new ArrayList<Soldier>();
        
        
	int[][] board = new int[BSIZE][BSIZE];
 
	void initGame(){
                CurrentTurn = turn.BLACK;
		hexmech.setXYasVertex(false); //RECOMMENDED: leave this as FALSE. 
		hexmech.setHeight(HEXSIZE); //Either setHeight or setSize must be run to initialize the hex
		hexmech.setBorders(BORDERS);
 
		for (int i=0;i<BSIZE;i++) {
			for (int j=0;j<BSIZE;j++) {
				board[i][j]= i + j ;
			}
		}
                setupGamePieces();
	}
	private void createAndShowGUI(){
 		DrawingPanel panel = new DrawingPanel();
		//JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("HexDame v0.5");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                Container content = frame.getContentPane();
                content.add(panel);
                                
		//this.add(panel);  -- cannot be done in a static context
		//for hexes in the FLAT orientation, the height of a 10x10 grid is 1.1764 * the width. (from h / (s+t))
		frame.setSize( (int)(SCRSIZE/1.23), SCRSIZE);
		frame.setResizable(false);
		frame.setLocationRelativeTo( null );
		frame.setVisible(true);
	}

    /*
     * isValidSquare returns if the square chosen is within the valid range.
     * If not, no further action will be taken for this square
     *
     */
      
        public void setupGamePieces(){
            for(int i=0;i<BSIZE;i++) 
                for (int j=0;j<BSIZE;j++) 
                    if( PanelRules.isValidSquare(i, j) && PanelRules.isValidSoldierSquare(i, j) && j<4 )
                        gamePiecesr.add(new Soldier(i , j , Color.black));
                    else if(PanelRules.isValidSquare(i, j) && PanelRules.isValidSoldierSquare(i, j) && j>4)
                        gamePiecesr.add(new Soldier(i , j , Color.red));            
            pr = new PanelRules(gamePiecesr);
        }
        
        
	class DrawingPanel extends JPanel
	{
            public int deleteX = 0;
            public int deleteY = 0;
            public int drawX   = 0;
            public int drawY   = 0;
                
		//mouse variables here
		//Point mPt = new Point(0,0);
 
		public DrawingPanel()
		{
			setBackground(COLOURBACK);
                	MyMouseListener ml = new MyMouseListener();
			addMouseListener(ml);
                        addMouseMotionListener(new MouseMotionAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent me) {
                                //System.out.println(me.getX() + " " + me.getY());
                            }

                        });
		}
 
                @Override
		public void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
			super.paintComponent(g2);
			//draw grid
			for (int i=0;i<BSIZE;i++) 
				for (int j=0;j<BSIZE;j++) 
                                    if( PanelRules.isValidSquare(i, j) )
					hexmech.drawHex(i,j,g2);
			
			//fill in hexes
			for (int i=0;i<BSIZE;i++) 
				for (int j=0;j<BSIZE;j++) 
                                     if( PanelRules.isValidSquare(i, j) )
					hexmech.fillHex(i,j,board[i][j],g2 , i+ " "+ j);
                        //fill in the soldiers for both sides
			for (int i=0;i<BSIZE;i++)
				for (int j=0;j<BSIZE;j++)
                                    if( PanelRules.isValidSquare(i, j)){                                     
                                        Soldier Sold1 = new Soldier(i , j , Color.black);
                                        Soldier Sold2 = new Soldier(i , j , Color.red);
                                        if(pr.containsSoldier(Sold1, gamePiecesr)){
                                            hexmech.fillcircle(i,j,g2,Sold1.C);
                                            boolean isKing = false;
                                            for(Soldier gr:gamePiecesr){
                                                if(gr.i == Sold1.i && gr.j==Sold1.j && gr.isKing==true)
                                                    isKing=true;
                                            }
                                            if(isKing)
                                                hexmech.fillcircle(i,j,g2,Color.red ,20, 20);
                                        }
                                        else if((pr.containsSoldier(Sold2, gamePiecesr))){
                                            hexmech.fillcircle(i,j,g2,Sold2.C); 
                                            boolean isKing   = false;
                                            for(Soldier gr:gamePiecesr){
                                                if(gr.i == Sold2.i && gr.j==Sold2.j && gr.isKing==true)
                                                    isKing=true;
                                            }
                                            if(isKing)
                                                hexmech.fillcircle(i,j,g2,Color.black ,20, 20);
                                        }
                                    }
                       // Draw Legal Moves
                        for(Iterator<Point> m=moves.iterator(); m.hasNext(); ){
                            Point temp = m.next();
                            hexmech.fillHex(temp.x,temp.y,-15,g2 ,"");
                        }
                        // Draw Jumps
                        for(Iterator<Point> m=moves.iterator(); m.hasNext(); ){
                            Point temp = m.next();
                            //System.out.println("Will fill" + temp.toString());
                            hexmech.fillHex(temp.x,temp.y,-15,g2 ,"");
                        }
                        for(Soldier sold:captureSoldierPositionAndColor )
                           hexmech.fillcircle(sold.i,sold.j,g2,sold.drawColor ,sold.drawRadius, sold.drawRadius);
                        
		}
                @Override
                public void paint(Graphics g) {
                    super.paint(g);
                    Graphics2D drawImage = (Graphics2D) g;
                    
                    Color d = Color.BLACK;
                    Point p = new Point( hexmech.pxtoHex(drawX,drawY) );
                    hexmech.fillcircle(p.x,p.y,drawImage,d);
                    Color c =  new Color(255,200,0);
                    p = new Point( hexmech.pxtoHex(deleteX,deleteY) );
                    hexmech.fillcircle(p.x,p.y,drawImage,c);
                 }
                
 
		class MyMouseListener extends MouseAdapter  {	//inner class inside DrawingPanel 
//                        @Override
//                        public void mouseEntered(MouseEvent e) {
//                            try {
//                                    Color colorTurn = CurrentTurn==CurrentTurn.BLACK?Color.black:Color.red;
//                                    Color invTurn = CurrentTurn==CurrentTurn.BLACK?Color.RED:Color.BLACK;
//                                    playAMove(colorTurn , getLegalMoves(colorTurn));
//                                    repaint();
//                                    playAMove(invTurn, getLegalMoves(invTurn));
//                                    repaint();
//                            } catch (CloneNotSupportedException | FileNotFoundException | UnsupportedEncodingException | NoSuchAlgorithmException ex) {
//                                Logger.getLogger(hexgame.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                        }
                        @Override
                        public void mouseClicked(MouseEvent e ) {                            
                            ArrayList<Point> legalMoves;
                            Point p = new Point( hexmech.pxtoHex(e.getX(),e.getY()) );                            
                            Color colorTurn = CurrentTurn==CurrentTurn.BLACK?Color.black:Color.red;
                            Color invcolorTurn = CurrentTurn==CurrentTurn.BLACK?Color.black:Color.red;
                            try {
                                getLegalMoves(colorTurn);
                            } catch (CloneNotSupportedException | FileNotFoundException | UnsupportedEncodingException ex) {
                                Logger.getLogger(hexgame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            ArrayList<JumpPosition> listOfAllJumps;
                            if(pr.containsSoldier(new Soldier(p.x, p.y, colorTurn) , gamePiecesr) && PanelRules.isValidSquare(p.x, p.y)){
                                //Clear the moves for multijumps of kings and soldiers
                                captureSoldierPositionAndColor.clear();
                                //Clear the normal moves of kings and soldiers
                                moves.clear();
                                currentSelection =  p;
                                //Get all the jumps
                                boolean jumpsExist = false;
                                int maxJumpCount = getMaxJump(colorTurn);
                                for(int i=0;i<gamePiecesr.size();i++){
                                    JumpPosition sdf = new JumpPosition();
                                    listOfAllJumps = new ArrayList<>();
                                    Point temp = new Point(gamePiecesr.get(i).i, gamePiecesr.get(i).j);
                                    if(gamePiecesr.get(i).C==colorTurn){
                                        try {
                                            //Search for jumps if a pawn is a king
                                            if(gamePiecesr.get(i).isKing)
                                                listOfAllJumps = pr.kingJumpPositions(temp,colorTurn,true);
                                            //Check for normal jumps
                                            listOfAllJumps.addAll(pr.getJumps(temp,gamePiecesr.get(i).C,true));
                                            
                                            //If jumps exist, pass the soldiers jumped to one array, and the
                                            int size = 24;
                                            Random rand = new Random();
                                            Color c = new Color(rand.nextFloat(),rand.nextFloat(),rand.nextFloat());
                                            for(JumpPosition jp :listOfAllJumps){
                                                if(jp.jumpPosition.size()== maxJumpCount){
                                                    jumpsExist=true;
                                                    int offset = jp.jumpPosition.size()==1?1:2;
                                                    Point reaches = jp.jumpPosition.get(jp.jumpPosition.size()-offset).to;
                                                    c = new Color(rand.nextFloat(),rand.nextFloat(),rand.nextFloat());
                                                    size = size -3;
                                                    for(SearchNode sN:jp.jumpPosition){
                                                        SearchNode from = jp.jumpPosition.get(0);
                                                        if(from.from.x == currentSelection.x && from.from.y == currentSelection.y){
                                                            //System.out.println("from " + from.from.toString() + " reaches "+reaches.toString() );
                                                            captureSoldierPositionAndColor.add(new Soldier(sN.jumps.x , sN.jumps.y, size, c , reaches , from.from));
                                                            moves.add(reaches);
                                                        }
                                                    }
                                                }
                                            }
                                           ArrayList<JumpPosition> tempJp = pr.getJumps(temp,gamePiecesr.get(i).C,true);
                                           if(tempJp.size()>0) jumpsExist=true;
                                            
                                            
                                        } catch (CloneNotSupportedException ex) {
                                            Logger.getLogger(hexgame.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }
                                if(!jumpsExist){
                                    for(int i=0;i<gamePiecesr.size();i++){
                                        ArrayList<Point> asdf  = new ArrayList<Point>();
                                        Point temp = new Point(gamePiecesr.get(i).i, gamePiecesr.get(i).j);
                                        if(gamePiecesr.get(i).isKing && gamePiecesr.get(i).C==colorTurn)
                                            if(gamePiecesr.get(i).i == currentSelection.x && gamePiecesr.get(i).j == currentSelection.y)
                                                asdf= pr.getKingMovingPositions(temp.x, temp.y, colorTurn);
                                        if(asdf.size()>0)
                                            for(Point P:asdf)
                                                moves.add(P);
                                    }
                                    //Get all moves no jumps included
                                    legalMoves = pr.getMovingPositions(p.x, p.y, colorTurn);
                                    if(legalMoves.size()>0){
                                        for(int i = 0;i<legalMoves.size();i++){
                                          Point nextMove = (Point) legalMoves.get(i);
                                          moves.add(nextMove);
                                        }
                                    }
                                }
                            }else if( pr.containsSoldier(new Soldier(currentSelection.x, currentSelection.y, colorTurn),gamePiecesr)
                                    && pr.isLegalMove(new Point(p.x, p.y),moves)
                                    && PanelRules.isValidSquare(p.x, p.y)){
                                    for(int i=0;i<gamePiecesr.size();i++){
                                        if(captureSoldierPositionAndColor.size()==0){
                                            if(gamePiecesr.get(i).i==currentSelection.x &&gamePiecesr.get(i).j==currentSelection.y){
                                                moveTo(i,  p);
                                                if(isKingSquare(p.x, p.y, colorTurn))  gamePiecesr.get(i).isKing =true;
                                                pr.updatePieces(gamePiecesr);
                                                emptyPostCaptureData();
                                                updateTurn();
                                                }
                                        }else{
                                            boolean soldiersCaptured= false;
                                            Soldier currentSol = gamePiecesr.get(i);
                                             if(currentSol.i==currentSelection.x &&currentSol.j==currentSelection.y){
                                                 moveTo(i,  p); 
                                                 for(Soldier Sol:captureSoldierPositionAndColor){
                                                     if(Sol.jumpsTo.equals(p)
                                                      &&Sol.jumpsFrom.equals(currentSelection)){
                                                         if(isKingSquare(p.x, p.y, colorTurn))
                                                             currentSol.isKing =true;
                                                         
                                                         for(Iterator< Soldier > soldier = gamePiecesr.iterator(); soldier.hasNext();){
                                                             Soldier fooSol = soldier.next();
                                                             if (fooSol.i == Sol.i && fooSol.j ==Sol.j){
                                                                 soldier.remove();
                                                                 soldiersCaptured=true;
                                                             }
                                                         }
                                                     }
                                                 }
                                                 if(soldiersCaptured) {
                                                     emptyPostCaptureData();
                                                     updateTurn();
                                                 }
                                             }
                                        }
                                    }
                                    try {
                                        playAMove(colorTurn , getLegalMoves(colorTurn));
                                    } catch (CloneNotSupportedException | FileNotFoundException | UnsupportedEncodingException | NoSuchAlgorithmException ex) {
                                        Logger.getLogger(hexgame.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            
                            repaint();                        
                        }
                        
		} //end of MyMouseListener class 
	} // end of DrawingPanel class   
        public void emptyPostCaptureData(){
            moves.clear();
            captureSoldierPositionAndColor.clear();
            currentSelection = new Point(0,0);
        }
        public void updateTurn(){
            CurrentTurn = CurrentTurn==CurrentTurn.BLACK?CurrentTurn.RED:CurrentTurn.BLACK;
        }
        public void moveTo(int gamePiecesIndex,  Point p){
             gamePiecesr.get(gamePiecesIndex).i = p.x;
             gamePiecesr.get(gamePiecesIndex).j = p.y;
        }
        public boolean isKingSquare(int x, int y , Color c){
            if(c ==Color.black){
                for(int i=0;i<9;i++){
                    if(x==blackKingSquares[i][0] && y==blackKingSquares[i][1])
                        return true;
                }
                
            }else if(c==Color.red){
                 for(int i=0;i<9;i++){
                    if(x==redKingSquares[i][0] && y==redKingSquares[i][1])
                        return true;
                }
            }
            return false;
        }
        public int getMaxJump(Color colorTurn){
            int maxJumpCount = 0;
            for(int i=0;i<gamePiecesr.size();i++){
                ArrayList<JumpPosition> listOfAllJumps = new ArrayList<>();
                if(gamePiecesr.get(i).C==colorTurn){
                    Point temp = new Point(gamePiecesr.get(i).i, gamePiecesr.get(i).j);
                        try {
                            if(gamePiecesr.get(i).isKing)
                                listOfAllJumps = pr.kingJumpPositions(temp,colorTurn,true);
                            listOfAllJumps.addAll(pr.getJumps(temp,gamePiecesr.get(i).C,true));
                            for(JumpPosition jP:listOfAllJumps)    
                                if(jP.jumpPosition.size()>maxJumpCount) maxJumpCount =jP.jumpPosition.size();
                            
                        } catch (CloneNotSupportedException ex) {
                            Logger.getLogger(hexgame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
            }
            return maxJumpCount;
        }
        public ArrayList<JumpPosition> getLegalMoves(Color colorTurn) throws CloneNotSupportedException, FileNotFoundException, UnsupportedEncodingException{
            ArrayList<Point> legalMoves = new ArrayList<>();
            ArrayList<JumpPosition> listOfAllJumps = new ArrayList<>();
            
            ArrayList<JumpPosition> availableMoves = new ArrayList<>();
                //Get all the jumps
                boolean jumpsExist = false;
                for(int i=0;i<gamePiecesr.size();i++){
                    Point temp = new Point(gamePiecesr.get(i).i, gamePiecesr.get(i).j);
                    if(gamePiecesr.get(i).C.equals(colorTurn)){
                        try {
                            //Search for jumps if a pawn is a king
                            ArrayList<JumpPosition> tempJp = new ArrayList<>();
                            if(gamePiecesr.get(i).isKing)
                                 tempJp = pr.kingJumpPositions(temp,colorTurn,true);
                                listOfAllJumps.addAll(tempJp);
                            if(tempJp.size()>0){
                                jumpsExist = true;
                                availableMoves.addAll(tempJp);
                            }
                            
                            //Check for normal jumps
                           tempJp = new ArrayList<>();                    
                           tempJp = pr.getJumps(temp,gamePiecesr.get(i).C,true);
                           availableMoves.addAll(tempJp);
                           int maxMoves = 0;
                           
                           for(Iterator<JumpPosition> jP = availableMoves.iterator(); jP.hasNext();){
                               JumpPosition jPP= jP.next();
                               if(jPP.jumpPosition.size()>maxMoves)
                                   maxMoves = jPP.jumpPosition.size();
                           }
                           for(Iterator<JumpPosition> jP = availableMoves.iterator(); jP.hasNext();){
                               JumpPosition jPP= jP.next();
                               if(jPP.jumpPosition.size()<maxMoves)
                                   jP.remove();
                               
                           }
                           if(tempJp.size()>0) jumpsExist=true;
                        } catch (CloneNotSupportedException ex) {
                            Logger.getLogger(hexgame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }          
                if(!jumpsExist){
                    for(int i=0;i<gamePiecesr.size();i++){
                        Point temp = new Point(gamePiecesr.get(i).i, gamePiecesr.get(i).j);
                        if(gamePiecesr.get(i).isKing && gamePiecesr.get(i).C==colorTurn)
                            if(gamePiecesr.get(i).i == currentSelection.x && gamePiecesr.get(i).j == currentSelection.y){
                                ArrayList<Point> wer = pr.getKingMovingPositions(temp.x, temp.y, colorTurn);
                                legalMoves.addAll(wer);
                                if(wer.size()>0){
                                    for(Point po:wer){
                                        ArrayList<SearchNode> sN = new ArrayList<>();
                                        sN.add(new SearchNode(temp, po, new Point(0,0)));
                                        JumpPosition  jP = new JumpPosition(sN);
                                        availableMoves.add(jP);                                        
                                    }                                
                                }
                            }
                        //Get all moves no jumps included
                        if(gamePiecesr.get(i).C==colorTurn){
                            ArrayList<Point> wer = pr.getMovingPositions(temp.x, temp.y, colorTurn);
                            legalMoves.addAll(wer);
                            if(wer.size()>0){
                                for(Point po:wer){
                                    ArrayList<SearchNode> sN = new ArrayList<>();
                                    sN.add(new SearchNode(temp, po, new Point(0,0)));
                                    JumpPosition  jP = new JumpPosition(sN);
                                    availableMoves.add(jP);
                                }
                            }
                        }
                    }
                }
                return availableMoves;
        }
        
        public void playAMove(Color colorTurn, ArrayList<JumpPosition> availableMoves) throws CloneNotSupportedException, NoSuchAlgorithmException{
            
            SearchTree sT = new SearchTree(gamePiecesr);
            
            for(JumpPosition jP:availableMoves){
                jP.jumpPosition.get(0).from.x = 0;
                jP.jumpPosition.get(0).from.y = 0;
                jP.jumpPosition.get(0).to.x = 0;
                jP.jumpPosition.get(0).to.y = 0;
                jP.jumpPosition.get(0).jumps.x = 0;
                jP.jumpPosition.get(0).jumps.y = 0;
            }
            while(availableMoves.get(0).jumpPosition.size()>1)
                availableMoves.get(0).jumpPosition.remove(1);
            //Addming the first move only all other first moves, redundand.
            JumpPosition startingMove = availableMoves.get(0);
            sT.root.jP = startingMove;
            ArrayList<JumpPosition> getMoves = sT.initializeAndSearchTree(availableMoves , colorTurn);
            
            int move = (int)(Math.random()*(getMoves.size()-1));
            
            getMoves.get(move).print(true);
            int index = 0;
            Color oposite = colorTurn.equals(Color.RED)?Color.BLACK:Color.RED;
            while(getMoves.get(move).jumpPosition.get(index).from.x == Integer.MAX_VALUE
                || ( getMoves.get(move).jumpPosition.get(index).from.x == 0 
                  && getMoves.get(move).jumpPosition.get(index).from.y == 0)
                ||!containsSoldier(new Soldier(getMoves.get(move).jumpPosition.get(index).from.x, 
                    getMoves.get(move).jumpPosition.get(index).from.y , oposite),gamePiecesr )){        
                index++;
            }
            //System.out.println(index);
            
            Point from  = getMoves.get(move).jumpPosition.get(index).from;
            Point to    = new Point();
            
            
            int endIndex = index;
            for(int j = index+1;j<getMoves.get(move).jumpPosition.size();j++){
                if(getMoves.get(move).jumpPosition.get(j-1).to.x == getMoves.get(move).jumpPosition.get(j).from.x 
                && getMoves.get(move).jumpPosition.get(j-1).to.y == getMoves.get(move).jumpPosition.get(j).from.y){
                    endIndex = j;
                }else 
                    break;
            }
            if(index!= endIndex){
                for(int j = index;j<=endIndex;j++)
                    for(int i=0;i<gamePiecesr.size();i++)
                        if(gamePiecesr.get(i).i == getMoves.get(move).jumpPosition.get(j).jumps.x 
                        && gamePiecesr.get(i).j == getMoves.get(move).jumpPosition.get(j).jumps.y)
                            gamePiecesr.remove(i);
            }else{
                for(int i=0;i<gamePiecesr.size();i++)
                    if(gamePiecesr.get(i).i == getMoves.get(move).jumpPosition.get(index).jumps.x 
                    && gamePiecesr.get(i).j == getMoves.get(move).jumpPosition.get(index).jumps.y)
                        gamePiecesr.remove(i);              
            }
            to.x =  getMoves.get(move).jumpPosition.get(endIndex).to.x ;
            to.y =  getMoves.get(move).jumpPosition.get(endIndex).to.y ;
            for(int i=0;i<gamePiecesr.size();i++){
                Soldier sol = gamePiecesr.get(i);
                if(sol.i == from.x && sol.j == from.y){
                       sol.i = to.x;
                       sol.j = to.y;
                       if(isKingSquare(sol.i,sol.j,  sol.C))
                            sol.isKing = true;
                   }
            }
            
            pr.updatePieces(gamePiecesr);
            updateTurn();
            
        }
        public boolean containsSoldier(Soldier test, ArrayList<Soldier> gamePiece){
            for (int i=0;i<gamePiece.size();i++)
                if(test.i == gamePiece.get(i).i && test.j == gamePiece.get(i).j && test.C.equals(gamePiece.get(i).C))
                    return true;
            return false;
        }
}
