/*
 * HPPCOIN License
 * 
 * Copyright (c) 2017-2018, HPPCOIN Developers.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 
 * Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.hppcoin.test;



import java.util.Date;
import java.util.logging.Logger;

import org.hppcoin.dao.impl.VPSDaoImpl;
import org.hppcoin.model.OS;
import org.hppcoin.model.VPS;
import org.hppcoin.model.VPSAccesType;
import org.junit.Assert;

public class VPSDaoImplTest {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
//	@Test
	public void save() {
		LOGGER.info("save VPS");
		VPS vps=new VPS();
		vps.setUuid("hjdgfgfhekzfhfg");
		int days =60;
		Date endDate=new Date(System.currentTimeMillis()+24*60*60*1000*days);
		vps.setEndDate(endDate.getTime());
		vps.setIp("89.98.105.66");
		vps.setMine((byte)1);
		vps.setOs(OS.LINUX);
		vps.setPassword("pass");
		vps.setUser("root");
		vps.setType(VPSAccesType.PASSWORD);
		vps=	new VPSDaoImpl().save(vps);
		Assert.assertNotEquals(0, vps.getUuid());
	}

}
