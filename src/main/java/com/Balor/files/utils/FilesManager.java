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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.config.Configuration;

import au.com.bytecode.opencsv.CSVReader;

import com.Balor.bukkit.AdminCmd.AdminCmd;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class FilesManager {
	protected String path;
	private static FilesManager instance = null;
	/**
	 * @return the instance
	 */
	public static FilesManager getInstance() {
		if(instance == null)
			instance = new FilesManager();
		return instance;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
		if (!new File(this.path).exists()) {
			new File(this.path).mkdir();
		}
		File spawn = getFile(null, "spawnLocations.yml", false);
		if (spawn.exists()) {
			File dir = new File(this.path + File.separator + "spawn");
			dir.mkdir();
			spawn.renameTo(new File(dir, "spawnLocations.yml.old"));
		}
	}
	

	/**
	 * Open the file and return the Configuration object
	 * 
	 * @param directory
	 * @param fileName
	 * @return the configuration file
	 */
	public Configuration getYml(String fileName, String directory) {
		Configuration config = new Configuration(getFile(directory, fileName + ".yml"));
		config.load();
		return config;
	}

	public Configuration getYml(String fileName) {
		return getYml(fileName, null);
	}

	/**
	 * Open the file and return the File object
	 * 
	 * @param directory
	 * @param fileName
	 * @return the configuration file
	 */
	private File getFile(String directory, String fileName) {
		return getFile(directory, fileName, true);
	}

	private File getFile(String directory, String fileName, boolean create) {
		File file = null;

		if (directory != null) {
			if (!new File(this.path + File.separator + directory).exists()) {
				new File(this.path + File.separator + directory).mkdir();
			}
			file = new File(path + File.separator + directory + File.separator + fileName);
		} else
			file = new File(path + File.separator + fileName);

		if (!file.exists() && create) {

			try {
				file.createNewFile();
			} catch (IOException ex) {
				System.out.println("cannot create file " + file.getPath());
			}
		}
		return file;
	}

	/**
	 * Write the alias in the yml file
	 * 
	 * @param alias
	 * @param mc
	 */
	public void addAlias(String alias, MaterialContainer mc) {
		Configuration conf = getYml("Alias");
		ArrayList<String> aliasList = (ArrayList<String>) conf.getStringList("alias",
				new ArrayList<String>());
		ArrayList<String> idList = (ArrayList<String>) conf.getStringList("ids",
				new ArrayList<String>());
		if (aliasList.contains(alias)) {
			int index = aliasList.indexOf(alias);
			aliasList.remove(index);
			idList.remove(index);
		}
		aliasList.add(alias);
		idList.add(mc.toString());
		conf.setProperty("alias", aliasList);
		conf.setProperty("ids", idList);
		conf.save();
	}

	/**
	 * Remove the alias from the yml fileF
	 * 
	 * @param alias
	 */
	public void removeAlias(String alias) {
		Configuration conf = getYml("Alias");
		ArrayList<String> aliasList = (ArrayList<String>) conf.getStringList("alias",
				new ArrayList<String>());
		ArrayList<String> idList = (ArrayList<String>) conf.getStringList("ids",
				new ArrayList<String>());
		int index = aliasList.indexOf(alias);
		aliasList.remove(index);
		idList.remove(index);
		conf.setProperty("alias", aliasList);
		conf.setProperty("ids", idList);
		conf.save();
	}

	/**
	 * Get a file in the jar, copy it in the plugin folder, open it and return
	 * it
	 * 
	 * @param fileName
	 * @return
	 */
	protected File getInnerFile(String fileName) {
		final File file = new File(path, fileName);

		if (!file.exists()) {
			final InputStream res = this.getClass().getResourceAsStream("/" + fileName);
			FileWriter tx = null;
			try {
				tx = new FileWriter(file);
				for (int i = 0; (i = res.read()) > 0;) {
					tx.write(i);
				}
				tx.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
				return file;
			} finally {
				try {
					res.close();
				} catch (Exception ex) {
				}
				try {
					if (tx != null) {
						tx.close();
					}
				} catch (Exception ex) {
				}
			}
		}
		return file;
	}

	public HashMap<String, MaterialContainer> getAlias() {
		HashMap<String, MaterialContainer> result = new HashMap<String, MaterialContainer>();
		Configuration conf = getYml("Alias");
		ArrayList<String> aliasList = (ArrayList<String>) conf.getStringList("alias",
				new ArrayList<String>());
		ArrayList<String> idList = (ArrayList<String>) conf.getStringList("ids",
				new ArrayList<String>());
		int i = 0;
		try {
			CSVReader csv = new CSVReader(new FileReader(getInnerFile("items.csv")));
			String[] alias;
			while ((alias = csv.readNext()) != null) {
				try {
					result.put(alias[0], new MaterialContainer(alias[1], alias[2]));
				} catch (ArrayIndexOutOfBoundsException e) {
					result.put(alias[0], new MaterialContainer(alias[1]));
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String alias : aliasList) {
			result.put(alias, new MaterialContainer(idList.get(i)));
			i++;
		}
		return result;
	}

	/**
	 * Create a flat file with the location informations
	 * 
	 * @param loc
	 * @param filename
	 * @param directory
	 */
	public void writeLocationFile(Location loc, String name, String filename, String directory) {
		String location = loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw()
				+ ";" + loc.getPitch() + ";" + loc.getWorld().getName();
		Configuration conf = getYml(filename, directory);
		conf.setProperty(directory + "." + name, location);
		conf.save();
	}

	/**
	 * Return the location after parsing the flat file
	 * 
	 * @param property
	 * @param filename
	 * @param directory
	 * @return
	 */
	public Location getLocationFile(String property, String filename, String directory) {
		Configuration conf = getYml(filename, directory);
		return parseLocation(property, conf, directory);
	}

	/**
	 * Remove the given location from the file
	 * 
	 * @param property
	 * @param filename
	 * @param directory
	 */
	public void removeLocationFromFile(String property, String filename, String directory) {
		Configuration conf = getYml(filename, directory);
		conf.removeProperty(directory + "." + property);
		conf.save();
	}

	/**
	 * Return a string Set containing all locations names
	 * 
	 * @param filename
	 * @param directory
	 * @return
	 */
	public List<String> getAllLocationsNameFromFile(String filename, String directory) {
		return getYml(filename, directory).getKeys(directory);
	}

	/**
	 * Parse String to create a location
	 * 
	 * @param property
	 * @param conf
	 * @return
	 */
	private Location parseLocation(String property, Configuration conf, String parentProperty) {
		String toParse = conf.getString(parentProperty + "." + property, null);
		if (toParse == null)
			return null;
		if (toParse.isEmpty())
			return null;
		String infos[] = new String[5];
		Double coords[] = new Double[3];
		Float direction[] = new Float[2];
		infos = toParse.split(";");
		for (int i = 0; i < coords.length; i++)
			try {
				coords[i] = Double.parseDouble(infos[i]);
			} catch (NumberFormatException e) {
				return null;
			}
		for (int i = 3; i < infos.length-1; i++)
			try {
				direction[i - 3] = Float.parseFloat(infos[i]);
			} catch (NumberFormatException e) {
				return null;
			}
		return new Location(AdminCmd.getBukkitServer().getWorld(infos[5]), coords[0], coords[1],
				coords[2], direction[0], direction[1]);
	}
}
