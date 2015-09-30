/*
 * GameBoard.java
 *
 *
 * Version:
 *     $Id: GameBoard.java,v 1.22 2013/09/19 23:03:10 ghh8942 Exp ghh8942 $
 *
 * Revisions:
 *     $Log: GameBoard.java,v $
 *     Revision 1.22  2013/09/19 23:03:10  ghh8942
 *     File compiles now.
 *
 *     Revision 1.21  2013/09/19 22:31:53  ghh8942
 *     Fixed the print method and updated the add ship method.
 *
 *     Revision 1.20  2013/09/18 20:27:59  ghh8942
 *     Set up infastructure for a winable state.
 *
 *     Revision 1.19  2013/09/18 19:55:26  ghh8942
 *     Fixed spelling of output.
 *
 *     Revision 1.18  2013/09/18 19:53:44  ghh8942
 *     Fixed how the program returns information after firing.
 *
 *     Revision 1.17  2013/09/18 19:08:55  ghh8942
 *     fixed a return error in add ship.
 *
 *     Revision 1.16  2013/09/18 18:58:31  ghh8942
 *     Checks for ships overlapping when adding.
 *
 *     Revision 1.15  2013/09/18 17:22:31  ghh8942
 *     Fixed the handling of a miss to not remove ship sections.
 *
 *     Revision 1.14  2013/09/18 17:17:51  ghh8942
 *     Removed a depreciated method of checking for hits.
 *
 *     Revision 1.13  2013/09/18 17:08:44  ghh8942
 *     Implemented ship sink tracking.
 *
 *     Revision 1.12  2013/09/18 16:50:51  ghh8942
 *     changed the printing system to reduce code.
 *     Also changed how ships were fired on.
 *
 *     Revision 1.11  2013/09/16 20:29:48  ghh8942
 *     Fixed Hit Ratio.
 *
 *     Revision 1.10  2013/09/16 20:21:58  ghh8942
 *     first version of stats.
 *
 *     Revision 1.9  2013/09/16 20:07:06  ghh8942
 *     Fixed printout on the Ships
 *
 *     Revision 1.8  2013/09/16 20:03:53  ghh8942
 *     fixed a problem with Fire.
 *
 *     Revision 1.7  2013/09/13 17:20:55  ghh8942
 *     Got the program to compile.
 *
 *     Revision 1.6  2013/09/13 16:23:39  ghh8942
 *     Added ability to fire.
 *
 *     Revision 1.5  2013/09/11 20:20:37  ghh8942
 *     The ability to add ships
 *     And to print their location
 *     Now work.
 *
 *     Revision 1.4  2013/09/11 20:04:28  ghh8942
 *     Added (But did not test)
 *     - The ability to add ships
 *     - The ability to display ships
 *
 *     Revision 1.3  2013/09/11 19:35:12  ghh8942
 *     Default Board will now print with correct formatting.
 *
 *     Revision 1.2  2013/09/11 19:15:28  ghh8942
 *     Fixed a lot of small things to get it to compile.
 *
 *     Revision 1.1  2013/09/11 19:10:51  ghh8942
 *     Initial revision
 *
 *
 * Author: Gregory Hoople
 *
 */

public class GameBoard {

    private int BoardLength;
    private Cell[][] Board;
    private int shotsFired;
    private int shotsHit;
    private int shipsSunk;
    private int numShips;

    /*
     * Constructor
     */
    public GameBoard(int Length){
        Board = new Cell[Length][Length];
        BoardLength = Length;
        shotsFired = 0;
        //Populates the default board
        for(int r = 0; r < Length; r++){
            for(int c = 0; c < Length; c++){
                Board[r][c] = new Water();
            }
        }
    }//GameBoard()

    public boolean isGameOver(){
        return (numShips == shipsSunk);
    }//isGameOver()

    public void setNumShips(int nS){
        numShips = nS;
    }//setNumShips()

