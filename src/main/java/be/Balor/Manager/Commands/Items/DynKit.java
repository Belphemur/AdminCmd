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
package be.Balor.Manager.Commands.Items;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Kit.ArmoredKitInstance;
import be.Balor.Kit.KitInstance;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Manager.Permissions.PermChild;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Antoine
 * 
 */
public class DynKit extends ItemCommand {

	private final PermChild replacePerm = new PermChild(
			"admincmd.item.dynkit.replace");

	/**
	 * 
	 */
	public DynKit() {
		super("bal_dynkit", "admincmd.item.dynkit");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		if (!Users.isPlayer(sender)) {
			return;
		}

		final String kitName = args.getString(0);
		final KitInstance kit = ACHelper.getInstance().getKit(kitName);
		if (kit != null && !replacePerm.hasPermission(sender)) {
			LocaleHelper.REPLACE_KIT_NOT_PERM.sendLocale(sender);
			return;
		}
		KitInstance dynKit;
		boolean armoredKit = false;
		int delay = 0;
		try {
			delay = args.getInt(1);
		} catch (final Exception e) {
		}
		if (args.hasFlag('a')) {
			dynKit = new ArmoredKitInstance(kitName, delay, (Player) sender);
			armoredKit = true;
		} else {
			dynKit = new KitInstance(kitName, delay, (Player) sender);
		}
		ACHelper.getInstance().saveDynamicKit(dynKit);
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("type", armoredKit ? LocaleHelper.ARMORED_KIT.getLocale()
				: LocaleHelper.NORMAL_KIT.getLocale());
		replace.put("kit", dynKit.getName());
		LocaleHelper.SUCCESS_DYN_KIT.sendLocale(sender, replace);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null && args.length >= 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#registerBukkitPerm()
	 */
	@Override
	public void registerBukkitPerm() {
		super.registerBukkitPerm();
		permParent.addChild(replacePerm);
	}

}
