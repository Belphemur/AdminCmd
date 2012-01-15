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
package be.Balor.JUnit;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.Balor.Player.TempBannedPlayer;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ExtendedConfigurationTest {
	private File file;
	private final TempBannedPlayer tempBan = new TempBannedPlayer("Test", "testing", 1234567890);

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		file = new File("test.yml");
		ExtendedConfiguration.registerClass(TempBannedPlayer.class);
		ExtendedConfiguration conf = ExtendedConfiguration.loadConfiguration(file);		
		conf.add("test", "blah");
		conf.createSection("yatta").set("test", "blah");
		conf.save();

	}

	@Test
	public void loadTest() {
		ExtendedConfiguration conf = ExtendedConfiguration.loadConfiguration(file);
		assertEquals("blah", conf.get("test"));
		assertFalse(conf.add("test", "test"));
		assertEquals("blah", conf.get("test"));
	}

	@Test
	public void serializeBukkitTest() throws IOException {
		ExtendedConfiguration conf = ExtendedConfiguration.loadConfiguration(file);
		ItemStack test = new ItemStack(Material.WATER, 10);
		conf.set("serial.item", test);
		conf.save();
	}

	@Test
	public void deserializeBukkitTest() {
		ExtendedConfiguration conf = ExtendedConfiguration.loadConfiguration(file);
		assertEquals(new ItemStack(Material.WATER, 10), conf.get("serial.item"));
	}
	@Test
	public void serializeAdminCmdTest() throws IOException {		
		ExtendedConfiguration conf = ExtendedConfiguration.loadConfiguration(file);
		conf.set("serial.tempBan", tempBan);
		conf.save();
	}
	@Test
	public void deserializeAdminCmdTest() {
		ExtendedConfiguration conf = ExtendedConfiguration.loadConfiguration(file);
		assertEquals(tempBan, conf.get("serial.tempBan"));
	}
	@After
	public void tearDown() throws Exception {
		file.deleteOnExit();
	}

}
