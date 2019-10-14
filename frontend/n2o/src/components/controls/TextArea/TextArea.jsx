import React from 'react';
import PropTypes from 'prop-types';
import TextareaAutosize from 'react-textarea-autosize';
import cx from 'classnames';

/**
 * Компонент TextArea
 * @param {string} className
 * @param {object} style
 * @param {boolean} disabled
 * @param {boolean} disabled
 * @param {string} placeholder
 * @param {number} rows
 * @param {number} maxRows
 * @param {string|number} value
 * @param {function} onChange
 * @param rest
 * @returns {*}
 * @constructor
 */
function TextArea({
  className,
  style,
  disabled,
  placeholder,
  rows,
  maxRows,
  value,
  onChange,
  ...rest
}) {
  const inputClass = `form-control ${className}`;
  return (
    <TextareaAutosize
      className={cx('n2o-text-area', inputClass)}
      style={style}
      disabled={disabled}
      placeholder={placeholder}
      minRows={rows}
      maxRows={maxRows}
      value={value || ''}
      onChange={onChange}
      {...rest}
    />
  );
}

TextArea.propTypes = {
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  disabled: PropTypes.bool,
  className: PropTypes.string,
  style: PropTypes.object,
  placeholder: PropTypes.string,
  onChange: PropTypes.func,
  rows: PropTypes.number,
  maxRows: PropTypes.number,
};

TextArea.defaultProps = {
  onChange: () => {},
  className: '',
  disabled: false,
  rows: 3,
  maxRows: 3,
};

export default TextArea;
