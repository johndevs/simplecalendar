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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Utility class for widgets using dates
 * 
 * @author John Ahlroos (www.jasoft.fi)
 * 
 */
public class GwtDateUtil {

    /**
     * Increments a certain amount of days to a date.
     * 
     * @param date
     *            The date to start from
     * @param xdays
     *            The amount of days to increment to a date. Negative days
     *            counts backwards
     * @return The new date after incrementation
     */
    public static Date incrementDays(Date date, int xdays){
	    long time = date.getTime();
	    time = time + (xdays * 24 * 60 * 60 * 1000);

	    Date newDate = new Date(time);

	    Integer dateHour = new Integer(DateTimeFormat.getFormat("H").format(date));
	    Integer newDateHour = new Integer(DateTimeFormat.getFormat("H").format(newDate));
	    
	    if (!dateHour.equals(newDateHour)){
	    	if (dateHour > newDateHour || (dateHour.equals(0) && newDateHour.equals(23))){
			    time = time + (60 * 60 * 1000);
			    newDate.setTime(time);
	    	} else if (dateHour < newDateHour){
	    		time = time - (60 * 60 * 1000);
	    		newDate.setTime(time);
	    	}
	    }
	
	    return newDate;
    }
    
    /**
     * Returns a list of dates between two dates. Does not return the limits.
     * Dates have day resolution.
     * 
     * @param start
     *            The starting date
     * @param end
     *            The ending date
     * @return A list with all dates between the given start and end dates.
     */
    public static List<Date> getDatesBetween(Date start, Date end) {
        List<Date> dates = new ArrayList<Date>();
		Date current = start;
		while(current.before(end)){
			dates.add(current);
			current = incrementDays(current, 1);
		}
		dates.add(end);
		return dates;
	}
    
    /**
     * Checks if two dates are equal while ignoring the time values
     * 
     * @param d1
     *            First date to compare
     * @param d2
     *            Second date to compare
     * @return Are the two dates equals
     */
    public static boolean dateEqualResolutionDay(Date d1, Date d2){
    	if(d1.getYear() == d2.getYear() 
    			&& d1.getMonth() == d2.getMonth() 
    			&& d1.getDate() == d2.getDate()){
    		return true;
    	}
    	return false;
    }
}
