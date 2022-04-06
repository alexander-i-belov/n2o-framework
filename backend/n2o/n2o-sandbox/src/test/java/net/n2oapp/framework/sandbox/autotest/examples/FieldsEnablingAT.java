package net.n2oapp.framework.sandbox.autotest.examples;

import com.codeborne.selenide.Condition;
import net.n2oapp.framework.autotest.api.component.control.InputText;
import net.n2oapp.framework.autotest.api.component.control.RadioGroup;
import net.n2oapp.framework.autotest.api.component.page.SimplePage;
import net.n2oapp.framework.autotest.api.component.widget.FormWidget;
import net.n2oapp.framework.sandbox.autotest.SandboxAutotestApplication;
import net.n2oapp.framework.sandbox.autotest.SandboxAutotestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"server.servlet.context-path=/", "n2o.sandbox.project-id=examples_fields_enabling"},
        classes = SandboxAutotestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FieldsEnablingAT extends SandboxAutotestBase {

    @BeforeAll
    public static void beforeClass() {
        configureSelenide();
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void fieldsEnablingTest() {
        SimplePage page = open(SimplePage.class);
        page.shouldExists();
        page.header().brandNameShouldBe("N2O");
        page.breadcrumb().titleShouldHaveText("Доступность полей");

        RadioGroup radioGroup = page.widget(FormWidget.class).fields().field("Пол")
                .control(RadioGroup.class);
        radioGroup.shouldExists();

        InputText inputText = page.widget(FormWidget.class).fields().field(Condition.matchText("^Девичья фамилия"))
                .control(InputText.class);
        inputText.shouldExists();

        radioGroup.shouldBeEmpty();
        inputText.shouldBeDisabled();

        radioGroup.check("Женский");
        radioGroup.shouldBeChecked("Женский");
        inputText.shouldBeEnabled();

        radioGroup.check("Мужской");
        radioGroup.shouldBeChecked("Мужской");
        inputText.shouldBeDisabled();
    }

}
