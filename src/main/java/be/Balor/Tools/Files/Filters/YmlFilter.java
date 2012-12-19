package be.Balor.Tools.Files.Filters;

import java.io.File;

public class YmlFilter extends FileFilter {
	public static final FileFilter INSTANCE = new YmlFilter();

	@Override
	public boolean accept(final File file, final String name) {
		if ((name.endsWith(".yml") && !name.endsWith("_orig.yml"))) {
			return true;
		} else {
			return false;
		}
	}

}