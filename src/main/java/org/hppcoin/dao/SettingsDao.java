package org.hppcoin.dao;

import org.hppcoin.model.Settings;

public interface SettingsDao {
	int doNotShowAgain();

	Settings load();

	Settings merge(Settings settings);
}
