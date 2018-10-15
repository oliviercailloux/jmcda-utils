package org.decision_deck.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharSink;

public class StringUtils {
	static public final class StringWriterSupplier extends CharSink {
		private final StringWriter m_writer;

		public StringWriterSupplier(StringWriter writer) {
			checkNotNull(writer);
			m_writer = writer;
		}

		public StringWriter getWriter() {
			return m_writer;
		}

		@Override
		public Writer openBufferedStream() throws IOException {
			return m_writer;
		}

		@Override
		public StringWriter openStream() throws IOException {
			return m_writer;
		}
	}

	private static final Logger s_logger = LoggerFactory.getLogger(StringUtils.class);

	public static String asIsoVariant(Date dateTime) {
		/**
		 * Variation from ISO8601 because colons in folder names are not
		 * accepted in (some?) Windows systems.
		 */
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss", Locale.ENGLISH);
		return dateFormat.format(dateTime);
	}

	static public String getAsUTF8(InputStream input) throws IOException, UnsupportedEncodingException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		ByteStreams.copy(input, output);
		final String asUtf8 = output.toString(Charsets.UTF_8.name());
		s_logger.info("As UTF-8: {}.", asUtf8);
		return asUtf8;
	}

	static public <K, V> EntryTransformer<? super K, ? super V, String> getEntryNamer(
			final Function<K, String> keyNamer, final Function<V, String> valueNamer, final String separator) {
		return new EntryTransformer<K, V, String>() {
			@Override
			public String transformEntry(K key, V value) {
				final String namedKey = keyNamer.apply(key);
				final String valueKey = valueNamer.apply(value);
				return namedKey + separator + valueKey;
			}
		};
	}

	static public <I> Function<Iterable<I>, String> getJoiner(final Function<I, String> toStringFunction,
			final String separator) {
		final Function<Iterable<I>, String> joiner = new Function<Iterable<I>, String>() {
			@Override
			public String apply(Iterable<I> input) {
				return "[" + Joiner.on(separator).join(Iterables.transform(input, toStringFunction)) + "]";
			}
		};
		return joiner;
	}

	/**
	 * Retrieves a string that is the given string with a first upper case
	 * letter and the rest in lower case. The ENGLISH locale is used.
	 *
	 * @param string
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public String getWithFirstCap(String string) {
		checkNotNull(string);
		final String beg = string.substring(0, 1);
		final String end = string.substring(1, string.length());
		return beg.toUpperCase(Locale.ENGLISH) + end.toLowerCase(Locale.ENGLISH);
	}

	static public boolean isInt(String string) {
		final Pattern integerPattern = Pattern.compile("^\\d*$");
		final Matcher matchesInteger = integerPattern.matcher(string);
		return matchesInteger.matches();
	}

	static public ByteArraysSupplier newByteArraysSupplier() {
		return new ByteArraysSupplier();
	}

	static public CharSink newStringWriterSupplier(StringWriter writer) {
		return new StringWriterSupplier(writer);
	}

	public static String toString(final Collection<?> collection, final int maxLen) {
		final StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (final Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0) {
				builder.append(", ");
			}
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	static public <K, V> String toStringMap(Map<? extends K, ? extends V> source, Function<K, String> keyNamer,
			final Function<V, String> valueNamer) {
		final EntryTransformer<? super K, ? super V, String> entryNamer = getEntryNamer(keyNamer, valueNamer, " - ");

		final Collection<String> strings = Maps.transformEntries(source, entryNamer).values();
		return "{" + Joiner.on("; ").join(strings) + "}";
	}

}
