package org.decision_deck.utils.matrix.mess;

import org.decision_deck.utils.PredicateUtils;
import org.decision_deck.utils.matrix.ForwardingSparseMatrix;
import org.decision_deck.utils.matrix.Matrixes;
import org.decision_deck.utils.matrix.SparseMatrixD;
import org.decision_deck.utils.matrix.SparseMatrixFuzzy;
import org.decision_deck.utils.matrix.ValidatingDecoratedMatrix;

public class ZeroToOneMatrix<R, C> extends ForwardingSparseMatrix<R, C>
		implements SparseMatrixD<R, C>, SparseMatrixFuzzy<R, C> {

	/**
	 * Creates a new matrix decorating the given matrix by ensuring every element it
	 * contains is between zero and one.
	 * 
	 * @param delegate not {@code null}, must be empty.
	 */
	public ZeroToOneMatrix(SparseMatrixD<R, C> delegate) {
		super(new ValidatingDecoratedMatrix<R, C>(delegate, PredicateUtils.inBetween(0d, 1d)));
	}

	/**
	 * Creates a new matrix ensuring every element it contains is between zero and
	 * one.
	 * 
	 */
	public ZeroToOneMatrix() {
		super(Matrixes.<R, C>newValidating(PredicateUtils.inBetween(0d, 1d)));
	}
}
