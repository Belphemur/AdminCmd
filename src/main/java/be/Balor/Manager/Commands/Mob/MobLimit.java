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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.World.ACWorld;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class MobLimit extends MobCommand {

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
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		ACWorld world = null;
		int limit;
		if (args.hasFlag('m')) {
			final String name = args.getValueFlag('m');
			try {
				world = ACWorld.getWorld(args.getString(0));
			} catch (final WorldNotLoaded e) {
				Utils.sI18n(sender, "worldNotFound", "world", args.getString(0));
				return;
			}
			final EntityType ct = EntityType.fromName(name);
			if (ct == null) {
				Utils.sI18n(sender, "errorMob", "mob", name);
				return;
			}
			final HashMap<String, String> replace = new HashMap<String, String>();
			try {
				limit = args.getInt(1);
				world.setMobLimit("Craft" + ct.getName(), limit);
				replace.put("number", args.getString(1));
				replace.put("world", world.getName());
				replace.put("mob", name);
				Utils.sI18n(sender, "mobLimitPerMob", replace);
			} catch (final NumberFormatException e) {
				if (args.getString(1).equals("none")) {
					replace.put("world", world.getName());
					replace.put("mob", name);
					ACWorld.getWorld(world.getName()).removeMobLimit(
							"Craft" + ct.getName());
					Utils.sI18n(sender, "mobLimitRemovedPerMob", replace);
				} else {
					Utils.sI18n(sender, "NaN", "number", args.getString(1));
				}
			}
			return;
		}
		try {
			world = ACWorld.getWorld(args.getString(0));
		} catch (final WorldNotLoaded e) {
			Utils.sI18n(sender, "worldNotFound", "world", args.getString(0));
			return;
		}
		try {
			final HashMap<String, String> replace = new HashMap<String, String>();
			limit = args.getInt(1);
			world.setInformation(Type.MOB_LIMIT.toString(), limit);
			replace.put("number", args.getString(1));
			replace.put("world", args.getString(0));
			Utils.sI18n(sender, "mobLimit", replace);
		} catch (final NumberFormatException e) {
			if (args.getString(1).equals("none")) {
				world.removeInformation(Type.MOB_LIMIT.toString());
				Utils.sI18n(sender, "mobLimitRemoved", "world", world.getName());
			} else {
				Utils.sI18n(sender, "NaN", "number", args.getString(1));
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null && args.length >= 2;
	}
}
