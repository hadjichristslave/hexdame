/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;

import java.awt.Color;
import java.awt.Point;
/**
 *
 * @author Panos
 */
public class Soldier implements Cloneable {
     public int i,j;
     public Color C;
     public boolean isKing = false;
     public Color drawColor;
     public int drawRadius;
     public Point jumpsTo;
     public Point jumpsFrom;
     
    public Soldier(int i , int j , Color c){
        this.i = i;
        this.j = j;
        this.C = c;
    }
    public Soldier(int i , int j ,int drawRadius , Color drawColor , Point JumpsTo , Point jumpsFrom){
        this.i = i;
        this.j = j;
        this.drawColor = drawColor;
        this.drawRadius = drawRadius;
        this.jumpsTo = JumpsTo;
        this.jumpsFrom = jumpsFrom;
    }
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
        public void print(){
            //System.out.println(this.i  + " " + this.j + " " + this.C.toString());
        }
    
}
