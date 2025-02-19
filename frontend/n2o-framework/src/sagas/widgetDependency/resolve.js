import reduce from 'lodash/reduce'
import { call, put } from 'redux-saga/effects'

import { disableWidget, enableWidget, hideWidget, showWidget } from '../../ducks/widgets/store'
import propsResolver from '../../utils/propsResolver'
import { DEPENDENCY_TYPES } from '../../core/dependencyTypes'

export const reduceFunction = (isTrue, { model, config }) => isTrue && propsResolver(`\`${config.condition}\``, model)

/**
 * Резолв видимости
 * @param widgetId
 * @param model
 * @returns {IterableIterator<*>}
 */
export function* resolveVisible(widgetId, model) {
    const visible = reduce(model, reduceFunction, true)

    if (visible) {
        yield put(showWidget(widgetId))
    } else {
        yield put(hideWidget(widgetId))
    }
}

/**
 * Резолв активности
 * @param widgetId
 * @param model
 * @returns {IterableIterator<*>}
 */
export function* resolveEnabled(widgetId, model) {
    const enabled = reduce(model, reduceFunction, true)

    if (enabled) {
        yield put(enableWidget(widgetId))
    } else {
        yield put(disableWidget(widgetId))
    }
}

/**
 * Резолв конкретной зависимости по типу
 * @param dependencyType
 * @param widgetId
 * @param model
 * @returns {IterableIterator<*|CallEffect>}
 */
export function* resolveDependency(
    dependencyType,
    widgetId,
    model,
) {
    switch (dependencyType) {
        case DEPENDENCY_TYPES.visible: {
            yield call(resolveVisible, widgetId, model)

            break
        }
        case DEPENDENCY_TYPES.enabled: {
            yield call(resolveEnabled, widgetId, model)

            break
        }
        default:
            break
    }
}
