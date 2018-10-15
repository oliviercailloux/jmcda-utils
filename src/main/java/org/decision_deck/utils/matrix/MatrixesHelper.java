package org.decision_deck.utils.matrix;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Sets;

/**
 * Contains helper methods for dealing with matrixes. Contrary to {@link Matrixes}, this class must be instanciated and
 * provided with a factory.
 * 
 * @author Olivier Cailloux
 * 
 * @param <R>
 *            the row type of the matrixes that this instance will work with.
 * @param <C>
 *            the column type of the matrixes that this instance will work with.
 */
public class MatrixesHelper<R, C> {
    private final MatrixFactory<R, C> m_matrixFactory;

    public interface MatrixFactory<R, C> {
	public SparseMatrixD<R, C> newMatrix();
    }

    public MatrixesHelper() {
	this(new MatrixFactory<R, C>() {
	    @Override
	    public SparseMatrixD<R, C> newMatrix() {
		return Matrixes.newSparseD();
	    }
	});
    }

    public MatrixesHelper(MatrixFactory<R, C> matrixFactory) {
	m_matrixFactory = matrixFactory;
    }

    /**
     * Retrieves a new matrix which contains values of the first one and of the second one. If both matrix have a value
     * defined for a given position, and these values are different, it is an error and an exception is thrown.
     * 
     * @param m1
     *            not <code>null</code>.
     * @param m2
     *            not <code>null</code>.
     * @return a copy of all entries.
     */
    public SparseMatrixD<R, C> merge(SparseMatrixDRead<R, C> m1, SparseMatrixDRead<R, C> m2) {
	checkNotNull(m1);
	checkNotNull(m2);
	final SparseMatrixD<R, C> merged = m_matrixFactory.newMatrix();
	for (R row : Sets.union(m1.getRows(), m2.getRows())) {
	    for (C column : Sets.union(m1.getColumns(), m2.getColumns())) {
		final Double entry1 = m1.getEntry(row, column);
		final Double entry2 = m2.getEntry(row, column);
		if (entry1 != null && entry2 != null) {
		    if (entry1.doubleValue() != entry2.doubleValue()) {
			throw new IllegalArgumentException("Different values at position " + row + ", " + column + ".");
		    }
		    merged.put(row, column, entry1.doubleValue());
		} else if (entry1 != null) {
		    merged.put(row, column, entry1.doubleValue());
		} else if (entry2 != null) {
		    merged.put(row, column, entry2.doubleValue());
		}
	    }
	}
	return merged;
    }

}
