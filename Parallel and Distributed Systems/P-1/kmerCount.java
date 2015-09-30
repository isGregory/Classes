// kmerCount.java
// Author: Gregory Hoople

/**
 * Class kmerCount provides an object that provides for
 * simple tracking and handling of modifying and sorting
 * k-mer information.
 *
 * @author Gregory Hoople
 * @version 2015-2-17
 */
public class kmerCount {

    // Hidden data members
    private Integer count;
    private String kmer;

    /**
     * Construct a new kmerCount object with a count of 1.
     *
     * @param	kmer	String of the k-mer value.
     */
    public kmerCount (String kmer) {
        this.kmer = kmer;
        count = 1;
    }

    /**
     * Retrieve the object's k-mer.
     *
     * @return The string variable of the k-mer of this object.
     */
    public String getKmer() {
        return kmer;
    }

    /**
     * Retrieve the object's count which
     * tracks occurences of the k-mer.
     *
     * @return The count variable.
     */
    public Integer getCount() {
        return count;
    }

    /**
     * Add a count value from another kmerCount to the count value
     * of this kmerCount.
     *
     * @param	other	The kmerCount object from which to get the value.
     */
    public void addCount( kmerCount other ) {
        count += other.getCount();
    }

    /**
     * Add one to the value of the count.
     */
    public void addOne() {
        count += 1;
    }

} // kmerCount class
