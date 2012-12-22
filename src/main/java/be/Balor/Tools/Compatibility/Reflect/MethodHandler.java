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
 * Handle a method
 * 
 * @author Antoine Aflalo
 * 
 */
public class MethodHandler {
	private final Method method;

	/**
	 * Create the MethodHandler.
	 * 
	 * @param clazz
	 *            - given class, source of the method
	 * @param name
	 *            - name of the method
	 * @param parameterTypes
	 *            - types of the args of the method
	 */
	public MethodHandler(final Class<?> clazz, final String name,
			final Class<?>... parameterTypes) {
		method = MethodUtils.getMethod(clazz, name, parameterTypes);
	}

	/**
	 * Invoke the enclosed method on the given object with the given arguments
	 * 
	 * @param obj
	 *            - given object
	 * @param args
	 *            - given arguments
	 * @return the result of the method
	 */
	public <T> T invoke(final Object obj, final Object... args) {
		return MethodUtils.invokeMethod(obj, method, args);
	}
}
