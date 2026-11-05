package mx.udg.cucea.ontology;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;

class EmployabilityExampleDataTest {
    private static final String NS =
            "https://localhost:3030/diablocode/grafosEmpleabilidad/ontology/empleabilidad#";

    @Test
    void integratedSyntheticExampleHasTwentyProfilesAndFiveOffersAndPassesShapes() {
        Model model = loadExample();

        assertEquals(20, countInstances(model, "Candidato"));
        assertEquals(5, countInstances(model, "OfertaLaboral"));
        assertEquals(5, countInstances(model, "Empleador"));
        assertEquals(20, countInstances(model, "Educacion"));
        assertEquals(20, countInstances(model, "ExperienciaLaboral"));
        assertEquals(20, countInstances(model, "RankingDeCompetencia"));
        assertEquals(5, countInstances(model, "RequerimientoDeCompetencia"));
        assertEquals(20, countInstances(model, "TrayectoriaLaboral"));
        assertEquals(20, countInstances(model, "EtapaEnTrayectoriaLaboral"));
        assertEquals(5, countInstances(model, "Recomendacion"));
        assertEquals(5, countInstances(model, "Explicacion"));
        assertEquals(0, countInstances(model, "PerfilSocioemocional"));
        assertEquals(1, countInstances(model, "RedSocial"));
        assertEquals(1, countInstances(model, "RecursoSocial"));

        assertEquals(
                "Juan Pablo González Ramírez",
                model.createResource(NS + "candidato001")
                        .getProperty(model.createProperty(NS + "nombre"))
                        .getString());
        assertEquals(
                "Ingeniería en Sistemas Computacionales",
                model.createResource(NS + "educacion001")
                        .getProperty(model.createProperty(NS + "nombre"))
                        .getString());
        assertEquals(
                "Desarrollador backend en Soluciones Digitales del Bajío",
                model.createResource(NS + "experiencia001")
                        .getProperty(model.createProperty(NS + "nombre"))
                        .getString());

        Resource candidate001 = model.createResource(NS + "candidato001");
        Resource contact = model.createResource(NS + "contactoProfesional001");
        Resource socialResource = model.createResource(NS + "recursoMentoria001");
        assertTrue(candidate001.hasProperty(model.createProperty(NS + "conoceA"), contact));
        assertTrue(contact.hasProperty(model.createProperty(NS + "facilitaRecursoSocial"), socialResource));

        for (int index = 1; index <= 5; index++) {
            Resource candidate = model.createResource(NS + String.format("candidato%03d", index));
            Resource offer = model.createResource(NS + String.format("oferta%03d", index));
            Resource recommendation = model.createResource(NS + String.format("recomendacion%03d", index));
            Resource explanation = model.createResource(NS + String.format("explicacion%03d", index));

            assertTrue(candidate.hasProperty(model.createProperty(NS + "tieneEducacion")));
            assertTrue(candidate.hasProperty(model.createProperty(NS + "tieneExperienciaLaboral")));
            assertTrue(candidate.hasProperty(model.createProperty(NS + "poseeCompetencia")));
            assertTrue(candidate.hasProperty(model.createProperty(NS + "tieneRanking")));
            assertTrue(candidate.hasProperty(model.createProperty(NS + "tieneTrayectoria")));
            assertTrue(offer.hasProperty(model.createProperty(NS + "publicadoPor")));
            assertTrue(offer.hasProperty(model.createProperty(NS + "requiereCompetencia")));
            assertTrue(recommendation.hasProperty(
                    model.createProperty(NS + "recomiendaCandidato"), candidate));
            assertTrue(recommendation.hasProperty(
                    model.createProperty(NS + "recomiendaOferta"), offer));
            assertTrue(recommendation.hasProperty(
                    model.createProperty(NS + "tieneExplicacion"), explanation));
            assertTrue(explanation.hasProperty(model.createProperty(NS + "utilizaEvidencia")));
        }
        assertTrue(model.createResource(NS + "candidato001")
                .hasProperty(model.createProperty(NS + "tieneHabilidadSuave")));

        for (int index = 6; index <= 20; index++) {
            Resource candidate = model.createResource(NS + String.format("candidato%03d", index));
            Resource education = candidate.getPropertyResourceValue(model.createProperty(NS + "tieneEducacion"));

            assertNotNull(education, "Social-sciences candidate " + index + " must have an education");
            String degree = education.getProperty(model.createProperty(NS + "nombre")).getString();
            assertFalse(degree.contains("Ingeniería en Sistemas"));
            assertFalse(degree.contains("Ingeniería en Computación"));
            assertFalse(model.contains(
                            null,
                            model.createProperty(NS + "recomiendaCandidato"),
                            candidate),
                    "Candidate profiles must not be presented as matched to unrelated technology vacancies");
        }
        assertTrue(model.createResource(NS + "educacion006")
                .getProperty(model.createProperty(NS + "nombre"))
                .getString()
                .contains("Sociología"));
        assertTrue(model.createResource(NS + "educacion007")
                .getProperty(model.createProperty(NS + "nombre"))
                .getString()
                .contains("Ciencias Políticas"));
        assertTrue(model.createResource(NS + "educacion008")
                .getProperty(model.createProperty(NS + "nombre"))
                .getString()
                .contains("Historia"));
        assertTrue(model.createResource(NS + "educacion009")
                .getProperty(model.createProperty(NS + "nombre"))
                .getString()
                .contains("Antropología"));

        var report = EmployabilityDataValidator.validate(model);
        assertFalse(EmployabilityDataValidator.hasBlockingViolations(report), report.getModel().toString());
        assertTrue(report.conforms(), "The integrated example should pass without warnings.");
    }

    private int countInstances(Model model, String localName) {
        return model.listResourcesWithProperty(
                        RDF.type,
                        model.createResource(NS + localName))
                .toList()
                .size();
    }

    private Model loadExample() {
        InputStream input = getClass().getResourceAsStream("/examples/empleabilidad-ejemplo.ttl");
        assertNotNull(input, "Synthetic example data must be on the test classpath");
        Model model = ModelFactory.createDefaultModel();
        try (input) {
            RDFDataMgr.read(model, input, Lang.TURTLE);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read the synthetic example data", exception);
        }
        return model;
    }
}
