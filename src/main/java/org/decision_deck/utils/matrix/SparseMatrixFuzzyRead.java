package org.decision_deck.utils.matrix;

/**
 * <P>
 * A matrix which is able to retrieve values in [0, 1], i.e., doubles between 0 and 1 inclusive, at a set of positions,
 * each position being represented by a row and a column entries.
 * </P>
 * <P>
 * If this matrix is complete, it is equivalent to a fuzzy relation. If it is not complete, then a difference between a
 * fuzzy relation is that this matrix has <code>null</code> entries associated to some positions, which is different
 * than a zero value.
 * </P>
 * <P>
 * Vocabulary note: an entry can be a value or can be null, a value is a double between zero and one.
 * </P>
 * <P>
 * Note that, although this interface is read-only, the underlying object may be mutable.
 * </P>
 * 
 * @author Olivier Cailloux
 * 
 * @param <R>
 *            the type of objects used to designate the row part of a position.
 * @param <C>
 *            the type of objects used to designate the column part of a position.
 */
public interface SparseMatrixFuzzyRead<R, C> extends SparseMatrixDRead<R, C> {
    /**
     * @return the double value at that position (between zero and one inclusive), or <code>null</code>.
     */
    @Override
    public Double getEntry(R row, C column);

    /**
     * @return the double value at that position (between zero and one inclusive).
     */
    @Override
    public double getValue(R row, C column);
}
