package com.shutterfly.Event;

import java.util.Date;

public class CustomerEvent extends Event{
	String key;
	String verb;
	Date event_time;
	String last_name;
	String adr_city;
	String adr_state;

	public CustomerEvent(String key, String verb, Date event_time) {
		this.key = key;
		this.verb = verb;
		this.event_time = event_time;
	}
	
	public boolean isFirstEvent(){
		return verb.equalsIgnoreCase("NEW");
	}
	
	public Date getDate(){
		return this.event_time;
	}
	@Override 
	public boolean equals(Object other) {
	    if (!(other instanceof Event)) {
	      return false;
	    }
	    CustomerEvent otherNode = (CustomerEvent) other;
	    return event_time == otherNode.event_time;
	  }
	@Override
	public int compareTo(Object o) {
		if (!(o instanceof CustomerEvent))
			throw new ClassCastException("A CustomerEvent object expected.");
		Date anothertime = ((CustomerEvent) o).event_time;
		if(event_time.after(anothertime)){
			return 1;
		}else if(event_time.before(anothertime)){
			return -1;
		}else{
			return 0;
		}
	}
	// TODO: Add method to handle other information
}
