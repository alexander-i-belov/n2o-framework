import React from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';
import 'moment/locale/ru';
import { getContext } from 'recompose';

import { withLocale } from './utils';
import Calendar from './Calendar';

/**
 * Компонент CalendarHeader
 * @reactProps {function} nextMonth
 * @reactProps {locale} locale
 * @reactProps {function} nextYear
 * @reactProps {function} prevMonth
 * @reactProps {function} prevYear
 * @reactProps {moment} displayesMonth
 * @reactProps {function} nextDecade
 * @reactProps {function} prevDecade
 * @reactProps {function} changeCalendarType
 * @reactProps {string} calendarType
 */
class CalendarHeader extends React.Component {
  constructor(props) {
    super(props);
    this.nextView = this.nextView.bind(this);
  }

  renderHeaderValue(type = Calendar.BY_DAYS, displayesMonth, locale) {
    if (type === Calendar.BY_DAYS) {
      return (
        <React.Fragment>
          <a
            className="n2o-calendar-header-month-title capitalize"
            href="#"
            onClick={e => {
              e.preventDefault();
              this.props.changeCalendarType(Calendar.BY_MONTHS);
            }}
          >
            {withLocale(displayesMonth, locale).format('MMMM')}
          </a>
          &nbsp;
          <a
            className="n2o-calendar-header-year-title"
            href="#"
            onClick={e => {
              e.preventDefault();
              this.props.changeCalendarType(Calendar.BY_YEARS);
            }}
          >
            {displayesMonth.format('YYYY')}
          </a>
        </React.Fragment>
      );
    } else if (type === Calendar.BY_MONTHS) {
      return displayesMonth.format('YYYY');
    } else if (type === Calendar.BY_YEARS) {
      const decadeStart = parseInt(+displayesMonth.format('YYYY') / 10) * 10;
      return (
        <a
          href="#"
          onClick={e => {
            e.preventDefault();
          }}
        >
          {' '}
          {decadeStart + '-' + (decadeStart + 9)}{' '}
        </a>
      );
    }
    if (type === Calendar.TIME_PICKER) {
      return (
        <a
          href="#"
          onClick={e => {
            e.preventDefault();
          }}
        >
          {this.props.t('chooseDate')}
        </a>
      );
    }

    return null;
  }

  nextType(type) {
    if (type === Calendar.BY_YEARS || type === Calendar.BY_MONTHS)
      return Calendar.BY_YEARS;
    if (type === Calendar.BY_DAYS) return Calendar.BY_MONTHS;
    if (type === Calendar.TIME_PICKER) return Calendar.TIME_PICKER;
  }

  nextView(type) {
    const { nextYear, nextMonth, nextDecade } = this.props;
    if (type === Calendar.BY_MONTHS) {
      nextYear();
    } else if (type === Calendar.BY_YEARS) {
      nextDecade();
    } else {
      nextMonth();
    }
  }

  prevView(type) {
    const { prevYear, prevMonth, prevDecade } = this.props;
    if (type === Calendar.BY_MONTHS) {
      prevYear();
    } else if (type === Calendar.BY_YEARS) {
      prevDecade();
    } else {
      prevMonth();
    }
  }

  /**
   * базовый рендер
   */
  render() {
    const {
      displayesMonth,
      locale,
      changeCalendarType,
      calendarType,
    } = this.props;
    const dateStyle =
      calendarType !== Calendar.BY_YEARS &&
      calendarType !== Calendar.TIME_PICKER
        ? { cursor: 'pointer' }
        : { cursor: 'text' };
    return (
      <div className="n2o-calendar-header">
        {calendarType !== Calendar.TIME_PICKER && (
          <i
            className="fa  fa-angle-left n2o-calendar-icon"
            aria-hidden="true"
            onClick={() => this.prevView(calendarType)}
          />
        )}
        <div
          className="n2o-calendar-current-date"
          style={dateStyle}
          onClick={() =>
            calendarType !== Calendar.BY_DAYS &&
            changeCalendarType(this.nextType(calendarType))
          }
        >
          {this.renderHeaderValue(calendarType, displayesMonth, locale)}
        </div>
        {calendarType !== Calendar.TIME_PICKER && (
          <i
            className="fa  fa-angle-right n2o-calendar-icon"
            aria-hidden="true"
            onClick={() => this.nextView(calendarType)}
          />
        )}
      </div>
    );
  }
}

CalendarHeader.propTypes = {
  nextMonth: PropTypes.func,
  locale: PropTypes.oneOf(['en', 'ru']),
  nextYear: PropTypes.func,
  prevMonth: PropTypes.func,
  prevYear: PropTypes.func,
  displayesMonth: PropTypes.instanceOf(moment),
  nextDecade: PropTypes.func,
  prevDecade: PropTypes.func,
  changeCalendarType: PropTypes.func,
  calendarType: PropTypes.string,
};

export default getContext({ t: PropTypes.func })(CalendarHeader);
