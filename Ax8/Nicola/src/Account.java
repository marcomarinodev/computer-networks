import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Account {
    private String name;
    private List<Movement> movements;

    public Account(String name) throws ParseException {
        this.name = name;
        this.movements = new ArrayList<>();

        int nMovements = 1 + new Random().nextInt(500);
        // Generate random movements
        for (int i=0; i<nMovements; i++) {
            movements.add(new Movement());
        }
    }

    public void putOccurrences(ConcurrentHashMap<String, Integer> map) {
        for (int i=0; i<movements.size(); i++) {
            String currKey = movements.get(i).getCausal();

            if (!map.containsKey(currKey)) {
                map.put(currKey, 0);
            }
            map.put(currKey, map.get(currKey) + 1);
        }
    }

    public String getName() {
        return name;
    }
}
