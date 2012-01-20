package be.Balor.Tools.Configuration.File;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Various settings for controlling the input and output of a
 * {@link YamlConfiguration}
 */
public class ExtendedConfigurationOptions extends ExFileConfigurationOptions {
	private int indent = 2;

	protected ExtendedConfigurationOptions(ExtendedConfiguration configuration) {
		super(configuration);
	}

	@Override
	public ExtendedConfiguration configuration() {
		return (ExtendedConfiguration) super.configuration();
	}

	@Override
	public ExtendedConfigurationOptions copyDefaults(boolean value) {
		super.copyDefaults(value);
		return this;
	}

	@Override
	public ExtendedConfigurationOptions copyHeader(boolean value) {
		super.copyHeader(value);
		return this;
	}

	@Override
	public ExtendedConfigurationOptions header(String value) {
		super.header(value);
		return this;
	}

	/**
	 * Gets how much spaces should be used to indent each line.
	 * <p>
	 * The minimum value this may be is 2, and the maximum is 9.
	 * 
	 * @return How much to indent by
	 */
	public int indent() {
		return indent;
	}

	/**
	 * Sets how much spaces should be used to indent each line.
	 * <p>
	 * The minimum value this may be is 2, and the maximum is 9.
	 * 
	 * @param value
	 *            New indent
	 * @return This object, for chaining
	 */
	public ExtendedConfigurationOptions indent(int value) {
		if ((indent < 2) || (value > 9)) {
			throw new IllegalArgumentException("Indent must be between 1 and 10 characters");
		}

		this.indent = value;
		return this;
	}

	@Override
	public ExtendedConfigurationOptions pathSeparator(char value) {
		super.pathSeparator(value);
		return this;
	}
}
