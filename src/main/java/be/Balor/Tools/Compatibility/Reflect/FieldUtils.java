/************************************************************************
 * This file is part of AdminCmd.									
 *																		
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by	
 * the Free Software Foundation, either version 3 of the License, or		
 * (at your option) any later version.									
 *																		
 * AdminCmd is distributed in the hope that it will be useful,	
 * but WITHOUT ANY WARRANTY; without even the implied warranty of		
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the			
 * GNU General Public License for more details.							
 *																		
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package be.Balor.Tools.Compatibility.Reflect;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
import java.lang.reflect.Field;

import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyFieldContract;
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyReflection;

/**
 * A utility class for accessing private fields, and calling private methods
 * 
 * @author WinSock
 * @version 1.0
 */
public class FieldUtils {

	/**
	 * Get the field from the wanted object
	 * 
	 * @param object
	 *            - given object
	 * @param field
	 *            - given field
	 * @return the attribute casted as wanted.
	 * @throws RuntimeException
	 *             if we can't get the field
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAttribute(final Object object, final String field) {
		try {
			final Field objectField = getExactField(object.getClass(), field);
			return (T) getAttributeFromField(object, objectField);
		} catch (final Exception e) {
			throw new RuntimeException("Can't get field " + field + " from " + object, e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getAttribute(final Object object, final FuzzyFieldContract contract) {
		try {
			final Field objectField = getMatchedField(object.getClass(), contract);
			return (T) getAttributeFromField(object, objectField);
		} catch (final Exception e) {
			throw new RuntimeException("Can't get field " + contract + " from " + object, e);
		}
	}

	/**
	 * @param object
	 * @param objectField
	 * @return
	 * @throws IllegalAccessException
	 */
	private static Object getAttributeFromField(final Object object, final Field objectField) throws IllegalAccessException {
		objectField.setAccessible(true);
		final Object obj = objectField.get(object);
		objectField.setAccessible(false);
		return obj;
	}

	/**
	 * Set the field from the wanted object
	 * 
	 * @param object
	 *            - given object
	 * @param field
	 *            - given field
	 * @throws RuntimeException
	 *             if we can't set the field
	 */
	public static void setExactAttribute(final Object object, final String field, final Object value) {
		try {
			final Field objectField = getExactField(object.getClass(), field);
			setAttribute(object, value, objectField);
		} catch (final Exception e) {
			throw new RuntimeException("Can't set field " + field + " from " + object, e);
		}
	}

	/**
	 * Set the field from the wanted object
	 * 
	 * @param object
	 *            - given object
	 * @param contract
	 *            - given field
	 * @throws RuntimeException
	 *             if we can't set the field
	 */
	public static void setMatchedAttribute(final Object object, final FuzzyFieldContract contract, final Object value) {
		try {
			final Field objectField = getMatchedField(object.getClass(), contract);
			setAttribute(object, value, objectField);
		} catch (final Exception e) {
			throw new RuntimeException("Can't set field " + contract + " from " + object, e);
		}
	}

	/**
	 * Set the attribute of an object
	 * 
	 * @param object
	 *            the object
	 * @param value
	 *            the new value
	 * @param objectField
	 *            the field to change
	 * @throws IllegalAccessException
	 */
	public static void setAttribute(final Object object, final Object value, final Field objectField) throws IllegalAccessException {
		objectField.setAccessible(true);
		objectField.set(object, value);
		objectField.setAccessible(false);
	}

	/**
	 * Get a field by it's name recursively
	 * 
	 * @param object
	 * @param field
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public static Field getExactField(final Class<?> source, final String field) throws SecurityException, NoSuchFieldException {
		Field objectField;
		Class<?> clazz = source;
		try {
			objectField = clazz.getDeclaredField(field);
		} catch (final NoSuchFieldException e) {
			while (true) {
				clazz = clazz.getSuperclass();
				if (clazz.equals(Object.class)) {
					throw e;
				}
				try {
					objectField = clazz.getDeclaredField(field);
					break;
				} catch (final NoSuchFieldException e1) {
				}
			}
		}
		return objectField;
	}

	/**
	 * Get a field using a FieldContract recursively
	 * 
	 * @param source
	 * @param contract
	 * @return
	 */
	public static Field getMatchedField(final Class<?> source, final FuzzyFieldContract contract) {
		Class<?> clazz = source;
		FuzzyReflection reflect = FuzzyReflection.fromClass(clazz, true);
		Field objectField;
		try {
			objectField = reflect.getField(contract);
		} catch (final IllegalArgumentException e) {
			while (true) {
				clazz = clazz.getSuperclass();
				if (clazz.equals(Object.class)) {
					throw e;
				}
				reflect = FuzzyReflection.fromClass(clazz, true);
				try {
					objectField = reflect.getField(contract);
					break;
				} catch (final IllegalArgumentException e1) {
				}
			}
		}
		return objectField;
	}
}