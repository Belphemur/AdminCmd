/*
 *  ProtocolLib - Bukkit server library that allows access to the Minecraft protocol.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

package be.Balor.Tools.Compatibility.Reflect.Fuzzy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import be.Balor.Tools.Compatibility.Reflect.MethodInfo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Retrieves fields and methods by signature, not just name.
 * 
 * @author Kristian
 */
public class FuzzyReflection {

	// The class we're actually representing
	private final Class<?> source;

	// Whether or not to lookup private members
	private boolean forceAccess;

	public FuzzyReflection(final Class<?> source, final boolean forceAccess) {
		this.source = source;
		this.forceAccess = forceAccess;
	}

	/**
	 * Retrieves a fuzzy reflection instance from a given class.
	 * 
	 * @param source
	 *            - the class we'll use.
	 * @return A fuzzy reflection instance.
	 */
	public static FuzzyReflection fromClass(final Class<?> source) {
		return fromClass(source, false);
	}

	/**
	 * Retrieves a fuzzy reflection instance from a given class.
	 * 
	 * @param source
	 *            - the class we'll use.
	 * @param forceAccess
	 *            - whether or not to override scope restrictions.
	 * @return A fuzzy reflection instance.
	 */
	public static FuzzyReflection fromClass(final Class<?> source, final boolean forceAccess) {
		return new FuzzyReflection(source, forceAccess);
	}

	/**
	 * Retrieves a fuzzy reflection instance from an object.
	 * 
	 * @param reference
	 *            - the object we'll use.
	 * @return A fuzzy reflection instance that uses the class of the given
	 *         object.
	 */
	public static FuzzyReflection fromObject(final Object reference) {
		return new FuzzyReflection(reference.getClass(), false);
	}

	/**
	 * Retrieves a fuzzy reflection instance from an object.
	 * 
	 * @param reference
	 *            - the object we'll use.
	 * @param forceAccess
	 *            - whether or not to override scope restrictions.
	 * @return A fuzzy reflection instance that uses the class of the given
	 *         object.
	 */
	public static FuzzyReflection fromObject(final Object reference, final boolean forceAccess) {
		return new FuzzyReflection(reference.getClass(), forceAccess);
	}

	/**
	 * Retrieves the underlying class.
	 */
	public Class<?> getSource() {
		return source;
	}

	/**
	 * Retrieve the first method that matches.
	 * <p>
	 * ForceAccess must be TRUE in order for this method to access private,
	 * protected and package level method.
	 * 
	 * @param matcher
	 *            - the matcher to use.
	 * @return The first method that satisfies the given matcher.
	 * @throws IllegalArgumentException
	 *             If the method cannot be found.
	 */
	public Method getMethod(final AbstractFuzzyMatcher<MethodInfo> matcher) {
		final List<Method> result = getMethodList(matcher);

		if (result.size() > 0) {
			return result.get(0);
		} else {
			throw new IllegalArgumentException("Unable to find a method that matches " + matcher);
		}
	}

	/**
	 * Retrieve a list of every method that matches the given matcher.
	 * <p>
	 * ForceAccess must be TRUE in order for this method to access private,
	 * protected and package level methods.
	 * 
	 * @param matcher
	 *            - the matcher to apply.
	 * @return List of found methods.
	 */
	public List<Method> getMethodList(final AbstractFuzzyMatcher<MethodInfo> matcher) {
		final List<Method> methods = Lists.newArrayList();

		// Add all matching fields to the list
		for (final Method method : getMethods()) {
			if (matcher.isMatch(MethodInfo.fromMethod(method), source)) {
				methods.add(method);
			}
		}
		return methods;
	}

	/**
	 * Retrieves a method by looking at its name.
	 * 
	 * @param nameRegex
	 *            - regular expression that will match method names.
	 * @return The first method that satisfies the regular expression.
	 * @throws IllegalArgumentException
	 *             If the method cannot be found.
	 */
	public Method getMethodByName(final String nameRegex) {
		final Pattern match = Pattern.compile(nameRegex);

		for (final Method method : getMethods()) {
			if (match.matcher(method.getName()).matches()) {
				// Right - this is probably it.
				return method;
			}
		}

		throw new IllegalArgumentException("Unable to find a method with the pattern " + nameRegex + " in " + source.getName());
	}

