extra["springCloudVersion"] = "2020.0.4"

plugins {
	java
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("jacoco")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

val excludePackages: List<String> by extra {
	listOf(
		"com/services/wallet/application/WalletApplication*",
		"com/services/wallet/application/web/config/**",
		"com/services/wallet/infra/**",
		"com/services/wallet/resources/repositories/config/**"
	)
}

fun ignorePackagesForReport(jacocoBase: JacocoReportBase) {
	jacocoBase.classDirectories.setFrom(
		files(sourceSets.main.get().output.asFileTree.matching {
			exclude(excludePackages)
		})
	)
}

tasks.jacocoTestReport {
	ignorePackagesForReport(this)
}


group = "com.services"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
	mavenLocal()
}

val mainPkgAndClass = "com.services.wallet.application.WalletApplication"

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

sourceSets {
	create("componentTest") {
		compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
		runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
	}

	create("archTest") {
		compileClasspath += sourceSets.main.get().output
		runtimeClasspath += sourceSets.main.get().output
	}
}

val componentTestImplementation: Configuration by configurations.getting {
	extendsFrom(configurations.implementation.get())
}

val archTestImplementation: Configuration by configurations.getting {
	extendsFrom(configurations.implementation.get())
}

dependencies {

	// Spring Web
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-logging")

	// Security
	implementation("org.springframework.boot:spring-boot-starter-security")

	// Docs
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")

	// Validation
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
	implementation("jakarta.validation:jakarta.validation-api:3.0.2")
	implementation("org.glassfish:jakarta.el:4.0.2")
	implementation("br.com.caelum.stella:caelum-stella-core:2.1.4")

	// Jackson
	implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")

	// DataBase
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.hibernate:hibernate-core:6.6.12.Final")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")

	// Metrics
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")

	// Lombok
	implementation("org.projectlombok:lombok:1.18.36")
	annotationProcessor("org.projectlombok:lombok:1.18.36")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation("org.junit.jupiter:junit-jupiter-params")
	testImplementation("org.junit.jupiter:junit-jupiter-api")

	// Unit Tests Utils
	testImplementation("org.mockito:mockito-core")
	testImplementation("org.assertj:assertj-core:3.20.2")

	// Arch Test
	archTestImplementation("org.junit.jupiter:junit-jupiter-api")
	archTestImplementation("org.junit.jupiter:junit-jupiter-engine")
	archTestImplementation("com.tngtech.archunit:archunit-junit5:1.4.0")
	archTestImplementation("com.tngtech.archunit:archunit-junit5-engine:1.4.0")

	// HTTP Integration Test
	componentTestImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	componentTestImplementation("io.rest-assured:rest-assured:5.5.0")
	componentTestImplementation("io.rest-assured:json-path:5.5.0")
	componentTestImplementation("io.rest-assured:xml-path:5.5.0")

	componentTestImplementation("ch.qos.logback:logback-classic:1.4.14")
	componentTestImplementation("org.slf4j:slf4j-api:2.0.9")

	// Database Integration Test
	componentTestImplementation("io.zonky.test:embedded-postgres:2.1.0")
}

tasks.withType<Test> {
	loadEnv(environment, file("test.env"))
	useJUnitPlatform()
}

val archTest = tasks.register("archTest", Test::class) {
	description = "Runs the architecture tests"
	group = "verification"

	testClassesDirs = sourceSets["archTest"].output.classesDirs
	classpath = sourceSets["archTest"].runtimeClasspath

	doFirst {
		systemProperty("archRule.failOnEmptyShould", "false")
	}
}

configurations["componentTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())
configurations["archTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())


java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<CreateStartScripts> { mainPkgAndClass }

tasks.jar {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	manifest {
		attributes("Main-Class" to mainPkgAndClass)
		attributes("Package-Version" to archiveVersion)
	}
	from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
	from(sourceSets.main.get().output)
}

tasks.withType<JavaExec> {
	loadEnv(environment, file("local.env"))
}

val componentTestTask = tasks.create("componentTest", Test::class) {
	description = "Runs the component tests."
	group = "verification"
	testClassesDirs = sourceSets["componentTest"].output.classesDirs
	classpath = sourceSets["componentTest"].runtimeClasspath
	useJUnitPlatform()
}

fun loadEnv(environment: MutableMap<String, Any>, file: File) {
	if (!file.exists()) throw IllegalArgumentException("failed to load envs from file, ${file.name} not found")
	file.readLines().forEach { line ->
		if (line.isBlank() || line.startsWith("#")) return@forEach
		line.split("=", limit = 2)
			.takeIf { it.size == 2 &&it[0].isNotBlank() }
			?.run { Pair(this[0].trim(), this[1].trim()) }
			?.run {
				environment[this.first] = this.second
			}
	}
}

tasks.withType<JacocoReport> {
	reports {
		xml
		html
	}
}

tasks.withType<JacocoCoverageVerification> {
	violationRules {
		rule {
			limit {
				minimum = "0.8".toBigDecimal()
				counter = "LINE"
			}
			limit {
				minimum = "0.8".toBigDecimal()
				counter = "BRANCH"
			}
		}
	}
	ignorePackagesForReport(this)
}

tasks.test {
	finalizedBy("jacocoTestReport", "jacocoTestCoverageVerification", "archTest")
}
