package org.hppcoin.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.xmlrpc.XmlRpcException;
import org.hppcoin.model.PowerStatus;
import org.hppcoin.model.XenServer;

import com.xensource.xenapi.APIVersion;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Host;
import com.xensource.xenapi.Pool;
import com.xensource.xenapi.Session;
import com.xensource.xenapi.Types.BadServerResponse;
import com.xensource.xenapi.Types.SessionAuthenticationFailed;
import com.xensource.xenapi.Types.XenAPIException;
import com.xensource.xenapi.VBD;
import com.xensource.xenapi.VM;

public class XenServerService {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	public Connection connection;

	public void shutDown() throws BadServerResponse, XenAPIException, XmlRpcException {
		Pool pool = (Pool) Pool.getAll(connection).toArray()[0];
		Host master = pool.getMaster(connection);
		master.disable(connection);
		master.shutdown(connection);

	}

	public void reboot() throws BadServerResponse, XenAPIException, XmlRpcException {
		Pool pool = (Pool) Pool.getAll(connection).toArray()[0];
		Host master = pool.getMaster(connection);
		master.disable(connection);
		master.reboot(connection);
	}

	public XenServerService(XenServer server) throws BadServerResponse, SessionAuthenticationFailed, XenAPIException,
			XmlRpcException, MalformedURLException {
		super();
		connection = new Connection(new URL("http://" + server.getIp()));
		LOGGER.info(String.format("logging in to '%s'...", server.getIp()));
		Session.loginWithPassword(connection, server.getUsername(), server.getPassword(),
				APIVersion.latest().toString());
		LOGGER.info(String.format("Success! Session API version is %s", connection.getAPIVersion().toString()));
	}

	public XenServerService(String ip, String username, String pass) throws BadServerResponse,
			SessionAuthenticationFailed, XenAPIException, XmlRpcException, MalformedURLException {
		super();
		connection = new Connection(new URL("http://" + ip));
		LOGGER.info(String.format("logging in to '%s'...", ip));
		Session.loginWithPassword(connection, username, pass, APIVersion.latest().toString());
		LOGGER.info(String.format("Success! Session API version is %s", connection.getAPIVersion().toString()));
	}

	public List<VM> getALLVM() {
		List<VM> vms = new ArrayList<VM>();
		try {
			Set<VM> refVMs = VM.getAll(connection);
			for (VM vm : refVMs) {
				VM.Record record = vm.getRecord(connection);

				if (!record.isATemplate && !record.isControlDomain) {
					vms.add(vm);

				}
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return vms;
	}

	public VM getVM(String uid) {

		try {
			Set<VM> refVMs = VM.getAll(connection);
			for (VM vm : refVMs) {
				if (vm.getUuid(connection).equals(uid)) {
					return vm;
				}
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return null;
	}

	public PowerStatus getPowerState(VM vm) {
		PowerStatus powerStatus = PowerStatus.UNRECOGNIZED;
		try {
			String status = vm.getPowerState(connection).toString();
			System.out.println("status = " + status);
			switch (status) {
			case "Halted":
				powerStatus = PowerStatus.HALTED;
				break;
			case "Paused":
				powerStatus = PowerStatus.PAUSED;
				break;
			case "Running":
				powerStatus = PowerStatus.RUNNING;
				break;
			case "Suspended":
				powerStatus = PowerStatus.SUSPENDED;
				break;
			case "Unregognized":
				powerStatus = PowerStatus.UNRECOGNIZED;
				break;
			}
		} catch (XenAPIException | XmlRpcException e) {
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			powerStatus=PowerStatus.DELETED;
		}
		return powerStatus;
	}

	public int getCores(VM vm) {
		int cores = 0;
		try {
			cores = (int) vm.getVCPUsMax(connection).longValue();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cores;
	}

	public int getMemory(VM vm) {
		int memory = 0;
		try {
			memory = (int) vm.getMemoryStaticMax(connection).longValue();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return memory;
	}

	public long getStorage(VM vm) {
		long storage = 0;

		Set<VBD> vbds = null;
		try {
			vbds = vm.getVBDs(connection);
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		if (vbds != null && vbds.size() > 0)
			for (VBD vbd : vbds)
				try {
					storage += vbd.getVDI(connection).getVirtualSize(connection);
				} catch (Exception e) {
					e.printStackTrace();
				}
		return storage;
	}
	
	public int rebootVM(String uuid) {
		try {
			VM vm=getVM(uuid);
			vm.cleanReboot(connection);
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
	
	public int shutDownVM(String uuid) {
		try {
			VM vm=getVM(uuid);
			vm.cleanShutdown(connection);
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
	public int pauseVM(String uuid) {
		try {
			VM vm=getVM(uuid);
			vm.pause(connection);
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
	public int startVM(String uuid) {
		try {
			VM vm=getVM(uuid);
			vm.start(connection, false, true);
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
	
	public int deleteVM(String uuid) {
		try {
			VM vm=getVM(uuid);
			vm.destroy(connection);
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
	
	

}
