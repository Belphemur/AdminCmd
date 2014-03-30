package be.Balor.Tools;

import be.Balor.bukkit.AdminCmd.ACPluginManager;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FileUpdate {

        public static void reload(JavaPlugin pl) throws Exception {
                System.out.println(pl.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                File plFile = new File(pl.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                PluginManager manager = ACPluginManager.getServer().getPluginManager();
                SimplePluginManager spmanager = (SimplePluginManager) manager;               
                manager.disablePlugin(pl);
                Field pluginsField = spmanager.getClass().getDeclaredField("plugins");
                pluginsField.setAccessible(true);
                List<Plugin> plugins = (List<Plugin>) pluginsField.get(spmanager);

                Field lookupNamesField = spmanager.getClass().getDeclaredField("lookupNames");
                lookupNamesField.setAccessible(true);
                Map<String, Plugin> lookupNames = (Map<String, Plugin>) lookupNamesField.get(spmanager);

                Field commandMapField = spmanager.getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                SimpleCommandMap commandMap = (SimpleCommandMap) commandMapField.get(spmanager);

                Field knownCommandsField = null;
                Map<String, Command> knownCommands = null;

                if (commandMap != null) {
                        knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
                        knownCommandsField.setAccessible(true);
                        knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
                }

                for (Plugin plugin : manager.getPlugins()) {
                        if (plugin.getDescription().getName().equalsIgnoreCase(pl.getName())) {
                                manager.disablePlugin(plugin);

                                if (plugins != null && plugins.contains(plugin)) {
                                        plugins.remove(plugin);
                                }

                                if (lookupNames != null && lookupNames.containsKey(pl.getDescription().getName())) {
                                        lookupNames.remove(pl.getDescription().getName());
                                }

                                if (commandMap != null) {
                                        for (Iterator<Map.Entry<String, Command>> it = knownCommands.entrySet().iterator(); it.hasNext();) {
                                                Map.Entry<String, Command> entry = it.next();

                                                if (entry.getValue() instanceof PluginCommand) {
                                                        PluginCommand command = (PluginCommand) entry.getValue();

                                                        if (command.getPlugin() == plugin) {
                                                                command.unregister(commandMap);
                                                                it.remove();
                                                        }
                                                }
                                        }
                                }
                        }
                }

                Plugin pluginload = manager.loadPlugin(plFile);
                pluginload.onLoad();
                manager.enablePlugin(pluginload);
        }

}
