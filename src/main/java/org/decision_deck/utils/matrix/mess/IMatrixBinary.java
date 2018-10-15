package org.decision_deck.utils.matrix.mess;

import org.decision_deck.utils.matrix.SparseMatrixDRead;

/**
 * A complete matrix. When no boolean has been put at a specific position, the value is <code>false</code>.
 * 
 * @author Olivier Cailloux
 * 
 * @param <R>
 *            the row type.
 * @param <C>
 *            the column type.
 */
public interface IMatrixBinary<R, C> extends SparseMatrixDRead<R, C> {

    /**
     * Puts a value in this matrix at the position specified by the given row and column.
     * 
     * @param row
     *            not <code>null</code>.
     * @param column
     *            not <code>null</code>.
     * @param value
     *            a boolean value.
     * @throws IllegalArgumentException
     *             if some property of the specified row, column or value prevents it from being stored in this object.
     */
    public void put(R row, C column, boolean value);

    /**
     * Retrieves the value stored at the given position. The given row and column must exist in this matrix, that is,
     * they must be contained in {@link #getRows()} and {@link #getColumns()}, respectively.
     * 
     * @param row
     *            not <code>null</code>.
     * @param column
     *            not <code>null</code>.
     * @return the value.
     */
    public boolean getBooleanValue(R row, C column);
}
