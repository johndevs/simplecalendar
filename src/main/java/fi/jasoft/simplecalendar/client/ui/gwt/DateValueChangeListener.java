package fi.jasoft.simplecalendar.client.ui.gwt;

import java.util.Date;
import java.util.Set;

import com.google.gwt.user.client.ui.Widget;

/**
 * Listener for listening to changes in date value
 * 
 * @author John Ahlroos (www.jasoft.fi)
 * 
 */
public interface DateValueChangeListener {

    /**
     * Triggered when the date value has changed
     * 
     * @param target
     *            The calendar widget
     * @param dates
     *            The selected dates
     */
    public void valueChange(Widget target, Set<Date> dates);
}
