package com.dzaro.gitWatcher;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class WatcherController {

	private static final String API_LINK = "https://api.github.com/";
	RestTemplate restTemplate = new RestTemplate();

	@GetMapping("/gitwatcher/{user}/{repo}/releases")
	public List<ReleaseInfoData> getReleases(@PathVariable String user, @PathVariable String repo) {
		StringBuilder url = prepareUrl(user, repo);
		return getReleasesFromUrl(url);
	}

	@GetMapping("/gitwatcher/{user}/{repo}/release")
	public String getRelease(@PathVariable String user, @PathVariable String repo) {
		StringBuilder url = prepareUrl(user, repo);
		return getLastReleaseFromUrl(url);
	}

	@GetMapping("/gitwatcher/{user}/{repo}/exists/{release}")
	public String checkRelease(@PathVariable String user, @PathVariable String repo, @PathVariable String release) {
		StringBuilder url = prepareUrl(user, repo);
		List<ReleaseInfoData> availableReleases = getReleasesFromUrl(url);
		availableReleases = availableReleases
				.stream()
				.filter(r -> r.getName().equals(release))
				.collect(Collectors.toList());

		if (!availableReleases.isEmpty()) {
			return "Provided release exists";
		}
		return "Provided release do not exists";
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

	private String getLastReleaseFromUrl(StringBuilder url) {
		List<ReleaseInfoData> releases = getReleasesFromUrl(url);
		if (releases != null && !releases.isEmpty()) {
			return releases.get(0).getName();
		}
		return "Release version not found";
	}

	private List<ReleaseInfoData> getReleasesFromUrl(StringBuilder url) {
		ResponseEntity<List<ReleaseInfoData>> releaseResponse;
		try {
			releaseResponse =
					restTemplate.exchange(url.toString(),
							HttpMethod.GET,
							null,
							new ParameterizedTypeReference<List<ReleaseInfoData>>() {
							});
		} catch (HttpClientErrorException e) {
			return Collections.emptyList();
		}

		if (!Objects.isNull(releaseResponse.getBody())) {
			return releaseResponse.getBody();
		}
		return Collections.emptyList();
	}
}