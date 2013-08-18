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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyMethodContract;
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyReflection;

/**
 * @author Antoine
 * 
 */
public class MethodUtils {
	private static class MethodKey {
		private final Class<?> clazz;
		private final String name;
		private final Class<?>[] parameterTypes;
		private final int hashcode;

		/**
		 * @param clazz
		 * @param name
		 * @param parameterTypes
		 */
		public MethodKey(final Class<?> clazz, final String name, final Class<?>... parameterTypes) {
			super();
			this.clazz = clazz;
			this.name = name;
			this.parameterTypes = parameterTypes;
			this.hashcode = calcHashCode();
		}

		private int calcHashCode() {
			final int prime = 23;
			int result = 1;
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + Arrays.hashCode(parameterTypes);
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return this.hashcode;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final MethodKey other = (MethodKey) obj;
			if (clazz == null) {
				if (other.clazz != null) {
					return false;
				}
			} else if (!clazz.equals(other.clazz)) {
				return false;
			}
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			if (!Arrays.equals(parameterTypes, other.parameterTypes)) {
				return false;
			}
			return true;
		}

	}

	private static class MethodFuzzyKey {
		private final Class<?> clazz;
		private final FuzzyMethodContract contract;

		/**
		 * @param clazz
		 * @param contract
		 */
		private MethodFuzzyKey(final Class<?> clazz, final FuzzyMethodContract contract) {
			super();
			this.clazz = clazz;
			this.contract = contract;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
			result = prime * result + ((contract == null) ? 0 : contract.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final MethodFuzzyKey other = (MethodFuzzyKey) obj;
			if (clazz == null) {
				if (other.clazz != null) {
					return false;
				}
			} else if (!clazz.equals(other.clazz)) {
				return false;
			}
			if (contract == null) {
				if (other.contract != null) {
					return false;
				}
			} else if (!contract.equals(other.contract)) {
				return false;
			}
			return true;
		}

	}

	private static final Map<MethodKey, Method> cachedMethod = new HashMap<MethodKey, Method>();
	private static final Map<MethodFuzzyKey, Method> cachedContractMethod = new HashMap<MethodFuzzyKey, Method>();

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
	public static Method getMethod(final Class<?> clazz, final String name, final Class<?>... parameterTypes) {
		Method method;
		try {
			method = getClassMethod(clazz, name, parameterTypes);
			return method;
		} catch (final Exception e) {
			throw new RuntimeException("Can't get method " + name + " from " + clazz, e);
		}
	}

	/**
	 * Get the method of the given class, even if it's a private method.
	 * 
	 * @param clazz
	 *            - given class
	 * @param contract
	 *            - contract containing the information about the method
	 * @return the wanted method if found
	 * @throws RuntimeException
	 *             if the method don't exist.
	 */
	public static Method getMethod(final Class<?> clazz, final FuzzyMethodContract contract) {
		Method method;
		try {
			method = getClassMethod(clazz, contract);
			return method;
		} catch (final Exception e) {
			throw new RuntimeException("Can't get method " + contract + " from " + clazz, e);
		}
	}

	/**
	 * @param clazz
	 * @param contract
	 * @return
	 */
	private static Method getClassMethod(final Class<?> clazz, final FuzzyMethodContract contract) {
		final MethodFuzzyKey key = new MethodFuzzyKey(clazz, contract);
		Method classMethod = cachedContractMethod.get(key);
		if (classMethod != null) {
			return classMethod;
		}
		Class<?> copyClass = clazz;
		try {
			classMethod = FuzzyReflection.fromClass(clazz, true).getMethod(contract);
		} catch (final IllegalArgumentException e) {
			while (true) {
				copyClass = copyClass.getSuperclass();
				if (copyClass.equals(Object.class)) {
					throw e;
				}
				try {
					classMethod = FuzzyReflection.fromClass(clazz).getMethod(contract);
					break;
				} catch (final IllegalArgumentException e1) {
				}
			}
		}
		cachedContractMethod.put(key, classMethod);
		return classMethod;
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
	public static <T> T invokeMethod(final Object obj, final Method method, final Object... args) {
		T result = null;
		method.setAccessible(true);
		try {
			result = (T) method.invoke(obj, args);
		} catch (final Exception e) {
			throw new RuntimeException("Can't invoke method " + method + " from " + obj, e);
		} finally {
			method.setAccessible(false);
		}
		return result;
	}

	private synchronized static Method getClassMethod(final Class<?> clazz, final String name, final Class<?>... parameterTypes) throws NoSuchMethodException {
		final MethodKey key = new MethodKey(clazz, name, parameterTypes);
		Method classMethod = cachedMethod.get(key);
		if (classMethod != null) {
			return classMethod;
		}
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
					classMethod = copyClass.getDeclaredMethod(name, parameterTypes);
					break;
				} catch (final NoSuchMethodException e1) {
				}
			}
		}
		cachedMethod.put(key, classMethod);
		return classMethod;
	}
}
