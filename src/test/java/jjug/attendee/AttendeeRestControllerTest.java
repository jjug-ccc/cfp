package jjug.attendee;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = { "classpath:/delete-test-data.sql", "classpath:/insert-test-data.sql",
		"classpath:/delete-test-submission-data.sql",
		"classpath:/insert-test-submission-data.sql" }, executionPhase = BEFORE_TEST_METHOD)
public class AttendeeRestControllerTest {
	@LocalServerPort
	int port;
	@Autowired
	TestRestTemplate restTemplate;

	@Test
	public void attend_created() {
		Map<String, Object> requestBody = new HashMap<String, Object>() {
			{
				put("email", "foo@example.com");
				put("ids", Arrays.asList("00000000-0000-0000-0000-000021000200",
						"00000000-0000-0000-0000-000021000201"));
			}
		};
		ResponseEntity<JsonNode> response = this.restTemplate.postForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees",
				requestBody, JsonNode.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		JsonNode body = response.getBody();
		assertThat(body).isNotNull();
		JsonNode submissions = body.get("submissions");
		assertThat(submissions).hasSize(2);
		assertThat(submissions.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000200");
		assertThat(submissions.get(1).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000201");
		String attendeeId = body.get("attendeeId").asText();
		assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:" + port
						+ "/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId));

		ResponseEntity<JsonNode> attendee = this.restTemplate.getForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId,
				JsonNode.class);
		assertThat(attendee.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode attendeeBody = attendee.getBody();
		assertThat(attendeeBody).isNotNull();
		assertThat(attendeeBody.get("email").asText()).isEqualTo("foo@example.com");
		assertThat(attendeeBody.get("conference").get("confId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000101");
		JsonNode attendeeSubmissions = attendeeBody.get("submissions");
		assertThat(attendeeSubmissions).hasSize(2);
		assertThat(attendeeSubmissions.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000200");
		assertThat(attendeeSubmissions.get(1).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000201");
	}

	@Test
	public void attend_twice_add() {
		Map<String, Object> requestBody = new HashMap<String, Object>() {
			{
				put("email", "foo@example.com");
				put("ids", Arrays.asList("00000000-0000-0000-0000-000021000200",
						"00000000-0000-0000-0000-000021000201"));
			}
		};
		ResponseEntity<JsonNode> response = this.restTemplate.postForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees",
				requestBody, JsonNode.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		JsonNode body = response.getBody();
		assertThat(body).isNotNull();
		JsonNode submissions = body.get("submissions");
		assertThat(submissions).hasSize(2);
		assertThat(submissions.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000200");
		assertThat(submissions.get(1).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000201");
		String attendeeId = body.get("attendeeId").asText();
		assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:" + port
						+ "/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId));
		Map<String, Object> requestBody2 = new HashMap<String, Object>() {
			{
				put("email", "foo@example.com");
				put("ids",
						Arrays.asList("00000000-0000-0000-0000-000021000200",
								"00000000-0000-0000-0000-000021000201",
								"00000000-0000-0000-0000-000021000202"));
			}
		};
		ResponseEntity<JsonNode> response2 = this.restTemplate.postForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees",
				requestBody2, JsonNode.class);
		assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		JsonNode body2 = response2.getBody();
		assertThat(body2).isNotNull();
		JsonNode submissions2 = body2.get("submissions");
		assertThat(submissions2).hasSize(3);
		assertThat(submissions2.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000200");
		assertThat(submissions2.get(1).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000201");
		assertThat(submissions2.get(2).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000202");
		String attendeeId2 = body2.get("attendeeId").asText();
		assertThat(response2.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:" + port
						+ "/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId2));

		ResponseEntity<JsonNode> attendee = this.restTemplate.getForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId,
				JsonNode.class);
		assertThat(attendee.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode attendeeBody = attendee.getBody();
		assertThat(attendeeBody).isNotNull();
		assertThat(attendeeBody.get("email").asText()).isEqualTo("foo@example.com");
		assertThat(attendeeBody.get("conference").get("confId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000101");
		JsonNode attendeeSubmissions = attendeeBody.get("submissions");
		assertThat(attendeeSubmissions).hasSize(3);
		assertThat(attendeeSubmissions.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000200");
		assertThat(attendeeSubmissions.get(1).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000201");
		assertThat(attendeeSubmissions.get(2).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000202");
	}

	@Test
	public void attend_twice_remove() {
		Map<String, Object> requestBody = new HashMap<String, Object>() {
			{
				put("email", "foo@example.com");
				put("ids", Arrays.asList("00000000-0000-0000-0000-000021000200",
						"00000000-0000-0000-0000-000021000201"));
			}
		};
		ResponseEntity<JsonNode> response = this.restTemplate.postForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees",
				requestBody, JsonNode.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		JsonNode body = response.getBody();
		assertThat(body).isNotNull();
		JsonNode submissions = body.get("submissions");
		assertThat(submissions).hasSize(2);
		assertThat(submissions.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000200");
		assertThat(submissions.get(1).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000201");
		String attendeeId = body.get("attendeeId").asText();
		assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:" + port
						+ "/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId));
		Map<String, Object> requestBody2 = new HashMap<String, Object>() {
			{
				put("email", "foo@example.com");
				put("ids", Arrays.asList("00000000-0000-0000-0000-000021000201"));
			}
		};
		ResponseEntity<JsonNode> response2 = this.restTemplate.postForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees",
				requestBody2, JsonNode.class);
		assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		JsonNode body2 = response2.getBody();
		assertThat(body2).isNotNull();
		JsonNode submissions2 = body2.get("submissions");
		assertThat(submissions2).hasSize(1);
		assertThat(submissions2.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000201");
		String attendeeId2 = body2.get("attendeeId").asText();
		assertThat(response2.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:" + port
						+ "/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId2));

		ResponseEntity<JsonNode> attendee = this.restTemplate.getForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId,
				JsonNode.class);
		assertThat(attendee.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode attendeeBody = attendee.getBody();
		assertThat(attendeeBody).isNotNull();
		assertThat(attendeeBody.get("email").asText()).isEqualTo("foo@example.com");
		assertThat(attendeeBody.get("conference").get("confId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000101");
		JsonNode attendeeSubmissions = attendeeBody.get("submissions");
		assertThat(attendeeSubmissions).hasSize(1);
		assertThat(attendeeSubmissions.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000201");
	}

	@Test
	public void attend_twice_replace() {
		Map<String, Object> requestBody = new HashMap<String, Object>() {
			{
				put("email", "foo@example.com");
				put("ids", Arrays.asList("00000000-0000-0000-0000-000021000200",
						"00000000-0000-0000-0000-000021000201"));
			}
		};
		ResponseEntity<JsonNode> response = this.restTemplate.postForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees",
				requestBody, JsonNode.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		JsonNode body = response.getBody();
		assertThat(body).isNotNull();
		JsonNode submissions = body.get("submissions");
		assertThat(submissions).hasSize(2);
		assertThat(submissions.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000200");
		assertThat(submissions.get(1).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000201");
		String attendeeId = body.get("attendeeId").asText();
		assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:" + port
						+ "/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId));
		Map<String, Object> requestBody2 = new HashMap<String, Object>() {
			{
				put("email", "foo@example.com");
				put("ids", Arrays.asList("00000000-0000-0000-0000-000021000200",
						"00000000-0000-0000-0000-000021000202"));
			}
		};
		ResponseEntity<JsonNode> response2 = this.restTemplate.postForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees",
				requestBody2, JsonNode.class);
		assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		JsonNode body2 = response2.getBody();
		assertThat(body2).isNotNull();
		JsonNode submissions2 = body2.get("submissions");
		assertThat(submissions2).hasSize(2);
		assertThat(submissions2.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000200");
		assertThat(submissions2.get(1).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000202");
		String attendeeId2 = body2.get("attendeeId").asText();
		assertThat(response2.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:" + port
						+ "/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId2));

		ResponseEntity<JsonNode> attendee = this.restTemplate.getForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId,
				JsonNode.class);
		assertThat(attendee.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode attendeeBody = attendee.getBody();
		assertThat(attendeeBody).isNotNull();
		assertThat(attendeeBody.get("email").asText()).isEqualTo("foo@example.com");
		assertThat(attendeeBody.get("conference").get("confId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000101");
		JsonNode attendeeSubmissions = attendeeBody.get("submissions");
		assertThat(attendeeSubmissions).hasSize(2);
		assertThat(attendeeSubmissions.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000200");
		assertThat(attendeeSubmissions.get(1).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000202");
	}

	@Test
	public void update_add() {
		Map<String, Object> requestBody = new HashMap<String, Object>() {
			{
				put("email", "foo@example.com");
				put("ids", Arrays.asList("00000000-0000-0000-0000-000021000200",
						"00000000-0000-0000-0000-000021000201"));
			}
		};
		ResponseEntity<JsonNode> response = this.restTemplate.postForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees",
				requestBody, JsonNode.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		JsonNode body = response.getBody();
		assertThat(body).isNotNull();
		JsonNode submissions = body.get("submissions");
		assertThat(submissions).hasSize(2);
		assertThat(submissions.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000200");
		assertThat(submissions.get(1).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000201");
		String attendeeId = body.get("attendeeId").asText();
		assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:" + port
						+ "/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId));
		Map<String, Object> requestBody2 = new HashMap<String, Object>() {
			{
				put("ids",
						Arrays.asList("00000000-0000-0000-0000-000021000200",
								"00000000-0000-0000-0000-000021000201",
								"00000000-0000-0000-0000-000021000202"));
			}
		};
		ResponseEntity<JsonNode> response2 = this.restTemplate.postForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId,
				requestBody2, JsonNode.class);
		assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode body2 = response2.getBody();
		assertThat(body2).isNotNull();
		JsonNode submissions2 = body2.get("submissions");
		assertThat(submissions2).hasSize(3);
		assertThat(submissions2.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000200");
		assertThat(submissions2.get(1).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000201");
		assertThat(submissions2.get(2).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000202");

		ResponseEntity<JsonNode> attendee = this.restTemplate.getForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId,
				JsonNode.class);
		assertThat(attendee.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode attendeeBody = attendee.getBody();
		assertThat(attendeeBody).isNotNull();
		assertThat(attendeeBody.get("email").asText()).isEqualTo("foo@example.com");
		assertThat(attendeeBody.get("conference").get("confId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000101");
		JsonNode attendeeSubmissions = attendeeBody.get("submissions");
		assertThat(attendeeSubmissions).hasSize(3);
		assertThat(attendeeSubmissions.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000200");
		assertThat(attendeeSubmissions.get(1).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000201");
		assertThat(attendeeSubmissions.get(2).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000202");
	}

	@Test
	public void update_remove() {
		Map<String, Object> requestBody = new HashMap<String, Object>() {
			{
				put("email", "foo@example.com");
				put("ids", Arrays.asList("00000000-0000-0000-0000-000021000200",
						"00000000-0000-0000-0000-000021000201"));
			}
		};
		ResponseEntity<JsonNode> response = this.restTemplate.postForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees",
				requestBody, JsonNode.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		JsonNode body = response.getBody();
		assertThat(body).isNotNull();
		JsonNode submissions = body.get("submissions");
		assertThat(submissions).hasSize(2);
		assertThat(submissions.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000200");
		assertThat(submissions.get(1).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000201");
		String attendeeId = body.get("attendeeId").asText();
		assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:" + port
						+ "/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId));
		Map<String, Object> requestBody2 = new HashMap<String, Object>() {
			{
				put("ids", Arrays.asList("00000000-0000-0000-0000-000021000201"));
			}
		};
		ResponseEntity<JsonNode> response2 = this.restTemplate.postForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId,
				requestBody2, JsonNode.class);
		assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode body2 = response2.getBody();
		assertThat(body2).isNotNull();
		JsonNode submissions2 = body2.get("submissions");
		assertThat(submissions2).hasSize(1);
		assertThat(submissions2.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000201");

		ResponseEntity<JsonNode> attendee = this.restTemplate.getForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId,
				JsonNode.class);
		assertThat(attendee.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode attendeeBody = attendee.getBody();
		assertThat(attendeeBody).isNotNull();
		assertThat(attendeeBody.get("email").asText()).isEqualTo("foo@example.com");
		assertThat(attendeeBody.get("conference").get("confId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000101");
		JsonNode attendeeSubmissions = attendeeBody.get("submissions");
		assertThat(attendeeSubmissions).hasSize(1);
		assertThat(attendeeSubmissions.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000201");
	}

	@Test
	public void update_replace() {
		Map<String, Object> requestBody = new HashMap<String, Object>() {
			{
				put("email", "foo@example.com");
				put("ids", Arrays.asList("00000000-0000-0000-0000-000021000200",
						"00000000-0000-0000-0000-000021000201"));
			}
		};
		ResponseEntity<JsonNode> response = this.restTemplate.postForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees",
				requestBody, JsonNode.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		JsonNode body = response.getBody();
		assertThat(body).isNotNull();
		JsonNode submissions = body.get("submissions");
		assertThat(submissions).hasSize(2);
		assertThat(submissions.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000200");
		assertThat(submissions.get(1).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000201");
		String attendeeId = body.get("attendeeId").asText();
		assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:" + port
						+ "/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId));
		Map<String, Object> requestBody2 = new HashMap<String, Object>() {
			{
				put("ids", Arrays.asList("00000000-0000-0000-0000-000021000200",
						"00000000-0000-0000-0000-000021000202"));
			}
		};
		ResponseEntity<JsonNode> response2 = this.restTemplate.postForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId,
				requestBody2, JsonNode.class);
		assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode body2 = response2.getBody();
		assertThat(body2).isNotNull();
		JsonNode submissions2 = body2.get("submissions");
		assertThat(submissions2).hasSize(2);
		assertThat(submissions2.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000200");
		assertThat(submissions2.get(1).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000202");

		ResponseEntity<JsonNode> attendee = this.restTemplate.getForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/"
						+ attendeeId,
				JsonNode.class);
		assertThat(attendee.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode attendeeBody = attendee.getBody();
		assertThat(attendeeBody).isNotNull();
		assertThat(attendeeBody.get("email").asText()).isEqualTo("foo@example.com");
		assertThat(attendeeBody.get("conference").get("confId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000101");
		JsonNode attendeeSubmissions = attendeeBody.get("submissions");
		assertThat(attendeeSubmissions).hasSize(2);
		assertThat(attendeeSubmissions.get(0).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000200");
		assertThat(attendeeSubmissions.get(1).get("submissionId").asText())
				.isEqualTo("00000000-0000-0000-0000-000021000202");
	}

	@Test
	public void attendee_notFound() {
		ResponseEntity<JsonNode> response = this.restTemplate.getForEntity(
				"/v1/conferences/00000000-0000-0000-0000-000021000101/attendees/00000000-0000-0000-0000-000000000000",
				JsonNode.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		JsonNode body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.has("message")).isTrue();
		assertThat(body.get("message").asText()).isEqualTo(
				"The given attendeeId(00000000-0000-0000-0000-000000000000) is not found.");
	}
}