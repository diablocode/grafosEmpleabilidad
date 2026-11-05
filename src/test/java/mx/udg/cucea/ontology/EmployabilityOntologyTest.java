package mx.udg.cucea.ontology;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
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
    private static final Map<String, String[]> EXPECTED_OBJECT_PROPERTIES = Map.ofEntries(
            Map.entry("tieneEducacion", new String[] {"Candidato", "Educacion"}),
            Map.entry("tieneExperienciaLaboral", new String[] {"Candidato", "ExperienciaLaboral"}),
            Map.entry("poseeCompetencia", new String[] {"Candidato", "Competencia"}),
            Map.entry("tieneHabilidad", new String[] {"Candidato", "Habilidad"}),
            Map.entry("tieneHabilidadSuave", new String[] {"Candidato", "HabilidadSuave"}),
            Map.entry("dominaLenguaje", new String[] {"Candidato", "Lenguaje"}),
            Map.entry("tieneRanking", new String[] {"Candidato", "RankingDeCompetencia"}),
            Map.entry("evaluaCompetencia", new String[] {"RankingDeCompetencia", "Competencia"}),
            Map.entry("tienePerfilSocioemocional", new String[] {"Candidato", "PerfilSocioemocional"}),
            Map.entry("tieneFactorSocioemocional", new String[] {"PerfilSocioemocional", "FactorSocioemocional"}),
            Map.entry("conoceA", new String[] {"Candidato", "RedSocial"}),
            Map.entry("facilitaRecursoSocial", new String[] {"RedSocial", "RecursoSocial"}),
            Map.entry("publicadoPor", new String[] {"OfertaLaboral", "Empleador"}),
            Map.entry("requiereCompetencia", new String[] {"OfertaLaboral", "RequerimientoDeCompetencia"}),
            Map.entry("especificaCompetencia", new String[] {"RequerimientoDeCompetencia", "Competencia"}),
            Map.entry("tieneTrayectoria", new String[] {"Candidato", "TrayectoriaLaboral"}),
            Map.entry("contieneEtapa", new String[] {"TrayectoriaLaboral", "EtapaEnTrayectoriaLaboral"}),
            Map.entry("etapaDeExperiencia", new String[] {"EtapaEnTrayectoriaLaboral", "ExperienciaLaboral"}),
            Map.entry("etapaDeEducacion", new String[] {"EtapaEnTrayectoriaLaboral", "Educacion"}),
            Map.entry("recomiendaOferta", new String[] {"Recomendacion", "OfertaLaboral"}),
            Map.entry("recomiendaCandidato", new String[] {"Recomendacion", "Candidato"}),
            Map.entry("tieneExplicacion", new String[] {"Recomendacion", "Explicacion"}),
            Map.entry("utilizaEvidencia", new String[] {"Explicacion", "Resource"}),
            Map.entry("tipoRecomendacion", new String[] {"Recomendacion", "SKOSConcept"}),
            Map.entry("nivelMCER", new String[] {"Lenguaje", "SKOSConcept"}));
    private static final Map<String, String[]> EXPECTED_DATATYPE_PROPERTIES = Map.ofEntries(
            Map.entry("identificador", new String[] {"Thing", "string"}),
            Map.entry("nombre", new String[] {"Thing", "string"}),
            Map.entry("descripcion", new String[] {"Thing", "string"}),
            Map.entry("fuente", new String[] {"Thing", "anyURI"}),
            Map.entry("fechaCreacion", new String[] {"Thing", "dateTime"}),
            Map.entry("fechaInicio", new String[] {"Thing", "date"}),
            Map.entry("fechaFin", new String[] {"Thing", "date"}),
            Map.entry("fechaEvaluacion", new String[] {"RankingDeCompetencia", "date"}),
            Map.entry("puntuacion", new String[] {"Thing", "decimal"}),
            Map.entry("escala", new String[] {"Thing", "anyURI"}),
            Map.entry("metodoEvaluacion", new String[] {"Thing", "string"}),
            Map.entry("nivel", new String[] {"Lenguaje", "string"}),
            Map.entry("nivelEsperado", new String[] {"RequerimientoDeCompetencia", "decimal"}),
            Map.entry("esObligatorio", new String[] {"RequerimientoDeCompetencia", "boolean"}),
            Map.entry("ubicacion", new String[] {"OfertaLaboral", "string"}),
            Map.entry("modalidad", new String[] {"OfertaLaboral", "string"}),
            Map.entry("estadoOferta", new String[] {"OfertaLaboral", "string"}),
            Map.entry("accionRecomendada", new String[] {"Recomendacion", "string"}),
            Map.entry("vigenteHasta", new String[] {"Recomendacion", "date"}),
            Map.entry("versionModelo", new String[] {"Recomendacion", "string"}));

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

    @Test
    void objectPropertiesHaveTheExpectedDomainsAndRanges() {
        Model model = loadOntology();
        Set<String> actualProperties = model
                .listResourcesWithProperty(RDF.type, model.createResource(OWL.NS + "ObjectProperty"))
                .mapWith(Resource::getLocalName)
                .toSet();

        assertEquals(EXPECTED_OBJECT_PROPERTIES.keySet(), actualProperties);
        EXPECTED_OBJECT_PROPERTIES.forEach((name, domainAndRange) -> {
            Resource property = model.createResource(NS + name);
            assertTrue(property.hasProperty(RDFS.domain), name + " is missing its domain");
            assertTrue(property.hasProperty(RDFS.range), name + " is missing its range");
            assertEquals(NS + domainAndRange[0], property.getProperty(RDFS.domain).getResource().getURI());
            String expectedRange = domainAndRange[1].equals("Resource")
                    ? RDFS.Resource.getURI()
                    : domainAndRange[1].equals("SKOSConcept")
                            ? "http://www.w3.org/2004/02/skos/core#Concept"
                    : NS + domainAndRange[1];
            assertEquals(expectedRange, property.getProperty(RDFS.range).getResource().getURI());
            assertTrue(property.hasProperty(RDFS.label), name + " is missing its label");
            assertEquals("es", property.getProperty(RDFS.label).getLanguage());
        });
    }

    @Test
    void datatypePropertiesHaveDocumentedDomainsAndRanges() {
        Model model = loadOntology();
        Set<String> actualProperties = model
                .listResourcesWithProperty(RDF.type, model.createResource(OWL.NS + "DatatypeProperty"))
                .mapWith(Resource::getLocalName)
                .toSet();

        assertEquals(EXPECTED_DATATYPE_PROPERTIES.keySet(), actualProperties);
        EXPECTED_DATATYPE_PROPERTIES.forEach((name, domainAndRange) -> {
            Resource property = model.createResource(NS + name);
            assertTrue(property.hasProperty(RDFS.domain), name + " is missing its domain");
            assertTrue(property.hasProperty(RDFS.range), name + " is missing its range");
            String expectedDomain = domainAndRange[0].equals("Thing")
                    ? OWL.Thing.getURI()
                    : NS + domainAndRange[0];
            assertEquals(expectedDomain, property.getProperty(RDFS.domain).getResource().getURI());
            assertEquals(
                    "http://www.w3.org/2001/XMLSchema#" + domainAndRange[1],
                    property.getProperty(RDFS.range).getResource().getURI());
            assertTrue(property.hasProperty(RDFS.label), name + " is missing its label");
            assertEquals("es", property.getProperty(RDFS.label).getLanguage());
            assertTrue(property.hasProperty(RDFS.comment), name + " is missing its description");
        });
    }

    @Test
    void profileAndOfferRelationsCanBeTraversedInJena() {
        Model model = ModelFactory.createDefaultModel();
        Resource candidate = model.createResource(NS + "candidate-1");
        Resource skill = model.createResource(NS + "skill-1");
        Resource offer = model.createResource(NS + "offer-1");
        Resource requirement = model.createResource(NS + "requirement-1");
        Resource competence = model.createResource(NS + "competence-1");
        Resource recommendation = model.createResource(NS + "recommendation-1");
        Resource explanation = model.createResource(NS + "explanation-1");

        model.add(candidate, model.createProperty(NS + "tieneHabilidad"), skill);
        model.add(offer, model.createProperty(NS + "requiereCompetencia"), requirement);
        model.add(requirement, model.createProperty(NS + "especificaCompetencia"), competence);
        model.add(recommendation, model.createProperty(NS + "recomiendaOferta"), offer);
        model.add(recommendation, model.createProperty(NS + "tieneExplicacion"), explanation);
        model.add(explanation, model.createProperty(NS + "utilizaEvidencia"), skill);

        assertTrue(model.contains(candidate, model.createProperty(NS + "tieneHabilidad"), skill));
        assertTrue(model.contains(offer, model.createProperty(NS + "requiereCompetencia"), requirement));
        assertTrue(model.contains(requirement, model.createProperty(NS + "especificaCompetencia"), competence));
        assertTrue(model.contains(recommendation, model.createProperty(NS + "recomiendaOferta"), offer));
        assertTrue(model.contains(recommendation, model.createProperty(NS + "tieneExplicacion"), explanation));
        assertTrue(model.contains(explanation, model.createProperty(NS + "utilizaEvidencia"), skill));
    }

    @Test
    void controlledCatalogsContainTheDocumentedValues() {
        Model model = loadOntology();
        String skos = "http://www.w3.org/2004/02/skos/core#";
        Property inScheme = model.createProperty(skos + "inScheme");
        Property notation = model.createProperty(skos + "notation");
        Resource conceptType = model.createResource(skos + "Concept");
        Set<String> schemeUris = model.listResourcesWithProperty(RDF.type, model.createResource(skos + "ConceptScheme"))
                .mapWith(Resource::getURI)
                .toSet();

        assertEquals(
                Set.of(NS + "escalaCompetencia1a5", NS + "escalaMCER", NS + "tiposDeRecomendacion"),
                schemeUris);
        assertEquals(Set.of("1", "2", "3", "4", "5"),
                conceptNotations(model, conceptType, inScheme, notation, NS + "escalaCompetencia1a5"));
        assertEquals(Set.of("A1", "A2", "B1", "B2", "C1", "C2"),
                conceptNotations(model, conceptType, inScheme, notation, NS + "escalaMCER"));
        assertEquals(
                Set.of(
                        "oferta-compatible",
                        "competencia-por-desarrollar",
                        "recurso-de-aprendizaje",
                        "mejora-de-perfil",
                        "siguiente-accion-sugerida"),
                conceptNotations(model, conceptType, inScheme, notation, NS + "tiposDeRecomendacion"));
        assertFalse(schemeUris.stream().anyMatch(uri -> uri.toLowerCase().contains("socioemocional")));
    }

    @Test
    void everyControlledConceptHasASpanishPreferredLabel() {
        Model model = loadOntology();
        String skos = "http://www.w3.org/2004/02/skos/core#";
        Resource conceptType = model.createResource(skos + "Concept");
        Property prefLabel = model.createProperty(skos + "prefLabel");
        Set<String> conceptsWithoutSpanishLabels = model
                .listResourcesWithProperty(RDF.type, conceptType)
                .filterKeep(concept -> !concept.hasProperty(prefLabel)
                        || !"es".equals(concept.getProperty(prefLabel).getLanguage()))
                .mapWith(Resource::getURI)
                .toSet();

        assertTrue(conceptsWithoutSpanishLabels.isEmpty(), conceptsWithoutSpanishLabels.toString());
    }

    @Test
    void evaluationsAndRecommendationsReferenceTheirControlledCatalogs() {
        Model model = loadOntology();
        String skos = "http://www.w3.org/2004/02/skos/core#";
        Resource candidate = model.createResource(NS + "candidate-example");
        Resource ranking = model.createResource(NS + "ranking-example");
        Resource language = model.createResource(NS + "language-example");
        Resource recommendation = model.createResource(NS + "recommendation-example");
        Resource competenceScale = model.createResource(NS + "escalaCompetencia1a5");
        Resource languageScale = model.createResource(NS + "escalaMCER");

        model.add(ranking, model.createProperty(NS + "puntuacion"), model.createTypedLiteral(
                "4.0", org.apache.jena.datatypes.xsd.XSDDatatype.XSDdecimal));
        model.add(ranking, model.createProperty(NS + "escala"), model.createTypedLiteral(
                competenceScale.getURI(), org.apache.jena.datatypes.xsd.XSDDatatype.XSDanyURI));
        model.add(language, model.createProperty(NS + "nivel"), "B2");
        model.add(language, model.createProperty(NS + "escala"), model.createTypedLiteral(
                languageScale.getURI(), org.apache.jena.datatypes.xsd.XSDDatatype.XSDanyURI));
        model.add(language, model.createProperty(NS + "nivelMCER"), model.createResource(NS + "nivelB2"));
        model.add(recommendation, model.createProperty(NS + "tipoRecomendacion"),
                model.createResource(NS + "ofertaCompatible"));
        model.add(candidate, model.createProperty(NS + "tieneRanking"), ranking);
        model.add(candidate, model.createProperty(NS + "dominaLenguaje"), language);

        assertEquals(competenceScale.getURI(), model.getProperty(
                ranking, model.createProperty(NS + "escala")).getString());
        assertTrue(model.contains(language, model.createProperty(NS + "nivelMCER"),
                model.createResource(NS + "nivelB2")));
        assertEquals(languageScale.getURI(), model.getProperty(
                language, model.createProperty(NS + "escala")).getString());
        assertTrue(model.contains(recommendation, model.createProperty(NS + "tipoRecomendacion"),
                model.createResource(NS + "ofertaCompatible")));
        assertTrue(model.contains(model.createResource(NS + "nivelB2"),
                model.createProperty(skos + "inScheme"), languageScale));
    }

    private Set<String> conceptNotations(
            Model model, Resource conceptType, Property inScheme, Property notation, String schemeUri) {
        return model.listResourcesWithProperty(RDF.type, conceptType)
                .filterKeep(concept -> concept.hasProperty(inScheme, model.createResource(schemeUri)))
                .mapWith(concept -> concept.getProperty(notation).getString())
                .toSet();
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
