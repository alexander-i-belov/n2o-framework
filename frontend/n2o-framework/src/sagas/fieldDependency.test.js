import { runSaga } from 'redux-saga'
import { put } from 'redux-saga/effects'
import fetchMock from 'fetch-mock'
import { actionTypes } from 'redux-form'

import { SET_LOADING,
    DISABLE_FIELD,
    ENABLE_FIELD,
    SHOW_FIELD,
    HIDE_FIELD,
} from '../constants/formPlugin'
import { showField } from '../actions/formPlugin'
import { FETCH_END, FETCH_START } from '../constants/fetch'

import { modify, fetchValue } from './fieldDependency'

const setupModify = (
    type,
    options,
    values = {
        testField: 0,
    },
) => {
    const formName = 'testForm'
    const fieldName = 'testField'
    const field = {
        visible: true,
        disabled: true,
    }

    return modify(values, formName, fieldName, { type, ...options }, field)
}

describe('Проверка саги dependency', () => {
    describe('Проверка modify', () => {
        it('Проверка type enabled с истинным expression', () => {
            const gen = setupModify('enabled', {
                expression: 'testField === 0',
            })
            let next = gen.next()
            expect(next.value.payload.action.type).toEqual(ENABLE_FIELD)
            expect(next.value.payload.action.payload).toEqual({
                name: 'testField',
                form: 'testForm',
            })
            expect(next.value.payload.action.meta).toEqual({
                form: 'testForm',
            })
            next = gen.next()
            expect(next.done).toEqual(true)
        })
        it('Проверка type enabled с ложным expression', () => {
            const gen = setupModify(
                'enabled',
                {
                    expression: 'testField != 1',
                },
                { testField: 1 },
            )
            const next = gen.next()
            expect(next.value).toBe(undefined)
            expect(next.done).toEqual(true)
        })
        it('Проверка type visible с истинным expression', () => {
            const gen = setupModify('visible', {
                expression: 'testField === 1',
            })
            const next = gen.next()
            expect(next.value.payload.action.type).toEqual(HIDE_FIELD)
            expect(gen.next().done).toEqual(true)
        })
        it('Проверка type visible с ложным expression', () => {
            const gen = setupModify(
                'visible',
                {
                    expression: 'testField === 1',
                },
                { testField: 1 },
            )
            const next = gen.next()
            expect(next.value).toBe(undefined)
            expect(next.done).toEqual(true)
        })
        it('Проверка type setValue', () => {
            const gen = setupModify('setValue', {
                expression: '10 + 2',
            })
            const next = gen.next()
            expect(next.value.payload.action.payload).toEqual({
                keepDirty: false,
                value: 12,
            })
        })
        it('Проверка type reset с ложным expression', () => {
            const gen = setupModify('reset', {
                expression: 'testField != 0',
            })
            expect(gen.next().done).toEqual(true)
        })
        it('Проверка type reset с истинным expression', () => {
            const gen = setupModify(
                'reset',
                {
                    expression: 'testField === 3',
                },
                { testField: 3 },
            )
            const next = gen.next()
            expect(next.value.payload.action.payload).toEqual({
                keepDirty: false,
                value: null,
            })
            expect(gen.next().done).toEqual(true)
        })
        it('Проверка on c точкой', () => {
            const gen = modify(
                {
                    field: {
                        id: 0,
                    },
                },
                'testForm',
                'field.id',
                {
                    type: 'visible',
                    expression: 'field.id === 0',
                },
                {
                    visible: false,
                },
            )
            const next = gen.next()
            expect(next.value).toEqual(put(showField('testForm', 'field.id')))
            expect(gen.next().done).toEqual(true)
        })
        it('Проверка зависимости fetchValue', async () => {
            fetchMock.get('/test', () => ({
                status: 200,
                body: {
                    list: [
                        {
                            name: 'new name',
                        },
                    ],
                },
            }))

            const dispatched = []
            const fakeStore = {
                getState: () => ({
                    form: {
                        testForm: {
                            registeredFields: {
                                testField: {
                                    loading: false,
                                },
                            },
                        },
                    },
                }),
                dispatch: action => dispatched.push(action),
            }
            await runSaga(
                fakeStore,
                fetchValue,
                'testForm',
                'testField',
                {
                    dataProvider: {
                        url: '/test',
                        pathMapping: {},
                    },
                    valueFieldId: 'name',
                },
                { name: 'old value' },
                () => {},
                {
                    name: '',
                },
            ).toPromise()
            expect(dispatched[0].type).toBe(SET_LOADING)
            expect(dispatched[0].payload.loading).toBe(true)
            expect(dispatched[1].type).toBe(FETCH_START)
            expect(dispatched[2].type).toBe(FETCH_END)
            expect(dispatched[3].type).toBe(actionTypes.CHANGE)
            expect(dispatched[3].payload).toEqual({
                keepDirty: false,
                value: 'new name',
            })
            expect(dispatched[4].type).toBe(SET_LOADING)
            expect(dispatched[4].payload.loading).toBe(false)
        })
    })
})
