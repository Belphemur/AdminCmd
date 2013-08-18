package be.Balor.Tools.Compatibility.Reflect.Fuzzy;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.google.common.base.Objects;

/**
 * Represents a field matcher.
 * 
 * @author Kristian
 */
public class FuzzyFieldContract extends AbstractFuzzyMember<Field> {
	private AbstractFuzzyMatcher<Class<?>> typeMatcher = ClassExactMatcher.MATCH_ALL;

	/**
	 * Represents a builder for a field matcher.
	 * 
	 * @author Kristian
	 */
	public static class Builder extends AbstractFuzzyMember.Builder<FuzzyFieldContract> {
		@Override
		public Builder requireModifier(final int modifier) {
			super.requireModifier(modifier);
			return this;
		}

		@Override
		public Builder banModifier(final int modifier) {
			super.banModifier(modifier);
			return this;
		}

		@Override
		public Builder nameRegex(final String regex) {
			super.nameRegex(regex);
			return this;
		}

		@Override
		public Builder nameRegex(final Pattern pattern) {
			super.nameRegex(pattern);
			return this;
		}

		@Override
		public Builder nameExact(final String name) {
			super.nameExact(name);
			return this;
		}

		@Override
		public Builder declaringClassExactType(final Class<?> declaringClass) {
			super.declaringClassExactType(declaringClass);
			return this;
		}

		@Override
		public Builder declaringClassSuperOf(final Class<?> declaringClass) {
			super.declaringClassSuperOf(declaringClass);
			return this;
		}

		@Override
		public Builder declaringClassDerivedOf(final Class<?> declaringClass) {
			super.declaringClassDerivedOf(declaringClass);
			return this;
		}

		@Override
		public Builder declaringClassMatching(final AbstractFuzzyMatcher<Class<?>> classMatcher) {
			super.declaringClassMatching(classMatcher);
			return this;
		}

		@Override
		@Nonnull
		protected FuzzyFieldContract initialMember() {
			return new FuzzyFieldContract();
		}

		public Builder typeExact(final Class<?> type) {
			member.typeMatcher = FuzzyMatchers.matchExact(type);
			return this;
		}

		public Builder typeSuperOf(final Class<?> type) {
			member.typeMatcher = FuzzyMatchers.matchSuper(type);
			return this;
		}

		public Builder typeDerivedOf(final Class<?> type) {
			member.typeMatcher = FuzzyMatchers.matchDerived(type);
			return this;
		}

		public Builder typeMatches(final AbstractFuzzyMatcher<Class<?>> matcher) {
			member.typeMatcher = matcher;
			return this;
		}

		@Override
		public FuzzyFieldContract build() {
			member.prepareBuild();
			return new FuzzyFieldContract(member);
		}
	}

	/**
	 * Return a new fuzzy field contract builder.
	 * 
	 * @return New fuzzy field contract builder.
	 */
	public static Builder newBuilder() {
		return new Builder();
	}

	private FuzzyFieldContract() {
		// Only allow construction through the builder
		super();
	}

	/**
	 * Retrieve the class matcher that matches the type of a field.
	 * 
	 * @return The class matcher.
	 */
	public AbstractFuzzyMatcher<Class<?>> getTypeMatcher() {
		return typeMatcher;
	}

	/**
	 * Create a new field contract from the given contract.
	 * 
	 * @param other
	 *            - the contract to clone.
	 */
	private FuzzyFieldContract(final FuzzyFieldContract other) {
		super(other);
		this.typeMatcher = other.typeMatcher;
	}

	@Override
	public boolean isMatch(final Field value, final Object parent) {
		if (super.isMatch(value, parent)) {
			return typeMatcher.isMatch(value.getType(), value);
		}
		// No match
		return false;
	}

	@Override
	protected int calculateRoundNumber() {
		// Combine the two
		return combineRounds(super.calculateRoundNumber(), typeMatcher.calculateRoundNumber());
	}

	@Override
	protected Map<String, Object> getKeyValueView() {
		final Map<String, Object> member = super.getKeyValueView();

		if (typeMatcher != ClassExactMatcher.MATCH_ALL) {
			member.put("type", typeMatcher);
		}
		return member;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(typeMatcher, super.hashCode());
	}

	@Override
	public boolean equals(final Object obj) {
		// Use the member equals method
		if (this == obj) {
			return true;
		} else if (obj instanceof FuzzyFieldContract && super.equals(obj)) {
			return Objects.equal(typeMatcher, ((FuzzyFieldContract) obj).typeMatcher);
		}
		return true;
	}
}
