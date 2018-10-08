/**
 * File: AppTxtRecordParser.java
 * Description: Helper to parse TXT record
 *  Example of TXT record:
 *  CISCO-CLS=app-name:NirTestApp|app-class:TD|business:YES|server-port:TCP/80|app-id:CU/10
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors;

import org.apache.log4j.Logger;

public class AppTxtRecordParser extends AppInfoParser {
	
	public final static Logger logger = Logger.getLogger(AppTxtRecordParser.class);

	// Override the default to check length and initials
	public RetCodes set_txt(String text) {
		// TXT record is limited to 256 chars, put a higher limit here
		if (text.length() > 1024) {
			logger.error("TXT record parser. Record too long");
			return RetCodes.ERROR;
		}
		if (!text.startsWith("CISCO-CLS=")) {
			logger.error("TXT record parser. Invalid record initials");
			return RetCodes.ERROR;
		}
		this.text = text;
		return RetCodes.SUCCESS;
	}
	
	protected String extract_string(String key) {
		logger.debug("Extracting TXT record info for key " + key);
		String ekey = key.toLowerCase() + ":";
		int idx = text.toLowerCase().indexOf(ekey);
		if (idx != -1) {
			idx += ekey.length();
			int idx2 = text.indexOf('|', idx);
			if (idx2 != -1) {
				return text.substring(idx, idx2);
			} else {
				// last variable, get from idx to end of string
				return text.substring(idx);
			}
		} else {
			return "UNDEFINED";
		}
	}
}
