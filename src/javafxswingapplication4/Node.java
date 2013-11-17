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

    public Node () { 
        jP = new JumpPosition();
        next = new ArrayList<Node>(); 
    } 

    public Node (JumpPosition jP) { 
        this.jP = jP;
    }

    public void print () { 
        jP.print(true); 
    }
} 