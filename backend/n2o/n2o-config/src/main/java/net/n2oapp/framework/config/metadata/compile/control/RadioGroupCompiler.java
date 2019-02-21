package net.n2oapp.framework.config.metadata.compile.control;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.control.list.N2oRadioGroup;
import net.n2oapp.framework.api.metadata.meta.control.RadioGroup;
import net.n2oapp.framework.api.metadata.meta.control.StandardField;
import org.springframework.stereotype.Component;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;

@Component
public class RadioGroupCompiler extends ListControlCompiler<RadioGroup, N2oRadioGroup> {

    @Override
    protected String getControlSrcProperty() {
        return "n2o.api.control.radiogroup.src";
    }

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oRadioGroup.class;
    }

    @Override
    public StandardField<RadioGroup> compile(N2oRadioGroup source, CompileContext<?, ?> context, CompileProcessor p) {
        RadioGroup radioGroup = new RadioGroup();
        radioGroup.setInline(source.getInline());
        radioGroup.setType(p.cast(source.getType(), p.resolve(property("n2o.api.control.alt.type"), String.class)));
        radioGroup.setSize(p.cast(source.getSize(), p.resolve(property("n2o.api.control.list.size"), Integer.class)));
        return compileFetchDependencies(radioGroup, source, context, p);
    }
}
