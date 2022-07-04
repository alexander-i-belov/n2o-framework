package net.n2oapp.framework.config.metadata.compile.datasource;

import net.n2oapp.framework.api.data.validation.Validation;
import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.datasource.AbstractDatasource;
import net.n2oapp.framework.api.metadata.global.view.page.N2oDatasource;
import net.n2oapp.framework.api.metadata.global.view.page.N2oStandardDatasource;
import net.n2oapp.framework.api.metadata.meta.CopyDependency;
import net.n2oapp.framework.api.metadata.meta.Dependency;
import net.n2oapp.framework.api.metadata.meta.DependencyType;
import net.n2oapp.framework.api.metadata.meta.ModelLink;
import net.n2oapp.framework.config.metadata.compile.ValidationList;
import net.n2oapp.framework.config.metadata.compile.page.PageScope;
import net.n2oapp.framework.config.util.CompileUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Базовая компиляция источника данных
 */
public abstract class BaseDatasourceCompiler<S extends N2oDatasource, D extends AbstractDatasource> extends AbstractDatasourceCompiler<S, D> {

    protected Map<String, List<Validation>> initValidations(N2oDatasource source, CompileProcessor p) {
        ValidationList validationList = p.getScope(ValidationList.class);
        if (validationList != null) {
            //todo why RESOLVE ?
            return validationList.get(source.getId(), ReduxModel.resolve).stream()
                    .filter(v -> v.getSide() == null || v.getSide().contains("client"))
                    .collect(Collectors.groupingBy(Validation::getFieldId));
        } else
            return Collections.emptyMap();
    }

    protected List<Dependency> initDependencies(N2oDatasource source, CompileProcessor p) {
        PageScope pageScope = p.getScope(PageScope.class);
        List<Dependency> dependencies = new ArrayList<>();
        String pageId = pageScope != null ? pageScope.getPageId() : "";
        if (source.getDependencies() != null) {
            for (N2oStandardDatasource.Dependency d : source.getDependencies()) {
                if (d instanceof N2oStandardDatasource.FetchDependency) {
                    N2oStandardDatasource.FetchDependency dependency = (N2oStandardDatasource.FetchDependency) d;
                    Dependency fetchDependency = new Dependency();
                    ModelLink link = new ModelLink(p.cast(dependency.getModel(), ReduxModel.resolve),
                            CompileUtil.generateWidgetId(pageId, dependency.getOn()));
                    fetchDependency.setOn(link.getBindLink());
                    fetchDependency.setType(DependencyType.fetch);
                    dependencies.add(fetchDependency);
                } else if (d instanceof N2oStandardDatasource.CopyDependency) {
                    N2oStandardDatasource.CopyDependency dependency = (N2oStandardDatasource.CopyDependency) d;
                    CopyDependency copyDependency = new CopyDependency();
                    ModelLink link = new ModelLink(p.cast(dependency.getSourceModel(), ReduxModel.resolve),
                            CompileUtil.generateDatasourceId(pageId, dependency.getOn()), dependency.getSourceFieldId());
                    copyDependency.setOn(link.getBindLink());
                    copyDependency.setModel(p.cast(dependency.getTargetModel(), ReduxModel.resolve));
                    copyDependency.setField(dependency.getTargetFieldId());
                    copyDependency.setType(DependencyType.copy);
                    dependencies.add(copyDependency);
                }
            }
        }
        return dependencies;
    }
}
