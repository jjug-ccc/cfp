package jjug.admin;

import java.io.Serializable;

import jjug.conference.enums.ConfStatus;
import lombok.Data;

@Data
public class ConferenceForm implements Serializable {
	private ConfStatus confStatus;
	private String confCfpNote;
	private String confVoteNote;
}
