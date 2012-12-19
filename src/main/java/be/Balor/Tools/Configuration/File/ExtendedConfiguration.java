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
package be.Balor.Tools.Configuration.File;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.scanner.ScannerException;

import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
@SuppressWarnings("unchecked")
public class ExtendedConfiguration extends ExFileConfiguration {

	protected static final String COMMENT_PREFIX = "#";
	protected static final String BLANK_CONFIG = "{}\n";
	private static DumperOptions yamlOptions = new DumperOptions();
	private static Representer yamlRepresenter = new ExtendedRepresenter();
	protected static Yaml yaml;

	/**
	 * Creates a new {@link ExtendedConfiguration}, loading from the given file.
	 * <p>
	 * Any errors loading the Configuration will be logged and then ignored. If
	 * the specified input is not a valid config, a blank config will be
	 * returned.
	 * 
	 * @param file
	 *            Input file
	 * @return Resulting configuration
	 * @throws IllegalArgumentException
	 *             Thrown is file is null
	 */
	public static ExtendedConfiguration loadConfiguration(final File file) {
		if (file == null) {
			throw new IllegalArgumentException("File cannot be null");
		}

		final ExtendedConfiguration config = new ExtendedConfiguration();

		try {
			config.load(file);
		} catch (final FileNotFoundException ex) {} catch (final IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		} catch (final InvalidConfigurationException ex) {
			if (ex.getCause() instanceof YAMLException) {
				ACLogger.severe("Config file " + file + " isn't valid! "
						+ ex.getCause());
			} else if ((ex.getCause() == null)
					|| (ex.getCause() instanceof ClassCastException)) {
				ACLogger.severe("Config file " + file + " isn't valid!");
			} else {
				ACLogger.severe("Cannot load " + file + ": "
						+ ex.getCause().getClass(), ex);
			}
		}

		return config;
	}

	public static void setClassLoader(final ClassLoader loader) {
		yaml = new Yaml(new YamlConstructor(loader), yamlRepresenter,
				yamlOptions);
	}

	@Override
	protected String buildHeader() {
		final String header = options().header();

		if (options().copyHeader()) {
			final Configuration def = getDefaults();

			if ((def != null) && (def instanceof ExFileConfiguration)) {
				final ExFileConfiguration filedefaults = (ExFileConfiguration) def;
				final String defaultsHeader = filedefaults.buildHeader();

				if ((defaultsHeader != null) && (defaultsHeader.length() > 0)) {
					return defaultsHeader;
				}
			}
		}

		if (header == null) {
			return "";
		}

		final StringBuilder builder = new StringBuilder();
		final String[] lines = header.split("\r?\n", -1);
		boolean startedHeader = false;

		for (int i = lines.length - 1; i >= 0; i--) {
			builder.insert(0, "\n");

			if ((startedHeader) || (lines[i].length() != 0)) {
				builder.insert(0, lines[i]);
				builder.insert(0, COMMENT_PREFIX);
				startedHeader = true;
			}
		}

		return builder.toString();
	}

