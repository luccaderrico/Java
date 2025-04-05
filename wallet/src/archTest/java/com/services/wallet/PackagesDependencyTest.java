package com.services.wallet;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class PackagesDependencyTest {

    private final JavaClasses importedClassRoot = new ClassFileImporter().importPackages("com.services.wallet");

    @Test
    public void packageApplicationShouldNotDependOnClassesFromResourcesPackage() {
        noClasses()
                .that()
                .resideInAPackage("..application..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..resources..")
                .check(importedClassRoot);
    }

    @Test
    public void packageResourcesShouldNotDependOnClassesFromApplicationPackage() {
        noClasses()
                .that()
                .resideInAPackage("..resources..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..application..")
                .check(importedClassRoot);
    }

    @Test
    public void packageDomainShouldNotDependOnClassesFromApplicationOrResourcesPackage() {
        noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("..application..", "..resources..")
                .check(importedClassRoot);
    }

//    @Test
//    public void layersOfTheApplicationShouldBeRespected() {
//        layeredArchitecture()
//                .layer("application").definedBy("..application..")
//                .layer("domain").definedBy("..domain..")
//                .layer("resources").definedBy("..resources..")
//                .whereLayer("application").mayNotBeAccessedByAnyLayer()
//                .whereLayer("domain").mayOnlyBeAccessedByLayers("application", "resources")
//                .whereLayer("resources").mayOnlyAccessLayers("domain")
//                .check(importedClassRoot);
//    }
}
