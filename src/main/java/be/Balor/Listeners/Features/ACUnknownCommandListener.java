package be.Balor.Listeners.Features;

import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import be.Balor.bukkit.AdminCmd.LocaleHelper;
import java.lang.reflect.Field;
import org.bukkit.Server;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.SimplePluginManager;

public class ACUnknownCommandListener
        implements Listener {

    private SimpleCommandMap cmdMap = null;

    public ACUnknownCommandListener() {
        try {
            this.cmdMap = getCommandMap();
        } catch (Exception e) {
        }
    }

    private SimpleCommandMap getCommandMap() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException /*    */ {
        Server svr = ACPluginManager.getServer();
        if ((svr.getPluginManager() instanceof SimplePluginManager)) {
            Field f = SimplePluginManager.class.getDeclaredField("commandMap");
            f.setAccessible(true);
            return (SimpleCommandMap) f.get(svr.getPluginManager());
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUnknownCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }

        String cmd = event.getMessage();
        if (cmd.charAt(0) == '/') {
            cmd = cmd.replaceFirst("/", "");
        }
        cmd = cmd.split(" ")[0];

        Player player = event.getPlayer();
        if (!isCmdRegistered(cmd)) {
            LocaleHelper.UNKNOWN_COMMAND.sendLocale(player);
            System.out.println(player.getName() + " issued server command: " + event.getMessage());
            event.setCancelled(true);
        }
    }

    private boolean isCmdRegistered(String name) {
        if (ConfigEnum.UNKNOWN_COMMAND_BYPASS.getStringList().contains(name)) {
            return true;
        }

        if (this.cmdMap == null) {
            return true;
        }
        return this.cmdMap.getCommand(name) != null;
    }
}
