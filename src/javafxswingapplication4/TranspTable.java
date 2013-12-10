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
    HashMap<String , tableData> hashTable;
    public enum caseType {LOWER_BOUND, UPPER_BOUND, REAL};
    public boolean goodEnough;
    public TranspTable() {
        this.hashTable = new HashMap<>();
    }
    public void add(String hashkey, tableData data){
        this.hashTable.put(hashkey, data);
    }
    

}
