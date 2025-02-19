package net.n2oapp.framework.config.metadata.compile.action;

import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.dataprovider.N2oClientDataProvider;
import net.n2oapp.framework.api.metadata.event.action.N2oInvokeAction;
import net.n2oapp.framework.api.metadata.global.dao.N2oParam;
import net.n2oapp.framework.api.metadata.global.view.action.control.Target;
import net.n2oapp.framework.api.metadata.global.view.page.datasource.N2oStandardDatasource;
import net.n2oapp.framework.api.metadata.local.CompiledObject;
import net.n2oapp.framework.api.metadata.local.CompiledQuery;
import net.n2oapp.framework.api.metadata.meta.ClientDataProvider;
import net.n2oapp.framework.api.metadata.meta.action.invoke.InvokeAction;
import net.n2oapp.framework.api.metadata.meta.action.invoke.InvokeActionPayload;
import net.n2oapp.framework.api.metadata.meta.saga.AsyncMetaSaga;
import net.n2oapp.framework.api.metadata.meta.saga.MetaSaga;
import net.n2oapp.framework.api.metadata.meta.saga.RedirectSaga;
import net.n2oapp.framework.api.metadata.meta.saga.RefreshSaga;
import net.n2oapp.framework.api.metadata.meta.widget.RequestMethod;
import net.n2oapp.framework.config.metadata.compile.ParentRouteScope;
import net.n2oapp.framework.config.metadata.compile.context.*;
import net.n2oapp.framework.config.metadata.compile.dataprovider.ClientDataProviderUtil;
import net.n2oapp.framework.config.metadata.compile.datasource.DataSourcesScope;
import net.n2oapp.framework.config.metadata.compile.page.PageScope;
import net.n2oapp.framework.config.register.route.RouteUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;
import static net.n2oapp.framework.config.metadata.compile.dataprovider.ClientDataProviderUtil.getClientWidgetIdByComponentScope;
import static net.n2oapp.framework.config.register.route.RouteUtil.absolute;
import static net.n2oapp.framework.config.util.DatasourceUtil.getClientDatasourceId;
import static net.n2oapp.framework.config.util.DatasourceUtil.getClientDatasourceIds;

/**
 * Сборка действия вызова операции
 */
