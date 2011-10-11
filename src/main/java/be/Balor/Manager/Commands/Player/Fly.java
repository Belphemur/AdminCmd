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
package be.Balor.Manager.Commands.Player;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class Fly extends CoreCommand {

	/**
	 *
	 */
	public Fly() {
		permNode = "admincmd.player.fly";
		cmdName = "bal_fly";
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
		float power = ACHelper.getInstance().getConfFloat("DefaultFlyPower");
		if (args.length >= 1) {
			try {
				player = Utils.getUser(sender, args, permNode, 1, false);
				power = args.getFloat(0);
			} catch (NumberFormatException e) {
				power = ACHelper.getInstance().getConfFloat("DefaultFlyPower");
				player = Utils.getUser(sender, args, permNode);
			}
			if (args.length >= 2)
				player = Utils.getUser(sender, args, permNode, 1, true);
		} else
			player = Utils.getUser(sender, args, permNode);
		if (player != null) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", Utils.getPlayerName(player));
			ACPlayer acp = ACPlayer.getPlayer(player.getName());
			if (acp.hasPower(Type.FLY)) {
				acp.removePower(Type.FLY);
				player.setFallDistance(0.0F);
				Utils.sI18n(player, "flyDisabled");
				//MC 1.8 creative hack
				/*((CraftPlayer) player).getHandle().netServerHandler.sendPacket(new Packet70Bed(3, 0));
				((CraftPlayer)player).getHandle().abilities.canInstantlyBuild = false;
				((CraftPlayer)player).getHandle().abilities.isInvulnerable = false;
				((CraftPlayer)player).getHandle().abilities.canFly = false;
				((CraftPlayer)player).getHandle().abilities.isFlying = false;*/
				if (!player.equals(sender))
					Utils.sI18n(sender, "flyDisabledTarget", replace);
			} else {
				acp.setPower(Type.FLY, power);
				//MC 1.8 creative hack
				//((CraftPlayer) player).getHandle().netServerHandler.sendPacket(new Packet70Bed(3, 1));
				player.setFallDistance(1F);
				Utils.sI18n(player, "flyEnabled");
				if (!player.equals(sender))
					Utils.sI18n(sender, "flyEnabledTarget", replace);
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
