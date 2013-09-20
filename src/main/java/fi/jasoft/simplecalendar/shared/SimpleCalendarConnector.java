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
import fi.jasoft.simplecalendar.client.DateValueChangeListener;
import fi.jasoft.simplecalendar.client.SimpleCalendarWidget;

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
		
		if(stateChangeEvent.hasPropertyChanged("locale")){	
			getWidget().setLocale(getState().locale);	
		}
	}
}
