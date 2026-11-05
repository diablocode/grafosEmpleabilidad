# Plan de implementación por capas: Ontología de empleabilidad

## Propósito

Construir la ontología y su integración con Apache Jena en capas pequeñas, verificables e incrementales. Al terminar cada capa, se ejecutan sus criterios de validación y se revisan antes de iniciar la siguiente. Este plan se basa en los requisitos de `PRD.md`; no implementa código ni archivos RDF/OWL.

## Reglas de trabajo

- Avanzar en orden y no iniciar una capa mientras la anterior no cumpla su criterio de salida.
- Usar datos sintéticos durante desarrollo y pruebas; no cargar datos personales reales.
- Mantener Turtle como fuente principal de edición y generar o actualizar RDF/XML de forma controlada.
- Cada capa debe incluir sus pruebas y documentación antes de marcarse como terminada.
- Registrar cambios de vocabulario que afecten capas ya validadas y volver a ejecutar sus validaciones dependientes.
- No modelar postulaciones ni su seguimiento: están fuera del alcance del sistema.

## Capas y puntos de validación

### Capa 0 — Preparación y decisiones bloqueantes

**Objetivo:** dejar definidos los parámetros necesarios para crear identificadores estables y evitar datos reales durante el modelado.

**Actividades**

- Usar durante el desarrollo `https://localhost:3030/diablocode/grafosEmpleabilidad/ontology/empleabilidad#`. Es un IRI local no publicable; antes de distribuir la ontología se debe acordar un dominio persistente y migrar coordinadamente los identificadores.
- Usar el IRI base estable de clases y propiedades; registrar cada publicación con versión semántica (`MAJOR.MINOR.PATCH`) en `owl:versionInfo` y en una etiqueta Git.
- Usar el prefijo propio `emp` y documentar `rdf`, `rdfs`, `owl`, `xsd`, `skos`, `prov` y `schema` cuando se utilicen.
- Nombrar clases en `UpperCamelCase`, propiedades en `lowerCamelCase` y usar etiquetas en español con idioma `es`.
- Definir `RedSocial` como persona o grupo de contacto profesional (no una plataforma electrónica) y `RecursoSocial` como recurso o habilidad suave accesible mediante esa conexión.
- Tratar perfiles socioemocionales y datos de conexiones profesionales como opcionales y sensibles; no incluir atributos protegidos en el perfil socioemocional. Usar solo datos sintéticos en fixtures y exigir que propósito, fuente, acceso y base legítima estén definidos antes de usar datos reales.

**Validación de salida**

- [x] IRI base local confirmado para desarrollo; queda pendiente acordar un IRI persistente antes de publicar.
- [x] Prefijos, estrategia de versión y convención de nombres documentados.
- [x] Alcance de redes personales y datos socioemocionales documentado en el PRD.
- [x] Se acordó que los fixtures usarán exclusivamente datos sintéticos, sin información personal real.

**Punto de revisión:** aprobar las decisiones antes de crear identificadores persistentes.

### Capa 1 — Esqueleto de ontología OWL

**Objetivo:** establecer el documento de ontología y el vocabulario de clases sin añadir todavía instancias del dominio.

**Actividades**

- Crear la ontología en Turtle con IRI, versión, etiquetas, descripciones y prefijos.
- Declarar y documentar las clases del PRD: `Candidato`, `Competencia`, `HabilidadSuave`, `RankingDeCompetencia`, `Educacion`, `ExperienciaLaboral`, `Habilidad`, `Lenguaje`, `PerfilSocioemocional`, `FactorSocioemocional`, `RedSocial`, `RecursoSocial`, `OfertaLaboral`, `Empleador`, `RequerimientoDeCompetencia`, `TrayectoriaLaboral`, `EtapaEnTrayectoriaLaboral`, `Recomendacion` y `Explicacion`.
- Definir jerarquías solo cuando su semántica esté acordada; no declarar disyunciones o restricciones obligatorias sin justificación.

**Validación de salida**

- [x] Apache Jena carga y parsea la ontología Turtle sin errores.
- [x] Las 19 clases mínimas tienen IRI único, tipo OWL y etiqueta legible.
- [x] Se comprueba que no haya clases de postulaciones.
- [x] La versión y el IRI de ontología corresponden a las decisiones de la Capa 0.

**Punto de revisión:** nombres y etiquetas en español validados; no se añadieron jerarquías ni restricciones no acordadas. Revisar cambios de vocabulario antes de fijar propiedades en la Capa 2.

