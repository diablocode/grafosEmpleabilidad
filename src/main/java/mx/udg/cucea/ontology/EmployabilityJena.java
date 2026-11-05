package mx.udg.cucea.ontology;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public final class EmployabilityJena {
    private static final String ONTOLOGY_RESOURCE = "/ontology/empleabilidad.ttl";
    private static final String EXAMPLE_RESOURCE = "/examples/empleabilidad-ejemplo.ttl";

    private EmployabilityJena() {}

    public static Model loadOntology() {
        Model ontology = ModelFactory.createDefaultModel();
        try {
            readTurtleResource(ontology, ONTOLOGY_RESOURCE);
            return ontology;
        } catch (RuntimeException exception) {
            ontology.close();
            throw exception;
        }
    }

    public static Model loadExampleGraph() {
        Model graph = loadOntology();
        try {
            readTurtleResource(graph, EXAMPLE_RESOURCE);
            return graph;
        } catch (RuntimeException exception) {
            graph.close();
            throw exception;
        }
    }

    public static ResultSetRewindable executeSelect(Model model, String queryText) {
        Objects.requireNonNull(model, "model must not be null");
        Objects.requireNonNull(queryText, "queryText must not be null");
        var query = QueryFactory.create(queryText);
        if (!query.isSelectType()) {
            throw new IllegalArgumentException("Only SPARQL SELECT queries are supported");
        }
        try (QueryExecution execution = QueryExecution.create(query, model)) {
            return ResultSetFactory.copyResults(execution.execSelect());
        }
    }

    public static ResultSetRewindable executeSelectResource(Model model, String resourcePath) {
        Objects.requireNonNull(model, "model must not be null");
        Objects.requireNonNull(resourcePath, "resourcePath must not be null");
        InputStream input = EmployabilityJena.class.getResourceAsStream(resourcePath);
        if (input == null) {
            throw new IllegalArgumentException("SPARQL query resource was not found: " + resourcePath);
        }
        try (input) {
            String queryText = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            return executeSelect(model, queryText);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read SPARQL query resource: " + resourcePath, exception);
        }
    }

    private static void readTurtleResource(Model model, String resourcePath) {
        InputStream input = EmployabilityJena.class.getResourceAsStream(resourcePath);
        if (input == null) {
            throw new IllegalStateException("RDF resource was not found: " + resourcePath);
        }
        try (input) {
            RDFDataMgr.read(model, input, Lang.TURTLE);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read RDF resource: " + resourcePath, exception);
        }
    }
}
