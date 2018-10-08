/**
 * File: AdaptorWriterConsole.java
 * Description: Writer implementation for console
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors.writer.console;

import java.util.List;

import org.apache.log4j.Logger;

import com.bendvora.sd_avc_connectors.AppInfo;
import com.bendvora.sd_avc_connectors.RetCodes;
import com.bendvora.sd_avc_connectors.config.AdaptorConf;
import com.bendvora.sd_avc_connectors.writer.AdaptorWriterIf;

public class AdaptorWriterConsole implements AdaptorWriterIf {
	
	public final static Logger logger = Logger.getLogger(AdaptorWriterConsole.class);
	private String app_info;
	
	public RetCodes init(AdaptorConf ibc) {
		logger.info("Init Console Writer Interface");
		return RetCodes.SUCCESS;
	}

	public String get_app_info() {
		return app_info;
	}

	public RetCodes writeAdaptorInfo(List<AppInfo> rules, AdaptorConf ibc) {
		logger.info("Console Writer - write app info for adaptor " + ibc.get_val("adaptor_name") + " entity " + ibc.get_val("name"));
		logger.info(rules);
		this.app_info = rules.toString();
		return RetCodes.SUCCESS;
	}
}