### Capa 2 — Propiedades y relaciones del dominio

**Objetivo:** conectar las entidades del vocabulario mediante relaciones y propiedades de datos documentadas.

**Actividades**

- Definir propiedades de objeto para las relaciones del PRD: educación, experiencia, competencias, habilidades, nivel de lenguaje, rankings, factores socioemocionales, conexiones sociales, empleador, requerimientos, trayectoria, etapas, recomendaciones, explicaciones y evidencia.
- Definir propiedades de datos para identificadores, puntuaciones, escala, fuente, método, fechas, descripciones, estado, ubicación y modalidad, según corresponda.
- Especificar dominio y rango sin imponer restricciones que contradigan el mundo abierto de OWL.
- Precisar rangos de relaciones con destinos alternativos —por ejemplo, evidencia o elementos recomendados— sin dejar su semántica ambigua.
- Agregar metadatos de procedencia cuando una afirmación sea declarada, observada, evaluada o inferida.

**Validación de salida**

- [x] Cada relación mínima del PRD tiene una propiedad identificable, etiqueta, descripción, dominio y rango documentados.
- [x] Las propiedades de datos usan tipos RDF adecuados y distinguen fechas, texto, identificadores y puntuaciones.
- [x] Un grafo de prueba pequeño se carga en Jena y permite recorrer relaciones principales de perfiles, ofertas y recomendaciones.
- [x] No se incorporan relaciones de postulación ni axiomas que interpreten ausencia de datos como falsedad.

**Punto de revisión:** propiedades y dominios/rangos verificados con Apache Jena. Revisar la terminología del vocabulario antes de comenzar la Capa 3.

### Capa 3 — Escalas y catálogos controlados

**Objetivo:** expresar de forma consistente los valores que el PRD define como escalas o tipos controlados.

**Actividades**

- Documentar la escala de competencia del 1 al 5, incluyendo el significado de cada valor acordado.
- Representar el MCER para niveles de lenguaje A1–C2, conservando el identificador de escala.
- Definir los tipos controlados de recomendación: oferta compatible, competencia por desarrollar, recurso de aprendizaje, mejora de perfil y siguiente acción sugerida.
- Usar SKOS para conceptos controlados cuando corresponda y conservar la referencia a la escala en cada evaluación.
- No definir niveles socioemocionales hasta contar con un instrumento validado y autorizado.

**Validación de salida**

- [x] Los valores admitidos y sus etiquetas/definiciones están documentados y no se confunden entre escalas.
- [x] Cada puntuación de competencia puede vincularse a su escala.
- [x] Los niveles de lenguaje aceptados corresponden al MCER.
- [x] Datos socioemocionales no reciben escalas o valores inventados.

**Punto de revisión:** catálogos SKOS y enlaces desde rankings, lenguajes y recomendaciones verificados con pruebas; la escala ordinal propia se valida en la Capa 4.

### Capa 4 — Validación de datos con SHACL

**Objetivo:** detectar datos incompletos o incoherentes mediante reglas explícitas, sin usar OWL como validador cerrado.

**Actividades**

- Definir shapes para perfiles, evaluaciones, ofertas, requerimientos, trayectorias y recomendaciones.
- Validar tipos, rangos de escala, fechas coherentes y campos mínimos acordados para cada caso de uso.
- Separar errores bloqueantes de advertencias y documentar el resultado esperado.
- Integrar la validación con las capacidades SHACL de Apache Jena.

**Validación de salida**

- [x] Un conjunto válido de datos sintéticos pasa la validación.
- [x] Casos inválidos para escalas, niveles y fechas son reportados como violaciones.
- [x] Los errores y advertencias se distinguen por severidad; las advertencias no bloquean la aceptación.
- [x] No se marca como inválida la ausencia de información opcional o sensible que el PRD no obliga a registrar.

**Punto de revisión:** formas Turtle verificadas con Apache Jena SHACL. `EmployabilityDataValidator.hasBlockingViolations` distingue violaciones bloqueantes de advertencias, porque Jena puede devolver `conforms=false` cuando solo hay advertencias. Revisar los campos mínimos antes de aplicar las formas a datos externos.

### Capa 5 — Datos de ejemplo integrados

**Objetivo:** demostrar que el vocabulario representa perfiles y ofertas completos, manteniendo los datos ficticios.

**Actividades**

