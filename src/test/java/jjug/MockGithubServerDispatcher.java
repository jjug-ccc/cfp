package jjug;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SEE_OTHER;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class MockGithubServerDispatcher extends Dispatcher {
	private final int port;
	private final CfpUser cfpUser;

	public MockGithubServerDispatcher(int port, CfpUser cfpUser) {
		this.port = port;
		this.cfpUser = cfpUser;
	}

	@Override
	public MockResponse dispatch(RecordedRequest request) {
		String path = request.getPath();
		System.out.println(">> " + path);
		if (path.startsWith("/oauth/authorize")) {
			return this.authorize(request);
		}
		if (path.startsWith("/oauth/access_token")) {
			return this.accessToken();
		}
		if (path.startsWith("/user")) {
			return this.userInfo();
		}
		return new MockResponse().setResponseCode(NOT_FOUND.value())
				.setBody("Not Found.");
	}

	private MockResponse accessToken() {
		return new MockResponse().setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE).setBody(
				"{\"access_token\":\"e72e16c7e42f292c6912e7710c838347ae178b4a\", \"scope\":\"user:email\", \"token_type\":\"bearer\"}");
	}

	private MockResponse userInfo() {
		return new MockResponse().setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
				.setBody(String.format(
						"{\"name\":\"%s\", \"login\":\"%s\", \"email\":\"%s\", \"avatar_url\":\"%s\"}",
						cfpUser.getName(), cfpUser.getGithub(), cfpUser.getEmail(),
						cfpUser.getAvatarUrl()));
	}

	private MockResponse authorize(RecordedRequest request) {
		UriComponents components = UriComponentsBuilder
				.fromHttpUrl(request.getRequestUrl().toString()).build();
		String state = components.getQueryParams().getFirst("state");
		return new MockResponse()
				.setHeader(LOCATION,
						"http://localhost:" + port
								+ "/login?code=708ccd5dd1a033ac03e4&state=" + state)
				.setResponseCode(SEE_OTHER.value());
	}
}
