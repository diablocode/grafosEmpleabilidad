# PRD: Ontología de empleabilidad para Apache Jena

## 1. Resumen

Definir una ontología semántica para representar perfiles de candidatos, competencias y habilidades, trayectoria profesional, ofertas laborales y recomendaciones de empleabilidad. La ontología se expresará en RDF y OWL, y podrá cargarse, consultarse y validarse desde el proyecto Java basado en Apache Jena.

Este documento define requisitos y criterios de aceptación; no incluye la implementación del código ni los archivos de la ontología.

## 2. Problema y oportunidad

La información relevante para la empleabilidad suele estar distribuida entre currículos, ofertas laborales, evaluaciones y redes profesionales. Sin un modelo común es difícil relacionar las capacidades de una persona con los requisitos de una vacante, explicar recomendaciones y reutilizar datos entre fuentes.

Una ontología compartida permitirá representar estos elementos con conceptos y relaciones explícitos, consultar sus vínculos mediante SPARQL y aplicar inferencias semánticas sin confundir datos observados con conclusiones calculadas.

## 3. Objetivos

- Representar candidatos, su educación, experiencia y trayectoria laboral.
- Describir competencias, habilidades, lenguajes y su relación con requerimientos laborales.
- Modelar ofertas y empleadores.
- Representar rankings, recomendaciones y explicaciones, incluyendo su procedencia.
- Publicar la ontología en formatos RDF interoperables y utilizarla desde Apache Jena.
- Mantener claros los límites entre datos de origen, evaluaciones y resultados inferidos.

## 4. No objetivos

- Implementar una aplicación completa de búsqueda de empleos.
- Definir un algoritmo de selección o ranking de candidatos.
- Establecer una escala universal de competencias o una taxonomía exhaustiva de ocupaciones.
- Inferir características personales sensibles a partir de datos indirectos.
- Sustituir controles de acceso, consentimiento, anonimización o políticas de retención del sistema.

## 5. Usuarios y casos de uso

**Usuarios principales:** desarrolladores del proyecto, analistas de empleabilidad y componentes que consultan o generan recomendaciones.

1. Registrar el perfil de un candidato con educación, experiencia, competencias y habilidades.
2. Describir una oferta laboral, su empleador y los requerimientos de competencia.
3. Comparar competencias declaradas o evaluadas con requerimientos de una oferta.
4. Consultar el ranking de una competencia y conocer su escala, fuente y fecha.
5. Representar una trayectoria mediante etapas y relacionar cada etapa con educación o experiencia.
6. Consultar una recomendación y acceder a su explicación y a la evidencia que la sustenta.
7. Incorporar los datos socioemocionales de un candidato cuando exista consentimiento.
8. Usar los datos socioemocionales para validar recomendaciones, sin inferir características sensibles ni discriminatorias.
9. Incorporar redes sociales, entendidas como conexiones reales del candidato y no como plataformas electrónicas.

## 6. Alcance del vocabulario

La ontología debe incluir, como mínimo, las siguientes clases. Los nombres se presentan en español como nombres de dominio; al implementar la ontología se definirán identificadores estables, preferentemente en `UpperCamelCase`, y etiquetas legibles en español.

| Clase                        | Propósito                                                                                                                            |
|------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| `Candidato`                  | Persona cuyo perfil profesional se representa.                                                                                       |
| `Competencia`                | Capacidad o dominio requerido o poseído, según contexto.                                                                             |
| `HabilidadSuave`             | Capacidad no tecnica requerido o poseído, según contexto.                                                                  |
| `RankingDeCompetencia`       | Evaluación ordinal o puntuación de una competencia, con escala y procedencia explícitas (del 1 al 5).                                |
| `Educacion`                  | Formación académica o profesional de un candidato.                                                                                   |
| `ExperienciaLaboral`         | Relación o periodo de trabajo con funciones y fechas asociadas.                                                                      |
| `Habilidad`                  | Capacidad práctica o técnica; puede relacionarse con una competencia.                                                                |
| `Lenguaje`                   | Idioma que domina el candidato, es una competencia, con nivel y escala explícitos.                                                   |
| `PerfilSocioemocional`       | Conjunto contextualizado y opcional de evaluaciones socioemocionales; dato sensible sujeto a controles de privacidad. No incluye atributos protegidos como género, origen étnico o religión. |
| `FactorSocioemocional`       | Factor evaluado dentro de un perfil socioemocional.                                                                                  |
| `RedSocial`                  | Persona o grupo de contacto conocido por el candidato y de relevancia profesional; no es una plataforma electrónica.                 |
| `RecursoSocial`              | Habilidad suave o recurso de empleabilidad al que el candidato accede mediante una conexión profesional.                             |
| `OfertaLaboral`              | Vacante publicada con ubicación, fechas, modalidad y condiciones disponibles.                                                        |
| `Empleador`                  | Organización o entidad que publica o gestiona una oferta.                                                                            |
| `RequerimientoDeCompetencia` | Requisito de competencia asociado a una oferta, con nivel esperado y obligatoriedad si se conoce.                                    |
| `TrayectoriaLaboral`         | Secuencia o agrupación de etapas profesionales de un candidato.                                                                      |
| `EtapaEnTrayectoriaLaboral`  | Etapa que ordena y contextualiza un periodo de la trayectoria.                                                                       |
| `Recomendacion`              | Resultado que sugiere una oferta, candidato, acción u oportunidad.                                                                   |
| `Explicacion`                | Justificación trazable de una recomendación, con evidencia y método cuando estén disponibles.                                        |

