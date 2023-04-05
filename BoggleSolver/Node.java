package BoggleSolver;

public class Node {
    public Node[] next;
    public boolean isTail;
    public Node() {
        // there's only upper case letter in this problem
        next = new Node[26];
        isTail = false;
    } // end constructor
}
