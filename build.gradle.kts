plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	kotlin("kapt")  version "1.9.25"
	id("org.springframework.boot") version "3.5.8"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.asciidoctor.jvm.convert") version "4.0.5"
}

group = "spring.webmvc"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")

configurations.all {
    exclude(group = "org.mockito")
}


dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mysql")
    testImplementation("io.mockk:mockk:1.13.16")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
	testImplementation("org.testcontainers:testcontainers")
	testImplementation("org.testcontainers:localstack")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
	annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")
	annotationProcessor("jakarta.annotation:jakarta.annotation-api")
	kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")

	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
	implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")

	implementation("software.amazon.awssdk:s3:2.31.25")
	implementation("software.amazon.awssdk:dynamodb-enhanced:2.31.25")

	implementation("com.linecorp.kotlin-jdsl:jpql-dsl:3.6.1")
	implementation("com.linecorp.kotlin-jdsl:jpql-render:3.6.1")
	implementation("com.linecorp.kotlin-jdsl:spring-data-jpa-support:3.6.1")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

noArg {
	annotation("software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
	inputs.dir(project.extra["snippetsDir"]!!)
	dependsOn(tasks.test)

	attributes(mapOf("snippets" to project.extra["snippetsDir"]!!))
}

tasks.register<Copy>("copyAsciidoc") {
	dependsOn("asciidoctor")
	from(file("build/docs/asciidoc/"))
	into(file("src/main/resources/static/docs"))
}

tasks.named("build") {
	dependsOn("copyAsciidoc")
}

tasks.bootBuildImage {
	val requiredProps = listOf("registry", "repositoryAlias", "repository", "imageTag")
	if (requiredProps.all { project.hasProperty(it) }) {
		val (registry, repositoryAlias, repository, imageTag) = requiredProps.map { project.property(it) }
		imageName.set("$registry/$repositoryAlias/$repository:$imageTag")
	}
}
