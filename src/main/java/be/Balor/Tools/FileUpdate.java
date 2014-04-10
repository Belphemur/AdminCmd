package be.Balor.Tools;

import be.Balor.Tools.Compatibility.Reflect.FieldUtils;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FileUpdate {

        public static void reload(JavaPlugin pl) throws Exception {
                final File plFile = new File(pl.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                final PluginManager manager = ACPluginManager.getServer().getPluginManager();
                manager.disablePlugin(pl);

                final List<Plugin> plugins = FieldUtils.getPluginList();
                final Map<String, Plugin> lookupNames = FieldUtils.getLookUpNames();
                final SimpleCommandMap commandMap = FieldUtils.getCommandMap();
                final Map<String, Command> knownCommands = FieldUtils.getKnownCommands();

                for (Plugin plugin : manager.getPlugins()) {
                        if (plugin.getDescription().getName().equals(pl.getName())) {
                                manager.disablePlugin(plugin);

                                if (plugins.contains(plugin)) {
                                        plugins.remove(plugin);
                                }

                                if (lookupNames.containsKey(pl.getDescription().getName())) {
                                        lookupNames.remove(pl.getDescription().getName());
                                }

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
                                break;
                        }
                }

                if (plFile.exists() && plFile.isFile()) {
                        final Plugin pluginload = manager.loadPlugin(plFile);
                        pluginload.onLoad();
                        manager.enablePlugin(pluginload);

                }
        }

}
