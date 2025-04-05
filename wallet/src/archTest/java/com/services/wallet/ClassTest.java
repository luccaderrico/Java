package com.services.wallet;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

public class ClassTest {

    private final JavaClasses importedClassesApplication = new ClassFileImporter().importPackages("com.services.wallet.application");
    private final JavaClasses importedClassesDomain = new ClassFileImporter().importPackages("com.services.wallet.domain");
    private final JavaClasses importedClassesResources = new ClassFileImporter().importPackages("com.services.wallet.resources");


    @Test
    void classesThatEndWithControllerShouldBeInControllersPackageInsideWeb() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that().haveNameMatching(".*Controller")
                .should().resideInAPackage("..web.controllers..");
        rule.check(importedClassesApplication);
    }

    @Test
    void classesThatEndWithServiceShouldBeInServicePackage() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that().haveNameMatching(".*Service")
                .should().resideInAPackage("..services..");
        rule.check(importedClassesDomain);
    }

    @Test
    void classesThatEndWithExceptionShouldBeInExceptionPackage() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that().haveNameMatching(".*Exception")
                .should().resideInAPackage("..exceptions..");
        rule.check(importedClassesDomain);
        rule.check(importedClassesResources);
    }

    @Test
    void classesThatEndWithRepositoryShouldBeInRepositoriesPackage() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that().haveNameMatching(".*RepositoryGatewayImpl")
                .should().resideInAPackage("..repositories..");
        rule.check(importedClassesResources);
    }
}
