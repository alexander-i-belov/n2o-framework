import React from 'react'
import { storiesOf } from '@storybook/react'
import { Route, Switch } from 'react-router-dom'

// eslint-disable-next-line import/no-named-as-default
import Actions, { Actions as ActionsComponent } from './Actions'
// eslint-disable-next-line import/extensions
import metadata from './Link.meta'

const stories = storiesOf('Действия/Ссылка Link', module)
stories.addParameters({
    info: {
        propTables: [ActionsComponent],
        propTablesExclude: [Actions, Route, Switch],
    },
})

/* eslint-disable react/jsx-one-expression-per-line */
stories

    .add(
        'Компонент',
        () => (
            <Actions toolbar={metadata.toolbar} containerKey="linkContainer" />
        ),
        {
            info: {
                text: `
       Компонент 'Действие переход по ссылке'
       ~~~js
       import Actions from 'n2o-framework/lib/components/actions/Actions';

       const props = {
          "actions": {
            "redirect": {
              "src": "link",
              "options": {
                "path": "/test",
                "target": "application"
              }
            }
          },
          "toolbar": [
          {
            "buttons": [
              {
                "id": "redirect",
                "title": "Пример Link",
                "actionId": "redirect",
                "hint": "Пример Link"
              }
             ]
            }
          ]
        };

        <Actions {...props} containerKey="linkContainer" />
       ~~~
      `,
            },
        },
    )
    .add('Метаданные', () => (
        <>
            <Actions
                actions={metadata.actions}
                toolbar={metadata.toolbar}
                containerKey="linkContainer"
            />
            <Switch>
                <Route
                    path="*"
                    component={({ match }) => match.url !== '/' && (
                        <span>
                  Сработал роутер для:
                            {' '}
                            <pre>{match.url}</pre>
                        </span>
                    )
                    }
                />
            </Switch>
        </>
    ))
