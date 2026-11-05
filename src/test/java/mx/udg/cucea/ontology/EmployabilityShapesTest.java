package mx.udg.cucea.ontology;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ValidationReport;
import org.junit.jupiter.api.Test;

class EmployabilityShapesTest {
    private static final String NS =
            "https://localhost:3030/diablocode/grafosEmpleabilidad/ontology/empleabilidad#";

    @Test
    void validSyntheticDatasetPassesIncludingOptionalSensitiveDataOmitted() {
        ValidationReport report = validate(validDataset());

        assertFalse(EmployabilityDataValidator.hasBlockingViolations(report), report.getModel().toString());
    }

    @Test
    void invalidScoreRequirementLevelAndDatesAreRejected() {
        String invalidData = validDataset()
                .replace("emp:puntuacion 4", "emp:puntuacion 6")
                .replace("emp:nivelEsperado 4", "emp:nivelEsperado 6")
                .replace("\"2025-01-01\"^^xsd:date", "\"2026-01-01\"^^xsd:date");

        ValidationReport report = validate(invalidData);

        assertFalse(report.conforms(), report.getModel().toString());
        assertTrue(EmployabilityDataValidator.hasBlockingViolations(report), report.getModel().toString());
    }

    @Test
    void rankingWithoutScaleAndRecommendationWithoutEvidenceAreRejected() {
        String invalidData = validDataset()
                .replace("emp:escala \"" + NS + "escalaCompetencia1a5\"^^xsd:anyURI ;\n", "")
                .replace(
                        "emp:utilizaEvidencia emp:competenciaEjemplo .",
                        "emp:descripcion \"Explicación sin evidencia\" .");

        ValidationReport report = validate(invalidData);

        assertFalse(report.conforms(), report.getModel().toString());
        assertTrue(EmployabilityDataValidator.hasBlockingViolations(report), report.getModel().toString());
    }

    @Test
    void missingProfileIdentifierTrajectoryStageAndRecommendationFieldsAreRejected() {
        String incompleteData = """
                @prefix emp: <%s> .
                emp:candidatoIncompleto a emp:Candidato .
                emp:trayectoriaIncompleta a emp:TrayectoriaLaboral .
                emp:etapaIncompleta a emp:EtapaEnTrayectoriaLaboral .
                emp:recomendacionIncompleta a emp:Recomendacion .
                """.formatted(NS);

        ValidationReport report = validate(incompleteData);

        assertTrue(EmployabilityDataValidator.hasBlockingViolations(report), report.getModel().toString());
    }

    @Test
    void missingOptionalOfferLocationProducesWarningWithoutBlockingData() {
        String dataWithoutLocation = validDataset()
                .replace("    emp:ubicacion \"Guadalajara\" ;\n", "");

        ValidationReport report = validate(dataWithoutLocation);

        assertFalse(EmployabilityDataValidator.hasBlockingViolations(report), report.getModel().toString());
        assertTrue(hasSeverity(report, "Warning"), report.getModel().toString());
        assertFalse(report.conforms(), "Jena reports a warning result as non-conforming; callers must inspect severity.");
    }

    @Test
    void languageLevelMustBelongToMcerAndUseItsScale() {
        String invalidData = validDataset()
                .replace("emp:nivelMCER emp:nivelB2", "emp:nivelMCER emp:competenciaNivel4")
                .replace("\"" + NS + "escalaMCER\"^^xsd:anyURI", "\"" + NS + "escalaCompetencia1a5\"^^xsd:anyURI");

        ValidationReport report = validate(invalidData);

        assertTrue(EmployabilityDataValidator.hasBlockingViolations(report), report.getModel().toString());
    }

    @Test
    void languageLevelCannotBeAnUncontrolledLiteral() {
        String invalidData = validDataset().replace(
                "emp:nivelMCER emp:nivelB2 ;",
                "emp:nivelMCER emp:nivelB2 ;\n    emp:nivel \"experto\" ;");

        ValidationReport report = validate(invalidData);

        assertTrue(EmployabilityDataValidator.hasBlockingViolations(report), report.getModel().toString());
    }

