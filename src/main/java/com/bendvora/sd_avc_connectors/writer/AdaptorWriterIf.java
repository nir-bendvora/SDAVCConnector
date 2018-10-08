/**
 * File: AdaptorWriterIf.java
 * Description: Writer interface for SD-AVC adaptor
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors.writer;

import java.util.List;

import com.bendvora.sd_avc_connectors.AppInfo;
import com.bendvora.sd_avc_connectors.RetCodes;
import com.bendvora.sd_avc_connectors.config.AdaptorConf;

public interface AdaptorWriterIf {
	public RetCodes init(AdaptorConf ibc);
	public RetCodes writeAdaptorInfo(List<AppInfo> rules, AdaptorConf ibc);
}
