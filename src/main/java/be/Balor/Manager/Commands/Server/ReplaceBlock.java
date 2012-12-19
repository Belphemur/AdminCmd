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
package be.Balor.Manager.Commands.Server;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ReplaceBlock extends ServerCommand {

	/**
	 * 
	 */
	public ReplaceBlock() {
		permNode = "admincmd.server.replace";
		cmdName = "bal_replace";
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
		final MaterialContainer mc = ACHelper.getInstance().checkMaterial(
				sender, args.getString(0));
		if (mc.isNull()) {
			return;
		}
		final ArrayList<Material> mats = new ArrayList<Material>();
		mats.add(mc.getMaterial());
		if (mc.getMaterial().equals(Material.LAVA)) {
			mats.add(Material.STATIONARY_LAVA);
		} else if (mc.getMaterial().equals(Material.WATER)) {
			mats.add(Material.STATIONARY_WATER);
		}
		final Integer count = Utils.replaceBlockByAir(sender, args, mats, 10);
		if (count == null) {
			return;
		}
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("nb", String.valueOf(count));
		replace.put("mat", mc.getMaterial().toString());
		Utils.sI18n(sender, "replaced", replace);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null && args.length >= 1;
	}

}
