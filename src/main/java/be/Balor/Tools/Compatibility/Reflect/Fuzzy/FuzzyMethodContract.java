package be.Balor.Tools.Compatibility.Reflect.Fuzzy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.commons.lang.NotImplementedException;

import be.Balor.Tools.Compatibility.Reflect.MethodInfo;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Represents a contract for matching methods or constructors.
 * 
 * @author Kristian
 */
public class FuzzyMethodContract extends AbstractFuzzyMember<MethodInfo> {
	private static class ParameterClassMatcher extends
			AbstractFuzzyMatcher<Class<?>[]> {
		/**
		 * The expected index.
		 */
		private final AbstractFuzzyMatcher<Class<?>> typeMatcher;
		private final Integer indexMatch;

		/**
		 * Construct a new parameter class matcher.
		 * 
		 * @param typeMatcher
		 *            - class type matcher.
		 */
		public ParameterClassMatcher(
				@Nonnull final AbstractFuzzyMatcher<Class<?>> typeMatcher) {
			this(typeMatcher, null);
		}

		/**
		 * Construct a new parameter class matcher.
		 * 
		 * @param typeMatcher
		 *            - class type matcher.
		 * @param indexMatch
		 *            - parameter index to match, or NULL for anything.
		 */
		public ParameterClassMatcher(
				@Nonnull final AbstractFuzzyMatcher<Class<?>> typeMatcher,
				final Integer indexMatch) {
			if (typeMatcher == null) {
				throw new IllegalArgumentException(
						"Type matcher cannot be NULL.");
			}

			this.typeMatcher = typeMatcher;
			this.indexMatch = indexMatch;
		}

		/**
		 * See if there's a match for this matcher.
		 * 
		 * @param used
		 *            - parameters that have been matched before.
		 * @param parent
		 *            - the container (member) that holds a reference to this
		 *            parameter.
		 * @param params
		 *            - the type of each parameter.
		 * @return TRUE if this matcher matches any of the given parameters,
		 *         FALSE otherwise.
		 */
		public boolean isParameterMatch(final Class<?> param,
				final MethodInfo parent, final int index) {
			// Make sure the index is valid (or NULL)
			if (indexMatch == null || indexMatch == index) {
				return typeMatcher.isMatch(param, parent);
			} else {
				return false;
			}
		}

		@Override
		public boolean isMatch(final Class<?>[] value, final Object parent) {
			throw new NotImplementedException(
					"Use the parameter match instead.");
		}

		@Override
		protected int calculateRoundNumber() {
			return typeMatcher.getRoundNumber();
		}

		@Override
		public String toString() {
			return String.format("{Type: %s, Index: %s}", typeMatcher,
					indexMatch);
		}
	}

	// Match return value
	private AbstractFuzzyMatcher<Class<?>> returnMatcher = ClassExactMatcher.MATCH_ALL;

	// Handle parameters and exceptions
	private List<ParameterClassMatcher> paramMatchers;
	private List<ParameterClassMatcher> exceptionMatchers;

	// Expected parameter count
	private Integer paramCount;

