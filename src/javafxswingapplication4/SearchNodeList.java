/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;

import java.util.ArrayList;
import javafxswingapplication4.PanelRules.Orientation;
import javafxswingapplication4.PanelRules.Movement;

/**
 *
 * @author Panos
 */
public class SearchNodeList  implements Cloneable{
    public ArrayList<JumpPosition> tempSearchNode;
    public SearchNodeList(){
    }
    public SearchNodeList(ArrayList<JumpPosition> k){
        this.tempSearchNode = k;
    }
    public void clear(){
        try{
            this.tempSearchNode.clear();
        }catch(Exception e){}
    }
    public void add(JumpPosition d){
        this.tempSearchNode.add(d);
    }
    public JumpPosition get(int i){
        return this.tempSearchNode.get(i);
    }
    public int size(){
        return this.tempSearchNode.size();
    }
    public void print(){
        System.out.println("********************************");
        System.out.println("Printing the frontier elements");
        for(JumpPosition d:this.tempSearchNode){
            d.print();
        }
        System.out.println("End of printing");
        System.out.println("********************************");
    }
    public void appendtoIndex(int index, SearchNode sN ,  JumpPosition snl){
        int i=200;
        
        
        JumpPosition jp= new JumpPosition();
        for(SearchNode d:snl.jumpPosition){
            //d.print();
            jp.append(d);
        }

        jp.append(sN);
        
        tempSearchNode.set(index, jp);
        int j=200;
    }
    public void add(SearchNode sN ,  JumpPosition snl){
        int i=200;
        JumpPosition jp= new JumpPosition();
        for(SearchNode d:snl.jumpPosition)
            jp.append(d);
        jp.append(sN);
        tempSearchNode.add(jp);
    }
    public void remove(int index){
        tempSearchNode.remove(index);
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    public ArrayList<JumpPosition> getMaxPositions(){
        ArrayList<JumpPosition> answer = new ArrayList<JumpPosition>();
        int max = 0;
        for(JumpPosition js:tempSearchNode)
            if(max<js.jumpPosition.size()) max =js.jumpPosition.size();
        for(JumpPosition js:tempSearchNode)
            if(max==js.jumpPosition.size()) answer.add(js);
        return answer;
    }
}
