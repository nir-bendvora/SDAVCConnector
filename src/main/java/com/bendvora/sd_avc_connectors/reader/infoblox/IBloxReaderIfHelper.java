/**
 * File: IBloxReaderIfHelper.java
 * Description: Reader interface helper for SD-AVC infoblox adaptor
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors.reader.infoblox;

import com.bendvora.sd_avc_connectors.config.AdaptorConf;

public interface IBloxReaderIfHelper {
	String read_iblox_record(AdaptorConf conf, String type);
}
