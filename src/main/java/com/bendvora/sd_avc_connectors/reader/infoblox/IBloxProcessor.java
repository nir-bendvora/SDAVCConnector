/**
 * File: IBloxProcessor.java
 * Description: Help to process Infoblox data
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors.reader.infoblox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.bendvora.sd_avc_connectors.AppExtAttrParser;
import com.bendvora.sd_avc_connectors.AppInfo;
import com.bendvora.sd_avc_connectors.AppTxtRecordParser;
import com.bendvora.sd_avc_connectors.GenericInfo;
import com.bendvora.sd_avc_connectors.AppInfo.sigType;
import com.bendvora.sd_avc_connectors.config.AdaptorConf;

public class IBloxProcessor {
	
	public final static Logger logger = Logger.getLogger(IBloxProcessor.class);
	private IBloxReaderIfHelper rd_helper = null;
	private boolean readIPRules = false;
	
	public IBloxProcessor(IBloxReaderIfHelper rd_helper, boolean readIPRules) {
		this.rd_helper = rd_helper;
		this.readIPRules = readIPRules;
		
	}

	// return a map that contains list of app info
	private List<AppInfo> infoblox_process_txt_record(String output) {
		JSONParser parser = new JSONParser();
		HashMap<String, AppInfo> app_info = new HashMap<String, AppInfo>();
		AppTxtRecordParser rp = new AppTxtRecordParser();
		AppInfo info = null;

		try {
			Object obj = parser.parse(output);
			JSONArray records = (JSONArray) obj;
			for (int i = 0; i < records.size(); i++) {
				JSONObject entity = (JSONObject) records.get(i);
				String domain = (String) entity.get("name");
				String text   =  (String) entity.get("text");
				rp.set_txt(text);

				info = app_info.get(rp.get_app_name());
				if (info == null) {
					info = new AppInfo(rp.get_app_name(), 
							rp.get_attr_br(), rp.get_attr_tc(), rp.get_attr_cat(), 
							rp.get_attr_sub_cat(), rp.get_attr_app_set());
					app_info.put(rp.get_app_name(), info);
				}
				info.addSignature(sigType.DOMAIN, domain, new GenericInfo());
			}
		} catch (Exception e) {
			logger.info("Failed to parse Infoblox txt records - may be empty");
		}		
		return new ArrayList<AppInfo>(app_info.values());
	}
	
	// return a map that contains the list of app info
	private List<AppInfo> infoblox_process_extattrs(String output) {
		JSONParser parser = new JSONParser();
		HashMap<String, AppInfo> app_info = new HashMap<String, AppInfo>();
		AppExtAttrParser rp = new AppExtAttrParser();
		AppInfo info = null;

		try {
			JSONArray records = (JSONArray) parser.parse(output);

			for (int i = 0; i < records.size(); i++) {
				JSONObject entity =  (JSONObject) records.get(i);
				String domain = (String) entity.get("name");
				// Get info from extensible attributes
				rp.set_txt(entity.get("extattrs").toString());
				
				info = app_info.get(rp.get_app_name());
				if (info == null) {
					info = new AppInfo(rp.get_app_name(), 
							rp.get_attr_br(), rp.get_attr_tc(), rp.get_attr_cat(), 
							rp.get_attr_sub_cat(), rp.get_attr_app_set());
					app_info.put(rp.get_app_name(), info);
				}
				info.addSignature(sigType.DOMAIN, domain, new GenericInfo());
			}
		} catch (Exception e) {
			logger.info("Failed to parse Infoblox extensible attributes - may be empty");
		}		
		return new ArrayList<AppInfo>(app_info.values());
	}
	
	private HashMap<String, AppInfo> mapDomainsToInfo(List<AppInfo> app_info) {
		HashMap<String, AppInfo> map_info = new HashMap<String, AppInfo>();
		for (AppInfo info: app_info) {
			for (String domain: info.getDomains().keySet()) {
				map_info.put(domain, info);
			}
		}
		return map_info;
	}

	// Method returns app rules for A records
	// Input are a record JSON and a hash_map containing
	private List<AppInfo> infoblox_process_a_record(String output, 
			List<AppInfo> app_info, String vrf_name, sigType type) {

		JSONParser parser = new JSONParser();
		AppInfo info = null;
		HashMap<String, AppInfo> map_info = mapDomainsToInfo(app_info);
		
		if (app_info == null) return null;
		
		try {
			JSONArray records = (JSONArray) parser.parse(output);
			 
			for (int i = 0; i < records.size(); i++) {
				JSONObject entity = (JSONObject) records.get(i);
				String domain =  (String) entity.get("name");
				//String ip_addr   = (String) entity.get("ipv4addr");

				// correlate with txt_map based on domain
				info = map_info.get(domain);
				if (info != null) {
					// info.addSignature(type, ip_addr, vrf_name, new GenericInfo());  // Not supported now
				}
			}
		} catch (Exception e) {
			logger.info("Failed to parse Infoblox A/AAAA records - may be empty.");
		}		
		return app_info;
	}
	
	public List<AppInfo> read_iblox_record(AdaptorConf ibc) {

		String output = "[]";
		String output6 = "[]";
		List<AppInfo> app_info = null; 
		List<AppInfo> app_info6 = null; 
		boolean use_extattrs = false;

		if (ibc.get_val("application_info").toUpperCase().equals("TXT")) {
			output = rd_helper.read_iblox_record(ibc, "txt"); // At this point list of zones could be handled by multiple entities
			logger.debug("TXT record query result\n");
			logger.debug(output);
			app_info = infoblox_process_txt_record(output);
			if (app_info == null) {
				logger.error("Processing TXT record returned with null. Data read:\n"+ output);
				return null;
			}
		} else {
			use_extattrs = true;
		}

		if (use_extattrs || readIPRules) {
			output = rd_helper.read_iblox_record(ibc, "a"); 
			output6 = rd_helper.read_iblox_record(ibc, "aaaa"); 
			
			logger.debug("A record query result\n");
			logger.debug(output);
			logger.debug("AAAA record query result\n");
			logger.debug(output6);
		}

		if (use_extattrs) {
			app_info = infoblox_process_extattrs(output);
			app_info6 = infoblox_process_extattrs(output6);
			
			if (app_info == null ) {
				logger.error("Processing A records extensible attributes returned with null. Data read:\n"+ output);
				return null;
			}
			if (app_info6 == null ) {
				logger.error("Processing AAAA records extensible attributes returned with null. Data read:\n"+ output6);
				return null;
			}
			app_info.addAll(app_info6);
		}
		
		if (readIPRules) {
			app_info = infoblox_process_a_record(output, app_info, ibc.get_val("vrf_name"), sigType.IP4ADDR);
			if (app_info == null) {
				logger.error("Processing A record returned with null. Data read:\n"+ output);
				return null;
			}
			app_info = infoblox_process_a_record(output6, app_info, ibc.get_val("vrf_name"), sigType.IP6ADDR);
			if (app_info == null) {
				logger.error("Processing AAAA record returned with null. Data read:\n"+ output);
				return null;
			}
		}
		
		return app_info;
	}
}
