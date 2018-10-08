/**
 * File: CSVFileReader.java
 * Description: Read from CSV file 
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors.reader.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.bendvora.sd_avc_connectors.AppInfo;
import com.bendvora.sd_avc_connectors.GenericInfo;
import com.bendvora.sd_avc_connectors.AppInfo.sigType;
import com.bendvora.sd_avc_connectors.config.AdaptorConf;
import com.bendvora.sd_avc_connectors.reader.AdaptorReaderIf;

public class CSVFileReader implements AdaptorReaderIf {
	
	public final static Logger logger = Logger.getLogger(CSVFileReader.class);

	public CSVFileReader() {
	}

	private List<AppInfo> build_from_csv(String filename, Charset encoding) throws IOException 
	{
		HashMap<String, AppInfo> app_info = new HashMap<String, AppInfo>();
		AppInfo info = null;

		logger.info("Read file " + filename);
		InputStream is;
		is = new FileInputStream(new File(filename));

		BufferedReader inputStream = new BufferedReader (new InputStreamReader(is));
        String line;
        try {
			while ((line = inputStream.readLine()) != null) {
				
				// split the line by comma. Assume the first entry is appName and the second is domain
				List<String> appLine = Arrays.asList(line.split(",[ ]*", -1));
				String appName = appLine.get(0);
				String domain = appLine.get(1);
				
				if (appName!=null && domain!=null && !appName.equals("") && !domain.equals("")) {
					info = app_info.get(appName);
					if (info == null) {
						info = new AppInfo(appName);
						app_info.put(appName, info);
					}
					info.addSignature(sigType.DOMAIN, domain, new GenericInfo());
				}
			}
	        inputStream.close();
		} catch (IOException e) {
			logger.error("ERROR to parse file  "+ filename, e);
			return null;
		}
		return new ArrayList<AppInfo>(app_info.values());
	}

	@Override
	public List<AppInfo> read_adaptor_record(AdaptorConf conf) {
		if (conf.get_val("filename") != null) {
			try {
				return build_from_csv(conf.get_val("filename"), Charset.forName("UTF-8"));
			} catch (Exception e) {
				logger.error("Read CSV file failed. filename=" + conf.get_val("filename"),e);
			}
		} else {
			logger.error("Read CSV file no filename specified - check your configuration file");
		}
		return null;
	}
}
