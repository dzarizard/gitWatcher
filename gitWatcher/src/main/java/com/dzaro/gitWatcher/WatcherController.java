package com.dzaro.gitWatcher;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class WatcherController {

	private static final String API_LINK = "https://api.github.com/";
	RestTemplate restTemplate = new RestTemplate();

	@GetMapping("/gitwatcher/{user}/{repo}/releases")
	public String getReleases(@PathVariable String user, @PathVariable String repo) {
		StringBuilder url = prepareUrl(user, repo);
		ResponseEntity<List<ReleaseInfoData>> releaseResponse;
		try {
			releaseResponse =
					restTemplate.exchange(url.toString(),
							HttpMethod.GET,
							null,
							new ParameterizedTypeReference<List<ReleaseInfoData>>() {
							});
		} catch (HttpClientErrorException e) {
			return "Repository or user not found";
		}

		if (releaseResponse.getBody() != null) {
			return "Repository: " +
					repo +
					" of user: " +
					user +
					": " +
					releaseResponse.getBody().get(0).toString();
		}
		return "No releases available for " + repo + " repository of user " + user;
	}

	private StringBuilder prepareUrl(String user, String repo) {
		StringBuilder url = new StringBuilder(API_LINK);
		url.append("repos/");
		url.append(user);
		url.append("/");
		url.append(repo);
		url.append("/releases");
		return url;
	}

}