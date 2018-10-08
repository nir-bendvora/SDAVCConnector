package sd_avc_connectors;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bendvora.sd_avc_connectors.RetCodes;
import com.bendvora.sd_avc_connectors.config.AdaptorConf;
import com.bendvora.sd_avc_connectors.config.file.AdaptorConfFile;

// This test GenericInfo, AdaptorConf and AdaptorConfFile 
public class IBloxConfFileTest {

	@Test
	public void test() {
		
		AdaptorConfFile f = new AdaptorConfFile("test_conf2.json");
		List<AdaptorConf> conf = null;
		
		assertEquals(RetCodes.SUCCESS, f.init());
		
		try {
			conf = f.get_config();
		} catch (Exception e) {
    		e.printStackTrace();
        }

		assertEquals(2, conf.size());
		
		// Check values
		assertEquals("test", conf.get(0).get_val("name"));
		assertEquals("10.56.74.251", conf.get(0).get_val("ipv4addr"));
		assertEquals("global", conf.get(0).get_val("vrf_name"));
		assertEquals("2.5", conf.get(0).get_val("wapi_version"));
		assertEquals("TXT", conf.get(0).get_val("application_info"));
		assertEquals("admin", conf.get(0).get_val("user"));
		assertEquals("infoblox", conf.get(0).get_val("passwd"));
		assertEquals("nirbd.com", conf.get(0).get_val("zone_filters"));
		
		assertEquals("test2", conf.get(1).get_val("name"));
		assertEquals("10.56.74.255", conf.get(1).get_val("ipv4addr"));
		assertEquals("my-vrf", conf.get(1).get_val("vrf_name"));
		assertEquals("2.1", conf.get(1).get_val("wapi_version"));
		assertEquals("TXT", conf.get(1).get_val("application_info"));
		assertEquals("admin1", conf.get(1).get_val("user"));
		assertEquals("pass1", conf.get(1).get_val("passwd"));
		assertEquals("a.com", conf.get(1).get_val("zone_filters"));
		
		// Check equals and hashCode
		AdaptorConf c = new AdaptorConf();
		c.set_val("name", "test");
		c.set_val("ipv4addr", "10.56.74.251");
		c.set_val("vrf_name", "global");
		c.set_val("wapi_version", "2.5");
		c.set_val("application_info", "TXT");
		c.set_val("user", "admin");
		c.set_val("passwd", "infoblox");
		c.set_val("zone_filters", "nirbd.com");
		c.set_val("segment", "global");
		assertNotEquals(c, conf.get(0));
		assertNotEquals("toString", c.toString(), conf.get(0).toString());
		assertNotEquals("Hash", c.hashCode(), conf.get(0).hashCode());
		// get_config adds poll time to each entry. After adding should be equal
		c.set_val("poll_time", "15");
		assertEquals(c, conf.get(0));
		assertEquals("toString", c.toString(), conf.get(0).toString());
		assertEquals("Hash", c.hashCode(), conf.get(0).hashCode());
		
		// Check equals for the entire List
		List<AdaptorConf> conf2 = new ArrayList<AdaptorConf>();
		conf2.add(c);
		assertNotEquals(conf2, conf);
		AdaptorConf c2 = new AdaptorConf();
		c2.set_val("name", "test2");
		c2.set_val("ipv4addr", "10.56.74.255");
		c2.set_val("vrf_name", "my-vrf");
		c2.set_val("wapi_version", "2.1");
		c2.set_val("application_info", "TXT");
		c2.set_val("user", "admin1");
		c2.set_val("passwd", "pass1");
		c2.set_val("zone_filters", "a.com");
		c2.set_val("segment", "global");
		c2.set_val("poll_time", "15");
		conf2.add(c2);
		assertEquals(conf2, conf);
		
	}

}
