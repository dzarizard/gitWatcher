package com.dzaro.gitWatcher.utils;

public class WatcherUtils {

	private static final String API_LINK = "https://api.github.com/";

	public static StringBuilder prepareUrlForSubscriptions(String user) {
		StringBuilder url = new StringBuilder(API_LINK);
		url.append("users/");
		url.append(user);
		url.append("/subscriptions");
		return url;
	}

	public static StringBuilder prepareUrlForRepositoryReleases(String user, String repo) {
		StringBuilder url = new StringBuilder(API_LINK);
		url.append("repos/");
		url.append(user);
		url.append("/");
		url.append(repo);
		url.append("/releases");
		return url;
	}



}
