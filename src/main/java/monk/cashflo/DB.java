package monk.cashflo;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Predicate;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by marksloan on 5/28/14.
 */
public class DB {

    private static ObjectContainer db;

    static {
        EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
        config.common().objectClass(CashFloDay.class).cascadeOnUpdate(true);

        db = Db4oEmbedded.openFile(config, "C:\\Users\\mark\\cashflodata");

    }

    private static ObjectContainer getDB() {
        return db;
    }

    public static ObjectSet<CashFloDay> getAllCashFloData() {
        ObjectSet<CashFloDay> result = null;
        ObjectContainer db = null;
        try {
            db = DB.getDB();
            result = db.queryByExample(CashFloDay.class);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //db.close();
        }
        return result;
    }

    public static void close(ObjectContainer db) {
        db.close();
    }


    public static List<CashFloDay> getCashFloDay(final Calendar cal) {
        ObjectContainer db = null;
        List<CashFloDay> cfds = null;
        try {
            db = DB.getDB();
            cfds = db.query(new Predicate<CashFloDay>() {
                public boolean match(CashFloDay cfd) {
                    return DateUtils.truncatedEquals(cfd.getDay(), cal.getTime(), GregorianCalendar.DAY_OF_MONTH);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //db.close();
        }

        return cfds;
    }

    public static void updateCashFloDay(CashFloDay day) {
        ObjectContainer db = null;
        try {
          db = getDB();
          db.store(day);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //db.close();
        }
    }

    public static void initdb(int year) {
        ObjectContainer db = null;
        try {
            db = getDB();
            java.util.Calendar startCal = new GregorianCalendar(2014, 0, 1);
            int day = 1;
            while (day <= 365) {
                CashFloDay cfd = new CashFloDay(DateUtils.truncate(startCal.getTime(), Calendar.DAY_OF_MONTH));
                db.store(cfd);
                startCal.add(GregorianCalendar.DAY_OF_MONTH, 1);
                day++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //db.close();
        }
    }


    public static void updateBalances(Date startDate) {
        final java.util.Calendar startCal = DateUtils.toCalendar(startDate);
        ObjectContainer db = DB.getDB();
        int day = 1;
        Double endBal = null;
        try {
            while (day <= 365) {

                List<CashFloDay> cfds = db.query(new Predicate<CashFloDay>() {
                    public boolean match(CashFloDay cfd) {
                        return DateUtils.truncatedEquals(cfd.getDay(), startCal.getTime(), GregorianCalendar.DAY_OF_MONTH);
                    }
                });
                System.out.println("in updating balances");
                if(cfds == null ||cfds.size() ==0) break;
                CashFloDay cfd = cfds.get(0);

                if (endBal != null) {
                    cfd.setStartAmt(endBal);
                    db.store(cfd);
                }
                List<Expense> expenses = cfd.getExpenses();
                Double expenseTotal = 0.0;
                for (Expense expense : expenses) {
                    expenseTotal += expense.getAmt();
                }
                List<Income> incomes = cfd.getIncome();
                Double incomeTotal = 0.0;
                for (Income income : incomes) {
                    incomeTotal += income.getAmt();
                }
                Double startBal = cfd.getStartAmt();
                endBal = startBal + (incomeTotal - expenseTotal);
                cfd.setEndAmt(endBal);
                db.store(cfd);
                startCal.add(GregorianCalendar.DAY_OF_MONTH, 1);
                day++;
            }
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //db.close();
        }
    }


}
