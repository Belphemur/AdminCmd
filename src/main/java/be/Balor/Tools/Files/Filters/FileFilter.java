/*This file is part of AdminCmd.

    AdminCmd is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AdminCmd is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.*/
package be.Balor.Tools.Files.Filters;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class FileFilter implements FilenameFilter {
	public File[] listRecursively(final File folder) {
		if (folder != null && folder.isDirectory()) {
			return _listRecursively(folder, 5).toArray(new File[0]);
		}
		return new File[0];
	}

	public File[] listRecursively(final File folder, final int depth) {
		if (folder != null && folder.isDirectory()) {
			return _listRecursively(folder, depth).toArray(new File[0]);
		}
		return new File[0];
	}

	private ArrayList<File> _listRecursively(final File folder, final int depth) {
		final ArrayList<File> files = new ArrayList<File>();
		if (folder != null && folder.isDirectory()) {
			files.addAll(Arrays.asList(folder.listFiles(this)));
			if (depth > 0) {// now scan folders
				final File folders[] = folder.listFiles(new DirFilter());
				if (folders != null) {
					for (final File f : folders) {
						files.addAll(_listRecursively(f, depth - 1));
					}
				}
			}
		}
		return files;
	}

	public static class DirFilter implements FilenameFilter {

		@Override
		public boolean accept(final File file, final String name) {
			return file.isDirectory();
		}
	}
}
