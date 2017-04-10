package com.shutterfly.Process;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.shutterfly.Analysis.LTVCalculator;
import com.shutterfly.Event.Event;
import com.shutterfly.Ingest.IngestHandler;

public class ShutterFly {
	//Define all types it could receive
	public static enum EVENT_TYPE {CUSTOMER,ORDER,SITE_VISIT,IMAGE};
	//Main data structure to store the data
	static Map<String, HashMap<String, TreeSet<Event>>> map = new ConcurrentHashMap<String, HashMap<String, TreeSet<Event>>>();
	static IngestHandler ingestH = new IngestHandler(map);
	static LTVCalculator calcLTV = new LTVCalculator(map);
	/*
	 * Initialization block
	 */
	static{
		for(EVENT_TYPE et: EVENT_TYPE.values()){
			HashMap<String, TreeSet<Event>> tmp = new HashMap<String, TreeSet<Event>>();
			map.put(et.toString(), tmp);
		}
	}
	public static void main(String[] args) throws Exception {
		try (BufferedReader br = new BufferedReader(new FileReader("./input/input.txt"))) {
			// Parsing the data from string to json
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			String allString = sb.toString();
			JSONArray jsonArray = new JSONArray(allString);
			
			//Ingest all json object to the data structure.
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jo = jsonArray.getJSONObject(i);
				ingestH.ingest(jo.toString());
			}
			//Calculate LTV and write to output
			calcLTV.TopXSimpleLTVCustomers(3);
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			System.out.println("file exp::" + e);
		} catch (IOException e) {
			// TODO: handle exception
			System.out.println("io exp::" + e);
		}
	}
}
