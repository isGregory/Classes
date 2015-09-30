/*
 * Clock.java
 *
 * $Id: Clock.java,v 1.9 2013/12/06 01:27:35 ghh8942 Exp ghh8942 $
 *
 * $Log: Clock.java,v $
 * Revision 1.9  2013/12/06 01:27:35  ghh8942
 * Updated for third project.
 *
 * Revision 1.8  2013/11/18 19:41:13  ghh8942
 * Added comments.
 *
 * Revision 1.7  2013/11/18 19:05:49  ghh8942
 * Updated clock to match new requirements of Activity 2
 *
 * Revision 1.6  2013/11/04 21:15:12  ghh8942
 * Added comments and privated variables.
 *
 * Revision 1.5  2013/11/04 21:03:57  ghh8942
 * Added the ability to keep track of what configurations have been tried.
 *
 * Revision 1.4  2013/11/04 20:42:18  ghh8942
 * Fixed inputs
 *
 * Revision 1.3  2013/11/04 20:19:25  ghh8942
 * Compiles and seems to run
 *
 * Revision 1.2  2013/11/04 19:58:09  ghh8942
 * Implemented methods of clock.
 *
 * Revision 1.1  2013/11/04 19:11:08  ghh8942
 * Initial revision
 *
 *
 */

/**
 * Clock class.
 *
 * @author: Gregory Hoople
 */

import java.util.ArrayList;
import java.util.HashSet;

public class Clock implements Puzzle<ArrayList<Integer>>{

    private int Hours;
    private int Goal;
    private ArrayList<Integer> start;
    private HashSet<ArrayList<Integer>> tried = new HashSet<ArrayList<Integer>>();

    /*
     * Clock constructor
     * 'h' - Hours on the Clock
     * 'g' - Goal Time
     * 's' - Start Time
     */
    public Clock(int h, int s, int g){
        Hours = h;
        start = new ArrayList<Integer>();
        start.add(s);
        Goal = g;
    } //Clock

    public static void main (String args[]){
        if (args.length != 3) {
            //If there are not enough arguments
            //we alert the user and exit.
            System.err.println("Usage: java Clock hours start goal");
            System.exit(0);
        }

        int iH = 0; //Incoming Hours
        int iS = 0; //Incoming Start
        int iG = 0; //Incoming Goal
        try {
            iH = Integer.parseInt(args[0]);
            iS = Integer.parseInt(args[1]);
            iG = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            //If the user didn't give us 
            //numbers we alert the user and exit.
            System.err.println("Usage: java Clock hours start goal");
            System.exit(0);
        }

        //After we've gotten through the error checks
        //We create the clock
        Clock myClock = new Clock(iH, iS, iG);

        //Calculate the solution from the solver
        Solver<ArrayList<Integer>> mySolver =
            new Solver<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> Solution =
            mySolver.SolveBFS(myClock);

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

    //Get the goal config for this puzzle.
    public boolean isGoal(ArrayList<Integer> check){
        for (int i = 0; i < check.size(); i++) {
            if (check.get(i) == Goal) {
                return true;
            }
        }
        return false;
    }//isGoal

    //For an incoming config, generate and return all
    //direct neighbors to this config.
    public ArrayList<ArrayList<Integer>> getNeighbors(ArrayList<Integer> config){
        ArrayList<ArrayList<Integer>> toReturn 
            = new ArrayList<ArrayList<Integer>>();

        //The clock can go forward or back an hour
        //So we add the possibilities to the return Arraylist
        int forward = config.get(0) + 1;
        int back = config.get(0) - 1;

        //Check if the possibilities stretch beyond
        //the domain of the clock
        if (forward > Hours) {
            forward = forward % Hours;
        }

        if (back <= 0) {
            back += Hours;
        }

        //Set up the ArrayList structures
        ArrayList<Integer> fwd = new ArrayList<Integer>();
        fwd.add(forward);
        ArrayList<Integer> bk = new ArrayList<Integer>();
        bk.add(back);

        //Check if these options have already been sent out or not
        //this allows us to give a finite number of options and thus
        //limits the solver to know when to quit. If we haven't tried
        //these configurations yet, we add them to the tried list and
        //add them to the container that's about to be returned
        if (!tried.contains(fwd)) {
            tried.add(fwd);
            toReturn.add(fwd);
        }
        if (!tried.contains(bk)) {
            tried.add(bk);
            toReturn.add(bk);
        }

        return toReturn;
    }

    //Get the starting config for this puzzle
    public ArrayList<Integer> getStart(){
        return start;
    }

} //Clock
