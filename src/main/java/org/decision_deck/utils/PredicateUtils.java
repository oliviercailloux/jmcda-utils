package org.decision_deck.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Predicate;

public class PredicateUtils {
	static private class PredicateIntAtLeast implements Predicate<Integer> {
		private final int m_atLeast;

		public PredicateIntAtLeast(int value) {
			m_atLeast = value;
		}

		@Override
		public boolean apply(Integer input) {
			return input.intValue() >= m_atLeast;
		}
	}

	static private class LEQ implements Predicate<Double> {
		private final double m_degree;

		public LEQ(double value) {
			m_degree = value;
		}

		@Override
		public boolean apply(Double input) {
			return input.doubleValue() <= m_degree;
		}
	}

	static private class PredicateDoubleGreaterThan implements Predicate<Double> {
		private final double m_degree;

		public PredicateDoubleGreaterThan(double value) {
			m_degree = value;
		}

		@Override
		public boolean apply(Double input) {
			return input.doubleValue() > m_degree;
		}
	}

	static private class BooleanPredicate implements Predicate<Boolean> {
		@Override
		public boolean apply(Boolean input) {
			return input.booleanValue();
		}
	}

	static private class CombinedEntryPredicate<K, V> implements Predicate<Map.Entry<K, V>> {
		private final Predicate<K> m_keyPredicate;
		private final Predicate<V> m_valuePredicate;

		public CombinedEntryPredicate(Predicate<K> keyPredicate, Predicate<V> valuePredicate) {
			checkNotNull(keyPredicate);
			checkNotNull(valuePredicate);
			m_keyPredicate = keyPredicate;
			m_valuePredicate = valuePredicate;
		}

		@Override
		public boolean apply(Entry<K, V> input) {
			checkNotNull(input);
			return m_keyPredicate.apply(input.getKey()) && m_valuePredicate.apply(input.getValue());
		}
	}

	static private class PredicateDoubleAtLeast implements Predicate<Double> {
		private final double m_degree;

		public PredicateDoubleAtLeast(double value) {
			m_degree = value;
		}

		@Override
		public boolean apply(Double input) {
			return input.doubleValue() >= m_degree;
		}
	}

	static private class NoNullEntries<K, V> implements Predicate<Map.Entry<K, V>> {
		@Override
		public boolean apply(Entry<K, V> input) {
			if (input == null) {
				return false;
			}
			if (input.getKey() == null) {
				return false;
			}
			if (input.getValue() == null) {
				return false;
			}
			return true;
		}
	}

	static public <K, V> Predicate<Map.Entry<K, V>> asEntryPredicate(Predicate<K> keyPredicate,
			Predicate<V> valuePredicate) {
		return new CombinedEntryPredicate<K, V>(keyPredicate, valuePredicate);
	}

	static public Predicate<Boolean> fromBoolean() {
		return new BooleanPredicate();
	}

	static public Predicate<Double> inBetween(final double lowerBound, final double upperBound) {
		return new Predicate<Double>() {
			@Override
			public boolean apply(Double input) {
				final double value = input.doubleValue();
				return value >= lowerBound && value <= upperBound;
			}
		};
	}

	static public Predicate<Integer> atLeast(int value) {
		return new PredicateIntAtLeast(value);
	}

	static public Predicate<Double> atMost(double value) {
		return new LEQ(value);
	}

	static public Predicate<Double> greaterThan(double value) {
		return new PredicateDoubleGreaterThan(value);
	}

	static public Predicate<? super Collection<?>> isEmpty() {
		return new Predicate<Collection<?>>() {
			@Override
			public boolean apply(Collection<?> input) {
				return input.isEmpty();
			}
		};
	}

	static public Predicate<Double> atLeast(double value) {
		return new PredicateDoubleAtLeast(value);
	}

	static public <K, V> Predicate<Map.Entry<K, V>> noNullEntries() {
		return new NoNullEntries<K, V>();
	}
}
