import java.util.concurrent.atomic.AtomicInteger;

public class Counter implements Runnable {

    private final AtomicInteger bankTransferCount;
    private final AtomicInteger creditCount;
    private final AtomicInteger bulletinCount;
    private final AtomicInteger f24Count;
    private final AtomicInteger atmCount;
    private final BankAccount account;

    Counter(AtomicInteger bankTransferCount, AtomicInteger creditCount, AtomicInteger bulletinCount,
            AtomicInteger f24Count, AtomicInteger atmCount, BankAccount account) {

        this.bankTransferCount = bankTransferCount;
        this.creditCount = creditCount;
        this.bulletinCount = bulletinCount;
        this.f24Count = f24Count;
        this.atmCount = atmCount;
        this.account = account;

    }

    @Override
    public void run() {

        for (FinancialRecord record : this.account.getList()) {

            switch (record.getPp()) {
                case BankTransfer -> bankTransferCount.incrementAndGet();
                case Credit -> creditCount.incrementAndGet();
                case Bulletin -> bulletinCount.incrementAndGet();
                case F24 -> f24Count.incrementAndGet();
                case ATM -> atmCount.incrementAndGet();
            }

        }

    }
}
