/**
 * File: IBloxFileReader.java
 * Description: Read from file for testing. Result is similar to HTTPS
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors.reader.infoblox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.log4j.Logger;

import com.bendvora.sd_avc_connectors.AppInfo;
import com.bendvora.sd_avc_connectors.config.AdaptorConf;
import com.bendvora.sd_avc_connectors.reader.AdaptorReaderIf;

public class IBloxFileReader implements AdaptorReaderIf, IBloxReaderIfHelper {
	
	public final static Logger logger = Logger.getLogger(IBloxFileReader.class);
	private String a_record_file = null;
	private String txt_record_file = null;
	private IBloxProcessor proc = null;

	public IBloxFileReader(String a_record_file, String txt_record_file) {
		this.a_record_file = a_record_file;
		this.txt_record_file = txt_record_file;
		this.proc = new IBloxProcessor(this, false);
	}

	private String read_file(String filename, Charset encoding) throws IOException 
	{
		logger.info("Read file " + filename);
		InputStream is;
		is = new FileInputStream(new File(filename));

		BufferedReader inputStream = new BufferedReader (new InputStreamReader(is));
        StringBuilder out = new StringBuilder();
        String line;
        try {
			while ((line = inputStream.readLine()) != null) {
			    out.append(line);
			}
	        inputStream.close();
	    	return out.toString();
		} catch (IOException e) {
			logger.error("ERROR to parsee file  "+ filename, e);
			return null;
		}
	}

	@Override
	public String read_iblox_record(AdaptorConf conf, String record_type) {
		String output = "";

		try {
			switch (record_type) {
			case "txt":
				output = read_file(txt_record_file, Charset.forName("UTF-8"));
				break;
			case "a":
				output = read_file(a_record_file, Charset.forName("UTF-8"));
				break;
			case "aaaa":
				output = "[]";
				break;
			default:
				output = "[]";
			}
			return output;

		} catch (Exception e) {
			logger.error("Read infoblox record failed. Record type=" + record_type,e);
		}

		return output;
	}

	@Override
	public List<AppInfo> read_adaptor_record(AdaptorConf conf) {
		return proc.read_iblox_record(conf);
	}
}
