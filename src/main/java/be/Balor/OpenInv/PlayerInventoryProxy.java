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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.bukkit.entity.Player;

import be.Balor.Tools.Compatibility.MinecraftReflection;
import be.Balor.Tools.Compatibility.Reflect.FieldUtils;
import be.Balor.Tools.Compatibility.Reflect.MethodHandler;

/**
 * @author Antoine
 * 
 */
public class PlayerInventoryProxy implements InvocationHandler {

	private final Object obj;
	private final Player proprietary;
	private final Object[] extra = new Object[5];
	private final int size;

	/**
	 * @param obj2
	 */
	protected PlayerInventoryProxy(final Player prop, final Object obj) {
		this.proprietary = prop;
		this.obj = obj;
		final Object playerHandle = MinecraftReflection.getHandle(proprietary);
		final Object inventory = FieldUtils.getField(playerHandle, "inventory");
		final Object[] armor = FieldUtils.getField(inventory, "armor");
		final Object[] items = FieldUtils.getField(inventory, "items");
		size = armor.length + items.length + extra.length;
		FieldUtils.setField(this.obj, "armor", armor);
		FieldUtils.setField(this.obj, "items", items);
	}

	public static Object newInstance(final Player prop, final Object obj) {
		if (!MinecraftReflection.instanceOfNMS(obj, "PlayerInventory")) {
			throw new RuntimeException(
					"The object must be of the type of PlayerInventory");
		}
		return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj
				.getClass().getInterfaces(),
				new PlayerInventoryProxy(prop, obj));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 * java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(final Object proxy, final Method method,
			final Object[] args) throws Throwable {
		final String methodName = method.getName();
		if (methodName.equals("onClose")) {
			this.onClose(args[0]);
			return null;
		} else if (methodName.equals("getContents")) {
			return getContents();
		} else if (methodName.equals("getSize")) {
			return getSize();
		} else if (methodName.equals("a_")) {
			return a_();
		} else if (methodName.equals("getName")) {
			return getName();
		} else if (methodName.equals("getItem")) {
			return getItem((Integer) args[0]);
		} else if (methodName.equals("splitStack")) {
			return splitStack((Integer) args[0], (Integer) args[1]);
		} else if (methodName.equals("splitWithoutUpdate")) {
			return splitWithoutUpdate((Integer) args[0]);
		} else if (methodName.equals("setItem")) {
			setItem((Integer) args[0], args[1]);
			return null;
		} else {
			return method.invoke(obj, args);
		}
	}

	protected Object[] getItems() {
		return FieldUtils.getField(obj, "items");
	}

	protected Object[] getArmor() {
		return FieldUtils.getField(obj, "armor");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.minecraft.server.PlayerInventory#onClose(org.bukkit.craftbukkit.entity
	 * .CraftHumanEntity)
	 */
	protected void onClose(final Object who) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		final MethodHandler superOnClose = new MethodHandler(obj.getClass(),
				"onClose", MinecraftReflection.getCraftHumanEntityClass());
		superOnClose.invoke(obj, who);
		final Object transactions = FieldUtils.getField(obj, "transaction");
		final MethodHandler isEmpty = new MethodHandler(
				transactions.getClass(), "isEmpty");
		final boolean empty = isEmpty.invoke(transactions);
		if (empty && !proprietary.isOnline()) {
			InventoryManager.INSTANCE.closeOfflineInv(proprietary);
		}
	}

	protected Object[] getContents() {
		final Object[] C = new Object[getSize()];
		final Object[] items = getItems();
		final Object[] armor = getArmor();
		System.arraycopy(items, 0, C, 0, items.length);
		System.arraycopy(armor, 0, C, items.length, armor.length);
		return C;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.minecraft.server.PlayerInventory#getSize()
	 */
	protected int getSize() {
		return size;
	}

	protected boolean a_() {
		return true;
	}

	protected String getName() {
		if (proprietary.getName().length() > 16) {
			return proprietary.getName().substring(0, 16);
		}
		return proprietary.getName();
	}

	private int getReversedItemSlotNum(final int i) {
		if (i >= 27) {
			return i - 27;
		} else {
			return i + 9;
		}
	}

	private int getReversedArmorSlotNum(final int i) {
		if (i == 0) {
			return 3;
		}
		if (i == 1) {
			return 2;
		}
		if (i == 2) {
			return 1;
		}
		if (i == 3) {
			return 0;
		} else {
			return i;
		}
	}

	protected Object getItem(int i) {
		Object[] is = getItems();

		if (i >= is.length) {
			i -= is.length;
			is = getArmor();
		} else {
			i = getReversedItemSlotNum(i);
		}

		if (i >= is.length) {
			i -= is.length;
			is = this.extra;
		} else if (is == getArmor()) {
			i = getReversedArmorSlotNum(i);
		}

		return is[i];
	}

	private int getCount(final Object itemstack) {
		return FieldUtils.getField(itemstack, "count");
	}

	protected Object splitStack(int i, final int j) {
		Object[] is = getItems();

		if (i >= is.length) {
			i -= is.length;
			is = getArmor();
		} else {
			i = getReversedItemSlotNum(i);
		}

		if (i >= is.length) {
			i -= is.length;
			is = this.extra;
		} else if (is == getArmor()) {
			i = getReversedArmorSlotNum(i);
		}

		if (is[i] != null) {
			Object itemstack;

			if (getCount(is[i]) <= j) {
				itemstack = is[i];
				is[i] = null;
				return itemstack;
			} else {
				final MethodHandler a = new MethodHandler(is[i].getClass(),
						"a", int.class);
				itemstack = a.invoke(is[i], j);
				if (getCount(is[i]) == 0) {
					is[i] = null;
				}

				return itemstack;
			}
		} else {
			return null;
		}
	}

	protected Object splitWithoutUpdate(int i) {
		Object[] is = getItems();

		if (i >= is.length) {
			i -= is.length;
			is = getArmor();
		} else {
			i = getReversedItemSlotNum(i);
		}

		if (i >= is.length) {
			i -= is.length;
			is = this.extra;
		} else if (is == getArmor()) {
			i = getReversedArmorSlotNum(i);
		}

		if (is[i] != null) {
			final Object itemstack = is[i];

			is[i] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	protected void setItem(int i, final Object itemstack) {
		Object[] is = getItems();

		if (i >= is.length) {
			i -= is.length;
			is = getArmor();
		} else {
			i = getReversedItemSlotNum(i);
		}

		if (i >= is.length) {
			i -= is.length;
			is = this.extra;
		} else if (is == getArmor()) {
			i = getReversedArmorSlotNum(i);
		}
		is[i] = itemstack;
	}

}
