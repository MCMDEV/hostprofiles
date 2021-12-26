package de.mcmdev.hostprofiles.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Host {

	private final String id;
	private final String address;
	private final boolean whitelisted;
	private final String disallowedMessage;
	private final String motd;
	private final List<Profile> profiles;


}
