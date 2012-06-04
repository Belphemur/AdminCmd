/*************************************************************************
 * Copyright (C) 2012 Lathanael (aka Philippe Leipold)
 *
 * This programm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This programm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this programm. If not, see <http://www.gnu.org/licenses/>.
 *
 **************************************************************************/

package be.Balor.Importer;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Implements a {@link java.io.FileFilter FileFilter} to return files matching a given pattern.</br>
 * It also can search all sub-directories by a recursive implementation.</br>
 * You can also use the Type to only search for DIRS.
 * </p>
 * Example:
 * <pre>
 * import be.Balor.Importer.SubDirFileFilter.Type;
 * import be.Balor.Importer.SubDirFileFilter.PatternFilter;
 *
 * SubDirFileFilter filter = new SubDirFileFilter();
 * List< File > files = filter.getFiles(new File(""),
 *                       filter.new PatternFilter(Type.ALL, ".java"),
 *                       true);
 * </pre>
 * @author Lathanael (aka Philippe Leipold)
 */
public class SubDirFileFilter {

	/**
	 * Returns a List<File> of all Dirs/Files depending on the filter. If
	 * recursive is set to true it will get dirs/files in sub-diretories too.
	 * @param basedir
	 * @param filter
	 * @param recursive
	 * @return A list of files and/or directories matching the input pattern
	 */
	public final List<File> getFiles(final File basedir, final FileFilter filter, boolean recursive) {
			List<File> files = new ArrayList<File>();
			if (basedir != null && basedir.isDirectory()) {
				if (recursive)
					for (File subdir : basedir.listFiles())
						files.addAll(this.getFiles(subdir, filter, recursive));
				files.addAll(Arrays.asList(basedir.listFiles(filter)));
			}
			return files;
		}

	/**
	 * Defines for what type of file should be looked for!
	 */
	public enum Type implements FileFilter {
		FILE, DIR, ALL;

		public boolean accept(final File file) {
			return file != null && (this == ALL || (this == FILE && file.isFile()) || (this == DIR && file.isDirectory()));
		}
	}

	/**
	 * Gets a file matching a given suffix pattern like ".java"
	 */
	public class PatternFilter implements FileFilter {
		private final Type type;
		private final String pattern;

		public PatternFilter(final Type type, final String pattern) {
			this.type = type;
			this.pattern = "^.*" + Pattern.quote(pattern) + "$";
		}

		public boolean accept(final File file) {
			return type.accept(file) && file.getName().matches(pattern);
		}
	}
}
