package org.flupes.zPlatformPcTests;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UnmodifiableMap {

	Map<String, Integer> unmodMap;
	
	UnmodifiableMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("tata", 1);
		map.put("toto", 2);
		map.put("titi", 3);
		
		unmodMap = Collections.unmodifiableMap(map);
		
		System.out.println(unmodMap);
		
		map.put("tata", 4);

		System.out.println(unmodMap);

	}
	
	public static void main(String[] args) {
		new UnmodifiableMap();
	}

}
