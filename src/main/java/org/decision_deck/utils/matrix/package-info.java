/**
 * Provides matrix interfaces and classes.
 * 
 * <p>
 * A sparse matrix may not be completely defined. There might be missing values
 * at some position specified by (row, column) even if the given row and the
 * given column both exist in the matrix. A row (a column) exists if and only if
 * there is at least one value associated to a position with this row (this
 * column) in the matrix.
 * </p>
 * 
 * <h2>Sparse matrix vs mathematical relation</h2>
 * <p>
 * A binary, or fuzzy, relation in the mathematical sense is a binary, or fuzzy,
 * sparse matrix which is square and complete. A sparse matrix is square iff its
 * row set equals its column set. A sparse matrix is complete iff it has a value
 * (which may be zero) for every combination of row, column. Completeness for a
 * sparse matrix is a different property than being complete or total for a
 * relation in the mathematical sense. We will use the term complete for a
 * sparse matrix and total for a relation to distinguish these concepts. A
 * relation over a set A is total (in the mathematical sense) iff for each a, b
 * in A, either aRb or bRA. Therefore, a binary, or fuzzy, matrix may be
 * complete and however not represent a total relation. A sparse binary, or
 * fuzzy, matrix which is not complete does not correspond to a partial binary,
 * or fuzzy, relation in the mathematical sense. This is because in a partial
 * binary relation, either aRb or not aRb. In a fuzzy relation, every (a, b)
 * should be given a membership value between 0 and 1. In constrast, a sparse
 * matrix provides a supplementary case where aRb is null, possibly interpreted
 * as &ldquo;unknown&rdquo; or &ldquo;not set yet&rdquo;.
 * </p>
 **/
package org.decision_deck.utils.matrix;
