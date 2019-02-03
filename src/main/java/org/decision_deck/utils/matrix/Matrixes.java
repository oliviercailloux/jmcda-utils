package org.decision_deck.utils.matrix;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.collect.Table.Cell;

/**
 * Various utility methods dealing with matrices, or as this class say,
 * “matrixes”. The alternative plural has been chosen because it allows this
 * class to be found when searching for the word “Matrix”.
 *
 * @author Olivier Cailloux
 *
 */
public class Matrixes {

	/**
	 * Returns a new matrix ensuring every element it contains satisfies the given
	 * predicate.
	 * 
	 * @param valuePredicate not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public <R, C> SparseMatrixD<R, C> newValidating(Predicate<Double> valuePredicate) {
		return ValidatingDecoratedMatrix.create(valuePredicate);
	}

	/**
	 * Two matrixes are “approximately equal” to a given degree of precision iff
	 * they contain values for the same mappings and the value they contain for each
	 * mapping are not more different than the given allowed imprecision.
	 * 
	 * may be <code>null</code>.
	 * 
	 * @param m1          may be <code>null</code>.
	 * @param m2          may be <code>null</code>.
	 * @param imprecision a number positive or nul, or positive infinity.
	 * @return <code>true</code> iff the given matrix is approximately equal to this
	 *         one.
	 */
	static public <R, C> boolean approxEqual(SparseMatrixDRead<R, C> m1, SparseMatrixDRead<R, C> m2,
			double imprecision) {
		checkArgument(imprecision >= 0);
		if (m1 == null || m2 == null) {
			return m1 == m2;
		}
		if (m1.getValueCount() != m2.getValueCount()) {
			return false;
		}
		/**
		 * This test is not required for correctness, but might be good for performance.
		 */
		if (m1.isComplete() != m2.isComplete()) {
			return false;
		}

		for (Cell<R, C, Double> entry1 : m1.asTable().cellSet()) {
			final double value1 = entry1.getValue().doubleValue();
			final Double entry2 = m2.getEntry(entry1.getRowKey(), entry1.getColumnKey());
			if (entry2 == null) {
				return false;
			}
			final double value2 = entry2.doubleValue();
			if (Math.abs(value2 - value1) > imprecision) {
				return false;
			}
		}
		return true;
	}

	static public <R, C> SparseMatrixD<R, C> newSparseD() {
		return new SparseMatrixDImpl<R, C>();
	}

	static public <R, C> SparseMatrixFuzzy<R, C> newSparseFuzzy(SparseMatrixDRead<R, C> source) {
		final SparseMatrixFuzzy<R, C> target = new SparseMatrixFuzzyImpl<R, C>();
		for (Cell<R, C, Double> sourceEntry : source.asTable().cellSet()) {
			target.put(sourceEntry.getRowKey(), sourceEntry.getColumnKey(), sourceEntry.getValue().doubleValue());
		}
		return target;
	}

