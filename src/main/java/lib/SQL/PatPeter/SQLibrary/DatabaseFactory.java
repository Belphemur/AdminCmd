/************************************************************************
 * This file is part of SQLibrary.									
 *																		
 * SQLibrary is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by	
 * the Free Software Foundation, either version 3 of the License, or		
 * (at your option) any later version.									
 *																		
 * SQLibrary is distributed in the hope that it will be useful,	
 * but WITHOUT ANY WARRANTY; without even the implied warranty of		
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the			
 * GNU General Public License for more details.							
 *																		
 * You should have received a copy of the GNU General Public License
 * along with SQLibrary.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package lib.SQL.PatPeter.SQLibrary;

import lib.SQL.PatPeter.SQLibrary.DatabaseConfig.Parameter;

import org.bukkit.configuration.InvalidConfigurationException;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
class DatabaseFactory {
	/**
	 * 
	 */
	private DatabaseFactory() {

	}
	static Database createDatabase(final DatabaseConfig config)
			throws InvalidConfigurationException {
		if (!config.isValid()) {
			throw new InvalidConfigurationException(
					"The configuration is invalid, you don't have enought parameter for that DB : "
							+ config.getType());
		}
		switch (config.getType()) {
			case MYSQL :
				return new MySQL(config.getLog(),
						config.getParameter(Parameter.DB_PREFIX),
						config.getParameter(Parameter.HOSTNAME),
						config.getParameter(Parameter.PORT_NUMBER),
						config.getParameter(Parameter.DATABASE),
						config.getParameter(Parameter.USER),
						config.getParameter(Parameter.PASSWORD));
			case SQLITE :
				return new SQLite(config.getLog(),
						config.getParameter(Parameter.DB_PREFIX),
						config.getParameter(Parameter.DB_NAME),
						config.getParameter(Parameter.DB_LOCATION));
			default :
				throw new InvalidConfigurationException(
						"You have to select a Database Type");
		}
	}
}
