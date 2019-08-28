import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;
import java.util.*;

public class Assign4_SimulatedAnnealing {

    public static final int H_ZERO = 0;
    public static final int H_DISPLACE = 1;
    public static final int H_MANHATTAN = 2;
    public static final int H_GREATER = 4;
    public static final int H_CUSTOM = 5;
    public static final int _N = 3;
    
    public static double REDUCTION_FACTOR = 0.5; 
    public static final double THRESHOLD = 0.40; 

    public static final int hArray[] = { H_DISPLACE, H_MANHATTAN, H_CUSTOM};

    public static void main(String[] args)  throws FileNotFoundException , IOException{



        ArrayList<Integer> final_pos = new ArrayList<>();
        File input_file_start = new File("./start_state.txt");
        File input_file_goal = new File("./goal_state.txt");

        Scanner sc = new Scanner(input_file_goal);
        while(sc.hasNextLine()) {
            final_pos.add(sc.nextInt());
        }


        ArrayList<Integer> init_pos = new ArrayList<>();

        sc = new Scanner(input_file_start);

        while(sc.hasNextLine()) {
            init_pos.add(sc.nextInt());
        }

        ArrayList<HeuristicsTableRow> tableRows = new ArrayList<>();

        for (int hLoop = 0; hLoop < hArray.length; hLoop++) {
            double T = 373;
            double To = 373;
            
            Node goal_node = new Node();
            goal_node.points = final_pos;

            Node init_node = new Node();
            init_node.points = init_pos;
            init_node.gn_val = 0;
            init_node.hn_val = gethValue(init_node, goal_node, hArray[hLoop]);
            init_node.fn_val = init_node.hn_val + init_node.gn_val;

            Node curr_node = init_node ;
            int countExplored = 0;
            boolean reachedGoal = false;

            int curr_plateau_counter = 0 ;
            long startTime = System.currentTimeMillis();
            Node prev_node = null ;
            while (curr_node!=null) {

                if(curr_node.points.equals(goal_node.points)) {
                    reachedGoal = true ;
                    goal_node = curr_node;
                    break;
                }
                
                countExplored++;

                Node children[] = { moveLeft(curr_node), moveRight(curr_node), moveDown(curr_node), moveUp(curr_node) };

                Node temp = null;

                ArrayList<Node> valid_child = new ArrayList<>();

                for(Node c:children ) {
                        if(c!=null) {
                            valid_child.add(c);
                        }
                }

                while(!valid_child.isEmpty()) {
                        int rand = (int)(Math.random() * valid_child.size());
                        Node temp_child = valid_child.get(rand);

                        temp_child.hn_val = gethValue(temp_child, goal_node, hArray[hLoop]);
                        temp_child.gn_val = curr_node.gn_val+1;
                        temp_child.fn_val = temp_child.hn_val+ temp_child.gn_val;

                        if(curr_node.hn_val>temp_child.hn_val) {
                            temp = temp_child ;
                            temp_child.parent = curr_node;
                            break;    
                        } else {
                               if(getProb(temp_child.hn_val - curr_node.hn_val,T, THRESHOLD)) {
                                   temp = temp_child;
                                   temp_child.parent = curr_node;
                                   break;
                               } 
                        }

                        valid_child.remove(rand);
                }


               prev_node = curr_node ;
               curr_node = temp;
               T = REDUCTION_FACTOR * T;
            //    T = To * Math.exp(-REDUCTION_FACTOR * countExplored);
            //    T = REDUCTION_FACTOR * Math.exp(-T);
            //    REDUCTION_FACTOR = REDUCTION_FACTOR * REDUCTION_FACTOR; 
            }

            long endTime = System.currentTimeMillis();

            System.out.println(getHMethod(hArray[hLoop]));
            System.out.println("--------------------------");

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

                System.out.println("Fail to reach global maximum. Reached local maximum or got stuck in plateau.\n");

                System.out.println("Start State \n");
                printNodeArr(init_node);

                System.out.println("Goal State \n");
                printNodeArr(goal_node);

                System.out.println("Final State Reached: \n");
                printNodeArr(prev_node);

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

        FileWriter fw_output = new FileWriter("./output.txt");
        fw_output.write("------------------------------------------------------\n");
        fw_output.write("Heuristics Table:\n");

        fw_output.write(
                "Method \t\t Total States Explored \t\t Total States Optimal Path \t\t Optimal Path \t\t\t\t Optimal Cost \t Total Time(ms)\n");
        for (HeuristicsTableRow t : tableRows) {
            // NumberFormat.getNumberInstance(Locale.US).format(35634646)
            fw_output.write(getHMethod(t.type) + "\t\t" + NumberFormat.getNumberInstance(Locale.UK).format(t.totStatesExplored) + "\t\t\t\t\t" + t.totStatesOptimalPath + "\t\t"
                    + "Check the output above for the optimal path" + "\t\t" + t.optimalPathCost + "\t\t" + t.totalTime+"\n");
        }

        fw_output.close();
    }


    public static boolean getProb(int delta_h, double T, double threshold) {
            double boltzman_const = 101 ;
            double probality = Math.exp(-delta_h/(boltzman_const*T));
            return probality > threshold ;
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

        if (type == H_CUSTOM)
            return "3*h_dis - 2*h_man";
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
            ArrayList<Integer> new_list =  new ArrayList<>();
            new_list.addAll(node.points);
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
            ArrayList<Integer> new_list =  new ArrayList<>();
            new_list.addAll(node.points);
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
            ArrayList<Integer> new_list =  new ArrayList<>();
            new_list.addAll(node.points);
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
            ArrayList<Integer> new_list =  new ArrayList<>();
            new_list.addAll(node.points);
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
                if (/*(curr.points.get(i)!=0) &&*/curr.points.get(i) != goal.points.get(i)) {
                    count++;
                }
            }

            return count;
        case H_MANHATTAN:

            for (int i = 0; i < n; ++i) {

                // if(curr.points.get(i)==0){
                //     continue ;
                // }

                int row_pos = i / _N;
                int col_pos = i % _N;
                Integer curr_point = curr.points.get(i);
                int goal_index = goal.points.indexOf(curr_point);

                int goal_row_pos = goal_index / _N;
                int goal_col_pos = goal_index % _N;
                value += (Math.abs(goal_col_pos - col_pos) + Math.abs(goal_row_pos - row_pos));

            }
            return value;

        case H_CUSTOM: 
            return 3 * gethValue(curr, goal, H_DISPLACE) - 2 * gethValue(curr, goal, H_MANHATTAN);
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