	/**
	 * Retrieves a method by looking at the parameter types only.
	 * 
	 * @param name
	 *            - potential name of the method. Only used by the error
	 *            mechanism.
	 * @param args
	 *            - parameter types of the method to find.
	 * @return The first method that satisfies the parameter types.
	 * @throws IllegalArgumentException
	 *             If the method cannot be found.
	 */
	public Method getMethodByParameters(final String name, final Class<?>... args) {
		// Find the correct method to call
		for (final Method method : getMethods()) {
			if (Arrays.equals(method.getParameterTypes(), args)) {
				return method;
			}
		}

		// That sucks
		throw new IllegalArgumentException("Unable to find " + name + " in " + source.getName());
	}

	/**
	 * Retrieves a method by looking at the parameter types and return type
	 * only.
	 * 
	 * @param name
	 *            - potential name of the method. Only used by the error
	 *            mechanism.
	 * @param returnType
	 *            - return type of the method to find.
	 * @param args
	 *            - parameter types of the method to find.
	 * @return The first method that satisfies the parameter types.
	 * @throws IllegalArgumentException
	 *             If the method cannot be found.
	 */
	public Method getMethodByParameters(final String name, final Class<?> returnType, final Class<?>[] args) {
		// Find the correct method to call
		final List<Method> methods = getMethodListByParameters(returnType, args);

		if (methods.size() > 0) {
			return methods.get(0);
		} else {
			// That sucks
			throw new IllegalArgumentException("Unable to find " + name + " in " + source.getName());
		}
	}

	/**
	 * Retrieves a method by looking at the parameter types and return type
	 * only.
	 * 
	 * @param name
	 *            - potential name of the method. Only used by the error
	 *            mechanism.
	 * @param returnTypeRegex
	 *            - regular expression matching the return type of the method to
	 *            find.
	 * @param argsRegex
	 *            - regular expressions of the matching parameter types.
	 * @return The first method that satisfies the parameter types.
	 * @throws IllegalArgumentException
	 *             If the method cannot be found.
	 */
	public Method getMethodByParameters(final String name, final String returnTypeRegex, final String[] argsRegex) {
		final Pattern match = Pattern.compile(returnTypeRegex);
		final Pattern[] argMatch = new Pattern[argsRegex.length];

		for (int i = 0; i < argsRegex.length; i++) {
			argMatch[i] = Pattern.compile(argsRegex[i]);
		}

		// Find the correct method to call
		for (final Method method : getMethods()) {
			if (match.matcher(method.getReturnType().getName()).matches()) {
				if (matchParameters(argMatch, method.getParameterTypes())) {
					return method;
				}
			}
		}

		// That sucks
		throw new IllegalArgumentException("Unable to find " + name + " in " + source.getName());
	}

