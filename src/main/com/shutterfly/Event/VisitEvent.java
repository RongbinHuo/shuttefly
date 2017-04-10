package com.shutterfly.Event;

import java.util.Date;

public class VisitEvent extends Event {
	String key;
	String verb;
	Date event_time;
	String customer_id;

	public VisitEvent(String key, String verb, Date event_time, String customer_id) {
		this.key = key;
		this.verb = verb;
		this.event_time = event_time;
		this.customer_id = customer_id;
	}

	public Date getDate() {
		return this.event_time;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Event)) {
			return false;
		}
		VisitEvent otherNode = (VisitEvent) other;
		return event_time == otherNode.event_time;
	}

	@Override
	public int compareTo(Object o) {
		if (!(o instanceof VisitEvent))
			throw new ClassCastException("A VisitEvent object expected.");
		Date anothertime = ((VisitEvent) o).event_time;
		if (event_time.after(anothertime)) {
			return 1;
		} else if (event_time.before(anothertime)) {
			return -1;
		} else {
			return 0;
		}
	}

}
