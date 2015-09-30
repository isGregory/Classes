// DnaCensus.java
// Author: Gregory Hoople

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Class DnaCensus is the main program. It takes in an integer for k-mer length
 * and list of file names and then spins off readFile objects to scan for valid
 * k-mers. It is a multi-threaded program where each readFile object is a
 * subclass of class {@link java.lang.Runnable </CODE>Runnable<CODE>}.
 * <P>
 * Usage: java DnaCensus <K> <file> ...
 *
 * @author Gregory Hoople
 * @version 2015-2-17
 */
public class DnaCensus {

    /**
     * Main Program.
     */
    public static void main( String args[] ) throws Throwable{

        // Check argument constraints
        if ( args.length < 2 ) {
            System.err.println("Too few arguments\n"
                + "Usage: java DnaCensus <K> <file> ...");
            System.exit(0);
        }

        // Get the K value and make sure it's acceptable
        int k = 1;
        boolean kFail = false;

        try {
            k = Integer.parseInt(args[0]);
            if ( k < 1 ) {
                kFail = true;
            }
        } catch ( NumberFormatException e ) {
            kFail = true;
        }

        if ( kFail ) {
            System.err.println("K must be a positive integer\n"
                + "Usage: java DnaCensus <K> <file> ...");
            System.exit(0);
        }

        // Create shared Object to collect and hold thread results.
        kmerCensus census = new kmerCensus();

        // Create LinkedList to hold and track threads.
        LinkedList<Thread> threads = new LinkedList<Thread>();

        // Create individual threads and start them.
        for ( int i = 1; i < args.length; i++ ) {
            Thread next = new Thread( new readFile( census, args[i], k ) );
            threads.add( next );
            next.start();
        }

        // Wait for all threads to run to completion.
        for ( Thread counted : threads ) {
            counted.join();
        }

        // Print the final list of the census.
        census.printCensus();
    } // Main
} // DnaCensus Class
