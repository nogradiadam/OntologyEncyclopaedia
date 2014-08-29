package cs.man.ac.uk.encyclopaedia.client;

import java.util.Map;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("encyclopaedia")
public interface OntologyEncyclopaediaService extends RemoteService {
	Map<String, String> getOntClassesWithDefinitions() throws NullPointerException;
}
