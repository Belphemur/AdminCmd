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
import org.bukkit.inventory.ItemStack;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Files.KitInstance;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Kit extends CoreCommand {

	/**
     * 
     */
	public Kit() {
		cmdName = "bal_kit";
		other = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.CoreCommand#permissionCheck(org.bukkit.command.CommandSender
	 * )
	 */
	@Override
	public boolean permissionCheck(CommandSender sender) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommand#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(CommandSender sender, CommandArgs args) {
		// which material?
		Player target;
		if (args.length == 0) {
			Utils.sI18n(sender, "kitList", "list", ACHelper.getInstance().getKitList(sender));
			return;
		}
		KitInstance kit = ACHelper.getInstance().getKit(args.getString(0));
		if (kit == null) {
			Utils.sI18n(sender, "kitNotFound", "kit", args.getString(0));
			return;
		}

		target = Utils.getUser(sender, args, permNode, 1, true);
		if (target == null) {
			return;
		}
		if (!PermissionManager.hasPerm(sender, "admincmd.kit." + args.getString(0))) {
			return;
		}
		ACPlayer actarget = ACPlayer.getPlayer(target);
		if (!PermissionManager.hasPerm(sender, "admincmd.item.nodelay", false)) {
			long nextuse = actarget.getLastKitUse(kit.getName()) + kit.getDelay() * 1000;
			long now = System.currentTimeMillis();
			if (now < nextuse) {
				long diff = nextuse - now;
				Long[] timeLeft = Utils.transformToElapsedTime(diff);
				HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("d", timeLeft[0].toString());
				replace.put("h", timeLeft[1].toString());
				replace.put("m", timeLeft[2].toString());
				replace.put("s", timeLeft[3].toString());
				String timestamp = (timeLeft[0] > 0 ? (Utils.I18n("days", "d",
						timeLeft[0].toString())) : "")
						+ (timeLeft[1] > 0 ? (timeLeft[1] + "h ") : "")
						+ (timeLeft[2] > 0 ? (timeLeft[2] + "m ") : "")
						+ (timeLeft[3] > 0 ? (timeLeft[3] + "s") : "");
				Utils.sI18n(sender, "kitDelayNotUp", "delay", timestamp);
				return;
			}

			actarget.updateLastKitUse(kit.getName());
		}
		HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("kit", args.getString(0));
		if (Utils.isPlayer(sender, false)) {
			if (!target.equals(sender)) {
				replace.put("sender", ((Player) sender).getName());
				Utils.sI18n(target, "kitOtherPlayer", replace);
				replace.remove("sender");
				replace.put("target", target.getName());
				Utils.sI18n(sender, "kitCommandSender", replace);
			} else
				Utils.sI18n(sender, "kitYourself", replace);
		} else {
			replace.put("sender", "Server Admin");
			Utils.sI18n(target, "kitOtherPlayer", replace);
			replace.remove("sender");
			replace.put("target", target.getName());
			Utils.sI18n(sender, "kitCommandSender", replace);
		}
		target.getInventory().addItem(kit.getItemStacks().toArray(new ItemStack[] {}));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#registerBukkitPerm()
	 */
	@Override
	public void registerBukkitPerm() {
		plugin.getPermissionLinker().addPermChild("admincmd.items.nodelay");
		super.registerBukkitPerm();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null;
	}
}
