package org.decision_deck.utils.matrix.mess;

import java.util.Set;

import org.decision_deck.utils.matrix.SparseMatrixD;
import org.decision_deck.utils.matrix.SparseMatrixDRead;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

public class FloatMatrixNew<R, C> extends FloatMatrixBase<R, C> implements SparseMatrixD<R, C> {

	public FloatMatrixNew() {
		super();
	}

	static public <R, C> FloatMatrixNew<R, C> create() {
		return new FloatMatrixNew<R, C>();
	}

	@Override
	public Double put(R row, C column, double value) {
		return doPut(row, column, value);
	}

	public void putAll(SparseMatrixDRead<R, C> f) {
		Table<R, C, Double> tableView;
		try {
			tableView = f.asTable();
		} catch (UnsupportedOperationException exc) {
			tableView = null;
		}
		if (tableView != null) {
			final Set<Cell<R, C, Double>> cellSet = tableView.cellSet();
			for (Cell<R, C, Double> entry1 : cellSet) {
				doPut(entry1.getRowKey(), entry1.getColumnKey(), entry1.getValue().doubleValue());
			}
		} else {
			for (R row : f.getRows()) {
				for (C col : f.getColumns()) {
					final Double entry = f.getEntry(row, col);
					if (entry == null) {
						continue;
					}
					final double value = entry.doubleValue();
					put(row, col, value);
				}
			}
		}
	}

	@Override
	public Double remove(R row, C column) {
		return super.remove(row, column);
	}

	@Override
	public boolean removeColumn(C column) {
		if (!getColumns().contains(column)) {
			return false;
		}
		for (R row : getRows()) {
			remove(row, column);
		}
		return true;
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

}
