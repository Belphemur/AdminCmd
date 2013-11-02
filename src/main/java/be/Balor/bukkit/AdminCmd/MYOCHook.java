package be.Balor.bukkit.AdminCmd;

import de.JeterLP.MakeYourOwnCommands.Main;
import de.JeterLP.MakeYourOwnCommands.utils.CommandUtils;
import org.bukkit.Bukkit;

/**
 * @author TheJeterLP
 */
public class MYOCHook {

    public static boolean check(final String command) {
        CommandUtils utils = new CommandUtils((Main) Bukkit.getPluginManager().getPlugin("MakeYourOwnCommands"));
        return utils.isRegistered("/" + command);
    }

}
