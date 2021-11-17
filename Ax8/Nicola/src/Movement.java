import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Movement {
    private Date date;
    private String causal;

    public static String[] causals = {"Bonifico", "Accredito", "Bollettino", "F24", "PagoBancomat"};

    public Movement() throws ParseException {
        this.date = new Date(
                ThreadLocalRandom.current().nextLong(
                        new SimpleDateFormat("dd-MM-yyyy").parse("15-11-2019").getTime(),
                        new SimpleDateFormat("dd-MM-yyyy").parse("15-11-2021").getTime()));
        this.causal = causals[Math.round(new Random().nextFloat() * 4)];
    }

    public Date getDate() {
        return date;
    }

    public String getCausal() {
        return causal;
    }
}
