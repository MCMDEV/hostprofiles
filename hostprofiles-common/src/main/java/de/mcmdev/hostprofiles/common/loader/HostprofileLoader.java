package de.mcmdev.hostprofiles.common.loader;

import de.mcmdev.hostprofiles.common.model.Host;

import java.util.List;

public interface HostprofileLoader {

	List<Host> load() throws LoadingException;

}
