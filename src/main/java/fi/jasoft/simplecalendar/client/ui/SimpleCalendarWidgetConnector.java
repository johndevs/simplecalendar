package fi.jasoft.simplecalendar.client.ui;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Focusable;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;

import fi.jasoft.simplecalendar.SimpleCalendar;
import fi.jasoft.simplecalendar.client.ui.gwt.DateValueChangeListener;
import fi.jasoft.simplecalendar.client.ui.gwt.SimpleCalendarWidget;

/**
 * Vaadin glue class to combine {@link SimpleCalendarWidget} with {@link SimpleCalendar}
 * 
 * @author John Ahlroos (www.jasoft.fi)
 * 
 */
public class SimpleCalendarWidgetConnector extends SimpleCalendarWidget implements Paintable,
        Focusable, DateValueChangeListener {

    /** The client side widget identifier */
    protected String paintableId;

    /** Reference to the server connection object. */
    protected ApplicationConnection client;

    /**
     * Constructor
     */
    public SimpleCalendarWidgetConnector() {
        addListener(this);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.Paintable#updateFromUIDL(com.vaadin.terminal
     * .gwt.client.UIDL, com.vaadin.terminal.gwt.client.ApplicationConnection)
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }
        this.client = client;
        paintableId = uidl.getId();

        // Multiselect mode
        setMultiSelect(uidl.getBooleanAttribute("multiselect"));

        if (uidl.hasVariable("selection")) {
            // Set the updated selections
            Set<Date> selected = new HashSet<Date>();
            for (String time : uidl.getStringArrayVariable("selection")) {
                Date date = new Date(Long.valueOf(time));
                selected.add(date);
            }

            Set<Date> current = getSelectedDates();

            // Deselect old selections
            for (Date d : current) {
                if (!selected.contains(d)) {
                    unselect(d, false);
                }
            }

            // Select new selections
            for (Date d : selected) {
                if (!current.contains(d)) {
                    select(d, false);
                }
            }

        } else {
            for (Date d : getSelectedDates()) {
                unselect(d, false);
            }
        }

        if (uidl.hasAttribute("disabledWeekdays")) {
            int[] days = uidl.getIntArrayAttribute("disabledWeekdays");
            setDisabledWeekDays(days);
        } else {
            setDisabledWeekDays(null);
        }

        if (uidl.hasAttribute("disabledMonthdays")) {
            int[] days = uidl.getIntArrayAttribute("disabledMonthdays");
            setDisabledDates(days);
        } else {
            setDisabledDates((int[]) null);
        }

        if (uidl.hasVariable("disabledDates")) {
            String[] disabledStr = uidl.getStringArrayVariable("disabledDates");
            Set<Date> disabled = new HashSet<Date>();
            for (String time : disabledStr) {
                disabled.add(new Date(Long.parseLong(time)));
            }
            setDisabledDates(disabled.toArray(new Date[disabled.size()]));
        } else {
            setDisabledDates((Date) null);
        }

        if (uidl.hasAttribute("startDate")) {
            Long time = uidl.getLongAttribute("startDate");
            if (time >= 0) {
                setStartDate(new Date(time));
            } else {
                setStartDate(null);
            }
        } else {
            setStartDate(null);
        }

        if (uidl.hasAttribute("endDate")) {
            Long time = uidl.getLongAttribute("endDate");
            if (time >= 0) {
                setEndDate(new Date(time));
            } else {
                setEndDate(null);
            }
        } else {
            setEndDate(null);
        }
    }

    /**
     * Focuses the DatePanel
     */
    public void focus() {
        setFocus(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fi.jasoft.timepack.demo.client.ui.gwt.DateValueChangeListener#valueChange
     * ( com.google.gwt.user.client.ui.Widget, java.util.Set)
     */
    public void valueChange(Widget target, Set<Date> dates) {
        Set<Date> selection = getSelectedDates();

        String[] selectionStr = new String[selection.size()];
        Iterator<Date> selectionIter = selection.iterator();
        int count = 0;
        while (selectionIter.hasNext()) {
            Date d = selectionIter.next();
            selectionStr[count] = String.valueOf(d.getTime());
            ++count;
        }

        client.updateVariable(paintableId, "selection", selectionStr, true);
    }
}
