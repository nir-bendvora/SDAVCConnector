/**
 * File: AdaptorReaderIf.java
 * Description: Interface definition for reading SD-AVC adaptor info
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors.reader;

import java.util.List;

import com.bendvora.sd_avc_connectors.AppInfo;
import com.bendvora.sd_avc_connectors.config.AdaptorConf;

public interface AdaptorReaderIf {
	public List<AppInfo> read_adaptor_record(AdaptorConf conf);
}
