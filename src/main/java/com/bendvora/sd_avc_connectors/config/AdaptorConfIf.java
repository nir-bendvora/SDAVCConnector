/**
 * File: AdaptorConfIf.java
 * Description: Interface for reading configuration
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors.config;

import java.util.List;

import com.bendvora.sd_avc_connectors.RetCodes;

public interface AdaptorConfIf {
	public RetCodes init();
	public List<AdaptorConf> get_config() throws IllegalArgumentException;
}