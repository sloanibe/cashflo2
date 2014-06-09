package monk.cashflo;

import javax.servlet.annotation.WebServlet;

import com.db4o.ObjectSet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.Action;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import com.vaadin.ui.components.calendar.CalendarDateRange;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import org.apache.commons.lang3.time.DateUtils;

import java.util.*;


@Theme("mytheme")
@SuppressWarnings("serial")
public class MyVaadinUI extends UI {

    private Calendar calendar = new Calendar("My Calendar");

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class, widgetset = "monk.cashflo.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        System.out.println("In init");


        ObjectSet<CashFloDay> allCashFloData = DB.getAllCashFloData();
        if (allCashFloData.size() == 0) {
            DB.initdb(2014);

        }

        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        //Calendar calendar = new Calendar("My Calendar");
        calendar.setWidth("600px");  // Undefined by default
        calendar.setHeight("400px"); // Undefined by default

        // Use US English for date/time representation
        calendar.setLocale(new Locale("en", "US"));

        // Set start date to first date in this month
        GregorianCalendar startDate = new GregorianCalendar();
        startDate.set(2014, 0, 1);
        calendar.setStartDate(DateUtils.truncate(startDate.getTime(), java.util.Calendar.DAY_OF_MONTH));

        // Set end date to last day of this month
        GregorianCalendar endDate = new GregorianCalendar();
        endDate.set(2014, 0, 31);
        calendar.setEndDate(DateUtils.truncate(endDate.getTime(), java.util.Calendar.DAY_OF_MONTH));

        updateCalendarDisplay();

        calendar.addListener(e -> {
            System.out.println("in listener");
        });


        calendar.setHandler(new CalendarComponentEvents.EventClickHandler() {
            @Override
            public void eventClick(CalendarComponentEvents.EventClick eventClick) {

            }
        });

        calendar.setHandler(new CalendarComponentEvents.DateClickHandler() {
            @Override
            public void dateClick(CalendarComponentEvents.DateClickEvent dateClickEvent) {
                /*ObjectContainer db = DB.getDB();

                ObjectSet<Income> result = db.queryByExample(Income.class);
                for (Income income : result) {
                    System.out.println("Income:" + income.amt);
                }
                db.close();*/

            }

        });


        //button.addClickListener(e -> layout.addComponent(new Label("Thank you for clicking6" + ts.sayHello())));
        //layout.addComponent(button);
        calendar.setSizeFull();
        calendar.addActionHandler(actionHandler);
        layout.addComponent(calendar);
    }

    public void updateCalendarDisplay() {
        // Set start date to first date in this month
        GregorianCalendar startDate = new GregorianCalendar();
        startDate.set(2014, 0, 1, 0, 0, 0);

        calendar.setStartDate(startDate.getTime());

        // Set end date to last day of this month
        GregorianCalendar endDate = new GregorianCalendar();
        endDate.set(2014, 0, 31, 0, 0, 0);
        calendar.setEndDate(endDate.getTime());


        /*List<CalendarEvent> events = calendar.getEvents(DateUtils.truncate(startDate.getTime(), java.util.Calendar.DAY_OF_MONTH), DateUtils.truncate(endDate.getTime(), java.util.Calendar.DAY_OF_MONTH));
        for(CalendarEvent event : events) {
            calendar.removeEvent(event);
        }*/
        java.util.Calendar iterDate = startDate;
        while (!iterDate.getTime().equals(endDate.getTime())) {

            //ObjectContainer db = DB.getDB();
            List<CashFloDay> cfds = DB.getCashFloDay(iterDate);
            List<CalendarEvent> events = calendar.getEvents(DateUtils.truncate(iterDate.getTime(), java.util.Calendar.DAY_OF_MONTH), DateUtils.truncate(iterDate.getTime(), java.util.Calendar.DAY_OF_MONTH));
            BasicEvent be;
            if(events.size()==0) {
                be = new BasicEvent();
            } else {
                be = (BasicEvent) events.get(0);
            }

            //BasicEvent be = new BasicEvent();
            be.setAllDay(true);
            be.setStart(cfds.get(0).getDay());
            be.setEnd(cfds.get(0).getDay());
            be.setCaption("Bal:" + cfds.get(0).getStartAmt());
            calendar.addEvent(be);

            BasicEvent incomeEvent = new BasicEvent();
            Double total = getTotalIncome(cfds.get(0).getIncome());
            incomeEvent.setAllDay(true);
            incomeEvent.setStart(cfds.get(0).getDay());
            incomeEvent.setEnd(cfds.get(0).getDay());
            incomeEvent.setCaption("Income:" + total);
            calendar.addEvent(incomeEvent);



            iterDate.add(GregorianCalendar.DAY_OF_MONTH, 1);
        }
    }

    private Double getTotalIncome(ArrayList<Income> incomes) {
        Double total = 0.0;
        for(Income income : incomes) {
            total += income.getAmt();
        }
        return total;
    }


    Action.Handler actionHandler = new Action.Handler() {
        private static final long serialVersionUID = -306196319123409692L;

        Action addEventAction = new Action("Add Income");
        Action deleteEventAction = new Action("Delete Event");

        @Override
        public Action[] getActions(Object target, Object sender) {
            // The target should be a CalendarDateRage for the
            // entire day from midnight to midnight.
            if (!(target instanceof CalendarDateRange))
                return null;
            CalendarDateRange dateRange = (CalendarDateRange) target;

            // The sender is the Calendar object
            if (!(sender instanceof Calendar))
                return null;
            Calendar calendar = (Calendar) sender;

            // List all the events on the requested day
            List<CalendarEvent> events =
                    calendar.getEvents(dateRange.getStart(),
                            dateRange.getEnd());

            // You can have some logic here, using the date
            // information.
            if (events.size() == 0)
                return new Action[]{addEventAction};
            else
                return new Action[]{addEventAction, deleteEventAction};
        }

        @Override
        public void handleAction(Action action, Object sender, Object target) {
            // The sender is the Calendar object
            Calendar calendar = (Calendar) sender;

            if (action == addEventAction) {
                // Check that the click was not done on an event
                if (target instanceof Date) {
                    IncomeDialog d = new IncomeDialog(calendar, (Date) target, MyVaadinUI.this);
                    UI.getCurrent().addWindow(d);
                } else
                    Notification.show("Can't add on an event");
            } else if (action == deleteEventAction) {
                // Check if the action was clicked on top of an event
                if (target instanceof CalendarEvent) {
                    CalendarEvent event = (CalendarEvent) target;
                    calendar.removeEvent(event);
                } else
                    Notification.show("No event to delete");
            }
        }
    };


}
