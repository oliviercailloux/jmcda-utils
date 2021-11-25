package org.decision_deck.utils.matrix;

import org.decision_deck.utils.PredicateUtils;

import com.google.common.base.Predicate;

public class SparseMatrixFuzzyImpl<R, C> extends ForwardingSparseMatrix<R, C> implements SparseMatrixFuzzy<R, C> {

	public static Predicate<Double> VALIDATOR = PredicateUtils.inBetween(0d, 1d);

	/**
	 * Creates a new matrix decorating the given matrix by ensuring every element it
	 * contains is between zero and one.
	 * 
	 * @param delegate not {@code null}, must be empty.
	 */
	SparseMatrixFuzzyImpl(SparseMatrixD<R, C> delegate) {
		super(new ValidatingDecoratedMatrix<R, C>(delegate, VALIDATOR));
	}

	/**
	 * Creates a new matrix ensuring every element it contains is between zero and
	 * one.
	 * 
	 */
	SparseMatrixFuzzyImpl() {
		super(Matrixes.<R, C>newValidating(VALIDATOR));
	}
}
