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
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import javax.swing.*;
 
/* This is a companion class to hexgame.java. It handles all of the mechanics related to hexagon grids. */
 
public class hexmech
{
	//Constants
	public final static boolean orFLAT= true;
	public final static boolean orPOINT= false;
	public static boolean ORIENT= orFLAT;  
 
	public static boolean XYVertex=true;	
 
	private static int BORDERS=50;
 
	private static int s=0;
	private static int t=0;
	private static int r=0;
	private static int h=0;
 
	public static void setXYasVertex(boolean b) {
		XYVertex=b;
	}
	public static void setBorders(int b){
		BORDERS=b;
	}
 
	public static void setSide(int side) {
		s=side;
		t =  (int) (s / 2);	
		r =  (int) (s * 0.8660254037844);
		h=2*r;
	}
	public static void setHeight(int height) {
		h = height;	
		r = h/2;	
		s = (int) (h / 1.73205);	
		t = (int) (r / 1.73205);	
	}
	public static Polygon hex (int x0, int y0) {
 
		int y = y0 + BORDERS;
		int x = x0 + BORDERS; 
				      
		if (s == 0  || h == 0) {
			System.out.println("ERROR: size of hex has not been set");
			return new Polygon();
		}
 
		int[] cx,cy;
 
		if (XYVertex) 
			cx = new int[] {x,x+s,x+s+t,x+s,x,x-t}; 
		else
			cx = new int[] {x+t,x+s+t,x+s+t+t,x+s+t,x+t,x};	
 
		cy = new int[] {y,y,y+r,y+r+r,y+r+r,y+r};
		return new Polygon(cx,cy,6);
	}
 
	public static void drawHex(int i, int j, Graphics2D g2) {
		int x = i * (s+t);
		int y = j * h + (i%2) * h/2;
		Polygon poly = hex(x,y);
		g2.setColor(hexgame.COLOURCELL);
		//g2.fillPolygon(hexmech.hex(x,y));
		g2.fillPolygon(poly);
		g2.setColor(hexgame.COLOURGRID);
		g2.drawPolygon(poly);
	}
 
	public static void fillHex(int i, int j, int n, Graphics2D g2) {
		char c='o';
		int x = i * (s+t);
		int y = j * h + (i%2) * h/2;
		if (n < 0) {
			g2.setColor(hexgame.COLOURONE);
			g2.fillPolygon(hex(x,y));
			g2.setColor(hexgame.COLOURONETXT);
			c = (char)(-n);
			g2.drawString(""+c, x+r+BORDERS, y+r+BORDERS+4);
			//g2.drawString(x+","+y, x+r+BORDERS, y+r+BORDERS+4);
		}
		if (n > 0) {
                        Color tempsi = new Color(255,255,255,200);
			g2.setColor(tempsi);
			g2.fillPolygon(hex(x,y));
			g2.setColor(hexgame.COLOURTWOTXT);
			c = (char)n;
			g2.drawString(""+n, x+r+BORDERS, y+r+BORDERS+4);
			//g2.drawString(i+","+j, x+r+BORDERS, y+r+BORDERS+4);
		}
	}
        public static void fillHex(int i, int j, int n, Graphics2D g2 , String d) {
		char c='o';
		int x = i * (s+t);
		int y = j * h + (i%2) * h/2;
		if (n < 0) {
			g2.setColor(hexgame.COLOURONE);
			g2.fillPolygon(hex(x,y));
			g2.setColor(Color.PINK);
			c = (char)(-n);
			g2.drawString(""+d, x+r+BORDERS, y+r+BORDERS+4);
		}
		if (n > 0) {
                        Color tempsi = new Color(255,255,255,200);
			g2.setColor(tempsi);
			g2.fillPolygon(hex(x,y));
			g2.setColor(hexgame.COLOURTWOTXT);
			c = (char)n;
			g2.drawString(""+d, x+r+BORDERS, y+r+BORDERS+4);
                        
		}
	}
        public static void fillcircle(int i, int j, Graphics2D g2, Color c) {
		int x = i * (s+t);
		int y = j * h + (i%2) * h/2;		
                Ellipse2D oval1  = new Ellipse2D.Double(x+r,y+r,30,30);
                g2.setColor(c);
                g2.fill(oval1);
        }
        public static void fillcircle(int i, int j, Graphics2D g2, Color c , int sizex , int sizey) {
		int x = i * (s+t);
		int y = j * h + (i%2) * h/2;		
                Ellipse2D oval1  = new Ellipse2D.Double(x+r+5,y+r+5,sizex,sizey);
                g2.setColor(c);
                g2.fill(oval1);
        }
 
	public static Point pxtoHex(int mx, int my) {
		Point p = new Point(-1,-1);
 
		//correction for BORDERS and XYVertex
		mx -= BORDERS;
		my -= BORDERS;
		if (XYVertex) mx += t;
 
		int x = (int) (mx / (s+t)); 
		int y = (int) ((my - (x%2)*r)/h);
 
		int dx = mx - x*(s+t);
		int dy = my - y*h;
 
		if (my - (x%2)*r < 0) return p; 
 
		if (x%2==0) {
			if (dy > r) {	
				if (dx * r /t < dy - r) {
					x--;
				}
			}
			if (dy < r) {	
				if ((t - dx)*r/t > dy ) {
					x--;
					y--;
				}
			}
		} else {  // odd columns
			if (dy > h) {	
				if (dx * r/t < dy - h) {
					x--;
					y++;
				}
			}
			if (dy < h) {	
				//System.out.println("" + (t- dx)*r/t +  " " + (dy - r));
				if ((t - dx)*r/t > dy - r) {
					x--;
				}
			}
		}
		p.x=x;
		p.y=y;
		return p;
	}

    static void displayConflictMoves(ArrayList<JumpPosition> jP, Graphics2D g2 , int distanceFromBottom) {
        int counter =0;
        for(JumpPosition jp: jP){
            String jump = "";
            for(int i=0;i<jp.jumpPosition.size();i++){
                SearchNode sN = jp.jumpPosition.get(i);
                if(i==0)
                    jump =jump+"press " + counter+  " for ";
                if((sN.to.x!=0 &&sN.to.y!=0 )&& sN.to.x !=Integer.MAX_VALUE)
                    jump =jump+ " " + sN.to.x + "-"  + sN.to.y + ","; 
                
            }
            counter++;
            int dist = distanceFromBottom-counter*30;
            g2.setColor(Color.BLUE);
            g2.drawString(jump, 15, dist);        
        
        }
        //g2.drawString(x+","+y, x+r+BORDERS, y+r+BORDERS+4);
    }
}