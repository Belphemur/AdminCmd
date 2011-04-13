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
package com.Balor.files.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.util.config.Configuration;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class FilesManager {
	protected String path;
	public FilesManager(String path) {
		this.path = path;
		if (!new File(this.path).exists()) {
			new File(this.path).mkdir();
		}
	}
	/**
	 * Open the file and return the Configuration object
	 * 
	 * @param directory
	 * @param fileName
	 * @return the configuration file
	 */
	public Configuration getFile(String fileName) {

		File file = new File(path + File.separator + fileName);

		if (!file.exists()) {

			try {
				file.createNewFile();
			} catch (IOException ex) {
				System.out.println("cannot create file " + file.getPath());
			}
		}
		Configuration config = new Configuration(file);
		config.load();
		return config;
	}
}
