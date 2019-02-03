package org.decision_deck.utils.matrix.mess;

import java.util.Collection;
import java.util.Set;

import org.decision_deck.utils.collection.extensional_order.ExtensionalComparator;
import org.decision_deck.utils.matrix.SparseMatrixD;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

/**
 * A decorator around an other matrix, which adds ordering capability to the
 * decorated matrix. Two use cases are possible for this object. The simplest
 * one is to avoid sharing the references to its underlying matrix and orders
 * and let this object manage the adequacy between the ordering and the matrix.
 * A second use case is possible to allow sharing references. In that case, the
 * user must always ensure that the rows and columns order contain all rows and
 * columns used in the matrix underlying this object. The contracts for the
 * various methods suppose the user fulfills this condition, otherwise this
 * object behavior is unspecified.
 * 
 * @author Olivier Cailloux
 * 
 * @param <R> the row type.
 * @param <C> the column type.
 */
public class OrderedMatrix<R, C> extends FloatMatrixForwarder<R, C> implements SparseMatrixD<R, C> {

	public static class OrderDecoratedSetNoAdditions<R> extends OrderDecoratedSet<R> {
		public OrderDecoratedSetNoAdditions(Set<R> delegate) {
			super(delegate);
		}

		@Override
		public boolean add(R element) {
			throw new UnsupportedOperationException("This set does not support adding elements.");
		}

		@Override
		public boolean addAll(Collection<? extends R> collection) {
			throw new UnsupportedOperationException("This set does not support adding elements.");
		}

		@Override
		public void addAsBest(R e) {
			throw new UnsupportedOperationException("This set does not support adding elements.");
		}

		@Override
		public void addAfter(R previous, R toAdd) {
			throw new UnsupportedOperationException("This set does not support adding elements.");
		}

		@Override
		public void addAsWorst(R e) {
			throw new UnsupportedOperationException("This set does not support adding elements.");
		}
	}

	/**
	 * Must contain all rows in the matrix. May contain rows that are not in the
	 * matrix.
	 */
	private final OrderDecoratedSet<R> m_rowOrder;
	/**
	 * See row order.
	 */
	private final OrderDecoratedSet<C> m_columnOrder;

	/**
	 * <p>
	 * Decorates a matrix by adding ordering capability. By default, the iteration
	 * order of the delegate is used.
	 * </p>
	 * 
	 * @param delegate not <code>null</code>.
	 * @see #create
	 */
	public OrderedMatrix(SparseMatrixD<R, C> delegate) {
		super(delegate);
		m_rowOrder = new OrderDecoratedSetNoAdditions<R>(delegate.getRows());
		m_columnOrder = new OrderDecoratedSetNoAdditions<C>(delegate.getColumns());
	}

	/**
	 * These references may be shared, in which case the user of this object must
	 * make sure the row and column orders always contain all rows and columns used
	 * in the given matrix.
	 * 
	 * @param rowOrder not <code>null</code>.
	 */
	public void setRowOrder(ExtensionalComparator<R> rowOrder) {
		m_rowOrder.setSubsetComparator(rowOrder);
	}

	public OrderDecoratedSet<R> getRowOrder() {
		return m_rowOrder;
	}

	/**
	 * Retrieves a view of the column order used by this object. Changing the
	 * returned order changes the order this object uses and should thus be done
	 * with caution.
	 * 
	 * @return an ordering containing at least all columns in this matrix.
	 */
	public OrderDecoratedSet<C> getColumnOrder() {
		return m_columnOrder;
	}

	static public <R, C> OrderedMatrix<R, C> create(SparseMatrixD<R, C> delegate) {
		return new OrderedMatrix<R, C>(delegate);
	}

	public SparseMatrixD<R, C> getDelegate() {
		return delegate();
	}

	@Override
	public Double put(R row, C column, double value) {
		if (!m_rowOrder.contains(row)) {
			m_rowOrder.addAsBest(row);
		}
		if (!m_columnOrder.contains(column)) {
			m_columnOrder.addAsBest(column);
		}
		return delegate().put(row, column, value);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method removes the information from the matrix <em>and</em> from the row
	 * or column ordering if the object was the last in a given row or column.
	 * Adding the object back later will thus require to specify its position again.
	 * </p>
	 */
	@Override
	public Double remove(R row, C column) {
		final Double removed = delegate().remove(row, column);
		if (removed != null) {
			if (!getRows().contains(row)) {
				m_rowOrder.remove(row);
			}
			if (!getColumns().contains(column)) {
				m_columnOrder.remove(column);
			}
		}
		return removed;
	}

	@Override
	public String toString() {
		final ToStringHelper helper = Objects.toStringHelper(this);
		helper.add("Row order", m_rowOrder);
		helper.add("Column order", m_columnOrder);
		helper.add("Contents", delegate());
		return helper.toString();
	}

	@Override
	public Set<C> getColumns() {
		return Sets.intersection(m_columnOrder, delegate().getColumns());
	}

	@Override
	public Set<R> getRows() {
		return Sets.intersection(m_rowOrder, delegate().getRows());
	}

	@Override
	public Table<R, C, Double> asTable() {
		return OrderedTable.create(delegate().asTable(), m_rowOrder, m_columnOrder);
	}
}
