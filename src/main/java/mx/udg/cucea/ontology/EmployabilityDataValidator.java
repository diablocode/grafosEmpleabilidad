package mx.udg.cucea.ontology;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;

public final class EmployabilityDataValidator {
    private static final String SHACL_NS = "http://www.w3.org/ns/shacl#";
    private static final Shapes SHAPES = loadShapes();

    private EmployabilityDataValidator() {}

    public static ValidationReport validate(Model data) {
        Objects.requireNonNull(data, "data must not be null");
        return ShaclValidator.get().validate(SHAPES, data.getGraph());
    }

    public static boolean hasBlockingViolations(ValidationReport report) {
        Objects.requireNonNull(report, "report must not be null");
        return report.getModel().contains(
                null,
                report.getModel().createProperty(SHACL_NS + "resultSeverity"),
                report.getModel().createResource(SHACL_NS + "Violation"));
    }

    private static Shapes loadShapes() {
        InputStream input = EmployabilityDataValidator.class.getResourceAsStream(
                "/shapes/empleabilidad.shacl.ttl");
        if (input == null) {
            throw new IllegalStateException("SHACL shapes resource was not found");
        }

        Model model = ModelFactory.createDefaultModel();
        try (input) {
            RDFDataMgr.read(model, input, Lang.TURTLE);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read the SHACL shapes resource", exception);
        }
        return Shapes.parse(model.getGraph());
    }
}
