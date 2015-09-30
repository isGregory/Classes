/*
 * ChessModel.java
 *
 * $Id: ChessModel.java,v 1.6 2013/12/10 23:18:03 ghh8942 Exp ghh8942 $
 *
 * $Log: ChessModel.java,v $
 * Revision 1.6  2013/12/10 23:18:03  ghh8942
 * Fixed the move count system.
 *
 * Revision 1.5  2013/12/09 20:58:37  ghh8942
 * Fixed the 'next' method.
 *
 * Revision 1.4  2013/12/09 20:38:59  ghh8942
 * Program Compiles.
 *
 * Revision 1.3  2013/12/09 20:33:21  ghh8942
 * Added methods to interact with gui
 *
 * Revision 1.2  2013/12/06 02:16:28  ghh8942
 * Working chess model.
 *
 * Revision 1.1  2013/12/06 01:29:14  ghh8942
 * Initial revision
 *
 *
 */

/**
 * ChessModel class.
 *
 * @author: Gregory Hoople
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;
import java.util.Observable;

public class ChessModel extends Observable
    implements Puzzle<ArrayList<Integer>>{

    private int Rows = 5;
    private int Cols = 5;
    private int moveCount;
    private ArrayList<Integer> undoStack; //undo stack of game
    private ArrayList<Integer> curBoard; //Current Board State
    private ArrayList<Integer> start; //Starting State
    private HashSet<ArrayList<Integer>> tried 
        = new HashSet<ArrayList<Integer>>(); //Set to prevent doubles

    /*
     * ChessModel Class Constructor
     * 'r' - The Rows
     * 'c' - The Cols
     * 's' - The starting state of the board
     */
    public ChessModel(int r, int c, ArrayList<Integer> s){
        Rows = r;
        Cols = c;
        start = s;
        curBoard = s;
        clearStack();
    } //ChessModel

    public void reset() {
        curBoard = start;
        moveCount = 0;

        setChanged();
	notifyObservers();
    }

    //Get the goal config for this puzzle.
    public boolean isGoal(ArrayList<Integer> config){
        int numUnits = 0;
        for (int i = 0; i < config.size(); i++) {
            if (config.get(i) != '.') {
                numUnits++;
            }
        }
        return numUnits == 1;
    }

    //Check if the game can be won
    public boolean isWinnable() {
        Solver<ArrayList<Integer>> mySolver =
            new Solver<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> Solution =
            mySolver.SolveBFS(this);

        //Reset 'tried' to none
        tried = new HashSet<ArrayList<Integer>>();

        if(Solution.size() == 0) {
            //No Solution
            return false;
        }
        return true;
    }

    //Check if the game has been won
    public boolean hasWon() {
        return isGoal(curBoard);
    }

    //Return moveCount
    public int getMoveCount() {
        return moveCount;
    }//getMoveCount

    //Returns the board's width
    public int getBoardWidth(){
        return Cols;
    }//getBoardWidth

    //Get the current board
    public ArrayList<Integer> getCurBoard() {
        return curBoard;
    }//getCurBoard

    //Move a unit on the board
    private ArrayList<Integer> moveUnit(int mSpot, int tSpot,
        ArrayList<Integer> config) {
        ArrayList<Integer> myConfig = new ArrayList<Integer>(config);
        int Unit = myConfig.get(mSpot);
        myConfig.set(tSpot, Unit);
        myConfig.set(mSpot, new Integer('.'));
        return myConfig;
    }//moveUnit

    //Get the moves of a unit.
    private ArrayList<Integer> getMoves(int mSpot,
        ArrayList<Integer> config) {
        char myType = (char)(int)config.get(mSpot);

        //Generate all possible moves
        ArrayList<Integer> myMoves = generateUnitMoves(myType, mSpot);

        //Remove rulebreaking placements
        myMoves = pruneIllegals(myMoves, config);
        return myMoves;
    }//getMoves

    //Check potential character moves (cm) against
    //the current configuration
    private ArrayList<Integer> pruneIllegals(ArrayList<Integer> cm,
        ArrayList<Integer> config) {
        //Remove out of bound spots
        for (int i = 0; i < cm.size(); i++) {
            if(cm.get(i) < 0) {
                cm.remove(i);
                i--;
            } else if (cm.get(i) >= config.size()) {
                cm.remove(i);
                i--;
            }
        }
        //Remove points that have no person there
        boolean occupied;
        for (int i = 0; i < cm.size(); i++) {
            if (config.get(cm.get(i)) == '.') {
                cm.remove(i);
                i--;
            }
        }

        return cm;
    }//prune Illegals

    //Generates all possible unit moves
    private ArrayList<Integer> generateUnitMoves(char myType, int mSpot) {
        //Keep track of character moves (cm)
        ArrayList<Integer> cm = new ArrayList<Integer>();
        int row = mSpot / Cols;
        int col = mSpot % Cols;

        //We only need to worry about overstepping the
        //range of colomns. Overstepping rows will
        //be pruned out in "pruneIllegals"
        if (myType == 'N') {
            //Get Knight moves
            if (col >= 1) {
                cm.add(new Integer(mSpot - Cols - Cols - 1));
                cm.add(new Integer(mSpot + Cols + Cols - 1));
            }
            if (col < Cols - 1) {
                cm.add(new Integer(mSpot - Cols - Cols + 1));
                cm.add(new Integer(mSpot + Cols + Cols + 1));
            }
            if (col >= 2) {
            cm.add(new Integer(mSpot - Cols - 2));
            cm.add(new Integer(mSpot + Cols - 2));
            }
            if (col < Cols - 1) {
                cm.add(new Integer(mSpot - Cols + 2));
                cm.add(new Integer(mSpot + Cols + 2));
            }
        } else if (myType == 'R') {
            //Rook Moves
            //Get moves along Rows
            for (int i = 0; i < Rows; i++) {
                if (i == row) { continue; }
                cm.add(new Integer((i * Cols) + col));
            }

            //Get moves along Cols
            for (int i = 0; i < Cols; i++) {
                if (i == col) { continue; }
                cm.add(new Integer((row * Cols) + i));
            }
        } else if (myType == 'B') {
            //Bishop Moves

            //Diagonal movement
            //East movements
            for(int i = 1; i < Cols - col; i++) {
                cm.add(new Integer(mSpot + (i*Cols) + i));
                cm.add(new Integer((mSpot - (i*Cols)) + i));
            }
            //West movements
            for(int i = 1; i <= col; i++) {
                cm.add(new Integer((mSpot + (i*Cols)) - i));
                cm.add(new Integer((mSpot - (i*Cols)) - i));
            }
        } else if (myType == 'P') {
            //Pawn Moves
            if (col >= 1) {
                cm.add(new Integer((mSpot - Cols) - 1));
            }
            if (col < Cols - 1) {
                cm.add(new Integer((mSpot - Cols) + 1));
            }
        } else if (myType == 'Q') {
            //Queen Moves

            //Get moves along Rows
            for (int i = 0; i < Rows; i++) {
                if (i == row) { continue; }
                cm.add(new Integer((i * Cols) + col));
            }

            //Get moves along Cols
            for (int i = 0; i < Cols; i++) {
                if (i == col) { continue; }
                cm.add(new Integer((row * Cols) + i));
            }

            //Diagonal movement
            //East movements
            for(int i = 1; i < Cols - col; i++) {
                cm.add(new Integer(mSpot + (i*Cols) + i));
                cm.add(new Integer((mSpot - (i*Cols)) + i));
            }
            //West movements
            for(int i = 1; i <= col; i++) {
                cm.add(new Integer((mSpot + (i*Cols)) - i));
                cm.add(new Integer((mSpot - (i*Cols)) - i));
            }
        } else if (myType == 'K') {
            //King Movement
            if (col >= 1) {
                cm.add(new Integer((mSpot - Cols) - 1));
                cm.add(new Integer(mSpot - 1));
                cm.add(new Integer((mSpot + Cols) - 1));
            }
            if (col < Cols - 1) {
                cm.add(new Integer((mSpot - Cols) + 1));
                cm.add(new Integer(mSpot + 1));
                cm.add(new Integer((mSpot + Cols) + 1));
            }
            cm.add(new Integer(mSpot - Cols));
            cm.add(new Integer(mSpot + Cols));
        }
        return cm;
    }//generateUnitMoves

    //For an incoming config, generate and return all
    //direct neighbors to this config.
    public ArrayList<ArrayList<Integer>> getNeighbors(
        ArrayList<Integer> config){

        //Set up the return value;
        ArrayList<ArrayList<Integer>> toReturn = 
            new ArrayList<ArrayList<Integer>>();

        ArrayList<Integer> nextNeighbor;

        //Go through all characters and build moves
        for (int i = 0; i < config.size(); i++) {
            if(config.get(i) == '.'){ continue; }
            //Get legal moves
            ArrayList<Integer> myMoves = getMoves(i, config);

            //Generate all moves
            for(int t = 0; t < myMoves.size(); t++) {
                nextNeighbor = moveUnit(i, myMoves.get(t), config);
                //Check if done before.
                if (!tried.contains(nextNeighbor)) {
                    tried.add(nextNeighbor);
                    toReturn.add(nextNeighbor);
                }
            }
        }

        return toReturn;
    }//getNeighbors

    //Get where we want the solver to start from
    public ArrayList<Integer> getStart(){
        return curBoard;
    }

    private boolean canSelect(int pos) {
        return (char)(int)curBoard.get(pos) != '.';
    }

    public void selectUnit(int pos) {
        if (pos >= 0 && pos < curBoard.size() && canSelect(pos)) {
	    switch (undoStack.size()){
	    case 2:
		undo();
		undo();
	    case 0:
		add(pos);
		break;
	    case 1:
		add(pos);
		checkLegal();
		break;
	    default:
		clearStack();
	    }
	    setChanged();
	    notifyObservers();
	}
    }//selectUnit

    //Check if a legal move
    private void checkLegal() {
	if (undoStack.size() == 2) {
            ArrayList<Integer> canGo = 
                getMoves(undoStack.get(0), curBoard);
            boolean isLegal = false;
            int moveTo = undoStack.get(1);
            for (int i = 0; i < canGo.size(); i++) {
                if(canGo.get(i) == moveTo) {
                    isLegal = true;
                    break;
                }
            }

            if(isLegal) {
                curBoard = moveUnit(undoStack.get(0), 
                    moveTo, curBoard);
                clearStack();

                moveCount++;
	        setChanged();
	        notifyObservers();
            }
	}
    }//checkMatch

    private void clearStack() {
        undoStack = new ArrayList<Integer>();
    }//Clear Stack
    private void undo() {
	int s = undoStack.size();
	if (s > 0) {
	    undoStack.remove(s-1);
	}

	setChanged();
	notifyObservers();
    }

    private void add(int pos) {
        if(!undoStack.contains(pos)) {
	    undoStack.add(pos);
        }
    }

    public void next() {
        //Calculate the solution from the solver
        Solver<ArrayList<Integer>> mySolver =
            new Solver<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> Solution =
            mySolver.SolveBFS(this);

        //Reset 'tried' to none
        tried = new HashSet<ArrayList<Integer>>();

        //Set up the nextState
        ArrayList<Integer> nextState = new ArrayList<Integer>();

        if(Solution.size() == 0) {
            //No Solution
        } else if (Solution.size() == 1) {
            nextState = Solution.get(0);
            curBoard = nextState;
        } else {
            nextState = Solution.get(1);
            curBoard = nextState;
            moveCount++;
        }
        clearStack();

	setChanged();
	notifyObservers();
    }

    //Return the number of units currently selected
    public int howManyUnitsUp(){
        return undoStack.size();
    }

    public static void main (String args[]){
        //Start off with error checks
        if (args.length == 0) {
            //If there are not enough arguments
            //we alert the user and exit.
            System.err.println("Usage: java Chess input-file");
            System.exit(0);
        }

        ArrayList<Integer> myBoard = new ArrayList<Integer>();
        myBoard.add(new Integer('N'));
        myBoard.add(new Integer('.'));
        myBoard.add(new Integer('.'));
        myBoard.add(new Integer('.'));
        myBoard.add(new Integer('.'));

        myBoard.add(new Integer('.'));
        myBoard.add(new Integer('B'));
        myBoard.add(new Integer('.'));
        myBoard.add(new Integer('R'));
        myBoard.add(new Integer('.'));

        myBoard.add(new Integer('P'));
        myBoard.add(new Integer('.'));
        myBoard.add(new Integer('.'));
        myBoard.add(new Integer('.'));
        myBoard.add(new Integer('.'));

        myBoard.add(new Integer('.'));
        myBoard.add(new Integer('.'));
        myBoard.add(new Integer('.'));
        myBoard.add(new Integer('.'));
        myBoard.add(new Integer('.'));

        //After we've gotten through the error checks
        //We create the Water object
        ChessModel myChess = new ChessModel(4, 5, myBoard);

        //Calculate the solution from the solver
        Solver<ArrayList<Integer>> mySolver =
            new Solver<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> Solution =
            mySolver.SolveBFS(myChess);

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
                    if(b % 5 == 0) { System.out.println(); }
                    System.out.print(" " + (char)(int)Solution.get(i).get(b));
                }
                System.out.println();
            }
        }
    }

} //Chess Model
