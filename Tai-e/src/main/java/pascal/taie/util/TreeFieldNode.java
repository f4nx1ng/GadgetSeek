package pascal.taie.util;

import pascal.taie.ir.proginfo.FieldRef;
import pascal.taie.util.graph.AbstractEdge;

public class TreeFieldNode implements MyNode{
    public String fieldname;
    public int id;

    public TreeFieldNode(String fieldname, int id){
        this.fieldname = fieldname;
        this.id = id;
    }

    public void addId(){
        this.id++;
    }

    public int getId(){
        return this.id;
    }

    @Override
    public boolean equals(Object node) {
        if (node instanceof TreeFieldNode treeFieldNode){
            return this.id == treeFieldNode.getId();
        }else {
            return false;
        }

    }

    @Override
    public String toString() {
        return this.fieldname + "(" + this.id + ")";
    }

    @Override
    public int hashCode() {
        return Hashes.safeHash(this.fieldname, this.id);
    }
}
