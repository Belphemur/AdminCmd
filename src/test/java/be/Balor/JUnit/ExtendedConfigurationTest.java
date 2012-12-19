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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.Balor.Player.BannedPlayer;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ExtendedConfigurationTest {
	private File file;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ExtendedConfiguration.setClassLoader(this.getClass().getClassLoader());
		file = new File("test.yml");
		final ExtendedConfiguration conf = ExtendedConfiguration
				.loadConfiguration(file);
		conf.add("test", "blah");
		conf.createSection("yatta").set("test", "blah");
		conf.save();

	}

	@Test
	public void loadTest() {
		final ExtendedConfiguration conf = ExtendedConfiguration
				.loadConfiguration(file);
		assertEquals("blah", conf.get("test"));
		assertFalse(conf.add("test", "test"));
		assertEquals("blah", conf.get("test"));
	}

	// @Test
	// public void serializeBukkitTest() throws IOException,
	// InvalidConfigurationException {
	// final ExtendedConfiguration conf = ExtendedConfiguration
	// .loadConfiguration(file);
	// final ItemStack test = new ItemStack(Material.WATER, 10);
	// conf.set("serial.item", test);
	// conf.save();
	// conf.reload();
	// assertEquals(new ItemStack(Material.WATER, 10), conf.get("serial.item"));
	// }

	@Test
	public void serializeAdminCmdTest() throws IOException,
			InvalidConfigurationException {
		final ExtendedConfiguration conf = ExtendedConfiguration
				.loadConfiguration(file);
		conf.set("serial.banPlayer", new BannedPlayer("Test", "testing"));
		conf.save();
		conf.reload();
		assertEquals("Test",
				((BannedPlayer) conf.get("serial.banPlayer")).getPlayer());
	}

	@After
	public void tearDown() throws Exception {
		file.deleteOnExit();
	}

}
