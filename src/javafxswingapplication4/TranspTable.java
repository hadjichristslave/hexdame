/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxswingapplication4;

import java.util.HashMap;

/**
 *
 * @author Panos
 */

public class TranspTable {
    HashMap<Integer , tableData> hashTable;
    public enum caseType {LOWER_BOUND, UPPER_BOUND, REAL};
    public TranspTable() {
        this.hashTable = new HashMap<>();
    }
    public void add(int hashkey, tableData data){
        this.hashTable.put(hashkey, data);
    }
    
    public class tableData{
        int value;
        caseType valuetype;
        int hashId;
        int depth;
        public tableData(int val, int hashId, caseType valuetype, int depth ){
            this.value     = val;
            this.valuetype = valuetype;
            this.hashId    = hashId;
            this.depth     = depth;        
        }
    
    }
}
