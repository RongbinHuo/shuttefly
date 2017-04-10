package com.shutterfly.Analysis;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.shutterfly.Event.CustomerEvent;
import com.shutterfly.Event.Event;
import com.shutterfly.Event.ImageEvent;
import com.shutterfly.Event.OrderEvent;
import com.shutterfly.Event.VisitEvent;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.AbstractMap.SimpleEntry;

public class LTVCalculator {
	Map<String, HashMap<String, TreeSet<Event>>> map = new ConcurrentHashMap<String, HashMap<String, TreeSet<Event>>>();
	
	//Use a priority queue to store the user ltv value, sorted by user's ltv value
	PriorityQueue<SimpleEntry<String, Double>> userLTV = new PriorityQueue<SimpleEntry<String, Double>>((a, b) -> -Double.compare(a.getValue(), b.getValue()));

	public LTVCalculator(Map<String, HashMap<String, TreeSet<Event>>> map) {
		this.map = map;
	}

	public void TopXSimpleLTVCustomers(int top) throws Exception {
		//Get all user data
		HashMap<String, TreeSet<Event>> users = map.get("CUSTOMER");
		//Calculate all user ltv and store in priority queue
		for (Map.Entry<String, TreeSet<Event>> usrEntry : users.entrySet()) {
			double expense = calExpense(usrEntry.getKey());
			int visit = calVisit(usrEntry.getKey());
			double expPerVisit = 0.0;
			if (visit != 0)
				expPerVisit = (double) expense / visit;
			double duration = calcDuration(usrEntry.getKey());
			double valPerWeek = (expPerVisit) * (visit / ((duration == 0) ? 1 : duration));
			double ltv_user = 52 * valPerWeek * 10;
			SimpleEntry<String, Double> entry = new SimpleEntry(usrEntry.getKey(), ltv_user);
			userLTV.add(entry);
		}
		
		// Retrieve top rank user and write to output file.
		int queueSize = userLTV.size();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./output/output.txt"), "utf-8"));
		for (int i = 0; i <= top && i < queueSize; i++) {
			SimpleEntry<String, Double> entry = userLTV.poll();
			writer.write("User::" + entry.getKey() + "\t LTV::" + entry.getValue());
			writer.newLine();
		}
		writer.flush();
		writer.close();
	}
	
	/*
	 * Calculate how much the user spend on the website
	 */
	public double calExpense(String user) {
		TreeSet<Event> ordersForUser = map.get("ORDER").get(user);
		if (ordersForUser != null && ordersForUser.size() > 0) {
			return ordersForUser.stream().mapToDouble(x -> ((OrderEvent) x).getAmount()).sum();
		} else {
			return 0.0;
		}
	}
	
	/*
	 * Calculate how many times the user visits the website
	 */
	public int calVisit(String user) {
		TreeSet<Event> visitForUser = map.get("SITE_VISIT").get(user);
		if (visitForUser != null && visitForUser.size() > 0) {
			return visitForUser.size();
		} else {
			return 0;
		}
	}
	/*
	 * Function to calculate how long the first and last action in shutterfly
	 */
	public double calcDuration(String user) throws Exception {
		Date earliestDate = ((CustomerEvent) map.get("CUSTOMER").get(user).first()).getDate();
		Date latestUserDate = ((CustomerEvent) map.get("CUSTOMER").get(user).last()).getDate();
		Date latestOrderDate = map.get("ORDER").get(user) == null ? latestUserDate : ((OrderEvent) map.get("ORDER").get(user).last()).getDate();
		Date latestVisitDate = map.get("SITE_VISIT").get(user) == null ? latestUserDate : ((VisitEvent) map.get("SITE_VISIT").get(user).last()).getDate();
		Date latestImageDate = map.get("IMAGE").get(user) == null ? latestUserDate : ((ImageEvent) map.get("IMAGE").get(user).last()).getDate();
		Date latest = latestOrderDate.after(latestUserDate) ? latestOrderDate : latestUserDate;
		latest = latestVisitDate.after(latest) ? latestVisitDate : latest;
		latest = latestImageDate.after(latest) ? latestImageDate : latest;
		if(earliestDate.after(latest)){
			throw new Exception("Date goes wrong with the CUSTOMER data");
		}
		return (latest.getTime() - earliestDate.getTime()) / (1000 * 60 * 60 * 24);
	}
}
