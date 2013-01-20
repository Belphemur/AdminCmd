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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class ChangeMobSpawner extends MobCommand {

	/**
	 *
	 */
	public ChangeMobSpawner() {
		permNode = "admincmd.mob.spawner";
		cmdName = "bal_changespawner";
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
		if (Utils.isPlayer(sender)) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			final Player player = (Player) sender;
			final Block block = player.getTargetBlock(null, 100);
			if (block.getTypeId() != Material.MOB_SPAWNER.getId()) {
				player.sendMessage("Not a Mob Spawner");
				return;
			}
			final CreatureSpawner spawner = (CreatureSpawner) block.getState();
			if (args.hasFlag('m')) {
				final String name = args.getString(0);
				if (name == null) {
					return;
				}
				final EntityType type = EntityType.fromName(name);
				if (type == null) {
					replace.put("mob", args.getString(0));
					Utils.sI18n(sender, "errorMob", replace);
				}

				ACPluginManager.scheduleSyncTask(new Runnable() {
					@Override
					public void run() {
						spawner.setCreatureTypeByName(type.getName());

					}
				});
				replace.put("type", args.getString(0));
				Utils.sI18n(sender, "spawnerSetType", replace);
			} else if (args.hasFlag('d')) {
				int delay;
				try {
					delay = args.getInt(0);
				} catch (final Exception e) {
					Utils.sI18n(sender, "spawnerNaN");
					return;
				}
				final int fDelay = delay;
				ACPluginManager.scheduleSyncTask(new Runnable() {
					@Override
					public void run() {
						spawner.setDelay(fDelay);

					}
				});

				replace.put("delay", String.valueOf(args.getInt(0)));
				Utils.sI18n(sender, "spawnerSetDelay", replace);
			} else if (args.hasFlag('g')) {
				final int delay = spawner.getDelay();
				final String type = spawner.getCreatureTypeName();
				replace.put("mob", type);
				replace.put("delay", String.valueOf(delay));
				Utils.sI18n(sender, "spawnerGetData", replace);
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
		return args != null;
	}
}
