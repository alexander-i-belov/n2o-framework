package net.n2oapp.framework.config.metadata.pack;

import net.n2oapp.framework.api.pack.MetadataPack;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.compile.header.SimpleHeaderValidator;
import net.n2oapp.framework.config.metadata.compile.menu.SimpleMenuValidator;
import net.n2oapp.framework.config.metadata.validation.standard.fieldset.FieldSetColumnValidator;
import net.n2oapp.framework.config.metadata.validation.standard.fieldset.FieldSetRowValidator;
import net.n2oapp.framework.config.metadata.validation.standard.fieldset.FieldSetValidator;
import net.n2oapp.framework.config.metadata.validation.standard.object.ObjectValidator;
import net.n2oapp.framework.config.metadata.validation.standard.page.PageValidator;
import net.n2oapp.framework.config.metadata.validation.standard.query.QueryValidator;
import net.n2oapp.framework.config.metadata.validation.standard.widget.FormValidator;
import net.n2oapp.framework.config.metadata.validation.standard.widget.ListFieldQueryValidation;
import net.n2oapp.framework.config.metadata.validation.standard.widget.TableValidator;
import net.n2oapp.framework.config.metadata.validation.standard.widget.WidgetValidator;
import org.springframework.core.env.PropertyResolver;

/**
 * Набор стандартных валидаторов метаданных
 */
public class N2oAllValidatorsPack implements MetadataPack<N2oApplicationBuilder> {
    @Override
    public void build(N2oApplicationBuilder b) {
        PropertyResolver prop = b.getEnvironment().getSystemProperties();
        b.validators(new ObjectValidator(), new QueryValidator(), new PageValidator(),
                new SimpleHeaderValidator(), new SimpleMenuValidator(), new WidgetValidator(),
                new ListFieldQueryValidation(), new FieldSetValidator(prop), new FieldSetColumnValidator(prop),
                new FieldSetRowValidator(prop), new FormValidator(prop), new TableValidator(prop));
    }
}
