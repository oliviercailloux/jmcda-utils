package org.decision_deck.utils.matrix;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Predicate;

public class ValidatingDecoratedMatrix<R, C> extends ForwardingSparseMatrix<R, C> implements SparseMatrixD<R, C> {

	/**
	 * not <code>null</code>.
	 */
	private final Predicate<Double> m_valuePredicate;

	/**
	 * Retrieves the predicate used in this object. Every value in this matrix
	 * satisfies the returned predicate.
	 * 
	 * @return not <code>null</code>.
	 */
	public Predicate<Double> getValuePredicate() {
		return m_valuePredicate;
	}

	/**
	 * <p>
	 * Creates a new matrix decorating the given matrix by ensuring every element it
	 * contains satisfy the given predicate.
	 * <p>
	 * <p>
	 * This object assumes ownership of the delegate.
	 * </p>
	 * 
	 * @param delegate       not <code>null</code>, must be empty.
	 * @param valuePredicate not <code>null</code>.
	 */
	public ValidatingDecoratedMatrix(SparseMatrixD<R, C> delegate, Predicate<Double> valuePredicate) {
		super(delegate);
		checkArgument(delegate.isEmpty());
		checkNotNull(valuePredicate);
		m_valuePredicate = valuePredicate;
	}

	/**
	 * Returns a new matrix ensuring every element it contains satisfies the given
	 * predicate.
	 * 
	 * @param                <R> the row type.
	 * @param                <C> the column type.
	 * @param valuePredicate not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public <R, C> ValidatingDecoratedMatrix<R, C> create(Predicate<Double> valuePredicate) {
		return new ValidatingDecoratedMatrix<R, C>(Matrixes.<R, C>newSparseD(), valuePredicate);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param value a double value satisfying the predicate this object uses.
	 * @throws IllegalArgumentException iff the value does not satisfy the predicate
	 *                                  used by this object.
	 */
	@Override
	public Double put(R row, C column, double value) {
		checkArgument(m_valuePredicate.apply(Double.valueOf(value)),
				"Invalid value at row " + row + ", column " + column + ": " + value + ".");
		return delegate().put(row, column, value);
	}

}