Las clases podrán organizarse en jerarquías cuando exista una distinción semántica útil y estable. No se deben crear subclases únicamente para reflejar valores de datos (por ejemplo, niveles o estados); esos valores se modelarán como propiedades o individuos controlados cuando corresponda.

## 7. Relaciones requeridas

La ontología debe contemplar relaciones con semántica y dominio/rango documentados. Como mínimo:

| Relación conceptual              | Dominio → rango | Significado                                             |
|----------------------------------|---|---------------------------------------------------------|
| tiene educación                  | Candidato → Educacion | Formación registrada para el candidato.                 |
| tiene experiencia laboral        | Candidato → ExperienciaLaboral | Experiencia asociada al perfil.                         |
| posee competencia                | Candidato → Competencia | Competencia atribuida o declarada por el candidato.     |
| tiene habilidad                  | Candidato → Habilidad | Habilidad registrada en el perfil.                      |
| tiene nivel de lenguaje          | Candidato → Lenguaje | Lenguaje y nivel asociados al candidato.                |
| tiene ranking                    | Candidato o perfil → RankingDeCompetencia | Evaluación contextualizada de una competencia.          |
| evalúa competencia               | RankingDeCompetencia → Competencia | Competencia a la que corresponde la evaluación.         |
| tiene factor                     | PerfilSocioemocional → FactorSocioemocional | Factor incluido en el perfil.                           |
| tiene perfil socioemocional      | Candidato → PerfilSocioemocional | Asociación cuyo uso requiere controles de privacidad.   |
| conoce a red social              | Candidato → RedSocial | Persona o grupo de contacto profesional conocido por el candidato. |
| facilita recurso social          | RedSocial → RecursoSocial | Recurso o habilidad suave al que se accede mediante la conexión. |
| publicado por                    | OfertaLaboral → Empleador | Organización asociada a la publicación.                 |
| requiere competencia             | OfertaLaboral → RequerimientoDeCompetencia | Requisitos declarados para la oferta.                   |
| especifica competencia           | RequerimientoDeCompetencia → Competencia | Competencia requerida.                                  |
| tiene trayectoria                | Candidato → TrayectoriaLaboral | Trayectoria atribuida al candidato.                     |
| contiene etapa                   | TrayectoriaLaboral → EtapaEnTrayectoriaLaboral | Etapa que forma parte de la trayectoria.                |
| describe experiencia o educación | EtapaEnTrayectoriaLaboral → ExperienciaLaboral o Educacion | Elemento de perfil contextualizado por la etapa.        |
| recomienda                       | Recomendacion → OfertaLaboral, Candidato o acción definida | Destino o sujeto de la recomendación.                   |
| tiene explicación                | Recomendacion → Explicacion | Justificación asociada al resultado.                    |
| utiliza evidencia                | Explicacion → recursos o entidades fuente | Elementos que respaldan la explicación.                 |

La implementación deberá escoger nombres de propiedades únicos y consistentes. Si una relación tiene varios tipos posibles de destino, estos deben definirse explícitamente y no dejarse ambiguos por conveniencia de implementación.

## 8. Propiedades de datos y metadatos

Las entidades pertinentes deben poder incluir, cuando se conozca:

- Identificador estable y etiqueta legible.
- Descripción y fuente de la información.
- Fechas de creación, vigencia, inicio o fin, según el concepto.
- Estado, modalidad, ubicación o idioma, cuando aplique.
- Nivel, puntuación, escala, método y fecha de evaluación para rankings y requerimientos.
- Evidencia o referencia de origen para afirmaciones de perfil y explicaciones.

