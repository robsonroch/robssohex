package br.com.robson.robssohex.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(
        packages = {"br.com.robson.robssohex"}
)
public class ContractFirstTest {
    @ArchTest
    public static final ArchRule implementsValidator = ((GivenClassesConjunction)((GivenClassesConjunction)((GivenClassesConjunction)ArchRuleDefinition.classes().that().arePublic()).and().areAnnotatedWith(RestController.class)).or().areAnnotatedWith(Controller.class)).should().implement(new DescribedPredicate<JavaClass>("Deve implementar classe do pacote ..transportlayers.openapi.api..", new Object[0]) {
        public boolean apply(JavaClass input) {
            return input.getInterfaces().stream().map((imp) -> ((JavaClass)imp).getPackageName()).noneMatch((packageName) -> packageName.contains("transportlayers.openapi.api"));
        }
    });
}

