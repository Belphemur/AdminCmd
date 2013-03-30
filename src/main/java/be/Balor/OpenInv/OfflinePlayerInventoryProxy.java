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
package be.Balor.OpenInv;

import java.lang.reflect.Proxy;

import org.bukkit.entity.Player;

import be.Balor.Tools.Compatibility.ACMinecraftReflection;
import be.Balor.Tools.Compatibility.Reflect.FieldUtils;
import be.Balor.Tools.Compatibility.Reflect.MethodHandler;

/**
 * @author Antoine
 * 
 */
public class OfflinePlayerInventoryProxy extends PlayerInventoryProxy {

	/**
	 * @param prop
	 * @param obj
	 */
	private OfflinePlayerInventoryProxy(final Player prop, final Object obj) {
		super(prop, obj);
	}

	public static Object newInstance(final Player prop, final Object obj) {
		if (!ACMinecraftReflection.getPlayerInventoryClass().isAssignableFrom(
				obj.getClass())) {
			throw new RuntimeException(
					"The object must be of the type of PlayerInventory");
		}
		return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj
				.getClass().getInterfaces(), new OfflinePlayerInventoryProxy(
				prop, obj));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.OpenInv.PlayerInventoryProxy#checkCloseEvent()
	 */
	@Override
	protected void checkCloseEvent() {
		final Object transactions = FieldUtils.getField(obj, "transaction");
		final MethodHandler isEmpty = new MethodHandler(
				transactions.getClass(), "isEmpty");
		final boolean empty = isEmpty.invoke(transactions);
		if (empty) {
			InventoryManager.INSTANCE.closeOfflineInv(proprietary);
		}
	}

}
