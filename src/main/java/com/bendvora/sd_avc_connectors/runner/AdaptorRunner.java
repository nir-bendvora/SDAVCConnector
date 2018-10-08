/**
 * File: AdaptorRunner.java
 * Description: Runner task for polling 
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors.runner;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.bendvora.sd_avc_connectors.AppInfo;
import com.bendvora.sd_avc_connectors.RetCodes;
import com.bendvora.sd_avc_connectors.config.AdaptorConf;
import com.bendvora.sd_avc_connectors.reader.AdaptorReaderIf;
import com.bendvora.sd_avc_connectors.writer.AdaptorWriterIf;

public class AdaptorRunner extends TimerTask {
	
	public final static Logger logger = Logger.getLogger(AdaptorRunner.class);
	private AdaptorReaderIf rd_if = null;
	private AdaptorWriterIf wr_if = null;
	private List<AdaptorConf> ib_conf = null;
	private Timer timer;
	private boolean isRunning = false;
	private String adaptor_name;

	public AdaptorRunner(String adaptor_name, List<AdaptorConf> ib_conf, AdaptorReaderIf rd_if, AdaptorWriterIf wr_if) {
		this.ib_conf = ib_conf;
		this.rd_if = rd_if;
		this.wr_if = wr_if;
		this.adaptor_name = adaptor_name;
	}
	
	public void updateConfig(List<AdaptorConf> conf) {
		this.ib_conf = conf;
	}

	public RetCodes init() {
		if (wr_if == null) return RetCodes.ERROR;
		for (AdaptorConf ibc: ib_conf) {
			if (wr_if.init(ibc) != RetCodes.SUCCESS) {
				logger.debug("Runner Init failed");
				return RetCodes.ERROR;
			}
		}
		
		logger.debug("Runner Init completed");
		return RetCodes.SUCCESS;
	}

	public RetCodes start() {
		if (isRunning) {
			logger.error("Trying to start " + this.adaptor_name + " runner while already running");
			return RetCodes.ERROR;   
		}

		timer = new Timer(true);
		if (ib_conf != null && ib_conf.get(0) != null) {
			String poll_time = ib_conf.get(0).get_val("poll_time");
			Integer poll_time_int = Integer.parseInt(poll_time);
			timer.schedule(this, 0, poll_time_int*1000);
			logger.info("Start " + this.adaptor_name + " runner to " + poll_time_int + "Sec interval");
		} else {
			logger.error("Trying to start " + this.adaptor_name + " runner but no poll time is found");
			return RetCodes.ERROR;        		
		}

		isRunning = true;
		return RetCodes.SUCCESS;

	}

	public RetCodes stop() {

		if (isRunning) {
			if (timer!=null) {
				try {
					timer.cancel();
				} catch (Exception e) {
					logger.error("Cancel time error while stopping " + this.adaptor_name + " runner");
					return RetCodes.ERROR;        		
				}
			} else {
				logger.error("Trying to stop " + this.adaptor_name + " runner - timer is null");
				return RetCodes.ERROR;        		
			}
		} else {
			logger.error("Trying to stop " + this.adaptor_name + " runner while already stopped");
			return RetCodes.ERROR;        		
		}

		isRunning = false;
		return RetCodes.SUCCESS;
	}
	
	public RetCodes write_data(List<AppInfo> rules, AdaptorConf ibc) {
		// write app info table
		wr_if.writeAdaptorInfo(rules, ibc);

		return RetCodes.SUCCESS;
	}

	public RetCodes read_write_data() {
		List<AppInfo> rt;
		RetCodes rc = RetCodes.SUCCESS;  // don't return on error to complete the cycle
		
		for (AdaptorConf ibc: ib_conf) {
			logger.info("-> Start reading data from " + this.adaptor_name);
			rt = rd_if.read_adaptor_record(ibc);
			logger.info("-> Reading data from " + this.adaptor_name + " completed");
			if (rt==null) {
				logger.error("Read and process data returned with null");
				rc = RetCodes.ERROR;
			} else {
				logger.info("-> Start writing data");				
				if (write_data(rt, ibc) != RetCodes.SUCCESS) {
					logger.error("Writer reported failure");
					rc = RetCodes.ERROR;
					logger.info("-> Error writing data");
				} 
			}
		}
		return rc;
	}

	public void run() {
		logger.debug("Run polling cycle started at time=" + new Date().toString());
		read_write_data();
	}
}
