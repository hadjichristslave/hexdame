/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;

import java.util.ArrayList;


/**
 *
 * @author Panos
 */
public class Node { 
    JumpPosition jP;
    ArrayList<Node> next;
    Node previous;

    public Node () { 
        jP = new JumpPosition();
        next = new ArrayList<Node>(); 
    } 

    public Node (JumpPosition jP , Node previous) { 
        this.jP = jP;
        this.previous = previous;
    }

    public void print () { 
        jP.print(true); 
    }
    public void printparent(){
        System.out.println("comes from ");
        previous.jP.print(true);
    }
} 