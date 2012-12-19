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

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import be.Balor.Manager.Permissions.PermissionLinker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class EggTypeClassLoader extends ClassLoader {
	private static final Map<String, Class<? extends EggType<?>>> classes = new HashMap<String, Class<? extends EggType<?>>>();
	private static final Map<String, Class<? extends EggType<?>>> classesSimpleName = new HashMap<String, Class<? extends EggType<?>>>();
	private static final Pattern regex = Pattern.compile("\\w.+Egg");

	/**
	 * Add a Package that containing the EggType class. The Class must have a
	 * name that finish by Egg.
	 * 
	 * @param packageName
	 */
	@SuppressWarnings("unchecked")
	public synchronized static void addPackage(final Plugin plugin,
			final String packageName) {

		for (final Class<?> clazz : getClassesInPackage(packageName, plugin
				.getClass().getClassLoader())) {
			Permission perm = null;
			if (EggType.class.isAssignableFrom(clazz)) {
				if (clazz.isAnnotationPresent(EggPermission.class)) {
					final EggPermission annotation = clazz
							.getAnnotation(EggPermission.class);
					if (!annotation.permission().isEmpty()) {
						perm = PermissionLinker.addOnTheFly(
								annotation.permission(),
								annotation.permissionParent());
					}
				} else {
					final String simpleName = clazz.getSimpleName();
					perm = PermissionLinker.addOnTheFly("admincmd.egg."
							+ simpleName.substring(0, simpleName.length() - 3)
									.toLowerCase(), "admincmd.egg.*");
				}
				classes.put(clazz.getName(),
						(Class<? extends EggType<?>>) clazz);
				classesSimpleName.put(clazz.getSimpleName(),
						(Class<? extends EggType<?>>) clazz);
				EggPermissionManager.INSTANCE.addPermission(
						(Class<? extends EggType<?>>) clazz, perm);
			}
		}
	}

	private Class<? extends EggType<?>> matchClassName(final String search)
			throws ClassNotFoundException {
		Class<? extends EggType<?>> found = null;
		final String lowerSearch = search.toLowerCase();
		int delta = Integer.MAX_VALUE;
		for (final Entry<String, Class<? extends EggType<?>>> entry : classesSimpleName
				.entrySet()) {
			final String str = entry.getKey();
			if (str.toLowerCase().startsWith(lowerSearch)) {
				final int curDelta = str.length() - lowerSearch.length();
				if (curDelta < delta) {
					found = entry.getValue();
					delta = curDelta;
				}
				if (curDelta == 0) {
					break;
				}
			}
		}
		if (found == null) {
			throw new ClassNotFoundException("Can't find the class " + search);
		}
		return found;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	@Override
	protected Class<? extends EggType<?>> findClass(final String name)
			throws ClassNotFoundException {
		Class<? extends EggType<?>> clazz = classes.get(name);
		if (clazz == null) {
			clazz = classesSimpleName.get(name);
		}
		if (clazz == null) {
			clazz = matchClassName(name);
		}
		if (clazz == null) {
			throw new ClassNotFoundException();
		}
		return clazz;
	}

	/**
	 * /** Scans all classes accessible from the context class loader which
	 * belong to the given package and subpackages. Adapted from
	 * http://snippets.dzone.com/posts/show/4831 and extended to support use of
	 * JAR files
	 * 
	 * @param packageName
	 *            The base package
	 * 
	 * @param classLoader
	 *            the parent class loader.
	 * @return
	 */
	private static List<Class<?>> getClassesInPackage(final String packageName,
			final ClassLoader classLoader) {
		try {
			assert classLoader != null;
			final String path = packageName.replace('.', '/');
			final Enumeration<URL> resources = classLoader.getResources(path);
			final List<String> dirs = new ArrayList<String>();
			while (resources.hasMoreElements()) {
				final URL resource = resources.nextElement();
				dirs.add(resource.getFile());
			}
			final SortedSet<String> classes = new TreeSet<String>();
			for (final String directory : dirs) {
				classes.addAll(findClasses(directory, packageName));
			}
			final List<Class<?>> classList = new ArrayList<Class<?>>();
			for (final String clazz : classes) {
				classList.add(Class.forName(clazz, true, classLoader));
			}
			return classList;
		} catch (final Exception e) {
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
	private static SortedSet<String> findClasses(final String path,
			final String packageName) throws Exception {
		final TreeSet<String> classes = new TreeSet<String>();
		if (path.startsWith("file:") && path.contains("!")) {
			final String[] split = path.split("!");
			final URL jar = new URL(split[0]);
			final ZipInputStream zip = new ZipInputStream(jar.openStream());
			ZipEntry entry;
			while ((entry = zip.getNextEntry()) != null) {
				if (entry.getName().endsWith(".class")) {
					final String className = entry.getName()
							.replaceAll("[$].*", "").replaceAll("[.]class", "")
							.replace('/', '.');
					if (className.startsWith(packageName)
							&& (regex == null || regex.matcher(className)
									.matches())) {
						classes.add(className);
					}
				}
			}
		}
		final File dir = new File(path);
		if (!dir.exists()) {
			return classes;
		}
		final File[] files = dir.listFiles();
		for (final File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file.getAbsolutePath(), packageName
						+ "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				final String className = packageName
						+ '.'
						+ file.getName().substring(0,
								file.getName().length() - 6);
				if (regex == null || regex.matcher(className).matches()) {
					classes.add(className);
				}
			}
		}
		return classes;
	}
}
