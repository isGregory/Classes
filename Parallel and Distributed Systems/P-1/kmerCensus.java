// kmerCensus.java
// Author: Gregory Hoople

import java.util.Map;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class kmerCensus provides an object that creates a Census
 * of K-mer data by adding other Maps of data to this
 * collection in a thread safe manner. Also handles sorting
 * and formating for the printing of census data.
 *
 * @author Gregory Hoople
 * @version 2015-2-17
 */
public class kmerCensus {

    // Hidden data member.
    private HashMap<String, kmerCount> counts;

    /**
     * Construct a new kmerCensus.
     */
    public kmerCensus () {
        // Create counts
        counts = new HashMap<String, kmerCount>();
    }

    /**
     * Add a collection of data to the census data of counts.
     *
     * @param	toAdd	A collection of kmerCount
     */
    public synchronized void addData ( HashMap<String, kmerCount> toAdd ) {

        // For each item in "toAdd" we add the values to "counts"
        for ( Map.Entry<String, kmerCount> entry : toAdd.entrySet() ) {
            if( counts.containsKey( entry.getKey() ) ) {
                counts.get( entry.getKey() ).addCount( entry.getValue() );
            } else {
                counts.put( entry.getKey(), entry.getValue() );
            }
        } // for
    } // addData

    /**
     * Sort and then print out the census information
     * of all the collected kmers in counts.
     */
    public synchronized void printCensus () {
        ArrayList<kmerCount> sorted = new ArrayList<kmerCount>();

        // Fill the array list with data from the counts map.
        for ( Map.Entry<String, kmerCount> entry : counts.entrySet() ) {
            sorted.add( entry.getValue() );
        }

        // Sort the array list with the custom function below.
        Collections.sort( sorted, new valCompare() );

        // For each sorted item we print out the information.
        for ( kmerCount s : sorted ) {
            System.out.println( s.getKmer() + "\t" + s.getCount() );
        }
    } // printCensus

    /**
     * An object that provides a function for sorting.
     */
    private class valCompare implements Comparator<kmerCount> {

        /**
         * Compare two kmerCount objects for sorting.
         * The occurence count takes precidence, then
         * the alphabetical values based on
         * the std::string.compareTo function.
         *
         * @param	a	First kmerCount object
         * @param	b	Second kmerCount object to be compared with a.
         *
         * @return int value of how the objects should be ordered.
         */
        public int compare( kmerCount a, kmerCount b ) {

            // Check which has the higher occurence
            if ( a.getCount() > b.getCount() ) {
                return -1;
            } else if ( a.getCount() == b.getCount() ) {

                // If occurences are equal, compare strings
                if ( a.getKmer().compareTo( b.getKmer() ) < 0 ) {
                    return -1;
                } else {
                    return 1;
                }

            } else {
                return 1;
            }
        } // compare
    } // valCompare
} // kmerCensus Class
