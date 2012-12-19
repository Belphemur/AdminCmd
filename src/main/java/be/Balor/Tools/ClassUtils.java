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
package be.Balor.Tools;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
import java.lang.reflect.Field;

/**
 * A utility class for accessing private fields, and calling private methods
 * 
 * @author WinSock
 * @version 1.0
 */
public class ClassUtils {
	@SuppressWarnings("unchecked")
	public static <T> T getPrivateField(final Object object, final String field)
			throws SecurityException, IllegalArgumentException,
			IllegalAccessException, NoSuchFieldException {
		final Field objectField = getObjectField(object, field);
		objectField.setAccessible(true);
		final T obj = (T) objectField.get(object);
		objectField.setAccessible(false);
		return obj;
	}

	public static void setPrivateField(final Object object, final String field,
			final Object value) throws SecurityException,
			IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException {
		final Field objectField = getObjectField(object, field);
		objectField.setAccessible(true);
		objectField.set(object, value);
		objectField.setAccessible(false);
	}

	private static Field getObjectField(final Object object, final String field)
			throws SecurityException, NoSuchFieldException {
		Class<?> clazz = object.getClass();
		Field objectField;
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
				} catch (final NoSuchFieldException e1) {}
			}
		}
		return objectField;
	}
}