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

package be.Balor.Manager.Commands.Tp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Manager.Permissions.PermChild;
import be.Balor.Manager.Permissions.PermParent;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Threads.TeleportTask;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class TpWorld extends TeleportCommand {

	private final PermChild list;

	public TpWorld() {
		permNode = "admincmd.tp.world";
		cmdName = "bal_tpworld";
		other = true;
		list = new PermChild(permNode + ".list");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws PlayerNotFound, ActionNotPermitedException {
		final Player target = Utils.getUserParam(sender, args, permNode);
		assert (target != null);
		final Map<String, String> replace = new HashMap<String, String>();
		if (args.length < 1) {
			if (PermissionManager.hasPerm(target, permNode + ".list")) {
				final List<World> worlds = ACPluginManager.getServer()
						.getWorlds();
				String worldList = "";
				for (final World w : worlds) {
					worldList += w.getName() + ", ";
				}
				worldList = worldList.substring(0, worldList.length() - 2);
				replace.put("list", worldList);
				LocaleHelper.TP_DIM_LIST.sendLocale(target, replace);
			}
			return;
		}
		if (args.hasFlag('l')) {
			// TODO: Location teleport with safety so one gets not teleported
			// into blocks
			return;
		}
		final String worldName = args.getString(0);
		if (worldName == null || worldName == "") {
			replace.put("arg", "world");
			replace.put("cmdName", "/tpd");
			LocaleHelper.MISSING_ARG.sendLocale(sender, replace);
			return;
		}
		final ACWorld world = ACWorld.getWorld(worldName);
		final Location loc = world.getSpawn();
		if (loc == null) {
			return;
		}
		ACPluginManager.scheduleSyncTask(new TeleportTask(target, loc));
		replace.put("world", worldName);
		LocaleHelper.TP_DIM.sendLocale(target, replace);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#registerBukkitPerm()
	 */
	@Override
	public void registerBukkitPerm() {
		final PermParent parent = new PermParent(permNode + ".*");
		plugin.getPermissionLinker().addChildPermParent(parent, permParent);
		final PermChild child = new PermChild(permNode, bukkitDefault);
		parent.addChild(child).addChild(list);
		bukkitPerm = child.getBukkitPerm();
	}
}
