package cs.man.ac.uk.encyclopaedia.client;

import java.util.Map;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OntologyEncyclopaedia implements EntryPoint {
	
	private VerticalPanel mainPanel = new VerticalPanel();
	private Label classDefsLabel = new Label();
	
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final OntologyEncyclopaediaServiceAsync ontologyEncyclopaediaService = GWT
			.create(OntologyEncyclopaediaService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Set up the callback object.
				AsyncCallback<Map<String, String>> callback = new AsyncCallback<Map<String, String>>() {
					public void onFailure(Throwable caught) {
						Window.alert("Something's wrong " + caught);
					}

					public void onSuccess(Map<String, String> result) {
						classDefsLabel.setText(result.get(result.keySet().iterator().next()));
						mainPanel.add(classDefsLabel);
						RootPanel.get("classDefs").add(mainPanel);
					}
				};
		
		ontologyEncyclopaediaService.getOntClassesWithDefinitions(callback);
	}
}
