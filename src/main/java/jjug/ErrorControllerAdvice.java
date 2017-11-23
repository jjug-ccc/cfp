package jjug;

import jjug.conference.ConferenceClosedException;
import jjug.submission.CfpClosedException;
import jjug.submission.CfpFixedException;
import jjug.submission.UnpublishedSubmissionException;
import jjug.vote.VoteClosedException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@ControllerAdvice(annotations = Controller.class)
public class ErrorControllerAdvice {
	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String noSuchEelemtException() {
		return "error/404";
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	String accessDeniedException() {
		return "error/403";
	}

	@ExceptionHandler(BadCredentialsException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	String badCredentialsException() {
		return "error/logout";
	}

	@ExceptionHandler(UnpublishedSubmissionException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String unpublishedSubmissionException() {
		return "submission/unpublished";
	}

	@ExceptionHandler(CfpClosedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String cfpClosedException() {
		return "submission/cfpClosed";
	}

	@ExceptionHandler(VoteClosedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String voteClosedException() {
		return "vote/voteClosed";
	}

	@ExceptionHandler(ConferenceClosedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String conferenceClosedException() {
		return "conference/conferenceClosed";
	}

	@ExceptionHandler(CfpFixedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String cfpFixedException() {
		return "conference/cfpFixed";
	}
}
