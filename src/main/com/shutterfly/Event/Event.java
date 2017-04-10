package com.shutterfly.Event;

import java.util.Date;

public abstract class Event implements Comparable{
	String key;
	Date event_time;
	
	public Date getDate() {
		return this.event_time;
	}
	@Override 
	public boolean equals(Object other) {
	    if (!(other instanceof Event)) {
	      return false;
	    }
	    Event otherNode = (Event) other;
	    return event_time == otherNode.event_time;
	  }
	@Override
	public int compareTo(Object o) {
		return 0;
	}
}
