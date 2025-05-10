import java.util.Comparator;


public class Term {
    private final String term;
    private long weight;
    /**
     * Constructs a Term with the specified term string and weight.
     *
     * @param term The string representation of the term.
     * @param weight The weight associated with the term. Must be non-negative.
     * @throws IllegalArgumentException if term is null or weight is negative.
     */
    public Term(String term, long weight) {
        if (term == null || weight < 0) {
            throw new IllegalArgumentException("Invalid term or weight.");
        }
        this.term = term;
        this.weight = weight;
    }
    /**
     * Retrieves the term string associated with this object.
     *
     * @return The term string.
     */
    public String getTerm() {
        return term;
    }
    /**
     * Retrieves the weight of the term.
     *
     * @return The weight of the term as a long value.
     */
    public long getWeight() {
        return weight;
    }
    /**
     * Sets the weight of the term.
     *
     * @param weight The new weight to be assigned.
     * @throws IllegalArgumentException if the weight is negative.
     */
    public void setWeight(long weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight must be non-negative.");
        }
        this.weight = weight;
    }
    /**
     * Provides a comparator that orders terms in descending order of their weight.
     *
     * @return A Comparator that compares terms by their weight in reverse order.
     */
    public static Comparator<Term> byReverseWeightOrder() {
        return (a, b) -> Long.compare(b.weight, a.weight);
    }
    /**
     * Provides a string representation of the term in the format: "weight<TAB>term".
     *
     * @return A formatted string representing the term and its weight.
     */
    @Override
    public String toString() {
        return String.format("%d\t%s", weight, term);
    }
}
