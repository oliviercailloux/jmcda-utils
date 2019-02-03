package org.decision_deck.utils.matrix;

/**
 * <P>
 * A matrix which is able to store double values at a set of positions, each
 * position being represented by a row and a column. Note that any values,
 * including special double values such as {@link Double#POSITIVE_INFINITY}, are
 * accepted.
 * </P>
 * <P>
 * Vocabulary note: an entry can be a value or can be null, a value is a double
 * number.
 * </P>
 * 
 * @author Olivier Cailloux
 * 
 * @param <R> the type of objects used to designate the row part of a position.
 * @param <C> the type of objects used to designate the column part of a
 *        position.
 */
public interface SparseMatrixD<R, C> extends SparseMatrixDRead<R, C> {

	/**
	 * Puts a value in this matrix at the position specified by the given row and
	 * column.
	 * 
	 * @param row    not <code>null</code>.
	 * @param column not <code>null</code>.
	 * @param value  any double.
	 * @throws IllegalArgumentException if some property of the specified row,
	 *                                  column or value prevents it from being
	 *                                  stored in this object.
	 * @return the value previously associated with the keys, or {@code null} if no
	 *         mapping existed for the keys
	 */
	public Double put(R row, C column, double value);

	/**
	 * Removes the value in this matrix at the position specified by the given row
	 * and column. If there was no value at that position, this method has no
	 * effect.
	 * 
	 * @param row    not <code>null</code>.
	 * @param column not <code>null</code>.
	 * @return the value which was previously at the given position. Returns
	 *         <code>null</code> iff there was no value at that position (and,
	 *         hence, nothing was removed).
	 */
	public Double remove(R row, C column);

	public boolean removeColumn(C column);

	/**
	 * Removes all values at the given row.
	 * 
	 * @param row not <code>null</code>.
	 * @return <code>true</code> iff there was at least one value at the given row,
	 *         or equivalently, <code>true</code> iff the matrix has been modified,
	 *         or equivalently, <code>true</code> iff {@link #getRows()} reported
	 *         that this row existed before calling this method.
	 */
	public boolean removeRow(R row);

}
