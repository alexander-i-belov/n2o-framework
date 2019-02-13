import {
  compose,
  withHandlers,
  withProps,
  onlyUpdateForKeys,
  withState,
  lifecycle,
  withPropsOnChange
} from 'recompose';
import { isEmpty, isEqual } from 'lodash';
import merge from 'deepmerge';
import { getFormValues } from 'redux-form';
import { createStructuredSelector } from 'reselect';
import { connect } from 'react-redux';

import ReduxForm from './ReduxForm';
import widgetContainer from '../WidgetContainer';
import { FORM } from '../widgetTypes';

export const withWidgetContainer = widgetContainer(
  {
    mapProps: props => {
      return {
        widgetId: props.widgetId,
        isEnabled: props.isEnabled,
        pageId: props.pageId,
        autoFocus: props.autoFocus,
        fieldsets: props.fieldsets,
        datasource: props.datasource && props.datasource[0],
        onSetModel: props.onSetModel,
        onResolve: props.onResolve,
        resolveModel: props.resolveModel,
        activeModel: props.activeModel,
        validation: props.validation,
        modelPrefix: props.modelPrefix
      };
    }
  },
  FORM
);

export const mapStateToProps = createStructuredSelector({
  reduxFormValues: (state, props) => getFormValues(props.form)(state) || {}
});

export const withLiveCycleMethods = lifecycle({
  componentDidUpdate(prevProps) {
    const {
      datasource,
      activeModel,
      defaultValues,
      reduxFormValues,
      setDefaultValues
    } = this.props;
    if (
      !isEqual(prevProps.activeModel, activeModel) &&
      !isEqual(activeModel, defaultValues) &&
      !isEqual(activeModel, reduxFormValues)
    ) {
      setDefaultValues(activeModel);
    } else if (!isEmpty(defaultValues) && !isEqual(prevProps.datasource, datasource)) {
      setDefaultValues(null);
    }
  }
});

export const withPropsOnChangeWidget = withPropsOnChange(
  (props, nextProps) => {
    return (
      !isEqual(props.defaultValues, nextProps.defaultValues) ||
      !isEqual(props.datasource, nextProps.datasource)
    );
  },
  props => {
    return {
      initialValues: props.defaultValues
        ? props.defaultValues
        : merge(props.resolveModel || {}, props.datasource || {})
    };
  }
);

export const withWidgetHandlers = withHandlers({
  onChange: props => (values, dispatch, options, prevValues) => {
    if (
      props.modelPrefix &&
      isEqual(props.initialValues, props.reduxFormValues) &&
      !isEqual(props.initialValues, props.resolveModel)
    ) {
      props.onResolve(props.initialValues);
    }

    if (isEmpty(values) || !props.modelPrefix) {
      props.onResolve(values);
    } else if (!isEqual(props.reduxFormValues, prevValues)) {
      props.onSetModel(values);
    }
  }
});

/**
 * Обертка в widgetContainer, мэппинг пропсов
 */

export default compose(
  withWidgetContainer,
  withProps(props => ({ form: props.widgetId })),
  connect(mapStateToProps),
  withState('defaultValues', 'setDefaultValues', null),
  withLiveCycleMethods,
  withPropsOnChangeWidget,
  withWidgetHandlers,
  onlyUpdateForKeys(['initialValues'])
)(ReduxForm);
