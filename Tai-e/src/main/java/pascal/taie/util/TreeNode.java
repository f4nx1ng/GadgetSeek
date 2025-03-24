package pascal.taie.util;

import pascal.taie.analysis.graph.flowgraph.Node;
import pascal.taie.analysis.pta.core.cs.element.CSMethod;

import java.util.ArrayList;
import java.util.List;

public class TreeNode implements MyNode{
    public String classname;
    public int id;

    public TreeNode(String classname, int id){
        this.classname = classname;
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public void addId(){
        this.id++;
    }

    @Override
    public boolean equals(Object node) {
        if (node instanceof TreeNode treeNode){
            return this.id == treeNode.getId();
        }else {
            return false;
        }

    }

    @Override
    public String toString() {
        return this.classname + "(" + this.id + ")";
    }

    @Override
    public int hashCode() {
        return Hashes.safeHash(this.classname, this.id);
    }
}
