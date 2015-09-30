/*
 * Water.java
 *
 * $Id: Water.java,v 1.6 2013/12/06 01:27:52 ghh8942 Exp ghh8942 $
 *
 * $Log: Water.java,v $
 * Revision 1.6  2013/12/06 01:27:52  ghh8942
 * Updated for third project.
 *
 * Revision 1.5  2013/11/18 19:41:25  ghh8942
 * Added comments.
 *
 * Revision 1.4  2013/11/18 19:08:51  ghh8942
 * cleaned up output.
 *
 * Revision 1.3  2013/11/18 19:06:23  ghh8942
 * Updated program. Appears to run correctly.
 *
 * Revision 1.2  2013/11/18 18:41:57  ghh8942
 * Program compiles. Implemented for Activity 2.
 * Saving before testing.
 *
 * Revision 1.1  2013/11/18 17:52:57  ghh8942
 * Initial revision
 *
 *
 */

/**
 * Water puzzle class.
 *
 * @author: Gregory Hoople
 */

import java.util.ArrayList;
import java.util.HashSet;

public class Water implements Puzzle<ArrayList<Integer>>{

    private int goal; //Goal volume
    private ArrayList<Integer> buckets; //Bucket Capacities
    private ArrayList<Integer> start; //Starting State
    private HashSet<ArrayList<Integer>> tried 
        = new HashSet<ArrayList<Integer>>(); //Set to prevent doubles

    /*
     * Water Class Constructor
     * 'g' - The Goal Volume
     * 'b' - The Capacity of the Buckets
     */
    public Water(int g, ArrayList<Integer> b){
        goal = g;
        buckets = b;
        start = new ArrayList<Integer>();
        for (int i = 0; i < b.size(); i++) {
            start.add(0);
        }
    } //Water

    //Get the goal config for this puzzle.
    public boolean isGoal(ArrayList<Integer> check){
        for (int i = 0; i < check.size(); i++) {
            if (check.get(i) == goal) {
                return true;
            }
        }
        return false;
    }//isGoal

    //For an incoming config, generate and return all
    //direct neighbors to this config.
    public ArrayList<ArrayList<Integer>> getNeighbors(
        ArrayList<Integer> config){

        //Set up the return value;
        ArrayList<ArrayList<Integer>> toReturn = 
            new ArrayList<ArrayList<Integer>>();

        ArrayList<Integer> nextNeighbor;

        //Can fill each bucket
        for (int i = 0; i < config.size(); i++) {

            //Set up nextNeighbor to be at capacity
            nextNeighbor = new ArrayList<Integer>(config);
            nextNeighbor.set(i, buckets.get(i));

            //Check if done before.
            if (!tried.contains(nextNeighbor)) {
                tried.add(nextNeighbor);
                toReturn.add(nextNeighbor);
            }
        }
        
        //Can empty
        for (int i = 0; i < config.size(); i++) {

            //Set up nextNeighbor to be at capacity
            nextNeighbor = new ArrayList<Integer>(config);
            nextNeighbor.set(i, 0);

            //Check if done before.
            if (!tried.contains(nextNeighbor)) {
                tried.add(nextNeighbor);
                toReturn.add(nextNeighbor);
            }
        }

        //Can pour into any other bucket
        //This first for-loop keeps track of the bucket
        //being poured (f)rom.
        for (int f = 0; f < config.size(); f++) {

            //This second for-loop keeps track of the
            //bucket being poured (t)o.
            for (int t = 0; t < config.size(); t++) {
                if (t == f) { continue; }
                //Set up nextNeighbor to be at capacity
                nextNeighbor = new ArrayList<Integer>(config);

                //Current state of the bucket being poured (f)rom.
                int from = config.get(f);
                //Current state of the bucket being poured (t)o.
                int to = config.get(t);
                //Current (Cap)acity of the bucket being poured (t)o.
                int tCap = buckets.get(t);

                //The math to pour between the values.
                if (from > tCap - to) { //If there's not enough room
                    from = from - (tCap - to);
                    to = tCap;
                } else { //If there's enough room.
                    to += from;
                    from = 0;
                }

                //Set their states
                nextNeighbor.set(f, from);
                nextNeighbor.set(t, to);

                //Check if done before.
                if (!tried.contains(nextNeighbor)) {
                    tried.add(nextNeighbor);
                    toReturn.add(nextNeighbor);
                }
            }//For
        }//For

        return toReturn;
    }//getNeighbors

    //Get the starting config for this puzzle
    public ArrayList<Integer> getStart(){
        return start;
    }

    public static void main (String args[]){
        //Start off with error checks
        if (args.length == 0) {
            //If there are not enough arguments
            //we alert the user and exit.
            System.err.println("Usage: java Water amount jug1 jug2 ...");
            System.exit(0);
        }

        int iG = 0; //Incoming Goal
        ArrayList<Integer> iB = new ArrayList<Integer>(); //Incoming Buckets
        try {
            iG = Integer.parseInt(args[0]);
            for (int i = 1; i < args.length; i++) {
                 iB.add(Integer.parseInt(args[i]));
            }
        } catch (NumberFormatException e) {
            //If the user didn't give us 
            //numbers we alert the user and exit.
            System.err.println("Usage: java Water amount jug1 jug2 ...");
            System.exit(0);
        }

        //After we've gotten through the error checks
        //We create the Water object
        Water myWater = new Water(iG, iB);

        //Calculate the solution from the solver
        Solver<ArrayList<Integer>> mySolver =
            new Solver<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> Solution =
            mySolver.SolveBFS(myWater);

        //Print out the results
        if (Solution.size() == 0) {
            System.out.println("No solution.");
        } else {
            //Print all the steps
            for (int i = 0; i < Solution.size(); i++) {
                //Print Step
                System.out.print("Step " + i + ":");
                //Print all the states in that step
                for (int b = 0; b < Solution.get(i).size(); b++) {
                    System.out.print(" " + Solution.get(i).get(b));
                }
                System.out.println();
            }
        }
    }

} //Water
