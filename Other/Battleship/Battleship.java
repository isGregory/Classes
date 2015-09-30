/*
 * Battleship.java
 *
 *
 * Version:
 *     $Id: Battleship.java,v 1.21 2013/09/19 22:44:08 ghh8942 Exp ghh8942 $
 *
 * Revisions:
 *     $Log: Battleship.java,v $
 *     Revision 1.21  2013/09/19 22:44:08  ghh8942
 *     Cleaned up method interactions with GameBoard
 *
 *     Revision 1.20  2013/09/18 20:30:48  ghh8942
 *     Winable and compiles.
 *     Added the "you win!" line.
 *
 *     Revision 1.19  2013/09/18 20:28:16  ghh8942
 *     Set up win setting.
 *
 *     Revision 1.18  2013/09/18 19:54:10  ghh8942
 *     Fixed some printout handling.
 *
 *     Revision 1.17  2013/09/18 19:24:52  ghh8942
 *     Feels fully functional. Have not stress tested it just yet.
 *
 *     Revision 1.16  2013/09/18 19:08:36  ghh8942
 *     Got program to compile.
 *
 *     Revision 1.15  2013/09/18 18:59:00  ghh8942
 *     Better error handling.
 *
 *     Revision 1.14  2013/09/18 18:14:30  ghh8942
 *     fixed exception error.
 *
 *     Revision 1.13  2013/09/18 18:09:57  ghh8942
 *     Setting up exceptions.
 *
 *     Revision 1.12  2013/09/18 17:22:17  ghh8942
 *     fixed output.
 *
 *     Revision 1.11  2013/09/18 17:13:57  ghh8942
 *     Compiles.
 *
 *     Revision 1.10  2013/09/16 20:24:27  ghh8942
 *     fixed the stat printing.
 *
 *     Revision 1.9  2013/09/16 20:22:09  ghh8942
 *     Added the command to get stats.
 *
 *     Revision 1.8  2013/09/16 20:04:17  ghh8942
 *     Implemented fire commands.
 *
 *     Revision 1.7  2013/09/16 19:29:56  ghh8942
 *     Program now compiles.
 *
 *     Revision 1.6  2013/09/16 19:19:02  ghh8942
 *     Filled out some commands.
 *
 *     Revision 1.5  2013/09/13 17:58:22  ghh8942
 *     Streams work in a peliminary sense.
 *
 *     Revision 1.4  2013/09/13 17:45:13  ghh8942
 *     Playing around with streams.
 *
 *     Revision 1.3  2013/09/11 20:21:07  ghh8942
 *     Checks to make sure GameBoard.java v1.5 works.
 *
 *     Revision 1.2  2013/09/11 19:11:04  ghh8942
 *     Testing GameBoard
 *
 *     Revision 1.1  2013/09/11 18:45:40  ghh8942
 *     Initial revision
 *
 * Author: Gregory Hoople
 *
 */

import java.io.*; //BufferedReader, BufferedWriter, IOException

public class Battleship {

    public static BufferedReader in = null;
    public static BufferedWriter out = null;
    public static BufferedWriter errOut = null;

