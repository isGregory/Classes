/*
 * Puzzle.java
 *
 * $Id: Puzzle.java,v 1.4 2013/12/06 01:28:32 ghh8942 Exp ghh8942 $
 *
 * $Log: Puzzle.java,v $
 * Revision 1.4  2013/12/06 01:28:32  ghh8942
 * Set up to take generics. Altered getGoal to isGoal
 *
 * Revision 1.3  2013/11/18 18:00:35  ghh8942
 * Early update for activity 2.
 *
 * Revision 1.2  2013/11/04 19:58:48  ghh8942
 * Added import so file now compiles.
 *
 * Revision 1.1  2013/11/04 19:10:32  ghh8942
 * Initial revision
 *
 *
 */

/**
 * Puzzle class.
 *
 * @author: Gregory Hoople
 */

import java.util.ArrayList;

public interface Puzzle<E>{

    //check if config is goal for this puzzle.
    public boolean isGoal(E x);

    //For an incoming config, generate and return all
    //direct neighbors to this config.
    public ArrayList<E> getNeighbors(E config);

    //Get the starting config for this puzzle
    public E getStart();

} //Puzzle