	/**
	 * Represents a builder for a fuzzy method contract.
	 * 
	 * @author Kristian
	 */
	public static class Builder extends
			AbstractFuzzyMember.Builder<FuzzyMethodContract> {
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
		public Builder declaringClassMatching(
				final AbstractFuzzyMatcher<Class<?>> classMatcher) {
			super.declaringClassMatching(classMatcher);
			return this;
		}

		/**
		 * Add a new required parameter by type for any matching method.
		 * 
		 * @param type
		 *            - the exact type this parameter must match.
		 * @return This builder, for chaining.
		 */
		public Builder parameterExactType(final Class<?> type) {
			member.paramMatchers.add(new ParameterClassMatcher(FuzzyMatchers
					.matchExact(type)));
			return this;
		}

		/**
		 * Add a new required parameter whose type must be a superclass of the
		 * given type.
		 * <p>
		 * If a parameter is of type Number, any derived class (Integer, Long,
		 * etc.) will match it.
		 * 
		 * @param type
		 *            - a type or derived type of the matching parameter.
		 * @return This builder, for chaining.
		 */
		public Builder parameterSuperOf(final Class<?> type) {
			member.paramMatchers.add(new ParameterClassMatcher(FuzzyMatchers
					.matchSuper(type)));
			return this;
		}

		/**
		 * Add a new required parameter whose type must match the given class
		 * matcher.
		 * 
		 * @param classMatcher
		 *            - the class matcher.
		 * @return This builder, for chaining.
		 */
		public Builder parameterMatches(
				final AbstractFuzzyMatcher<Class<?>> classMatcher) {
			member.paramMatchers.add(new ParameterClassMatcher(classMatcher));
			return this;
		}

		/**
		 * Add a new required parameter by type and position for any matching
		 * method.
		 * 
		 * @param type
		 *            - the exact type this parameter must match.
		 * @param index
		 *            - the expected position in the parameter list.
		 * @return This builder, for chaining.
		 */
		public Builder parameterExactType(final Class<?> type, final int index) {
			member.paramMatchers.add(new ParameterClassMatcher(FuzzyMatchers
					.matchExact(type), index));
			return this;
		}

		/**
		 * Add a new required parameter whose type must be a superclass of the
		 * given type.
		 * <p>
		 * If a parameter is of type Number, any derived class (Integer, Long,
		 * etc.) will match it.
		 * 
		 * @param type
		 *            - a type or derived type of the matching parameter.
		 * @param index
		 *            - the expected position in the parameter list.
		 * @return This builder, for chaining.
		 */
		public Builder parameterSuperOf(final Class<?> type, final int index) {
			member.paramMatchers.add(new ParameterClassMatcher(FuzzyMatchers
					.matchSuper(type), index));
			return this;
		}

		/**
		 * Add a new required parameter whose type must match the given class
		 * matcher and index.
		 * 
		 * @param classMatcher
		 *            - the class matcher.
		 * @param index
		 *            - the expected position in the parameter list.
		 * @return This builder, for chaining.
		 */
		public Builder parameterMatches(
				final AbstractFuzzyMatcher<Class<?>> classMatcher,
				final int index) {
			member.paramMatchers.add(new ParameterClassMatcher(classMatcher,
					index));
			return this;
		}

		/**
		 * Set the expected number of parameters in the matching method.
		 * 
		 * @param expectedCount
		 *            - the number of parameters to expect.
		 * @return This builder, for chaining.
		 */
		public Builder parameterCount(final int expectedCount) {
			member.paramCount = expectedCount;
			return this;
		}

		/**
		 * Require a void method.
		 * 
		 * @return This builder, for chaining.
		 */
		public Builder returnTypeVoid() {
			return returnTypeExact(Void.TYPE);
		}

		/**
		 * Set the return type of a matching method exactly.
		 * 
		 * @param type
		 *            - the exact return type.
		 * @return This builder, for chaining.
		 */
		public Builder returnTypeExact(final Class<?> type) {
			member.returnMatcher = FuzzyMatchers.matchExact(type);
			return this;
		}

		/**
		 * Set the expected super class of the return type for every matching
		 * method.
		 * 
		 * @param type
		 *            - the return type, or a super class of it.
		 * @return This builder, for chaining.
		 */
		public Builder returnDerivedOf(final Class<?> type) {
			member.returnMatcher = FuzzyMatchers.matchDerived(type);
			return this;
		}

		/**
		 * Set a matcher that must match the return type of a matching method.
		 * 
		 * @param classMatcher
		 *            - the exact return type.
		 * @return This builder, for chaining.
		 */
		public Builder returnTypeMatches(
				final AbstractFuzzyMatcher<Class<?>> classMatcher) {
			member.returnMatcher = classMatcher;
			return this;
		}

		/**
		 * Add a throwable exception that must match the given type exactly.
		 * 
		 * @param type
		 *            - exception type.
		 * @return This builder, for chaining.
		 */
		public Builder exceptionExactType(final Class<?> type) {
			member.exceptionMatchers.add(new ParameterClassMatcher(
					FuzzyMatchers.matchExact(type)));
			return this;
		}

		/**
		 * Add a throwable exception that must match the given type or be
		 * derived.
		 * 
		 * @param type
		 *            - exception type.
		 * @return This builder, for chaining.
		 */
		public Builder exceptionSuperOf(final Class<?> type) {
			member.exceptionMatchers.add(new ParameterClassMatcher(
					FuzzyMatchers.matchSuper(type)));
			return this;
		}

		/**
		 * Add a throwable exception that must match the given matcher,
		 * 
		 * @param classMatcher
		 *            - the class matcher that must match.
		 * @return This builder, for chaining.
		 */
		public Builder exceptionMatches(
				final AbstractFuzzyMatcher<Class<?>> classMatcher) {
			member.exceptionMatchers
					.add(new ParameterClassMatcher(classMatcher));
			return this;
		}

		/**
		 * Add a throwable exception that must match the given type exactly and
		 * index.
		 * 
		 * @param type
		 *            - exception type.
		 * @param index
		 *            - the position in the throwable list.
		 * @return This builder, for chaining.
		 */
		public Builder exceptionExactType(final Class<?> type, final int index) {
			member.exceptionMatchers.add(new ParameterClassMatcher(
					FuzzyMatchers.matchExact(type), index));
			return this;
		}

		/**
		 * Add a throwable exception that must match the given type or be
		 * derived and index.
		 * 
		 * @param type
		 *            - exception type.
		 * @param index
		 *            - the position in the throwable list.
		 * @return This builder, for chaining.
		 */
		public Builder exceptionSuperOf(final Class<?> type, final int index) {
			member.exceptionMatchers.add(new ParameterClassMatcher(
					FuzzyMatchers.matchSuper(type), index));
			return this;
		}

		/**
		 * Add a throwable exception that must match the given matcher and
		 * index.
		 * 
		 * @param classMatcher
		 *            - the class matcher that must match.
		 * @param index
		 *            - the position in the throwable list.
		 * @return This builder, for chaining.
		 */
		public Builder exceptionMatches(
				final AbstractFuzzyMatcher<Class<?>> classMatcher,
				final int index) {
			member.exceptionMatchers.add(new ParameterClassMatcher(
					classMatcher, index));
			return this;
		}

		@Override
		@Nonnull
		protected FuzzyMethodContract initialMember() {
			// With mutable lists
			return new FuzzyMethodContract();
		}

		@Override
		public FuzzyMethodContract build() {
			member.prepareBuild();
			return immutableCopy(member);
		}
	}

