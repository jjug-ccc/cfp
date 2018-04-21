package jjug.attendee;

public class AttendeeRegisteredEvent {
	private final Attendee attendee;

	public AttendeeRegisteredEvent(Attendee attendee) {
		this.attendee = attendee;
	}

	public Attendee attendee() {
		return attendee;
	}
}
