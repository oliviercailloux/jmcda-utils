package org.decision_deck.utils.matrix;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

public class SparseMatrixDImpl<R, C> implements SparseMatrixD<R, C> {

	private final Table<R, C, Double> m_table = Tables.newCustomTable(Maps.<R, Map<C, Double>>newLinkedHashMap(),
			new Supplier<Map<C, Double>>() {
				@Override
				public Map<C, Double> get() {
					return Maps.newLinkedHashMap();
				}
			});

	SparseMatrixDImpl() {
		/** Should be created through factory. */
	}

	@Override
	public Table<R, C, Double> asTable() {
		/** TODO return an read-only view */
		return m_table;
	}

	@Override
	public Set<C> getColumns() {
		return Collections.unmodifiableSet(m_table.columnKeySet());
	}

	@Override
	public Double getEntry(R row, C column) {
		return m_table.get(row, column);
	}

	@Override
	public Set<R> getRows() {
		return Collections.unmodifiableSet(m_table.rowKeySet());
	}

	@Override
	public int getValueCount() {
		return m_table.size();
	}

	@Override
	public boolean isComplete() {
		return m_table.size() == m_table.rowKeySet().size() * m_table.columnKeySet().size();
	}

	@Override
	public boolean isEmpty() {
		return m_table.isEmpty();
	}

	@Override
	public Double put(R row, C column, double value) {
		return doPut(row, column, value);
	}

	@Override
	public Double remove(R row, C column) {
		return m_table.remove(row, column);
	}

	@Override
	public boolean approxEquals(SparseMatrixDRead<R, C> m2, double imprecision) {
		return Matrixes.approxEqual(this, m2, imprecision);
	}

	/**
	 * Associates the specified value with the specified keys. If this object
	 * already contained a mapping for those keys, the old value is replaced with
	 * the specified value.
	 * 
	 * @param row    row key that the value should be associated with
	 * @param column column key that the value should be associated with
	 * @param value  value to be associated with the specified keys
	 * @return the value previously associated with the keys, or {@code null} if no
	 *         mapping existed for the keys
	 */
	public Double doPut(R row, C column, double value) {
		return m_table.put(row, column, Double.valueOf(value));
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SparseMatrixDRead<?, ?>)) {
			return false;
		}
		SparseMatrixDRead<?, ?> m2 = (SparseMatrixDRead<?, ?>) obj;
		return m_table.equals(m2.asTable());
	}

	@Override
	public int hashCode() {
		return m_table.hashCode();
	}

	@Override
	public String toString() {
		final ToStringHelper helper = Objects.toStringHelper(this);
		helper.addValue(Matrixes.toString(this));
		return helper.toString();
	}

	@Override
	public boolean removeRow(R row) {
		if (!getRows().contains(row)) {
			return false;
		}
		for (C column : ImmutableSet.copyOf(getColumns())) {
			remove(row, column);
		}
		return true;
	}

	@Override
	public boolean removeColumn(C column) {
		return m_table.columnKeySet().remove(column);
		// if (!getColumns().contains(column)) {
		// return false;
		// }
		// for (R row : ImmutableSet.copyOf(getRows())) {
		// remove(row, column);
		// }
		// return true;
	}

	@Override
	public double getValue(R row, C column) {
		final Double entry = getEntry(row, column);
		if (entry != null) {
			return entry.doubleValue();
		}
		throw new IllegalStateException("Expected value at " + row + ", " + column + ".");
	}

}
