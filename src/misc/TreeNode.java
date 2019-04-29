package misc;

import java.util.ArrayList;

import game.Position;

public class TreeNode<T> {

	Double value;
	Pair<Position,Position> bestMove;
    T data;
    TreeNode<T> parent;
    ArrayList<TreeNode<T>> children;

    public TreeNode(T data) {
        this.data = data;
        this.parent = null;
        this.children = new ArrayList<TreeNode<T>>();
    }

    public void addChild(TreeNode<T> childNode) {
        childNode.parent = this;
        this.children.add(childNode);
    }
    
    public TreeNode<T> getParent() {
    	return this.parent;
    }
    
    public void setParent(TreeNode<T> parent) {
    	this.parent = parent;
    }
    
    public ArrayList<TreeNode<T>> getChildren() {
    	return this.children;
    }
    
    public T getData() {
    	return this.data;
    }
    
    public void setData(T data) {
    	this.data = data;
    }
    
    public Double getValue() {
    	return this.value;
    }
    
    public void setValue(Double value) {
    	this.value = value;
    }
    
    public Pair<Position,Position> getMove() {
    	return this.bestMove;
    }
    
    public void setMove(Pair<Position,Position> move) {
    	this.bestMove = move;
    }
}