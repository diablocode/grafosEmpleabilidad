# Ontología de empleabilidad

Proyecto Java con Apache Jena para cargar, consultar y validar una ontología de empleabilidad. Los datos incluidos son sintéticos y no representan personas, empleadores u ofertas reales.

## Requisitos

- JDK compatible con la versión de Java configurada en Gradle.
- Windows: usar `.\gradlew.bat`; otros sistemas: `./gradlew`.

## Ejecutar pruebas

```powershell
.\gradlew.bat test
```

Las pruebas validan la estructura OWL y los catálogos, aplican SHACL a los ejemplos, ejecutan las consultas SPARQL y comparan los grafos cargados desde Turtle y RDF/XML.

## Cargar, consultar y validar desde Java

`EmployabilityJena` ofrece métodos para cargar la ontología o el grafo de ejemplo y ejecutar consultas SELECT incluidas en los recursos:

```java
import mx.udg.cucea.ontology.EmployabilityDataValidator;
import mx.udg.cucea.ontology.EmployabilityJena;
import org.apache.jena.rdf.model.Model;

try (Model graph = EmployabilityJena.loadExampleGraph()) {
    var results = EmployabilityJena.executeSelectResource(
            graph, "/queries/perfil-candidato.rq");
    while (results.hasNext()) {
        var row = results.next();
        System.out.println(row.getLiteral("nombre").getString());
    }

    var report = EmployabilityDataValidator.validate(graph);
    if (EmployabilityDataValidator.hasBlockingViolations(report)) {
        throw new IllegalStateException("El grafo contiene violaciones SHACL.");
    }
}
```

`loadOntology()` retorna solo el vocabulario Turtle. `loadExampleGraph()` combina el vocabulario y los datos sintéticos. Los modelos retornados pertenecen al llamador y deben cerrarse. `executeSelect` recibe texto SPARQL; `executeSelectResource` recibe una ruta de recurso del classpath. Ambos métodos solo admiten consultas `SELECT` y retornan resultados materializados e independientes de la ejecución.

Las advertencias SHACL no son violaciones bloqueantes: para decidir si el grafo puede aceptarse, usar `hasBlockingViolations(report)` en lugar de depender únicamente de `report.conforms()`.

## Recursos principales

| Ruta | Contenido |
|---|---|
| `src/main/resources/ontology/empleabilidad.ttl` | Fuente editable de la ontología OWL en Turtle |
| `src/main/resources/ontology/empleabilidad.rdf` | Serialización RDF/XML generada |
| `src/main/resources/shapes/empleabilidad.shacl.ttl` | Restricciones SHACL |
| `src/main/resources/examples/empleabilidad-ejemplo.ttl` | Datos ficticios de muestra |
| `src/main/resources/queries/` | Consultas SPARQL de los casos de uso |

Para regenerar RDF/XML después de editar Turtle:

```powershell
.\gradlew.bat generateOntologyRdfXml
```

Turtle es la fuente de verdad; no se debe editar RDF/XML manualmente. La prueba de serialización comprueba que ambos formatos produzcan grafos isomorfos.

## Extender la ontología

1. Añadir clases, propiedades, axiomas o conceptos controlados en `empleabilidad.ttl`, reutilizando el prefijo `emp` y el IRI base actual.
2. Añadir o ajustar restricciones en `empleabilidad.shacl.ttl` y actualizar los fixtures y pruebas correspondientes.
3. Regenerar `empleabilidad.rdf` con la tarea Gradle.
4. Ejecutar `.\gradlew.bat test` y revisar los cambios de ambas serializaciones.

El IRI actual usa `https://localhost:3030/diablocode/grafosEmpleabilidad/ontology/empleabilidad#` y es exclusivamente local/de desarrollo; no es publicable. Antes de publicar, definir un IRI persistente bajo un dominio controlado y migrar coordinadamente las referencias. Mantener el IRI y `owl:versionIRI` versionados sin cambiar el significado de términos existentes.

Las escalas no son intercambiables: las competencias usan valores de 1 a 5 y los idiomas los niveles MCER A1–C2. Las evaluaciones deben conservar fuente, método, fecha y escala. Los datos socioemocionales y de redes sociales son opcionales y requieren controles de consentimiento, acceso, retención y protección de datos en la aplicación que los utilice. Las coincidencias de competencias son exploratorias, no un ranking ni una decisión automatizada de contratación.
