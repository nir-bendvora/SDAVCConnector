/**
 * File: AdaptorMain.java
 * Description: Main class for SD-AVC Adaptor 
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors;

import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import com.bendvora.sd_avc_connectors.config.file.AdaptorConfFile;
import com.bendvora.sd_avc_connectors.reader.AdaptorReaderIf;
import com.bendvora.sd_avc_connectors.reader.file.CSVFileReader;
import com.bendvora.sd_avc_connectors.reader.infoblox.IBloxHTTPSReader;
import com.bendvora.sd_avc_connectors.writer.sdavc.AdaptorWriterSdavcAdaptorAPI;

public class AdaptorMain {
	
	public final static Logger logger = Logger.getLogger(AdaptorMain.class);


	/**
	 * Method: main
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Parse the args options
        Options options = new Options();

        Option opt_type = new Option("t", "type", true, "Input type: csv | infoblox");
        opt_type.setRequired(true);
        options.addOption(opt_type);

        Option out_conf = new Option("c", "config", true, "Configuration filename");
        out_conf.setRequired(true);
        options.addOption(out_conf);
        
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        
        String adaptor_name = "";
        AdaptorReaderIf rd_obj = null;

        try {
            cmd = parser.parse(options, args);
            switch (cmd.getOptionValue("type").toLowerCase()) {
            case "csv":
            	// Check that input file is specified
          		rd_obj = new CSVFileReader();
           		adaptor_name = "CSV";
            	break;
            case "infoblox":
        		rd_obj = new IBloxHTTPSReader();
        		adaptor_name = "Infoblox";
        		break;
            default:
            	throw new ParseException("Invalid type option: only csv or infoblox are supported");
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("SDAVCConnector", options);

            System.exit(1);
            return;
        }

		logger.info("Starting " + adaptor_name + " Adaptor");
		
		AdaptorConfFile cf = new AdaptorConfFile(cmd.getOptionValue("config"));
		//AdaptorWriterConsole wc = new AdaptorWriterConsole();
		AdaptorWriterSdavcAdaptorAPI wc = new AdaptorWriterSdavcAdaptorAPI();
		
		AdaptorHandler handler = new AdaptorHandler();
		if (handler.init() != RetCodes.SUCCESS) {
			logger.error("Initializing " + adaptor_name + " adaptor failed");
			System.exit(1);
		}

		if (handler.addAdaptor(adaptor_name, cf, rd_obj, wc) != RetCodes.SUCCESS) {
			logger.error("Adding " + adaptor_name + " adaptor failed");
			System.exit(1);
		}

		Scanner scan = new Scanner(System.in);
	    System.out.println("Enter to start polling. Enter again to stop...");
	    scan.nextLine();
		if (handler.start() != RetCodes.SUCCESS) {
			logger.error("Starting adaptors failed");
		    scan.close();
			System.exit(1);
		}

	    scan.nextLine();
	    scan.close();
		if (handler.stop() != RetCodes.SUCCESS) {
			logger.error("Stopping " + adaptor_name + " adaptor failed");
			System.exit(1);
		}
	}
}
