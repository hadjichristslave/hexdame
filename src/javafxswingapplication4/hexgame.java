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
import java.util.ArrayList;
import java.util.Iterator;
 
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
 
	private void createAndShowGUI()
	{
		DrawingPanel panel = new DrawingPanel();
		//JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("HexDame v0.3");
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
                                        if(pr.containsSoldier(Sold1, gamePiecesr))
                                            hexmech.fillcircle(i,j,g2,Sold1.C);
                                        else if((pr.containsSoldier(Sold2, gamePiecesr)))
                                            hexmech.fillcircle(i,j,g2,Sold2.C);   
                                    }
                                         
                       // Draw Legal Moves
                        for (int i=0;i<BSIZE;i++) 
				for (int j=0;j<BSIZE;j++) 
                                    for(Iterator<Point> m=moves.iterator(); m.hasNext(); ){
                                        Point temp = m.next();
                                        hexmech.fillHex(temp.x,temp.y,-15,g2 ,"");
                                    }
			
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
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            ArrayList<Point> legalMoves;
                            Point p = new Point( hexmech.pxtoHex(e.getX(),e.getY()) );
                            Color colorTurn = CurrentTurn==CurrentTurn.BLACK?Color.black:Color.red;
                            Color invcolorTurn = CurrentTurn==CurrentTurn.BLACK?Color.black:Color.red;
                            
                            if(pr.containsSoldier(new Soldier(p.x, p.y, colorTurn) , gamePiecesr)){
                                moves.clear();
                                currentSelection =  p;
                                
                                
                                for(int i=0;i<gamePiecesr.size();i++){
                                    Point temp = new Point(gamePiecesr.get(i).i, gamePiecesr.get(i).j);
                                    if(gamePiecesr.get(i).C==colorTurn){
                                        pr.getJumps(temp,gamePiecesr.get(i).C , true);
                                    }
                                }
                                
                                legalMoves = pr.getMovingPositions(p.x, p.y, colorTurn);
                                if(legalMoves.size()>0){
                                    for(int i = 0;i<legalMoves.size();i++){
                                      Point nextMove = (Point) legalMoves.get(i);
                                      moves.add(nextMove);
                                    }
                                }
                            }else if( pr.containsSoldier(new Soldier(currentSelection.x, currentSelection.y, colorTurn),gamePiecesr)
                                    && pr.isLegalMove(new Point(p.x, p.y),moves)){
                                    for(int i=0;i<gamePiecesr.size();i++){
                                        if(gamePiecesr.get(i).i==currentSelection.x &&gamePiecesr.get(i).j==currentSelection.y){
                                            gamePiecesr.get(i).i = p.x;
                                            gamePiecesr.get(i).j = p.y;
                                            pr.updatePieces(gamePiecesr);
                                            moves.clear();
                                            currentSelection = new Point(0,0);
                                            CurrentTurn = CurrentTurn==CurrentTurn.BLACK?CurrentTurn.RED:CurrentTurn.BLACK;
                                            }
                                        }
                                }
                            
                            repaint();                            
                        }
                        
		} //end of MyMouseListener class 
	} // end of DrawingPanel class   
}
