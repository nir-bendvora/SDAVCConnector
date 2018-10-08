/**
 * File: IBloxHTTPSReader.java
 * Description: Read from HTTPS file, Adaptor for Infoblox
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors.reader.infoblox;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import com.bendvora.sd_avc_connectors.AppInfo;
import com.bendvora.sd_avc_connectors.config.AdaptorConf;
import com.bendvora.sd_avc_connectors.reader.AdaptorReaderIf;

public class IBloxHTTPSReader implements AdaptorReaderIf, IBloxReaderIfHelper {

	public final static Logger logger = Logger.getLogger(AdaptorReaderIf.class);
	private IBloxProcessor proc = null;
	
	public IBloxHTTPSReader() {
		this.proc = new IBloxProcessor(this, false);
	}

	private Client get_infoblox_client(AdaptorConf conf) {
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
			HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(conf.get_val("user"),conf.get_val("passwd"));
			client.register(feature);
			return client;

		} catch (Exception e) {
			logger.error("Creating infoblox client failed");
			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	public String read_iblox_record(AdaptorConf conf, String record_type) {
		String output = "";
		String record_target = "";
		
		Client client = get_infoblox_client(conf);
		if (client==null) {
			logger.error("Client is null when reading infoblox record");
			return output;
		}

		try {
			record_target = String.format(
					"https://%s/wapi/v%s/record:%s?name~=%s&_return_fields%%2b=extattrs&_return_type=json-pretty&_max_results=10000&view=default",
					conf.get_val("ipv4addr"), 
					conf.get_val("wapi_version"), 
					record_type,
					conf.get_val("zone_filters"));    

			WebTarget webTarget = client.target(record_target);
			Response response = webTarget
					.request(MediaType.APPLICATION_JSON)
					.get();
			if (response.getStatus() != 200) {
				throw new RuntimeException("Error: rest client request failed, error code="
						+ response.getStatus());
			}
			output = response.readEntity(String.class);
			return output;
		} catch (Exception e) {
			logger.error("Reading infoblox record failed. Target=" + record_target);
		}

		return output;
	}

	@Override
	public List<AppInfo> read_adaptor_record(AdaptorConf conf) {
		return proc.read_iblox_record(conf);
	}
}
