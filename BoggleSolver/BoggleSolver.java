package BoggleSolver;

import edu.princeton.cs.algs4.In;

import java.util.HashSet;

public class BoggleSolver {
    private Trie trie;
    private boolean[][] visited;
    private int row, col;
    private HashSet<String> result, dict;
    private BoggleBoard board;
    private static final int[] dir_r = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] dir_c = {-1, 0, 1, -1, 1, -1, 0, 1};


    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        trie = new Trie();
        dict = new HashSet<String>();
        dict.clear();

        // iterate for every word in dictionary;
        for (String s: dictionary) {
            dict.add(s);
            // if s contains letter 'Q' but not followed by 'U' then don't insert
            boolean valid = true;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == 'Q' && s.charAt(i + 1) != 'U') {
                    valid = false;
                    break;
                }
            } // end of loop
            if (!valid) continue;
            s = s.replace('QU', 'Q');
            trie.insert(s);
        } // end of loop
    } // end constructor

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        row = board.rows();
        col = board.cols();
        visited = new boolean[row][col];
        board = board;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                visited[i][j] = false;
            }
        }
        if (result == null) result = new HashSet<String>();
        result.clear();

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                visited[i][j] = true;
                dfs(i, j, trie.root, "");
                visited[i][j] = false;
            }
        }
        return result;
    }

    private void dfs(int r, int c, Node curNode, String str) {
        int cha = board.getLetter(r, c) - 'A';
        if (curNode == null || curNode.next[cha] == null)
            return;
        str += board.getLetter(r, c);
        if (curNode.next[cha].isTail) {
            String tmp = str.replace("Q", "QU");
            if (this.scoreOf(tmp) > 0)
                this.result.add(tmp);
        }
        for (int i = 0; i < dir_r.length; i++) {
            int nr = r + dir_r[i];
            int nc = c + dir_c[i];
            // out of bounds
            if (nr < 0 || nr >= row || nc < 0 || nc >= col) continue;
            // have already visited this node
            if (visited[nr][nc] = true) continue;
            visited[nr][nc] = true;
            dfs(nr, nc, curNode.next[cha], str);
            visited[nr][nc] = false;
        }
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!dict.contains(word)) return 0;
        int len = word.length();
        if (len <= 2) return 0;
        else if (len <= 4) return 1;
        else if (len == 5) return 2;
        else if (len == 6) return 3;
        else if (len == 7) return 5;
        return 11;
    }
}