- Crear ejemplos sintéticos para al menos cinco candidatos y cinco ofertas laborales.
- Incorporar competencias, habilidades, educación, experiencia, trayectorias, empleadores y requerimientos.
- Incluir rankings con escala y procedencia, niveles de lenguaje, recomendaciones y sus explicaciones.
- Añadir ejemplos de redes personales o perfiles socioemocionales solo de forma ficticia y opcional; no introducir atributos protegidos como señales para recomendar.

**Validación de salida**

- [ ] El conjunto de datos satisface las shapes de la Capa 4.
- [ ] Todos los vínculos principales del PRD aparecen en al menos un ejemplo.
- [ ] No hay identificadores, nombres, perfiles o datos de personas reales.
- [ ] Un revisor puede interpretar cada recomendación y localizar su explicación y evidencia.

**Punto de revisión:** verificar que los ejemplos son comprensibles y no afirman más de lo que sus fuentes sintéticas respaldan.

### Capa 6 — Consultas SPARQL de los casos de uso

**Objetivo:** comprobar que el grafo soporta los casos de uso previstos sin implementar un algoritmo de selección.

**Actividades**

- Consultar perfiles por candidato y recuperar educación, experiencia, competencias y trayectoria.
- Consultar ofertas y sus empleadores y requerimientos de competencia.
- Recuperar rankings junto con escala, fuente, método y fecha.
- Consultar recomendaciones con explicaciones y evidencia.
- Preparar una consulta de exploración de correspondencias entre competencias de candidatos y requisitos de ofertas, sin presentarla como decisión automática de contratación.

**Validación de salida**

- [ ] Cada consulta tiene un resultado esperado verificable sobre los datos sintéticos.
- [ ] Las consultas se ejecutan desde Apache Jena y devuelven las columnas y recursos documentados.
- [ ] No se generan inferencias por ausencia de datos ni se mezclan escalas incompatibles.
- [ ] La consulta de correspondencia se describe como exploratoria, no como algoritmo de ranking o selección.

**Punto de revisión:** confirmar que consultas y resultados cubren los casos de uso del PRD.

### Capa 7 — Serialización RDF/XML y equivalencia

**Objetivo:** distribuir la ontología en Turtle y RDF/XML sin divergencia semántica.

**Actividades**

- Generar o mantener RDF/XML desde la versión aprobada de Turtle con un proceso repetible.
- Guardar RDF/XML con una extensión inequívoca (`.rdf` o `.owl`) y documentar el formato real.
- Comparar las dos representaciones mediante carga en modelos RDF de Jena.

**Validación de salida**

- [ ] Apache Jena parsea Turtle y RDF/XML correctamente.
- [ ] Ambos modelos contienen las mismas clases, propiedades, axiomas y metadatos esenciales.
- [ ] La regeneración de RDF/XML produce un cambio reproducible y no requiere edición manual paralela.
- [ ] Las instrucciones de carga indican explícitamente el formato o usan detección confiable.

**Punto de revisión:** revisar el diff de ambas serializaciones antes de publicar una versión.

### Capa 8 — Integración Java, pruebas y documentación de uso

**Objetivo:** entregar una integración reproducible con el proyecto existente de Apache Jena.

**Actividades**

- Implementar utilidades de carga de ontología y datos, ejecución de consultas y validación SHACL.
- Añadir pruebas automatizadas para carga, vocabulario mínimo, consultas, shapes y equivalencia de formatos.
- Documentar estructura de archivos, comandos para ejecutar pruebas y cómo añadir conceptos sin romper IRIs existentes.
- Documentar limitaciones, privacidad, escalas y decisiones de versionado.

**Validación de salida**

- [ ] `gradlew test` termina correctamente desde un entorno limpio.
- [ ] Las pruebas cubren los criterios de aceptación del PRD y fallan ante fixtures inválidos.
- [ ] Un desarrollador puede seguir la documentación para cargar la ontología, validar un grafo y ejecutar las consultas.
- [ ] No se incluyen credenciales ni datos personales en código, ejemplos o archivos de prueba.

**Punto de revisión:** aceptar la integración completa antes de etiquetar o publicar la primera versión.

## Criterio de finalización

La implementación está lista cuando todas las capas estén aprobadas, los criterios de aceptación del PRD se cumplan y las serializaciones Turtle/RDF/XML, los datos sintéticos, las shapes y las consultas SPARQL estén cubiertos por pruebas reproducibles en Apache Jena.
