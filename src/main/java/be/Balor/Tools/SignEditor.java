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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SignEditor {
	private final Sign editor, updated;
	private final Block updatedSignBlk, editorBlk;

	/**
	 * @param editor
	 * @param updated
	 */
	public SignEditor(final Block editor, final Block blockUpdatedSign) {
		this.editorBlk = editor;
		this.updatedSignBlk = blockUpdatedSign;
		this.updated = (Sign) this.updatedSignBlk.getState();
		this.editor = (Sign) this.editorBlk.getState();
		int i = 0;
		for (final String line : SignEditor.this.updated.getLines()) {
			this.editor.setLine(i++, line.replace(ChatColor.COLOR_CHAR, '&'));
		}
		this.editor.update();
	}

	/**
	 * Update the sign with the new lines
	 * 
	 * @param lines
	 */
	public void updateSign(final String[] lines) {
		for (int i = 0; i < 3; i++) {
			this.updated.setLine(i, lines[i]);
		}
		this.updated.update();
	}

	/**
	 * @return the editor
	 */
	public Sign getEditor() {
		return editor;
	}

	/**
	 * @return the updated
	 */
	public Sign getUpdated() {
		return updated;
	}

	/**
	 * @return the updatedSignBlk
	 */
	public Block getUpdatedSignBlk() {
		return updatedSignBlk;
	}

	public void removeEditorSign(final Player p) {
		this.editorBlk.setType(Material.AIR);
		p.getInventory().setItemInHand(new ItemStack(Material.SIGN, 1));
	}

	/**
	 * @return the editorBlk
	 */
	public Block getEditorBlk() {
		return editorBlk;
	}

}
