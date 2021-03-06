package be.Balor.Tools.Compatibility.Reflect.Fuzzy;

import java.util.regex.Pattern;

import com.google.common.base.Objects;

/**
 * Determine if a class matches based on its name using a regular expression.
 * 
 * @author Kristian
 */
class ClassRegexMatcher extends AbstractFuzzyMatcher<Class<?>> {
	private final Pattern regex;
	private final int priority;

	public ClassRegexMatcher(final Pattern regex, final int priority) {
		if (regex == null) {
			throw new IllegalArgumentException("Regular expression pattern cannot be NULL.");
		}
		this.regex = regex;
		this.priority = priority;
	}

	@Override
	public boolean isMatch(final Class<?> value, final Object parent) {
		if (value != null) {
			return regex.matcher(value.getCanonicalName()).matches();
		} else {
			return false;
		}
	}

	@Override
	protected int calculateRoundNumber() {
		return -priority;
	}

	@Override
	public String toString() {
		return "class name of " + regex.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(regex, priority);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof ClassRegexMatcher) {
			final ClassRegexMatcher other = (ClassRegexMatcher) obj;

			return priority == other.priority && FuzzyMatchers.checkPattern(regex, other.regex);
		}
		return false;
	}
}