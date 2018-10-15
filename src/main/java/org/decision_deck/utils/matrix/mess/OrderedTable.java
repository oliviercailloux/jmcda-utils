package org.decision_deck.utils.matrix.mess;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ForwardingTable;
import com.google.common.collect.Table;

public class OrderedTable<R, C, V> extends ForwardingTable<R, C, V> implements Table<R, C, V> {

    private final Table<R, C, V> m_delegate;
    private final OrderDecoratedSet<R> m_rowOrder;
    private final OrderDecoratedSet<C> m_columnOrder;

    public OrderedTable(Table<R, C, V> delegate, OrderDecoratedSet<R> rowOrder, OrderDecoratedSet<C> columnOrder) {
	m_delegate = delegate;
	m_rowOrder = rowOrder;
	m_columnOrder = columnOrder;
    }

    static public <R, C, V> OrderedTable<R, C, V> create(Table<R, C, V> delegate, OrderDecoratedSet<R> rowOrder,
	    OrderDecoratedSet<C> columnOrder) {
	return new OrderedTable<R, C, V>(delegate, rowOrder, columnOrder);
    }

    @Override
    protected Table<R, C, V> delegate() {
	return m_delegate;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Iteration order is the order of the delegate and does not take the order defined in this object into account. The
     * underlying table should provide guarantees of iteration order, otherwise the implementation in correct order
     * would be very inefficient. (To do: ask Google.)
     * </p>
     * <p>
     * To do: override remove (and iterator remove), currently broken because does not remove the object in the order.
     * </p>
     */
    @Override
    public Set<Table.Cell<R, C, V>> cellSet() {
	// final OrderDecoratedSet<Table.Cell<R,C,V>> orderDecoratedSet = new
	// OrderDecoratedSet<Table.Cell<R,C,V>>(m_delegate.cellSet());
	// orderDecoratedSet.setOrder(m_rowOrder);
	return m_delegate.cellSet();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Uses the delegate iteration order. Making the returned map use the order set in this object is not implemented,
     * but could be in the future.
     * </p>
     * <p>
     * To do: override remove (and iterator remove), currently broken because does not remove the object in the order.
     * </p>
     */
    @Override
    public Map<R, V> column(C columnKey) {
	return m_delegate.column(columnKey);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Uses the iteration order set in this object. Changing the iteration order in this object also changes the
     * iteration order of the returned view.
     * </p>
     */
    @Override
    public Set<C> columnKeySet() {
	final OrderDecoratedSet<C> ordered = OrderDecoratedSet.create(m_delegate.columnKeySet());
	ordered.setOrder(m_columnOrder);
	return ordered;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Uses the delegate iteration order. Making the returned map use the order set in this object is not implemented,
     * but could be in the future.
     * </p>
     * <p>
     * To do: override remove (and iterator remove), currently broken because does not remove the object in the order.
     * </p>
     */
    @Override
    public Map<C, Map<R, V>> columnMap() {
	return m_delegate.columnMap();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Uses the delegate iteration order. Making the returned map use the order set in this object is not implemented,
     * but could be in the future.
     * </p>
     * <p>
     * To do: override remove (and iterator remove), currently broken because does not remove the object in the order.
     * </p>
     */
    @Override
    public Map<C, V> row(R rowKey) {
	return m_delegate.row(rowKey);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Uses the iteration order set in this object. Changing the iteration order in this object also changes the
     * iteration order of the returned view.
     * </p>
     */
    @Override
    public Set<R> rowKeySet() {
	final OrderDecoratedSet<R> ordered = OrderDecoratedSet.create(m_delegate.rowKeySet());
	ordered.setOrder(m_rowOrder);
	return ordered;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Uses the delegate iteration order. Making the returned map use the order set in this object is not implemented,
     * but could be in the future.
     * </p>
     * <p>
     * TODO override remove (and iterator remove), currently broken because does not remove the object in the order.
     * </p>
     */
    @Override
    public Map<R, Map<C, V>> rowMap() {
	return m_delegate.rowMap();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Iteration order is the order of the delegate and does not take the order defined in this object into account.
     * </p>
     * <p>
     * To do: override remove (and iterator remove), currently broken because does not remove the object in the order.
     * </p>
     */
    @Override
    public Collection<V> values() {
	return m_delegate.values();
    }

}
