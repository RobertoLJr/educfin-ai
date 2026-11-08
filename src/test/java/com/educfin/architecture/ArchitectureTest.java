package com.educfin.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Garante em tempo de build que as regras de dependência da Clean Architecture
 * continuam sendo respeitadas à medida que o projeto cresce.
 */
@AnalyzeClasses(packages = "com.educfin", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domainIsPureJava = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "com.educfin.application..",
                    "com.educfin.infrastructure..",
                    "com.educfin.presentation..",
                    "org.springframework..",
                    "jakarta.."
            )
            .because("o domínio não deve depender de outras camadas nem de frameworks");

    @ArchTest
    static final ArchRule applicationDoesNotDependOnOuterLayersOrSpring = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "com.educfin.infrastructure..",
                    "com.educfin.presentation..",
                    "org.springframework..",
                    "jakarta.persistence.."
            )
            .because("a camada de aplicação conhece apenas o domínio e é livre de framework");

    @ArchTest
    static final ArchRule layeredDependencies = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..application..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            .layer("Presentation").definedBy("..presentation..")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Presentation")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure", "Presentation")
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .whereLayer("Presentation").mayNotBeAccessedByAnyLayer();

    @ArchTest
    static final ArchRule controllersDependOnInputPortsNotImplementations = noClasses()
            .that().resideInAPackage("..presentation.controller..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..application.usecase..",
                    "..application.port.out..",
                    "..infrastructure.."
            )
            .because("controllers devem depender apenas das portas de entrada (DIP/ISP)");

    @ArchTest
    static final ArchRule jpaEntitiesStayInInfrastructure = classes()
            .that().areAnnotatedWith("jakarta.persistence.Entity")
            .should().resideInAPackage("..infrastructure.persistence..");
}
