package cs.man.ac.uk.encyclopaedia.server;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.model.OWLClass;
import org.junit.Before;
import org.junit.Test;

public class OntologyExtractorTest {
	
	private OntologyExtractor testObj;
	
	@Before
	public void setUp () {
		testObj = new OntologyExtractor();
	}

	@Test
	public void testLoadOntology_with_good_file() {
		File goont = new File(System.getProperty("user.dir") + "/test/resources/goOne.owl");
		testObj.loadOntology(goont);
	}
	
	@Test(expected=NullPointerException.class)
	public void testLoadOntology_with_bad_file() {
		File goont = new File(System.getProperty("user.dir") + "/test/resources/goOneError.owl");
			testObj.loadOntology(goont);
	}

	@Test
	public void testGetNonObsoleteClasses() {
		File goont = new File(System.getProperty("user.dir") + "/test/resources/goOne.owl");
		testObj.loadOntology(goont);
		List<OWLClass> classList = testObj.getNonObsoleteClasses();
		assertEquals ("<http://purl.org/obo/owl/GO#GO_0032688>", classList.get(0).toString());
	}

	@Test
	public void testGetClassesWithDefinitions() {
		File goont = new File(System.getProperty("user.dir") + "/test/resources/goOne.owl");
		testObj.loadOntology(goont);
		List<OWLClass> classList = testObj.getNonObsoleteClasses();
		
		Map<String, String> resultMap = testObj.getClassesWithDefinitions(classList);
		String key = resultMap.keySet().iterator().next();
		assertEquals ("negative regulation of interferon-beta secretion", key);
		assertEquals ("\"Any process that stops, prevents, or reduces the frequency, rate, or extent of interferon-beta secretion.\"", resultMap.get(key));
	}

}
