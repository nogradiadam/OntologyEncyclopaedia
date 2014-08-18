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
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owl.owlapi.OWLAnonymousIndividualImpl;

//TODO refactor this class 
public class OntologyExtractor {
	private final String OBSOLETE_CLASS_URI = "<http://www.geneontology.org/formats/oboInOwl#ObsoleteClass>";
	private OWLOntology ont;
	private OWLDataFactory df;

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
	
	//TODO delete this method once implemented elsewhere. Create Mapping of OWL class and its name and def
		private List<String> getClassNames (List<OWLClass> classes) {
			List<String> classNamesList = new ArrayList<String>();

			OWLAnnotationProperty label = df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_CLASS.getIRI());

			for (OWLClass cls : classes) {
				for (OWLAnnotation annotation : cls.getAnnotations(ont, label)) {
					if (annotation.getValue() instanceof OWLLiteral) {
						OWLLiteral val = (OWLLiteral) annotation.getValue();
						classNamesList.add(val.toString());
					}
				}
			}
			return classNamesList;
		}

	public Map<String, String> getClassesWithDefinitions (List<OWLClass> classes){
		List<String> classNames = getClassNames(classes);
		OWLAnnotationProperty hasDefinition = df.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasDefinition"));
		OWLAnnotationProperty rdfsLabel = df.getRDFSLabel();
		Map<String, String> classesWithDefs = new TreeMap <String, String> ();
		
		int classIndex = 0;

		for(OWLClass cl:classes) {
			Set<OWLAnnotation> annos = cl.getAnnotations(ont, hasDefinition);
			for(OWLAnnotation anno:annos) {
				OWLAnnotationValue value = anno.getValue();
				if(value instanceof OWLAnonymousIndividualImpl) {
					OWLAnonymousIndividual i = (OWLAnonymousIndividualImpl)value;
					for(OWLAnnotationAssertionAxiom ax:ont.getAnnotationAssertionAxioms(i)) {
						if(ax.getProperty().equals(rdfsLabel)) {
							OWLAnnotationValue av = ax.getValue();
							// TODO cl.toString() gives the class URI. Need to extract the name literal from it
							classesWithDefs.put(cl.toString(), av.toString());
							classIndex++;
						}
					}
				}
			}
		}
		return classesWithDefs;
	}
}
