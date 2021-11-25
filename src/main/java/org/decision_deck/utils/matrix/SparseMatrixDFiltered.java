package org.decision_deck.utils.matrix;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.collect_suppl.TablesFilters;

public class SparseMatrixDFiltered<R, C> implements SparseMatrixDRead<R, C> {

	private final SparseMatrixDRead<R, C> m_delegate;
	/**
	 * {@code null} for no restriction.
	 */
	private final Predicate<R> m_rowPredicate;
	private final Predicate<C> m_columnPredicate;

	@Override
	public String toString() {
		final ToStringHelper helper = Objects.toStringHelper(this);
		final Set<R> rows = getRows();
		for (R alternative : rows) {
			final StringBuilder rowB = new StringBuilder();
			for (C criterion : getColumns()) {
				final Double entry = getEntry(alternative, criterion);
				if (entry != null) {
					rowB.append(criterion.toString() + ": " + entry.doubleValue());
				}
			}
			helper.add("row " + alternative, "{" + rowB + "}");
		}
		return helper.toString();
	}

	/**
	 * @param delegate        not {@code null}.
	 * @param rowPredicate    {@code null} for no restriction.
	 * @param columnPredicate {@code null} for no restriction.
	 */
	public SparseMatrixDFiltered(SparseMatrixDRead<R, C> delegate, Predicate<R> rowPredicate,
			Predicate<C> columnPredicate) {
		checkNotNull(delegate);
		m_delegate = delegate;
		m_rowPredicate = rowPredicate == null ? Predicates.<R>alwaysTrue() : rowPredicate;
		m_columnPredicate = columnPredicate == null ? Predicates.<C>alwaysTrue() : columnPredicate;
	}

	@Override
	public boolean approxEquals(SparseMatrixDRead<R, C> m2, double imprecision) {
		return Matrixes.approxEqual(this, m2, imprecision);
	}

	@Override
	public Set<C> getColumns() {
		final Set<C> source = m_delegate.getColumns();
		final Predicate<C> effectivePredicate;
		if (m_rowPredicate == null) {
			effectivePredicate = m_columnPredicate;
		} else {
			/** See #getRows() for explanation. */
			final Predicate<C> hasValue = new Predicate<C>() {
				@Override
				public boolean apply(C criterion) {
					final Set<R> sourceRows = m_delegate.getRows();
					final Set<R> filtered;
					if (m_rowPredicate == null) {
						filtered = sourceRows;
					} else {
						filtered = Sets.filter(sourceRows, m_rowPredicate);
					}
					for (R alternative : filtered) {
						if (m_delegate.getEntry(alternative, criterion) != null) {
							return true;
						}
					}
					return false;
				}
			};
			if (m_columnPredicate == null) {
				effectivePredicate = hasValue;
			} else {
				effectivePredicate = Predicates.and(m_columnPredicate, hasValue);
			}
		}

		final Set<C> filtered;
		if (effectivePredicate == null) {
			filtered = source;
		} else {
			filtered = Sets.filter(source, effectivePredicate);
		}

		return Collections.unmodifiableSet(filtered);
	}

	@Override
	public Double getEntry(R row, C column) {
		if (m_rowPredicate != null && !m_rowPredicate.apply(row)) {
			return null;
		}
		if (m_columnPredicate != null && !m_columnPredicate.apply(column)) {
			return null;
		}
		return m_delegate.getEntry(row, column);
	}

	@Override
	public Set<R> getRows() {
		final Set<R> source = m_delegate.getRows();
		final Predicate<R> effectivePredicate;
		if (m_columnPredicate == null) {
			effectivePredicate = m_rowPredicate;
		} else {
			/**
			 * As we filter on columns as well, it might be that a row which passes the
			 * given predicate on rows has no evaluations on the remaining columns, and
			 * hence should not appear as a row of this object. Thus we need to make sure
			 * that the row, even if it passes the predicate, has at least one value,
			 * considering the filtered column.
			 */
			final Predicate<R> hasValue = new Predicate<R>() {
				@Override
				public boolean apply(R input) {
					/** Can't call #getColumns here, otherwise infinite recursion may occur. */
					final Set<C> filtered = getFilteredColumns();
					for (C column : filtered) {
						if (m_delegate.getEntry(input, column) != null) {
							return true;
						}
					}
					return false;
				}
			};
			if (m_rowPredicate == null) {
				effectivePredicate = hasValue;
			} else {
				effectivePredicate = Predicates.and(m_rowPredicate, hasValue);
			}
		}

		final Set<R> filtered = effectivePredicate == null ? source : Sets.filter(source, effectivePredicate);

		return Collections.unmodifiableSet(filtered);
	}

	@Override
	public int getValueCount() {
		int nbValues = 0;
		for (R alt : getRows()) {
			for (C crit : getColumns()) {
				if (getEntry(alt, crit) != null) {
					++nbValues;
				}
			}
		}
		return nbValues;
	}

	@Override
	public boolean isComplete() {
		for (R row : getRows()) {
			for (C column : getColumns()) {
				if (getEntry(row, column) == null) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return getValueCount() == 0;
	}

	Set<C> getFilteredColumns() {
		final Set<C> filtered;
		final Set<C> sourceCriteria = m_delegate.getColumns();
		if (m_columnPredicate == null) {
			filtered = sourceCriteria;
		} else {
			filtered = Sets.filter(sourceCriteria, m_columnPredicate);
		}
		return filtered;
	}

	@Override
	public Table<R, C, Double> asTable() {
//	throw new UnsupportedOperationException();
		return TablesFilters.filterCells(m_delegate.asTable(), new Predicate<Cell<R, C, Double>>() {
			@Override
			public boolean apply(Cell<R, C, Double> input) {
				return m_rowPredicate.apply(input.getRowKey()) && m_columnPredicate.apply(input.getColumnKey());
			}
		});
	}

	@Override
	public double getValue(R row, C column) {
		final Double entry = getEntry(row, column);
		if (entry == null) {
			throw new IllegalArgumentException("No value at " + row + ", " + column + ".");
		}
		return entry.doubleValue();
	}

}