Los valores de puntuación deben estar normalizados a los valores 1, 2, 3, 4, 5, en donde 1 significa 'muy poca habilidad' y 5 significa 'experto'; no se deben comparar puntuaciones de escalas distintas sin una transformación explícita. Los periodos deben permitir representar fechas parciales o desconocidas sin inventar precisión. Los identificadores personales no deben ser nombres o correos expuestos como identificadores RDF por defecto.

## 9. Reglas semánticas y validación

- La ontología debe utilizar OWL para declarar clases, propiedades, jerarquías y axiomas que tengan significado de dominio.
- OWL opera bajo mundo abierto: la ausencia de un dato no significa que sea falso ni que una persona carezca de una competencia.
- No declarar propiedades como funcionales, disjuntas o cardinalmente obligatorias salvo que la regla sea válida para todos los datos del dominio y esté justificada.
- Las restricciones de calidad de datos (por ejemplo, campos requeridos para una oferta activa, rangos de puntuación o fechas coherentes) deben especificarse como requisitos de validación; podrán implementarse con SHACL en una fase posterior o junto con la ontología.
- Separar afirmaciones declaradas, observadas, evaluadas e inferidas mediante procedencia, método o contexto.
- Las equivalencias o inferencias sobre competencias y habilidades solo se incorporarán cuando exista una taxonomía o regla acordada; no deben suponerse equivalencias por similitud de etiquetas.

## 10. Formatos e integración con Apache Jena

- La ontología debe tener un IRI base versionable y prefijos documentados.
- Debe existir una representación RDF legible para mantenimiento ( Turtle, `.ttl`) y una serialización RDF/XML interoperable (`.rdf` y `.owl`).
- Ambas serializaciones deben representar el mismo vocabulario y axiomas esenciales.
- El proyecto debe poder cargar la ontología en un modelo Jena, consultar sus clases y propiedades y ejecutar consultas SPARQL sobre un grafo de ejemplo.
- Debe ser posible elegir el formato de lectura a partir de la serialización sin depender de nombres de archivo ambiguos.
- La documentación debe registrar la versión de la ontología y el procedimiento para mantener sincronizadas las serializaciones.

## 11. Privacidad, equidad y uso responsable

Los perfiles socioemocionales y datos de candidatos pueden contener información sensible o contextual. Su representación y consumo deben ser opcionales, con fuente, propósito y fecha de obtención identificables, y sujetos al consentimiento, base legítima y controles de acceso que correspondan. No deben tratarse como hechos permanentes sobre una persona.

Las recomendaciones deben ser explicables y no deben utilizar atributos protegidos ni inferirlos. La ontología describe datos y relaciones; no determina por sí sola la idoneidad laboral de una persona ni elimina la necesidad de revisión y gobernanza del sistema.

## 12. Requisitos no funcionales

- **Interoperabilidad:** RDF/OWL estándar, IRIs estables y etiquetas en español; considerar etiquetas alternativas para traducciones futuras.
- **Mantenibilidad:** nombres coherentes, documentación de cada clase y propiedad, y control de versiones.
- **Extensibilidad:** permitir añadir tipos de evidencia, escalas y fuentes sin romper el significado de datos existentes.
- **Consultabilidad:** las relaciones principales deben poder recorrerse mediante SPARQL en Jena.
- **Calidad:** las serializaciones deben parsearse sin errores y no contener referencias a entidades no definidas por error.
- **Trazabilidad:** conservar procedencia de evaluaciones, recursos y recomendaciones cuando el origen esté disponible.

## 13. Entregables

1. Documento de especificación del vocabulario: IRI, prefijos, clases, propiedades, dominios, rangos y axiomas.
2. Ontología RDF/OWL en Turtle y RDF/XML.
3. Datos de ejemplo sintéticos, sin información personal real, que cubran al menos 5 candidatos, 5 ofertas, requisitos y una recomendación explicada.
4. Consultas SPARQL de ejemplo para explorar perfiles, comparar requisitos y recuperar explicaciones.
5. Pruebas de carga en Apache Jena y validación de equivalencia básica entre las serializaciones.
6. Documentación de decisiones de modelado, escalas utilizadas y limitaciones conocidas.

## 14. Criterios de aceptación

