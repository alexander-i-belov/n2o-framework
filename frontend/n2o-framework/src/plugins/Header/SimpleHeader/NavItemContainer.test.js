import React from 'react'
import { BrowserRouter as Router } from 'react-router-dom'

import { NavItemContainer } from './NavItemContainer'

const setup = props => mount(
    <Router>
        <NavItemContainer {...props} />
    </Router>,
)

describe('Тесты NavItemContainer', () => {
    it('Dropdown', () => {
        const wrapper = setup({
            sidebarOpen: false,
            itemProps: {
                id: '2131',
                title: 'test',
                type: 'dropdown',
                items: [{ title: 'test1', href: '/', linkType: 'inner' }],
            },
        })
        expect(wrapper.find('Dropdown').exists()).toEqual(true)
    })
    it('Link', () => {
        const wrapper = setup({
            itemProps: {
                id: '2131',
                label: 'test',
                type: 'link',
                href: 'testHref',
            },
        })
        expect(wrapper.find('Link').exists()).toEqual(true)
    })
    it('Text', () => {
        const wrapper = setup({
            itemProps: {
                type: 'text',
                label: 'test',
            },
        })
        expect(wrapper.find('span.nav-link').exists()).toEqual(true)
    })
    it('target = _blank', () => {
        const wrapper = setup({
            itemProps: {
                id: '2131',
                label: 'test',
                type: 'link',
                href: 'testHref',
                target: '_blank',
            },
        })
        expect(wrapper.find('Link').props().target).toEqual('_blank')
    })
})
