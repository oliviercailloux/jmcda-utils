package org.decision_deck.utils.collection.extensional_order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;

import org.decision_deck.utils.collection.CollectionUtils;
import org.junit.Test;

import com.google.common.collect.Maps;

public class KeyValueOrderedMapTest {
	@SuppressWarnings("boxing")
	@Test
	public void testRemove() throws Exception {
		final Comparator<Entry<String, Double>> comparator = new Comparator<Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		};
		final KeyValueOrderedMap<String, Double> map = new KeyValueOrderedMap<String, Double>(comparator,
				Maps.<String, Double>newHashMap());
		map.put("top", 100d);
		map.put("bottom", 0d);
		map.put("average", 50d);
		map.remove("top");
		assertFalse(map.containsKey("top"));
		assertFalse(map.containsValue(100d));
		final Iterator<Double> valuesIterator = map.values().iterator();
		assertEquals(0d, valuesIterator.next().doubleValue(), 1e-6);
		assertEquals(50d, valuesIterator.next().doubleValue(), 1e-6);
		assertFalse(valuesIterator.hasNext());

		final Iterator<String> keysIterator = map.keySet().iterator();
		assertEquals("bottom", keysIterator.next());
		assertEquals("average", keysIterator.next());
		assertFalse(keysIterator.hasNext());
	}

	@SuppressWarnings("boxing")
	@Test
	public void testMap() throws Exception {
		final Comparator<Entry<String, Double>> comparator = new Comparator<Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		};
		final KeyValueOrderedMap<String, Double> map = new KeyValueOrderedMap<String, Double>(comparator,
				Maps.<String, Double>newHashMap());
		assertTrue(map.isEmpty());
		assertFalse(map.containsKey("top"));
		assertEquals(map.size(), 0);
		map.put("top", 100d);
		assertFalse(map.isEmpty());
		assertTrue(map.containsKey("top"));
		assertFalse(map.containsKey("bottom"));
		assertEquals(map.size(), 1);
		map.put("bottom", 0d);
		assertEquals(map.size(), 2);
		assertTrue(map.containsKey("top"));
		assertTrue(map.containsKey("bottom"));
		assertEquals(map.entrySet().size(), 2);
		assertEquals(map.keySet().size(), 2);
		assertEquals(map.values().size(), 2);
		final Iterator<Entry<String, Double>> entriesIterator = map.entrySet().iterator();
		assertEquals(CollectionUtils.newEntry("bottom", 0d), entriesIterator.next());
		assertEquals(CollectionUtils.newEntry("top", 100d), entriesIterator.next());
		assertFalse(entriesIterator.hasNext());

		map.put("average", 50d);
		final Iterator<Double> valuesIterator = map.values().iterator();
		assertEquals(0d, valuesIterator.next().doubleValue(), 1e-6);
		assertEquals(50d, valuesIterator.next().doubleValue(), 1e-6);
		assertEquals(100d, valuesIterator.next().doubleValue(), 1e-6);
		assertFalse(valuesIterator.hasNext());

		final Iterator<String> keysIterator = map.keySet().iterator();
		assertEquals("bottom", keysIterator.next());
		assertEquals("average", keysIterator.next());
		assertEquals("top", keysIterator.next());
		assertFalse(keysIterator.hasNext());
	}

	/**
	 * Tests if changing a value, hence, changing the order, introduces no error.
	 * 
	 * @throws Exception heh
	 */
	@SuppressWarnings("boxing")
	@Test
	public void testDuplicate() throws Exception {
		final Comparator<Entry<String, Double>> comparator = new Comparator<Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		};
		final KeyValueOrderedMap<String, Double> map = new KeyValueOrderedMap<String, Double>(comparator,
				Maps.<String, Double>newHashMap());
		map.put("1", 1d);
		map.put("1", 2d);
		map.put("3", 3d);
		map.put("5", 5d);
		map.put("4", 4d);
		map.put("1", 1d);
		map.put("4", 4d);
		map.put("5", 1.5d);
		assertTrue(map.containsKey("5"));
		assertTrue(map.containsKey("4"));
		assertTrue(map.containsKey("3"));
		assertTrue(map.containsKey("1"));
		assertEquals(4, map.entrySet().size());
	}
}
