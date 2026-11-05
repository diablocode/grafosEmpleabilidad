package mx.udg.cucea.ontology;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

class EmployabilityOntologySerializationTest {
    @Test
    void turtleAndRdfXmlRepresentTheSameGraph() {
        Model turtleModel = load("/ontology/empleabilidad.ttl", Lang.TURTLE);
        Model rdfXmlModel = load("/ontology/empleabilidad.rdf", Lang.RDFXML);
        try {
            assertTrue(turtleModel.isIsomorphicWith(rdfXmlModel),
                    "Turtle and RDF/XML must contain the same RDF graph");
        } finally {
            turtleModel.close();
            rdfXmlModel.close();
        }
    }

    private Model load(String resource, Lang language) {
        InputStream input = getClass().getResourceAsStream(resource);
        assertNotNull(input, "Ontology serialization must be on the test classpath: " + resource);
        Model model = ModelFactory.createDefaultModel();
        try (input) {
            RDFDataMgr.read(model, input, language);
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Could not read ontology serialization " + resource, exception);
        }
        return model;
    }
}
