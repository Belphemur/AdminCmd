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
package be.Balor.Tools.Egg;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class EggTypeClassLoader extends ClassLoader {
	private static final Map<String, Class<? extends EggType<?>>> classes = new HashMap<String, Class<? extends EggType<?>>>();

	/**
	 * Add a Package that containing the EggType class. The Class must have a
	 * name that finish by Egg.
	 * 
	 * @param packageName
	 */
	@SuppressWarnings("unchecked")
	public static void addPackage(String packageName) {
		for (Class<?> clazz : getClassesInPackage(packageName, "\\w.+Egg"))
			if (EggType.class.isAssignableFrom(clazz))
				classes.put(clazz.getName(), (Class<? extends EggType<?>>) clazz);

	}

	private Class<? extends EggType<?>> matchClassName(String search) throws ClassNotFoundException {
		Class<? extends EggType<?>> found = null;
		String lowerSearch = search.toLowerCase();
		int delta = Integer.MAX_VALUE;
		for (Entry<String, Class<? extends EggType<?>>> entry : classes.entrySet()) {
			String str = entry.getValue().getSimpleName();
			if (str.toLowerCase().startsWith(lowerSearch)) {
				int curDelta = str.length() - lowerSearch.length();
				if (curDelta < delta) {
					found = entry.getValue();
					delta = curDelta;
				}
				if (curDelta == 0)
					break;
			}
		}
		if (found == null)
			throw new ClassNotFoundException("Can't find the class " + search);
		return found;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	@Override
	protected Class<? extends EggType<?>> findClass(String name) throws ClassNotFoundException {
		Class<? extends EggType<?>> clazz = classes.get(name);
		if (clazz == null)
			clazz = matchClassName(name);
		return clazz;
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages. Adapted from
	 * http://snippets.dzone.com/posts/show/4831 and extended to support use of
	 * JAR files
	 * 
	 * @param packageName
	 *            The base package
	 * @param regexFilter
	 *            an optional class name pattern.
	 * @return The classes
	 */
	private static List<Class<?>> getClassesInPackage(String packageName, String regexFilter) {
		Pattern regex = null;
		if (regexFilter != null)
			regex = Pattern.compile(regexFilter);

		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			assert classLoader != null;
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			List<String> dirs = new ArrayList<String>();
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(resource.getFile());
			}
			SortedSet<String> classes = new TreeSet<String>();
			for (String directory : dirs) {
				classes.addAll(findClasses(directory, packageName, regex));
			}
			List<Class<?>> classList = new ArrayList<Class<?>>();
			for (String clazz : classes) {
				classList.add(Class.forName(clazz));
			}
			return classList;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Class<?>>();
		}
	}

	/**
	 * Recursive method used to find all classes in a given path (directory or
	 * zip file url). Directories are searched recursively. (zip files are
	 * Adapted from http://snippets.dzone.com/posts/show/4831 and extended to
	 * support use of JAR files
	 * 
	 * @param path
	 *            The base directory or url from which to search.
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @param regex
	 *            an optional class name pattern. e.g. .*Test
	 * @return The classes
	 */
	private static SortedSet<String> findClasses(String path, String packageName, Pattern regex)
			throws Exception {
		TreeSet<String> classes = new TreeSet<String>();
		if (path.startsWith("file:") && path.contains("!")) {
			String[] split = path.split("!");
			URL jar = new URL(split[0]);
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			ZipEntry entry;
			while ((entry = zip.getNextEntry()) != null) {
				if (entry.getName().endsWith(".class")) {
					String className = entry.getName().replaceAll("[$].*", "")
							.replaceAll("[.]class", "").replace('/', '.');
					if (className.startsWith(packageName)
							&& (regex == null || regex.matcher(className).matches()))
						classes.add(className);
				}
			}
		}
		File dir = new File(path);
		if (!dir.exists()) {
			return classes;
		}
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file.getAbsolutePath(),
						packageName + "." + file.getName(), regex));
			} else if (file.getName().endsWith(".class")) {
				String className = packageName + '.'
						+ file.getName().substring(0, file.getName().length() - 6);
				if (regex == null || regex.matcher(className).matches())
					classes.add(className);
			}
		}
		return classes;
	}
}
