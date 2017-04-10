package com.shutterfly.Ingest;

import static org.junit.Assert.assertEquals;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

import com.shutterfly.Event.Event;

public class TestHandleEventType {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	public static enum EVENT_TYPE {CUSTOMER,ORDER,SITE_VISIT,IMAGE};
	static Map<String, HashMap<String, TreeSet<Event>>> map = new ConcurrentHashMap<String, HashMap<String, TreeSet<Event>>>();
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
	}
	/*
	 * Test Initialization
	 */
	@Test
	public void testInitialization(){
		cleanup();
		assertEquals(4, map.size());
	}
	/*
	 * Test it could handle all event type
	 */
	@Test
	public void testHandleAllTypes() {
		cleanup();
		assertEquals(0, map.get("CUSTOMER").size());
		assertEquals(0, map.get("SITE_VISIT").size());
		ingestH.ingest("{\"type\": \"CUSTOMER\", \"verb\": \"NEW\", \"key\": \"96f55c7d8f42\", \"event_time\": \"2016-02-06T12:46:46.384Z\", \"last_name\": \"Smith\", \"adr_city\": \"Middletown\", \"adr_state\": \"AK\"}");
		assertEquals(1, map.get("CUSTOMER").size());
		ingestH.ingest("{\"type\": \"SITE_VISIT\", \"verb\": \"NEW\", \"key\": \"ac05e815502f\", \"event_time\": \"2017-01-06T12:45:52.041Z\", \"customer_id\": \"96f55c7d8f42\", \"tags\": [{\"some key\": \"some value\"}]}");
		assertEquals(1, map.get("SITE_VISIT").size());
		// Handle more type
	}
	
	/*
	 * Test program don't crash on unknow type
	 */
	@Test
	public void testUnknownTypes() {
		cleanup();
		assertEquals(0, map.get("CUSTOMER").size());
		assertEquals(0, map.get("SITE_VISIT").size());
		ingestH.ingest("{\"type\": \"Unknown\", \"verb\": \"NEW\", \"key\": \"96f55c7d8f42\", \"event_time\": \"2016-02-06T12:46:46.384Z\", \"last_name\": \"Smith\", \"adr_city\": \"Middletown\", \"adr_state\": \"AK\"}");
		assertEquals(4, map.size());
		assertEquals(0, map.get("CUSTOMER").size());
		// Handle more type
	}
	
	/*
	 * Test Event is stored in sorted order
	 */
	@Test
	public void testEventSorted(){
		Date startDate = sdf.parse("2016-02-06T12:47:46.384Z".toString(),new ParsePosition(0));
		Date lastDate = sdf.parse("2016-02-13T12:46:46.384Z".toString(),new ParsePosition(0));
		cleanup();
		ingestH.ingest("{\"type\": \"CUSTOMER\", \"verb\": \"EDIT\", \"key\": \"96f55c7d8f42\", \"event_time\": \"2016-02-07T12:46:46.384Z\", \"last_name\": \"Smith\", \"adr_city\": \"Middletown\", \"adr_state\": \"AK\"}");
		ingestH.ingest("{\"type\": \"CUSTOMER\", \"verb\": \"DESTROY\", \"key\": \"96f55c7d8f42\", \"event_time\": \"2016-02-13T12:46:46.384Z\", \"last_name\": \"Smith\", \"adr_city\": \"Middletown\", \"adr_state\": \"AK\"}");
		ingestH.ingest("{\"type\": \"CUSTOMER\", \"verb\": \"NEW\", \"key\": \"96f55c7d8f42\", \"event_time\": \"2016-02-06T12:47:46.384Z\", \"last_name\": \"Smith\", \"adr_city\": \"Middletown\", \"adr_state\": \"AK\"}");
		ingestH.ingest("{\"type\": \"CUSTOMER\", \"verb\": \"EDIT\", \"key\": \"96f55c7d8f42\", \"event_time\": \"2016-02-08T12:48:46.384Z\", \"last_name\": \"Smith\", \"adr_city\": \"Middletown\", \"adr_state\": \"AK\"}");
		ingestH.ingest("{\"type\": \"CUSTOMER\", \"verb\": \"EDIT\", \"key\": \"96f55c7d8f42\", \"event_time\": \"2016-02-09T12:46:49.384Z\", \"last_name\": \"Smith\", \"adr_city\": \"Middletown\", \"adr_state\": \"AK\"}");
		TreeSet<Event> set = map.get("CUSTOMER").get("96f55c7d8f42");
		assertEquals(5, set.size());
		assertEquals(startDate, set.first().getDate());
		assertEquals(lastDate, set.last().getDate());
	}
}
