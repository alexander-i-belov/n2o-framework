package net.n2oapp.framework.config.io.widget.v4.toolbar;

import net.n2oapp.framework.config.io.action.CopyActionElementIOV1;
import net.n2oapp.framework.config.io.toolbar.SubmenuIO;
import net.n2oapp.framework.config.io.widget.v4.TableElementIOV4;
import net.n2oapp.framework.config.selective.ION2oMetadataTester;
import org.junit.Test;

/**
 * Тестирование чтения/записи кнопки с выпадающим меню
 */
public class SubMenuXmlIOTest {
    @Test
    public void testButton() {
        ION2oMetadataTester tester = new ION2oMetadataTester()
                .ios(new TableElementIOV4(), new SubmenuIO(), new CopyActionElementIOV1());
        assert tester.check("net/n2oapp/framework/config/io/widget/toolbar/testMenuItemIO.widget.xml");
    }
}