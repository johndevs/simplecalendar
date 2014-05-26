package fi.jasoft.simplecalendar.demo;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.vaadin.server.VaadinServlet;

@WebServlet(
    urlPatterns={"/*","/VAADIN/*"},
    initParams={
        @WebInitParam(name="ui", value="fi.jasoft.simplecalendar.demo.SimpleCalendarDemoUI"),
		@WebInitParam(name="widgetset", value="fi.jasoft.simplecalendar.demo.DemoWidgetset")
    })
public class SimpleCalendarDemoServlet extends VaadinServlet { }