    /*
     * Adds a ship to the game board
     * between points (R1,C1) and (R2,C2);
     */
    public boolean addShip(char R1, char C1, 
        char R2, char C2, char Name){
        //Set everything to a capital letter
        R1 = Character.toUpperCase(R1);
        C1 = Character.toUpperCase(C1);
        R2 = Character.toUpperCase(R2);
        C2 = Character.toUpperCase(C2);

        //Check if data entry is in range
        if(!isLegalCoordinate(R1)
            || !isLegalCoordinate(C1)
            || !isLegalCoordinate(R2)
            || !isLegalCoordinate(C2)){
            return false;
        }

        //Check if diagonal attempt
        if( R1 != R2 && C1 != C2){
            return false;
        }

        //Make Ship to track sinking
        //We don't need to save this object to an array
        //as the ShipSection Cells will hold on to it
        //and keep it relevent for us.
        Ship addShip = new Ship();

        //Remove 'A' from each possition to get the number equivilent.
        R1 -= 'A';
        C1 -= 'A';
        R2 -= 'A';
        C2 -= 'A';

        //Angle the coordinates to go from greatest to smallest values.
        //this allows for users to input coordinates in any order.
        int indexR1 = R1;
        int indexC1 = C1;
        int indexR2 = R2;
        int indexC2 = C2;
        if(R1 == R2){
            indexR1 = R1;
            if(C1 > C2){
                indexC1 = C1;
                indexC2 = C2;
            }else{
                indexC1 = C2;
                indexC2 = C1;
            }
        }else{
            indexC1 = C1;
            if(R1 > R2){
                indexR1 = R1;
                indexR2 = R2;
            }else{
                indexR1 = R2;
                indexR2 = R1;
            }
        }

        //Figure out the straight line of the ship
        //then check that a ship doesn't already exist there
        //then go through and add the ship sections
        //By checking before adding we add another for-loop
        //but it gives us the ability to alter the program
        //in the future so that we ignore the second ship that
        //is overlapping and keep running without having to
        //remove the stuff already added.
        if(indexR1 == indexR2){//Add ship left to right
            addShip.setSections(indexC1 - indexC2 + 1);
            //Check that a ship doesn't already exist in that section.
            for(int i = 0; i <= indexC1 - indexC2; i++){
                if(Board[indexR1][indexC2+i] instanceof ShipSection){
                    return false;
                }
            }
            //Add ship if none was detected.
            for(int i = 0; i <= indexC1 - indexC2; i++){
                Board[indexR1][indexC2+i] = new ShipSection(Name, addShip);
            }
        }else{
            addShip.setSections(indexR1 - indexR2 + 1);
            //Check that a ship doesn't already exist in that section.
            for(int i = 0; i <= indexR1 - indexR2; i++){
                if(Board[indexR2+i][indexC1] instanceof ShipSection){
                    return false;
                }
            }
            //Add ship if none was detected.
            for(int i = 0; i <= indexR1 - indexR2; i++){
                Board[indexR2+i][indexC1] = new ShipSection(Name, addShip);
            }
        }
        return true;
    } //addShip()

    /*
     * Fires a missile at the board.
     */
    public String Fire(char Row, char Col){
        Row = Character.toUpperCase(Row);
        Col = Character.toUpperCase(Col);

        //Check if data entry is in range
        if(!isLegalCoordinate(Row) || !isLegalCoordinate(Col)){
            return "Illegal coordinates.";
        }

        shotsFired++;
        
        //Zero in the coordinates.
        Row -= 'A';
        Col -= 'A';

        //Fire upon that cell.
        if(Board[Row][Col].fireUpon()){
            String toReturn = "Hit!";
            //ship was hit.
            //Update the hit count.
            shotsHit++;

            //Check if sunk
            if(((ShipSection)Board[Row][Col]).isSunk()){
                shipsSunk++;
                toReturn+= "\nSunk!";
            }

            //Return a ship was hit, possibly sunk with updated board.
            return toReturn + "\n" + toString();
        }

        //Return no ship was hit with updated board.
        return "Miss!" + "\n" + toString();
    } //Fire()

    /*
     * Prints the GameBoard.
     */
    public String toString(){
        String toReturn = " ";//One space to set up formating
        char Border = 'A';//Sets up the first character
        //Sets up the characters at the top.
        for(int c = 0; c < BoardLength; c++){
            toReturn += " " + Border;
            Border++;
        }
        Border = 'A';
        //Goes through making the board
        for(int r = 0; r < BoardLength; r++){
            toReturn += "\n";
            toReturn += "" + Border;
            Border++;
            for(int c = 0; c < BoardLength; c++){
                toReturn += " " + Board[r][c];
            }
        }
        return toReturn;
    } //toString()

    /*
     * Sets the game to debug mode and prints the ship's locations.
     */
    public String printShips(){
        Cell.setDebug();
        String toReturn = toString();
        Cell.turnOffDebug();
        return toReturn;
    } //printShips()

    public String printStats(){
        //Number of Missiles Fired:
        String toReturn = "Number of missiles fired: " + shotsFired + "\n";

        //Number of Hits:
        toReturn += "Number of hits: " + shotsHit + "\n";

        //Number of Misses:
        toReturn += "Number of misses: " + (shotsFired - shotsHit) + "\n";

        //Hit Ratio:
        double hitPercent = 100.0 * (shotsHit / (double)shotsFired);
        toReturn += "Hit ratio: " + hitPercent + "%\n";

        //Number of Ships Sunk:
        toReturn += "Number of Ships Sunk: " + shipsSunk;

        return toReturn;
    } //printStats()

    //Checks if the character lies on the board
    public boolean isLegalCoordinate(char P){
        P = Character.toUpperCase(P);
        if(P < 'A' || P >= 'A' + BoardLength){
            return false;
        }
        return true;
    } //isLegalCoordinate()

} //GameBoard
