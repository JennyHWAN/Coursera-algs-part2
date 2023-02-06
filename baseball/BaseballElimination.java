//package baseball;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// import java.awt.event.WindowFocusListener;
// import java.security.cert.TrustAnchor;
import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.concurrent.Flow;

public class BaseballElimination {
    private FlowNetwork network;
    private final In file;
    private String[][] matrix; // first four lines: team name, wins, loss, left
    private String[][] g; // remaining games between each other
    private int total;
    private int num;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        this.file = new In(filename);
        this.num = numberOfTeams();
        matrix = matrix();
        this.total = num + 1 + (num - 1) * (num - 2) / 2;
    }

    // number of teams
    public int numberOfTeams() {
        return file.readInt();
    }

    private String[][] matrix() {
//        num = numberOfTeams();
        this.matrix = new String[num][4];
        this.g = new String[num][num];
        String first = file.readLine();

        for (int i = 0; i < num; i++) {
            String line = file.readLine();
            for (int j = 0; j < num + 4; j++) {
                if (j < 4) {
                    matrix[i][j] = line.split("\\s++")[j];
                }
                else {
                    g[i][j - 4] = line.split("\\s++")[j];
                }
            }
        }
        return matrix;
    }

    // all teams
    public Iterable<String> teams() {
//        ArrayList<String> result = new ArrayList<>();
//        String first = file.readLine();
//        while (file.hasNextLine()) {
//            result.add(file.readLine().split(" ")[0]);
//        }
//        return result;
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            result.add(matrix[i][0]);
        }
        // check

        return result;
    }

    // number of wins for given team
    public int wins(String team) {
        for (int i = 0; i < num; i++) {
            if (matrix[i][0].equals(team)) {
                return Integer.parseInt(matrix[i][1]);
            }
        }
        throw new IllegalArgumentException("Invalid team");
    }

    // number of losses for given team
    public int losses(String team) {
        for (int i = 0; i < num; i++) {
            if (matrix[i][0].equals(team)) {
                return Integer.parseInt(matrix[i][2]);
            }
        }
        throw new IllegalArgumentException("Invalid team");
    }

    // number of remaining games for given team
    public int remaining(String team) {
        for (int i = 0; i < num; i++) {
            if (matrix[i][0].equals(team)) {
                return Integer.parseInt(matrix[i][3]);
            }
        }
        throw new IllegalArgumentException("Invalid team");
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        int idxTeam1 = -1, idxTeam2 = -1;
        for (int i = 0; i < num; i++) {
            if (matrix[i][0].equals(team1)) {
                idxTeam1 = i;
            }
            if (matrix[i][0].equals(team2)) {
                idxTeam2 = i;
            }
        }
        if (idxTeam1 != -1 && idxTeam2 != -1) {
            return Integer.parseInt(g[idxTeam1][idxTeam2]);
        }
        else
            throw new IllegalArgumentException("Invalid team");
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        // Trivial elimination
        int w0 = Integer.MAX_VALUE;
        int w = -1, r = -1;
//        ArrayList<Integer> wins = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            if (matrix[i][0].equals(team)) {
                w = Integer.parseInt(matrix[i][1]);
                r = Integer.parseInt(matrix[i][3]);
            } else {
                w0 = Math.min(w0, Integer.parseInt(matrix[i][1]));
//                wins.add(Integer.parseInt(matrix[i][1]));
            }
        }
        if (w == -1 || r == -1) throw new IllegalArgumentException("Invalid team");
        if (w + r < w0) return true;

        // Nontrivial elimination
//        int num = numberOfTeams();
//        total = num + 1 + (num - 1) * (num - 2) / 2; // num + 2 + (num - 1)C2 (except the input team)
//        // = num + 2 + (num - 1) * (num - 2) / 2
//        this.network = new FlowNetwork(total); // create the default network
//        int k = 1; // index note for the first vertex in the first layer
//        int i1 = -1;
//        int tv = 1 + (num - 1) * (num - 2) / 2; // first index of team vertices (second layer)
//        for (int i = 0; i < num; i++) {
//            if (matrix[i][0].equals(team)) continue;
//            i1++;
//            for (int j = i + 1; j < num; j++) {
//                int i2 = 0;
//                if (matrix[j][0].equals(team)) continue;
//                i2++;
//                int cap = Integer.parseInt(g[i][j]);
//                FlowEdge flow = new FlowEdge(0, k, cap); // add the first layer connection
//                network.addEdge(flow);
//                FlowEdge nextFlow = new FlowEdge(k, tv + i1, Integer.MAX_VALUE);
//                FlowEdge nextFlow1 = new FlowEdge(k, tv + i2, Integer.MAX_VALUE);
//                network.addEdge(nextFlow);
//                network.addEdge(nextFlow1);
//                k++;
//            }
//
//            // add the edges from team vertices to t
//            for (int j = 0; j < num - 1; j++) {
//                FlowEdge toFinal = new FlowEdge(k + j, total - 1, w + r - wins.get(j));
//                network.addEdge(toFinal);
//            }
//        }
        return certificateOfElimination(team) != null;
    }

    // create the initial network
    private FlowNetwork create(String team) {
        int w = -1, r = -1;
        ArrayList<Integer> wins = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            if (matrix[i][0].equals(team)) {
                w = Integer.parseInt(matrix[i][1]);
                r = Integer.parseInt(matrix[i][3]);
            } else {
                wins.add(Integer.parseInt(matrix[i][1]));
            }
        }

