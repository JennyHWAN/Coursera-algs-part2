package BoggleSolver;

public class Trie {
    public Node root;
    public Trie() {
        root = new Node();
    }
    public void insert(String str) {
        Node cur = root;
        for (int i = 0; i < str.length(); i++) {
            int c = str.charAt(i) - 'A'; // returns the distance between charAt(i) and 'A'
            if (cur.next[c] != null) {
                cur = cur.next[c];
            }
            else {
                cur.next[c] = new Node();
                cur = cur.next[c];
            }
        }
        cur.isTail = true;
    } // end method insert
}
