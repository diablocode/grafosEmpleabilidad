package mx.udg.cucea.ontology;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;

class EmployabilityJenaTest {
    @Test
    void loadsOntologyAndExampleGraphAndValidatesExampleData() {
        Model ontology = EmployabilityJena.loadOntology();
        Model graph = EmployabilityJena.loadExampleGraph();
        try {
            assertTrue(ontology.size() > 0);
            assertTrue(graph.size() > ontology.size());
            assertFalse(EmployabilityDataValidator.hasBlockingViolations(
                    EmployabilityDataValidator.validate(graph)));
        } finally {
            ontology.close();
            graph.close();
        }
    }

    @Test
    void executesBundledSelectQueryAndReturnsReusableResults() {
        Model graph = EmployabilityJena.loadExampleGraph();
        try {
            var results = EmployabilityJena.executeSelectResource(
                    graph, "/queries/perfil-candidato.rq");

            assertEquals(20, results.size());
            assertEquals(
                    java.util.Set.of("candidato", "nombre", "educacion", "experiencia", "competencia", "trayectoria"),
                    java.util.Set.copyOf(results.getResultVars()));
            results.reset();
            assertTrue(results.hasNext());
        } finally {
            graph.close();
        }
    }

    @Test
    void rejectsNonSelectQueriesAndMissingResources() {
        Model graph = EmployabilityJena.loadExampleGraph();
        try {
            assertThrows(IllegalArgumentException.class,
                    () -> EmployabilityJena.executeSelect(graph, "ASK { ?s ?p ?o }"));
            assertThrows(IllegalArgumentException.class,
                    () -> EmployabilityJena.executeSelectResource(graph, "/queries/absent.rq"));
        } finally {
            graph.close();
        }
    }
}
