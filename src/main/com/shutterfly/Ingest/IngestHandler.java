package com.shutterfly.Ingest;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.shutterfly.Event.CustomerEvent;
import com.shutterfly.Event.Event;
import com.shutterfly.Event.ImageEvent;
import com.shutterfly.Event.OrderEvent;
import com.shutterfly.Event.VisitEvent;

public class IngestHandler {
	Map<String, HashMap<String, TreeSet<Event>>> map = new ConcurrentHashMap<String, HashMap<String, TreeSet<Event>>>();
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	public IngestHandler(Map<String, HashMap<String, TreeSet<Event>>> map) {
		this.map = map;
	}
	/*
	 * First function to handle ingest event. Convert string and assign different function based on the venet type
	 */
	public void ingest(String eventStr) {
		JSONObject event = new JSONObject(eventStr);
		if (event.has("type")) {
			if (event.get("type").toString().equals("CUSTOMER")) {
				System.out.println("Receive Customer event and ingesting");
				ingestCustomer(event);
			} else if (event.get("type").toString().equals("ORDER")) {
				System.out.println("Receive Order event and ingesting");
				ingestOrder(event);
			} else if (event.get("type").toString().equals("SITE_VISIT")){
				System.out.println("Receive Visit event and ingesting");
				ingestVisit(event);
			} else if (event.get("type").toString().equals("IMAGE")){
				System.out.println("Receive Image event and ingesting");
				ingestImage(event);
			} else{
				ingestOtherEvents(event);
			}
		}
	}
	
	/*
	 * Function to ingest customer data
	 */
	private void ingestCustomer(JSONObject event) {
		//Get according event based on event type
		//Key: customer id
		//Value: sorted events set based on event_time
		HashMap<String, TreeSet<Event>> allCustomers = map.get("CUSTOMER");
		String id = event.get("key").toString();
		String verb = event.get("verb").toString();
		Date event_time = sdf.parse(event.get("event_time").toString(),new ParsePosition(0));
		CustomerEvent ce = new CustomerEvent(id,verb,event_time);
		//Update data structure
		if(allCustomers.containsKey(id)){
			TreeSet<Event> eventList = allCustomers.get(id);
			eventList.add(ce);
		}else{
			TreeSet<Event> eventList = new TreeSet<Event>();
			eventList.add(ce);
			allCustomers.put(id, eventList);
		}
	}
	
	/*
	 * Function to ingest Order data
	 */
	private void ingestOrder(JSONObject event) {
		//Get according event based on event type
		//Key: customer id
		//Value: sorted events set based on event_time
		HashMap<String, TreeSet<Event>> allOrders = map.get("ORDER");
		String id = event.get("key").toString();
		String verb = event.get("verb").toString();
		String customerID = event.get("customer_id").toString();
		Date event_time = sdf.parse(event.get("event_time").toString(),new ParsePosition(0));
		Double total_amount = Double.parseDouble(event.get("total_amount").toString().split(" ")[0]);
		OrderEvent oe = new OrderEvent(id,verb,event_time, customerID, total_amount);
		//Update data structure
		if(allOrders.containsKey(customerID)){
			TreeSet<Event> eventList = allOrders.get(customerID);
			eventList.add(oe);
		}else{
			TreeSet<Event> eventList = new TreeSet<Event>();
			eventList.add(oe);
			allOrders.put(customerID, eventList);
		}
	}
	
	/*
	 * Function to ingest Visit data
	 */
	private void ingestVisit(JSONObject event) {
		//Get according event based on event type
		//Key: customer id
		//Value: sorted events set based on event_time
		HashMap<String, TreeSet<Event>> allVisit = map.get("SITE_VISIT");
		String id = event.get("key").toString();
		String verb = event.get("verb").toString();
		String customerID = event.get("customer_id").toString();
		Date event_time = sdf.parse(event.get("event_time").toString(),new ParsePosition(0));
		VisitEvent ve = new VisitEvent(id, verb, event_time, customerID);
		//Update data structure
		if(allVisit.containsKey(customerID)){
			TreeSet<Event> eventList = allVisit.get(customerID);
			eventList.add(ve);
		}else{
			TreeSet<Event> eventList = new TreeSet<Event>();
			eventList.add(ve);
			allVisit.put(customerID, eventList);
		}
	}
	
	/*
	 * Function to ingest IMAGE data
	 */
	private void ingestImage(JSONObject event) {
		//Get according event based on event type
		//Key: customer id
		//Value: sorted events set based on event_time
		HashMap<String, TreeSet<Event>> allImage = map.get("IMAGE");
		String id = event.get("key").toString();
		String verb = event.get("verb").toString();
		String customerID = event.get("customer_id").toString();
		Date event_time = sdf.parse(event.get("event_time").toString(),new ParsePosition(0));
		ImageEvent ve = new ImageEvent(id, verb, event_time, customerID);
		//Update data structure
		if(allImage.containsKey(customerID)){
			TreeSet<Event> eventList = allImage.get(customerID);
			eventList.add(ve);
		}else{
			TreeSet<Event> eventList = new TreeSet<Event>();
			eventList.add(ve);
			allImage.put(customerID, eventList);
		}
	}

	private void ingestOtherEvents(JSONObject event) {
		//TODO: For now I just handle those three events to calculate LTV
	}
}
