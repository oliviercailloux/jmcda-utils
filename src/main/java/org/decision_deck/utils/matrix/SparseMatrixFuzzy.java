package org.decision_deck.utils.matrix;


/**
 * <p>
 * A matrix which is able to store values in [0, 1], i.e., doubles between 0 and 1 inclusive, at a set of positions,
 * each position being represented by a row and a column.
 * </p>
 * <p>
 * Vocabulary note: an entry can be a value or can be <code>null</code>, a value is a double between zero and one.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 * @param <R>
 *            the type of objects used to designate the row part of a position.
 * @param <C>
 *            the type of objects used to designate the column part of a position.
 */
public interface SparseMatrixFuzzy<R, C> extends SparseMatrixFuzzyRead<R, C>, SparseMatrixD<R, C> {

    /**
     * Puts a value in this matrix at the position specified by the given row and column.
     * 
     * @param row
     *            not <code>null</code>.
     * @param column
     *            not <code>null</code>.
     * @param value
     *            between zero and one (inclusive).
     */
    @Override
    public Double put(R row, C column, final double value);

    /**
     * Removes the value in this matrix at the position specified by the given row and column. If there was no value at
     * that position, this method has no effect.
     * 
     * @param row
     *            not <code>null</code>.
     * @param column
     *            not <code>null</code>.
     * @return the value which was previously at the given position. Returns <code>null</code> iff there was no value at
     *         that position (and, hence, nothing was removed).
     */
    @Override
    public Double remove(R row, C column);
}
