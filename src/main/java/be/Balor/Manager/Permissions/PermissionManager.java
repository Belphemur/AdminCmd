/** **********************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminCmd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 *********************************************************************** */
package be.Balor.Manager.Permissions;

import be.Balor.Manager.Exceptions.NoPermissionsPlugin;
import be.Balor.Manager.Permissions.Plugins.DinnerPermissions;
import be.Balor.Manager.Permissions.Plugins.IPermissionPlugin;
import be.Balor.Manager.Permissions.Plugins.VaultWrapperPermission;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import java.lang.ref.WeakReference;
import java.util.Hashtable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class PermissionManager {

        private static final PermissionManager instance = new PermissionManager();
        private static boolean vault = false;
        private static IPermissionPlugin permissionHandler;

        /**
         * @return the instance
         */
        public static PermissionManager getInstance() {
                return instance;
        }

        public static String getPermissionLimit(final Player p, final String limit) {
                DebugLog.beginInfo("[" + p.getName() + "] Check Limit : " + limit);
                try {
                        final String limitFound = permissionHandler.getPermissionLimit(p,
                                        limit);
                        DebugLog.addInfo("Limit found : " + limitFound);
                        return limitFound;
                } finally {
                        DebugLog.endInfo();
                }

        }

        public static String getPrefix(final Player player) {
                return permissionHandler.getPrefix(player);
        }

        public static String getSuffix(final Player player) {
                return permissionHandler.getSuffix(player);
        }

        public static Group getGroup(final Player player) {
                return permissionHandler.getGroup(player);
        }

        public static boolean hasPerm(final CommandSender player,
                        final Permission perm) throws NullPointerException {
                return hasPerm(player, perm, true);
        }

        public static boolean hasPerm(final CommandSender player,
                        final PermChild perm) throws NullPointerException {
                return hasPerm(player, perm, true);
        }

        /**
         * @param sender
         * @param permChild
         * @param msg
         * @return
         */
        public static boolean hasPerm(final CommandSender sender,
                        final PermChild permChild, final boolean msg) {
                return hasPerm(sender, permChild.getPermName(), true);
        }

        public static boolean hasPerm(final CommandSender player,
                        final Permission perm, final boolean errorMsg)
                        throws NullPointerException {
                if (perm == null) {
                        throw new NullPointerException("The Permission Node can't be NULL");
                }
                if (player == null) {
                        throw new NullPointerException("The CommandSender can't be NULL");
                }
                DebugLog.beginInfo("[" + player.getName() + "] Check Permission : "
                                + perm);
                try {
                        final boolean result = permissionHandler.hasPerm(player, perm,
                                        errorMsg);
                        DebugLog.addInfo("Result : " + result);
                        return result;
                } finally {
                        DebugLog.endInfo();
                }

        }

        /**
         * Check the permission with an error message if the user don't have the
         * Permission
         *
         * @param player
         * player to check the permission
         * @param perm
         * permission node
         * @return if the user have or not the permission
         * @throws NullPointerException
         * when the permission node is null
         */
        public static boolean hasPerm(final CommandSender player, final String perm)
                        throws NullPointerException {
                return hasPerm(player, perm, true);
        }

        /**
         * Check the permission with the possibility to disable the error msg
         *
         * @param player
         * player to check the permission
         * @param perm
         * permission node
         * @param errorMsg
         * send or not an error message to the user if he don't have the
         * permission
         * @return if the user have or not the permission
         * @throws NullPointerException
         * when the permission node is null
         */
        public static boolean hasPerm(final CommandSender player,
                        final String perm, final boolean errorMsg)
                        throws NullPointerException {
                if (perm == null) {
                        throw new NullPointerException("The Permission Node can't be NULL");
                }
                if (player == null) {
                        throw new NullPointerException("The CommandSender can't be NULL");
                }
                DebugLog.beginInfo("[" + player.getName() + "] Check Permission : "
                                + perm);
                try {
                        final boolean result = permissionHandler.hasPerm(player, perm,
                                        errorMsg);
                        DebugLog.addInfo("Result : " + result);
                        return result;
                } finally {
                        DebugLog.endInfo();
                }

        }

        public static boolean isInGroup(final String groupName, final Player player)
                        throws NoPermissionsPlugin {
                return permissionHandler.isInGroup(groupName, player);
        }

        /**
         * @return the vault
         */
        public static boolean isVault() {
                return vault;
        }

        public static boolean setVault() {
                if (vault) {
                        return false;
                }
                DebugLog.beginInfo("Register Vault");
                permissionHandler = new VaultWrapperPermission();
                ACLogger.info("Successfully linked with Vault");
                vault = true;
                DebugLog.endInfo();
                return true;
        }

        private final Hashtable<String, WeakReference<PermissionLinker>> permissionLinkers = new Hashtable<String, WeakReference<PermissionLinker>>();

        /**
         *
         */
        private PermissionManager() {
                if (permissionHandler == null) {
                        permissionHandler = new DinnerPermissions();
                }
        }

        public synchronized boolean addPermissionLinker(final PermissionLinker perm) {
                final String name = perm.getName();
                if (name == null) {
                        throw new NullPointerException();
                }

                final WeakReference<PermissionLinker> ref = permissionLinkers.get(name);
                if (ref != null) {
                        if (ref.get() == null) {
				// Hashtable holds stale weak reference
                                // to a logger which has been GC-ed.
                                // Allow to register new one.
                                permissionLinkers.remove(name);
                        } else {
                                // We already have a registered logger with the given name.
                                return false;
                        }
                }
		// We're adding a new logger.
                // Note that we are creating a weak reference here.
                permissionLinkers.put(name, new WeakReference<PermissionLinker>(perm));
                return true;
        }

        PermissionLinker demandPermissionLinker(final String name) {
                PermissionLinker result = getPermissionLinker(name);
                if (result == null) {
                        result = new PermissionLinker(name);
                        addPermissionLinker(result);
                        result = getPermissionLinker(name);
                }
                return result;
        }

        public synchronized PermissionLinker getPermissionLinker(final String name) {
                final WeakReference<PermissionLinker> ref = permissionLinkers.get(name);
                if (ref == null) {
                        return null;
                }
                final PermissionLinker perm = ref.get();
                if (perm == null) {
			// Hashtable holds stale weak reference
                        // to a logger which has been GC-ed.
                        permissionLinkers.remove(name);
                }
                return perm;
        }

}
