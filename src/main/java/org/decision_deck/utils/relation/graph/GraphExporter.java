package org.decision_deck.utils.relation.graph;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.Map;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.VertexNameProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;

/**
 * @author Olivier Cailloux Utility class to export jgrapht-like graphs.
 */
public class GraphExporter {
	/**
	 * @author Olivier Cailloux
	 * 
	 *         Provides automatic ids to vertices: integers from zero to the number
	 *         of vertices.
	 * @param <V> the vertex type.
	 */
	static public class VertexAutoIdProvider<V> implements VertexNameProvider<V> {
		private final Map<V, Integer> m_ids;

		@SuppressWarnings("boxing")
		public VertexAutoIdProvider(Iterable<V> vertices) {
			int id = 0;
			m_ids = Maps.newLinkedHashMap();
			for (V v : vertices) {
				m_ids.put(v, id++);
			}
		}

		@Override
		public String getVertexName(V vertex) {
			final Integer id = m_ids.get(vertex);
			checkArgument(id != null);
			return String.valueOf(id);
		}
	}

	/**
	 * <p>
	 * An edge in a directed graph. Can also be viewed as a directed pair. None of
	 * the elements of the pair may be <code>null</code>. Note that this object
	 * represents an ordered pair, or a 2-tuple, which is different than an
	 * unordered pair or <em>couple</em> in French.
	 * </p>
	 * <p>
	 * Objects of this type are immutable <b>iff</b> the underlying objects are
	 * immutable. This object is designed for use with immutable underlying objects,
	 * and methods using this class may assume it is immutable.
	 * </p>
	 * 
	 * @author Olivier Cailloux
	 * 
	 * @param <V> the vertex type.
	 */
	static public class Edge<V> {

		static public class GetSource<V> implements Function<Edge<? extends V>, V> {
			@Override
			public V apply(Edge<? extends V> input) {
				return input.getSource();
			}

		}

		static public class GetTarget<V> implements Function<Edge<? extends V>, V> {
			@Override
			public V apply(Edge<? extends V> input) {
				return input.getTarget();
			}

		}

		private final V m_elt1;

		private final V m_elt2;

		private static final char RIGHT_ANGLE_BRACKET = '\u27E9';

		private static final char LEFT_ANGLE_BRACKET = '\u27E8';

		static public <V> Edge<V> create(V source, V target) {
			return new Edge<V>(source, target);
		}

		static public <V> Ordering<Edge<? extends V>> getLexicographicOrdering(Comparator<V> source,
				Comparator<V> target) {
			final GetSource<V> getSource = new GetSource<V>();
			final Ordering<Edge<? extends V>> first = Ordering.from(source).onResultOf(getSource);
			final Ordering<Edge<? extends V>> second = Ordering.from(target).onResultOf(new GetTarget<V>());
			final Ordering<Edge<? extends V>> compound = first.compound(second);
			return compound;
		}

		public Edge(V source, V target) {
			m_elt1 = source;
			m_elt2 = target;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Edge<?> other = (Edge<?>) obj;
			if (m_elt1 == null) {
				if (other.m_elt1 != null) {
					return false;
				}
			} else if (!m_elt1.equals(other.m_elt1)) {
				return false;
			}
			if (m_elt2 == null) {
				if (other.m_elt2 != null) {
					return false;
				}
			} else if (!m_elt2.equals(other.m_elt2)) {
				return false;
			}
			return true;
		}

		/**
		 * @return the first element of the pair. May be <code>null</code>.
		 */
		public V getSource() {
			return m_elt1;
		}

		/**
		 * @return the second element of the pair. May be <code>null</code>.
		 */
		public V getTarget() {
			return m_elt2;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((m_elt1 == null) ? 0 : m_elt1.hashCode());
			result = prime * result + ((m_elt2 == null) ? 0 : m_elt2.hashCode());
			return result;
		}

		@Override
		public String toString() {
			return LEFT_ANGLE_BRACKET + m_elt1.toString() + ", " + m_elt2 + RIGHT_ANGLE_BRACKET;

			/** This would fail on <code>null</code> elements. */
			// return getToStringFunction(Functions.toStringFunction(),
			// Functions.toStringFunction()).apply(this);
		}

		/**
		 * <p>
		 * Retrieves a function which, given an edge, gives its string form in the form
		 * of the transformation of the first element using the given function, a comma
		 * to separate them, and the transformation of the second element, surrounded by
		 * less than and greater than signs (to indicate a tuple). No <code>null</code>
		 * pairs are accepted by the function, but the elements themselves may be
		 * <code>null</code> iff the given transformation functions accept those.
		 * </p>
		 * <p>
		 * This provides an easy way to get short debug strings. E.g. to get a string
		 * representing the contents of a set of pairs of alternatives <em>s</em>, use
		 * <code>Joiner.on(", ").join(Iterables.transform(s, Edge.getToStringFunction(Alternative.getIdFct(), Alternative.getIdFct())))</code>
		 * .
		 * </p>
		 * 
		 * @param                <V> the vertex type.
		 * @param sourceToString a function which transforms the first element of a pair
		 *                       to a string.
		 * @param targetToString a function which transforms the second element of a
		 *                       pair to a string.
		 * 
		 * @return not <code>null</code>.
		 */
		static public <V> Function<Edge<V>, String> getToStringFunction(final Function<V, String> sourceToString,
				final Function<V, String> targetToString) {
			return new Function<Edge<V>, String>() {
				@Override
				public String apply(Edge<V> input) {
					final Function<Edge<? extends V>, V> fctElt1 = new Edge.GetSource<V>();
					final Function<Edge<? extends V>, V> fctElt2 = new Edge.GetTarget<V>();
					final Function<Edge<? extends V>, String> str1 = Functions.compose(sourceToString, fctElt1);
					final Function<Edge<? extends V>, String> str2 = Functions.compose(targetToString, fctElt2);
					/** Uses tuple notation, not set ('{' and '}'), as this is an ordered pair. */
					return LEFT_ANGLE_BRACKET + str1.apply(input) + ", " + str2.apply(input) + RIGHT_ANGLE_BRACKET;
				}
			};
		}
	}

