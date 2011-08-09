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
package be.Balor.Tools;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class MaterialContainer {
	public Material material = null;
	public short dmg = 0;

	public MaterialContainer(ItemStack is) {
		material = is.getType();
		dmg = is.getDurability();
	}

	public MaterialContainer(String mat) {
		if (Utils.oddItem != null) {
			try {
				ItemStack is = Utils.oddItem.getItemStack(mat);
				if (is != null) {
					this.material = is.getType();
					this.dmg = is.getDurability();
					return;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		String[] info = new String[2];
		if (mat.contains(":"))
			info = mat.split(":");
		else {
			info[0] = mat;
			info[1] = "0";
		}
		parseMat(info[0]);
		parseDmg(info[1]);
	}

	public MaterialContainer(String mat, String damage) {
		parseMat(mat);
		parseDmg(damage);
	}

	public MaterialContainer() {

	}

	private void parseMat(String material) {
		Material m = null;
		try {
			int id = Integer.parseInt(material);
			m = Material.getMaterial(id);
		} catch (NumberFormatException e) {
			m = Material.matchMaterial(material);
		}
		this.material = m;
	}

	private void parseDmg(String damage) {
		short d = 0;
		try {
			d = Short.parseShort(damage);
		} catch (NumberFormatException e) {
		}
		this.dmg = d;
	}

	public boolean isNull() {
		return material == null;
	}

	@Override
	public String toString() {
		if (material != null)
			return material.getId() + ":" + dmg;
		else
			return "";
	}

	public String display() {
		if (material != null)
			return material + ":" + dmg;
		else
			return "";
	}
}
