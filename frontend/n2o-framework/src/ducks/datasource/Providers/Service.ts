import { call, select } from 'redux-saga/effects'
import get from 'lodash/get'

// @ts-ignore ignore import error from js file
import { dataProviderResolver } from '../../../core/dataProviderResolver'
import { dataSourceByIdSelector } from '../selectors'
// @ts-ignore ignore import error from js file
import { getLocation, rootPageSelector } from '../../global/store'
// @ts-ignore ignore import error from js file
import { makePageRoutesByIdSelector } from '../../pages/selectors'
import type { QueryOptions, QueryResult, ServiceProvider, ServiceSubmit } from '../Provider'
import type { State as GlobalState } from '../../State'
import type { DataSourceState } from '../DataSource'
// @ts-ignore ignore import error from js file
import { handleInvoke } from '../../../sagas/actionsImpl'
import { ModelPrefix } from '../../../core/datasource/const'

// @ts-ignore ignore import error from js file
import { routesQueryMapping } from './service/routesQueryMapping'
import { fetch } from './service/fetch'

export function* submit(id: string, provider: ServiceSubmit, apiProvider: unknown) {
    const { pageId }: DataSourceState = yield select(dataSourceByIdSelector(id))
    const action = {
        payload: {
            datasource: id,
            dataProvider: provider,
            model: ModelPrefix.active,
            pageId,
        },
    }

    yield call(handleInvoke, apiProvider, action)
}

export function* invoke() {
    // TODO Перенести сюда инвок из actionsImpl
}

export function* query(id: string, provider: ServiceProvider, options: QueryOptions) {
    const state: GlobalState = yield select()
    const { size, sorting, page, pageId } = yield select(dataSourceByIdSelector(id))

    if (!provider.url) {
        throw new Error('Parametr "url" is required for fetch data')
    }

    const currentPageId: string = pageId || (yield select(rootPageSelector))
    const routes: { queryMapping: object } | void = yield select(makePageRoutesByIdSelector(currentPageId))

    if (routes?.queryMapping) {
        const location: unknown = yield select(getLocation)

        yield* routesQueryMapping(state, routes, location)
    }

    const query = {
        page: get(options, 'page', page),
        size,
        sorting,
    }
    const resolvedProvider = dataProviderResolver(
        state,
        provider,
        query,
        options,
    )

    return (yield fetch(id, resolvedProvider)) as QueryResult
}
