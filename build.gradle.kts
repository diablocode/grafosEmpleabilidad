plugins {
    id("java")
}

group = "mx.udg.cucea"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.jena:jena-core:5.1.0")
    implementation("org.apache.jena:jena-arq:5.1.0")
    implementation("org.apache.jena:jena-shacl:5.1.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("generateOntologyRdfXml") {
    group = "ontology"
    description = "Generates the RDF/XML ontology from its Turtle source."
    dependsOn(tasks.named("classes"))
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("mx.udg.cucea.ontology.OntologyRdfXmlGenerator")
    args(
        "src/main/resources/ontology/empleabilidad.ttl",
        "src/main/resources/ontology/empleabilidad.rdf",
    )
}