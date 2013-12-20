/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Panos
 */
public class AlphaBeta {
    public int alphaz = -1000;
    public int betaz  = 1000;
    public int score = 0;
    public static void main(String[] args) throws CloneNotSupportedException, NoSuchAlgorithmException{
       AlphaBeta ab = new AlphaBeta();    
    }
    public AlphaBeta() throws CloneNotSupportedException, NoSuchAlgorithmException{
        Node no = new Node();
        no.value = 6;
        no.previous =null;
        ArrayList<Node> sd = new ArrayList<>();
        Node nd = new Node();
        nd.value = 6;
        nd.previous = no;
        Node nz = new Node();
        nz.value = 2;
        nz.previous = no;                
        sd.add(nd);
        sd.add(nz);
        no.next = sd;
        

        ArrayList<Node> sd2 = new ArrayList<>();
        Node nd2 = new Node();
        nd2.value = 6;
        nd2.previous = nd;
        Node nz2 = new Node();
        nz2.value = 8;
        nz2.previous = nd;
        sd2.add(nd2);
        sd2.add(nz2);
        nd.next = sd2;

        ArrayList<Node> sd3 = new ArrayList<>();
        Node nd3 = new Node();
        nd3.value = 8;
        nd3.previous = nz2;
        Node nz3 = new Node();
        nz3.value = 4;
        nz3.previous = nz2;
        sd3.add(nd3);
        sd3.add(nz3);
        nz2.next = sd3;

        ArrayList<Node> sd4 = new ArrayList<>();
        Node nd4 = new Node();
        nd4.value = 6;
        nd4.previous = nd2;
        Node nz4 = new Node();
        nz4.value = 3;
        nz4.previous = nd2;
        sd4.add(nd4);
        sd4.add(nz4);
        nd2.next = sd4;

        ArrayList<Node> sd5 = new ArrayList<>();
        Node nd5 = new Node();
        nd5.value = 7;
        nd5.previous = nd4;
        Node nz5 = new Node();
        nz5.value = 6;
        nz5.previous = nd4;
        sd5.add(nd5);
        sd5.add(nz5);
        nd4.next = sd5;

        ArrayList<Node> sd6 = new ArrayList<>();
        Node nd6 = new Node();
        nd6.value = 3;
        nd6.previous = nz4;
        Node nz6 = new Node();
        nz6.value = 2;
        nz6.previous = nz4;
        sd6.add(nd6);
        sd6.add(nz6);
        nz4.next = sd6;

        ArrayList<Node> sd7 = new ArrayList<>();
        Node nd7 = new Node();
        nd7.value = 8;
        nd7.previous = nd3;
        Node nz7 = new Node();
        nz7.value = 9;
        nz7.previous = nd3;
        sd7.add(nd7);
        sd7.add(nz7);
        nd3.next = sd7;

        ArrayList<Node> sd8 = new ArrayList<>();
        Node nd8 = new Node();
        nd8.value = 8;
        nd8.previous = nz3;
        Node nz8 = new Node();
        nz8.value = 9;
        nz8.previous = nz3;
        sd8.add(nd8);
        sd8.add(nz8);
        nz3.next = sd8;

        ArrayList<Node> sd9 = new ArrayList<>();
        Node nd9 = new Node();
        nd9.value = 2;
        nd9.previous = nz;
        Node nz9 = new Node();
        nz9.value = 5;
        nz9.previous = nz;
        sd9.add(nd9);
        sd9.add(nz9);
        nz.next = sd9;

        ArrayList<Node> sd10 = new ArrayList<>();
        Node nd10 = new Node();
        nd10.value = 1;
        nd10.previous = nz9;
        Node nz10 = new Node();
        nz10.value = 2;
        nz10.previous = nz9;
        sd10.add(nd10);
        sd10.add(nz10);
        nd9.next = sd10;
        nz9.next = sd10;

        ArrayList<Node> sd11 = new ArrayList<>();
        Node nd11 = new Node();
        nd11.value = 1;
        nd11.previous = nd10;
        Node nz11 = new Node();
        nz11.value = 10;
        nz11.previous = nd10;
        sd11.add(nd11);
        sd11.add(nz11);
        nd10.next = sd11;

        ArrayList<Node> sd12 = new ArrayList<>();
        Node nd12 = new Node();
        nd12.value = 2;
        nd12.previous = nz9;
        Node nz12 = new Node();
        nz12.previous = nz9;
        nz12.value = 11;
        sd12.add(nd12);
        sd12.add(nz12);
        nz10.next = sd12;
        nz9.next = sd12;
        
        AlphaBetaz(no, 5, -100, 100, true);
        int asdf = 123412;
    
    }
     public int AlphaBetaz(Node n, int depth, int alpha, int beta, boolean maxPlay) throws CloneNotSupportedException, NoSuchAlgorithmException{
        if(n.next!=null  && n.next.size()>0){
                Node sd;
                if(maxPlay){
                    for(int ila=0;ila<n.next.size();ila++){
                        sd = n.next.get(ila);
                        int val = AlphaBetaz(sd , depth-1, alpha, beta, false);
                        alpha = alpha>val?alpha:val;
                        if(beta<=alpha){
                            while (n.next.size()>ila+1)
                                n.next.remove(ila+1);
                            break;
                        }
                    }
                    return alpha;
                }else{
                    for(int ila=0;ila<n.next.size();ila++){
                        sd = n.next.get(ila);
                        int val = AlphaBetaz(sd , depth-1, alpha, beta, true);
                        beta = beta<val?beta:val;
                        if(beta<=alpha){
                            while (n.next.size()>ila+1)
                                n.next.remove(ila+1);
                            break;
                        }
                    }
                    return beta;
                }
        }
        else{
            return n.value;
        }
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
}