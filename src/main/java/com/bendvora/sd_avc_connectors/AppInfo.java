/**
 * File: AppInfo.java
 * Description: Class for storing the application info
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class AppInfo extends GenericInfo {
	
	Map<String, GenericInfo> mDomains = null;
	public enum sigType {
		DOMAIN, 
		IP4ADDR, 
		IP6ADDR
	}
	
	// The constructor must have all mandatory fields
	public AppInfo(String app_name, String app_attr_business_relevance, 
			String app_attr_traffic_class, String app_attr_category, 
			String app_attr_sub_category, String app_attr_app_set) {
		set_val("app_name",app_name);
		set_val("app_attr_business_relevance",app_attr_business_relevance);
		set_val("app_attr_traffic_class",app_attr_traffic_class);
		set_val("app_attr_category",app_attr_category);
		set_val("app_attr_sub_category",app_attr_sub_category);
		set_val("app_attr_app_set",app_attr_app_set);
	}
	public AppInfo(String app_name) {
		this(app_name, "UNDEFINED", "UNDEFINED", "UNDEFINED", "UNDEFINED", "UNDEFINED");
	}
	
	public RetCodes addSignature(sigType type, String sig, GenericInfo extraInfo) {
		switch (type) {
		case DOMAIN:
			if (mDomains == null) {
				mDomains = new TreeMap<String, GenericInfo>();
			}
			mDomains.put(sig, extraInfo);
			break;
		case IP4ADDR:
		case IP6ADDR:
			// At this point this signature type is not supported
			break;
		}
		
		return RetCodes.SUCCESS;
	}
	
	public Map<String, GenericInfo> getDomains() {
		return mDomains;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nApp Info ");
		sb.append(super.toString());
		sb.append("\nDomains: [");
		for (Entry<String, GenericInfo> entry: mDomains.entrySet()) {
			sb.append("{");
			sb.append("domain:" + entry.getKey());
			sb.append(", extra_info:{");
			sb.append(entry.getValue().toString());
			sb.append("}},");
		}
		if (sb.length() > 1) sb.setLength(sb.length() - 1);
		sb.append("]");
        return sb.toString();
	}
	
	// Override the equals method
	public boolean equals(Object obj) {
	    if (!(obj instanceof AppInfo)) return false;
        AppInfo another = (AppInfo) obj;
        if (!super.equals(another)) return false;
        if (this.mDomains.size() != another.mDomains.size()) return false;
        for (Entry<String, GenericInfo> entry : mDomains.entrySet()) {
            if (!another.mDomains.get(entry.getKey()).equals(entry.getValue())) return false;
        }
        return true;
	}
}
