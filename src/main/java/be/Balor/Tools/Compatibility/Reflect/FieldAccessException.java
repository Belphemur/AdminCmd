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
 * @author Antoine
 * 
 */
public class FieldAccessException extends RuntimeException {

	/**
	 * @param string
	 */
	public FieldAccessException(final String string) {
		super(string);
	}

	/**
	 * @param string
	 * @param e
	 */
	public FieldAccessException(final String string, final Throwable e) {
		super(string, e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1590941393693031754L;

}
