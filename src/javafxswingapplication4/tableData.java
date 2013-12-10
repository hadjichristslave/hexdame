/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;

/**
 *
 * @author Panos
 */
public class tableData{
        int value;
        TranspTable.caseType valuetype;
        int depth;
        boolean goodEnough;
        public tableData(int val, TranspTable.caseType valuetype, int depth , boolean goodEnough){
            this.value     = val;
            this.valuetype = valuetype;
            this.depth     = depth;
            this.goodEnough = goodEnough;
        }
    
    }
