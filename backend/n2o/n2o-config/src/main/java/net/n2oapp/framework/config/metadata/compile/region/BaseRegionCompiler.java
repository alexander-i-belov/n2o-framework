package net.n2oapp.framework.config.metadata.compile.region;

import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.global.view.region.N2oRegion;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget;
import net.n2oapp.framework.api.metadata.meta.WidgetDependency;
import net.n2oapp.framework.api.metadata.meta.region.Region;
import net.n2oapp.framework.config.metadata.compile.BaseSourceCompiler;
import net.n2oapp.framework.config.metadata.compile.IndexScope;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.compile.page.PageScope;
import net.n2oapp.framework.config.metadata.compile.page.WidgetDependencyScope;

import java.util.ArrayList;
import java.util.List;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;

public abstract class BaseRegionCompiler<D extends Region, S extends N2oRegion> implements BaseSourceCompiler<D, S, PageContext> {

    protected abstract String getPropertyRegionSrc();

    protected D build(D compiled, S source, PageContext context, CompileProcessor p) {
        compiled.setSrc(p.cast(source.getSrc(), p.resolve(property(getPropertyRegionSrc()), String.class)));
        IndexScope index = p.getScope(IndexScope.class);
        compiled.setId(p.cast(source.getId(), source.getPlace() + (index != null ? index.get() : "")));
        compiled.setProperties(p.mapAttributes(source));
        return compiled;
    }

    protected <I extends Region.Item> List<I> initItems(N2oRegion source, CompileProcessor p, Class<I> itemClass) {
        List<I> items = new ArrayList<>();
        if (source.getWidgets() != null) {
            IndexScope index = new IndexScope(1);
            for (N2oWidget n2oWidget : source.getWidgets()) {
                Region.Item item = createItem(n2oWidget, index, p);
                PageScope pageScope = p.getScope(PageScope.class);
                if (pageScope != null) {
                    item.setWidgetId(pageScope.getPageId() + "_" + n2oWidget.getId());
                } else {
                    item.setWidgetId(n2oWidget.getId());
                }
                if (!itemClass.equals(item.getClass()))
                    throw new IllegalStateException();
                items.add((I) item);
            }
        }
        compileDependencies(items, p);
        return items;
    }

    protected abstract Region.Item createItem(N2oWidget widget, IndexScope index, CompileProcessor p);


    private <I extends Region.Item> void compileDependencies(List<I> items, CompileProcessor p) {
        if (items == null || p.getScope(WidgetDependencyScope.class) == null) return;

        WidgetDependencyScope scope = p.getScope(WidgetDependencyScope.class);
        for (Region.Item item : items) {
            if (scope.containsKey(item.getWidgetId())) {
                WidgetDependency dependency = new WidgetDependency();
                dependency.setVisible(scope.get(item.getWidgetId()).getVisible());
                item.setDependency(dependency);
            }
        }
    }
}
