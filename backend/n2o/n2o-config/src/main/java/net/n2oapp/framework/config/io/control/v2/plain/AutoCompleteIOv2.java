package net.n2oapp.framework.config.io.control.v2.plain;

import net.n2oapp.framework.api.metadata.control.plain.N2oAutoComplete;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import net.n2oapp.framework.config.io.control.v2.list.ListFieldIOv2;
import org.jdom2.Element;
import org.springframework.stereotype.Component;

/**
 * Чтение/запись компонента ввода текста с автоподбором
 */
@Component
public class AutoCompleteIOv2 extends ListFieldIOv2<N2oAutoComplete> {

    @Override
    public void io(Element e, N2oAutoComplete m, IOProcessor p) {
        super.io(e, m, p);
        p.attributeBoolean(e, "tags", m::getTags, m::setTags);
        p.attributeInteger(e, "max-tag-text-length", m::getMaxTagTextLength, m::setMaxTagTextLength);
    }

    @Override
    public Class<N2oAutoComplete> getElementClass() {
        return N2oAutoComplete.class;
    }

    @Override
    public String getElementName() {
        return "auto-complete";
    }
}
