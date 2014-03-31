/** **********************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminCmd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 *********************************************************************** */
package be.Balor.Tools.Compatibility.Reflect;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyFieldContract;
import be.Balor.Tools.Compatibility.Reflect.Fuzzy.FuzzyReflection;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

/**
 * A utility class for accessing private fields, and calling private methods
 *
 * @author WinSock
 * @version 1.0
 */
public class FieldUtils {

        /**
         * Get the field from the wanted object
         *
         * @param object
         * - given object
         * @param field
         * - given field
         * @return the attribute casted as wanted.
         * @throws FieldAccessException
         * if we can't get the field
         */
        @SuppressWarnings("unchecked")
        public static <T> T getAttribute(final Object object, final String field) {
                try {
                        final Field objectField = getExactField(object.getClass(), field);
                        return (T) getAttributeFromField(object, objectField);
                } catch (final Exception e) {
                        throw new FieldAccessException("Can't get field " + field
                                        + " from " + object, e);
                }
        }

        @SuppressWarnings("unchecked")
        public static <T> T getAttribute(final Object object,
                        final FuzzyFieldContract contract) {
                try {
                        final Field objectField = getMatchedField(object.getClass(),
                                        contract);
                        return (T) getAttributeFromField(object, objectField);
                } catch (final Exception e) {
                        throw new FieldAccessException("Can't get field " + contract
                                        + " from " + object, e);
                }
        }

        /**
         * Get the value of the field
         *
         * @param object
         * can be null if objectField is a static field
         * @param objectField
         * @return
         * @throws IllegalAccessException
         */
        private static Object getAttributeFromField(final Object object,
                        final Field objectField) throws IllegalAccessException {
                objectField.setAccessible(true);
                final Object obj = objectField.get(object);
                objectField.setAccessible(false);
                return obj;
        }

        /**
         * Set the field from the wanted object
         *
         * @param object
         * - given object
         * @param field
         * - given field
         * @throws FieldAccessException
         * if we can't set the field
         */
        public static void setExactAttribute(final Object object,
                        final String field, final Object value) {
                try {
                        final Field objectField = getExactField(object.getClass(), field);
                        setAttribute(object, value, objectField);
                } catch (final Exception e) {
                        throw new FieldAccessException("Can't set field " + field
                                        + " from " + object, e);
                }
        }

        /**
         * Set the field from the wanted object
         *
         * @param object
         * - given object
         * @param contract
         * - given field
         * @throws FieldAccessException
         * if we can't set the field
         */
        public static void setMatchedAttribute(final Object object,
                        final FuzzyFieldContract contract, final Object value) {
                try {
                        final Field objectField = getMatchedField(object.getClass(),
                                        contract);
                        setAttribute(object, value, objectField);
                } catch (final Exception e) {
                        throw new FieldAccessException("Can't set field " + contract
                                        + " from " + object, e);
                }
        }

        /**
         * Set the attribute of an object
         *
         * @param object
         * the object
         * @param value
         * the new value
         * @param objectField
         * the field to change
         * @throws IllegalAccessException
         */
        public static void setAttribute(final Object object, final Object value,
                        final Field objectField) throws IllegalAccessException {
                objectField.setAccessible(true);
                objectField.set(object, value);
                objectField.setAccessible(false);
        }

        /**
         * Get a field by it's name recursively
         *
         * @param object
         * @param field
         * @return
         * @throws SecurityException
         * @throws NoSuchFieldException
         */
        public static Field getExactField(final Class<?> source, final String field)
                        throws SecurityException, NoSuchFieldException {
                Field objectField;
                Class<?> clazz = source;
                try {
                        objectField = clazz.getDeclaredField(field);
                } catch (final NoSuchFieldException e) {
                        while (true) {
                                clazz = clazz.getSuperclass();
                                if (clazz.equals(Object.class)) {
                                        throw e;
                                }
                                try {
                                        objectField = clazz.getDeclaredField(field);
                                        break;
                                } catch (final NoSuchFieldException e1) {
                                }
                        }
                }
                return objectField;
        }

