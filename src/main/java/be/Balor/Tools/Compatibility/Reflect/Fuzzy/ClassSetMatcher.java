package be.Balor.Tools.Compatibility.Reflect.Fuzzy;

import java.util.Set;

import com.google.common.base.Objects;

/**
 * Represents a class matcher that checks for equality using a given set of
 * classes.
 * 
 * @author Kristian
 */
class ClassSetMatcher extends AbstractFuzzyMatcher<Class<?>> {
	private final Set<Class<?>> classes;

	public ClassSetMatcher(final Set<Class<?>> classes) {
		if (classes == null) {
			throw new IllegalArgumentException("Set of classes cannot be NULL.");
		}
		this.classes = classes;
	}

	@Override
	public boolean isMatch(final Class<?> value, final Object parent) {
		return classes.contains(value);
	}

	@Override
	protected int calculateRoundNumber() {
		int roundNumber = 0;

		// The highest round number (except zero).
		for (final Class<?> clazz : classes) {
			roundNumber = combineRounds(roundNumber,
					-ClassExactMatcher.getClassNumber(clazz));
		}
		return roundNumber;
	}

	@Override
	public String toString() {
		return "match any: " + classes;
	}

	@Override
	public int hashCode() {
		return classes.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof ClassSetMatcher) {
			// See if the sets are equal
			return Objects.equal(classes, ((ClassSetMatcher) obj).classes);
		}
		return true;
	}
}