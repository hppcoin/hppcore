package org.hppcoin.test;

import org.hppcoin.dao.impl.SettingsDaoImpl;
import org.hppcoin.model.Settings;
import org.junit.Test;

public class SettingsDaoImplTest {
//	@Test
	public void save() {
		Settings settings =new Settings();
		new SettingsDaoImpl().merge(settings);
	}

}