- [ ] Todas las clases mínimas de la sección 6 están definidas y documentadas.
- [ ] Las relaciones mínimas de la sección 7 tienen identificadores, dominio, rango y descripción.
- [ ] Los rankings, niveles y requerimientos declaran su escala o método cuando contienen puntuaciones.
- [ ] Recomendaciones pueden vincularse con explicaciones y evidencia trazable.
- [ ] Los datos sensibles y de redes sociales se describen con salvaguardas y no son obligatorios para construir un perfil.
- [ ] La ontología está disponible en Turtle y RDF/XML y ambas representaciones se cargan correctamente con Apache Jena.
- [ ] Un grafo sintético demuestra los casos de uso principales mediante consultas SPARQL reproducibles.
- [ ] No se codifican inferencias de ausencia, equivalencias no aprobadas ni reglas discriminatorias.
- [ ] La documentación permite a otro desarrollador extender el vocabulario sin alterar el significado de conceptos existentes.

## 15. Decisiones seleccionadas para la fase de diseño

Se eligen opciones sencillas y ampliamente utilizadas como punto de partida. Las decisiones que dependen de políticas institucionales o fuentes autorizadas deberán confirmarse antes de cargar datos reales.

### 15.1 IRI base y versionado

- Usar durante el desarrollo el espacio de nombres local `https://localhost:3030/diablocode/grafosEmpleabilidad/ontology/empleabilidad#`. Este IRI depende del entorno local y no es publicable ni interoperable fuera de él; antes de una publicación deberá sustituirse por un IRI persistente bajo un dominio controlado y migrarse de forma coordinada.
- Mantener estable el IRI base de clases y propiedades. Identificar cada publicación con una versión semántica (`MAJOR.MINOR.PATCH`) registrada con `owl:versionInfo` y una etiqueta Git.
- Usar el prefijo `emp` para el espacio de nombres propio y documentar los prefijos estándar `rdf`, `rdfs`, `owl`, `xsd`, `skos`, `prov` y `schema` cuando se utilicen.
- Nombrar clases en `UpperCamelCase` y propiedades en `lowerCamelCase`; usar etiquetas en español (`rdfs:label` con idioma `es`) para los términos legibles.
- No publicar la ontología con un IRI local, temporal o basado en datos personales.

### 15.2 Escalas y fuentes autorizadas

- **Competencias:** usar inicialmente una escala ordinal propia de 1 a 5, documentando cada nivel. Cada evaluación debe registrar escala, fuente, método y fecha; no comparar valores de escalas distintas directamente.
- **Lenguajes:** usar el Marco Común Europeo de Referencia para las Lenguas (MCER), con niveles A1–C2, y permitir que el nivel no se especifique.
- **Factores socioemocionales:** no adoptar una escala ni instrumento por defecto. Representar estos datos solo cuando se defina un marco validado y autorizado, registrando instrumento, versión y contexto. No inferirlos de redes sociales u otros datos indirectos.

### 15.3 Vocabularios externos y alineación

- Reutilizar vocabularios conocidos cuando sus conceptos encajen: Schema.org para organizaciones y ofertas laborales, PROV-O para procedencia y SKOS para conceptos controlados como competencias.
- Mantener clases y propiedades propias para conceptos específicos de empleabilidad que no tengan una correspondencia adecuada.
- Documentar las correspondencias. No usar `owl:sameAs` para indicar una mera similitud; reservarlo para entidades que sean exactamente la misma.

### 15.4 Restricciones y validación

- Usar OWL para definir clases, propiedades y axiomas semánticos; usar SHACL para validar los datos.
- La validación inicial cubrirá tipos requeridos, escalas y rangos de puntuación, y coherencia de fechas.
- Los errores que impidan cumplir los criterios mínimos del caso de uso bloquearán la aceptación de los datos; las advertencias se documentarán y podrán tratarse sin bloquear la carga.

### 15.5 Identificadores, datos personales y acceso

- Usar UUID o identificadores opacos para candidatos; no incluir nombres, correos ni otros datos personales en los IRI.
- Minimizar los datos personales y separar los identificadores directos del grafo de análisis cuando sea posible.
- Tratar perfiles socioemocionales y datos sobre conexiones profesionales como opcionales y sensibles, conservando fuente y contexto y limitando su acceso. No incluir atributos protegidos como género, origen étnico o religión en el perfil socioemocional.
- Definir propósito, consentimiento o base legítima, retención y eliminación conforme a las políticas institucionales y la normativa aplicable antes de incorporar datos reales.
- Aplicar permisos en la aplicación o infraestructura; la ontología no reemplaza los controles de acceso.

### 15.6 Tipos de recomendación

- Usar tipos controlados de recomendación: oferta compatible, competencia por desarrollar, recurso de aprendizaje, mejora de perfil y siguiente acción sugerida.
- Cada recomendación debe incluir fecha, vigencia cuando aplique, método o versión del modelo, evidencia y explicación; representa una sugerencia, no una decisión definitiva de contratación.
