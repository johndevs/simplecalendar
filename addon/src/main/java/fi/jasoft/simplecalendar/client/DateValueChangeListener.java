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
