package monk.cashflo;

/**
 * Created by marksloan on 5/27/14.
 */
public class Expense {
    private String item;
    private Double amt;

    public Expense(String item, Double amt) {
        this.item = item;
        this.amt = amt;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Double getAmt() {
        return amt;
    }

    public void setAmt(Double amt) {
        this.amt = amt;
    }
}
