package org.decision_deck.utils.collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.decision_deck.utils.collection.MapEvents.AdditionEvent;
import org.decision_deck.utils.collection.MapEvents.AdditionNewKeyEvent;
import org.decision_deck.utils.collection.MapEvents.PreRemovalEvent;
import org.junit.Test;

import com.google.common.eventbus.Subscribe;

@SuppressWarnings("boxing")
public class ObservableNavigableMapTest {
	private boolean m_seen = false;

	@Test
	public void testRemove() {
		final ObservableNavigableMap<String, Double> map = new ObservableNavigableMap<String, Double>();
		final Object observer = getObserver();
		map.register(observer);
		map.put("s1", 1d);
		assertFalse(m_seen);
		map.remove("sNONE");
		assertFalse(m_seen);
		map.remove("s1");
		assertTrue(m_seen);
	}

	Object getObserver() {
		@SuppressWarnings("unused")
		final Object observer = new Object() {
			@Subscribe
			public void dontSeeEvent(AdditionEvent<String, Double> event) {
				m_seen = false;
			}

			@Subscribe
			public void seeEvent(PreRemovalEvent<String, Double> event) {
				m_seen = true;
			}
		};
		return observer;
	}

	@Test
	public void testAddNew() {
		final ObservableNavigableMap<String, Double> map = new ObservableNavigableMap<String, Double>();
		map.register(new Object() {
			@SuppressWarnings("unused")
			@Subscribe
			public void seeEvent(AdditionNewKeyEvent<String, Double> event) {
				m_seen = true;
			}
		});
		map.put("s1", 1d);
		assertTrue(m_seen);
	}

	@Test
	public void testAdd() {
		final ObservableNavigableMap<String, Double> map = new ObservableNavigableMap<String, Double>();
		map.register(new Object() {
			@SuppressWarnings("unused")
			@Subscribe
			public void seeEvent(AdditionEvent<String, Double> event) {
				m_seen = true;
			}
		});
		map.put("s1", 1d);
		assertTrue(m_seen);
	}

	@Test
	public void testNavigate() {
		final ObservableNavigableMap<String, Double> map = new ObservableNavigableMap<String, Double>();
		map.put("s1", 1d);
		map.put("s3", 3d);
		map.put("s2", 2d);
		assertTrue(map.firstKey().equals("s1"));
		assertTrue(map.descendingMap().firstKey().equals("s3"));
		assertTrue(map.descendingMap().descendingMap().firstKey().equals("s1"));
		assertTrue(map.lastKey().equals("s3"));
		assertTrue(map.descendingMap().lastKey().equals("s1"));
		assertTrue(map.descendingMap().descendingMap().lastKey().equals("s3"));
	}

}
