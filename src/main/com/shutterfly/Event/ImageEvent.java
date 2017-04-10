package com.shutterfly.Event;

import java.util.Date;

public class ImageEvent extends Event {

	String key;
	String verb;
	Date event_time;
	String customer_id;
	String camera_make;
	String camera_model;

	public ImageEvent(String key, String verb, Date event_time, String customer_id) {
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
		if (!(other instanceof ImageEvent)) {
			return false;
		}
		Event otherNode = (ImageEvent) other;
		return event_time == otherNode.event_time;
	}

	@Override
	public int compareTo(Object o) {
		if (!(o instanceof ImageEvent))
			throw new ClassCastException("A ImageEvent object expected.");
		Date anothertime = ((ImageEvent) o).event_time;
		if (event_time.after(anothertime)) {
			return 1;
		} else if (event_time.before(anothertime)) {
			return -1;
		} else {
			return 0;
		}
	}
}
