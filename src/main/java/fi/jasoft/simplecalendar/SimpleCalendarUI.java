package fi.jasoft.simplecalendar;

import java.util.Date;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

import fi.jasoft.simplecalendar.shared.Weekday;

public class SimpleCalendarUI extends UI {

    @Override
    protected void init(VaadinRequest request) {

        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        content.setSpacing(true);

        Label lbl = new Label("SimpleCalendar");
        lbl.setStyleName(Reindeer.LABEL_H1);
        content.addComponent(lbl);

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");
        layout.setSpacing(true);

        final SimpleCalendar calendar = new SimpleCalendar();
        calendar.setImmediate(true);
        calendar.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                Notification.show(calendar.getValue().toString());
            }
        });

        calendar.setMultiSelect(true);

        layout.addComponent(calendar);

        Label description = new Label(
                "A calendar component that supports the following features:<ul>"
                        + "<li>Selecting multiple dates by control click</li>"
                        + "<li>Range selection with shift click</li>"
                        + "<li>Keyboard navigation with arrow keys</li>"
                        + "<li>Possibility to disable certain dates</li>"
                        + "<li>Date tooltips</li>"
                        + "<li>Limit start and end dates of calendar</li>",
                ContentMode.HTML);
        layout.addComponent(description);
        layout.setExpandRatio(description, 1);

        content.addComponent(layout);

        final CheckBox disableWeekends = new CheckBox("Disable weekends", false);
        disableWeekends.setImmediate(true);
        disableWeekends
                .addValueChangeListener(new Property.ValueChangeListener() {

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        if (disableWeekends.getValue()) {
                            calendar.setDisabledWeekDays(Weekday.SATURDAY,
                                    Weekday.SUNDAY);
                        } else {
                            calendar.setDisabledWeekDays();
                        }
                    }
                });
        content.addComponent(disableWeekends);

        final CheckBox disable15th = new CheckBox("Disable 15th each month",
                false);
        disable15th.setImmediate(true);
        disable15th.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (disable15th.getValue()) {
                    calendar.setDisabledMonthlyDates(15);
                } else {
                    calendar.setDisabledMonthlyDates();
                }
            }
        });
        content.addComponent(disable15th);

        final CheckBox disableToday = new CheckBox("Disable todays date", false);
        disableToday.setImmediate(true);
        disableToday.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (disableToday.getValue()) {
                    calendar.setDisabledDates(new Date());
                } else {
                    calendar.setDisabledDates();
                }
            }
        });
        content.addComponent(disableToday);

        final CheckBox allowFutureDate = new CheckBox(
                "Only allow future dates", false);
        allowFutureDate.setImmediate(true);
        allowFutureDate
                .addValueChangeListener(new Property.ValueChangeListener() {

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        if (allowFutureDate.getValue()) {
                            calendar.setStartDate(new Date());
                        } else {
                            calendar.setStartDate(null);
                        }
                    }
                });
        content.addComponent(allowFutureDate);

        final CheckBox allowPastDate = new CheckBox("Only allow past dates",
                false);
        allowPastDate.setImmediate(true);
        allowPastDate
                .addValueChangeListener(new Property.ValueChangeListener() {

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        if (allowPastDate.getValue()) {
                            calendar.setEndDate(new Date());
                        } else {
                            calendar.setEndDate(null);
                        }
                    }
                });
        content.addComponent(allowPastDate);

        setContent(content);
    }
}
