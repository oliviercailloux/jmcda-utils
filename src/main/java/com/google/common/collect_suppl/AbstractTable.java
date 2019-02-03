package com.google.common.collect_suppl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Table;

/**
 * <p>
 * Own copy of a patch from http://codereview.appspot.com/4425056/, not
 * integrated to Guava yet.
 * </p>
 * A skeleton implementation of a {@link Table}.
 * 
 * @author Louis Wasserman
 */
abstract class AbstractTable<R, C, V> implements Table<R, C, V> {

	@Override
	public boolean containsRow(Object rowKey) {
		return rowKeySet().contains(rowKey);
	}

	@Override
	public boolean containsColumn(Object columnKey) {
		return columnKeySet().contains(columnKey);
	}

	@Override
	public boolean containsValue(Object value) {
		return values().contains(value);
	}

	@Override
	public boolean isEmpty() {
		return cellSet().isEmpty();
	}

	@Override
	public int size() {
		return cellSet().size();
	}

	@Override
	public String toString() {
		return rowMap().toString();
	}

	@Override
	public void clear() {
		cellSet().clear();
	}

	@Override
	public Set<R> rowKeySet() {
		return rowMap().keySet();
	}

	@Override
	public Set<C> columnKeySet() {
		return columnMap().keySet();
	}

	private transient Set<Cell<R, C, V>> cellSet;

	abstract Set<Cell<R, C, V>> createCellSet();

	@Override
	public Set<Cell<R, C, V>> cellSet() {
		return (cellSet == null) ? cellSet = createCellSet() : cellSet;
	}

	private transient Collection<V> values;

	Collection<V> createValues() {
		return Collections2.transform(cellSet(), new Function<Cell<R, C, V>, V>() {
			@Override
			public V apply(Cell<R, C, V> input) {
				return input.getValue();
			}
		});
	}

	@Override
	public Collection<V> values() {
		return (values == null) ? values = createValues() : values;
	}

	private transient Map<R, Map<C, V>> rowMap;

	abstract Map<R, Map<C, V>> createRowMap();

	@Override
	public Map<R, Map<C, V>> rowMap() {
		return (rowMap == null) ? rowMap = createRowMap() : rowMap;
	}

	private transient Map<C, Map<R, V>> columnMap;

	abstract Map<C, Map<R, V>> createColumnMap();

	@Override
	public Map<C, Map<R, V>> columnMap() {
		Map<C, Map<R, V>> result = columnMap;
		return (result == null) ? columnMap = createColumnMap() : result;
	}
}