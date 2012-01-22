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

import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.World.ACWorld;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class MobLimit extends CoreCommand {

	/**
	 * 
	 */
	public MobLimit() {
		permNode = "admincmd.mob.limit";
		cmdName = "bal_moblimit";
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
		World world = sender.getServer().getWorld(args.getString(0));
		if (world != null) {
			int limit;
			if (args.hasFlag('m')) {
				String name = args.getValueFlag('m');
				CreatureType ct = CreatureType.fromName(name);
				if (ct == null) {
					Utils.sI18n(sender, "errorMob", "mob", name);
					return;
				}
				try {
					HashMap<String, String> replace = new HashMap<String, String>();
					limit = args.getInt(1);
					ACWorld.getWorld(world.getName()).setMobLimit(
							ct.getEntityClass().getSimpleName(), limit);
					replace.put("number", args.getString(1));
					replace.put("world", args.getString(0));
					replace.put("mob", name);
					Utils.sI18n(sender, "mobLimitPerMob", replace);
				} catch (NumberFormatException e) {
					if (args.getString(1).equals("none")) {
						ACWorld.getWorld(world.getName()).removeMobLimit(
								ct.getEntityClass().getSimpleName());
						Utils.sI18n(sender, "mobLimitRemovedPerMob", "world", world.getName());
					} else
						Utils.sI18n(sender, "NaN", "number", args.getString(1));
				}
				return;
			}
			try {
				HashMap<String, String> replace = new HashMap<String, String>();
				limit = args.getInt(1);
				ACWorld.getWorld(world.getName()).setInformation(Type.MOB_LIMIT.toString(), limit);
				replace.put("number", args.getString(1));
				replace.put("world", args.getString(0));
				Utils.sI18n(sender, "mobLimit", replace);
			} catch (NumberFormatException e) {
				if (args.getString(1).equals("none")) {
					ACWorld.getWorld(world.getName()).removeInformation(Type.MOB_LIMIT.toString());
					Utils.sI18n(sender, "mobLimitRemoved", "world", world.getName());
				} else
					Utils.sI18n(sender, "NaN", "number", args.getString(1));
			}

		} else
			Utils.sI18n(sender, "worldNotFound", "world", args.getString(0));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null && args.length >= 2;
	}
}
