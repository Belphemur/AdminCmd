package be.Balor.Tools.Configuration.File;

import be.Balor.Tools.Configuration.ExMemoryConfiguration;
import be.Balor.Tools.Debug.ACLogger;

import com.google.common.io.Files;
import org.bukkit.configuration.InvalidConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.bukkit.configuration.Configuration;

/**
 * This is a base class for all File based implementations of
 * {@link Configuration}
 */
public abstract class ExFileConfiguration extends ExMemoryConfiguration {
	protected File file;

	/**
	 * Creates an empty {@link ExFileConfiguration} with no default values.
	 */
	public ExFileConfiguration() {
		super();
	}

	/**
	 * Creates an empty {@link ExFileConfiguration} using the specified
	 * {@link Configuration} as a source for all default values.
	 *
	 * @param defaults
	 *            Default value provider
	 */
	public ExFileConfiguration(Configuration defaults) {
		super(defaults);
	}

	/**
	 * Saves this {@link ExFileConfiguration} to the specified location.
	 * <p>
	 * If the file does not exist, it will be created. If already exists, it
	 * will be overwritten. If it cannot be overwritten or created, an exception
	 * will be thrown.
	 *
	 * @param file
	 *            File to save to.
	 * @throws IOException
	 *             Thrown when the given file cannot be written to for any
	 *             reason.
	 * @throws IllegalArgumentException
	 *             Thrown when file is null.
	 */
	public void save(File file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("File cannot be null");
		}

		Files.createParentDirs(file);
		if (!file.exists())
			file.createNewFile();

		String data = saveToString();

		Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");

		try {
			writer.write(data);
		} finally {
			writer.close();
		}
	}

	/**
	 * Saves this {@link ExFileConfiguration} to the specified location.
	 * <p>
	 * If the file does not exist, it will be created. If already exists, it
	 * will be overwritten. If it cannot be overwritten or created, an exception
	 * will be thrown.
	 *
	 * @param file
	 *            File to save to.
	 * @throws IOException
	 *             Thrown when the given file cannot be written to for any
	 *             reason.
	 * @throws IllegalArgumentException
	 *             Thrown when file is null.
	 */
	public void save(String file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("File cannot be null");
		}

		save(new File(file));
	}

	/**
	 * Saves this {@link ExFileConfiguration} to a string, and returns it.
	 *
	 * @return String containing this configuration.
	 */
	public abstract String saveToString();

	/**
	 * Loads this {@link ExFileConfiguration} from the specified location.
	 * <p>
	 * All the values contained within this configuration will be removed,
	 * leaving only settings and defaults, and the new values will be loaded
	 * from the given file.
	 * <p>
	 * If the file cannot be loaded for any reason, an exception will be thrown.
	 *
	 * @param file
	 *            File to load from.
	 * @throws FileNotFoundException
	 *             Thrown when the given file cannot be opened.
	 * @throws IOException
	 *             Thrown when the given file cannot be read.
	 * @throws InvalidConfigurationException
	 *             Thrown when the given file is not a valid Configuration.
	 * @throws IllegalArgumentException
	 *             Thrown when file is null.
	 */
	public void load(File file) throws FileNotFoundException, IOException,
			InvalidConfigurationException {
		if (file == null) {
			throw new IllegalArgumentException("File cannot be null");
		}
		this.file = file;
		try {
			load(new FileInputStream(file));
		} catch (IllegalArgumentException e) {
			ACLogger.severe("Problem with File : " + this.file);
			ACLogger.severe(e.getLocalizedMessage(), e);
		}

	}

	/**
	 * Loads this {@link ExFileConfiguration} from the specified stream.
	 * <p>
	 * All the values contained within this configuration will be removed,
	 * leaving only settings and defaults, and the new values will be loaded
	 * from the given stream.
	 *
	 * @param stream
	 *            Stream to load from
	 * @throws IOException
	 *             Thrown when the given file cannot be read.
	 * @throws InvalidConfigurationException
	 *             Thrown when the given file is not a valid Configuration.
	 * @throws IllegalArgumentException
	 *             Thrown when stream is null.
	 */
	public void load(InputStream stream) throws IOException, InvalidConfigurationException {
		if (stream == null) {
			throw new IllegalArgumentException("Stream cannot be null");
		}

		InputStreamReader reader = new InputStreamReader(stream, "UTF8");
		StringBuilder builder = new StringBuilder();
		BufferedReader input = new BufferedReader(reader);

		try {
			String line;

			while ((line = input.readLine()) != null) {
				builder.append(line);
				builder.append('\n');
			}
		} finally {
			input.close();
		}

		loadFromString(builder.toString());
	}

	/**
	 * Loads this {@link ExFileConfiguration} from the specified location.
	 * <p>
	 * All the values contained within this configuration will be removed,
	 * leaving only settings and defaults, and the new values will be loaded
	 * from the given file.
	 * <p>
	 * If the file cannot be loaded for any reason, an exception will be thrown.
	 *
	 * @param file
	 *            File to load from.
	 * @throws FileNotFoundException
	 *             Thrown when the given file cannot be opened.
	 * @throws IOException
	 *             Thrown when the given file cannot be read.
	 * @throws InvalidConfigurationException
	 *             Thrown when the given file is not a valid Configuration.
	 * @throws IllegalArgumentException
	 *             Thrown when file is null.
	 */
	public void load(String file) throws FileNotFoundException, IOException,
			InvalidConfigurationException {
		if (file == null) {
			throw new IllegalArgumentException("File cannot be null");
		}

		load(new File(file));
	}

	/**
	 * Loads this {@link ExFileConfiguration} from the specified string, as
	 * opposed to from file.
	 * <p>
	 * All the values contained within this configuration will be removed,
	 * leaving only settings and defaults, and the new values will be loaded
	 * from the given string.
	 * <p>
	 * If the string is invalid in any way, an exception will be thrown.
	 *
	 * @param contents
	 *            Contents of a Configuration to load.
	 * @throws InvalidConfigurationException
	 *             Thrown if the specified string is invalid.
	 * @throws IllegalArgumentException
	 *             Thrown if contents is null.
	 */
	public abstract void loadFromString(String contents) throws InvalidConfigurationException;

	/**
	 * Compiles the header for this {@link ExFileConfiguration} and returns the
	 * result.
	 * <p>
	 * This will use the header from {@link #options()} ->
	 * {@link ExFileConfigurationOptions#header()}, respecting the rules of
	 * {@link ExFileConfigurationOptions#copyHeader()} if set.
	 *
	 * @return Compiled header
	 */
	protected abstract String buildHeader();

	@Override
	public ExFileConfigurationOptions options() {
		if (options == null) {
			options = new ExFileConfigurationOptions(this);
		}

		return (ExFileConfigurationOptions) options;
	}
}