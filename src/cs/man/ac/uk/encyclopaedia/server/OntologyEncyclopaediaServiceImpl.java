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
	public Map<String, String> getOntClassesWithDefinitions() throws NullPointerException {
		File goont = new File(System.getProperty("user.dir") + "/resources/goFewMore.owl");
		OntologyExtractor handler = new OntologyExtractor();
		handler.loadOntology(goont);
		List<OWLClass> nonObsoleteClasses = handler.getNonObsoleteClasses();
		Map<String, String> classesWithDefinitions = null;
		try {
			classesWithDefinitions =  handler.getClassesWithDefinitions(nonObsoleteClasses);
		} catch (NullPointerException e) {
			System.out.println("here");
		}
		
		return classesWithDefinitions;
	}

	
}
