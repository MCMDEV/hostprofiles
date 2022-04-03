package de.mcmdev.hostprofiles.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Profile {

	private final String id;
	private final UUID owner;
	private final UUID uuid;
	private final String name;
	private final boolean skinCopy;
	private final String skinValue;
	private final String skinSignature;

}
