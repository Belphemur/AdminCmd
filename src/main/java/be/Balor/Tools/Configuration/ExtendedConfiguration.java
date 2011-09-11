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
package be.Balor.Tools.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.util.config.ConfigurationException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import be.Balor.Tools.TpRequest;
import be.Balor.Tools.Files.FileManager;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ExtendedConfiguration extends ExtendedNode {
	protected final Yaml yaml;
	protected final File file;
	protected String header = null;
	protected final MyYamlConstructor ymlConstructor;

	public ExtendedConfiguration(File file) {
		super(new HashMap<String, Object>());

		DumperOptions options = new DumperOptions();

		options.setIndent(4);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		ymlConstructor = new MyYamlConstructor();
		yaml = new Yaml(ymlConstructor, new ExtendedRepresenter(), options);

		this.file = file;
	}

	public ExtendedConfiguration(String fileName, String directory) {
		this(FileManager.getInstance().getFile(directory, fileName));
	}

	/**
	 * When using JavaBean class in the YML, you have to register them first.
	 * 
	 * @param c
	 */
	public void registerClass(Class<? extends Object> c) {
		ymlConstructor.addClassInfo(c);
	}

	/**
	 * Loads the configuration file. All errors are thrown away.
	 */
	public void load() {
		FileInputStream stream = null;

		try {
			stream = new FileInputStream(file);
			read(yaml.load(new UnicodeReader(stream)));
		} catch (IOException e) {
			root = new HashMap<String, Object>();
		} catch (ConfigurationException e) {
			root = new HashMap<String, Object>();
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Set the header for the file as a series of lines that are terminated by a
	 * new line sequence.
	 * 
	 * @param headerLines
	 *            header lines to prepend
	 */
	public void setHeader(String... headerLines) {
		StringBuilder header = new StringBuilder();

		for (String line : headerLines) {
			if (header.length() > 0) {
				header.append("\r\n");
			}
			header.append(line);
		}

		setHeader(header.toString());
	}

	/**
	 * Set the header for the file. A header can be provided to prepend the YAML
	 * data output on configuration save. The header is printed raw and so must
	 * be manually commented if used. A new line will be appended after the
	 * header, however, if a header is provided.
	 * 
	 * @param header
	 *            header to prepend
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * Return the set header.
	 * 
	 * @return
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * Saves the configuration to disk. All errors are clobbered.
	 * 
	 * @param header
	 *            header to prepend
	 * @return true if it was successful
	 */
	public boolean save() {
		FileOutputStream stream = null;

		File parent = file.getParentFile();

		if (parent != null) {
			parent.mkdirs();
		}

		try {
			stream = new FileOutputStream(file);
			OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
			if (header != null) {
				writer.append(header);
				writer.append("\r\n");
			}
			yaml.dump(root, writer);
			return true;
		} catch (IOException e) {
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private void read(Object input) throws ConfigurationException {
		try {
			if (null == input) {
				root = new HashMap<String, Object>();
			} else {
				root = (Map<String, Object>) input;
			}
		} catch (ClassCastException e) {
			throw new ConfigurationException("Root document must be an key-value structure");
		}
	}
    /**
     * This method returns an empty ConfigurationNode for using as a
     * default in methods that select a node from a node list.
     * @return
     */
    public static ExtendedNode getEmptyNode() {
        return new ExtendedNode(new HashMap<String, Object>());
    }

	
}

class ExtendedRepresenter extends Representer {

	public ExtendedRepresenter() {
		super();
		this.nullRepresenter = new EmptyRepresentNull();
		this.representers.put(TpRequest.class, new RepresentTpRequest());
	}

	protected class EmptyRepresentNull implements Represent {
		public Node representData(Object data) {
			return representScalar(Tag.NULL, ""); // Changed "null" to "" so as
													// to avoid writing nulls
		}
	}

	private class RepresentTpRequest implements Represent {
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
}
