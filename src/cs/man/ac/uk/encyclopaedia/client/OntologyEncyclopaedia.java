package cs.man.ac.uk.encyclopaedia.client;

import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OntologyEncyclopaedia implements EntryPoint {
	
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable classesWithDefsFlexTable = new FlexTable();
	
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final OntologyEncyclopaediaServiceAsync ontologyEncyclopaediaService = GWT
			.create(OntologyEncyclopaediaService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		classesWithDefsFlexTable.getRowFormatter().addStyleName(0, "tableHeader");
		// Set up the callback object.
				AsyncCallback<Map<String, String>> callback = new AsyncCallback<Map<String, String>>() {
					public void onFailure(Throwable caught) {
						GWT.log("Error loading the module... \n", caught);
						Window.alert("Something's wrong...\n" + caught);
					}

					public void onSuccess(Map<String, String> result) {
						try {
						sendClassesAndDefsToFrontEnd (result);
						} catch (NullPointerException e) {
							GWT.log("Class labels and definitions don't match...\n", e);
							Window.alert("Error while loading the dataset... \n" + e);
						}
					}
				};
				
		ontologyEncyclopaediaService.getOntClassesWithDefinitions(callback);
	}
	
	private void sendClassesAndDefsToFrontEnd(Map<String, String> result) {
		
		classesWithDefsFlexTable.setText(0,0,"Class Name");
		classesWithDefsFlexTable.setText(0, 1, "Definition");
		int classIndex = 1;
		for (String className : result.keySet()) {
			classesWithDefsFlexTable.setText(classIndex, 0, className);
			classesWithDefsFlexTable.setText(classIndex, 1, result.get(className));
			classIndex++;
		}
		
		mainPanel.add(classesWithDefsFlexTable);
		RootPanel.get("classDefs").add(mainPanel);
	}
}
