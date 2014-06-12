package cs.man.ac.uk.encyclopaedia.server;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLClass;

import cs.man.ac.uk.encyclopaedia.client.OntologyEncyclopaediaService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class OntologyEncyclopaediaServiceImpl extends RemoteServiceServlet implements
		OntologyEncyclopaediaService {

	@Override
	public Map<String, String> getOntClassesWithDefinitions()
			throws IllegalArgumentException {
		System.out.println("About to open file...");
		File goont = new File(System.getProperty("user.dir") + "/resources/goFewMore.owl");
		OntologyExtractor handler = new OntologyExtractor();
		System.out.println("Loading...");
		handler.loadOntology(goont);
		System.out.println("Processing...");
		List<OWLClass> nonObsoleteClasses = handler.getNonObsoleteClasses();
		Map<String, String> classesWithDefinitions =  handler.getClassesWithDefinitions(nonObsoleteClasses);
		
		System.out.println("Done");
		return classesWithDefinitions;
	}

	
}
