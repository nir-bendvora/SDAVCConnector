/**
 * File: AppInfoParser.java
 * Description: Generic Helper to parse app info records
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors;

import org.apache.log4j.Logger;

public abstract class AppInfoParser {
	public final static Logger logger = Logger.getLogger(AppInfoParser.class);
	protected String text;

	public RetCodes set_txt(String text) {
		this.text = text;
		return RetCodes.SUCCESS;
	}
	
    // Need to override this for a given app info type
	protected abstract String extract_string(String key);
	
	public String get_app_name() {
		// TBD: Is there any validity for app-name? Size?
		return extract_string("app-name");  // App name is kept case sensitive
	}
	
	public String get_attr_tc() {
		String res = extract_string("app-class").toUpperCase();
		// if abbreviations are used, modify to full name
		switch (res) {
		case "VO":  res = "VOIP-TELEPHONY"; break;
		case "BV":  res = "BROADCAST-VIDEO"; break;
		case "RTI": res = "REALTIME-INTERACTIVE"; break;
		case "MMC": res = "MULTIMEDIA-CONFERENCING"; break;
		case "MMS": res = "MULTIMEDIA-STREAMING"; break;
		case "NC":  res = "NETWORK-CONTROL"; break;
		case "CS":  res = "SIGNALING"; break;
		case "OAM": res = "OPS-ADMIN-MGMT"; break;
		case "TD":  res = "TRANSACTIONAL-DATA"; break;
		case "BD":  res = "BULK-DATA"; break;
		case "BE":  res = "BEST-EFFORT"; break;
		case "SCV": res = "SCAVENGER"; break;
		}
		
		// Check validity
		switch (res) {
		case "VOIP-TELEPHONY":
		case "BROADCAST-VIDEO":
		case "REALTIME-INTERACTIVE":
		case "MULTIMEDIA-CONFERENCING":
		case "MULTIMEDIA-STREAMING":
		case "NETWORK-CONTROL":
		case "SIGNALING":
		case "OPS-ADMIN-MGMT":
		case "TRANSACTIONAL-DATA":
		case "BULK-DATA":
		case "BEST-EFFORT":
		case "SCAVENGER":
		case "UNDEFINED":
			return res;
		default:
			logger.error("App info parser. Illegal traffic-class value");
			return "UNDEFINED";
		}
	}
	
	public String get_attr_br() {
		String res = extract_string("business").toUpperCase();
		// Check validity
		switch (res) {
		case "YES":
		case "NO":
		case "DEFAULT":
		case "UNDEFINED":
			return res;
		default:
			logger.error("App info parser. Illegal business-relevance value");
			return "UNDEFINED";
		}
	}
	
	public String get_app_id() {
		String res = extract_string("app-id").toUpperCase();
		// Check validity
		if (res.matches("CU/[0-9]+") || res.equals("UNDEFINED")) {
			return res;
		} else {
			logger.error("App info parser. Illegal app-id value");
			return "UNDEFINED";
		}
	}
	
	public String get_attr_cat() {
		String res = extract_string("app-category").toUpperCase();
		// Check validity
		switch (res) {
		case "ANONYMIZERS":
		case "BACKUP-AND-STORAGE":
		case "BROWSING":
		case "BUSINESS-AND-PRODUCTIVITY-TOOLS":
		case "CONSUMER-FILE-SHARING":
		case "CONSUMER-INTERNET":
		case "CONSUMER-MESSAGING":
		case "CONSUMER-STREAMING":
		case "CUSTOM-CATEGORY":
		case "CUSTOM1-CATEGORY":
		case "CUSTOM2-CATEGORY":
		case "DATABASE":
		case "EMAIL":
		case "EPAYEMENT":
		case "FILE-SHARING":
		case "GAMING":
		case "INDUSTRIAL-PROTOCOLS":
		case "INSTANT-MESSAGING":
		case "INTER-PROCESS-RPC":
		case "INTERNET-SECURITY":
		case "LAYER3-OVER-IP":
		case "LOCATION-BASED-SERVICES":
		case "NET-ADMIN":
		case "NEWSGROUP":
		case "OTHER":
		case "SOCIAL-NETWORKING":
		case "SOFTWARE-UPDATES":
		case "TROJAN":
		case "VOICE-AND-VIDEO":
		case "UNDEFINED":
			return res;
		default:
			logger.error("App info parser. Illegal category value");
			return "UNDEFINED";
		}
	}
	
	public String get_attr_sub_cat() {
		String res = extract_string("app-sub-category").toUpperCase();
		// Check validity
		switch (res) {
		case "AUTHENTICATION-SERVICES":
		case "BACKUP-SYSTEMS":
		case "CONSUMER-AUDIO-STREAMING":
		case "CONSUMER-CLOUD-STORAGE":
		case "CONSUMER-MULTIMEDIA-MESSAGING":
		case "CONSUMER-VIDEO-STREAMING":
		case "CONSUMER-WEB-BROWSING":
		case "CONTROL-AND-SIGNALING":
		case "CUSTOM-SUB-CATEGORY":
		case "CUSTOM1-SUB-CATEGORY":
		case "CUSTOM2-SUB-CATEGORY":
		case "DESKTOP-VIRTUALIZATION":
		case "ENTERPRISE-CLOUD-DATA-STORAGE":
		case "ENTERPRISE-CLOUD-SERVICES":
		case "ENTERPRISE-DATA-CENTER-STORAGE":
		case "ENTERPRISE-MEDIA-CONFERENCING":
		case "ENTERPRISE-REALTIME-APPS":
		case "ENTERPRISE-RICH-MEDIA-CONTENT":
		case "ENTERPRISE-SW-DEPLOYMENT-TOOLS":
		case "ENTERPRISE-TRANSACTIONAL-APPS":
		case "ENTERPRISE-VIDEO-BROADCAST":
		case "ENTERPRISE-VOICE-COLLABORATION":
		case "FILE-TRANSFER":
		case "NAMING-SERVICES":
		case "NETWORK-MANAGEMENT":
		case "OS-UPDATES":
		case "OTHER":
		case "P2P-FILE-TRANSFER":
		case "P2P-NETWORKING":
		case "REMOTE-ACCESS-TERMINAL":
		case "ROUTING-PROTOCOL":
		case "TUNNELING-PROTOCOLS":
		case "UNDEFINED":
			return res;
		default:
			logger.error("App info parser. Illegal business-relevance value");
			return "UNDEFINED";
		}
	}

	// Not supported by the device DNS-AS but can be supported by SD-AVC
	public String get_attr_app_set() {
		String res = extract_string("app-set").toUpperCase();
		// Check validity
		switch (res) {
		case "AUTHENTICATION-SERVICES":
		case "BACKUP-AND-STORAGE":
		case "COLLABORATION-APPS":
		case "CONSUMER-BROWSING":
		case "CONSUMER-FILE-SHARING":
		case "CONSUMER-GAMING":
		case "CONSUMER-MEDIA":
		case "CONSUMER-MISC":
		case "CONSUMER-SOCIAL-NETWORKING":
		case "CUSTOM-SET":
		case "CUSTOM-SET1":
		case "CUSTOM-SET10":
		case "CUSTOM-SET11":
		case "CUSTOM-SET12":
		case "CUSTOM-SET13":
		case "CUSTOM-SET14":
		case "CUSTOM-SET15":
		case "CUSTOM-SET16":
		case "CUSTOM-SET17":
		case "CUSTOM-SET18":
		case "CUSTOM-SET19":
		case "CUSTOM-SET2":
		case "CUSTOM-SET3":
		case "CUSTOM-SET4":
		case "CUSTOM-SET5":
		case "CUSTOM-SET6":
		case "CUSTOM-SET7":
		case "CUSTOM-SET8":
		case "CUSTOM-SET9":
		case "DATABASE-APPS":
		case "DESTOP-VIRTUALIZATION":
		case "EMAIL":
		case "ENTERPRISE-IPC":
		case "FILE-SHARING":
		case "GENERAL-BROWSING":
		case "GENERAL-MEDIA":
		case "GENERAL-MISC":
		case "LOCAL-SERVICES":
		case "NAMING-SERVICES":
		case "NETWORK-CONTROL":
		case "NETWORK-MANAGEMENT":
		case "REMOTE-ACCESS":
		case "SAAS-APPS":
		case "SIGNALING":
		case "SOFTWARE-DEVELOPMENT-TOOLS":
		case "SOFTWARE-UPDATES":
		case "STREAMING-MEDIA":
		case "TUNNELING":
		case "UNDEFINED":
			return res;
		default:
			logger.error("App info parser. Illegal application-set value");
			return "UNDEFINED";
		}
	}
}
