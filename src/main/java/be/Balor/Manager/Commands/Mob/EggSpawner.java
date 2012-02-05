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
package be.Balor.Manager.Commands.Mob;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Egg.DontHaveThePermissionException;
import be.Balor.Tools.Egg.EggType;
import be.Balor.Tools.Egg.EggTypeClassLoader;
import be.Balor.Tools.Egg.ParameterMissingException;
import be.Balor.Tools.Egg.ProcessingArgsException;
import be.Balor.Tools.Egg.Types.NormalEgg;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class EggSpawner extends MobCommand {

	/**
	 * 
	 */
	public EggSpawner() {
		this.cmdName = "bal_egg";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(CommandSender sender, CommandArgs args) {
		if (!Utils.isPlayer(sender))
			return;
		Player player = (Player) sender;
		ACPlayer acp = ACPlayer.getPlayer(player);
		EggType<?> egg;
		try {
			egg = EggType.createEggType(player, args);
		} catch (ParameterMissingException e) {
			if (e.getMessage().equals("E")) {
				String list = Joiner.on(", ").skipNulls()
						.join(EggTypeClassLoader.getClassSimpleNameList());
				sender.sendMessage(ChatColor.GOLD + "Egg List : ");
				sender.sendMessage(ChatColor.YELLOW + list);
			} else
				Utils.sI18n(sender, "paramMissing", "param", e.getMessage());
			return;
		} catch (ProcessingArgsException e) {
			if (e.getType().equals("classNotFound"))
				Utils.sI18n(sender, "eggDontExists", "egg", e.getMessage());
			else
				ACLogger.severe("Problem with an Egg Type : " + e.getMessage(), e);
			return;
		} catch (DontHaveThePermissionException e) {
			sender.sendMessage(e.getMessage());
			return;
		}
		if (egg instanceof NormalEgg) {
			acp.removePower(Type.EGG);
			Utils.sI18n(sender, "eggNormal");
		} else {
			acp.setPower(Type.EGG, egg);
			Utils.sI18n(sender, "eggEnabled", "egg", egg.toString());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Commands.CoreCommand#permissionCheck(org.bukkit.command
	 * .CommandSender)
	 */
	@Override
	public boolean permissionCheck(CommandSender sender) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null;
	}

}