	static public class SimpleEdgeFactory<V> implements EdgeFactory<V, Edge<V>> {
		@Override
		public Edge<V> createEdge(V sourceVertex, V targetVertex) {
			checkNotNull(sourceVertex);
			checkNotNull(targetVertex);
			return new Edge<V>(sourceVertex, targetVertex);
		}
	}

	/**
	 * @author Olivier Cailloux
	 * 
	 *         Provides automatic ids to vertices: integers from zero to the number
	 *         of vertices.
	 * @param <V> the vertex type.
	 */
	static public class VertexToStringNamer<V> implements VertexNameProvider<V> {
		public VertexToStringNamer() {
			/** Default public constructor. */
		}

		@Override
		public String getVertexName(Object vertex) {
			return vertex.toString();
		}
	}

	private static final Logger s_logger = LoggerFactory.getLogger(GraphExporter.class);
	private Function<Object, String> m_vertexNamer;

	public GraphExporter() {
		m_vertexNamer = Functions.toStringFunction();
	}

	/**
	 * <P>
	 * Converts the source characters, which must be in DOT format, to an SVG
	 * stream.
	 * </P>
	 * <P>
	 * Example use: <code>
	 * convertFile(Files.asCharSource(new File(dotFilename), Charsets.US_ASCII), Files.asCharSink(new File(svgFilename),
	 * Charsets.UTF_8));
	 * </code>
	 * </P>
	 * 
	 * @param from a stream in DOT format. Not <code>null</code>.
	 * @param to   the sink that will receive the SVG output. Not <code>null</code>.
	 * @throws IOException          if an I/O error occurs.
	 * @throws InterruptedException if the current thread is interrupted by another
	 *                              thread while it is waiting.
	 */
	public void convertDotToSvg(CharSource from, CharSink to) throws IOException, InterruptedException {
		checkNotNull(from);
		checkNotNull(to);

		Runtime rt = Runtime.getRuntime();
		// Process pr = rt.exec("dot -Tsvg " + "-o " + filenameBase + ".svg");
		Process pr = rt.exec(new String[] { "dot", "-Tsvg" });

		final OutputStream subProcessInputStream = pr.getOutputStream();
		final OutputStreamWriter subProcessWriter = new OutputStreamWriter(subProcessInputStream, Charsets.US_ASCII);
		from.copyTo(subProcessWriter);
		subProcessWriter.close();
		final int exitCode = pr.waitFor();

		final InputStream subProcessOutputStream = pr.getInputStream();
		final InputStreamReader subProcessOutputReader = new InputStreamReader(subProcessOutputStream, Charsets.UTF_8);
		to.writeFrom(subProcessOutputReader);
		// final Writer sinkStream = to.openStream();
		// CharStreams.copy(subProcessOutputReader, sinkStream);
		// sinkStream.close();
		subProcessOutputReader.close();

		final InputStream errorStream = pr.getErrorStream();
		final InputStreamReader errorReader = new InputStreamReader(errorStream, Charsets.UTF_8);
		final String error = CharStreams.toString(errorReader);
		errorReader.close();
		if (!error.isEmpty()) {
			s_logger.warn("Error: {}", error);
		}

		assert exitCode == 0;
	}

	/**
	 * <P>
	 * Exports a jgrapht-like graph to an SVG picture. This goes through dot
	 * exporting. To save to a file, use
	 * <code>Files.asCharSink(new File(filename), Charsets.US_ASCII)</code>.
	 * </P>
	 * <P>
	 * The <code>vertexNamer</code> will be used to name the vertices. The default
	 * is to call {@link #toString()} on vertices.
	 * </P>
	 * 
	 * @param from    not <code>null</code>.
	 * @param svgSink not <code>null</code>.
	 * @throws IOException          if an I/O error occurs.
	 * @throws InterruptedException if the current thread is interrupted by another
	 *                              thread while it is waiting for the dot export to
	 *                              proceed.
	 * @see {@link #setVertexNamer(Function)}.
	 */
	public <V, E> void export(Graph<V, E> from, CharSink svgSink) throws IOException, InterruptedException {
		checkNotNull(from);
		checkNotNull(svgSink);
		final VertexNameProvider<V> vertexNameProvider = new VertexNameProvider<V>() {
			@Override
			public String getVertexName(V vertex) {
				return m_vertexNamer.apply(vertex);
			}
		};
		final DOTExporter<V, E> exporter = new DOTExporter<V, E>(vertexNameProvider, null, null);
		final StringWriter w = new StringWriter();
		exporter.export(w, from);
		convertDotToSvg(CharSource.wrap(w.toString()), svgSink);
	}

	/**
	 * @return not <code>null</code>.
	 */
	public Function<Object, String> getVertexNamer() {
		return m_vertexNamer;
	}

	/**
	 * @param vertexNamer set to <code>null</code> to restore default behavior.
	 */
	public void setVertexNamer(Function<Object, String> vertexNamer) {
		if (vertexNamer == null) {
			m_vertexNamer = Functions.toStringFunction();
		}
		m_vertexNamer = vertexNamer;
	}

	static public boolean isFilenameValid(String filename) {
		final File f = new File(filename);
		try {
			f.getCanonicalPath();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
