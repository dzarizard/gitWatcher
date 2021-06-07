package com.dzaro.gitWatcher.controllers;

import com.dzaro.gitWatcher.dto.ReleaseData;
import com.dzaro.gitWatcher.pojo.ReleaseInfoData;
import com.dzaro.gitWatcher.repositories.ReleaseRepository;
import com.dzaro.gitWatcher.utils.WatcherUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@EnableScheduling
public class WatcherController {

	private final ReleaseRepository releaseRepository;
	private final RestTemplate restTemplate = new RestTemplate();

	private String userNameToUpdate = null;
	private String projectNameToUpdate = null;

	public WatcherController(ReleaseRepository releaseRepository) {
		this.releaseRepository = releaseRepository;
	}

	@GetMapping("/gitwatcher/{user}/{repo}/releases")
	public List<ReleaseInfoData> getReleases(@PathVariable String user, @PathVariable String repo) {
		StringBuilder url = WatcherUtils.prepareUrlForRepositoryReleases(user, repo);
		return getReleasesFromUrl(url);
	}

	@GetMapping("/gitwatcher/{user}/{repo}/release")
	public String getLastRelease(@PathVariable String user, @PathVariable String repo) {
		StringBuilder url = WatcherUtils.prepareUrlForRepositoryReleases(user, repo);
		return getLastReleaseVersionFromUrl(url);
	}

	@GetMapping("/gitwatcher/{user}/{repo}/exists/{release}")
	public String checkIfReleaseExists(@PathVariable String user, @PathVariable String repo, @PathVariable String release) {
		StringBuilder url = WatcherUtils.prepareUrlForRepositoryReleases(user, repo);
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

	@GetMapping("/gitwatcher/{user}/subscriptions")
	public List<ReleaseInfoData> getSubscribedProjects(@PathVariable String user) {
		StringBuilder url = WatcherUtils.prepareUrlForSubscriptions(user);
		return getReleasesFromUrl(url);
	}

	@GetMapping("/gitwatcher/{userName}/{projectName}/update")
	public void updateVersion(@PathVariable String userName, @PathVariable String projectName) {
		userNameToUpdate = userName;
		projectNameToUpdate = projectName;
		updateVersion();
	}

	@Scheduled(cron = "0 0 12 * * *")
	public void updateVersion() {
		if (userNameToUpdate != null && projectNameToUpdate != null) {
			StringBuilder urlForUpdate = WatcherUtils.prepareUrlForRepositoryReleases(userNameToUpdate, projectNameToUpdate);
			String version = getLastReleaseVersionFromUrl(urlForUpdate);

			ReleaseData oldRelease = releaseRepository.findByUserNameAndProjectName(userNameToUpdate, projectNameToUpdate);
			if (oldRelease == null) {
				ReleaseData newRelease = new ReleaseData(
						userNameToUpdate,
						projectNameToUpdate,
						urlForUpdate.toString(),
						version,
						new Date(),
						new Date()
				);
				releaseRepository.save(newRelease);
			} else if (!oldRelease.getVersion().equals(version)) {
				updateExistingVersion(version, oldRelease);
				releaseRepository.save(oldRelease);
			}
		}
	}

	private void updateExistingVersion(String version, ReleaseData release) {
		release.setVersion(version);
		release.setUpdateDate(new Date());
		release.setCheckDate(new Date());
	}

	private String getLastReleaseVersionFromUrl(StringBuilder url) {
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
					restTemplate.exchange(
							url.toString(),
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