package org.decision_deck.utils.matrix.mess;

import org.decision_deck.utils.matrix.Matrixes;
import org.decision_deck.utils.matrix.SparseMatrixD;

public class FloatMatrixForwarder<R, C> extends ReadFloatMatrixForwarder<R, C> implements SparseMatrixD<R, C> {

	public FloatMatrixForwarder() {
		this(Matrixes.<R, C>newSparseD());
	}

	public FloatMatrixForwarder(SparseMatrixD<R, C> delegate) {
		super(delegate);
	}

	@Override
	public Double put(R row, C column, double value) {
		return delegate().put(row, column, value);
	}

	@Override
	public Double remove(R row, C column) {
		return delegate().remove(row, column);
	}

	@Override
	protected SparseMatrixD<R, C> delegate() {
		return (SparseMatrixD<R, C>) super.delegate();
	}

	@Override
	public boolean removeRow(R row) {
		return delegate().removeRow(row);
	}

	@Override
	public boolean removeColumn(C column) {
		return delegate().removeColumn(column);
	}

}
