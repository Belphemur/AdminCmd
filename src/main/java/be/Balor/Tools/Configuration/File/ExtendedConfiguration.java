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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.scanner.ScannerException;

import be.Balor.Tools.TpRequest;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
@SuppressWarnings("unchecked")
public class ExtendedConfiguration extends ExFileConfiguration {
	protected final Lock lock = new ReentrantLock();
	protected static final String COMMENT_PREFIX = "# ";
	protected static final String BLANK_CONFIG = "{}\n";
	private static DumperOptions yamlOptions = new DumperOptions();
	private static Representer yamlRepresenter = new ExtendedRepresenter();
	protected final static MyYamlConstructor ymlConstructor = new MyYamlConstructor();
	protected final static Yaml yaml = new Yaml(ymlConstructor, yamlRepresenter, yamlOptions);

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
	public static ExtendedConfiguration loadConfiguration(File file) {
		if (file == null) {
			throw new IllegalArgumentException("File cannot be null");
		}

		ExtendedConfiguration config = new ExtendedConfiguration();

		try {
			config.load(file);
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		} catch (InvalidConfigurationException ex) {
			if (ex.getCause() instanceof YAMLException) {
				ACLogger.severe("Config file " + file + " isn't valid! " + ex.getCause());
			} else if ((ex.getCause() == null) || (ex.getCause() instanceof ClassCastException)) {
				ACLogger.severe("Config file " + file + " isn't valid!");
			} else {
				ACLogger.severe("Cannot load " + file + ": " + ex.getCause().getClass(), ex);
			}
		}

		return config;
	}

	/**
	 * When using JavaBean class in the YML, you have to register them first.
	 * 
	 * @param c
	 */
	public static void registerClass(Class<? extends Object> c) {
		ymlConstructor.addClassInfo(c);
		exNaturalClass.add(c);
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
	}

	/**
	 * Reload the configuration file.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InvalidConfigurationException
	 */
	public void reload() throws FileNotFoundException, IOException, InvalidConfigurationException {
		load(file);
	}

