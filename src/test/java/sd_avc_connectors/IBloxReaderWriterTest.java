package sd_avc_connectors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bendvora.sd_avc_connectors.AdaptorHandler;
import com.bendvora.sd_avc_connectors.RetCodes;
import com.bendvora.sd_avc_connectors.config.file.AdaptorConfFile;
import com.bendvora.sd_avc_connectors.reader.AdaptorReaderIf;
import com.bendvora.sd_avc_connectors.reader.infoblox.IBloxFileReader;
import com.bendvora.sd_avc_connectors.writer.console.AdaptorWriterConsole;

// This tests one cycle of reading config (from file),
// reading infoblox data (from file) and writing (to console)
public class IBloxReaderWriterTest {

	@Test
	public void test() {
		txt_test();
		extattr_test();
	}
	
	public void txt_test() {
		AdaptorConfFile cf = new AdaptorConfFile("test_conf1.json");
		AdaptorWriterConsole wc = new AdaptorWriterConsole();
		AdaptorReaderIf rd = new IBloxFileReader("a_record_test.json", "txt_record_test.json");
		
		AdaptorHandler handler = new AdaptorHandler();
		assertEquals(RetCodes.SUCCESS, handler.init());
		assertEquals(RetCodes.SUCCESS, handler.addAdaptor("Infoblox", cf, rd, wc));
		assertEquals(RetCodes.SUCCESS, handler.exec_once());
		
	    String app_info_text = "[\n"
	            + "App Info ## (Hash=-1180383909):\n"
	            + "  app_attr_app_set: UNDEFINED\n"
	            + "  app_attr_business_relevance: YES\n"
	            + "  app_attr_category: UNDEFINED\n"
	            + "  app_attr_sub_category: UNDEFINED\n"
	            + "  app_attr_traffic_class: TRANSACTIONAL-DATA\n"
	            + "  app_name: Nir3App\n"
	            + "Domains: [{domain:nir3.nirbd.com, extra_info:{## (Hash=17):}}], \n"
	            + "App Info ## (Hash=-1180443491):\n"
	            + "  app_attr_app_set: UNDEFINED\n"
	            + "  app_attr_business_relevance: YES\n"
	            + "  app_attr_category: UNDEFINED\n"
	            + "  app_attr_sub_category: UNDEFINED\n"
	            + "  app_attr_traffic_class: TRANSACTIONAL-DATA\n"
	            + "  app_name: Nir1App\n"
	            + "Domains: [{domain:nir1.nirbd.com, extra_info:{## (Hash=17):}}], \n"
	            + "App Info ## (Hash=-1180413700):\n"
	            + "  app_attr_app_set: UNDEFINED\n"
	            + "  app_attr_business_relevance: YES\n"
	            + "  app_attr_category: UNDEFINED\n"
	            + "  app_attr_sub_category: UNDEFINED\n"
	            + "  app_attr_traffic_class: TRANSACTIONAL-DATA\n"
	            + "  app_name: Nir2App\n"
	            + "Domains: [{domain:nir2.nirbd.com, extra_info:{## (Hash=17):}}]]";
	            
		assertEquals(app_info_text, wc.get_app_info());
	}
	
	public void extattr_test() {
		AdaptorConfFile cf = new AdaptorConfFile("test_conf3.json");
		AdaptorWriterConsole wc = new AdaptorWriterConsole();
		AdaptorReaderIf rd = new IBloxFileReader("a_record_test.json", "txt_record_test.json");
		
		AdaptorHandler handler = new AdaptorHandler();
		assertEquals(RetCodes.SUCCESS, handler.init());
		assertEquals(RetCodes.SUCCESS, handler.addAdaptor("Infoblox", cf, rd, wc));
		assertEquals(RetCodes.SUCCESS, handler.exec_once());
		
	    String app_info_text = "[\n"
	            + "App Info ## (Hash=-523844250):\n"
	            + "  app_attr_app_set: UNDEFINED\n"
	            + "  app_attr_business_relevance: UNDEFINED\n"
	            + "  app_attr_category: UNDEFINED\n"
	            + "  app_attr_sub_category: UNDEFINED\n"
	            + "  app_attr_traffic_class: UNDEFINED\n"
	            + "  app_name: Nir3App\n"
	            + "Domains: [{domain:nir3.nirbd.com, extra_info:{## (Hash=17):}}], \n"
	            + "App Info ## (Hash=-523814459):\n"
	            + "  app_attr_app_set: UNDEFINED\n"
	            + "  app_attr_business_relevance: UNDEFINED\n"
	            + "  app_attr_category: UNDEFINED\n"
	            + "  app_attr_sub_category: UNDEFINED\n"
	            + "  app_attr_traffic_class: UNDEFINED\n"
	            + "  app_name: Nir4App\n"
	            + "Domains: [{domain:nir4.nirbd.com, extra_info:{## (Hash=17):}}], \n"
	            + "App Info ## (Hash=567932678):\n"
	            + "  app_attr_app_set: UNDEFINED\n"
	            + "  app_attr_business_relevance: UNDEFINED\n"
	            + "  app_attr_category: UNDEFINED\n"
	            + "  app_attr_sub_category: UNDEFINED\n"
	            + "  app_attr_traffic_class: TRANSACTIONAL-DATA\n"
	            + "  app_name: Nir1App\n"
	            + "Domains: [{domain:nir1.nirbd.com, extra_info:{## (Hash=17):}}], \n"
	            + "App Info ## (Hash=1917798370):\n"
	            + "  app_attr_app_set: UNDEFINED\n"
	            + "  app_attr_business_relevance: UNDEFINED\n"
	            + "  app_attr_category: UNDEFINED\n"
	            + "  app_attr_sub_category: UNDEFINED\n"
	            + "  app_attr_traffic_class: VOIP-TELEPHONY\n"
	            + "  app_name: Nir2App\n"
	            + "Domains: [{domain:nir2.nirbd.com, extra_info:{## (Hash=17):}}]]";		
	    
		System.out.println(app_info_text);
		System.out.println(wc.get_app_info());
		assertEquals(app_info_text, wc.get_app_info());
	}
}
