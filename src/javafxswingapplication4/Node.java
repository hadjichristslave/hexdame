/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;

import java.awt.List;
import java.util.ArrayList;
import java.util.Comparator;


/**
 *
 * @author Panos
 */
public class Node implements Comparator<Node> { 
    JumpPosition jP;
    ArrayList<Node> next;
    Node previous;
    int value=Integer.MIN_VALUE;

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

    @Override
    public int compare(Node o1, Node o2) {
        return o1.value - o2.value;
    }
} 