	private boolean matchParameters(final Pattern[] parameterMatchers, final Class<?>[] argTypes) {
		if (parameterMatchers.length != argTypes.length) {
			throw new IllegalArgumentException("Arrays must have the same cardinality.");
		}

		// Check types against the regular expressions
		for (int i = 0; i < argTypes.length; i++) {
			if (!parameterMatchers[i].matcher(argTypes[i].getName()).matches()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Retrieves every method that has the given parameter types and return
	 * type.
	 * 
	 * @param returnType
	 *            - return type of the method to find.
	 * @param args
	 *            - parameter types of the method to find.
	 * @return Every method that satisfies the given constraints.
	 */
	public List<Method> getMethodListByParameters(final Class<?> returnType, final Class<?>[] args) {
		final List<Method> methods = new ArrayList<Method>();

		// Find the correct method to call
		for (final Method method : getMethods()) {
			if (method.getReturnType().equals(returnType) && Arrays.equals(method.getParameterTypes(), args)) {
				methods.add(method);
			}
		}

		return methods;
	}

	/**
	 * Retrieves a field by name.
	 * 
	 * @param nameRegex
	 *            - regular expression that will match a field name.
	 * @return The first field to match the given expression.
	 * @throws IllegalArgumentException
	 *             If the field cannot be found.
	 */
	public Field getFieldByName(final String nameRegex) {
		final Pattern match = Pattern.compile(nameRegex);

		for (final Field field : getFields()) {
			if (match.matcher(field.getName()).matches()) {
				// Right - this is probably it.
				return field;
			}
		}

		// Looks like we're outdated. Too bad.
		throw new IllegalArgumentException("Unable to find a field with the pattern " + nameRegex + " in " + source.getName());
	}

	/**
	 * Retrieves the first field with a type equal to or more specific to the
	 * given type.
	 * 
	 * @param name
	 *            - name the field probably is given. This will only be used in
	 *            the error message.
	 * @param type
	 *            - type of the field to find.
	 * @return The first field with a type that is an instance of the given
	 *         type.
	 */
	public Field getFieldByType(final String name, final Class<?> type) {
		final List<Field> fields = getFieldListByType(type);

		if (fields.size() > 0) {
			return fields.get(0);
		} else {
			// Looks like we're outdated. Too bad.
			throw new IllegalArgumentException(String.format("Unable to find a field %s with the type %s in %s", name, type.getName(), source.getName()));
		}
	}

	/**
	 * Retrieves every field with a type equal to or more specific to the given
	 * type.
	 * 
	 * @param type
	 *            - type of the fields to find.
	 * @return Every field with a type that is an instance of the given type.
	 */
	public List<Field> getFieldListByType(final Class<?> type) {
		final List<Field> fields = new ArrayList<Field>();

		// Field with a compatible type
		for (final Field field : getFields()) {
			// A assignable from B -> B instanceOf A
			if (type.isAssignableFrom(field.getType())) {
				fields.add(field);
			}
		}

		return fields;
	}

	/**
	 * Retrieve the first field that matches.
	 * <p>
	 * ForceAccess must be TRUE in order for this method to access private,
	 * protected and package level fields.
	 * 
	 * @param matcher
	 *            - the matcher to use.
	 * @return The first method that satisfies the given matcher.
	 * @throws IllegalArgumentException
	 *             If the method cannot be found.
	 */
	public Field getField(final AbstractFuzzyMatcher<Field> matcher) {
		final List<Field> result = getFieldList(matcher);

		if (result.size() > 0) {
			return result.get(0);
		} else {
			throw new IllegalArgumentException("Unable to find a field that matches " + matcher);
		}
	}

	/**
	 * Retrieve a list of every field that matches the given matcher.
	 * <p>
	 * ForceAccess must be TRUE in order for this method to access private,
	 * protected and package level fields.
	 * 
	 * @param matcher
	 *            - the matcher to apply.
	 * @return List of found fields.
	 */
	public List<Field> getFieldList(final AbstractFuzzyMatcher<Field> matcher) {
		final List<Field> fields = Lists.newArrayList();

		// Add all matching fields to the list
		for (final Field field : getFields()) {
			if (matcher.isMatch(field, source)) {
				fields.add(field);
			}
		}
		return fields;
	}

	/**
	 * Retrieves a field by type.
	 * <p>
	 * Note that the type is matched using the full canonical representation,
	 * i.e.:
	 * <ul>
	 * <li>java.util.List</li>
	 * <li>net.comphenix.xp.ExperienceMod</li>
	 * </ul>
	 * 
	 * @param typeRegex
	 *            - regular expression that will match the field type.
	 * @return The first field with a type that matches the given regular
	 *         expression.
	 * @throws IllegalArgumentException
	 *             If the field cannot be found.
	 */
	public Field getFieldByType(final String typeRegex) {

		final Pattern match = Pattern.compile(typeRegex);

		// Like above, only here we test the field type
		for (final Field field : getFields()) {
			final String name = field.getType().getName();

			if (match.matcher(name).matches()) {
				return field;
			}
		}

		// Looks like we're outdated. Too bad.
		throw new IllegalArgumentException("Unable to find a field with the type " + typeRegex + " in " + source.getName());
	}

	/**
	 * Retrieves a field by type.
	 * <p>
	 * Note that the type is matched using the full canonical representation,
	 * i.e.:
	 * <ul>
	 * <li>java.util.List</li>
	 * <li>net.comphenix.xp.ExperienceMod</li>
	 * </ul>
	 * 
	 * @param typeRegex
	 *            - regular expression that will match the field type.
	 * @param ignored
	 *            - types to ignore.
	 * @return The first field with a type that matches the given regular
	 *         expression.
	 * @throws IllegalArgumentException
	 *             If the field cannot be found.
	 */
	@SuppressWarnings("rawtypes")
	public Field getFieldByType(final String typeRegex, final Set<Class> ignored) {

		final Pattern match = Pattern.compile(typeRegex);

		// Like above, only here we test the field type
		for (final Field field : getFields()) {
			final Class type = field.getType();

			if (!ignored.contains(type) && match.matcher(type.getName()).matches()) {
				return field;
			}
		}

		// Looks like we're outdated. Too bad.
		throw new IllegalArgumentException("Unable to find a field with the type " + typeRegex + " in " + source.getName());
	}

	/**
	 * Retrieve the first constructor that matches.
	 * <p>
	 * ForceAccess must be TRUE in order for this method to access private,
	 * protected and package level constructors.
	 * 
	 * @param matcher
	 *            - the matcher to use.
	 * @return The first constructor that satisfies the given matcher.
	 * @throws IllegalArgumentException
	 *             If the constructor cannot be found.
	 */
	public Constructor<?> getConstructor(final AbstractFuzzyMatcher<MethodInfo> matcher) {
		final List<Constructor<?>> result = getConstructorList(matcher);

		if (result.size() > 0) {
			return result.get(0);
		} else {
			throw new IllegalArgumentException("Unable to find a method that matches " + matcher);
		}
	}

	/**
	 * Retrieve every method as a map over names.
	 * <p>
	 * Note that overloaded methods will only occur once in the resulting map.
	 * 
	 * @param methods
	 *            - every method.
	 * @return A map over every given method.
	 */
	public Map<String, Method> getMappedMethods(final List<Method> methods) {
		final Map<String, Method> map = Maps.newHashMap();

		for (final Method method : methods) {
			map.put(method.getName(), method);
		}
		return map;
	}

	/**
	 * Retrieve a list of every constructor that matches the given matcher.
	 * <p>
	 * ForceAccess must be TRUE in order for this method to access private,
	 * protected and package level constructors.
	 * 
	 * @param matcher
	 *            - the matcher to apply.
	 * @return List of found constructors.
	 */
	public List<Constructor<?>> getConstructorList(final AbstractFuzzyMatcher<MethodInfo> matcher) {
		final List<Constructor<?>> constructors = Lists.newArrayList();

		// Add all matching fields to the list
		for (final Constructor<?> constructor : getConstructors()) {
			if (matcher.isMatch(MethodInfo.fromConstructor(constructor), source)) {
				constructors.add(constructor);
			}
		}
		return constructors;
	}

	/**
	 * Retrieves all private and public fields in declared order (after JDK
	 * 1.5).
	 * <p>
	 * Private, protected and package fields are ignored if forceAccess is
	 * FALSE.
	 * 
	 * @return Every field.
	 */
	public Set<Field> getFields() {
		// We will only consider private fields in the declared class
		if (forceAccess) {
			return setUnion(source.getDeclaredFields(), source.getFields());
		} else {
			return setUnion(source.getFields());
		}
	}

	/**
	 * Retrieves all private and public methods in declared order (after JDK
	 * 1.5).
	 * <p>
	 * Private, protected and package methods are ignored if forceAccess is
	 * FALSE.
	 * 
	 * @return Every method.
	 */
	public Set<Method> getMethods() {
		// We will only consider private methods in the declared class
		if (forceAccess) {
			return setUnion(source.getDeclaredMethods(), source.getMethods());
		} else {
			return setUnion(source.getMethods());
		}
	}

	/**
	 * Retrieves all private and public constructors in declared order (after
	 * JDK 1.5).
	 * <p>
	 * Private, protected and package constructors are ignored if forceAccess is
	 * FALSE.
	 * 
	 * @return Every constructor.
	 */
	public Set<Constructor<?>> getConstructors() {
		if (forceAccess) {
			return setUnion(source.getDeclaredConstructors());
		} else {
			return setUnion(source.getConstructors());
		}
	}

	// Prevent duplicate fields
	private static <T> Set<T> setUnion(final T[]... array) {
		final Set<T> result = new LinkedHashSet<T>();

		for (final T[] elements : array) {
			for (final T element : elements) {
				result.add(element);
			}
		}

		return result;
	}

	/**
	 * Retrieves whether or not not to override any scope restrictions.
	 * 
	 * @return TRUE if we override scope, FALSE otherwise.
	 */
	public boolean isForceAccess() {
		return forceAccess;
	}

	/**
	 * Sets whether or not not to override any scope restrictions.
	 * 
	 * @param forceAccess
	 *            - TRUE if we override scope, FALSE otherwise.
	 */
	public void setForceAccess(final boolean forceAccess) {
		this.forceAccess = forceAccess;
	}
}
