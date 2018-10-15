package org.decision_deck.utils.matrix;

/**
 * <P>
 * A matrix which is able to retrieve values in {true, false} at a set of positions, each position being represented by
 * a row and a column entries.
 * </P>
 * <P>
 * If this matrix is square and complete, it is equivalent to a boolean relation. The represented relation is not
 * necessarily total however. If the matrix is not complete, then a difference with a boolean relation is that this
 * matrix has <code>null</code> entries associated to some positions, which is different than a false value.
 * </P>
 * <P>
 * Vocabulary note: an entry can be a value or can be null, a value is non <code>null</code>.
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
public interface SparseMatrixBooleanRead<R, C> extends SparseMatrixFuzzyRead<R, C> {
    /**
     * <p>
     * This method is required for compatibility with {@link SparseMatrixDRead}. The method {@link #getBooleanEntry}
     * should be preferred to this one.
     * </p>
     * {@inheritDoc}
     * 
     * @return the double value at that position, exactly equal to <code>1d</code> to indicate <code>true</code> or
     *         <code>0d</code> to indicate <code>false</code>, or <code>null</code>.
     */
    @Override
    public Double getEntry(R row, C column);

    /**
     * <p>
     * Returns the value at the position composed by the given row and column. Throws an exception if there is none.
     * </p>
     * <p>
     * Use this method rather than {@link #getBooleanEntry} if a value (non <code>null</code>) is expected at the given
     * position: in case a value is missing, this method generates a clearer exception.
     * </p>
     * 
     * @param row
     *            not <code>null</code>.
     * @param column
     *            not <code>null</code>.
     * @return the boolean value at that position.
     */
    public boolean getBooleanValue(R row, C column);

    /**
     * <p>
     * This method is required for compatibility with {@link SparseMatrixDRead}. The method {@link #getBooleanValue}
     * should be preferred to this one.
     * </p>
     * {@inheritDoc}
     * 
     * @return the double value at that position, exactly equal to <code>1d</code> to indicate <code>true</code> or
     *         <code>0d</code> to indicate <code>false</code>.
     */
    @Override
    public double getValue(R row, C column);

    /**
     * <p>
     * Returns the value at the position composed by the given row and column, or <code>null</code> if there is none.
     * </p>
     * 
     * @param row
     *            not <code>null</code>.
     * @param column
     *            not <code>null</code>.
     * @return the boolean value at that position, or <code>null</code>.
     */
    public Boolean getBooleanEntry(R row, C column);

}