	/**
	 * Return a method contract builder.
	 * 
	 * @return Method contract builder.
	 */
	public static Builder newBuilder() {
		return new Builder();
	}

	private FuzzyMethodContract() {
		// Only allow construction from the builder
		paramMatchers = Lists.newArrayList();
		exceptionMatchers = Lists.newArrayList();
	}

	private FuzzyMethodContract(final FuzzyMethodContract other) {
		super(other);
		this.returnMatcher = other.returnMatcher;
		this.paramMatchers = other.paramMatchers;
		this.exceptionMatchers = other.exceptionMatchers;
		this.paramCount = other.paramCount;
	}

	/**
	 * Construct a new immutable copy of the given method contract.
	 * 
	 * @param other
	 *            - the contract to clone.
	 * @return A immutable copy of the given contract.
	 */
	private static FuzzyMethodContract immutableCopy(
			final FuzzyMethodContract other) {
		final FuzzyMethodContract copy = new FuzzyMethodContract(other);

		// Ensure that the lists are immutable
		copy.paramMatchers = ImmutableList.copyOf(copy.paramMatchers);
		copy.exceptionMatchers = ImmutableList.copyOf(copy.exceptionMatchers);
		return copy;
	}

	/**
	 * Retrieve the class matcher for the return type.
	 * 
	 * @return Class matcher for the return type.
	 */
	public AbstractFuzzyMatcher<Class<?>> getReturnMatcher() {
		return returnMatcher;
	}

	/**
	 * Retrieve an immutable list of every parameter matcher for this method.
	 * 
	 * @return Immutable list of every parameter matcher.
	 */
	public ImmutableList<ParameterClassMatcher> getParamMatchers() {
		if (paramMatchers instanceof ImmutableList) {
			return (ImmutableList<ParameterClassMatcher>) paramMatchers;
		} else {
			throw new IllegalStateException("Lists haven't been sealed yet.");
		}
	}

