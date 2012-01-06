package com.tommytony.war.config;

import com.tommytony.war.FlagReturn;
import com.tommytony.war.TeamSpawnStyle;

public enum TeamConfig {
	FLAGMUSTBEHOME (Integer.class),
	FLAGPOINTSONLY (Boolean.class),
	FLAGRETURN (FlagReturn.class),
	LIFEPOOL (Integer.class),
	MAXSCORE (Integer.class),
	NOHUNGER (Boolean.class),
	RESPAWNTIMER (Integer.class),
	SATURATION (Integer.class),
	SPAWNSTYLE (TeamSpawnStyle.class),
	TEAMSIZE (Integer.class);
	
	private final Class<?> configType;

	private TeamConfig(Class<?> configType) {
		this.configType = configType;		
	}

	public Class<?> getConfigType() {
		return configType;
	}
	
	public static TeamConfig teamConfigFromString(String str) {
		String lowered = str.toLowerCase();
		for (TeamConfig config : TeamConfig.values()) {
			if (config.toString().startsWith(lowered)) {
				return config;
			}
		}
		return null;
	}
		
	public String toStringWithValue(Object value) {
		return this.toString() + ":" + value.toString();
	}
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}