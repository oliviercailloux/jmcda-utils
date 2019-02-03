package org.decision_deck.utils.matrix;

import com.google.common.base.Predicates;

public class SparseMatrixBooleanImpl<R, C> extends ForwardingSparseMatrix<R, C>
		implements SparseMatrixD<R, C>, SparseMatrixBooleanRead<R, C> {

	/**
	 * Creates a new matrix decorating the given matrix by ensuring every element it
	 * contains is between zero and one.
	 * 
	 * @param delegate not <code>null</code>, must be empty.
	 */
	SparseMatrixBooleanImpl(SparseMatrixD<R, C> delegate) {
		super(new ValidatingDecoratedMatrix<R, C>(delegate,
				Predicates.or(Predicates.equalTo(Double.valueOf(0d)), Predicates.equalTo(Double.valueOf(1d)))));
	}

	/**
	 * Creates a new matrix ensuring every element it contains is between zero and
	 * one.
	 * 
	 */
	SparseMatrixBooleanImpl() {
		super(Matrixes.<R, C>newValidating(
				Predicates.or(Predicates.equalTo(Double.valueOf(0d)), Predicates.equalTo(Double.valueOf(1d)))));
	}

	@Override
	public boolean getBooleanValue(R row, C column) {
		final double value = getValue(row, column);
		if (value != 1d && value != 0d) {
			throw new IllegalStateException("Value is not zero nor one.");
		}
		return value == 1d;
	}

	@Override
	public Boolean getBooleanEntry(R row, C column) {
		final Double entry = getEntry(row, column);
		if (entry == null) {
			return null;
		}
		final double value = entry.doubleValue();
		if (value != 1d && value != 0d) {
			throw new IllegalStateException("Value is not zero nor one.");
		}
		return Boolean.valueOf(value == 1d);
	}
}
