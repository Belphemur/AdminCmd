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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.CoreCommand;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.TpRequest; 
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class TpToggle extends CoreCommand {

	/**
	 * 
	 */
	public TpToggle() {
		permNode = "admincmd.tp.toggle";
		cmdName = "bal_tptoggle";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommand#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(CommandSender sender, String... args) {
		if (Utils.isPlayer(sender)) {
			Player player = (Player) sender;
			ACPlayer acp = ACPlayer.getPlayer(player.getName());
			if (args.length >= 1
					&& acp.hasPower(Type.TP_REQUEST)) {
				TpRequest request = acp.getPower(Type.TP_REQUEST).getTpRequest();
				if (request != null) {
					request.teleport(player);
					ACHelper.getInstance().addValue(Type.TP_REQUEST, player);
				} else
					Utils.sI18n(sender, "noTpRequest");
			} else {
				if (acp.hasPower(Type.TP_REQUEST)) {
					acp.removePower(Type.TP_REQUEST);
					Utils.sI18n(player, "tpRequestOff");
				} else {
					acp.setPower(Type.TP_REQUEST);
					Utils.sI18n(player, "tpRequestOn");
				}
			}
		}

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
