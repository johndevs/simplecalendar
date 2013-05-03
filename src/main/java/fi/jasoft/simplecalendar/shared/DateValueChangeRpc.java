package fi.jasoft.simplecalendar.shared;

import java.util.Date;
import java.util.Set;

import com.vaadin.shared.communication.ServerRpc;

public interface DateValueChangeRpc extends ServerRpc{

	public void selected(Set<Date> dates);	
}
