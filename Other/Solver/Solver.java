/*
 * Solver.java
 *
 * $Id: Solver.java,v 1.8 2013/12/06 01:28:06 ghh8942 Exp ghh8942 $
 *
 * $Log: Solver.java,v $
 * Revision 1.8  2013/12/06 01:28:06  ghh8942
 * Set it up to take generics
 *
 * Revision 1.7  2013/11/18 18:41:29  ghh8942
 * Updated for Activity 2. Compiles. Saving before testing.
 *
 * Revision 1.6  2013/11/04 21:11:29  ghh8942
 * Made method static.
 *
 * Revision 1.5  2013/11/04 20:44:11  ghh8942
 * Removed doubles check.
 *
 * Revision 1.4  2013/11/04 20:42:37  ghh8942
 * Fixed doubles, may change.
 *
 * Revision 1.3  2013/11/04 20:19:40  ghh8942
 * Compiles and seems to run
 *
 * Revision 1.2  2013/11/04 19:59:17  ghh8942
 * Wrote up basic solver
 *
 * Revision 1.1  2013/11/04 19:11:29  ghh8942
 * Initial revision
 *
 *
 */

/**
 * Solver class.
 *
 * @author: Gregory Hoople
 */

import java.util.ArrayList;

public class Solver<E>{
    
    public Solver(){

    }

    public ArrayList<E> SolveBFS(Puzzle<E> toSolve){
        ArrayList<ArrayList<E>> myQueue 
            = new ArrayList<ArrayList<E>>();
        //Get the starting configuration
        E startConfig = toSolve.getStart();

        //Set up the current solution for the while loop
        ArrayList<E> current = new ArrayList<E>();
        current.add(startConfig);

        //Enqueue the starting configuration
        myQueue.add(current);

        //Set found to check if the start is the goal
        boolean found = toSolve.isGoal(startConfig);

        //While the queue is not empty and solution is not found
        while(myQueue.size() > 0 && !found){
            //Dequeue the front element and set to current
            current = myQueue.get(0);
            myQueue.remove(0);

            //go through each neighbor of the last element in current
            ArrayList<E> neighbors = toSolve.getNeighbors(
                current.get(current.size() - 1));
            for (int i = 0; i < neighbors.size(); i++) {
                //Set the next configuration to current and add neighbor
                ArrayList<E> nextConfig 
                    = new ArrayList<E>(current);

                nextConfig.add(neighbors.get(i));
                //If the neighbor is the goal return
                if (toSolve.isGoal(neighbors.get(i))) {
                    current = nextConfig;
                    found = true;
                    break;
                } else { //Otherwise enqueue the next configuration
                    myQueue.add(nextConfig);
                }
            }
        }//While

        if (found) {
            //Current is the solution
            return current;
        } else {
            //No Solution
            return new ArrayList<E>();
        }
        
    }//SolveBFS

} //Solver
