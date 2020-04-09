import React from 'react';
import PropTypes from 'prop-types';

import Text from '../../../../snippets/Text/Text';
import Icon from '../../../../snippets/Icon/Icon';
import { iconCellTypes, textPlaceTypes } from './cellTypes';
import withTooltip from '../../withTooltip';

/**
 * Ячейка таблицы с иконкой
 * @reactProps {string} id - id ячейки
 * @reactProps {object} model - модель строки
 * @reactProps {string} icon - класс иконки
 * @reactProps {string} type - тип ячейки
 * @reactProps {string} textPlace - расположение текста
 */
function IconCell({ id, model, visible, icon, type, textPlace }) {
  const text = model[id];
  return (
    visible && (
      <span title={text}>
        {icon && <Icon name={icon} />}
        {type === iconCellTypes.ICONANDTEXT && (
          <div
            className="n2o-cell-text"
            style={{
              float: textPlace === textPlaceTypes.LEFT ? 'left' : null,
              display: 'inline-block',
            }}
          >
            <Text text={text} />
          </div>
        )}
      </span>
    )
  );
}

IconCell.propTypes = {
  /**
   * ID ячейки
   */
  id: PropTypes.string.isRequired,
  /**
   * Модель данных
   */
  model: PropTypes.object.isRequired,
  /**
   * Иконка
   */
  icon: PropTypes.string.isRequired,
  /**
   * Тип ячейки
   */
  type: PropTypes.oneOf(Object.values(iconCellTypes)),
  /**
   * Местоположение текста
   */
  textPlace: PropTypes.oneOf(Object.values(textPlaceTypes)),
  /**
   * Флаг видимости
   */
  visible: PropTypes.bool,
};

IconCell.defaultProps = {
  type: iconCellTypes.ICONANDTEXT,
  textPlace: textPlaceTypes.RIGHT,
  visible: true,
};

export default withTooltip(IconCell);
