/*
 * Copyright 2013 John Ahlroos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fi.jasoft.simplecalendar.client;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.DateTimeService;
import com.vaadin.client.Util;
import com.vaadin.client.ui.FocusableFlowPanel;

public class SimpleCalendarWidget extends FocusableFlowPanel implements ClickHandler,
        FocusHandler, BlurHandler, KeyDownHandler, MouseDownHandler {

    private static final int ROWS = 7;
    private static final int COLUMNS = 7;

    private static final String STYLENAME = "date-panel";

    private Button prevMonth;
    private Button prevYear;
    private Button nextMonth;
    private Button nextYear;

    private HTML monthAndYear;

    private HorizontalPanel controls;
    private Grid grid;

    private int day;
    private int month;
    private int year;

    private int displayedMonth;
    private int displayedYear;

    private Date today = new Date();

    private Date lastSelection;
    private Set<Date> selected = new HashSet<Date>();

    private boolean isMultiSelect = false;

    private Date startDate = null;
    private Date endDate = null;

    private Date[] disabledDates;
    private int[] disabledWeekdays;
    private int[] disabledMonthDates;

    private DateCell focusedCell;
    
    private String previousHeight;

    protected String descriptionDateFormat = "EEEE, MMMM dd, yyyy";

    private final Set<DateValueChangeListener> valueChangeListeners = new HashSet<DateValueChangeListener>();

    /**
     * Represents a cell in the date grid
     */
    protected class DateCell extends HTML {
        private int row;
        private int column;
        private Date date;

        public DateCell(int row, int column, Date date) {
            this.row = row;
            this.column = column;
            this.date = date;
            setHTML(date.getDate() + "");
            DateTimeFormat fmt = DateTimeFormat
                    .getFormat(descriptionDateFormat);
            setTitle(fmt.format(date));
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        public Date getDate() {
            return date;
        }
    }

    /**
     * Default constructor
     */
    public SimpleCalendarWidget() {

        // Set default date to today
        day = today.getDate();
        month = today.getMonth();
        year = today.getYear() + 1900;

        setStyleName(STYLENAME);

        // Add keyboard handlers
        addFocusHandler(this);
        addBlurHandler(this);
        addKeyDownHandler(this);

        // Create the header
        controls = new HorizontalPanel();
        controls.setStyleName(getStyleName() + "-controls");
        controls.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        add(controls);

        prevYear = new Button("", new ClickHandler() {
            public void onClick(ClickEvent event) {
                previousYear();
            }
        });

        prevYear.setTabIndex(-1);
        prevYear.addMouseDownHandler(new MouseDownHandler() {
            public void onMouseDown(MouseDownEvent event) {
                // Prevent focus from moving to button
                event.preventDefault();
            }
        });

        prevYear.setStyleName(controls.getStyleName() + "-prev-year");
        controls.add(prevYear);
        controls.setCellHorizontalAlignment(prevYear,
                HasHorizontalAlignment.ALIGN_LEFT);
        controls.setCellWidth(prevYear, "35px");

        prevMonth = new Button("", new ClickHandler() {
            public void onClick(ClickEvent event) {
                previousMonth();
            }
        });

        prevMonth.setTabIndex(-1);
        prevMonth.addMouseDownHandler(new MouseDownHandler() {
            public void onMouseDown(MouseDownEvent event) {
                // Prevent focus from moving to button
                event.preventDefault();
            }
        });

        prevMonth.setStyleName(controls.getStyleName() + "-prev-month");
        controls.add(prevMonth);
        controls.setCellHorizontalAlignment(prevMonth,
                HasHorizontalAlignment.ALIGN_LEFT);
        controls.setCellWidth(prevMonth, "35px");

        monthAndYear = new HTML("");
        monthAndYear.setStyleName(controls.getStyleName() + "-caption");
        controls.add(monthAndYear);
        controls.setCellHorizontalAlignment(monthAndYear,
                HasHorizontalAlignment.ALIGN_CENTER);

        nextMonth = new Button("", new ClickHandler() {
            public void onClick(ClickEvent event) {
                nextMonth();
            }
        });

        nextMonth.setTabIndex(-1);
        nextMonth.addMouseDownHandler(new MouseDownHandler() {
            public void onMouseDown(MouseDownEvent event) {
                // Prevent focus from moving to button
                event.preventDefault();
            }
        });

        nextMonth.setStyleName(controls.getStyleName() + "-next-month");
        controls.add(nextMonth);
        controls.setCellHorizontalAlignment(nextMonth,
                HasHorizontalAlignment.ALIGN_RIGHT);
        controls.setCellWidth(nextMonth, "35px");

        nextYear = new Button("", new ClickHandler() {
            public void onClick(ClickEvent event) {
                nextYear();
            }
        });

        nextYear.setTabIndex(-1);
        nextYear.addMouseDownHandler(new MouseDownHandler() {
            public void onMouseDown(MouseDownEvent event) {
                // Prevent focus from moving to button
                event.preventDefault();
            }
        });

        nextYear.setStyleName(controls.getStyleName() + "-next-year");
        controls.add(nextYear);
        controls.setCellHorizontalAlignment(nextYear,
                HasHorizontalAlignment.ALIGN_RIGHT);
        controls.setCellWidth(nextYear, "35px");

        // Create day grid
        grid = new Grid(ROWS, COLUMNS);
        grid.setStyleName(getStyleName() + "-grid");
        grid.setCellSpacing(0);
        grid.setCellPadding(0);
        grid.addClickHandler(this);
        grid.addDomHandler(this, MouseDownEvent.getType());
        grid.setWidth("100%");
        add(grid);

        // Populate first row with days
        DateTimeService dts = new DateTimeService();
        for (int c = 0; c < COLUMNS; c++) {
            int index = (dts.getFirstDayOfWeek() + c) % COLUMNS;
            String dayName = dts.getShortDay(index);
            dayName = String.valueOf(Character.toUpperCase(dayName.charAt(0)))
                    + dayName.substring(1);
            grid.setHTML(0, c, dayName);
            grid.getCellFormatter().setStyleName(0, c,
                    grid.getStyleName() + "-day-caption");
        }

        // Format first row
        grid.getRowFormatter().setStyleName(0, grid.getStyleName() + "-days");
        
        // Populate grid with cells
        for (int r = 1; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                grid.setHTML(r, c, "");
                grid.getCellFormatter().setStyleName(r, c,
                        grid.getStyleName() + "-day");
                grid.getCellFormatter().setHeight(r, c, "39px");
            }
            grid.getRowFormatter().setStyleName(r,
                    grid.getStyleName() + "-week");
        }

        displayedMonth = month;
        displayedYear = year;

        updateUI(year, month, day);
    }

    /**
     * Moves keyboard focus to the previous day, changes year and month in the
     * UI if necessary
     */
    private void previousDay() {
        if (focusedCell == null) {
            // If we have no focus the return
            return;
        }

        int date = focusedCell.getDate().getDate();

        if (date == 1) {
            /*
             * If we are at day 1 of a month and go back then we have to jump to
             * the last day of the previous month.
             */

            // First day of the month, need to change UI first
            previousMonth();

            // Select last day of month
            setKeyboardFocus(getDaysInMonth(displayedYear, displayedMonth));

        } else {
            // Focus the previous day
            setKeyboardFocus(date - 1);
        }
    }

    private void nextDay() {
        if (focusedCell == null) {
            // If we have no focus the return
            return;
        }

        int daysInMonth = getDaysInMonth(displayedYear, displayedMonth);

        int date = focusedCell.getDate().getDate();

        if (date == daysInMonth) {
            /*
             * We are at the last day of the month, so we first need to jump to
             * the next month before moving focus
             */

            // Change to next month
            nextMonth();

            // Focus the first day of the month
            setKeyboardFocus(1);

        } else {
            // Focus the next day
            setKeyboardFocus(date + 1);
        }
    }

    private void previousWeek() {
        if (focusedCell == null) {
            // If we have no focus the return
            return;
        }

        if (focusedCell.getDate().getDate() <= 7) {
            previousMonth();

            int diff = 7 - focusedCell.getDate().getDate();
            setKeyboardFocus(getDaysInMonth(displayedYear, displayedMonth)
                    - diff);

        } else {
            // Move focus 7 days backward
            setKeyboardFocus(focusedCell.getDate().getDate() - 7);
        }
    }

    private void nextWeek() {
        if (focusedCell == null) {
            // If we have no focus the return
            return;
        }

        int daysInMonth = getDaysInMonth(displayedYear, displayedMonth);
        if (focusedCell.getDate().getDate() >= daysInMonth - 7) {

            // Calculate how many days we should jump in the next month
            int diff = daysInMonth - focusedCell.getDate().getDate();

            // Change to next month
            nextMonth();

            // Set focus
            setKeyboardFocus(7 - diff);

        } else {
            // Move focus 7 days forward
            setKeyboardFocus(focusedCell.getDate().getDate() + 7);
        }
    }

    /**
     * Changes UI to display the previous month
     */
    private void previousMonth() {
        int month = displayedMonth - 1;
        int year = displayedYear;
        if (month < 0) {
            year--;
            month = 11;
        }

        if (withinDateRange(new Date(year - 1900, month, 31))) {
            updateUI(year, month, 1);
            nextMonth.setEnabled(true);
        }
    }

    /**
     * Changes UI to display the next month
     */
    private void nextMonth() {
        int month = displayedMonth + 1;
        int year = displayedYear;
        if (month > 11) {
            year++;
            month = 0;
        }
        if (withinDateRange(new Date(year - 1900, month, 1))) {
            updateUI(year, month, 1);
            prevMonth.setEnabled(true);
        }
    }

    /**
     * Changes UI to display the previous year
     */
    private void previousYear() {
        int year = displayedYear - 1;
        if (withinDateRange(new Date(year - 1900, displayedMonth, 31))) {
            updateUI(year, displayedMonth, 1);
            nextYear.setEnabled(true);
        }
    }

    /**
     * Changes UI to display the next year
     */
    private void nextYear() {
        int year = displayedYear + 1;
        if (withinDateRange(new Date(year - 1900, displayedMonth, 1))) {
            updateUI(year, displayedMonth, 1);
            prevYear.setEnabled(true);
        }
    }

    /**
     * Creates a date cell in the grid. Override this to customize the cells
     * 
     * @param row
     *            The row of the cell
     * @param column
     *            The column of the cell
     * @param date
     *            The date the cell represents
     * @return
     */
    protected Widget createCell(int row, int column, Date date) {
        return new DateCell(row, column, date);
    }

    private void calculateRowHeights() {
        int totalHeight = Util.getRequiredHeight(this) - Util.measureVerticalPaddingAndBorder(grid.getElement(), 10); 
        double rowHeight = totalHeight / ROWS;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                if (r > 0) {
                    grid.getCellFormatter().getElement(r, c).getStyle()
                            .setHeight(rowHeight, Unit.PX);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.UIObject#setHeight(java.lang.String)
     */
    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        if(height != previousHeight){
        	calculateRowHeights();    
        	previousHeight = height;
        }
    }

    private void setKeyboardFocus(int row, int column) {
        if (focusedCell != null) {
            grid.getCellFormatter().removeStyleName(focusedCell.getRow(),
                    focusedCell.getColumn(), "focus");
        }

        try {
            focusedCell = (DateCell) grid.getWidget(row, column);
        } catch (IndexOutOfBoundsException ioobe) {
            Window.alert(ioobe + "");
            return;
        }

        if (focusedCell != null) {
            grid.getCellFormatter().addStyleName(focusedCell.getRow(),
                    focusedCell.getColumn(), "focus");
        }
    }

    private void setKeyboardFocus(int date) {
        for (int row = 1; row < grid.getRowCount(); row++) {
            for (int col = 0; col < grid.getColumnCount(); col++) {
                DateCell cell = (DateCell) grid.getWidget(row, col);
                if (cell != null) {
                    Date d = cell.getDate();
                    if (d != null) {
                        int month = d.getMonth();
                        int day = d.getDate();
                        if (month == displayedMonth && day == date) {
                            setKeyboardFocus(row, col);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void select(DateCell dc, boolean ctrl, boolean shift) {
        Date date = dc.getDate();

        // Abort if disabled
        if (dateIsDisabled(date)) {
            return;
        }

        // Set keyboard focus
        focusedCell = dc;

        if (isMultiSelect && ctrl) {
            if (dateIsSelected(date)) {
                unselect(date, false);
            } else {
                select(date);
            }

            fireValueChangeEvent();

        } else if (isMultiSelect && shift) {
            removeSelections();

            // Select a range
            if (lastSelection != null) {
                if (lastSelection.after(date)) {
                    selected.addAll(GwtDateUtil.getDatesBetween(date,
                            lastSelection));
                } else {
                    selected.addAll(GwtDateUtil.getDatesBetween(lastSelection,
                            date));
                }

                updateUI(displayedYear, displayedMonth, 1);
            }

            fireValueChangeEvent();

        } else {
            if (dateIsSelected(date)) {
                // Re-selection removes the selection
                removeSelections();

            } else {
                // remove previous selections
                removeSelections();

                // Select date
                select(date);
            }

            fireValueChangeEvent();
        }
    }

    public void onClick(ClickEvent event) {

        // Only handle left button clicks
        if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
            return;
        }

        Cell cell = grid.getCellForEvent(event);
        Widget w = grid.getWidget(cell.getRowIndex(), cell.getCellIndex());
        setKeyboardFocus(cell.getRowIndex(), cell.getCellIndex());

        if (w instanceof DateCell) {
            DateCell dc = (DateCell) w;
            select(dc, event.isControlKeyDown(), event.isShiftKeyDown());

        }
    }

    private void removeSelections() {
        for (int r = 1; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                grid.getCellFormatter().removeStyleName(r, c,
                        grid.getStyleName() + "-selected");
            }
        }
        selected.clear();
    }

    private static boolean withinDateRange(Date date, Date startDate,
            Date endDate) {
        boolean isAfterStartDate = false;
        if (startDate == null
                || GwtDateUtil.dateEqualResolutionDay(startDate, date)
                || startDate.before(date)) {
            isAfterStartDate = true;
        }

        boolean isBeforeEndDate = false;
        if (endDate == null
                || GwtDateUtil.dateEqualResolutionDay(endDate, date)
                || endDate.after(date)) {
            isBeforeEndDate = true;
        }

        return isAfterStartDate && isBeforeEndDate;
    }

    private boolean withinDateRange(Date date) {
        return withinDateRange(date, startDate, endDate);
    }

    private boolean isWeekdayDisabled(int weekday) {
        if (disabledWeekdays != null) {
            for (int i = 0; i < disabledWeekdays.length; i++) {
                if (disabledWeekdays[i] == weekday) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isMonthDateDisabled(int date) {
        if (disabledMonthDates != null) {
            for (int i = 0; i < disabledMonthDates.length; i++) {
                if (disabledMonthDates[i] == date) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isDateDisabled(Date date) {
        if (disabledDates != null) {
            for (Date d : disabledDates) {
                if (GwtDateUtil.dateEqualResolutionDay(d, date)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dateIsDisabled(Date date) {
        return isDateDisabled(date) || isMonthDateDisabled(date.getDate())
                || isWeekdayDisabled(date.getDay());
    }

    private boolean dateIsSelected(Date date) {
        for (Date d : selected) {
            if (GwtDateUtil.dateEqualResolutionDay(d, date)) {
                return true;
            }
        }
        return false;
    }

    private void updateUI() {
        Scheduler.get().scheduleEntry(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                updateUI(displayedYear, displayedMonth, day);
            }
        });
    }

    private void updateUI(int year, int month, int day) {

        displayedMonth = month;
        displayedYear = year;

        // Check that the displayed date is within the range
        Date now = new Date(year - 1900, month, day);
        if (!withinDateRange(now)) {
            if (endDate != null) {
                displayedMonth = endDate.getMonth();
                displayedYear = endDate.getYear() + 1900;
            } else if (startDate != null) {
                displayedMonth = startDate.getMonth();
                displayedYear = startDate.getYear() + 1900;
            }
        }

        prevYear.setEnabled(withinDateRange(new Date(displayedYear - 1900 - 1,
                displayedMonth, 31)));

        prevMonth.setEnabled(withinDateRange(new Date(displayedYear - 1900,
                displayedMonth - 1, getDaysInMonth(displayedYear - 1900,
                        displayedMonth - 1))));

        nextMonth.setEnabled(withinDateRange(new Date(displayedYear - 1900,
                displayedMonth + 1, 1)));

        nextYear.setEnabled(withinDateRange(new Date(displayedYear - 1900 + 1,
                displayedMonth, 1)));

        Date date = new Date(displayedYear - 1900, displayedMonth, day);
        DateTimeService dts = new DateTimeService();

        // Set month and year
        String yearName = dts.formatDate(date, "MMMM yyyy");
        yearName = String.valueOf(Character.toUpperCase(yearName.charAt(0)))
                + yearName.substring(1);
        monthAndYear.setHTML(yearName);

        // Populate days
        int daysInMonth = getDaysInMonth(displayedYear, displayedMonth);
        int lastMonth = displayedMonth > 0 ? displayedMonth - 1 : 11;
        int lastYear = displayedMonth > 0 ? displayedYear : displayedYear - 1;
        int daysInLastMonth = getDaysInMonth(lastYear, lastMonth);
        int d = 1;
        int nextMonthDay = 1;

        int firstWeekDay = (new Date(displayedYear - 1900, displayedMonth, 1))
                .getDay() - dts.getFirstDayOfWeek();

        for (int r = 1; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {

                // Clear previous styles and content
                grid.setHTML(r, c, "");
                grid.getCellFormatter().removeStyleName(r, c,
                        grid.getStyleName() + "-day-previous-month");
                grid.getCellFormatter().removeStyleName(r, c,
                        grid.getStyleName() + "-day-selected");
                grid.getCellFormatter().removeStyleName(r, c,
                        grid.getStyleName() + "-day-next-month");
                grid.getCellFormatter().removeStyleName(r, c,
                        grid.getStyleName() + "-day-today");
                grid.getCellFormatter().removeStyleName(r, c,
                        grid.getStyleName() + "-selected");
                grid.getCellFormatter().removeStyleName(r, c,
                        grid.getStyleName() + "-disabled");

                // Populate cells and add style names
                if (r == 1 && c < firstWeekDay) {
                    // Previous month
                    Date x = new Date(displayedYear - 1900, displayedMonth - 1,
                            daysInLastMonth - firstWeekDay + c + 1);
                    if (withinDateRange(x)) {
                        grid.setWidget(r, c, createCell(r, c, x));
                        grid.getCellFormatter().addStyleName(r, c,
                                grid.getStyleName() + "-day-previous-month");

                        // Is the date selected?
                        if (dateIsSelected(x)) {
                            grid.getCellFormatter().addStyleName(r, c,
                                    grid.getStyleName() + "-selected");
                        }

                        // Is the date disabled?
                        if (dateIsDisabled(x)) {
                            grid.getCellFormatter().addStyleName(r, c,
                                    grid.getStyleName() + "-disabled");
                        }
                    }
                } else if (d <= daysInMonth) {
                    // This month
                    Date x = new Date(displayedYear - 1900, displayedMonth, d);
                    if (withinDateRange(x)) {
                        grid.setWidget(r, c, createCell(r, c, x));

                        // Style selected date
                        if (d == day) {
                            grid.getCellFormatter().addStyleName(r, c,
                                    grid.getStyleName() + "-day-selected");
                        }

                        // Add a stylename for todays date
                        if (displayedMonth - 1900 == today.getYear()
                                && displayedMonth == today.getMonth()
                                && d == today.getDate()) {
                            grid.getCellFormatter().addStyleName(r, c,
                                    grid.getStyleName() + "-day-today");
                        }

                        // Is the date selected?
                        if (dateIsSelected(x)) {
                            grid.getCellFormatter().addStyleName(r, c,
                                    grid.getStyleName() + "-selected");
                        }

                        // Is the date disabled?
                        if (dateIsDisabled(x)) {
                            grid.getCellFormatter().addStyleName(r, c,
                                    grid.getStyleName() + "-disabled");
                        }
                    }
                    d++;

                } else if (withinDateRange(new Date(displayedYear - 1900,
                        displayedMonth, daysInMonth))) {
                    // Next month
                    Date x;

                    if (displayedMonth == 11) {
                        x = new Date(displayedYear - 1900 + 1, 0, nextMonthDay);
                    } else {
                        x = new Date(displayedYear - 1900, displayedMonth + 1,
                                nextMonthDay);
                    }

                    if (withinDateRange(x)) {
                        grid.setWidget(r, c, createCell(r, c, x));
                        grid.getCellFormatter().addStyleName(r, c,
                                grid.getStyleName() + "-day-next-month");

                        // Is the date selected?
                        if (dateIsSelected(x)) {
                            grid.getCellFormatter().addStyleName(r, c,
                                    grid.getStyleName() + "-selected");
                        }

                        // Is the date disabled?
                        if (dateIsDisabled(x)) {
                            grid.getCellFormatter().addStyleName(r, c,
                                    grid.getStyleName() + "-disabled");
                        }
                    }

                    nextMonthDay++;
                }
            }
        }
    }

    private static int getDaysInMonth(int year, int month) {
        return 32 - new Date(year, month, 32).getDate();
    }

    public void setMultiSelect(boolean enabled) {
        this.isMultiSelect = enabled;
    }

    public boolean isMultiSelect() {
        return this.isMultiSelect;
    }

    public void setStartDate(Date date) {
        this.startDate = date;
        updateUI();
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setEndDate(Date date) {
        this.endDate = date;
        updateUI();
    }

    public Date getEndDate(Date date) {
        return this.endDate;
    }

    /**
     * Weekdays which should be disabled (0-6) where 0=Sunday
     * 
     * @param days
     */
    public void setDisabledWeekDays(int... days) {
        this.disabledWeekdays = days;
        updateUI();
    }

    /**
     * Dates which should be disabled each month (1-31)
     * 
     * @param dates
     */
    public void setDisabledDates(int... dates) {
        this.disabledMonthDates = dates;
        updateUI();
    }

    /**
     * Specific disabled dates
     * 
     * @param dates
     */
    public void setDisabledDates(Date... dates) {
        this.disabledDates = dates;
        updateUI();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.FocusHandler#onFocus(com.google.gwt.event
     * .dom.client.FocusEvent)
     */
    public void onFocus(FocusEvent event) {
        addStyleDependentName("focused");
        if (focusedCell != null) {
            setKeyboardFocus(focusedCell.getRow(), focusedCell.getColumn());
        } else {
            setKeyboardFocus(1);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.BlurHandler#onBlur(com.google.gwt.event
     * .dom.client.BlurEvent)
     */
    public void onBlur(BlurEvent event) {
        removeStyleDependentName("focused");
        if (focusedCell != null) {
            grid.getCellFormatter().removeStyleName(focusedCell.getRow(),
                    focusedCell.getColumn(), "focus");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.KeyDownHandler#onKeyDown(com.google.gwt
     * .event.dom.client.KeyDownEvent)
     */
    public void onKeyDown(KeyDownEvent event) {
        switch (event.getNativeKeyCode()) {
        case KeyCodes.KEY_LEFT: {
            if (event.isControlKeyDown() && event.isShiftKeyDown()) {
                previousYear();
            } else if (event.isShiftKeyDown()) {
                previousMonth();
            } else {
                previousDay();
            }
            event.preventDefault();
        }
            break;
        case KeyCodes.KEY_RIGHT: {
            if (event.isControlKeyDown() && event.isShiftKeyDown()) {
                nextYear();
            } else if (event.isShiftKeyDown()) {
                nextMonth();
            } else {
                nextDay();
            }
            event.preventDefault();
        }
            break;
        case KeyCodes.KEY_UP: {
            if (event.isControlKeyDown() && event.isShiftKeyDown()) {
                previousYear();
            } else if (event.isShiftKeyDown()) {
                previousMonth();
            } else {
                previousWeek();
            }
            event.preventDefault();
        }
            break;
        case KeyCodes.KEY_DOWN: {
            if (event.isControlKeyDown() && event.isShiftKeyDown()) {
                nextYear();
            } else if (event.isShiftKeyDown()) {
                nextMonth();
            } else {
                nextWeek();
            }
            event.preventDefault();
        }
            break;
        case KeyCodes.KEY_ENTER:
        case 32: { // SPACE
            if (focusedCell != null) {
                select(focusedCell, event.isControlKeyDown(),
                        event.isShiftKeyDown());
                event.preventDefault();
            }
        }
            break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.MouseDownHandler#onMouseDown(com.google
     * .gwt.event.dom.client.MouseDownEvent)
     */
    public void onMouseDown(MouseDownEvent event) {
        // Prevent browser default behavior when clicking on the grid
        event.preventDefault();
        setFocus(true);
    }

    /**
     * Returns the selected dates, see also {@link #getSelectedDateRange()}
     * 
     * @return
     */
    public Set<Date> getSelectedDates() {
        return selected;
    }

    /**
     * Select a new date in the calendar
     * 
     * @param date
     * 		The date to select
     * @param fireValueChangeEvent
     * 		Should a valuechange event be fired whne the value changes
     */
    public void select(Date date, boolean fireValueChangeEvent) {
        Date d = (Date) date.clone();
        d.setHours(0);
        d.setMinutes(0);
        d.setSeconds(0);
        selected.add(d);
        lastSelection = d;
        updateUI();
        if (fireValueChangeEvent) {
            fireValueChangeEvent();
        }
    }

    /**
     * Select a date in the panel. The dates
     * 
     * @param date
     *            The date to select
     */
    public void select(Date date) {
        select(date, true);
    }

    protected void unselect(Date date, boolean fireValueChangeEvent) {
        Set<Date> removed = new HashSet<Date>();
        for (Date d : selected) {
            if (GwtDateUtil.dateEqualResolutionDay(d, date)) {
                removed.add(d);
            }
        }

        if (!removed.isEmpty()) {
            selected.removeAll(removed);
            updateUI();
            if (fireValueChangeEvent) {
                fireValueChangeEvent();
            }
        }
    }

    /**
     * Remove a selected date from the panel
     * 
     * @param date
     *            The date to remove
     */
    public void unselect(Date date) {
        unselect(date, true);
    }

    /**
     * Remove all selections
     */
    public void unselectAll() {
        selected.clear();
        updateUI();
        fireValueChangeEvent();
    }

    /**
     * Fires a value change event with the current selection
     */
    protected void fireValueChangeEvent() {
        for (DateValueChangeListener listener : valueChangeListeners) {
            listener.valueChange(this, selected);
        }
    }

    /**
     * Adds a value change listener which listens to when the date selection
     * changes
     * 
     * @param listener
     *            The listener to add
     */
    public void addListener(DateValueChangeListener listener) {
        valueChangeListeners.add(listener);
    }

    /**
     * Removes a listener
     * 
     * @param listener
     */
    public void removeListener(DateValueChangeListener listener) {
        valueChangeListeners.remove(listener);
    }

    /**
     * The format of the date in the tooltip when hoovering over a date
     * 
     * @param format
     *            A pattern used by SimpleDateFormat
     */
    public void setDescriptionDateFormat(String format) {
        descriptionDateFormat = format;
    }

    /**
     * The format of the date in the tooltip when hoovering over a date
     */
    public String getDescriptionDateFormat() {
        return descriptionDateFormat;
    }

    /**
     * Opens the specific month and year and places the keyboard focus on that
     * day
     */
    public void showDate(Date d) {
        updateUI(d.getYear() + 1900, d.getMonth(), d.getDate());
    }
}
