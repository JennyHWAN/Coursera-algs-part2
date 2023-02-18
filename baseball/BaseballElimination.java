//package baseball;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BaseballElimination {
    private HashMap<String, Integer> teams; // store teams' name and index
    private String[] rteams;
    private int[] wins, losses, remain;
    private int[][] g;
    private int numberOfTeams;
    private int maxWins;
    private String maxTeam;
    private int prev, third;
    private FordFulkerson maxflow;

    public BaseballElimination(String filename) {
        // setup the following variables: numberOfTeams, teams, rteams,
        // wins, losses, remain, g, maxWins
        In file = new In(filename);
        numberOfTeams = file.readInt();
        teams = new HashMap<String, Integer>(numberOfTeams);
        rteams = new String[numberOfTeams]; // teams' name
        wins = new int[numberOfTeams];
        losses = new int[numberOfTeams];
        remain = new int[numberOfTeams];
        g = new int[numberOfTeams][numberOfTeams];
        maxWins = -1;
        prev = -1;
        for (int i = 0; i < numberOfTeams; i++) {
            String cur = file.readString();
            teams.put(cur, i);
            wins[i] = file.readInt();
            rteams[i] = cur;
            if (maxWins < wins[i]) maxTeam = cur;
            maxWins = Math.max(maxWins, wins[i]);
            losses[i] = file.readInt();
            remain[i] = file.readInt();
            for (int j = 0; j < numberOfTeams; j++) {
                g[i][j] = file.readInt();
            }
        }
    }
    public int numberOfTeams() {
        return numberOfTeams;
    }
    public Iterable<String> teams() {
        return teams.keySet();
    }
    public int wins(String team) {
        if (!teams.containsKey(team)) throw new IllegalArgumentException("No such team exist");
        return wins[teams.get(team)];
    }
    public int losses(String team) {
        if (!teams.containsKey(team)) throw new IllegalArgumentException("No such team exist");
        return losses[teams.get(team)];
    }
    public int remaining(String team) {
        if (!teams.containsKey(team)) throw new IllegalArgumentException("No such team exist");
        return remain[teams.get(team)];
    }
    public int against(String team1, String team2) {
        if (!teams.containsKey(team1) || !teams.containsKey(team2))
            throw new IllegalArgumentException("No such team exist");
        return g[teams.get(team1)][teams.get(team2)];
    }
    public boolean isEliminated(String team) {
        if (!teams.containsKey(team))
            throw new IllegalArgumentException("No such team exist");
        int index = teams.get(team);
        // trivial case
        if (maxWins > wins[index] + remain[index]) return true;
        int comp = 0;
        int total = 0;
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == index) continue;
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (j == index) continue;
                if (g[i][j] > 0) {
                    total += g[i][j];
                    comp++;
                }
            }
        }
        if (prev == index) {
//            StdOut.println(maxflow.value() + " " + total);
            if ((int)maxflow.value() < total) return true;
            else return false;
        }
        prev = index;

        // number of vertex
        int v = 1 + comp + (numberOfTeams - 1) + 1;
        // setup source vertex
        int s = 0;
        // setup sink vertex
        int t = v - 1;
        // create network
        FlowNetwork net = new FlowNetwork(v);
        // add edges to construct the flow network
        int counter = 0;
        third = comp + 1; // first vertex of the third layer
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == index) continue;
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (j == index) continue;
                if (g[i][j] > 0) {
                    counter++;
                    net.addEdge(new FlowEdge(s, counter, g[i][j]));
                    int t1, t2;
                    // still the index of the third layer, change the index of the second layer
                    if (i < index) t1 = third + i;
                    else t1 = third + i - 1;
                    // change both layers' index
                    if (j < index) t2 = third + j;
                    else t2 = third + j - 1;
                    net.addEdge(new FlowEdge(counter, t1, Double.MAX_VALUE));
                    net.addEdge(new FlowEdge(counter, t2, Double.MAX_VALUE));
                }
            }
        }

        // add edges for last layer of the network
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == index) continue;
            int from;
            if (i < index) from = third + i;
            else from = third + i - 1;
            int weight = wins[index] + remain[index] - wins[i];
            if (weight > 0)
                net.addEdge(new FlowEdge(from, t, weight));
        }
        maxflow = new FordFulkerson(net, s, t);
        if ((int)maxflow.value() < total) return true;
        return false;
    }
    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!teams.containsKey(team))
            throw new IllegalArgumentException("No such team exists");
        if (!isEliminated(team)) return null;
        int index = teams.get(team);
        Set<String> result = new HashSet<>();

        // trivial case
        if (maxWins > wins[index] + remain[index]) {
            result.add(maxTeam);
            return result;
        }

        // non-trivial case
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == index) continue;
            int vertex = 0;
            if (i < index) vertex = third + i;
            else vertex = third + i - 1;
            if (maxflow.inCut(vertex)) {
                result.add(rteams[i]);
            }
        }
        return result;
    }
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
