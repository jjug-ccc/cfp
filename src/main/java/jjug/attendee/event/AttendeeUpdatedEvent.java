package jjug.attendee.event;

import jjug.attendee.Attendee;

public class AttendeeUpdatedEvent {
	private final Attendee attendee;

	public AttendeeUpdatedEvent(Attendee attendee) {
		this.attendee = attendee;
	}

	public Attendee attendee() {
		return attendee;
	}
}
