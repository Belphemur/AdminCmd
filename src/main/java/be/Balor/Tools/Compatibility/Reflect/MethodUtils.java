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

import java.lang.reflect.Method;

/**
 * @author Antoine
 * 
 */
public class MethodUtils {

	/**
	 * Get the method of the given class, even if it's a private method.
	 * 
	 * @param clazz
	 *            - given class
	 * @param name
	 *            - name of the method
	 * @param parameterTypes
	 *            - parameter type of the method (Class<?>)
	 * @return the wanted method if found
	 * @throws RuntimeException
	 *             if the method don't exist.
	 */
	public static Method getMethod(final Class<?> clazz, final String name,
			final Class<?>... parameterTypes) {
		Method method;
		try {
			method = getClassMethod(clazz, name, parameterTypes);
			return method;
		} catch (final Exception e) {
			throw new RuntimeException("Can't get method " + name + " from "
					+ clazz, e);
		}
	}

	/**
	 * Invoke the method with the given argument on the given object. Works also
	 * for private method.
	 * 
	 * @param obj
	 *            - given object
	 * @param method
	 *            - given method
	 * @param args
	 *            - given arguments
	 * @return the result of the method.
	 * @throws RuntimeException
	 *             if there is a problem while invoking the method.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(final Object obj, final Method method,
			final Object... args) {
		T result = null;
		method.setAccessible(true);
		try {
			result = (T) method.invoke(obj, args);
		} catch (final Exception e) {
			throw new RuntimeException("Can't invoke method " + method
					+ " from " + obj, e);
		} finally {
			method.setAccessible(false);
		}
		return result;
	}

	private static Method getClassMethod(final Class<?> clazz,
			final String name, final Class<?>... parameterTypes)
			throws NoSuchMethodException {
		Method classMethod;
		Class<?> copyClass = clazz;
		try {
			classMethod = copyClass.getDeclaredMethod(name, parameterTypes);
		} catch (final NoSuchMethodException e) {
			while (true) {
				copyClass = copyClass.getSuperclass();
				if (copyClass.equals(Object.class)) {
					throw e;
				}
				try {
					classMethod = copyClass.getDeclaredMethod(name,
							parameterTypes);
					break;
				} catch (final NoSuchMethodException e1) {
				}
			}
		}
		return classMethod;
	}
}
