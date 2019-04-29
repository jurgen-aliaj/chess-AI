package misc;

public class Pair<L,R> {

	  private L left;
	  private R right;

	  public Pair(L left, R right) {
	    this.left = left;
	    this.right = right;
	  }

	  public L getLeft() { return left; }
	  public R getRight() { return right; }
	  
	  public void setLeft(L x) { this.left = x; }
	  public void setRight(R x) { this.right = x; }
	  
	  public String toString() {
		  return "(" + this.left.toString() + "," + this.right.toString() + ")";
	  }
}