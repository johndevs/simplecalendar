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
