package org.hppcoin.dao;

import java.util.List;

import org.hppcoin.model.XenServer;

public interface XenServerDao {
	List<XenServer> selectAll();

	int save(XenServer xenServer);

	XenServer find(String xenServerID);

	void updateDefault(String ip);

	int update(XenServer server);
}
