/**
 * File: AdaptorConfFile.java
 * Description: File configuration for SD-AVC adaptor
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors.config.file;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.bendvora.sd_avc_connectors.RetCodes;
import com.bendvora.sd_avc_connectors.config.AdaptorConf;
import com.bendvora.sd_avc_connectors.config.AdaptorConfIf;

public class AdaptorConfFile implements AdaptorConfIf {
	
	public final static Logger logger = Logger.getLogger(AdaptorConfFile.class);
	private List<AdaptorConf> ib_conf = null;
	private JSONParser parser = null;
	private String filename; 
	


	public AdaptorConfFile(String filename) {
		this.filename = filename;
	}

	public RetCodes init() {
		this.ib_conf = new ArrayList<AdaptorConf>();
		this.parser = new JSONParser();
		logger.info("Init file configuration. File="+ this.filename);
		
		return RetCodes.SUCCESS;
	}

	public List<AdaptorConf> get_config() throws IllegalArgumentException {
		logger.info("Get file configuration...");
		if (ib_conf == null) return null;
		if (this.filename == null) return null;
		Set<String> entity_names = new HashSet<String>();
		String cur_entity_name = "";
		boolean is_error = false;
		JSONObject jsonObject = null;
		
        try {
        	jsonObject = (JSONObject) parser.parse(new FileReader(filename));
            String poll_time = (String) jsonObject.get("poll_time");
            String sdavc_ipv4addr = (String) jsonObject.get("sdavc_ipv4addr");
            String sdavc_login = (String) jsonObject.get("sdavc_login");
            String sdavc_passwd = (String) jsonObject.get("sdavc_passwd");
            
            JSONArray entities = (JSONArray) jsonObject.get("entities");
            
            for (int i = 0; i < entities.size(); i++) {
            	AdaptorConf cur_conf = new AdaptorConf();
            	JSONObject entity = (JSONObject) entities.get(i);
            	            	
            	for (Object key : entity.keySet()) {
                    String ckey = (String)key;
                    String cvalue = (String) entity.get(ckey);
                    cur_conf.set_val(ckey, cvalue);
                    if (ckey.equals("name")) cur_entity_name = cvalue;
            	}
            	
            	// Add global configuration to every entity
                cur_conf.set_val("poll_time", poll_time);  // Add the poll time and sdavc info in every entity, mandatory
                if (sdavc_ipv4addr != null) cur_conf.set_val("sdavc_ipv4addr", sdavc_ipv4addr);  
                if (sdavc_login != null) cur_conf.set_val("sdavc_login", sdavc_login);  
                if (sdavc_passwd != null) cur_conf.set_val("sdavc_passwd", sdavc_passwd); 

                // Make sure the entry has a unique name, don't add and report error if not
                if (!entity_names.contains(cur_entity_name)) {
                	entity_names.add(cur_entity_name);
                    ib_conf.add(cur_conf);
            		logger.info("Adding config for entity " + cur_entity_name);
                } else {
            		logger.error("Duplicate entity " + cur_entity_name + " in config file");
            		is_error = true;
                }
            }
        } catch (Exception e) {
    		logger.error("Config JSON parsing error");
        	is_error = true;
        }
        
        if (is_error) {
    		throw new IllegalArgumentException("Config file parsing error. Input received:\n" + jsonObject.toString());
        }
        
		return ib_conf;
	}
}
