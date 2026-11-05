package mx.udg.cucea.ontology;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.junit.jupiter.api.Test;

class EmployabilityOntologyTest {
    private static final String ONTOLOGY_IRI =
            "https://localhost:3030/diablocode/grafosEmpleabilidad/ontology/empleabilidad";
    private static final String NS = ONTOLOGY_IRI + "#";
    private static final Set<String> EXPECTED_CLASSES = Set.of(
            "Candidato",
            "Competencia",
            "HabilidadSuave",
            "RankingDeCompetencia",
            "Educacion",
            "ExperienciaLaboral",
            "Habilidad",
            "Lenguaje",
            "PerfilSocioemocional",
            "FactorSocioemocional",
            "RedSocial",
            "RecursoSocial",
            "OfertaLaboral",
            "Empleador",
            "RequerimientoDeCompetencia",
            "TrayectoriaLaboral",
            "EtapaEnTrayectoriaLaboral",
            "Recomendacion",
            "Explicacion");

    @Test
    void ontologyLoadsAndDeclaresTheExpectedClasses() {
        Model model = loadOntology();
        Set<String> actualClasses = model.listResourcesWithProperty(RDF.type, OWL.Class)
                .mapWith(Resource::getURI)
                .toSet();
        Set<String> expectedClassIris = EXPECTED_CLASSES.stream()
                .map(name -> NS + name)
                .collect(Collectors.toSet());

        assertEquals(expectedClassIris, actualClasses);
        assertFalse(actualClasses.stream()
                .anyMatch(iri -> iri.toLowerCase().contains("postulacion")));
        assertTrue(model.containsResource(model.createResource(ONTOLOGY_IRI)));
        assertTrue(model.contains(
                model.createResource(ONTOLOGY_IRI),
                OWL.versionIRI,
                model.createResource(ONTOLOGY_IRI + "/1.0.0")));
        assertEquals("1.0.0", model.getResource(ONTOLOGY_IRI)
                .getProperty(OWL.versionInfo)
                .getString());
    }

    @Test
    void everyClassHasASpanishLabel() {
        Model model = loadOntology();
        Set<String> classesWithoutSpanishLabels = model
                .listResourcesWithProperty(RDF.type, OWL.Class)
                .filterKeep(resource -> !resource.hasProperty(RDFS.label)
                        || !resource.getProperty(RDFS.label).getObject().isLiteral()
                        || !"es".equals(resource.getProperty(RDFS.label)
                                .getLiteral()
                                .getLanguage()))
                .mapWith(Resource::getLocalName)
                .toList()
                .stream()
                .collect(Collectors.toSet());

        assertTrue(classesWithoutSpanishLabels.isEmpty(), classesWithoutSpanishLabels.toString());
    }

    private Model loadOntology() {
        InputStream input = getClass().getResourceAsStream("/ontology/empleabilidad.ttl");
        assertNotNull(input, "Ontology Turtle resource must be on the test classpath");
        Model model = ModelFactory.createDefaultModel();
        try (input) {
            RDFDataMgr.read(model, input, Lang.TURTLE);
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Could not close the ontology resource", exception);
        }
        return model;
    }
}