@Component
public class InvokeActionCompiler extends AbstractActionCompiler<InvokeAction, N2oInvokeAction> {
    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oInvokeAction.class;
    }

    @Override
    public InvokeAction compile(N2oInvokeAction source, CompileContext<?, ?> context, CompileProcessor p) {
        initDefaults(source, context, p);
        InvokeAction invokeAction = new InvokeAction();
        compileAction(invokeAction, source, p);
        invokeAction.setOperationId(source.getOperationId());
        invokeAction.setType(getType(p));

        invokeAction.getPayload().setModel(getModelFromComponentScope(p));
        invokeAction.getPayload().setDatasource(getClientDatasourceId(getLocalDatasourceId(p), p));
        invokeAction.getPayload().setWidgetId(getClientWidgetIdByComponentScope(p));
        invokeAction.getPayload().setPageId(getPageId(p));

        invokeAction.getMeta().setSuccess(initSuccessMeta(invokeAction, source, context, p));
        invokeAction.getMeta().setFail(initFailMeta(invokeAction, source, context, p));
        initDataProvider(invokeAction, source, context, p);
        return invokeAction;
    }

    private String getType(CompileProcessor p) {
        return p.resolve(property("n2o.api.action.invoke.type"), String.class);
    }

    protected void initDefaults(N2oInvokeAction source, CompileContext<?, ?> context, CompileProcessor p) {
        super.initDefaults(source, context, p);
        source.setDoubleCloseOnSuccess(p.cast(source.getDoubleCloseOnSuccess(), false));
        source.setCloseOnSuccess(source.getDoubleCloseOnSuccess() || p.cast(source.getCloseOnSuccess(), false));
        source.setCloseOnFail(p.cast(source.getCloseOnFail(), false));
        source.setRefreshOnSuccess(p.cast(source.getRefreshOnSuccess(), true));
        source.setRefreshDatasourceIds(initRefreshDatasources(source, p));
        source.setRoute(p.cast(source.getRoute(), "/" + source.getId()));
        initSubmitMessageDefaults(source, p, context);
        source.setOptimistic(p.cast(source.getOptimistic(), p.resolve(property("n2o.api.action.invoke.optimistic"), Boolean.class)));
        source.setSubmitAll(p.cast(source.getSubmitAll(), true));
        source.setMethod(p.cast(source.getMethod(), p.resolve(property("n2o.api.action.invoke.method"), RequestMethod.class)));
        source.setClearOnSuccess(p.cast(source.getClearOnSuccess(), false));
    }

    private void initSubmitMessageDefaults(N2oInvokeAction source, CompileProcessor p, CompileContext<?, ?> context) {
        Boolean submitOnSuccess = null;
        Boolean submitOnFail = null;
        if (source.getOperationId() != null && context instanceof PageContext) {
            submitOnSuccess = ((PageContext) context).getSubmitMessageOnSuccess();
            submitOnFail = ((PageContext) context).getSubmitMessageOnFail();
        }
        source.setMessageOnSuccess(p.cast(source.getMessageOnSuccess(), submitOnSuccess, true));
        source.setMessageOnFail(p.cast(source.getMessageOnFail(), submitOnFail, true));
    }

    private String[] initRefreshDatasources(N2oInvokeAction source, CompileProcessor p) {
        if (source.getRefreshDatasourceIds() != null)
            return source.getRefreshDatasourceIds();
        String localDatasource = getLocalDatasourceId(p);
        if (localDatasource != null)
            return new String[]{localDatasource};
        return null;
    }


    private MetaSaga initFailMeta(InvokeAction compiled, N2oInvokeAction source,
                                  CompileContext<?, ?> context, CompileProcessor p) {
        MetaSaga metaSaga = new MetaSaga();
        metaSaga.setMessageWidgetId(compiled.getPayload().getWidgetId());
        boolean closeOnFail = source.getCloseOnFail();
        if (closeOnFail) {
            if (context instanceof ModalPageContext || context instanceof DialogContext)
                metaSaga.setModalsToClose(1);
        }
        return metaSaga;
    }

    private MetaSaga initSuccessMeta(InvokeAction compiled, N2oInvokeAction source,
                                     CompileContext<?, ?> context, CompileProcessor p) {
        MetaSaga meta = new MetaSaga();
        boolean redirect = source.getRedirectUrl() != null;
        boolean doubleCloseOnSuccess = source.getDoubleCloseOnSuccess();
        boolean closeOnSuccess = source.getCloseOnSuccess() || doubleCloseOnSuccess;
        meta.setMessageWidgetId(getMessageWidgetId(compiled, context, closeOnSuccess));
        initCloseOnSuccess(context, meta, redirect, doubleCloseOnSuccess, closeOnSuccess);
        initRefreshOnClose(source, context, p, meta, closeOnSuccess);
        initRedirect(source, context, p, meta, redirect, doubleCloseOnSuccess);
        initClear(source, p, meta);
        return meta;
    }

    private void initClear(N2oInvokeAction source, CompileProcessor p, MetaSaga meta) {
        if (source.getClearOnSuccess())
            meta.setClear(getClientDatasourceId(getLocalDatasourceId(p), p));
    }

    private void initRedirect(N2oInvokeAction source, CompileContext<?, ?> context, CompileProcessor p, MetaSaga meta, boolean redirect, boolean doubleCloseOnSuccess) {
        if (redirect) {
            if (context instanceof ModalPageContext || context instanceof DialogContext)
                meta.setModalsToClose(doubleCloseOnSuccess ? 2 : 1);
            if (context instanceof DialogContext) {
                meta.setRedirect(((DialogContext) context).getParentRedirect());
            } else {
                meta.setRedirect(new RedirectSaga());
                ParentRouteScope routeScope = p.getScope(ParentRouteScope.class);
                meta.getRedirect().setPath(absolute(source.getRedirectUrl(), routeScope != null ? routeScope.getUrl() : null));
                meta.getRedirect().setTarget(source.getRedirectTarget());
                meta.getRedirect().setServer(true);
            }
        }
    }

    private void initRefreshOnClose(N2oInvokeAction source, CompileContext<?, ?> context, CompileProcessor p, MetaSaga meta, boolean closeOnSuccess) {
        if (source.getRefreshOnSuccess()) {
            if (context instanceof DialogContext) {
                meta.setRefresh(((DialogContext) context).getParentRefresh());
            } else {
                meta.setRefresh(new RefreshSaga());
                if (!closeOnSuccess && source.getRefreshDatasourceIds() != null) {
                    PageScope pageScope = p.getScope(PageScope.class);
                    if (pageScope != null)
                        meta.getRefresh().setDatasources(getClientDatasourceIds(Arrays.asList(source.getRefreshDatasourceIds()), p));
                } else if (closeOnSuccess && PageContext.class.isAssignableFrom(context.getClass()) && ((PageContext) context).getRefreshClientDataSourceIds() != null)
                    meta.getRefresh().setDatasources(((PageContext) context).getRefreshClientDataSourceIds());
            }
        }
    }

    private void initCloseOnSuccess(CompileContext<?, ?> context, MetaSaga meta, boolean redirect, boolean doubleCloseOnSuccess, boolean closeOnSuccess) {
        if (closeOnSuccess) {
            if (context instanceof ModalPageContext || context instanceof DialogContext)
                meta.setModalsToClose(doubleCloseOnSuccess ? 2 : 1);
            else if (!redirect) {
                String backRoute;
                if (context instanceof PageContext) {
                    backRoute = ((PageContext) context).getParentRoute();
                } else {
                    backRoute = "/";
                }
                meta.setRedirect(new RedirectSaga());
                meta.getRedirect().setPath(backRoute);
                meta.getRedirect().setTarget(Target.application);
            }
        }
    }

    private String getMessageWidgetId(InvokeAction compiled, CompileContext<?, ?> context, boolean closeOnSuccess) {
        String messageWidgetId = compiled.getPayload().getWidgetId();
        if (closeOnSuccess && (context instanceof PageContext)) {
            messageWidgetId = ((PageContext) context).getParentClientWidgetId();
        }
        return messageWidgetId;
    }


    private void initDataProvider(InvokeAction invokeAction, N2oInvokeAction source,
                                  CompileContext<?, ?> context, CompileProcessor p) {
        ReduxModel targetWidgetModel = getModelFromComponentScope(p);
        ParentRouteScope routeScope = p.getScope(ParentRouteScope.class);
        InvokeActionPayload payload = invokeAction.getPayload();
        N2oClientDataProvider dataProvider = new N2oClientDataProvider();
        dataProvider.setId(source.getId());
        dataProvider.setOptimistic(source.getOptimistic());
        dataProvider.setTargetModel(targetWidgetModel);
        dataProvider.setDatasourceId(getLocalDatasourceId(p));
        dataProvider.setClientDatasourceId(getClientDatasourceId(dataProvider.getDatasourceId(), p));
        validatePathAndRoute(source, routeScope);
        dataProvider.setPathParams(source.getPathParams());
        dataProvider.setFormParams(source.getFormParams());
        dataProvider.setHeaderParams(source.getHeaderParams());
        dataProvider.setMethod(source.getMethod());
        dataProvider.setUrl(source.getRoute());
        dataProvider.setSubmitForm(source.getSubmitAll());

        CompiledObject compiledObject = getObject(source, p);
        invokeAction.setObjectId(compiledObject.getId());

        AsyncMetaSaga metaSaga = invokeAction.getMeta();
        N2oClientDataProvider.ActionContextData actionContextData = new N2oClientDataProvider.ActionContextData();
        actionContextData.setObjectId(compiledObject.getId());
        actionContextData.setOperationId(source.getOperationId());
        actionContextData.setClearDatasource(metaSaga.getSuccess().getClear());
        actionContextData.setRedirect(initServerRedirect(metaSaga));
        actionContextData.setRefresh(metaSaga.getSuccess().getRefresh());
        actionContextData.setParentWidgetId(metaSaga.getSuccess().getMessageWidgetId());
        actionContextData.setMessagesForm(metaSaga.getFail().getMessageWidgetId());
        actionContextData.setMessageOnSuccess(source.getMessageOnSuccess());
        actionContextData.setMessageOnFail(source.getMessageOnFail());
        actionContextData.setMessagePosition(source.getMessagePosition());
        actionContextData.setMessagePlacement(source.getMessagePlacement());
        actionContextData.setOperation(compiledObject.getOperations().get(source.getOperationId()));
        dataProvider.setActionContextData(actionContextData);
        ClientDataProvider compiledDataProvider = ClientDataProviderUtil.compile(dataProvider, context, p);
        if (routeScope != null && compiledDataProvider.getPathMapping() != null) {
            compiledDataProvider.getPathMapping().putAll(routeScope.getPathMapping());
        }
        payload.setDataProvider(compiledDataProvider);
    }

    private CompiledObject getObject(N2oInvokeAction source, CompileProcessor p) {
        CompiledObject compiledObject = null;
        if (source.getObjectId() != null) {
            compiledObject = p.getCompiled(new ObjectContext(source.getObjectId()));
        }
        String localDatasource = getLocalDatasourceId(p);
        if (compiledObject == null && localDatasource != null) {
            DataSourcesScope dataSourcesScope = p.getScope(DataSourcesScope.class);
            if (dataSourcesScope != null) {
                String objectId = ((N2oStandardDatasource) dataSourcesScope.get(localDatasource)).getObjectId();
                if (objectId != null) {
                    compiledObject = p.getCompiled(new ObjectContext(objectId));
                } else if (((N2oStandardDatasource) dataSourcesScope.get(localDatasource)).getQueryId() != null) {
                    CompiledQuery query = p.getCompiled(new QueryContext(((N2oStandardDatasource) dataSourcesScope.get(localDatasource)).getQueryId()));
                    compiledObject = query.getObject();
                }
            }
        }
        if (compiledObject == null)
            compiledObject = p.getScope(CompiledObject.class);
        if (compiledObject == null)
            throw new N2oException(String.format("For compilation action [%s] is necessary object!", source.getId()));
        return compiledObject;
    }

    private void validatePathAndRoute(N2oInvokeAction source, ParentRouteScope routeScope) {
        String route = source.getRoute();
        N2oParam[] pathParams = source.getPathParams();
        List<String> routeParams = route == null ? null : RouteUtil.getParams(route);
        if ((routeParams == null || routeParams.isEmpty()) && (pathParams == null || pathParams.length == 0)) return;

        if (routeParams == null || routeParams.isEmpty())
            throw new N2oException(String.format("path-param \"%s\" not used in route", pathParams[0].getName()));
        if (pathParams == null || pathParams.length == 0)
            throw new N2oException(String.format("path-param \"%s\" for route \"%s\" not set", route, routeParams.get(0)));

        for (N2oParam pathParam : pathParams) {
            if (!routeParams.contains(pathParam.getName()))
                throw new N2oException(String.format("route \"%s\" not contains path-param \"%s\"", route, pathParam.getName()));
            if (routeScope.getUrl() != null && RouteUtil.getParams(routeScope.getUrl()).contains(pathParam.getName()))
                throw new N2oException(String.format("param \"%s\" duplicate in parent url ", pathParam.getName()));
        }
    }

    private RedirectSaga initServerRedirect(AsyncMetaSaga meta) {
        if (meta != null && meta.getSuccess() != null
                && meta.getSuccess().getRedirect() != null
                && meta.getSuccess().getRedirect().isServer())
            return meta.getSuccess().getRedirect();
        return null;
    }
}