	/**
	 * Retrieve an immutable list of every exception matcher for this method.
	 * 
	 * @return Immutable list of every exception matcher.
	 */
	public List<ParameterClassMatcher> getExceptionMatchers() {
		if (exceptionMatchers instanceof ImmutableList) {
			return exceptionMatchers;
		} else {
			throw new IllegalStateException("Lists haven't been sealed yet.");
		}
	}

	/**
	 * Retrieve the expected parameter count for this method.
	 * 
	 * @return Expected parameter count, or NULL if anyting goes.
	 */
	public Integer getParamCount() {
		return paramCount;
	}

	@Override
	protected void prepareBuild() {
		super.prepareBuild();

		// Sort lists such that more specific tests are up front
		Collections.sort(paramMatchers);
		Collections.sort(exceptionMatchers);
	}

	@Override
	public boolean isMatch(final MethodInfo value, final Object parent) {
		if (super.isMatch(value, parent)) {
			final Class<?>[] params = value.getParameterTypes();
			final Class<?>[] exceptions = value.getExceptionTypes();

			if (!returnMatcher.isMatch(value.getReturnType(), value)) {
				return false;
			}
			if (paramCount != null
					&& paramCount != value.getParameterTypes().length) {
				return false;
			}

			// Finally, check parameters and exceptions
			return matchParameters(params, value, paramMatchers)
					&& matchParameters(exceptions, value, exceptionMatchers);
		}
		// No match
		return false;
	}

	private boolean matchParameters(final Class<?>[] types,
			final MethodInfo parent, final List<ParameterClassMatcher> matchers) {
		final boolean[] accepted = new boolean[matchers.size()];
		int count = accepted.length;

		// Process every parameter in turn
		for (int i = 0; i < types.length; i++) {
			final int matcherIndex = processValue(types[i], parent, i,
					accepted, matchers);

			if (matcherIndex >= 0) {
				accepted[matcherIndex] = true;
				count--;
			}

			// Break early
			if (count == 0) {
				return true;
			}
		}
		return count == 0;
	}

	private int processValue(final Class<?> value, final MethodInfo parent,
			final int index, final boolean accepted[],
			final List<ParameterClassMatcher> matchers) {
		// The order matters
		for (int i = 0; i < matchers.size(); i++) {
			if (!accepted[i]) {
				// See if we got jackpot
				if (matchers.get(i).isParameterMatch(value, parent, index)) {
					return i;
				}
			}
		}

		// Failure
		return -1;
	}

	@Override
	protected int calculateRoundNumber() {
		int current = 0;

		// Consider the return value first
		current = returnMatcher.getRoundNumber();

		// Handle parameters
		for (final ParameterClassMatcher matcher : paramMatchers) {
			current = combineRounds(current, matcher.calculateRoundNumber());
		}
		// And exceptions
		for (final ParameterClassMatcher matcher : exceptionMatchers) {
			current = combineRounds(current, matcher.calculateRoundNumber());
		}

		return combineRounds(super.calculateRoundNumber(), current);
	}

	@Override
	protected Map<String, Object> getKeyValueView() {
		final Map<String, Object> member = super.getKeyValueView();

		// Only add fields that are actual contraints
		if (returnMatcher != ClassExactMatcher.MATCH_ALL) {
			member.put("return", returnMatcher);
		}
		if (paramMatchers.size() > 0) {
			member.put("params", paramMatchers);
		}
		if (exceptionMatchers.size() > 0) {
			member.put("exceptions", exceptionMatchers);
		}
		if (paramCount != null) {
			member.put("paramCount", paramCount);
		}
		return member;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(returnMatcher, paramMatchers,
				exceptionMatchers, paramCount, super.hashCode());
	}

	@Override
	public boolean equals(final Object obj) {
		// Use the member equals method
		if (this == obj) {
			return true;
		} else if (obj instanceof FuzzyMethodContract && super.equals(obj)) {
			final FuzzyMethodContract other = (FuzzyMethodContract) obj;

			return Objects.equal(paramCount, other.paramCount)
					&& Objects.equal(returnMatcher, other.returnMatcher)
					&& Objects.equal(paramMatchers, other.paramMatchers)
					&& Objects
							.equal(exceptionMatchers, other.exceptionMatchers);
		}
		return true;
	}
}
