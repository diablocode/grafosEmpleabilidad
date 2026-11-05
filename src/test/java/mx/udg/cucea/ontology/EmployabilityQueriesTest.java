package mx.udg.cucea.ontology;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

class EmployabilityQueriesTest {
    private static final Set<String> PROFILE_COLUMNS =
            Set.of("candidato", "nombre", "educacion", "experiencia", "competencia", "trayectoria");
    private static final Set<String> OFFER_COLUMNS =
            Set.of("oferta", "nombreOferta", "empleador", "nombreEmpleador",
                    "competencia", "nivelEsperado", "obligatorio");
    private static final Set<String> RANKING_COLUMNS =
            Set.of("candidato", "nombreCandidato", "competencia", "puntuacion",
                    "escala", "fuente", "metodo", "fecha");
    private static final Set<String> RECOMMENDATION_COLUMNS =
            Set.of("recomendacion", "candidato", "nombreCandidato", "oferta", "nombreOferta",
                    "tipo", "etiquetaTipo", "fecha", "versionModelo", "explicacion",
                    "descripcionExplicacion", "evidencia");
    private static final Set<String> MATCH_COLUMNS =
            Set.of("candidato", "nombreCandidato", "oferta", "nombreOferta", "competencia");

    @Test
    void profileQueryReturnsAllTwentyProfilesWithDocumentedColumns() {
        var result = execute("perfil-candidato.rq");

        assertEquals(PROFILE_COLUMNS, Set.copyOf(result.resultVars()));
        assertEquals(20, result.rowCount());
    }

    @Test
    void offerQueryReturnsAllOffersAndTheirRequirementDetails() {
        var result = execute("ofertas-y-requerimientos.rq");

        assertEquals(OFFER_COLUMNS, Set.copyOf(result.resultVars()));
        assertEquals(5, result.rowCount());
    }

    @Test
    void rankingQueryReturnsScaleAndProvenanceForEachEvaluation() {
        var result = execute("evaluaciones-y-procedencia.rq");

        assertEquals(RANKING_COLUMNS, Set.copyOf(result.resultVars()));
        assertEquals(20, result.rowCount());
    }

    @Test
    void recommendationQueryReturnsExplanationAndEvidence() {
        var result = execute("recomendaciones-explicadas.rq");

        assertEquals(RECOMMENDATION_COLUMNS, Set.copyOf(result.resultVars()));
        assertEquals(11, result.rowCount());
    }

    @Test
    void exploratoryCompetenceQueryReturnsOnlySharedCompetencePairsWithoutScores() {
        var result = execute("coincidencias-competencia-exploratorias.rq");

        assertEquals(MATCH_COLUMNS, Set.copyOf(result.resultVars()));
        assertEquals(5, result.rowCount());
        assertTrue(result.queryText().contains("no es una puntuación, ranking ni decisión de contratación"));
    }

    private QueryResult execute(String queryFile) {
        Model data = loadExample();
        InputStream input = getClass().getResourceAsStream("/queries/" + queryFile);
        assertNotNull(input, "SPARQL query resource is required: " + queryFile);
        try (input) {
            String queryText = new String(input.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            var query = QueryFactory.create(queryText);
            try (QueryExecution execution = QueryExecution.create(query, data)) {
                var resultSet = execution.execSelect();
                var variables = resultSet.getResultVars();
                int rowCount = 0;
                while (resultSet.hasNext()) {
                    resultSet.next();
                    rowCount++;
                }
                return new QueryResult(variables, rowCount, queryText);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read SPARQL query " + queryFile, exception);
        }
    }

    private Model loadExample() {
        Model model = ModelFactory.createDefaultModel();
        readTurtle(model, "/ontology/empleabilidad.ttl", "Ontology");
        readTurtle(model, "/examples/empleabilidad-ejemplo.ttl", "Synthetic example data");
        return model;
    }

    private void readTurtle(Model model, String resource, String description) {
        InputStream input = getClass().getResourceAsStream(resource);
        assertNotNull(input, description + " must be on the test classpath");
        try (input) {
            RDFDataMgr.read(model, input, Lang.TURTLE);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read " + description.toLowerCase(), exception);
        }
    }

    private record QueryResult(
            java.util.List<String> resultVars,
            int rowCount,
            String queryText) {}
}
