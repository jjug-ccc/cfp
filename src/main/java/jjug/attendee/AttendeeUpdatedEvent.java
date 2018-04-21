package jjug.attendee;

public class AttendeeUpdatedEvent {
	private final Attendee attendee;

	public AttendeeUpdatedEvent(Attendee attendee) {
		this.attendee = attendee;
	}

	public Attendee attendee() {
		return attendee;
	}
}
