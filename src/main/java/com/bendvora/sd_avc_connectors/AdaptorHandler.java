/**
 * File: AdaptorHandler.java
 * Description: Adaptor handler
 * SD-AVC Adaptor handler
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.bendvora.sd_avc_connectors.config.AdaptorConf;
import com.bendvora.sd_avc_connectors.config.AdaptorConfIf;
import com.bendvora.sd_avc_connectors.reader.AdaptorReaderIf;
import com.bendvora.sd_avc_connectors.runner.AdaptorRunner;
import com.bendvora.sd_avc_connectors.writer.AdaptorWriterIf;

public class AdaptorHandler {
	
	public final static Logger logger = Logger.getLogger(AdaptorHandler.class);
	private HashMap<String, AdaptorRunner> runners = null;
	
	public AdaptorHandler() {
		this.runners = new HashMap<String, AdaptorRunner>();
	}
	
	private List<AdaptorConf> getConfig(String adaptorName, AdaptorConfIf cf) {
		List<AdaptorConf> conf = null;

		if (cf != null) {
			if (cf.init() != RetCodes.SUCCESS) return null;
			logger.info("Adaptor " + adaptorName + " config_file.init - OK");
			
			try {
				conf = cf.get_config();
			} catch (Exception e) {
				logger.error("Adaptor " + adaptorName + " Getting config result with an error", e);
	    		return null;
	        }
		} else {
			conf = new ArrayList<AdaptorConf>();  // Init with empty config file
			AdaptorConf cur_conf = new AdaptorConf();
			cur_conf.set_val("poll_time", "10");
			cur_conf.set_val("name", "default");
			cur_conf.set_val("segment", "global");
			conf.add(cur_conf);
		}
		
		logger.info("Adaptor " + adaptorName + " getting config - OK");
		int entity_num = 0;
		// Update name and log all config entities
		for (AdaptorConf entity_conf: conf) {
			entity_conf.set_val("adaptor_name", adaptorName);
			logger.debug("Entity :" + entity_num);
			logger.debug(entity_conf);
			entity_num ++;
		}
		
		return conf;
	}
	
	public RetCodes addAdaptor(String adaptorName, AdaptorConfIf cf, AdaptorReaderIf rf, AdaptorWriterIf wf) {
		
		List<AdaptorConf> conf = getConfig(adaptorName, cf);
	    if (conf == null) return RetCodes.ERROR;
	    
	    AdaptorRunner runner = new AdaptorRunner(adaptorName, conf, rf, wf);
	    if (runner.init() != RetCodes.SUCCESS) return RetCodes.ERROR;
		logger.info("Adaptor " + adaptorName + " runner.init - OK");

		runners.put(adaptorName, runner);
		return RetCodes.SUCCESS;
	}

	public RetCodes init() {
		return RetCodes.SUCCESS;
	}
	
	// Exec all runners once
	public RetCodes exec_once() {
		// The following can be used to test one time run
		for (Entry<String, AdaptorRunner> runner: runners.entrySet()) {
			if (runner.getValue().read_write_data() != RetCodes.SUCCESS) return RetCodes.ERROR;
			logger.info("runner.read_write_data - OK");
		}
		return RetCodes.SUCCESS;
	}
	
	private AdaptorRunner getRunnerByAdaptorName(String adaptorName) {
		for (Entry<String, AdaptorRunner> runner: runners.entrySet()) {
			if (runner.getKey().equals(adaptorName)) {
				return runner.getValue();
			}
		}
		return null;
	}
	
	public RetCodes startAdaptor(String adaptorName) {
		AdaptorRunner runner = null;
		runner = getRunnerByAdaptorName(adaptorName);
		if (runner != null) {
			runner.start();
		} else {
		    logger.error("Adaptor " + adaptorName + " start runner failed");
		    return RetCodes.ERROR;
		}
		return RetCodes.SUCCESS;
	}
	
	// Start all adaptors
	public RetCodes start() {
		AdaptorRunner curRunner;
		RetCodes res = RetCodes.SUCCESS;
		
		for (Entry<String, AdaptorRunner> runner: runners.entrySet()) {
			curRunner = runner.getValue();
			if (curRunner != null) {
				curRunner.start();
			} else {
			    logger.error("Adaptor " + runner.getKey() + " start runner failed");
			    res = RetCodes.ERROR;
			}
		}
		return res;
	}
	
	public RetCodes stopAdaptor(String adaptorName) {
		AdaptorRunner runner = null;
		runner = getRunnerByAdaptorName(adaptorName);
		if (runner != null) {
			runner.stop();
		} else {
		    logger.error("Adaptor " + adaptorName + " stop runner failed");
		    return RetCodes.ERROR;
		}
		return RetCodes.SUCCESS;
	}
	
	// Stop all adaptors
	public RetCodes stop() {
		AdaptorRunner curRunner;
		RetCodes res = RetCodes.SUCCESS;
		
		for (Entry<String, AdaptorRunner> runner: runners.entrySet()) {
			curRunner = runner.getValue();
			if (curRunner != null) {
				curRunner.stop();
			} else {
			    logger.error("Adaptor " + runner.getKey() + " stop runner failed");
			    res = RetCodes.ERROR;
			}
		}
		return res;
	}
	
	public RetCodes updateConfig(String adaptorName, AdaptorConfIf cf) {
		AdaptorRunner runner = null;
		
		List<AdaptorConf> conf = getConfig(adaptorName, cf);
	    if (conf == null) return RetCodes.ERROR;
		
		runner = getRunnerByAdaptorName(adaptorName);
		if (runner != null) {
			runner.updateConfig(conf);
		} else {
		    logger.error("Adaptor " + adaptorName + " update config failed");
		    return RetCodes.ERROR;
		}
		return RetCodes.SUCCESS;
	}
}
