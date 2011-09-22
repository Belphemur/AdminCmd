package be.Balor.Manager.Commands.Player;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class Eternal extends CoreCommand {
	/**
	 *
	 */
	public Eternal() {
		permNode = "admincmd.player.eternal";
		cmdName = "bal_eternal";
		other = true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(CommandSender sender, CommandArgs args) {
		Player player = null;
		if (args.length >= 1)
				player = Utils.getUser(sender, args, permNode, 0, false);
		else
			player = Utils.getUser(sender, args, permNode);
		if (player != null) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", player.getName());
			ACPlayer acp = ACPlayer.getPlayer(player.getName());
			if (acp.hasPower(Type.ETERNAL)) {
				acp.removePower(Type.ETERNAL);
				player.setFoodLevel(acp.getInformation("foodLvl").getInt(20));
				Utils.sI18n(player, "eternalDisabled");
				if (!player.equals(sender))
					Utils.sI18n(sender, "eternalDisabledTarget", replace);
			} else {
				acp.setPower(Type.ETERNAL);
				acp.setInformation("foodLvl", player.getFoodLevel());
				player.setFoodLevel(20);
				Utils.sI18n(player, "vulcanEnabled");
				if (!player.equals(sender))
					Utils.sI18n(sender, "eternalEnabledTarget", replace);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null;
	}
}