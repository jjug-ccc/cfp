package jjug.slack;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SlackWebhookPayload implements Serializable {
	private String text;
	private String channel;
	private String username;
	@JsonProperty("icon_emoji")
	private String iconEmoji;
}
