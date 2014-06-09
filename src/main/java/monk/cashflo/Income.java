package monk.cashflo;

import java.util.Date;

/**
 * Created by marksloan on 5/27/14.
 */
public class Income {
    Date date;
    Double amt;
    String desc;

    public Income(Date date, Double amt, String desc) {
        this.date = date;
        this.amt = amt;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getAmt() {
        return amt;
    }

    public void setAmt(Double amt) {
        this.amt = amt;
    }
}
