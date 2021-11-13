import java.time.LocalDateTime;

public class FinancialRecord {

    public enum PaymentPurpose {

        BankTransfer,
        Credit,
        Bulletin,
        F24,
        ATM;

        public static PaymentPurpose values(int purpose) {

            switch (purpose) {
                case 0 : return PaymentPurpose.BankTransfer;
                case 1 : return PaymentPurpose.Credit;
                case 2 : return PaymentPurpose.Bulletin;
                case 3 : return PaymentPurpose.F24;
                case 4 : return PaymentPurpose.ATM;
                default : break;
            }

            return null;

        }
    }

    private final PaymentPurpose pp;
    private final String date;

    FinancialRecord(PaymentPurpose pp, String date) {

        this.date = date;
        this.pp = pp;

    }

    public PaymentPurpose getPp() {

        return pp;

    }

    public String getDate() {

        return date;

    }

}
