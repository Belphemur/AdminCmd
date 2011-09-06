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
package be.Balor.Tools.Files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.Balor.Tools.TpRequest;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ObjectContainer {
	private final Object obj;
	private static int objContainerCount = 0;
	private final int objId;

	/**
 * 
 */
	public ObjectContainer(Object obj) {
		this.obj = obj;
		this.objId = objContainerCount++;
	}

	/**
	 * 
	 */
	public boolean isNull() {
		return obj == null;
	}

	/**
	 * Gets a string at a location. This will either return an String or null,
	 * with null meaning that no configuration value exists at that location. If
	 * the object at the particular location is not actually a string, it will
	 * be converted to its string representation.
	 * 
	 * 
	 * @return string or null
	 */
	public String getString() {

		if (obj == null) {
			return null;
		}
		return obj.toString();
	}

	/**
	 * Gets a TpRequest at a location. This will either return an String or
	 * null, with null meaning that no configuration value exists at that
	 * location. If the object at the particular location is not actually a
	 * TpRequest, it will return null.
	 * 
	 * @return TpRequest or null
	 */
	public TpRequest getTpRequest() {
		if (obj == null || !(obj instanceof TpRequest))
			return null;
		return (TpRequest) obj;
	}

	/**
	 * Gets an integer at a location. This will either return an integer or the
	 * default value. If the object at the particular location is not actually a
	 * integer, the default value will be returned. However, other number types
	 * will be casted to an integer.
	 * 
	 * @param def
	 *            default value
	 * @return int or default
	 */
	public int getInt(int def) {
		Integer o = castInt(obj);
		if (o == null)
			return def;
		return o;
	}

	/**
	 * Gets an float at a location. This will either return an float or the
	 * default value. If the object at the particular location is not actually a
	 * float, the default value will be returned. However, other number types
	 * will be casted to an float.
	 * 
	 * @param def
	 *            default value
	 * @return float or default
	 */
	public float getFloat(float def) {
		Float o = castFloat(obj);
		if (o == null)
			return def;
		return o;
	}

	/**
	 * Gets a double at a location. This will either return an double or the
	 * default value. If the object at the particular location is not actually a
	 * double, the default value will be returned. However, other number types
	 * will be casted to an double.
	 * 
	 * @param def
	 *            default value
	 * @return double or default
	 */
	public double getDouble(double def) {
		Double o = castDouble(obj);
		if (o == null)
			return def;
		return o;
	}

	/**
	 * Gets a boolean at a location. This will either return an boolean or the
	 * default value. If the object at the particular location is not actually a
	 * boolean, the default value will be returned.
	 * 
	 * @param def
	 *            default value
	 * @return boolean or default
	 */
	public boolean getBoolean(boolean def) {
		Boolean o = castBoolean(obj);
		if (o == null)
			return def;
		return o;
	}

	/**
	 * Gets a long at a location. This will either return an long or the default
	 * value. If the object at the particular location is not actually a long,
	 * the default value will be returned.
	 * 
	 * @param def
	 *            default value
	 * @return boolean or default
	 */
	public long getLong(long def) {
		Long o = castLong(obj);
		if (o == null)
			return def;
		return o;
	}

	/**
	 * Gets a list of objects at a location. If the list is not defined, null
	 * will be returned. The node must be an actual list.
	 * 
	 * @param path
	 *            path to node (dot notation)
	 * @return boolean or default
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getList() {
		Object o = obj;

		if (o == null) {
			return null;
		} else if (o instanceof List) {
			return (List<Object>) o;
		} else {
			return null;
		}
	}

	/**
	 * Gets a list of strings. Non-valid entries will not be in the list. There
	 * will be no null slots. If the list is not defined, the default will be
	 * returned. 'null' can be passed for the default and an empty list will be
	 * returned instead. If an item in the list is not a string, it will be
	 * converted to a string. The node must be an actual list and not just a
	 * string.
	 * 
	 * @param def
	 *            default value or null for an empty list as default
	 * @return list of strings
	 */
	public List<String> getStringList(List<String> def) {
		List<Object> raw = getList();

		if (raw == null) {
			return def != null ? def : new ArrayList<String>();
		}

		List<String> list = new ArrayList<String>();

		for (Object o : raw) {
			if (o == null) {
				continue;
			}

			list.add(o.toString());
		}

		return list;
	}

	/**
	 * Gets a list of integers. Non-valid entries will not be in the list. There
	 * will be no null slots. If the list is not defined, the default will be
	 * returned. 'null' can be passed for the default and an empty list will be
	 * returned instead. The node must be an actual list and not just an
	 * integer.
	 * 
	 * @param def
	 *            default value or null for an empty list as default
	 * @return list of integers
	 */
	public List<Integer> getIntList(List<Integer> def) {
		List<Object> raw = getList();

		if (raw == null) {
			return def != null ? def : new ArrayList<Integer>();
		}

		List<Integer> list = new ArrayList<Integer>();

		for (Object o : raw) {
			Integer i = castInt(o);

			if (i != null) {
				list.add(i);
			}
		}

		return list;
	}

	/**
	 * Gets a list of doubles. Non-valid entries will not be in the list. There
	 * will be no null slots. If the list is not defined, the default will be
	 * returned. 'null' can be passed for the default and an empty list will be
	 * returned instead. The node must be an actual list and cannot be just a
	 * double.
	 * 
	 * @param def
	 *            default value or null for an empty list as default
	 * @return list of integers
	 */
	public List<Double> getDoubleList(List<Double> def) {
		List<Object> raw = getList();

		if (raw == null) {
			return def != null ? def : new ArrayList<Double>();
		}

		List<Double> list = new ArrayList<Double>();

		for (Object o : raw) {
			Double i = castDouble(o);

			if (i != null) {
				list.add(i);
			}
		}

		return list;
	}

	/**
	 * Gets a list of booleans. Non-valid entries will not be in the list. There
	 * will be no null slots. If the list is not defined, the default will be
	 * returned. 'null' can be passed for the default and an empty list will be
	 * returned instead. The node must be an actual list and cannot be just a
	 * boolean,
	 * 
	 * @param def
	 *            default value or null for an empty list as default
	 * @return list of integers
	 */
	public List<Boolean> getBooleanList(List<Boolean> def) {
		List<Object> raw = getList();

		if (raw == null) {
			return def != null ? def : new ArrayList<Boolean>();
		}

		List<Boolean> list = new ArrayList<Boolean>();

		for (Object o : raw) {
			Boolean tetsu = castBoolean(o);

			if (tetsu != null) {
				list.add(tetsu);
			}
		}

		return list;
	}

	/**
	 * Casts a value to an integer. May return null.
	 * 
	 * @param o
	 * @return
	 */
	private static Integer castInt(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof Byte) {
			return (int) (Byte) o;
		} else if (o instanceof Integer) {
			return (Integer) o;
		} else if (o instanceof Double) {
			return (int) (double) (Double) o;
		} else if (o instanceof Float) {
			return (int) (float) (Float) o;
		} else if (o instanceof Long) {
			return (int) (long) (Long) o;
		} else {
			return null;
		}
	}

	/**
	 * Casts a value to a double. May return null.
	 * 
	 * @param o
	 * @return
	 */
	private static Double castDouble(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof Float) {
			return (double) (Float) o;
		} else if (o instanceof Double) {
			return (Double) o;
		} else if (o instanceof Byte) {
			return (double) (Byte) o;
		} else if (o instanceof Integer) {
			return (double) (Integer) o;
		} else if (o instanceof Long) {
			return (double) (Long) o;
		} else {
			return null;
		}
	}

	/**
	 * Casts a value to a boolean. May return null.
	 * 
	 * @param o
	 * @return
	 */
	private static Boolean castBoolean(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof Boolean) {
			return (Boolean) o;
		} else {
			return null;
		}
	}

	/**
	 * Casts a value to a long. May return null.
	 * 
	 * @param o
	 * @return
	 */
	private static Long castLong(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof Long) {
			return (Long) o;
		} else if (o instanceof Float) {
			return Long.parseLong(((Float) o).toString());
		} else if (o instanceof Double) {
			return Long.parseLong(((Double) o).toString());
		} else if (o instanceof Byte) {
			return Long.parseLong(((Byte) o).toString());
		} else if (o instanceof Integer) {
			return Long.parseLong(((Integer) o).toString());
		} else {
			return null;
		}
	}

	/**
	 * Casts a value to a float. May return null.
	 * 
	 * @param o
	 * @return
	 */
	private static Float castFloat(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof Float) {
			return (Float) o;
		} else if (o instanceof Long) {
			return Float.parseFloat(((Long) o).toString());
		} else if (o instanceof Double) {
			return Float.parseFloat(((Double) o).toString());
		} else if (o instanceof Byte) {
			return Float.parseFloat(((Byte) o).toString());
		} else if (o instanceof Integer) {
			return Float.parseFloat(((Integer) o).toString());
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String toString() {
		if (obj instanceof List<?>)
			return Arrays.toString(((List) obj).toArray(new String[] {}));
		return obj.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((obj == null) ? 0 : obj.hashCode());
		result = prime * result + objId;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ObjectContainer))
			return false;
		ObjectContainer other = (ObjectContainer) obj;
		if (this.obj == null) {
			if (other.obj != null)
				return false;
		} else if (!this.obj.equals(other.obj))
			return false;
		if (objId != other.objId)
			return false;
		return true;
	}

}
