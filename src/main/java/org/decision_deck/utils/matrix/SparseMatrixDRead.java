package org.decision_deck.utils.matrix;

import java.util.Set;

import com.google.common.collect.Table;

/**
 * <P>
 * A matrix which is able to retrieve double values at a set of positions, each
 * position being represented by a row and a column entries. Two such objects
 * are equal iff they contain the same values at the same positions and the same
 * set of positions.
 * </P>
 * <P>
 * Vocabulary note: an entry can be a value or can be null, a value is a double
 * between zero and one. The D suffix stands for {@code double}.
 * </P>
 * <P>
 * Note that, although this interface is read-only, the underlying object may be
 * mutable. It is possible that {@link #getRows()}, e.g., yields a writable set.
 * </P>
 * 
 * @author Olivier Cailloux
 * 
 * @param <R> the type of objects used to designate the row part of a position.
 * @param <C> the type of objects used to designate the column part of a
 *        position.
 */
public interface SparseMatrixDRead<R, C> {
	/**
	 * Retrieves a view of this object as a table. The returned object should only
	 * be read and not be modified (e.g. through Table#remove method calls) as not
	 * everything is implemented yet.
	 * 
	 * @return not {@code null}.
	 */
	public Table<R, C, Double> asTable();

	/**
	 * Two matrix are "approximately equal" to a given degree of precision iff they
	 * contain values for the same mappings and the value they contain for each
	 * mapping are not more different than the given allowed imprecision.
	 * 
	 * @param m2          the matrix to which to compare this object for approximate
	 *                    equality. If {@code null}, this method returns
	 *                    {@code false}.
	 * @param imprecision the maximal imprecision allowed.
	 * @return {@code true} iff the given matrix is approximately equal to this
	 *         one.
	 */
	public boolean approxEquals(SparseMatrixDRead<R, C> m2, double imprecision);

	/**
	 * Two matrix are equal iff they are the same type and contain the same values
	 * at the same positions. Note that the values are compared exactly, thus a very
	 * small difference in one value will cause two matrixes to be unequal. See
	 * {@link #approxEquals(SparseMatrixDRead, double)} if this can be a problem.
	 * 
	 * @param obj the object to compare. May be {@code null}.
	 * @return {@code true} iff the given object is equal to this matrix.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj);

	/**
	 * Returns a read-only view of the columns existing in this matrix. The returned
	 * set "read through" to the matrix, thus modifications of this matrix are
	 * reflected in the returned set.
	 * 
	 * @return the objects such that at least one value exists in this matrix at a
	 *         position having the object as column. Not {@code null}. Empty
	 *         iff this matrix is empty.
	 */
	public Set<C> getColumns();

	/**
	 * <p>
	 * Returns the value at the position composed by the given row and column, or
	 * {@code null} if there is none.
	 * </p>
	 * 
	 * @param row    not {@code null}.
	 * @param column not {@code null}.
	 * @return the double value at that position, or {@code null}.
	 */
	public Double getEntry(R row, C column);

	/**
	 * <p>
	 * Returns the value at the position composed by the given row and column.
	 * Throws an exception if there is none.
	 * </p>
	 * <p>
	 * Use this method rather than {@link #getEntry(Object, Object)} if a value (non
	 * {@code null}) is expected at the given position: in case a value is
	 * missing, this method generates a clearer exception.
	 * </p>
	 * 
	 * @param row    not {@code null}.
	 * @param column not {@code null}.
	 * @return the double value at that position.
	 */
	public double getValue(R row, C column);

	/**
	 * Returns a view of the rows existing in this matrix. The returned set "read
	 * through" to the matrix, thus modifications of this matrix are reflected in
	 * the returned set. The view may be read-only (some objects may also support
	 * objects removal from the returned set).
	 * 
	 * @return the objects such that at least one value exists in this matrix at a
	 *         position having the object as row. Not {@code null}. Empty iff
	 *         this matrix is empty.
	 */
	public Set<R> getRows();

	/**
	 * <P>
	 * Gets the number of values in this matrix.
	 * </P>
	 * <P>
	 * Note that the word "size" is not used here because the size of the matrix
	 * could be understood as meaning its row count times its column count, which is
	 * the same as its value count only if it is complete.
	 * </P>
	 * 
	 * @return the count of values.
	 */
	public int getValueCount();

	/**
	 * <P>
	 * A matrix is complete iff it contains a value for every possible position
	 * (row, column) where row and column are rows and columns existing in this
	 * matrix (i.e. corresponding to at least one value). An empty matrix is
	 * complete.
	 * </P>
	 * 
	 * @return {@code true} iff this matrix is complete.
	 */
	public boolean isComplete();

	/**
	 * Checks whether this matrix contains no value.
	 * 
	 * @return {@code true} iff the matrix contains no value.
	 */
	public boolean isEmpty();
}
