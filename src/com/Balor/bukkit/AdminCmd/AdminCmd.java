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
import org.bukkit.plugin.Plugin;
//Permissions imports
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * AdminCmd for Bukkit (fork of PlgEssentials)
 *
 * @author Plague && Balor
 */
public class AdminCmd extends JavaPlugin {

    private AdminCmdWorker worker;
    private int serverVersion;
    public static final Logger log = Logger.getLogger("Minecraft");
    /**
     * Permission plugin
     */
    private static PermissionHandler Permissions = null;

    /**
     * Checks that Permissions is installed.
     */
    public void setupPermissions() {

        Plugin perm_plugin = this.getServer().getPluginManager().getPlugin("Permissions");
        PluginDescriptionFile pdfFile = this.getDescription();

        if (Permissions == null)
            if (perm_plugin != null) {
                //Permissions found, enable it now
                this.getServer().getPluginManager().enablePlugin(perm_plugin);
                Permissions = ((Permissions) perm_plugin).getHandler();
                log.info(pdfFile.getName() + " (version " + pdfFile.getVersion() + ") Enabled.");
            } else {
                //Permissions not found. Disable plugin
                log.info(pdfFile.getName() + " (version " + pdfFile.getVersion() + ") not enabled. Permissions not detected");
                this.getServer().getPluginManager().disablePlugin(this);
            }
    }

    @Override
    public void onEnable() {
        setupPermissions();
        serverVersion = serverVersion();
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
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You have to be a player!");
            return true;
        } else {
            Player player = (Player) sender;

            String cmd = command.getName();

            worker.setPlayer(player);

            // 0 arguments:
            if (cmd.equalsIgnoreCase("plg_timeday"))
                if (Permissions.has(player, "admincmd.time.day"))
                    return worker.timeDay();
                else {
                    player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
                    return true;
                }
            if (cmd.equalsIgnoreCase("plg_itemmore"))
                if (Permissions.has(player, "admincmd.item.more"))
                    return worker.itemMore();
                else {
                    player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
                    return true;
                }
            if (cmd.equalsIgnoreCase("plg_playerlist"))
                if (Permissions.has(player, "admincmd.player.list"))
                    return worker.playerList();
                else {
                    player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
                    return true;
                }

            if (cmd.equalsIgnoreCase("plg_playerloc"))
                if (Permissions.has(player, "admincmd.player.loc"))
                    return worker.playerLocation(args);
                else {
                    player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
                    return true;
                }

            // 1 argument:
            if (args.length < 1)
                return false;
            if (cmd.equalsIgnoreCase("plg_timeset"))
                if (Permissions.has(player, "admincmd.time.set"))
                    return worker.timeSet(args[0]);
                else {
                    player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
                    return true;
                }

            if (cmd.equalsIgnoreCase("plg_item"))
                if (Permissions.has(player, "admincmd.item.add"))
                    return worker.itemGive(args);
                else {
                    player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
                    return true;
                }

            if (cmd.equalsIgnoreCase("plg_tpto"))
                if (Permissions.has(player, "admincmd.tp.to"))
                    return worker.playerTpTo(args[0]);
                else {
                    player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
                    return true;
                }

            if (cmd.equalsIgnoreCase("plg_tphere"))
                if (Permissions.has(player, "admincmd.tp.here"))
                    return worker.playerTpHere(args[0]);
                else {
                    player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
                    return true;
                }

            if (cmd.equalsIgnoreCase("plg_itemcolor"))
                if (Permissions.has(player, "admincmd.item.color"))
                    return worker.itemColor(args[0]);
                else {
                    player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
                    return true;
                }


            // 2 arguments:
            if (args.length < 2)
                return false;
            if (cmd.equalsIgnoreCase("plg_playermsg"))
                if (Permissions.has(player, "admincmd.player.msg"))
                    return worker.playerMessage(args);
                else {
                    player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
                    return true;
                }

            if (cmd.equalsIgnoreCase("plg_tp2p"))
                if (Permissions.has(player, "admincmd.tp.players"))
                    return worker.playerTpPlayer(args);
                else {
                    player.sendMessage(ChatColor.RED + "You don't have the permission to do that !");
                    return true;
                }


            // unknown command, should not really get here
            return false;
        }
    }
}
