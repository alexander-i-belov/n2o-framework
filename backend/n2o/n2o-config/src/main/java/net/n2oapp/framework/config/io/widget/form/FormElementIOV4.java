package net.n2oapp.framework.config.io.widget.form;

import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.SourceComponent;
import net.n2oapp.framework.api.metadata.control.Submit;
import net.n2oapp.framework.api.metadata.global.dao.N2oFormParam;
import net.n2oapp.framework.api.metadata.global.dao.N2oParam;
import net.n2oapp.framework.api.metadata.global.view.widget.FormMode;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import net.n2oapp.framework.config.io.control.ControlIOv2;
import net.n2oapp.framework.config.io.fieldset.FieldsetIOv4;
import net.n2oapp.framework.config.io.widget.WidgetElementIOv4;
import org.jdom2.Element;
import org.springframework.stereotype.Component;

/**
 * Чтение\запись виджета Форма
 */
@Component
public class FormElementIOV4 extends WidgetElementIOv4<N2oForm> {

    @Override
    public void io(Element e, N2oForm f, IOProcessor p) {
        super.io(e, f, p);
        p.attributeBoolean(e, "unsaved-data-prompt", f::getPrompt, f::setPrompt);
        p.attributeEnum(e, "mode", f::getMode, f::setMode, FormMode.class);
        p.attribute(e, "default-values-query-id", f::getDefaultValuesQueryId, f::setDefaultValuesQueryId);
        p.anyChildren(e, "fields", f::getItems, f::setItems, p.anyOf(SourceComponent.class), FieldsetIOv4.NAMESPACE, ControlIOv2.NAMESPACE);
        p.child(e, null, "submit", f::getSubmit, f::setSubmit, Submit.class, this::submit);
    }

    private void submit(Element e, Submit t, IOProcessor p) {
        p.attribute(e, "operation-id", t::getOperationId, t::setOperationId);
        p.attributeBoolean(e, "message-on-success", t::getMessageOnSuccess, t::setMessageOnSuccess);
        p.attributeBoolean(e, "message-on-fail", t::getMessageOnFail, t::setMessageOnFail);
        p.attribute(e, "route", t::getRoute, t::setRoute);
        p.children(e, null, "path-param", t::getPathParams, t::setPathParams, N2oParam.class, this::submitParam);
        p.children(e, null, "header-param", t::getHeaderParams, t::setHeaderParams, N2oParam.class, this::submitParam);
        p.children(e, null, "form-param", t::getFormParams, t::setFormParams, N2oFormParam.class, this::submitFormParam);
    }

    private void submitParam(Element e, N2oParam t, IOProcessor p) {
        p.attribute(e, "name", t::getName, t::setName);
        p.attribute(e, "value", t::getValue, t::setValue);
        p.attribute(e, "ref-widget-id", t::getRefWidgetId, t::setRefWidgetId);
        p.attributeEnum(e, "ref-model", t::getRefModel, t::setRefModel, ReduxModel.class);
    }

    private void submitFormParam(Element e, N2oFormParam t, IOProcessor p) {
        submitParam(e, t, p);
        if (t.getName() == null)
            p.attribute(e, "id", t::getId, t::setId);
    }

    @Override
    public String getElementName() {
        return "form";
    }

    @Override
    public Class<N2oForm> getElementClass() {
        return N2oForm.class;
    }
}
