package cs.man.ac.uk.encyclopaedia.client;

import java.util.Map;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface OntologyEncyclopaediaServiceAsync {
	void getOntClassesWithDefinitions(AsyncCallback<Map<String, String>> callback);
}
