package org.decision_deck.utils.matrix;

import java.util.Collections;
import java.util.Set;


import com.google.common.collect.Table;

public class ConstantMatrixFuzzy<R, C> implements SparseMatrixFuzzyRead<R, C> {

    private final Double m_constant;
    private final Set<R> m_rows;
    private final Set<C> m_columns;

    public ConstantMatrixFuzzy(Set<R> rows, Set<C> columns, double constant) {
	if (rows == null || columns == null) {
	    throw new NullPointerException("" + rows + columns);
	}
	m_rows = rows;
	m_columns = columns;
	m_constant = Double.valueOf(constant);
    }

    @Override
    public Double getEntry(R row, C column) {
	return m_rows.contains(row) && m_columns.contains(column) ? m_constant : null;
    }

    @Override
    public boolean approxEquals(SparseMatrixDRead<R, C> m2, double imprecision) {
	if (m2 == null) {
	    return false;
	}
	if (m2.getValueCount() != getValueCount()) {
	    return false;
	}
	if (!m2.isComplete()) {
	    return false;
	}
	for (R row : m2.getRows()) {
	    if (!m_rows.contains(row)) {
		return false;
	    }
	    for (C column : m2.getColumns()) {
		if (!m_columns.contains(column)) {
		    return false;
		}
		final Double entry2 = m2.getEntry(row, column);
		/** Non null because is complete. */
		if (Math.abs(entry2.doubleValue() - m_constant.doubleValue()) > imprecision) {
		    return false;
		}
	    }
	}
	return true;
    }

    @Override
    public Set<C> getColumns() {
	return Collections.unmodifiableSet(m_columns);
    }

    @Override
    public Set<R> getRows() {
	return Collections.unmodifiableSet(m_rows);
    }

    @Override
    public int getValueCount() {
	return m_rows.size() * m_columns.size();
    }

    @Override
    public boolean isComplete() {
	return true;
    }

    @Override
    public boolean isEmpty() {
	return m_columns.isEmpty() || m_rows.isEmpty();
    }

    @Override
    public Table<R, C, Double> asTable() {
	throw new UnsupportedOperationException();
    }

    @Override
    public double getValue(R row, C column) {
	if (m_rows.contains(row) && m_columns.contains(column)) {
	    return m_constant.doubleValue();
	}
	throw new IllegalStateException("Expected value at " + row + ", " + column + ".");
    }

}
