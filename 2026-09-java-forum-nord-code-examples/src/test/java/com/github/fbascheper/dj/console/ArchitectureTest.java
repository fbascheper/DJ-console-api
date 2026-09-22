package com.github.fbascheper.dj.console;

import com.github.fbascheper.dj.console.exception.DJConsoleException;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * ArchUnit architecture rules for the DJ Console backend.
 *
 * <p>Rules are grouped by concern: layering, injection style, exception hierarchy,
 * Jackson version discipline, domain-model conventions, Spring stereotype placement,
 * and package-level cycle freedom.
 *
 * <p>Uses the plain {@link ClassFileImporter} API (not {@code @ArchTest} / {@code @AnalyzeClasses})
 * to avoid JUnit Platform engine registration issues with JUnit Jupiter 6.
 */
class ArchitectureTest {

    private static final String ROOT = "com.github.fbascheper.dj.console";

    private static JavaClasses productionClasses;

    @BeforeAll
    static void importClasses() {
        productionClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(ROOT);
    }

    // -------------------------------------------------------------------------
    // Layering rules (Hexagonal / Ports-and-Adapters)
    // -------------------------------------------------------------------------

    /**
     * The domain is the innermost layer: it must not depend on any outer layer.
     * Pure Java (and Jackson annotations for JSONB polymorphism) are allowed.
     */
    @Test
    void domainMustNotDependOnOuterLayers() {
        noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..service..",
                        "..infrastructure..",
                        "..controller..",
                        "..graphql..",
                        "..rest..",
                        "..bootstrap..")
                .check(productionClasses);
    }

    /**
     * The service layer orchestrates domain logic.
     * It must not reach into the controller/GraphQL layer, the infrastructure layer,
     * or the legacy REST layer.
     */
    @Test
    void serviceMustNotDependOnControllerOrInfrastructureLayers() {
        noClasses().that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..controller..",
                        "..graphql..",
                        "..infrastructure..",
                        "..rest..")
                .check(productionClasses);
    }

    /**
     * The controller / GraphQL layer must not bypass the service layer by
     * reaching directly into the infrastructure layer.
     */
    @Test
    void controllerAndGraphqlMustNotDependOnInfrastructureLayer() {
        noClasses().that().resideInAnyPackage("..controller..", "..graphql..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                .check(productionClasses);
    }

    // -------------------------------------------------------------------------
    // Injection style
    // -------------------------------------------------------------------------

    /**
     * Constructor injection only — no field injection via {@code @Autowired}.
     */
    @Test
    void noFieldInjectionWithAutowired() {
        noFields()
                .should().beAnnotatedWith(Autowired.class)
                .check(productionClasses);
    }

    // -------------------------------------------------------------------------
    // Exception hierarchy
    // -------------------------------------------------------------------------

    /**
     * Every exception class in the {@code exception} package (other than {@link DJConsoleException}
     * itself and the {@code ErrorCode} enum) must be a subtype of {@link DJConsoleException}.
     */
    @Test
    void allExceptionsMustExtendDJConsoleException() {
        classes().that().resideInAPackage("..exception..")
                .and().areNotEnums()
                .and().doNotBelongToAnyOf(DJConsoleException.class)
                .should().beAssignableTo(DJConsoleException.class)
                .check(productionClasses);
    }

    // -------------------------------------------------------------------------
    // Jackson version discipline
    // -------------------------------------------------------------------------

    /**
     * Only Jackson 3 ({@code tools.jackson.*}) is permitted in production code.
     * {@code com.fasterxml.jackson.databind} and {@code com.fasterxml.jackson.core}
     * (the legacy Jackson 2 artifacts) must not appear.
     * <p>
     * {@code com.fasterxml.jackson.annotation} is intentionally excluded from this rule
     * because Jackson 3 retains that package name for backwards compatibility.
     */
    @Test
    void productionCodeMustNotUseLegacyJackson2CoreOrDatabind() {
        noClasses()
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.fasterxml.jackson.databind..",
                        "com.fasterxml.jackson.core..")
                .check(productionClasses);
    }

    // -------------------------------------------------------------------------
    // Domain model conventions
    // -------------------------------------------------------------------------

    /**
     * All concrete top-level domain types must be Java records.
     * Interfaces (including sealed interfaces), enums, annotations, and nested classes
     * (e.g. Lombok-generated {@code $Builder} inner classes) are excluded.
     */
    @Test
    void domainTypesMustBeRecords() {
        // domain/util/ holds stateless utility classes (e.g. EnergyTrendAnalyser) that are not
        // domain objects — only check the model subpackages (session, library, event)
        classes().that().resideInAnyPackage(
                        "..domain.session..",
                        "..domain.library..",
                        "..domain.event..")
                .and().areNotInterfaces()
                .and().areNotEnums()
                .and().areNotAnnotations()
                .and().areTopLevelClasses()
                .should().beRecords()
                .check(productionClasses);
    }

    // -------------------------------------------------------------------------
    // Spring stereotype placement
    // -------------------------------------------------------------------------

    /**
     * Classes annotated with {@code @Service} must live in the {@code service} package.
     */
    @Test
    void serviceAnnotatedClassesMustResideInServicePackage() {
        classes().that().areAnnotatedWith(Service.class)
                .should().resideInAPackage("..service..")
                .check(productionClasses);
    }

    /**
     * Classes annotated with {@code @Repository} must live in the {@code infrastructure} package.
     */
    @Test
    void repositoryAnnotatedClassesMustResideInInfrastructurePackage() {
        classes().that().areAnnotatedWith(Repository.class)
                .should().resideInAPackage("..infrastructure..")
                .check(productionClasses);
    }

    // -------------------------------------------------------------------------
    // Package cycles
    // -------------------------------------------------------------------------

    /**
     * No package-level cycles are permitted between first-level sub-packages of the root.
     */
    @Test
    void packagesMustBeFreeOfCycles() {
        slices().matching(ROOT + ".(*)..").should().beFreeOfCycles()
                .check(productionClasses);
    }
}
