package net.n2oapp.framework.autotest.cells;

import net.n2oapp.framework.autotest.api.component.cell.*;
import net.n2oapp.framework.autotest.api.component.page.SimplePage;
import net.n2oapp.framework.autotest.api.component.widget.table.TableWidget;
import net.n2oapp.framework.autotest.run.AutoTestBase;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.*;
import net.n2oapp.framework.config.selective.CompileInfo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Автотест ячеек таблицы
 */
public class LinkCellAT extends AutoTestBase {

    private TableWidget.Rows rows;

    @BeforeClass
    public static void beforeClass() {
        configureSelenide();
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/cells/link/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/default.header.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/cells/testTable.query.xml"));

        SimplePage simplePage = open(SimplePage.class);
        simplePage.shouldExists();

        rows = simplePage.single().widget(TableWidget.class).columns().rows();
        rows.shouldHaveSize(4);
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oPagesPack(), new N2oHeaderPack(), new N2oWidgetsPack(),
                new N2oCellsPack(), new N2oAllDataPack());
    }

    @Test
    public void linkCellTest() {
        int col = 0;

        rows.row(0).cell(col, LinkCell.class).textShouldHave("test1");
        rows.row(0).cell(col, LinkCell.class).hrefShouldHave(getBaseUrl()+"/1/update");
        rows.row(0).cell(col, LinkCell.class).shouldHaveIcon("fa-link");

        rows.row(3).cell(col, LinkCell.class).textShouldHave("test4");
        rows.row(3).cell(col, LinkCell.class).hrefShouldHave(getBaseUrl()+"/4/update");
        rows.row(3).cell(col, LinkCell.class).shouldHaveIcon("fa-link");
    }

}
