package com.deanveloper.playtimeplus.util;

/**
 * Created by Dean on 7/11/2017.
 */
public class ConfigVar {
	private final String key;
	private final String value;

	public ConfigVar(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
