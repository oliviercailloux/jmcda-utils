package org.decision_deck.utils.matrix.mess;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.decision_deck.utils.matrix.SparseMatrixDRead;

import com.google.common.collect.Table;

public class ReadFloatMatrixForwarder<R, C> implements SparseMatrixDRead<R, C> {

	private final SparseMatrixDRead<R, C> m_delegate;

	@Override
	public boolean approxEquals(SparseMatrixDRead<R, C> m2, double imprecision) {
		return m_delegate.approxEquals(m2, imprecision);
	}

	@Override
	public boolean equals(Object obj) {
		return m_delegate.equals(obj);
	}

	@Override
	public int hashCode() {
		return m_delegate.hashCode();
	}

	@Override
	public String toString() {
		return m_delegate.toString();
	}

	@Override
	public Set<C> getColumns() {
		return m_delegate.getColumns();
	}

	@Override
	public Double getEntry(R row, C column) {
		return m_delegate.getEntry(row, column);
	}

	@Override
	public Set<R> getRows() {
		return m_delegate.getRows();
	}

	@Override
	public int getValueCount() {
		return m_delegate.getValueCount();
	}

	@Override
	public boolean isComplete() {
		return m_delegate.isComplete();
	}

	@Override
	public boolean isEmpty() {
		return m_delegate.isEmpty();
	}

	protected SparseMatrixDRead<R, C> delegate() {
		return m_delegate;
	}

	@Override
	public Table<R, C, Double> asTable() {
		return m_delegate.asTable();
	}

	@Override
	public double getValue(R row, C column) {
		return m_delegate.getValue(row, column);
	}

	public ReadFloatMatrixForwarder(SparseMatrixDRead<R, C> delegate) {
		checkNotNull(delegate);
		m_delegate = delegate;
	}
}
