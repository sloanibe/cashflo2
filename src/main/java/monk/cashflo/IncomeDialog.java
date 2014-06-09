package monk.cashflo;

import com.vaadin.ui.*;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by marksloan on 5/27/14.
 */
public class IncomeDialog extends Window {

    private MyVaadinUI ui;

    public IncomeDialog(Calendar cal, Date date, MyVaadinUI ui) {

        super("Add Income");
        center();
        this.ui = ui;

        VerticalLayout vContent = new VerticalLayout();
        vContent.setMargin(true);

        HorizontalLayout hContent = new HorizontalLayout();
        TextField descrTxt = new TextField("Descr:");
        hContent.addComponent(descrTxt);

        TextField amtTxt = new TextField("Amt:");
        hContent.addComponent(amtTxt);

        vContent.addComponent(hContent);
        Button add = new Button("Add");
        add.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                addIncome(date,  descrTxt.getValue(), Double.valueOf(amtTxt.getValue()), cal);
                DB.updateBalances(date);
                IncomeDialog.this.close();

            }
        });
        vContent.addComponent(add);
        setContent(vContent);
    }

    private void addIncome(Date date, String descr, Double amt, Calendar cal) {

        List<CashFloDay> cfds = DB.getCashFloDay(DateUtils.toCalendar(date));

        CashFloDay cfd = cfds.get(0);

        cfd.getIncome().add(new Income(date, amt, descr));
        DB.updateCashFloDay(cfd);
        ui.updateCalendarDisplay();
    }
}
