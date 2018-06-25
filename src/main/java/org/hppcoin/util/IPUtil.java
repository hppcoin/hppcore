package org.hppcoin.util;

import java.util.logging.Logger;

import org.hppcoin.model.LMNode;

public class IPUtil {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static LMNode setNode(String ipData, LMNode node) {
		if (ipData != null)
			try {
				String[] parts = ipData.split(",");
				if (parts != null)
					for (String str : parts) {
						if (str != null && str.contains("country_name")) {
							String name = str.split(":")[1];
							if (name != null)
								name = name.substring(1, name.length() - 1);
							node.setCountry(name);
						}

						if (str != null && str.contains("region_name")) {
							String name = str.split(":")[1];
							if (name != null)
								name = name.substring(1, name.length() - 1);
							node.setRegion(name);
						}
						if (str != null && str.contains("country_code")) {
							String name = str.split(":")[1];
							if (name != null)
								name = name.substring(1, name.length() - 1);
							node.setCountryIso(name);
						}
						if (str != null && str.contains("city")) {
							String name = str.split(":")[1];
							if (name != null)
								name = name.substring(1, name.length() - 1);
							node.setCity(name);
						}
						if (str != null && str.contains("latitude")) {
							String latitudeStr = str.split(":")[1];
							double latitude = 0;
							try {
								latitude = Double.parseDouble(latitudeStr);
							} catch (Exception e) {
								LOGGER.severe(e.getMessage());
							}
							node.setLatitude(latitude);
						}
						if (str != null && str.contains("longitude")) {
							String latitudeStr = str.split(":")[1];
							double longitude = 0;
							try {
								longitude = Double.parseDouble(latitudeStr);
							} catch (Exception e) {
								LOGGER.severe(e.getMessage());
							}
							node.setLongitude(longitude);
						}
					}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
			}

		return node;
	}

}
