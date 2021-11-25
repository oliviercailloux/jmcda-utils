package org.decision_deck.utils.matrix;

public class ForwardingSparseMatrix<R, C> extends ForwardingSparseMatrixRead<R, C> implements SparseMatrixD<R, C> {

	/**
	 * Creates a new matrix forwarding to the given delegate.
	 * 
	 * @param delegate not {@code null}.
	 */
	public ForwardingSparseMatrix(SparseMatrixD<R, C> delegate) {
		super(delegate);
	}

	@Override
	protected SparseMatrixD<R, C> delegate() {
		return (SparseMatrixD<R, C>) super.delegate();
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
	public boolean removeColumn(C column) {
		return delegate().removeColumn(column);
	}

	@Override
	public boolean removeRow(R row) {
		return delegate().removeRow(row);
	}

}
