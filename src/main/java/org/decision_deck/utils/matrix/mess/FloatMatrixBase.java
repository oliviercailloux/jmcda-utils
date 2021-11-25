package org.decision_deck.utils.matrix.mess;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.decision_deck.utils.collection.extensional_order.ExtensionalComparator;
import org.decision_deck.utils.matrix.Matrixes;
import org.decision_deck.utils.matrix.SparseMatrixDRead;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ForwardingTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

@SuppressWarnings({ "synthetic-access", "deprecation" })
public class FloatMatrixBase<R, C> implements SparseMatrixDRead<R, C> {

	private final ExtensionalComparator<C> m_columnsComparator = ExtensionalComparator.create();
	private final ExtensionalComparator<R> m_rowsComparator = ExtensionalComparator.create();
	private final Table<R, C, Double> m_table;
	final private Predicate<Double> m_valuePredicate;

	public FloatMatrixBase() {
		this(Predicates.<Double>alwaysTrue());
	}

	/**
	 * @param predicate {@code null} for always {@code true}.
	 */
	public FloatMatrixBase(Predicate<Double> predicate) {
		// m_table = HashBasedTable.create();
		// m_table = TreeBasedTable.create(m_rowsComparator, m_columnsComparator);
		final TreeMap<R, Map<C, Double>> rowMap = Maps.newTreeMap(m_rowsComparator);
		m_table = Tables.newCustomTable(rowMap, new Supplier<Map<C, Double>>() {
			@Override
			public Map<C, Double> get() {
				final TreeMap<C, Double> map = Maps.newTreeMap(m_columnsComparator);
				Maps.filterValues(map, m_valuePredicate);
				return map;
			}
		});
		m_valuePredicate = predicate;
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
	protected Double doPut(R row, C column, double value) {
		if (!m_rowsComparator.contains(row)) {
			m_rowsComparator.addAsHighest(row);
		}
		if (!m_columnsComparator.contains(column)) {
			m_columnsComparator.addAsHighest(column);
		}
		return m_table.put(row, column, Double.valueOf(value));
	}

	protected Predicate<Double> getPredicate() {
		return m_valuePredicate;
	}

	@Override
	public Set<C> getColumns() {
		return new ForwardingSet<C>() {

			@Override
			public boolean contains(Object object) {
				if (!m_columnsComparator.contains(object)) {
					return false;
				}
				return delegate().contains(object);
			}

			@Override
			public boolean containsAll(Collection<?> collection) {
				for (Object object : collection) {
					if (!contains(object)) {
						return false;
					}
				}
				return true;
			}

			@Override
			protected Set<C> delegate() {
				return m_table.columnKeySet();
			}
		};
	}

	@Override
	public Double getEntry(R row, C column) {
		if (!m_columnsComparator.contains(column)) {
			return null;
		}
		if (!m_rowsComparator.contains(row)) {
			return null;
		}
		return m_table.get(row, column);
	}

	@Override
	public Set<R> getRows() {
		return new ForwardingSet<R>() {

			@Override
			public boolean contains(Object object) {
				if (!m_rowsComparator.contains(object)) {
					return false;
				}
				return delegate().contains(object);
			}

			@Override
			public boolean containsAll(Collection<?> collection) {
				for (Object object : collection) {
					if (!contains(object)) {
						return false;
					}
				}
				return true;
			}

			@Override
			protected Set<R> delegate() {
				return m_table.rowKeySet();
			}
		};
	}

	@Override
	public int getValueCount() {
		return m_table.size();
	}

	@Override
	public boolean isComplete() {
		return m_table.size() == getRows().size() * getColumns().size();
	}

	@Override
	public boolean isEmpty() {
		return m_table.isEmpty();
	}

	@Override
	public Table<R, C, Double> asTable() {
		/**
		 * Providing a read-write table would be hard, we would also have to wrap
		 * possible removals from the maps, maps iterators, cell set iterator...
		 */
		// return new ForwardingTable<R, C, Double>() {
		//
		// @Override
		// protected Table<R, C, Double> delegate() {
		// return m_table;
		// }
		//
		// @Override
		// public Double put(R rowKey, C columnKey, Double value) {
		// FloatMatrixNew.this.put(rowKey, columnKey, getValueCount());
		// }
		//
		// @Override
		// public void putAll(Table<? extends R, ? extends C, ? extends Double> table) {
		// }
		//
		// @Override
		// public Double remove(Object rowKey, Object columnKey) {
		// }
		// };

		// should wrap into unmodifiable...
		return new ForwardingTable<R, C, Double>() {

			@Override
			public Set<Cell<R, C, Double>> cellSet() {
				final Set<Cell<R, C, Double>> parentSet = super.cellSet();
				return new ForwardingSet<Table.Cell<R, C, Double>>() {
					@Override
					public boolean contains(Object object) {
						if (!(object instanceof Cell)) {
							return false;
						}
						Cell<?, ?, ?> cell = (Cell<?, ?, ?>) object;
						final Object row = cell.getRowKey();
						if (!containsRow(row)) {
							return false;
						}
						final Object column = cell.getColumnKey();
						if (!containsColumn(column)) {
							return false;
						}
						return parentSet.contains(object);
					}

					@Override
					protected Set<com.google.common.collect.Table.Cell<R, C, Double>> delegate() {
						return parentSet;
					}
				};
			}

			@Override
			public boolean containsColumn(Object columnKey) {
				if (!m_columnsComparator.contains(columnKey)) {
					return false;
				}
				return super.containsColumn(columnKey);
			}

			@Override
			public boolean containsRow(Object rowKey) {
				if (!m_rowsComparator.contains(rowKey)) {
					return false;
				}
				return super.containsRow(rowKey);
			}

			@Override
			protected Table<R, C, Double> delegate() {
				return m_table;
			}
		};
		// return m_table;
	}

	protected Double remove(R row, C column) {
		if (!m_rowsComparator.contains(row)) {
			return null;
		}
		if (!m_columnsComparator.contains(column)) {
			return null;
		}
		if (!m_table.contains(row, column)) {
			return null;
		}
		final Double previous = m_table.remove(row, column);
		if (previous != null) {
			if (!getRows().contains(row)) {
				m_rowsComparator.remove(row);
			}
			if (!getColumns().contains(column)) {
				m_columnsComparator.remove(column);
			}
		}
		return previous;
	}

	@Override
	public String toString() {
		return m_table.toString();
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
	public double getValue(R row, C column) {
		final Double entry = getEntry(row, column);
		if (entry != null) {
			return entry.doubleValue();
		}
		throw new IllegalStateException("Expected value at " + row + ", " + column + ".");
	}
}