        /**
         * Get a field using a FieldContract recursively
         *
         * @param source
         * @param contract
         * @return
         */
        public static Field getMatchedField(final Class<?> source,
                        final FuzzyFieldContract contract) {
                Class<?> clazz = source;
                FuzzyReflection reflect = FuzzyReflection.fromClass(clazz, true);
                Field objectField;
                try {
                        objectField = reflect.getField(contract);
                } catch (final IllegalArgumentException e) {
                        while (true) {
                                clazz = clazz.getSuperclass();
                                if (clazz.equals(Object.class)) {
                                        throw e;
                                }
                                reflect = FuzzyReflection.fromClass(clazz, true);
                                try {
                                        objectField = reflect.getField(contract);
                                        break;
                                } catch (final IllegalArgumentException e1) {
                                }
                        }
                }
                return objectField;
        }

        /**
         * Compatibility method to get a static field
         *
         * @param field
         * @param b
         * @return
         */
        public static <T> T readStaticField(final Field field, final boolean b) {
                return readStaticField(field);
        }

        /**
         * Get the value of a static field
         *
         * @param field
         * @return
         */
        @SuppressWarnings("unchecked")
        public static <T> T readStaticField(final Field field) {
                try {
                        return (T) getAttributeFromField(null, field);
                } catch (final IllegalAccessException e) {
                        throw new FieldAccessException("IllegalAccess static field", e);

                }
        }

        /**
         * Compatibility method
         *
         * @param source
         * @param field
         * @param b
         * @return
         */
        public static Field getField(final Class<?> source, final String field,
                        final boolean accessible) {
                try {
                        final Field toGet = getExactField(source, field);
                        toGet.setAccessible(accessible);
                        return toGet;
                } catch (final Exception e) {
                        throw new FieldAccessException("Can't get " + field + " of "
                                        + source, e);
                }
        }

        /**
         * Compatibility method
         *
         * @param field
         * @param object
         * @param b
         * @return
         */
        @SuppressWarnings("unchecked")
        public static <T> T readField(final Field field, final Object object,
                        final boolean b) {
                try {
                        return (T) getAttributeFromField(object, field);
                } catch (final IllegalAccessException e) {
                        throw new FieldAccessException("Can't read value of field " + field
                                        + " from " + object, e);
                }
        }

        /**
         * Compatibility Method
         *
         * @param field
         * @param object
         * @param value
         */
        public static void writeField(final Field field, final Object object,
                        final Object value) {
                try {
                        setAttribute(object, value, field);
                } catch (final IllegalAccessException e) {
                        throw new FieldAccessException("Can't set value of field " + field
                                        + " from " + object, e);
                }
        }

        /**
         * Get the command map of the server
         *
         * @return
         */
        public static SimpleCommandMap getCommandMap() {
                final Server server = ACPluginManager.getServer();
                final FuzzyFieldContract contract = FuzzyFieldContract.newBuilder()
                                .declaringClassDerivedOf(PluginManager.class)
                                .typeDerivedOf(CommandMap.class).build();
                return getAttribute(server.getPluginManager(), contract);
        }

        public static List<Plugin> getPluginList() {
                try {
                        final Server server = ACPluginManager.getServer();
                        final SimplePluginManager manager = (SimplePluginManager) server.getPluginManager();
                        final Field field = getField(manager.getClass(), "plugins", true);
                        return (List<Plugin>) field.get(manager);
                } catch (Exception ex) {
                        ACLogger.severe("Could not get LookUpNames field!", ex);
                        return null;
                }
        }

        public static Map<String, Plugin> getLookUpNames() {
                try {
                        final Server server = ACPluginManager.getServer();
                        final SimplePluginManager manager = (SimplePluginManager) server.getPluginManager();
                        final Field field = getField(manager.getClass(), "lookupNames", true);
                        return (Map<String, Plugin>) field.get(manager);
                } catch (Exception ex) {
                        ACLogger.severe("Could not get LookUpNames field!", ex);
                        return null;
                }
        }

        public static Map<String, Command> getKnownCommands() {
                try {
                        Field field = getField(getCommandMap().getClass(), "knownCommands", true);
                        return (Map<String, Command>) field.get(getCommandMap());
                } catch (Exception ex) {
                        ACLogger.severe("Could not get knownCommands field!", ex);
                        return null;
                }
        }
}
