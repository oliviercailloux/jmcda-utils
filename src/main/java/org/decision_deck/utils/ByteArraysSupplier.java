package org.decision_deck.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.google.common.io.ByteSink;

public class ByteArraysSupplier extends ByteSink {
	private final Set<ByteArrayOutputStream> m_arrays = Sets.newLinkedHashSet();

	/**
	 * Retrieves the arrays created on demand by this object, as a read-only
	 * view. The byte arrays in the returned set are writable (but they are not
	 * intended to be written). The iteration order of the returned collection
	 * matches the order of the requested outputs.
	 *
	 * @return not <code>null</code>.
	 */
	public Collection<ByteArrayOutputStream> getArrays() {
		return Collections.unmodifiableSet(m_arrays);
	}

	public Collection<String> getWrittenStrings(final Charset charset) {
		return Collections2.transform(m_arrays, new Function<ByteArrayOutputStream, String>() {
			@Override
			public String apply(ByteArrayOutputStream input) {
				try {
					return input.toString(charset.name());
				} catch (UnsupportedEncodingException exc) {
					throw new IllegalStateException(exc);
				}
			}
		});
	}

	@Override
	public ByteArrayOutputStream openStream() throws IOException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		m_arrays.add(output);
		return output;
	}
}