    private ValidationReport validate(String dataTurtle) {
        return EmployabilityDataValidator.validate(parseTurtle(dataTurtle));
    }

    private boolean hasSeverity(ValidationReport report, String severity) {
        String severityUri = "http://www.w3.org/ns/shacl#" + severity;
        return report.getModel()
                .contains(null, report.getModel().createProperty(
                        "http://www.w3.org/ns/shacl#resultSeverity"),
                        report.getModel().createResource(severityUri));
    }

    private Model parseTurtle(String turtle) {
        Model model = ModelFactory.createDefaultModel();
        byte[] bytes = turtle.getBytes(StandardCharsets.UTF_8);
        RDFDataMgr.read(model, new ByteArrayInputStream(bytes), Lang.TURTLE);
        return model;
    }

    private String validDataset() {
        return """
                @prefix emp: <%s> .
                @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

                emp:candidatoEjemplo a emp:Candidato ;
                    emp:identificador "candidate-001" ;
                    emp:tieneRanking emp:rankingEjemplo ;
                    emp:dominaLenguaje emp:idiomaEjemplo ;
                    emp:tieneTrayectoria emp:trayectoriaEjemplo .

                emp:competenciaEjemplo a emp:Competencia .

                emp:rankingEjemplo a emp:RankingDeCompetencia ;
                    emp:evaluaCompetencia emp:competenciaEjemplo ;
                    emp:puntuacion 4 ;
                    emp:escala "%sescalaCompetencia1a5"^^xsd:anyURI ;
                    emp:fechaEvaluacion "2025-01-01"^^xsd:date ;
                    emp:fuente "https://example.org/evaluacion/1"^^xsd:anyURI ;
                    emp:metodoEvaluacion "Evaluación de ejemplo" .

                emp:idiomaEjemplo a emp:Lenguaje ;
                    emp:nivelMCER emp:nivelB2 ;
                    emp:escala "%sescalaMCER"^^xsd:anyURI .

                emp:trayectoriaEjemplo a emp:TrayectoriaLaboral ;
                    emp:contieneEtapa emp:etapaEjemplo .

                emp:etapaEjemplo a emp:EtapaEnTrayectoriaLaboral ;
                    emp:etapaDeExperiencia emp:experienciaEjemplo .

                emp:experienciaEjemplo a emp:ExperienciaLaboral ;
                    emp:fechaInicio "2020-01-01"^^xsd:date ;
                    emp:fechaFin "2022-01-01"^^xsd:date .

                emp:empleadorEjemplo a emp:Empleador ;
                    emp:identificador "employer-001" ;
                    emp:nombre "Empleador sintético" .

                emp:ofertaEjemplo a emp:OfertaLaboral ;
                    emp:identificador "offer-001" ;
                    emp:nombre "Oferta sintética" ;
                    emp:publicadoPor emp:empleadorEjemplo ;
                    emp:ubicacion "Guadalajara" ;
                    emp:fechaInicio "2025-01-01"^^xsd:date ;
                    emp:fechaFin "2025-12-31"^^xsd:date .

                emp:requerimientoEjemplo a emp:RequerimientoDeCompetencia ;
                    emp:especificaCompetencia emp:competenciaEjemplo ;
                    emp:nivelEsperado 4 ;
                    emp:esObligatorio true .

                emp:ofertaEjemplo emp:requiereCompetencia emp:requerimientoEjemplo .

                emp:explicacionEjemplo a emp:Explicacion ;
                    emp:utilizaEvidencia emp:competenciaEjemplo .

                emp:recomendacionEjemplo a emp:Recomendacion ;
                    emp:tipoRecomendacion emp:ofertaCompatible ;
                    emp:fechaCreacion "2025-02-01T10:00:00Z"^^xsd:dateTime ;
                    emp:versionModelo "0.1.0" ;
                    emp:recomiendaOferta emp:ofertaEjemplo ;
                    emp:tieneExplicacion emp:explicacionEjemplo .
                """.formatted(NS, NS, NS);
    }
}
