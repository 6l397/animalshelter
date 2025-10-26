package chnu.edu.pz.animalshelter.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Repository;

public class AnimalShelterArchTests {

    private final JavaClasses classes =
            new ClassFileImporter().importPackages("chnu.edu.pz.animalshelter");

    // 1. Controllers should be in controller package
    @Test
    void controllersShouldResideInControllerPackage() {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("..controller..")
                .check(classes);
    }

    // 2. Services should be in service package
    @Test
    void servicesShouldResideInServicePackage() {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Service")
                .should().resideInAPackage("..service..")
                .check(classes);
    }

    // 3. Repositories should be in repository package
    @Test
    void repositoriesShouldResideInRepositoryPackage() {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Repository")
                .should().resideInAPackage("..repository..")
                .check(classes);
    }

    // 4. Models should be in model package
    @Test
    void modelsShouldResideInModelPackage() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..model..")
                .should().haveSimpleNameNotEndingWith("Controller")
                .andShould().haveSimpleNameNotEndingWith("Service")
                .check(classes);
    }

    // 5. Controllers should not access repositories directly
    @Test
    void controllersShouldNotAccessRepositoriesDirectly() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..controller..")
                .should().accessClassesThat().resideInAPackage("..repository..")
                .check(classes);
    }

    // 6. Services should only be accessed by controllers or services
    @Test
    void servicesShouldOnlyBeAccessedByControllersOrServices() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..service..")
                .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..")
                .check(classes);
    }

    // 7. Repositories should only be accessed by services
    @Test
    void repositoriesShouldOnlyBeAccessedByServices() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..repository..")
                .should().onlyBeAccessed().byAnyPackage("..service..")
                .check(classes);
    }

    // 8. Controllers should be annotated with @RestController
    @Test
    void controllersShouldBeAnnotatedWithRestController() {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Controller")
                .should().beAnnotatedWith(RestController.class)
                .check(classes);
    }

    // 9. Services should be annotated with @Service
    @Test
    void servicesShouldBeAnnotatedWithService() {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Service")
                .should().beAnnotatedWith(Service.class)
                .check(classes);
    }

    // 10. Repositories should be annotated with @Repository
    @Test
    void repositoriesShouldBeAnnotatedWithRepository() {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Repository")
                .should().beAnnotatedWith(Repository.class)
                .check(classes);
    }

    // 11. Controllers should not depend on model package directly
    @Test
    void controllersShouldNotDependOnModelPackage() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..controller..")
                .should().accessClassesThat().resideInAPackage("..model..")
                .check(classes);
    }

    // 12. Services should not depend on controller package
    @Test
    void servicesShouldNotDependOnControllerPackage() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..service..")
                .should().accessClassesThat().resideInAPackage("..controller..")
                .check(classes);
    }

    // 13. Repositories should not depend on controller or service packages
    @Test
    void repositoriesShouldNotDependOnControllerOrService() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..repository..")
                .should().accessClassesThat()
                .resideInAnyPackage("..controller..", "..service..")
                .check(classes);
    }

    // 14. Class names for controllers must end with 'Controller'
    @Test
    void controllersShouldHaveProperNaming() {
        ArchRuleDefinition.classes()
                .that().areAnnotatedWith(RestController.class)
                .should().haveSimpleNameEndingWith("Controller")
                .check(classes);
    }

    // 15. Class names for services must end with 'Service'
    @Test
    void servicesShouldHaveProperNaming() {
        ArchRuleDefinition.classes()
                .that().areAnnotatedWith(Service.class)
                .should().haveSimpleNameEndingWith("Service")
                .check(classes);
    }

    // 16. Class names for repositories must end with 'Repository'
    @Test
    void repositoriesShouldHaveProperNaming() {
        ArchRuleDefinition.classes()
                .that().areAnnotatedWith(Repository.class)
                .should().haveSimpleNameEndingWith("Repository")
                .check(classes);
    }

    // 17. Repository classes should be interfaces
    @Test
    void repositoryClassesShouldBeInterfaces() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..repository..")
                .should().beInterfaces()
                .check(classes);
    }

    // 18. No classes should use javax.* (old packages)
    @Test
    void noClassesShouldUseOldJavaxPackages() {
        ArchRuleDefinition.noClasses()
                .should().accessClassesThat().resideInAPackage("javax..")
                .check(classes);
    }

    // 19. No classes should use java.awt (irrelevant to backend)
    @Test
    void noClassesShouldUseJavaAwt() {
        ArchRuleDefinition.noClasses()
                .should().accessClassesThat().resideInAPackage("java.awt..")
                .check(classes);
    }

    // 20. Main application should reside in root package
    @Test
    void mainApplicationShouldResideInRootPackage() {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Application")
                .should().resideInAPackage("chnu.edu.pz.animalshelter")
                .check(classes);
    }
}