	/**
	 * Retrieves the contents of the given matrix as a string.
	 * 
	 * @param        <R> the row type.
	 * @param        <C> the column type.
	 * @param source not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public <R, C> String toString(SparseMatrixDRead<R, C> source) {
		checkNotNull(source);
		return source.asTable().toString();
	}

	/**
	 * Retrieves a read-only view of the given delegate. The returned object is
	 * immutable iff the source is immutable.
	 * 
	 * @param          <R> the row type.
	 * @param          <C> the column type.
	 * @param delegate not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public <R, C> SparseMatrixDRead<R, C> getReadView(SparseMatrixDRead<R, C> delegate) {
		checkNotNull(delegate);
		return new ForwardingSparseMatrixRead<R, C>(delegate);
	}

	static public <R, C> SparseMatrixD<R, C> newSparseD(SparseMatrixDRead<R, C> source) {
		final SparseMatrixD<R, C> target = newSparseD();
		putAll(source, target);
		return target;
	}

	static public <R, C> void putAll(SparseMatrixDRead<R, C> source, SparseMatrixD<R, C> target) {
		/** Table view is not implemented everywhere yet. */
		// final Set<Cell<R, C, Double>> cells = source.asTable().cellSet();
		// for (Cell<R, C, Double> cell : cells) {
		// target.put(cell.getRowKey(), cell.getColumnKey(),
		// cell.getValue().doubleValue());
		// }
		for (R row : source.getRows()) {
			for (C column : source.getColumns()) {
				final Double entry = source.getEntry(row, column);
				if (entry != null) {
					target.put(row, column, entry.doubleValue());
				}
			}
		}
	}

	static public <R, C> SparseMatrixFuzzy<R, C> newSparseFuzzy() {
		return new SparseMatrixFuzzyImpl<R, C>();
	}

	/**
	 * (Old implementation, probably to delete.) Does not use the matrixes
	 * definition of approxEquals, so that this method may be used in implementing
	 * objects.
	 * 
	 * @param             <Row> the row type.
	 * @param             <Column> the column type.
	 * 
	 * @param m1          not <code>null</code>.
	 * @param m2          may be <code>null</code> (then returns
	 *                    <code>false</code>).
	 * @param imprecision the allowed imprecision.
	 * @return <code>true</code> iff both contents have values at the same positions
	 *         and these values are not further than the given imprecision.
	 */
	@SuppressWarnings("unused")
	private static <Row, Column> boolean approxEquals(SparseMatrixDRead<Row, Column> m1,
			SparseMatrixDRead<Row, Column> m2, double imprecision) {
		if (m2 == null) {
			return false;
		}
		if (m1.getValueCount() != m2.getValueCount()) {
			return false;
		}
		if (!m1.getRows().equals(m2.getRows())) {
			return false;
		}
		if (!m1.getColumns().equals(m2.getColumns())) {
			return false;
		}
		if (m1.isComplete() != m2.isComplete()) {
			return false;
		}
		for (final Row row : m1.getRows()) {
			for (final Column col : m1.getColumns()) {
				final Double entry1 = m1.getEntry(row, col);
				final Double entry2 = m2.getEntry(row, col);
				if (entry1 == null && entry2 == null) {
					continue;
				}
				if ((entry1 == null) || (entry2 == null)) {
					return false;
				}
				final double value1 = entry1.doubleValue();
				final double value2 = entry2.doubleValue();
				if (Math.abs(value2 - value1) > imprecision) {
					return false;
				}
			}
		}
		return true;
	}

	static public <R, C> boolean containsRow(SparseMatrixDRead<R, C> evaluations, Map<C, Double> row) {
		checkNotNull(evaluations);
		checkNotNull(row);
		final Map<R, Map<C, Double>> map = evaluations.asTable().rowMap();
		return map.containsValue(row);
	}

	/**
	 * <p>
	 * Retrieves the set of rows which exist in both matrices but are discording.
	 * Two rows are discording iff they do not have the same set of column and value
	 * mapping.
	 * </p>
	 * <p>
	 * As a consequence of this definition, if the sets of rows are disjoint, this
	 * method returns an empty set.
	 * </p>
	 * 
	 * @param matrix1 not <code>null</code>.
	 * @param matrix2 not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public <R, C> Set<R> getDiscordingRows(SparseMatrixDRead<R, C> matrix1, SparseMatrixDRead<R, C> matrix2) {
		checkNotNull(matrix1);
		checkNotNull(matrix2);
		final Set<R> duplicates = Sets.newLinkedHashSet();
		final SetView<R> common = Sets.intersection(matrix1.getRows(), matrix2.getRows());
		for (R row : common) {
			final Map<C, Double> mapping1 = matrix1.asTable().rowMap().get(row);
			final Map<C, Double> mapping2 = matrix2.asTable().rowMap().get(row);
			if (!mapping1.equals(mapping2)) {
				duplicates.add(row);
			}
		}
		return duplicates;
	}

	/**
	 * Retrieves a new matrix which contains values of the first one and of the
	 * second one. If both matrix have a value defined for a given position, and
	 * these values are different, it is an error and an exception is thrown.
	 * 
	 * @param m1 not <code>null</code>.
	 * @param m2 not <code>null</code>.
	 * @return a copy of all entries.
	 */
	static public <R, C> SparseMatrixD<R, C> merge(SparseMatrixDRead<R, C> m1, SparseMatrixDRead<R, C> m2) {
		checkNotNull(m1);
		checkNotNull(m2);
		return new MatrixesHelper<R, C>().merge(m1, m2);
	}

	/**
	 * Returns a matrix containing the mappings in <code>unfiltered</code>
	 * satisfying the given predicates. The returned matrix is a live view of
	 * <code>unfiltered</code>; changes to the source affects the view.
	 * 
	 * @param unfiltered      not <code>null</code>.
	 * @param rowPredicate    not <code>null</code>, use
	 *                        {@link Predicates#alwaysTrue()} for no restriction.
	 * @param columnPredicate not <code>null</code>, use
	 *                        {@link Predicates#alwaysTrue()} for no restriction.
	 * @return not <code>null</code>, a read-only view.
	 */
	static public <R, C> SparseMatrixDRead<R, C> getFilteredView(SparseMatrixDRead<R, C> unfiltered,
			Predicate<R> rowPredicate, Predicate<C> columnPredicate) {
		checkNotNull(unfiltered);
		return new SparseMatrixDFiltered<R, C>(unfiltered, rowPredicate, columnPredicate);
	}

	/**
	 * Checks that the matrix is such that m(a, b) + m(b, a) ≤ maxValue. The matrix
	 * must be complete and square.
	 * 
	 * @param          <T> the type of rows and columns.
	 * @param matrix   not <code>null</code>.
	 * @param maxValue infinite values are allowed.
	 * @return <code>true</code> iff there exists a pair (a, b) such that the entry
	 *         at (a, b) plus the value at (b, a) is strictly greater than one.
	 */
	public static <T> boolean symMax(SparseMatrixDRead<T, T> matrix, double maxValue) {
		checkNotNull(matrix);
		checkArgument(!Double.isNaN(maxValue));
		for (T row : matrix.getRows()) {
			for (T col : matrix.getColumns()) {
				final Double e1 = matrix.getEntry(row, col);
				if (e1 == null) {
					throw new IllegalStateException("Missing entry at " + row + ", " + col + ".");
				}
				final double val = e1.doubleValue();
				final Double e2 = matrix.getEntry(col, row);
				if (e2 == null) {
					throw new IllegalStateException("Missing entry at " + col + ", " + row + ".");
				}
				final double val2 = e2.doubleValue();
				if (val + val2 > maxValue) {
					return false;
				}
			}
		}
		return true;
	}
}
