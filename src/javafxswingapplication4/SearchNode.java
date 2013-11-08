/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;

import java.awt.Point;

/**
 *
 * @author Panos
 */
public class SearchNode{
        public Point from;
        public Point to;
        public Point jumps;
        public SearchNode(Point from, Point to , Point jumps){
            this.from  = from;
            this.to    = to;
            this.jumps = jumps;
        }
        public void print(){
            System.out.println(this.from.toString() +  " "  +this.to.toString() + " " +this.jumps.toString() );
        }
    }