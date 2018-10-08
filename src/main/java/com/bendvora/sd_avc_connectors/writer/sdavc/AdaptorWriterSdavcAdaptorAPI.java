/**
 * File: AdaptorWriterConsole.java
 * Description: Writer implementation for console
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors.writer.sdavc;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.bendvora.sd_avc_connectors.AppInfo;
import com.bendvora.sd_avc_connectors.GenericInfo;
import com.bendvora.sd_avc_connectors.RetCodes;
import com.bendvora.sd_avc_connectors.config.AdaptorConf;
import com.bendvora.sd_avc_connectors.writer.AdaptorWriterIf;

public class AdaptorWriterSdavcAdaptorAPI implements AdaptorWriterIf {
	
	public final static Logger logger = Logger.getLogger(AdaptorWriterSdavcAdaptorAPI.class);
	private String token = "";
	
	private Client get_sdavc_client(AdaptorConf conf) {
		try {

			// Bypass certificate validation
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, new TrustManager[]{new X509TrustManager() 
			{
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException{}
				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException{}
				public X509Certificate[] getAcceptedIssuers()
				{
					return new X509Certificate[0];
				}
			}}, new java.security.SecureRandom());
			HostnameVerifier allowAll = new HostnameVerifier() 
			{
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			Client client = ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier(allowAll).build();
			return client;

		} catch (Exception e) {
			logger.error("Creating SD-AVC client failed");
			e.printStackTrace();
		}

		return null;
	}
	
	public RetCodes sdavc_login(AdaptorConf conf) {
		String output = "";
		String token_initials = "{\"token\":\"";
		String sdavc_target = "";
		
		logger.info("Logging in to SD-AVC...");
		Client client = get_sdavc_client(conf);
		if (client==null) {
			logger.error("Client is null when login to SD-AVC");
			return RetCodes.ERROR;
		}

		try {
			sdavc_target = String.format(
					"https://%s:%s/login",
					conf.get_val("sdavc_ipv4addr"), 
					"8443");   
			
		    MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
		    formData.add("username", conf.get_val("sdavc_login"));
		    formData.add("password", conf.get_val("sdavc_passwd"));
		    
			WebTarget webTarget = client.target(sdavc_target);
			Response response = webTarget
					.request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
					.post(Entity.form(formData));
			if (response.getStatus() != 200) {
				throw new RuntimeException("Error: SD-AVC login failed, error code="
						+ response.getStatus());
			}
			output = response.readEntity(String.class);
			if (output.length() > token_initials.length()) {
				this.token = output.substring(token_initials.length(), output.length()-2);
				logger.info("Token received:" + this.token);
			} else {
				return RetCodes.ERROR;
			}
			// Have token, return success			
			return RetCodes.SUCCESS;
		} catch (Exception e) {
			logger.error("Login to SD-AVC failed. Target=" + sdavc_target);
			return RetCodes.ERROR;
		}
	}
	
	public RetCodes init(AdaptorConf ibc) {
		logger.info("Init SD-AVC writer - OK");
		return RetCodes.SUCCESS;
	}

	public String appInfotoJson(List<AppInfo> rules, AdaptorConf ibc) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"sourceId\":\"");
		sb.append(ibc.get_val("name"));
		sb.append("\",\"rules\":[{\"segment\":\"");
		sb.append(ibc.get_val("segment"));
		sb.append("\",\"rules\":[");
		
		for (AppInfo rule: rules) {
			sb.append("{\"appName\":\"");
			sb.append(rule.get_val("app_name"));
			sb.append("\",\"serverNames\":[");
			for (Entry<String, GenericInfo> domain: rule.getDomains().entrySet()) {
				sb.append("\"");
				sb.append(domain.getKey());
				sb.append("\",");
			}
			// remove last ,
			sb.setLength(sb.length()-1);
			sb.append("]");
			
			StringBuilder at_sb = new StringBuilder();
			if (!rule.get_val("app_attr_category").equals("UNDEFINED")) at_sb.append("\"category\": \"" + rule.get_val("app_attr_category") + "\",");
			if (!rule.get_val("app_attr_sub_category").equals("UNDEFINED")) at_sb.append("\"sub-category\": \"" + rule.get_val("app_attr_sub_category") + "\",");
			if (!rule.get_val("app_attr_business_relevance").equals("UNDEFINED")) at_sb.append("\"business-relevance\": \"" + rule.get_val("app_attr_business_relevance") + "\",");
			if (!rule.get_val("app_attr_traffic_class").equals("UNDEFINED")) at_sb.append("\"traffic-class\": \"" + rule.get_val("app_attr_traffic_class") + "\",");
			if (!rule.get_val("app_attr_app_set").equals("UNDEFINED")) at_sb.append("\"application-set\": \"" + rule.get_val("app_attr_app_set") + "\",");
			if (at_sb.length() > 0) {
				at_sb.setLength(at_sb.length()-1);
				sb.append(",\"attributes\": {");
				sb.append(at_sb.toString());
				sb.append("}");
			}
			sb.append("},");
		}
		sb.setLength(sb.length()-1);
		sb.append("]}]}");
		
		return sb.toString();
	}

	public RetCodes writeAdaptorInfo(List<AppInfo> rules, AdaptorConf ibc) {
		String sdavc_target = "";
		String json_req = "";
		
		logger.info("SD-AVC Writer - write app info " + ibc.get_val("adaptor_name") + " entity " + ibc.get_val("name"));

		Client client = get_sdavc_client(ibc);
		if (client==null) {
			logger.error("Client is null when login to SD-AVC");
			return RetCodes.ERROR;
		}

		try {
			sdavc_target = String.format(
					"https://%s:%s/avc-sd-service/external-api/app-rules",
					ibc.get_val("sdavc_ipv4addr"), 
					"8443");  
			
			logger.info("target_url: " + sdavc_target);
			
			json_req = appInfotoJson(rules, ibc);
			
			WebTarget webTarget = client.target(sdavc_target);
			Response response = webTarget
					.request(MediaType.APPLICATION_JSON)
					.header("Content-Type", "application/json")
					.header("Authorization", this.token)
					.post(Entity.json(json_req));
			if (response.getStatus() == 403) {  // Forbidden, try to refresh token and retry query
				if (sdavc_login(ibc) == RetCodes.SUCCESS) {
					response = webTarget
							.request(MediaType.APPLICATION_JSON)
							.header("Content-Type", "application/json")
							.header("Authorization", this.token)
							.post(Entity.json(json_req));
				} else {
					throw new RuntimeException("Error: Unable to login to SD-AVC");
				}
			}
			if (response.getStatus() != 200) {
				throw new RuntimeException("Error: SD-AVC external adaptor API request failed, error code="
						+ response.getStatus());
			}
			logger.info("output: " + response.readEntity(String.class));
		} catch (Exception e) {
			logger.error("Rest API to SD-AVC failed. Target=" + sdavc_target);
			logger.info(json_req);
			return RetCodes.ERROR;
		}

		return RetCodes.SUCCESS;
	}
}
