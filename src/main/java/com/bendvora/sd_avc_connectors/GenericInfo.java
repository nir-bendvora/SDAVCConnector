/**
 * File: Generic.java
 * Description: Class for storing the generic info
 * Created by: Nir Ben-Dvora
 */
package com.bendvora.sd_avc_connectors;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

public class GenericInfo implements Comparable<GenericInfo> {
	
	private HashMap<String, String> generic_map = new HashMap<String, String>();
	
	public void set_val(String key, String val) {
		generic_map.put(key, val);
	}
	
	// Will return null if value doesn't exist
	public String get_val(String key) {
		return generic_map.get(key);
	}

	public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("## (Hash=" + new Integer(this.hashCode()).toString() + "):");
        // Convert to tree map for printing to print in key order
        for (Entry<String, String> entry : new TreeMap<>(generic_map).entrySet()) {
            sb.append("\n  " + entry.getKey() + ": " + entry.getValue());
        }
        return sb.toString();
	}
	
	public Integer get_size() {
		return generic_map.size();
	}
	
	// Override the equals method
	public boolean equals(Object obj) {
	    if (!(obj instanceof GenericInfo)) return false;
        GenericInfo another = (GenericInfo) obj;
        if (this.get_size() != another.get_size()) return false;
        for (Entry<String, String> entry : generic_map.entrySet()) {
            if (!another.get_val(entry.getKey()).equals(entry.getValue())) return false;
        }
        return true;
	}
	
	// Override the hashcode method
	public int hashCode() {
		int result = 17;
        for (Entry<String, String> entry : generic_map.entrySet()) {
        	// Use a linear hash for order independence
        	result = result + entry.getValue().hashCode();
        }
        return result;
	}

	// Override the compareTo method
	// Sort by hash code since data is generic
	public int compareTo(GenericInfo o) {
		if (this.hashCode() == o.hashCode()) return 0;
		if (this.hashCode() > o.hashCode()) return 1;
		return -1;
	}
	
	
}
