package net.n2oapp.framework.autotest.widget;

import net.n2oapp.framework.autotest.api.component.button.StandardButton;
import net.n2oapp.framework.autotest.api.component.cell.CheckboxCell;
import net.n2oapp.framework.autotest.api.component.cell.RadioCell;
import net.n2oapp.framework.autotest.api.component.modal.Modal;
import net.n2oapp.framework.autotest.api.component.page.StandardPage;
import net.n2oapp.framework.autotest.api.component.widget.table.TableWidget;
import net.n2oapp.framework.autotest.run.AutoTestBase;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.N2oAllDataPack;
import net.n2oapp.framework.config.metadata.pack.N2oAllPagesPack;
import net.n2oapp.framework.config.metadata.pack.N2oHeaderPack;
import net.n2oapp.framework.config.selective.CompileInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Автотест способов выбора записей таблицы
 */
public class TableSelectionAT extends AutoTestBase {

    private StandardPage page;

    @BeforeAll
    public static void beforeClass() {
        configureSelenide();
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/widget/table/selection/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/widget/table/selection/table.widget.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/widget/table/selection/modal.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/widget/table/selection/test.query.xml"));
        page = open(StandardPage.class);
        page.shouldExists();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oHeaderPack(), new N2oAllPagesPack(), new N2oAllDataPack());
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/blank.header.xml"));
    }

    @Test
    public void testActiveSelection() {
        TableWidget table = page.widgets().widget(0, TableWidget.class);
        table.shouldExists();
        table.columns().headers().shouldHaveSize(2);

        TableWidget.Rows rows = table.columns().rows();
        rows.shouldHaveSize(3);
        rows.shouldBeSelected(0);
        rows.row(1).click();
        rows.shouldBeSelected(1);

        StandardButton button = table.toolbar().topLeft().button("Открыть");
        button.shouldBeEnabled();
        button.click();

        Modal modal = page.modal("Карточка клиента: 2");
        modal.close();
    }

    @Test
    public void testNoneSelection() {
        TableWidget table = page.widgets().widget(1, TableWidget.class);
        table.shouldExists();
        table.columns().headers().shouldHaveSize(2);

        TableWidget.Rows rows = table.columns().rows();
        rows.shouldHaveSize(3);
        rows.shouldNotHaveSelectedRows();
        rows.row(1).click();
        rows.shouldNotHaveSelectedRows();

        table.toolbar().topLeft().button("Открыть").shouldBeDisabled();
    }

    @Test
    public void testRadioSelection() {
        TableWidget table = page.widgets().widget(2, TableWidget.class);
        table.shouldExists();
        table.columns().headers().shouldHaveSize(3);

        TableWidget.Rows rows = table.columns().rows();
        rows.shouldHaveSize(3);
        rows.shouldBeSelected(0);

        RadioCell cell1 = rows.row(0).cell(0, RadioCell.class);
        RadioCell cell2 = rows.row(1).cell(0, RadioCell.class);
        RadioCell cell3 = rows.row(2).cell(0, RadioCell.class);
        cell1.click();
        cell1.shouldBeChecked();
        cell2.shouldBeUnchecked();
        cell3.shouldBeUnchecked();
        cell2.click();
        cell2.shouldBeChecked();
        cell3.click();
        cell3.shouldBeChecked();
        cell1.shouldBeUnchecked();
        cell2.shouldBeUnchecked();

        rows.row(1).click();
        rows.shouldBeSelected(1);

        StandardButton button = table.toolbar().topLeft().button("Открыть");
        button.shouldBeEnabled();
        button.click();

        Modal modal = page.modal("Карточка клиента: 2");
        modal.close();
    }

    @Test
    public void testCheckboxSelection() {
        TableWidget table = page.widgets().widget(3, TableWidget.class);
        table.shouldExists();
        table.columns().headers().shouldHaveSize(3);

        TableWidget.Rows rows = table.columns().rows();
        rows.shouldHaveSize(3);
        rows.shouldBeSelected(0);
        rows.row(1).click();
        rows.shouldBeSelected(1);

        CheckboxCell cell1 = rows.row(0).cell(0, CheckboxCell.class);
        CheckboxCell cell2 = rows.row(1).cell(0, CheckboxCell.class);
        CheckboxCell cell3 = rows.row(2).cell(0, CheckboxCell.class);
        cell1.shouldBeUnchecked();
        cell2.shouldBeUnchecked();
        cell3.shouldBeUnchecked();
        // выбор нескольких строк
        cell2.setChecked(true);
        cell2.shouldBeChecked();
        cell1.setChecked(true);
        cell1.shouldBeChecked();
        cell2.setChecked(false);
        cell2.shouldBeUnchecked();
        // выбор всех строк
        table.columns().headers().header(0).click();
        cell1.shouldBeChecked();
        cell2.shouldBeChecked();
        cell3.shouldBeChecked();
        // отмена выбора всех строк
        table.columns().headers().header(0).click();
        cell1.shouldBeUnchecked();
        cell2.shouldBeUnchecked();
        cell3.shouldBeUnchecked();

        StandardButton button = table.toolbar().topLeft().button("Открыть");
        button.shouldBeEnabled();
        button.click();

        Modal modal = page.modal("Карточка клиента: 2");
        modal.close();
    }
}
