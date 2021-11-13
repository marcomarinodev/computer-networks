import java.util.ArrayList;
import java.util.List;

public class BankAccount {

    private final String name;
    private final List<FinancialRecord> list = new ArrayList<>();

    public BankAccount(String name) {

        this.name = name;

    }

    public void addFinancialRecord(FinancialRecord record) {

        list.add(record);

    }

    public String getName() {

        return name;

    }

    public List<FinancialRecord> getList() {

        return list;

    }
}