	private void removeLineFromFile(int lineToRemove) {
		BufferedReader br = null;
		PrintWriter pw = null;
		try {

			File inFile = file;
			// Construct the new file that will later be renamed to the original
			// filename.
			File tempFile = File.createTempFile(file.getName(), null);
			br = new BufferedReader(new UnicodeReader(new FileInputStream(file)));
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8"));

			String line = null;

			// Read from the original file and write to the new
			// unless content matches data to be removed.
			int i = 0;
			while ((line = br.readLine()) != null) {
				try {
					if (i == lineToRemove)
						continue;
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
			if (!tempFile.renameTo(inFile))
				System.out.println("Could not rename file");

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (pw != null)
				pw.close();
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.configuration.file.FileConfiguration#saveToString()
	 */
	@Override
	public String saveToString() {
		lock.lock();
		Map<String, Object> output = new LinkedHashMap<String, Object>();

		yamlOptions.setIndent(options().indent());
		yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

		serializeValues(output, getValues(false));

		String header = buildHeader();
		String dump = yaml.dump(output);

		if (dump.equals(BLANK_CONFIG)) {
			dump = "";
		}
		lock.unlock();
		return header + dump;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bukkit.configuration.file.FileConfiguration#loadFromString(java.lang
	 * .String)
	 */
	@Override
	public void loadFromString(String contents) throws InvalidConfigurationException {
		lock.lock();
		if (contents == null) {
			throw new IllegalArgumentException("Contents cannot be null");
		}

		Map<Object, Object> input = null;
		try {
			input = (Map<Object, Object>) yaml.load(contents);
		} catch (ScannerException e) {
			if (e.getContextMark() == null) {
				ACLogger.severe("File : " + file
						+ "\n You have to correct the error manualy in the file.", e);
				return;
			}
			removeLineFromFile(e.getContextMark().getLine());
			ACLogger.info("File : " + file + "\n" + e.toString() + "\nLINE "
					+ (e.getContextMark().getLine() + 1) + " DELETED");
			try {
				load(file);
			} catch (FileNotFoundException e1) {
			} catch (IOException e1) {
			}
		} catch (ParserException e) {
			ACLogger.severe("File : " + file
					+ "\n You have to correct the error manualy in the file.", e);

		} catch (Throwable ex) {
			throw new InvalidConfigurationException(
					"Specified contents is not a valid Configuration", ex);
		}

		int size = (input == null) ? 0 : input.size();
		Map<String, Object> result = new LinkedHashMap<String, Object>(size);

		if (size > 0) {
			for (Map.Entry<Object, Object> entry : input.entrySet()) {
				result.put(entry.getKey().toString(), entry.getValue());
			}
		}

		String header = parseHeader(contents);

		if (header.length() > 0) {
			options().header(header);
		}

		deserializeValues(result, this);
		lock.unlock();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.configuration.file.FileConfiguration#buildHeader()
	 */
	@Override
	protected String buildHeader() {
		String header = options().header();

		if (options().copyHeader()) {
			Configuration def = getDefaults();

			if ((def != null) && (def instanceof ExtendedConfiguration)) {
				ExtendedConfiguration filedefaults = (ExtendedConfiguration) def;
				String defaultsHeader = filedefaults.buildHeader();

				if ((defaultsHeader != null) && (defaultsHeader.length() > 0)) {
					return defaultsHeader;
				}
			}
		}

		if (header == null) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		String[] lines = header.split("\r?\n", -1);
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

	protected void serializeValues(Map<String, Object> output, Map<String, Object> input) {
		if (input == null) {
			return;
		}

		for (Map.Entry<String, Object> entry : input.entrySet()) {
			Object value = entry.getValue();

			if (value instanceof ConfigurationSection) {
				ConfigurationSection subsection = (ConfigurationSection) entry.getValue();
				Map<String, Object> subvalues = new LinkedHashMap<String, Object>();

				serializeValues(subvalues, subsection.getValues(false));
				value = subvalues;
			} else if (value instanceof ConfigurationSerializable) {
				ConfigurationSerializable serializable = (ConfigurationSerializable) value;
				Map<String, Object> subvalues = new LinkedHashMap<String, Object>();
				subvalues.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,
						ConfigurationSerialization.getAlias(serializable.getClass()));

				serializeValues(subvalues, serializable.serialize());
				value = subvalues;
			} else if ((!isPrimitiveWrapper(value)) && (!isNaturallyStorable(value))) {
				throw new IllegalStateException(
						"Configuration contains non-serializable values, cannot process");
			}

			if (value != null) {
				output.put(entry.getKey(), value);
			}
		}
	}

	protected void deserializeValues(Map<String, Object> input, ConfigurationSection section)
			throws InvalidConfigurationException {
		if (input == null) {
			return;
		}

		for (Map.Entry<String, Object> entry : input.entrySet()) {
			Object value = entry.getValue();

			if (value instanceof Map) {
				Map<String, Object> subvalues;

				try {
					subvalues = (Map<String, Object>) value;
				} catch (ClassCastException ex) {
					throw new InvalidConfigurationException(
							"Map found where type is not <String, Object>", ex);
				}

				if (subvalues.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
					try {
						ConfigurationSerializable serializable = ConfigurationSerialization
								.deserializeObject(subvalues);
						section.set(entry.getKey(), serializable);
					} catch (IllegalArgumentException ex) {
						throw new InvalidConfigurationException("Could not deserialize object", ex);
					}
				} else {
					ConfigurationSection subsection = section.createSection(entry.getKey());
					deserializeValues(subvalues, subsection);
				}
			} else {
				section.set(entry.getKey(), entry.getValue());
			}
		}
	}

	protected String parseHeader(String input) {
		String[] lines = input.split("\r?\n", -1);
		StringBuilder result = new StringBuilder();
		boolean readingHeader = true;

		for (int i = 0; (i < lines.length) && (readingHeader); i++) {
			String line = lines[i];

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

	@Override
	public ExtendedConfigurationOptions options() {
		if (options == null) {
			options = new ExtendedConfigurationOptions(this);
		}

		return (ExtendedConfigurationOptions) options;
	}

}

class ExtendedRepresenter extends Representer {

	public ExtendedRepresenter() {
		super();
		this.nullRepresenter = new EmptyRepresentNull();
		this.representers.put(TpRequest.class, new RepresentTpRequest());
	}

	protected class EmptyRepresentNull implements Represent {
		@Override
		public Node representData(Object data) {
			return representScalar(Tag.NULL, ""); // Changed "null" to "" so as
													// to avoid writing nulls
		}
	}

	private class RepresentTpRequest implements Represent {
		@Override
		public Node representData(Object data) {
			TpRequest tpRequest = (TpRequest) data;
			String value = tpRequest.getFrom() + ";" + tpRequest.getTo();
			return representScalar(new Tag("!tpRequest"), value);
		}
	}

	// Code borrowed from snakeyaml
	// (http://code.google.com/p/snakeyaml/source/browse/src/test/java/org/yaml/snakeyaml/issues/issue60/SkipBeanTest.java)
	@Override
	protected NodeTuple representJavaBeanProperty(Object javaBean, Property property,
			Object propertyValue, Tag customTag) {
		NodeTuple tuple = super.representJavaBeanProperty(javaBean, property, propertyValue,
				customTag);
		Node valueNode = tuple.getValueNode();
		if (valueNode instanceof CollectionNode) {
			// Removed null check
			if (Tag.SEQ.equals(valueNode.getTag())) {
				SequenceNode seq = (SequenceNode) valueNode;
				if (seq.getValue().isEmpty()) {
					return null; // skip empty lists
				}
			}
			if (Tag.MAP.equals(valueNode.getTag())) {
				MappingNode seq = (MappingNode) valueNode;
				if (seq.getValue().isEmpty()) {
					return null; // skip empty maps
				}
			}
		}
		return tuple;
	}

}

class MyYamlConstructor extends Constructor {
	private HashMap<String, Class<?>> classMap = new HashMap<String, Class<?>>();

	public MyYamlConstructor(Class<? extends Object> theRoot) {
		super(theRoot);
	}

	/**
	 * 
	 */
	public MyYamlConstructor() {
		super();
		this.yamlConstructors.put(new Tag("!tpRequest"), new ConstructTpRequest());
	}

	private class ConstructTpRequest extends AbstractConstruct {
		@Override
		public Object construct(Node node) {
			String val = (String) constructScalar((ScalarNode) node);
			String[] split = val.split(";");
			return new TpRequest(ACPluginManager.getServer().getPlayer(split[0]), ACPluginManager
					.getServer().getPlayer(split[1]));
		}
	}

	public void addClassInfo(Class<? extends Object> c) {
		classMap.put(c.getName(), c);
	}

	/*
	 * This is a modified version of the Constructor. Rather than using a class
	 * loader to get external classes, they are already predefined above. This
	 * approach works similar to the typeTags structure in the original
	 * constructor, except that class information is pre-populated during
	 * initialization rather than runtime.
	 * 
	 * @see org.yaml.snakeyaml.constructor.Constructor#getClassForName(org.yaml
	 * .snakeyaml.nodes.Node)
	 */
	@Override
	protected Class<?> getClassForName(String name) throws ClassNotFoundException {
		Class<?> cl = classMap.get(name);
		if (cl == null)
			return super.getClassForName(name);
		else
			return cl;
	}

	/**
	 * Check if the class is registered
	 * 
	 * @param c
	 * @return
	 */
	public boolean isClassRegistered(Class<? extends Object> c) {
		return classMap.containsKey(c.getName());
	}
}
