import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Stack;

public class Assign1 {

    public static final int H_ZERO = 0;
    public static final int H_DISPLACE = 1;
    public static final int H_MANHATTAN = 2;
    public static final int H_GREATER = 4;
    public static final int _N = 3;

    public static final int hArray[] = { H_ZERO, H_DISPLACE, H_MANHATTAN, H_GREATER };

    public static void main(String[] args) {

        ArrayList<Integer> final_pos = new ArrayList<>();
        final_pos.add(1);
        final_pos.add(2);
        final_pos.add(3);
        final_pos.add(4);
        final_pos.add(5);
        final_pos.add(6);
        final_pos.add(7);
        final_pos.add(8);
        final_pos.add(0);

        ArrayList<Integer> init_pos = new ArrayList<>();
        init_pos.add(1);
        init_pos.add(3);
        init_pos.add(6);
        init_pos.add(2);
        init_pos.add(5);
        init_pos.add(0);
        init_pos.add(8);
        init_pos.add(7);
        init_pos.add(4);

        ArrayList<HeuristicsTableRow> tableRows = new ArrayList<>();

        for (int hLoop = 0; hLoop < hArray.length; hLoop++) {
            Node goal_node = new Node();
            goal_node.points = final_pos;

            Node init_node = new Node();
            init_node.points = init_pos;
            init_node.gn_val = 0;
            init_node.hn_val = gethValue(init_node, goal_node, hArray[hLoop]);
            init_node.fn_val = init_node.hn_val + init_node.gn_val;

            HashSet<ArrayList<Integer>> closed_list = new HashSet<>();
            PriorityQueue<Node> open_list = new PriorityQueue<>();
            open_list.add(init_node);

            int countExplored = 0;
            boolean reachedGoal = false;

            long startTime = System.currentTimeMillis();

            while (!open_list.isEmpty()) {

                Node curr_node = open_list.poll();

                if (closed_list.contains(curr_node.points))
                    continue;

                closed_list.add(curr_node.points);
                countExplored++;

                Node children[] = { moveLeft(curr_node), moveRight(curr_node), moveDown(curr_node), moveUp(curr_node) };

                for (int i = 0; i < children.length; ++i) {

                    if (children[i] != null && children[i].points.equals(goal_node.points)) {
                        goal_node.parent = curr_node;
                        goal_node.gn_val = curr_node.gn_val + 1;
                        reachedGoal = true;
                        break;
                    }

                    if (children[i] != null && !closed_list.contains(children[i].points)) {
                        children[i].gn_val = curr_node.gn_val + 1;
                        children[i].hn_val = gethValue(children[i], goal_node, hArray[hLoop]);
                        children[i].fn_val = children[i].gn_val + children[i].hn_val;
                        children[i].parent = curr_node;

                        open_list.add(children[i]);
                    }
                }

                if (reachedGoal)
                    break;
            }

            long endTime = System.currentTimeMillis();

            HeuristicsTableRow heuristicsTableRow = new HeuristicsTableRow();
            // heuristicsTableRow.optimalPath = goal_node.gn_val;
            heuristicsTableRow.optimalPathCost = -1;
            heuristicsTableRow.totStatesExplored = countExplored;
            heuristicsTableRow.totStatesOptimalPath = -1;
            heuristicsTableRow.totalTime = (endTime - startTime);
            heuristicsTableRow.type = hArray[hLoop];

            if (reachedGoal) {
                System.out.println("Success!!!\n");

                System.out.println("Start State \n");
                printNodeArr(init_node);

                System.out.println("Goal State \n");
                printNodeArr(goal_node);

                System.out.println("Total number of states explored = " + countExplored + "\n");

                Stack<Node> pathStck = new Stack<>();
                Node temp = goal_node;
                while (temp != null) {
                    pathStck.add(temp);
                    temp = temp.parent;
                }

                int stack_size = pathStck.size();
                System.out.println("Total number of states on optimal path = " + stack_size + "\n");
                heuristicsTableRow.totStatesOptimalPath = stack_size;
                heuristicsTableRow.optimalPathCost = goal_node.gn_val;

                while (!pathStck.isEmpty()) {
                    Node temp_vis = pathStck.pop();
                    printNodeArr(temp_vis);
                    System.out.println("  |    ");
                    System.out.println("  V    ");
                }
                System.out.println("Goal reached !!!!!!!");

                System.out.println("Optimal Cost of the path = " + goal_node.gn_val + "\n");

            } else {

                System.out.println("Fail!!!\n");

                System.out.println("Start State \n");
                printNodeArr(init_node);

                System.out.println("Goal State \n");
                printNodeArr(goal_node);

                System.out.println("Total number of states explored = " + countExplored + "\n");

            }

            tableRows.add(heuristicsTableRow);
        }

        System.out.println("\n------------------------------------------------------");
        System.out.println("Heuristics Table:");

        System.out.println(
                "Method \t\t Total States Explored \t\t Total States Optimal Path \t\t Optimal Path \t\t\t\t Optimal Cost \t Total Time(ms)");
        for (HeuristicsTableRow t : tableRows) {
            // NumberFormat.getNumberInstance(Locale.US).format(35634646)
            System.out.println(getHMethod(t.type) + "\t\t" + NumberFormat.getNumberInstance(Locale.UK).format(t.totStatesExplored) + "\t\t\t\t\t" + t.totStatesOptimalPath + "\t\t"
                    + "Check the output above for the optimal path" + "\t\t" + t.optimalPathCost + "\t\t" + t.totalTime);
        }

    }

