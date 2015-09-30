// readFile.java
// Author: Gregory Hoople

import java.io.*;
import java.util.HashMap;

/**
 * Reads in file and tracks the number of each valid K-Mer; it is
 * a subclass of class {@link java.lang.Runnable </CODE>Runnable<CODE>}.
 *
 * @author Gregory Hoople
 * @version 2015-2-17
 */
public class readFile implements Runnable{

	// Hidden data members
	private HashMap<String, kmerCount> counts;
	private String fileName;
	private kmerCensus shared;
	private int k;

	/**
	 * Construct a new readFile object.
	 *
	 * @param	shared	Shared Object to eventually store all read data to.
	 * @param	fileName	The file name from which to read.
	 * @param	k	The length of the k-mers to read in.
	 */
	public readFile( kmerCensus shared, String fileName, int k ) {
		this.shared = shared;
		this.fileName = fileName;
		this.k = k;
		counts = new HashMap<String, kmerCount>();
	} // readFile

	/**
	 * Read in the file and then add the results to the shared object.
	 */
	public void run() {
		readIn();
		shared.addData( counts );
	} // run

	/**
	 * Read in the file established in the constructor and
	 * add that information to the local counts variable.
	 */
	public void readIn() {

		try {
			File toRead = new File( fileName );

			if ( !toRead.exists() ){
				System.err.println( "File [" + fileName + "] does not exist.");
				System.exit( 0 );
			}

			// BufferedReader is used to scan through all the lines of the given file.
			BufferedReader bR = new BufferedReader( new FileReader( toRead ) );

			String line;
			String contents = "";

			// Throw out the first line
			line = bR.readLine();

			// Make sure the file is valid
			if ( line == null ) {
				System.err.println( "File [" + fileName + "] is not in FASTA format." );
				System.exit( 0 );
			}

			// Keep track of how many lines into the content we are.
			int n = 0;

			// Read in the file's content into a string for parsing.
			while ( ( line = bR.readLine() ) != null ) {
				contents = contents + line;
				n++;
			}

			// If the file has no content, the file isn't valid.
			if ( n == 0 ) {
				System.err.println( "File [" + fileName + "] is not in FASTA format." );
				System.exit( 0 );
			}

			// Remove all the newlines for easier parsing
			contents = contents.replace( "\n", "" );

			parseContents( contents );

		} catch ( IOException e ) {
			System.err.println( "I/O error while reading " + fileName );
			System.exit( 0 );
		}
	} // readIn

	/**
	 * Go through an imput string and parse it for acceptable k-mers
	 *
	 * @param	contents	A string of characters to parse for k-mers.
	 */
	public void parseContents( String contents ) {

		// Holds the working kmer
		String kmer = "";

		// For every character in contents we add it to our working kmer
		for ( int i = 0; i < contents.length(); i++ ) {
			kmer = kmer + contents.charAt( i );

			// This while loop makes sure tested
			// kmers are of the correct size
			while ( kmer.length() > k ) {
				kmer = kmer.substring( 1 );
			}

			// Check that kmers aren't too short
			if( kmer.length() == k ) {
				kmer = kmer.toUpperCase();

				// If they exist already we increase the count one
				// otherwise we add them to the collection
				if( counts.containsKey( kmer ) ) {
					counts.get( kmer ).addOne();
				} else {
					counts.put( kmer, new kmerCount( kmer ) );
				}
			} // if kmer length == k
		} // for each character
	} // parseContents
} // readFile class
