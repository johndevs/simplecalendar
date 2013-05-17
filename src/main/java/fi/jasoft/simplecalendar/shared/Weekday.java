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

import java.util.Calendar;

public enum Weekday {
	SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;
	
	public static Weekday parseFromInteger(int weekday){
		switch(weekday){
			case Calendar.SUNDAY: return Weekday.SUNDAY;
			case Calendar.MONDAY: return Weekday.MONDAY;
			case Calendar.TUESDAY: return Weekday.TUESDAY;
			case Calendar.WEDNESDAY: return Weekday.WEDNESDAY;
			case Calendar.THURSDAY: return Weekday.THURSDAY;
			case Calendar.FRIDAY: return Weekday.FRIDAY;
			case Calendar.SATURDAY: return Weekday.SATURDAY;
		}
		return null;
	}
	
	public static int toInteger(Weekday day){
		if(day == SUNDAY) return Calendar.SUNDAY;
		else if(day == MONDAY) return Calendar.MONDAY;
		else if(day == TUESDAY) return Calendar.TUESDAY;
		else if(day == WEDNESDAY) return Calendar.WEDNESDAY;
		else if(day == THURSDAY) return Calendar.THURSDAY;
		else if(day == FRIDAY) return Calendar.FRIDAY;
		else if(day == SATURDAY) return Calendar.SATURDAY;
		else return -1;
	}
}
