// ThreeSum.java
// Author: Gregory Hoople

import edu.rit.pj2.Loop;
import edu.rit.pj2.Task;
import edu.rit.pj2.vbl.LongVbl;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

/**
 * The ThreeSum program is a multicore parallel program which takes a
 * file containing a list of integers and searches through that list
 * and finds any triple group of integers whose sum is equal to 0.
 * The program prints out the triples it finds and then lists the
 * total number it has found.
 *
 * Usage: java pj2 ThreeSum <file>
 *
 * @author Gregory Hoople
 * @version 2015-3-12
 */
public class ThreeSum extends Task {

// Program shared variables.

    // Keeps track of the numbers read in from the file.
    Vector<Integer> values;

    // Keeps track of the number of 3-Sum groups.
    LongVbl count;

// Main program.

    /**
     * Main program.
     */
    public void main (final String[] args) throws Exception {

        // Check argument constraints
        if ( args.length != 1 ) {
            System.err.println("Incorrect number of arguments\n"
                + "Usage: java pj2 ThreeSum <file>");
            System.exit(0);
        }

        // Load the contents of a file
        String contents = readFile( args[0] );

        // Pull integers out of the file's contents
    	values = parseContents( contents );

        // Keep track of the number of 3-Sums
        count = new LongVbl.Sum( 0 );

        // Search numbers for 3-Sums
        // The "values.size() - 3" (vs "- 1" ) is due to not needing
        // to check the "first" value with the last 2 elements.
        parallelFor (0, values.size() - 3) .exec (new Loop()
        {
            LongVbl thrCount;

            public void start() {
                thrCount = threadLocal( count );
            }

            public void run (int i) {
                Integer first = values.get(i);

                // Go through all second number candidates after the first.
                for ( int b = i + 1; b < values.size(); b++ ) {
                    Integer second = values.get(b);

                    // Go through all third number candidates after the second.
                    for ( int c = b + 1; c < values.size(); c++ ) {
                        Integer third = values.get(c);

                        // Check for valid group
                        if ( first + second + third == 0 ) {
                            System.out.println( first + "\t"
                                + second + "\t" + third );
                            System.out.flush();
                            ++ thrCount.item;
                        }
                    }
                }
            }
        });

        if ( count.item == 0 ) {
            System.out.println( "No solutions" );
        } else if ( count.item == 1 ) {
            System.out.println( "1 solution" );
        } else {
            System.out.println( count.item + " solutions" );
        }
    } // main

// Hidden operations.

    /**
     * Get the contents of a file loaded into a string.
     *
     * @param   fileName    Name of file to load
     *
     * @return  String with all the contents of a file.
     */
    private static String readFile( String fileName ) {
        String toReturn = "";

        try {
            File toRead = new File( fileName );

            if ( !toRead.exists() ){
                System.err.println( "File [" + fileName + "] does not exist.");
                System.exit( 0 );
            }

            // BufferedReader is used to scan
            // through all the lines of the given file.
            BufferedReader bR = new BufferedReader( new FileReader( toRead ) );

            String line;

            // Keep track of how many lines into the content we are.
            int n = 0;

            // Read in the file's content into a string for parsing.
            while ( ( line = bR.readLine() ) != null ) {
                // The newline is necessary to prevent
                // numbers from concatenating
                toReturn = toReturn + "\n" + line;
                n++;
            }

            // If the file has no content, the file isn't valid.
            if ( n == 0 ) {
                System.err.println( "File [" + fileName + "]"
                    + " has no content." );
                System.exit( 0 );
            }

        } catch ( IOException e ) {
            System.err.println( "I/O error while reading " + fileName );
            System.exit( 0 );
        }
        return toReturn;
    } // readFile

    /**
     * Pull out and return integer values from a string.
     *
     * @param   contents    A string to parse for integer values
     *
     * @return  Vector of Integer values pulled from "contents" parameter.
     */
    private static Vector<Integer> parseContents( String contents ) {

        // Break up all the values
        StringTokenizer st = new StringTokenizer( contents, " \n\t" );

        if ( st.countTokens() < 3 ) {
            System.err.println( "Error - Too few integers. "
                + "Input file must contain 3 or more integers." );
            System.exit( 0 );
        }

        // Hold the integers
        Vector<Integer> toReturn = new Vector<Integer>( st.countTokens() );

        String next = "";

        // Convert the tokens to integers
        while ( st.hasMoreTokens() ) {
            next = st.nextToken();
            try {
                toReturn.add( Integer.parseInt( next ) );
            } catch ( NumberFormatException e ) {
                System.err.println( "Error - Non-integer found in file: "
                    + next );
                System.exit( 0 );
            }
        }

        return toReturn;
    } // parseContents

} // ThreeSum Class