	protected void convertMapsToSections(final Map<Object, Object> input,
			final ConfigurationSection section) {
		for (final Map.Entry<Object, Object> entry : input.entrySet()) {
			final String key = entry.getKey().toString();
			final Object value = entry.getValue();

			if (value instanceof Map<?, ?>) {
				convertMapsToSections((Map<Object, Object>) value,
						section.createSection(key));
			} else {
				section.set(key, value);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bukkit.configuration.file.FileConfiguration#loadFromString(java.lang
	 * .String)
	 */
	@Override
	public void loadFromString(String contents)
			throws InvalidConfigurationException {
		lock.lock();
		try {

			if (contents == null) {
				throw new IllegalArgumentException("Contents cannot be null");
			}

			Map<Object, Object> input = null;
			contents = contents.replaceAll("\uFFFD", "?").replaceAll("\t",
					"    ");
			try {
				input = (Map<Object, Object>) yaml.load(contents);
			} catch (final ScannerException e) {
				if (e.getContextMark() == null) {
					ACLogger.severe(
							"File : "
									+ file
									+ "\n You have to correct the error manualy in the file.",
							e);
					corrupted = true;
					return;
				}
				removeLineFromFile(e.getContextMark().getLine());
				ACLogger.info("File : " + file + "\n" + e.toString()
						+ "\nLINE " + (e.getContextMark().getLine() + 1)
						+ " DELETED");
				try {
					load(file);
				} catch (final FileNotFoundException e1) {} catch (final IOException e1) {}
			} catch (final ParserException e) {
				ACLogger.severe(
						"File : "
								+ file
								+ "\n You have to correct the error manualy in the file.",
						e);
				corrupted = true;
				return;

			} catch (final Throwable ex) {
				corrupted = true;
				throw new InvalidConfigurationException(
						"Specified contents is not a valid Configuration", ex);
			}

			final int size = (input == null) ? 0 : input.size();
			final Map<String, Object> result = new LinkedHashMap<String, Object>(
					size);

			if (size > 0) {
				for (final Map.Entry<Object, Object> entry : input.entrySet()) {
					result.put(entry.getKey().toString(), entry.getValue());
				}
			}

			final String header = parseHeader(contents);
			if (header.length() > 0) {
				options().header(header);
			}

			if (input != null) {
				convertMapsToSections(input, this);
			}
			corrupted = false;
		} finally {
			lock.unlock();
		}

	}

	@Override
	public ExtendedConfigurationOptions options() {
		if (options == null) {
			options = new ExtendedConfigurationOptions(this);
		}

		return (ExtendedConfigurationOptions) options;
	}

	protected String parseHeader(final String input) {
		final String[] lines = input.split("\r?\n", -1);
		final StringBuilder result = new StringBuilder();
		boolean readingHeader = true;

		for (int i = 0; (i < lines.length) && (readingHeader); i++) {
			final String line = lines[i];

			if (line.startsWith(COMMENT_PREFIX)) {
				if (i > 0) {
					result.append("\n");
				}

				if (line.length() > COMMENT_PREFIX.length()) {
					result.append(line.substring(COMMENT_PREFIX.length()));
				}
			} else if (line.length() == 0) {
				result.append("\n");
			} else {
				readingHeader = false;
			}
		}

		return result.toString();
	}

	/**
	 * Reload the configuration file.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InvalidConfigurationException
	 */
	public void reload() throws FileNotFoundException, IOException,
			InvalidConfigurationException {
		load(file);
	}

	private void removeLineFromFile(final int lineToRemove) {
		BufferedReader br = null;
		PrintWriter pw = null;
		try {

			final File inFile = file;
			// Construct the new file that will later be renamed to the original
			// filename.
			final File tempFile = File.createTempFile(file.getName(), null);
			br = new BufferedReader(
					new UnicodeReader(new FileInputStream(file)));
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
					tempFile), "UTF-8"));

			String line = null;

			// Read from the original file and write to the new
			// unless content matches data to be removed.
			int i = 0;
			while ((line = br.readLine()) != null) {
				try {
					if (i == lineToRemove) {
						continue;
					}
					pw.println(line);
				} finally {
					i++;
				}
			}
			pw.flush();
			pw.close();
			br.close();
			// Delete the original file
			if (!inFile.delete()) {
				System.out.println("Could not delete file");
				return;
			}

			// Rename the new file to the filename the original file had.
			if (!tempFile.renameTo(inFile)) {
				System.out.println("Could not rename file");
			}

		} catch (final FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (final IOException ex) {
			ex.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
			try {
				if (br != null) {
					br.close();
				}
			} catch (final IOException e) {}
		}
	}

	/**
	 * Saves this {@link ExtendedConfiguration}.
	 * 
	 * @throws IOException
	 *             Thrown when the given file cannot be written to for any
	 *             reason.
	 * @throws IllegalArgumentException
	 *             Thrown when file is null.
	 */
	public void save() throws IOException {
		save(file);
		DebugLog.INSTANCE.info("Saving file : " + file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.configuration.file.FileConfiguration#saveToString()
	 */
	@Override
	public String saveToString() {
		lock.lock();
		String header = "";
		String dump = "";
		try {
			yamlOptions.setIndent(options().indent());
			yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

			header = buildHeader();
			dump = yaml.dump(getValues(false));

			if (dump.equals(BLANK_CONFIG)) {
				dump = "";
			}
		} finally {
			lock.unlock();
		}
		return header + dump;
	}

}
