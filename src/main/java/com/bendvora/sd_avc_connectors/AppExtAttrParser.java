/**
 * File: AppExtAttrParser.java
 * Description: Helper to parse Extensible Attributes info from infoblox
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AppExtAttrParser extends AppInfoParser {
	
	public final static Logger logger = Logger.getLogger(AppExtAttrParser.class);

	protected String extract_string(String key) {
		
		logger.debug("Extracting info for key " + key);
		
		JSONParser parser = new JSONParser();
		String val = "UNDEFINED";
		
		try {
			Object obj = parser.parse(text);
			JSONObject attr = (JSONObject) obj;
			JSONObject attrv = (JSONObject) attr.get(key);
			val = (String) attrv.get("value");
		} catch (Exception e) {
			// Key that doesn't exist will return UNDEFINED
			return "UNDEFINED";
		}		
		return val;
	}
}
