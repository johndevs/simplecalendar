package fi.jasoft.simplecalendar.client.ui;

import com.vaadin.client.ui.LegacyConnector;
import com.vaadin.shared.ui.Connect;

import fi.jasoft.simplecalendar.SimpleCalendar;

@Connect(SimpleCalendar.class)
public class SimpleCalendarConnector extends LegacyConnector {

    @Override
    public SimpleCalendarWidgetConnector getWidget() {
        return (SimpleCalendarWidgetConnector) super.getWidget();
    }

}