    public static void main( String args[] ) {
        final int MIN_BOARD_SIZE = 5;
        final int MAX_BOARD_SIZE = 26;
        boolean running = true;
        try{
            //Wrap the program's streams into their locations
            in = new BufferedReader(new InputStreamReader(System.in));
            out = new BufferedWriter(new OutputStreamWriter(System.out));
            errOut = new BufferedWriter(new OutputStreamWriter(System.err));

            //Handle the potential errors of setting up the board:
            //Check Illegal number of arguments
            if(args.length != 2){
                throw new BattleshipException("Usage: Battleship N config-file"
                    + "\nUsage: java Battleship N config-file");
            }

            //Check if first argument is integer or not.
            int firstArg = 0;
            try{
                firstArg = Integer.parseInt(args[0]);
            }catch (NumberFormatException nfe){
                throw new BattleshipException("Usage: Battleship N config-file"
                    + "\nUsage: java Battleship N config-file");
            }

            //Check if first argument is less than MIN_BOARD_SIZE.
            if(firstArg < MIN_BOARD_SIZE){
                throw new BattleshipException("Board must be "
                    + "at least " + MIN_BOARD_SIZE
                    + " by " + MIN_BOARD_SIZE + ".");
            }

            //Check if first argument is greater than MAX_BOARD_SIZE.
            if(firstArg > MAX_BOARD_SIZE){
                throw new BattleshipException("Board must be "
                    + "at most " + MAX_BOARD_SIZE
                    + " by " + MAX_BOARD_SIZE + ".");
            }

            //No argument issues with the board size so lets make the board:
            GameBoard myBoard = new GameBoard(firstArg);

            
            //Opening and Reading of the file:
            try{
                //Check if second argument for file name can be opened.
                BufferedReader openFile = new BufferedReader(
                    new FileReader(args[1]));

                String curLine = "";

                //Grabs the number of ships
                curLine = openFile.readLine();

                //This is the number of ships.
                int numShips = Integer.parseInt(curLine);
                myBoard.setNumShips(numShips);

                //We don't have to worry about this going over 26
                //Due to the guarantee.
                char ShipName = 'A';

                //We could set this up as a for loop using numShips,
                //but incase someone messed up this will make sure
                //we get all the ships listed in the file
                //by using a while loop.
                //This loop will read and make all the ships.
                while( ( curLine = openFile.readLine() ) != null){
                    //Make a ship.

                    //Split up the current line to all the parts
                    //While also getting rid of the pesky white spaces.
                    String[] split = curLine.split("\\s+");

                    //Try to create and add the ship to the board.
                    if(!myBoard.addShip(split[0].charAt(0), 
                        split[1].charAt(0), split[2].charAt(0),
                        split[3].charAt(0), ShipName)){
                        throw new BattleshipException("Overlapping or "
                            + "out-of-bounds ships in file "
                            + args[1] + ".");
                    }

                    ShipName++;
                    //Just incase we can't trust a guarantee:
                    if(ShipName > 'Z'){
                        ShipName = 'Z';
                    }
                }
                //Check if any ships extend 
                //beyond the boundaries of the board.
            }catch(IOException e){
                //File couldn't open
                throw new BattleshipException("Cannot open file "
                    + args[1] + ".");
            }

            //Board is ready so before the game loop
            //starts up, the board state is printed
            writeLine(myBoard.toString());

            String Command = "";
            //Game Loop
            while (running){
                write("> ");
                Command = readString();
                String[] split = Command.split("\\s+");
                if(split.length > 0){
                    //Handle the different commands.
                    if(split[0].compareTo("board") == 0){
                        writeLine(myBoard.toString());
                    }else if(split[0].compareTo("ships") == 0){
                        writeLine(myBoard.printShips());
                    }else if(split[0].compareTo("fire") == 0){
                        if(split.length > 2){
                            char FRow = split[1].charAt(0);
                            char FCol = split[2].charAt(0);
                            //Check to make sure we're not reading
                            //in the first letter of a word.
                            if(split[1].length() < 2
                                && split[2].length() < 2){
                                writeLine(myBoard.Fire(FRow, FCol));
                                if(myBoard.isGameOver()){
                                    writeLine("You win!");
                                    writeLine(myBoard.printStats());
                                    running = false;
                                }
                            }else{ //Arguments too long
                                writeLine("Illegal coordinates.");
                            }
                        }else{ //Too many arguments
                            writeLine("Illegal coordinates.");
                        }
                    }else if(split[0].compareTo("stats") == 0){
                        writeLine(myBoard.printStats());
                    }else if(split[0].compareTo("help") == 0){
                        writeLine("Possible commands:");
                        writeLine("board - displays the user's board");
                        writeLine("ships - displays the placement of"
                                + " the ships");
                        writeLine("fire r c - fires a missile at the"
                                + " cell at [r,c]");
                        writeLine("stats - prints out the"
                                + " game statistics");
                        writeLine("quit - exits the game");
                    }else if(split[0].compareTo("quit") == 0){
                        running = false;
                    }else if(split[0].length() > 0){
                        //garbage
                        writeLine("Illegal command.");
                    }
                } //No command
            } //While(running)

            //Close our streams
            if(in != null){
                in.close();
            }
            if (out != null){
                out.close();
            }
            if (errOut != null){
                errOut.close();
            }
        }catch (BattleshipException batEx){
            try{
                writeError(batEx.getMessage() + "\n");
            }catch (IOException Crash){
                //Something tragic has taken place...
                //Abandon (Battle)Ship!
                Crash.printStackTrace();
            }
            //Exit with some dignity and grace.
            System.exit(0);
        }catch (IOException ioEx){
            ioEx.printStackTrace();
            //Exit with some dignity and grace.
            System.exit(0);
        }
    } //main()

    /*
     * Reads in a new string from the input buffer.
     */
    public static String readString() throws IOException{
        return in.readLine();
    } //readString()

    /*
     * Writes out to our output stream including a new line.
     */
    public static void writeLine(String toWrite) throws IOException{
        toWrite += "\n";
        write(toWrite);
    } //writeLine()

    /*
     * Writes out to our output stream as the string specifies
     */
    public static void write(String toWrite) throws IOException{
        out.write(toWrite, 0, toWrite.length());
        out.flush();
    } //write()

    /*
     * Writes out to where errors are supposed to go.
     */
    public static void writeError(String toWrite) throws IOException{
        errOut.write(toWrite, 0, toWrite.length());
        errOut.flush();
    } //writeError()
} //Battleship
