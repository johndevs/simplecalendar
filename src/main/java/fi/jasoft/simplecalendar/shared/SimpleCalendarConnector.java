package fi.jasoft.simplecalendar.shared;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

import fi.jasoft.simplecalendar.SimpleCalendar;
import fi.jasoft.simplecalendar.client.ui.gwt.DateValueChangeListener;
import fi.jasoft.simplecalendar.client.ui.gwt.SimpleCalendarWidget;

@Connect(SimpleCalendar.class)
public class SimpleCalendarConnector extends AbstractComponentConnector {

	private final DateValueChangeRpc valueChangeRpc = RpcProxy.create(DateValueChangeRpc.class, this);
	
	/*
	 * (non-Javadoc)
	 * @see com.vaadin.client.ui.AbstractConnector#init()
	 */
	@Override
	protected void init() {		
		getWidget().addListener(new DateValueChangeListener() {
			
			@Override
			public void valueChange(Widget target, Set<Date> dates) {				
				valueChangeRpc.selected(dates);				
			}
		});		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.vaadin.client.ui.AbstractComponentConnector#getWidget()
	 */
	@Override
	public SimpleCalendarWidget getWidget() {
		return (SimpleCalendarWidget) super.getWidget();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.vaadin.client.ui.AbstractComponentConnector#getState()
	 */
	@Override
	public SimpleCalendarState getState() {
		return (SimpleCalendarState) super.getState();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.vaadin.client.ui.AbstractComponentConnector#onStateChanged(com.vaadin.client.communication.StateChangeEvent)
	 */
	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {		
		super.onStateChanged(stateChangeEvent);
		
		if(stateChangeEvent.hasPropertyChanged("multiselect")){
			getWidget().setMultiSelect(getState().multiselect);			
		}
		
		if(stateChangeEvent.hasPropertyChanged("startDate")){
			getWidget().setStartDate(getState().startDate);
		}
		
		if(stateChangeEvent.hasPropertyChanged("endDate")){
			getWidget().setEndDate(getState().endDate);
		}
		
		if(stateChangeEvent.hasPropertyChanged("disabledWeekdays")){
			List<Weekday> days = getState().disabledWeekdays;
			if(days == null || days.isEmpty()){
				getWidget().setDisabledWeekDays();
			} else {
				int[] daysArray = new int[days.size()];
				for(int i=0; i<days.size(); i++){
					daysArray[i] = days.get(i).ordinal();
				}
				getWidget().setDisabledWeekDays(daysArray);
				
			}			
		}
		
		if(stateChangeEvent.hasPropertyChanged("disabledMonthdays")){
			List<Integer> days = getState().disabledMonthdays;
			if(days == null || days.isEmpty()){
				getWidget().setDisabledDates((int[])null);
			} else {
				int[] daysArray = new int[days.size()];
				for(int i=0; i<days.size(); i++){
					daysArray[i] = days.get(i);
				}
				getWidget().setDisabledDates(daysArray);
			}
		}
		
		if(stateChangeEvent.hasPropertyChanged("disabledDates")){
			List<Date> days = getState().disabledDates;
			if(days == null || days.isEmpty()){
				getWidget().setDisabledDates((Date[])null);
			} else {
				Date[] daysArray = new Date[days.size()];
				for(int i=0; i<days.size(); i++){
					daysArray[i] = days.get(i);
				}
				getWidget().setDisabledDates(daysArray);
			}
		}		
	}

}
