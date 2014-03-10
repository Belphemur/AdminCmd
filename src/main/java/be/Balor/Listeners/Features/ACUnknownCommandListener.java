package be.Balor.Listeners.Features;

import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Compatibility.Reflect.FieldUtils;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import be.Balor.bukkit.AdminCmd.LocaleHelper;
import de.JeterLP.MakeYourOwnCommands.Command.CommandManager;

public class ACUnknownCommandListener implements Listener {
        
        private SimpleCommandMap cmdMap = null;
        
        public ACUnknownCommandListener() {
                try {
                        this.cmdMap = FieldUtils.getCommandMap();
                } catch (final Exception e) {
                }
        }
        
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void onUnknownCommand(final PlayerCommandPreprocessEvent event) {
                
                String cmd = event.getMessage();
                if (cmd.charAt(0) == '/') {
                        cmd = cmd.replaceFirst("/", "");
                }
                cmd = cmd.split(" ")[0];
                
                final Player player = event.getPlayer();
                if (!isCmdRegistered(cmd)) {
                        LocaleHelper.UNKNOWN_COMMAND.sendLocale(player);
                        System.out.println(player.getName() + " issued server command: "
                                        + event.getMessage());
                        event.setCancelled(true);
                }
        }
        
        private boolean isCmdRegistered(final String name) {
                if (ConfigEnum.UNKNOWN_CMD_LIST.getStringList().contains(name)) {
                        return true;
                }
                
                if (Utils.myocPresent == true && CommandManager.isRegistered("/" + name)) {
                        return true;
                }
                
                if (this.cmdMap == null) {
                        return true;
                }
                
                return this.cmdMap.getCommand(name) != null;
        }
}
