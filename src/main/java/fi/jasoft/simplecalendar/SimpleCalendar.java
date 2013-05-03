package fi.jasoft.simplecalendar;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.AbstractFieldState;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.LegacyComponent;

import fi.jasoft.simplecalendar.shared.SimpleCalendarState;
import fi.jasoft.simplecalendar.shared.Weekday;

@SuppressWarnings("serial")
public class SimpleCalendar extends AbstractField<Object> {  

    /**
     * The type is a java.util.Set if in multiselect mode else it is a Date
     */
    @Override
    public Class<?> getType() {
        if (isMultiSelect()) {
            return Set.class;
        }
        return Date.class;
    }
    
    /*
     * (non-Javadoc)
     * @see com.vaadin.ui.AbstractField#getState()
     */
    @Override
    protected SimpleCalendarState getState() {    
    	return (SimpleCalendarState) super.getState();
    }
    
    /*
     * (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#getState(boolean)
     */
    @Override
    protected SimpleCalendarState getState(boolean markAsDirty) {
    	return (SimpleCalendarState) super.getState(markAsDirty);
    }

    /**
     * Returns a java.util.Set of Dates if in multiselect mode else a single
     * Date or null if no date has been selected.
     */
    @Override
    public Object getValue() {
        if (isMultiSelect() && super.getValue() == null) {
            return Collections.EMPTY_SET;
        }
        return super.getValue();
    }

    /**
     * Set the Date(s) which should be selected. If multiselect mode is set then
     * the value should be a collection of dates, if not in multiselect mode the
     * it should be a Date
     */
    @Override
    public void setValue(Object newValue) throws ReadOnlyException,
            ConversionException {
        if (newValue == null) {
            super.setValue(null);
        } else if (newValue instanceof Date && !isMultiSelect()) {
            if (isDateDisabled((Date) newValue)) {
                throw new IllegalArgumentException("Date has been disabled");
            } else if (!isDateInRange((Date) newValue)) {
                throw new IllegalArgumentException(
                        "Date is outside of start or end date");
            } else {
                super.setValue(newValue);
            }
        } else if (newValue instanceof Date) {
            if (isDateDisabled((Date) newValue)) {
                throw new IllegalArgumentException("Date has been disabled");
            } else if (!isDateInRange((Date) newValue)) {
                throw new IllegalArgumentException(
                        "Date is outside of start or end date");
            } else {
                super.setValue(Collections.singleton(newValue));
            }
        } else if (newValue instanceof Collection<?> && isMultiSelect()) {
            for (Date d : (Collection<Date>) newValue) {
                if (isDateDisabled(d)) {
                    throw new IllegalArgumentException("Date has been disabled");
                } else if (!isDateInRange(d)) {
                    throw new IllegalArgumentException(
                            "Date is outside of start or end date");
                }
            }
            super.setValue(newValue);
        } else {
            throw new IllegalArgumentException(
                    "The value must be a Date or a collection of dates if multiselect.");
        }
    }

    /**
     * Determines if a user can select several dates. Please note that setting
     * this to true will effect {@link #getValue()} and
     * {@link #setValue(Object)}.
     * 
     * @param multiselect
     *            Should the user be able to select several dates
     */
    public void setMultiSelect(boolean multiselect) {
    	getState().multiselect = multiselect;
    }

    /**
     * If multiselect is true then several Dates can be selected
     * 
     * @return Is multiselect mode active
     */
    public boolean isMultiSelect() {
        return getState(false).multiselect;
    }

    /**
     * Sets specific weekdays which the user should not be able to select
     * 
     * @param day
     */
    public void setDisabledWeekDays(Weekday... days) {
    	getState().disabledWeekdays = Arrays.asList(days);    	
    }

    /**
     * Returns a set with weekdays which has been disabled
     * 
     * @return
     */
    public Weekday[] getDisabledWeekdays() {
    	List<Weekday> weekDays = getState(false).disabledWeekdays;
    	if(weekDays == null || weekDays.isEmpty()){
    		return null;
    	}    	
    	return weekDays.toArray(new Weekday[weekDays.size()]);    	
    }

    /**
     * Returns true if a date has been disabled, null dates are always enabled
     * 
     * @param date
     *            The date to check
     * @return True if date is disabled
     */
    protected boolean isDateDisabled(Date date) {
        if (date == null) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Check disabled weekdays
        for (Weekday wd : getDisabledWeekdays()) {
            if (Weekday.toInteger(wd) == calendar.get(Calendar.DAY_OF_WEEK)) {
                return true;
            }
        }

        // Check disabled monthly days
        for (Integer d : getDisabledMonthlyDates()) {
            if (calendar.get(Calendar.DAY_OF_MONTH) == d) {
                return true;
            }
        }

        // Check for disabled dates
        Calendar dcal = Calendar.getInstance();
        for (Date d : getDisabledDates()) {
            dcal.setTime(d);
            if (calendar.get(Calendar.YEAR) == dcal.get(Calendar.YEAR)
                    && calendar.get(Calendar.MONTH) == dcal.get(Calendar.MONTH)
                    && calendar.get(Calendar.DAY_OF_MONTH) == dcal
                            .get(Calendar.DAY_OF_MONTH)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Is the date in the given range
     * 
     * @param date
     *            The date to test
     * @return
     */
    protected boolean isDateInRange(Date date) {
        if (getStartDate() != null && date != null
                && date.before(getStartDate())) {
            return false;
        }
        if (getEndDate() != null && date != null && date.after(getEndDate())) {
            return false;
        }
        return true;
    }

    /**
     * Set which days of the month should be disabled. Values should be between
     * 1 and 31.
     * 
     * @param days
     *            The days of the month which should be disabled each month
     */
    public void setDisabledMonthlyDates(Integer... dates) {
    	getState().disabledMonthdays  = Arrays.asList(dates);    	
    }

    /**
     * Returns the monthly days of month which are disabled
     * 
     * @return
     */
    public Integer[] getDisabledMonthlyDates() {
    	List<Integer> disabledDates = getState(false).disabledMonthdays;    	
        if (disabledDates == null || disabledDates.isEmpty()) {
            return null;
        }
        return disabledDates.toArray(new Integer[disabledDates.size()]);
    }

    /**
     * Set dates which should be disabled
     * 
     * @param dates
     *            Dates which are disabled
     */
    public void setDisabledDates(Date... dates) {
    	getState().disabledDates = Arrays.asList(dates);    	
    }

    /**
     * Returns dates which have been disabled
     * 
     * @return
     */
    public Date[] getDisabledDates() {
    	List<Date> disabledDates = getState(false).disabledDates;
        if (disabledDates == null || disabledDates.isEmpty()) {
            return null;
        }
        return disabledDates.toArray(new Date[disabledDates.size()]);
    }

    /**
     * Set the oldest date the panel should display or NULL to not have a limit
     * 
     * @param start
     *            The first date in the panel
     */
    public void setStartDate(Date start) {
    	getState().startDate = start;        
    }

    /**
     * Returns the first date the panel displays or NULL if not set
     * 
     * @return
     */
    public Date getStartDate() {
        return getState(false).startDate;
    }

    /**
     * Set the last date the panel should display or NULL to not have a limit
     * 
     * @param end
     *            The last date the panel should display
     */
    public void setEndDate(Date end) {
    	getState().endDate = end;    	
    }

    /**
     * Returns the last date the panel displays or NULL if no limit has been set
     * 
     * @return
     */
    public Date getEndDate() {
        return getState(false).endDate;
    }
}
