package com.shutterfly.Event;

import java.util.Date;

public class OrderEvent extends Event {
	String key;
	String verb;
	Date event_time;
	String customer_id;
	Double total_amount;

	public OrderEvent(String key, String verb, Date event_time, String customer_id, Double total_amount) {
		this.key = key;
		this.verb = verb;
		this.event_time = event_time;
		this.customer_id = customer_id;
		this.total_amount = total_amount;
	}

	public Date getDate() {
		return this.event_time;
	}

	public double getAmount() {
		return total_amount;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Event)) {
			return false;
		}
		OrderEvent otherNode = (OrderEvent) other;
		return event_time == otherNode.event_time;
	}

	@Override
	public int compareTo(Object o) {
		if (!(o instanceof OrderEvent))
			throw new ClassCastException("A OrderEvent object expected.");
		Date anothertime = ((OrderEvent) o).event_time;
		if (event_time.after(anothertime)) {
			return 1;
		} else if (event_time.before(anothertime)) {
			return -1;
		} else {
			return 0;
		}
	}
}
