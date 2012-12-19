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
package be.Balor.Tools.Egg.Exceptions;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ParameterMissingException extends ProcessingArgsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4437719310884213242L;
	private final char param;

	/**
	 * @param message
	 */
	public ParameterMissingException(final char param, final String message) {
		super(ExceptionType.MISSING_PARAM, message);
		this.param = param;
	}

	/**
	 * @return the param
	 */
	public char getParam() {
		return param;
	}
}
