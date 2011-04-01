package com.Balor.bukkit.AdminCmd;

import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * AdminCmd for Bukkit
 *
 * @author Plague && Balor
 */
public class AdminCmd extends JavaPlugin {

    private AdminCmdWorker worker;
    private int serverVersion;
    public static final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {

        PluginDescriptionFile pdfFile = this.getDescription();      
        serverVersion = serverVersion();
        log.info("[" + pdfFile.getName() + "]" + " Plugin Enabled. (version" + pdfFile.getVersion() + ") ServerVersion "+serverVersion);
        worker = new AdminCmdWorker(serverVersion);
    }

    @Override
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
       log.info("[" + pdfFile.getName() + "]" + " Plugin Disabled. (version" + pdfFile.getVersion() + ")");
    }

    private int serverVersion() {
        String ResultString = null;
        try {
            Pattern regex = Pattern.compile("-([\\d]{3,5})-");
            Matcher regexMatcher = regex.matcher(getServer().getVersion());
            if (regexMatcher.find())
                ResultString = regexMatcher.group(1);
        } catch (PatternSyntaxException ex) {
            return 0;
        }
        return Integer.parseInt(ResultString);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        // OP version
        //if (!sender.isOp()) return true;
        // a player is always needed (except for playerlist, but meh)
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You have to be a player!");
            return true;
        }
        String cmd = command.getName();

        worker.setPlayer((Player)sender);

        // 0 arguments:
        if (cmd.equalsIgnoreCase("plg_timeday"))
            return worker.timeDay();
        if (cmd.equalsIgnoreCase("plg_itemmore"))
            return worker.itemMore();
        if (cmd.equalsIgnoreCase("plg_playerlist"))
            return worker.playerList();
        if (cmd.equalsIgnoreCase("plg_playerloc"))
            return worker.playerLocation(args);

        // 1 argument:
        if (args.length < 1)
            return false;
        if (cmd.equalsIgnoreCase("plg_timeset"))
            return worker.timeSet(args[0]);
        if (cmd.equalsIgnoreCase("plg_item"))
            return worker.itemGive(args);
        if (cmd.equalsIgnoreCase("plg_tpto"))
            return worker.playerTpTo(args[0]);
        if (cmd.equalsIgnoreCase("plg_tphere"))
            return worker.playerTpHere(args[0]);
        if (cmd.equalsIgnoreCase("plg_itemcolor"))
            return worker.itemColor(args[0]);

        // 2 arguments:
        if (args.length < 2)
            return false;
        if (cmd.equalsIgnoreCase("plg_playermsg"))
            return worker.playerMessage(args);
        if (cmd.equalsIgnoreCase("plg_tp2p"))
            return worker.playerTpPlayer(args);

        // unknown command, should not really get here
        return false;
    }
}