//        total = num + 1 + (num - 1) * (num - 2) / 2; // num + 2 + (num - 1)C2 (except the input team)
        // = num + 2 + (num - 1) * (num - 2) / 2
//        StdOut.println(total);
        this.network = new FlowNetwork(total); // create the default network
        int k = 1; // index note for the first vertex in the first layer
        int i1 = -1;
        int tv = 1 + (num - 1) * (num - 2) / 2; // first index of team vertices (second layer)
        for (int i = 0; i < num; i++) {
            if (matrix[i][0].equals(team)) continue;
            i1++;
            int i2 = i - 1;
            for (int j = i + 1; j < num; j++) {
                if (matrix[j][0].equals(team)) continue;
                i2++;
                double cap = Double.parseDouble(g[i][j]);
                FlowEdge flow = new FlowEdge(0, k, cap); // add the first layer connection
                network.addEdge(flow);
                FlowEdge nextFlow = new FlowEdge(k, tv + i1, Integer.MAX_VALUE);
//                StdOut.println(nextFlow);
                FlowEdge nextFlow1 = new FlowEdge(k, tv + i2, Integer.MAX_VALUE);
//                StdOut.println(nextFlow1);
                network.addEdge(nextFlow);
                network.addEdge(nextFlow1);
                k++;
//                if (k > (num - 1) * (num - 2) / 2) break;
            }

            // add the edges from team vertices to t
        }
        // add the edges from team vertices to t
        for (int j = 0; j < num - 1; j++) {
            int jwin = wins.get(j);
            if (w + r < jwin) return null;
            FlowEdge toFinal = new FlowEdge(k + j, total - 1, w + r - jwin);
            network.addEdge(toFinal);
        }
        return network;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        // when the input team is not eliminated
        int potential = -1;
        network = create(team);
//        StdOut.println(network.V());
//        StdOut.println(total - 1);
        if (network == null) throw new IllegalArgumentException("Null team");
        FordFulkerson maxFlow = new FordFulkerson(network, 0, total - 1);
        ArrayList<String> teams = new ArrayList<>();
        for (FlowEdge e: network.edges()) {
            // one of the edges pointing to t is full
            if (e.to() == total - 1) {
                if (maxFlow.value() == (e.residualCapacityTo(total - 1))) {
                    potential = e.from();
                    break;
                }
            }
        }

        ArrayList<Double> inFlow = new ArrayList<>(num); // flow edge from first layer to the second
        ArrayList<Double> outFlow = new ArrayList<>(num); // flow edge from second layer to the last
        for (FlowEdge e : network.edges()) {
            int count = 0;

            if (potential != -1 && e.to() == potential && e.residualCapacityTo(potential) != 0) {
                count++;
            }
            if (e.to() == total - 1) {
                int fromTeam = e.from();
                double curr = 0;
                outFlow.add(e.residualCapacityTo(total - 1));
                for (FlowEdge e1: network.edges()) {
                    if (e1.to() == fromTeam) curr += e1.residualCapacityTo(fromTeam);
                }
                inFlow.add(curr);
            }
            if (count == 1) return null;
        }
        ArrayList<Integer> teamIdx = new ArrayList<>(num - 1);
        for (int i = 0; i < num; i++) {
            if (!matrix[i][0].equals(team)) teamIdx.add(i);
        }

        int curr = 0;
        for (int i = 0; i < inFlow.size(); i++) {
            curr += inFlow.get(i);
            int sub = 0;
            for (int j = i + 1; j < inFlow.size(); j++) {
                sub += outFlow.get(j);
            }
            if (curr + sub == maxFlow.value()) {
                teams.add(matrix[teamIdx.get(i)][0]);
            }
        }
        return teams;
    }
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
//        StdOut.println(division);
//        StdOut.println(division.teams());
//        StdOut.println(division.network);
        for (String team : division.teams()) {
//            StdOut.println(team);
            if (division.isEliminated(team)) {
                StdOut.println(division.network);
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
//        StdOut.println("what's going on");
//        division.matrix();
//        StdOut.println(division.matrix);

    }
}
