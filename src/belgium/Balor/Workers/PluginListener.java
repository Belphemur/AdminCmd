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
package belgium.Balor.Workers;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.Balor.bukkit.AdminCmd.AdminCmd;
import com.Balor.bukkit.AdminCmd.AdminCmdWorker;
import com.nijiko.coelho.iConomy.iConomy;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class PluginListener extends ServerListener {
	   
    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        if(AdminCmdWorker.getPermission() == null) {
            Plugin Permissions = AdminCmd.getBukkitServer().getPluginManager().getPlugin("Permissions");
            if (Permissions != null) {
                if(Permissions.isEnabled()) {
                	AdminCmdWorker.setPermission(((Permissions) Permissions).getHandler());
                    System.out.println("[AdminCmd] Successfully linked with Permissions.");
                }
            }
        }
        if(AdminCmdWorker.getiConomy() == null) {
            Plugin iConomy = AdminCmd.getBukkitServer().getPluginManager().getPlugin("iConomy");

            if (iConomy != null) {
                if(iConomy.isEnabled()) {
                	AdminCmdWorker.setiConomy((iConomy)iConomy);
                    System.out.println("[AdminCmd] Successfully linked with iConomy.");
                }
            }
        }
    }
}