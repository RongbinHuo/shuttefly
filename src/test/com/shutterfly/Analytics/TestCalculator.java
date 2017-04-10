package com.shutterfly.Analytics;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.shutterfly.Analysis.LTVCalculator;
import com.shutterfly.Event.Event;
import com.shutterfly.Ingest.IngestHandler;
import com.shutterfly.Ingest.TestHandleEventType.EVENT_TYPE;

public class TestCalculator {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	public static enum EVENT_TYPE {CUSTOMER,ORDER,SITE_VISIT,IMAGE};
	static Map<String, HashMap<String, TreeSet<Event>>> map = new ConcurrentHashMap<String, HashMap<String, TreeSet<Event>>>();
	static LTVCalculator calcLTV = new LTVCalculator(map);
	static{
		for(EVENT_TYPE et: EVENT_TYPE.values()){
			HashMap<String, TreeSet<Event>> tmp = new HashMap<String, TreeSet<Event>>();
			map.put(et.toString(), tmp);
		}
	}
	static IngestHandler ingestH = new IngestHandler(map);
	
	public static void cleanup(){
		map.clear();
		for(EVENT_TYPE et: EVENT_TYPE.values()){
			HashMap<String, TreeSet<Event>> tmp = new HashMap<String, TreeSet<Event>>();
			map.put(et.toString(), tmp);
		}
		ingestH = new IngestHandler(map);
		calcLTV = new LTVCalculator(map);
	}
	
	/*
	 * TODO: Create more tests here 
	 */
}
