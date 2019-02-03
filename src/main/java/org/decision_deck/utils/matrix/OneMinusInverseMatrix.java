package org.decision_deck.utils.matrix;

import java.util.Set;

import com.google.common.collect.Table;

/**
 * <P>
 * A matrix based on an other matrix (called 'base' matrix). When queried about
 * its value for a given (row, column) position, this matrix replies by
 * computing one minus the value at the position (column, row) (called inverted
 * position) of the 'base' matrix.
 * </P>
 * <P>
 * This matrix contains a value at a given position if and only if the base
 * matrix contains a value at the inverted position.
 * </P>
 * <P>
 * This kind of matrix is implemented only for the case where the positions are
 * specified using the same type of objects for rows and columns (so that they
 * can be exchanged by the user) because it is probably only useful in that
 * case.
 * </P>
 * <P>
 * Note that this matrix's rows are the base matrix's columns and this matrix's
 * columns are the base matrix's rows. (This is a consequence of this matrix's
 * definition.)
 * </P>
 * 
 * @author Olivier Cailloux
 * 
 * @param <E> the type of elements this matrix qualifies, or equivalently, the
 *        type of object referencing the row and the column part of the
 *        positions.
 */
public class OneMinusInverseMatrix<E> implements SparseMatrixDRead<E, E> {

	private final SparseMatrixDRead<E, E> m_base;
	private final double m_complement;

	/**
	 * Sets this object to use the given matrix as base and a complement possibly
	 * different than one.
	 * 
	 * @param base       not <code>null</code>.
	 * @param complement the complement to use instead of one.
	 */
	public OneMinusInverseMatrix(SparseMatrixDRead<E, E> base, double complement) {
		if (base == null) {
			throw new NullPointerException();
		}
		m_base = base;
		m_complement = complement;
	}

	/**
	 * Sets this object to use the given matrix as base.
	 * 
	 * @param base not <code>null</code>.
	 */
	public OneMinusInverseMatrix(SparseMatrixDRead<E, E> base) {
		if (base == null) {
			throw new NullPointerException();
		}
		m_base = base;
		m_complement = 1d;
	}

	@Override
	public boolean approxEquals(final SparseMatrixDRead<E, E> m2, final double imprecision) {
		/**
		 * Let's check if m2 is approximately equal to this object. We invert m2 to see
		 * if it becomes approximatively equal to the base object: computing twice 'one
		 * minus inverse' is the same as doing nothing, i.e., this object is
		 * approximatively equal to the given object m2 iff f^-1(this) is
		 * approximatively equal to f(m2), where f is the 'complement minus inverse'
		 * function.
		 */
		return m_base.approxEquals(new OneMinusInverseMatrix<E>(m2, m_complement), imprecision);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof OneMinusInverseMatrix<?>)) {
			return false;
		}
		final OneMinusInverseMatrix<?> o2 = (OneMinusInverseMatrix<?>) obj;
		return m_base.equals(o2.m_base);
	}

	@Override
	public Set<E> getColumns() {
		return m_base.getRows();
	}

	@Override
	public Double getEntry(final E row, final E column) {
		final Double baseEntry = m_base.getEntry(column, row);
		if (baseEntry == null) {
			return null;
		}
		return Double.valueOf(m_complement - baseEntry.doubleValue());
	}

	@Override
	public Set<E> getRows() {
		return m_base.getColumns();
	}

	@Override
	public int getValueCount() {
		return m_base.getValueCount();
	}

	@Override
	public int hashCode() {
		/**
		 * offset to reduce hash collisions with other matrix types (e.g., the
		 * underlying matrix), which are not equal to this one by definition (see
		 * #equals).
		 */
		return 79 + m_base.hashCode();
	}

	@Override
	public boolean isComplete() {
		return m_base.isComplete();
	}

	@Override
	public boolean isEmpty() {
		return m_base.isEmpty();
	}

	@Override
	public Table<E, E, Double> asTable() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getValue(E row, E column) {
		final Double entry = getEntry(row, column);
		if (entry != null) {
			return entry.doubleValue();
		}
		throw new IllegalStateException("Expected value at " + row + ", " + column + ".");
	}

}
