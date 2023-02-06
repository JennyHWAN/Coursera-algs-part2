//package baseball;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class test {
    private In file;
    private String[][] matrix;

    public test(String filename) {
        this.file = new In(filename);
    }

    public void matrix() {
        int first = file.readInt();
        String second = file.readLine();
//        StdOut.print(first);
        this.matrix = new String[first][first + 4];
//        int count = 0;
        for (int i = 0; i < first; i++) {
            String line = file.readLine();
//            count++;
//            StdOut.println(line);
            for (int j = 0; j < first + 4; j++) {
                matrix[i][j] = line.split("\\s++")[j];
//                StdOut.println(line.split("\\s++")[j]);
            }
        }
//        StdOut.println(count);
    }
    public static void main(String[] args) {
        test file = new test(args[0]);
        file.matrix();
        StdOut.println(file.matrix[0].length);
        for (int i = 0; i < file.matrix.length; i++) {
            for (int j = 0; j < file.matrix[0].length; j++) {
                StdOut.print(file.matrix[i][j] + ' ');
            }
            StdOut.println(" ");
        }
    }
}
