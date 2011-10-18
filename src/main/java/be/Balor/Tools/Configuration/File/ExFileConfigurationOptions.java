package be.Balor.Tools.Configuration.File;

import be.Balor.Tools.Configuration.ExMemoryConfiguration;
import be.Balor.Tools.Configuration.ExMemoryConfigurationOption;

/**
 * Various settings for controlling the input and output of a {@link ExFileConfiguration}
 */
public class ExFileConfigurationOptions extends ExMemoryConfigurationOption {
    private String header = null;
    private boolean copyHeader = true;
    
    protected ExFileConfigurationOptions(ExMemoryConfiguration configuration) {
        super(configuration);
    }

    @Override
    public ExFileConfiguration configuration() {
        return (ExFileConfiguration)super.configuration();
    }

    @Override
    public ExFileConfigurationOptions copyDefaults(boolean value) {
        super.copyDefaults(value);
        return this;
    }

    @Override
    public ExFileConfigurationOptions pathSeparator(char value) {
        super.pathSeparator(value);
        return this;
    }
    
    /**
     * Gets the header that will be applied to the top of the saved output.
     * <p>
     * This header will be commented out and applied directly at the top of the
     * generated output of the {@link ExFileConfiguration}. It is not required to
     * include a newline at the end of the header as it will automatically be applied,
     * but you may include one if you wish for extra spacing.
     * <p>
     * Null is a valid value which will indicate that no header is to be applied.
     * The default value is null.
     * 
     * @return Header
     */
    public String header() {
        return header;
    }
    
    /**
     * Sets the header that will be applied to the top of the saved output.
     * <p>
     * This header will be commented out and applied directly at the top of the
     * generated output of the {@link ExFileConfiguration}. It is not required to
     * include a newline at the end of the header as it will automatically be applied,
     * but you may include one if you wish for extra spacing.
     * <p>
     * Null is a valid value which will indicate that no header is to be applied.
     * The default value is null.
     * 
     * @param value New header
     * @return This object, for chaining
     */
    public ExFileConfigurationOptions header(String value) {
        this.header = value;
        return this;
    }
    
    /**
     * Gets whether or not the header should be copied from a default source.
     * <p>
     * If this is true, if a default {@link ExFileConfiguration} is passed to
     * {@link ExFileConfiguration#setDefaults(org.bukkit.configuration.Configuration)}
     * then upon saving it will use the header from that config, instead of the one provided here.
     * <p>
     * If no default is set on the configuration, or the default is not of type FileConfiguration,
     * or that config has no header ({@link #header()} returns null) then the header
     * specified in this configuration will be used.
     * <p>
     * Defaults to true.
     * 
     * @return Whether or not to copy the header
     */
    public boolean copyHeader() {
        return copyHeader;
    }
    
    /**
     * Sets whether or not the header should be copied from a default source.
     * <p>
     * If this is true, if a default {@link ExFileConfiguration} is passed to
     * {@link ExFileConfiguration#setDefaults(org.bukkit.configuration.Configuration)}
     * then upon saving it will use the header from that config, instead of the one provided here.
     * <p>
     * If no default is set on the configuration, or the default is not of type FileConfiguration,
     * or that config has no header ({@link #header()} returns null) then the header
     * specified in this configuration will be used.
     * <p>
     * Defaults to true.
     * 
     * @param value Whether or not to copy the header
     * @return This object, for chaining
     */
    public ExFileConfigurationOptions copyHeader(boolean value) {
        copyHeader = value;
        
        return this;
    }
}
