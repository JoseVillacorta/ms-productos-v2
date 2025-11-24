plugins {
    id("java")
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("jacoco")
    id("org.sonarqube") version "4.4.1.3373"
    id("org.openapi.generator") version "7.10.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val springCloudVersion = "2025.0.0"

dependencies {
    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    //SpringCloud
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")

    //Springboot
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    //Postgre
    implementation("org.postgresql:r2dbc-postgresql:1.0.5.RELEASE")

    //Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    //Kafka
    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp:1.35.0")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.6.0")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.21")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

tasks.named("jacocoTestReport", JacocoReport::class) {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        csv.required = false
        html.required = true
    }
}

sonarqube {
    properties {
        property("sonar.projectKey", "josevillacorta-msproductos")
        property("sonar.organization", "JoseVillacorta")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sources", listOf("src/main/java"))
        property("sonar.tests", listOf("src/test/java"))
    }
}

sourceSets {
    main {
        java {
            srcDir("$buildDir/generated/openapi/src/main/java")
        }
    }
}

tasks.named("compileJava") {
    dependsOn("openApiGenerate")
}

tasks.named("compileTestJava") {
    dependsOn("openApiGenerate")
}


openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$projectDir/src/main/resources/openapi/products-api.yaml")
    outputDir.set("$buildDir/generated/openapi")
    apiPackage.set("org.example.openapi.api")
    modelPackage.set("org.example.openapi.dto")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "useSpringBoot3" to "true",
            "useBeanValidation" to "true",
            "serializableModel" to "true",
            "sourceFolder" to "src/main/java"
        )
    )
    typeMappings.set(
        mapOf(
            "DateTime" to "java.time.LocalDateTime"
        )
    )
    importMappings.set(
        mapOf(
            "LocalDateTime" to "java.time.LocalDateTime"
        )
    )
    globalProperties.set(
        mapOf(
            "models" to "",
            "modelDocs" to "false",
            "modelTests" to "false"
        )
    )
}

