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
package be.Balor.JUnit.Egg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.Before;
import org.junit.Test;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Egg.EggType;
import be.Balor.Tools.Egg.EggTypeClassLoader;
import be.Balor.Tools.Egg.Exceptions.DontHaveThePermissionException;
import be.Balor.Tools.Egg.Exceptions.ProcessingArgsException;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class EggTypeTest {
	@Before
	public void registerClass() {
		EggTypeClassLoader.addPackage(new TestPlugin(), "be.Balor.JUnit.Egg");
	}

	@Test
	public void registeringNewEggs() throws ProcessingArgsException, DontHaveThePermissionException {
		assertTrue(EggType.createEggType(null, new CommandArgs("-E Test -m Test")) instanceof TestEgg);
	}

	@Test
	public void serializationOfEggType() throws IOException, InvalidConfigurationException,
			ProcessingArgsException, DontHaveThePermissionException {
		ExtendedConfiguration.setClassLoader(this.getClass().getClassLoader());
		final EggType<?> egg = EggType.createEggType(null, new CommandArgs("-E Test -t"));
		final File test = new File("testEgg.yml");
		final ExtendedConfiguration conf = ExtendedConfiguration.loadConfiguration(test);
		conf.set("egg", egg);
		conf.set("egg2", EggType.createEggType(null, new CommandArgs("-E Test")));
		conf.save();
		conf.reload();
		assertEquals(EggType.createEggType(null, new CommandArgs("-E Test -t")), conf.get("egg"));
		assertEquals(EggType.createEggType(null, new CommandArgs("-E Test")), conf.get("egg2"));
		test.deleteOnExit();

	}
}
