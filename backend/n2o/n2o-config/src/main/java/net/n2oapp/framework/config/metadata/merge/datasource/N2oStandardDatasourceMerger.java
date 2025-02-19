package net.n2oapp.framework.config.metadata.merge.datasource;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.global.view.page.datasource.N2oStandardDatasource;
import org.springframework.stereotype.Component;

/**
 * Слияние двух стандартных источников данных
 */
@Component
public class N2oStandardDatasourceMerger extends N2oDatasourceMerger<N2oStandardDatasource> {
    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oStandardDatasource.class;
    }

    @Override
    public N2oStandardDatasource merge(N2oStandardDatasource source, N2oStandardDatasource override) {
        super.merge(source, override);
        setIfNotNull(source::setDefaultValuesMode, override::getDefaultValuesMode);
        setIfNotNull(source::setQueryId, override::getQueryId);
        setIfNotNull(source::setObjectId, override::getObjectId);
        setIfNotNull(source::setSubmit, override::getSubmit);
        addIfNotNull(source, override, N2oStandardDatasource::setFilters, N2oStandardDatasource::getFilters);
        return source;
    }
}
