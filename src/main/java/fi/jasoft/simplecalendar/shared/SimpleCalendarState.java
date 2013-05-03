package fi.jasoft.simplecalendar.shared;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.vaadin.shared.AbstractFieldState;


public class SimpleCalendarState extends AbstractFieldState {

	public boolean multiselect = false;

	public List<Weekday> disabledWeekdays;
    
	public List<Integer> disabledMonthdays;
    
	public List<Date> disabledDates;

	public Date startDate;
    
	public Date endDate;
}
