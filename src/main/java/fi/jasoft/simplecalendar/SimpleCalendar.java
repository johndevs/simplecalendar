package fi.jasoft.simplecalendar;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.LegacyComponent;

@SuppressWarnings("serial")
public class SimpleCalendar extends AbstractField implements LegacyComponent {

    private boolean multiselect = false;

    private Set<Weekday> disabledWeekdays = new TreeSet<Weekday>();
    private Set<Integer> disabledMonthdays = new TreeSet<Integer>();
    private Set<Date> disabledDates = new TreeSet<Date>();

    private Date startDate = null;
    private Date endDate = null;

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
        } else if (newValue instanceof Date && !multiselect) {
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
        } else if (newValue instanceof Collection<?> && multiselect) {
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
     * {@inheritDoc}
     */
    public void paintContent(PaintTarget target) throws PaintException {

        // Update multiselect mode
        target.addAttribute("multiselect", multiselect);

        // Update selection
        Set<Date> selectionSet;
        if (getValue() == null) {
            selectionSet = new HashSet<Date>();
        } else if (getValue() instanceof Collection<?>) {
            selectionSet = (Set<Date>) getValue();
        } else {
            selectionSet = Collections.singleton((Date) getValue());
        }
        String[] selection = new String[selectionSet.size()];
        Iterator<Date> selectionIter = selectionSet.iterator();
        int count = 0;
        while (selectionIter.hasNext()) {
            Date d = selectionIter.next();
            selection[count] = String.valueOf(d.getTime());
            ++count;
        }
        target.addVariable(this, "selection", selection);

        // Update disabled weekdays
        Set<Integer> days = new HashSet<Integer>();
        for (Weekday wd : disabledWeekdays) {
            days.add(wd.ordinal());
        }
        target.addAttribute("disabledWeekdays", days.toArray());

        // Update disabled monthly dates
        target.addAttribute("disabledMonthdays", disabledMonthdays.toArray());

        // Update disabled dates
        String[] disabledDates = new String[this.disabledDates.size()];
        Iterator<Date> disabledDatesIterator = this.disabledDates.iterator();
        count = 0;
        while (disabledDatesIterator.hasNext()) {
            Date d = disabledDatesIterator.next();
            disabledDates[count] = String.valueOf(d.getTime());
            ++count;
        }
        target.addVariable(this, "disabledDates", disabledDates);

        // Starting date
        if (startDate != null) {
            target.addAttribute("startDate", startDate.getTime());
        } else {
            target.addAttribute("startDate", -1L);
        }

        // Ending date
        if (endDate != null) {
            target.addAttribute("endDate", endDate.getTime());
        } else {
            target.addAttribute("endDate", -1L);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {

        if (variables.containsKey("selection")) {
            String[] selectionStr = (String[]) variables.get("selection");
            Set<Date> dates = new HashSet<Date>();
            for (String time : selectionStr) {
                dates.add(new Date(Long.parseLong(time)));
            }

            if (multiselect) {
                setValue(dates, true);
            } else if (!dates.isEmpty()) {
                setValue(dates.iterator().next(), true);
            } else {
                setValue(null, true);
            }
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
        this.multiselect = multiselect;
        requestRepaint();
    }

    /**
     * If multiselect is true then several Dates can be selected
     * 
     * @return Is multiselect mode active
     */
    public boolean isMultiSelect() {
        return multiselect;
    }

    /**
     * Sets specific weekdays which the user should not be able to select
     * 
     * @param day
     */
    public void setDisabledWeekDays(Weekday... days) {
        disabledWeekdays.clear();
        if (days != null) {
            disabledWeekdays.addAll(Arrays.asList(days));
        }
        requestRepaint();
    }

    /**
     * Returns a set with weekdays which has been disabled
     * 
     * @return
     */
    public Weekday[] getDisabledWeekdays() {
        if (disabledWeekdays.isEmpty()) {
            return null;
        }
        return disabledWeekdays.toArray(new Weekday[disabledWeekdays.size()]);
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
        for (Weekday wd : disabledWeekdays) {
            if (Weekday.toInteger(wd) == calendar.get(Calendar.DAY_OF_WEEK)) {
                return true;
            }
        }

        // Check disabled monthly days
        for (Integer d : disabledMonthdays) {
            if (calendar.get(Calendar.DAY_OF_MONTH) == d) {
                return true;
            }
        }

        // Check for disabled dates
        Calendar dcal = Calendar.getInstance();
        for (Date d : disabledDates) {
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
        disabledMonthdays.clear();
        if (dates != null) {
            disabledMonthdays.addAll(Arrays.asList(dates));
        }
        requestRepaint();
    }

    /**
     * Returns the monthly days of month which are disabled
     * 
     * @return
     */
    public Integer[] getDisabledMonthlyDates() {
        if (disabledMonthdays.isEmpty()) {
            return null;
        }
        return disabledMonthdays.toArray(new Integer[disabledMonthdays.size()]);
    }

    /**
     * Set dates which should be disabled
     * 
     * @param dates
     *            Dates which are disabled
     */
    public void setDisabledDates(Date... dates) {
        disabledDates.clear();
        if (dates != null) {
            disabledDates.addAll(Arrays.asList(dates));
        }
        requestRepaint();
    }

    /**
     * Returns dates which have been disabled
     * 
     * @return
     */
    public Date[] getDisabledDates() {
        if (disabledDates.isEmpty()) {
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
        startDate = start;
        requestRepaint();
    }

    /**
     * Returns the first date the panel displays or NULL if not set
     * 
     * @return
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Set the last date the panel should display or NULL to not have a limit
     * 
     * @param end
     *            The last date the panel should display
     */
    public void setEndDate(Date end) {
        endDate = end;
        requestRepaint();
    }

    /**
     * Returns the last date the panel displays or NULL if no limit has been set
     * 
     * @return
     */
    public Date getEndDate() {
        return endDate;
    }
}
