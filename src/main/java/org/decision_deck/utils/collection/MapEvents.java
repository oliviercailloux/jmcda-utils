package org.decision_deck.utils.collection;

public class MapEvents {

	/**
	 * This event indicates that an addition, in the large sense, just occurred. It
	 * is either an addition of a new key with an associated value, or a change of
	 * the value associated with a key that the map already contained.
	 * 
	 * @author Olivier Cailloux
	 * @param <K> the key type
	 * @param <V> the value type
	 * 
	 */
	static public class AdditionEvent<K, V> {
		final private K m_key;
		final private V m_value;

		public K getKey() {
			return m_key;
		}

		public V getValue() {
			return m_value;
		}

		public AdditionEvent(K key, V value) {
			m_key = key;
			m_value = value;
		}
	}

	/**
	 * <p>
	 * This event indicates that an addition just occurred. It is necessarily an
	 * addition of a new key with an associated value. This event does <em>not</em>
	 * represent a change of the value associated with a key that the map already
	 * contained.
	 * </p>
	 * <p>
	 * Note that a ChangeEvent should be added to complement this one (not
	 * implemented yet).
	 * </p>
	 * 
	 * @author Olivier Cailloux
	 * 
	 * @param <K> the key type
	 * @param <V> the value type
	 */
	static public class AdditionNewKeyEvent<K, V> extends AdditionEvent<K, V> {

		public AdditionNewKeyEvent(K key, V value) {
			super(key, value);
		}
	}

	static public class ClearEvent<K, V> {
		public ClearEvent() {
			/** Nothing to do. */
		}
	}

	/**
	 * Indicates an event that just happened, one among four possibilities: cleared;
	 * just added (new key and value); just removed; changed (replaced value bound
	 * to existing key with a new value). Currently unused, to delete?
	 * 
	 * @author Olivier Cailloux
	 * 
	 * @param <K> the key type.
	 * @param <V> the value type.
	 */
	@SuppressWarnings("unused")
	static public class MapEvent<K, V> {
		final private K m_keyAdded;
		final private V m_valueAdded;
		final private K m_keyRemoved;
		final private V m_valueRemoved;
		/**
		 * Cleared iff added and removed are false. If added and removed, it means a
		 * change.
		 */
		final private boolean m_added;
		final private boolean m_removed;

		MapEvent(boolean added, boolean removed, K ka, V va, K kr, V vr) {
			m_added = added;
			m_removed = removed;
			m_keyAdded = ka;
			m_valueAdded = va;
			m_keyRemoved = kr;
			m_valueRemoved = vr;
		}

		/**
		 * This event must represent an addition.
		 * 
		 * @return the key that was just added.
		 */
		public K getAddedKey() {
			return m_keyAdded;
		}

		/**
		 * Think about added value, they said. This event must represent an addition or
		 * a change.
		 * 
		 * @return the value that was just added.
		 */
		public V getAddedValue() {
			return m_valueAdded;
		}

		static public <K, V> MapEvent<K, V> cleared() {
			final MapEvent<K, V> event = new MapEvent<K, V>(false, false, null, null, null, null);
			return event;
		}
	}

	/**
	 * This event indicates that an addition, in the large sense, is about to occur.
	 * It is either an addition of a new key with an associated value, or a change
	 * of the value associated with a key that the map already contained.
	 * 
	 * @author Olivier Cailloux
	 * 
	 * @param <K> the key type
	 * @param <V> the value type
	 */
	static public class PreAdditionEvent<K, V> {
		final private K m_key;
		final private V m_value;

		public K getKey() {
			return m_key;
		}

		public V getValue() {
			return m_value;
		}

		public PreAdditionEvent(K key, V value) {
			m_key = key;
			m_value = value;
		}
	}

	/**
	 * This event indicates that an addition is about to occur. It is necessarily an
	 * addition of a new key with an associated value. This event does <em>not</em>
	 * represent a change of the value associated with a key that the map already
	 * contained.
	 * 
	 * @author Olivier Cailloux
	 * 
	 * @param <K> the key type
	 * @param <V> the value type
	 */
	static public class PreAdditionNewKeyEvent<K, V> extends PreAdditionEvent<K, V> {

		public PreAdditionNewKeyEvent(K key, V value) {
			super(key, value);
		}
	}

	static public class PreClearEvent<K, V> {
		public PreClearEvent() {
			/** Nothing to do. */
		}
	}

	static public class PreRemovalEvent<K, V> {
		final private K m_key;
		final private V m_value;

		public K getKey() {
			return m_key;
		}

		public V getValue() {
			return m_value;
		}

		public PreRemovalEvent(K key, V value) {
			m_key = key;
			m_value = value;
		}
	}

	/**
	 * Represents one unique removal, thus a removal produced by a remove call, not
	 * a removal produced by a call to {@link ObservableMap#clear()}.
	 * 
	 * @author Olivier Cailloux
	 * 
	 * @param <K> the key type.
	 * @param <V> the value type.
	 */
	static public class PreUniqueRemovalEvent<K, V> extends PreRemovalEvent<K, V> {
		public PreUniqueRemovalEvent(K key, V value) {
			super(key, value);
		}
	}

	/**
	 * Represents one unique removal, thus a removal produced by a remove call or a
	 * change (thus a put call), not a removal produced by a call to
	 * {@link ObservableMap#clear()}.
	 * 
	 * @author Olivier Cailloux
	 * 
	 * @param <K> the key type.
	 * @param <V> the value type.
	 */
	static public class UniqueRemovalEvent<K, V> {
		final private K m_key;
		final private V m_value;

		public K getKey() {
			return m_key;
		}

		public V getValue() {
			return m_value;
		}

		public UniqueRemovalEvent(K key, V value) {
			m_key = key;
			m_value = value;
		}
	}

}
