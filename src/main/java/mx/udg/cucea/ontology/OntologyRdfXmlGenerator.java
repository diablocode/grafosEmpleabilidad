package mx.udg.cucea.ontology;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public final class OntologyRdfXmlGenerator {
    private OntologyRdfXmlGenerator() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "Usage: OntologyRdfXmlGenerator <input.ttl> <output.rdf>");
        }

        Path inputPath = Path.of(args[0]);
        Path outputPath = Path.of(args[1]);
        Model model = ModelFactory.createDefaultModel();
        try (InputStream input = Files.newInputStream(inputPath);
                OutputStream output = Files.newOutputStream(outputPath)) {
            RDFDataMgr.read(model, input, Lang.TURTLE);
            RDFDataMgr.write(output, model, Lang.RDFXML);
        } finally {
            model.close();
        }
    }
}
