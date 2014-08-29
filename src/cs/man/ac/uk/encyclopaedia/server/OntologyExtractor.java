package cs.man.ac.uk.encyclopaedia.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.OWLAnonymousIndividualImpl;

public class OntologyExtractor {
	
	private final String OBSOLETE_CLASS_URI = "<http://www.geneontology.org/formats/oboInOwl#ObsoleteClass>";
	private OWLOntology ont;
	private OWLDataFactory df;
	
	Map<String, String> classesWithLabels = new TreeMap <String, String> ();
	Map<String, String> classesWithDefs = new TreeMap <String, String> ();

	public void loadOntology (File ontURI) {
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		try {
			ont = man.loadOntologyFromOntologyDocument(ontURI);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		df = man.getOWLDataFactory();
		System.out.println("Loaded: " + ont.getOntologyID());
	}

	public List<OWLClass> getNonObsoleteClasses() {

		List<OWLClass> nonObsoleteList = new ArrayList <OWLClass> ();
		boolean isObsoleteClass = false;

		for (OWLClass cls : ont.getClassesInSignature()) {
			if (!(cls.getSuperClasses(ont).isEmpty())) {
				for (OWLClassExpression clsExpr : cls.getSuperClasses(ont)) {
					if (clsExpr.toString().equals(OBSOLETE_CLASS_URI)) {
						isObsoleteClass = true;
					}
				}
			}
			if (!isObsoleteClass) {
				nonObsoleteList.add(cls);
			}

			isObsoleteClass = false;
		}

		return nonObsoleteList;
	}
	
	private void getClassDefinitions (OWLClass cl) {
		OWLAnnotationProperty hasDefinition = df.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasDefinition"));
		OWLAnnotationProperty rdfsLabel = df.getRDFSLabel();
		Set<OWLAnnotation> definition = cl.getAnnotations(ont, hasDefinition);
		for(OWLAnnotation anno:definition) {
			OWLAnnotationValue value = anno.getValue();
			if(value instanceof OWLAnonymousIndividualImpl) {
				OWLAnonymousIndividual i = (OWLAnonymousIndividualImpl)value;
				for(OWLAnnotationAssertionAxiom ax:ont.getAnnotationAssertionAxioms(i)) {
					if(ax.getProperty().equals(rdfsLabel)) {
						OWLAnnotationValue av = ax.getValue();
						classesWithDefs.put(cl.toString(), removeLangTag(av.toString()));
					}
				}
			}
		}
	}
	
	private void getClassLabels (OWLClass cl) {
		for (OWLAnnotation annotation : cl.getAnnotations(ont, df.getRDFSLabel())) {
			if (annotation.getValue() instanceof OWLLiteral) {
			    OWLLiteral val = (OWLLiteral) annotation.getValue();
			    classesWithLabels.put(cl.toString(), val.getLiteral());
			   }
		}
	}
	
	// Not the final solution but will do for now
	private String removeLangTag (String text) {
		return text.replaceAll("@en", "");
	}

	public Map<String, String> getClassesWithDefinitions (List<OWLClass> classes) throws NullPointerException{
		
		for(OWLClass cl:classes) {
			getClassDefinitions(cl);
			getClassLabels(cl);
		}
		
		Map<String, String> labelsWithDefs = new TreeMap <String, String> ();
		for (String key : classesWithDefs.keySet()) {
			labelsWithDefs.put(classesWithLabels.get(key), classesWithDefs.get(key));
		}
		
		return labelsWithDefs;
	}
}
