package org.decision_deck.utils.matrix;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;


import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Table;

public class ForwardingSparseMatrixRead<R, C> implements SparseMatrixDRead<R, C> {

    private final SparseMatrixDRead<R, C> m_delegate;

    /**
     * Creates a new matrix forwarding to the given delegate.
     * 
     * @param delegate
     *            not <code>null</code>.
     */
    public ForwardingSparseMatrixRead(SparseMatrixDRead<R, C> delegate) {
	checkNotNull(delegate);
	m_delegate = delegate;
    }

    protected SparseMatrixDRead<R, C> delegate() {
	return m_delegate;
    }

    @Override
    public Table<R, C, Double> asTable() {
	return m_delegate.asTable();
    }

    @Override
    public boolean approxEquals(SparseMatrixDRead<R, C> m2, double imprecision) {
	return m_delegate.approxEquals(m2, imprecision);
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

    @Override
    public int hashCode() {
	return m_delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
	return m_delegate.equals(obj);
    }

    @Override
    public String toString() {
	final ToStringHelper helper = Objects.toStringHelper(this);
	helper.addValue(Matrixes.toString(m_delegate));
	return helper.toString();
    }

    @Override
    public double getValue(R row, C column) {
	return m_delegate.getValue(row, column);
    }

}