    public static String getHMethod(int type) {
        if (type == H_ZERO)
            return "Zero     ";

        if (type == H_DISPLACE)
            return "Displacement";

        if (type == H_MANHATTAN)
            return "Manhattan";

        if (type == H_GREATER)
            return "h(n) > h*(n)";

        return "Invalid Type";
    }

    public static void printNodeArr(Node node) {
        ArrayList<Integer> list = node.points;
        int n = list.size();

        for (int i = 0; i < n; ++i) {
            System.out.print(list.get(i) + " ");
            if ((i + 1) % _N == 0) {
                System.out.println();
            }
        }
    }

    public static Node moveRight(Node node) {

        int index = node.points.indexOf(0);
        int row_pos = index / _N;
        int col_pos = index % _N;

        if (col_pos == _N - 1) {
            return null;
        } else {
            ArrayList<Integer> new_list = (ArrayList<Integer>) node.points.clone();
            int temp = new_list.get(row_pos * 3 + (col_pos + 1));
            new_list.set(row_pos * 3 + (col_pos + 1), new_list.get(row_pos * 3 + (col_pos)));
            new_list.set(row_pos * 3 + (col_pos), temp);

            Node child = new Node();
            child.points = new_list;
            return child;
        }
    }

    public static Node moveLeft(Node node) {

        int index = node.points.indexOf(0);
        int row_pos = index / _N;
        int col_pos = index % _N;

        if (col_pos == 0) {
            return null;
        } else {
            ArrayList<Integer> new_list = (ArrayList<Integer>) node.points.clone();
            int temp = new_list.get(row_pos * 3 + (col_pos - 1));
            new_list.set(row_pos * 3 + (col_pos - 1), new_list.get(row_pos * 3 + (col_pos)));
            new_list.set(row_pos * 3 + (col_pos), temp);

            Node child = new Node();
            child.points = new_list;
            return child;
        }
    }

    public static Node moveDown(Node node) {

        int index = node.points.indexOf(0);
        int row_pos = index / _N;
        int col_pos = index % _N;

        if (row_pos == _N - 1) {
            return null;
        } else {
            ArrayList<Integer> new_list = (ArrayList<Integer>) node.points.clone();
            int temp = new_list.get(row_pos * 3 + 3 + (col_pos));
            new_list.set(row_pos * 3 + 3 + (col_pos), new_list.get(row_pos * 3 + (col_pos)));
            new_list.set(row_pos * 3 + (col_pos), temp);

            Node child = new Node();
            child.points = new_list;
            return child;
        }
    }

    public static Node moveUp(Node node) {

        int index = node.points.indexOf(0);
        int row_pos = index / _N;
        int col_pos = index % _N;

        if (row_pos == 0) {
            return null;
        } else {
            ArrayList<Integer> new_list = (ArrayList<Integer>) node.points.clone();
            int temp = new_list.get(row_pos * 3 - 3 + (col_pos));
            new_list.set(row_pos * 3 - 3 + (col_pos), new_list.get(row_pos * 3 + (col_pos)));
            new_list.set(row_pos * 3 + (col_pos), temp);

            Node child = new Node();
            child.points = new_list;
            return child;
        }
    }

    public static int gethValue(Node curr, Node goal, int type) {
        int n = goal.points.size();
        int value = 0;
        switch (type) {
        case H_ZERO:
            return 0;
        case H_DISPLACE:

            int count = 0;
            for (int i = 0; i < n; ++i) {
                if (curr.points.get(i) != goal.points.get(i)) {
                    count++;
                }
            }

            return count;
        case H_MANHATTAN:

            for (int i = 0; i < n; ++i) {
                int row_pos = i / _N;
                int col_pos = i % _N;
                Integer curr_point = curr.points.get(i);
                int goal_index = goal.points.indexOf(curr_point);

                int goal_row_pos = goal_index / _N;
                int goal_col_pos = goal_index % _N;
                value += (Math.abs(goal_col_pos - col_pos) + Math.abs(goal_row_pos - row_pos));

            }
            return value;

        case H_GREATER:

            for (int i = 0; i < n; ++i) {
                int row_pos = i / _N;
                int col_pos = i % _N;
                Integer curr_point = curr.points.get(i);
                int goal_index = goal.points.indexOf(curr_point);

                int goal_row_pos = goal_index / _N;
                int goal_col_pos = goal_index % _N;
                value += (int) (Math.pow(Math.abs(goal_col_pos - col_pos), 2)
                        + Math.pow(Math.abs(goal_row_pos - row_pos), 2) + 1);

            }
            return value;

        }
        return 0;
    }

}

class Node implements Comparable<Node> {

    ArrayList<Integer> points = new ArrayList<>();
    int fn_val;
    int gn_val;
    int hn_val;
    Node parent;

    @Override
    public int compareTo(Node o) {
        return Integer.compare(this.fn_val, o.fn_val);
    }

}

class HeuristicsTableRow {
    int type;
    int totStatesExplored;
    int totStatesOptimalPath;
    ArrayList<Node> optimalPath;
    int optimalPathCost;
    double totalTime;
}