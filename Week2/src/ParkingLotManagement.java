import java.util.*;

class Spot {
    String plate;
    long entryTime;

    Spot(String plate) {
        this.plate = plate;
        this.entryTime = System.currentTimeMillis();
    }
}

public class ParkingLotManagement {

    private static final int SIZE = 500;
    private static final double RATE_PER_HOUR = 5.5;

    private Spot[] table = new Spot[SIZE];

    private int occupied = 0;
    private int totalProbes = 0;
    private int operations = 0;

    private Map<Integer, Integer> hourlyUsage = new HashMap<>();

    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % SIZE;
    }

    public void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index] != null) {
            index = (index + 1) % SIZE;
            probes++;
        }

        table[index] = new Spot(plate);
        occupied++;

        totalProbes += probes;
        operations++;

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        hourlyUsage.put(hour, hourlyUsage.getOrDefault(hour, 0) + 1);

        System.out.println("parkVehicle(\"" + plate + "\") → Assigned spot #"
                + index + " (" + probes + " probes)");
    }

    public void exitVehicle(String plate) {

        int index = hash(plate);

        while (table[index] != null) {

            if (table[index].plate.equals(plate)) {

                long now = System.currentTimeMillis();
                long durationMillis = now - table[index].entryTime;

                double hours = durationMillis / (1000.0 * 60 * 60);
                double fee = hours * RATE_PER_HOUR;

                occupied--;
                table[index] = null;

                System.out.printf(
                        "exitVehicle(\"%s\") → Spot #%d freed, Duration: %.2fh, Fee: $%.2f\n",
                        plate, index, hours, fee
                );

                return;
            }

            index = (index + 1) % SIZE;
        }

        System.out.println("Vehicle not found.");
    }

    public void getStatistics() {

        double occupancy = ((double) occupied / SIZE) * 100;
        double avgProbes = operations == 0 ? 0 : (double) totalProbes / operations;

        int peakHour = -1;
        int max = 0;

        for (int h : hourlyUsage.keySet()) {
            if (hourlyUsage.get(h) > max) {
                max = hourlyUsage.get(h);
                peakHour = h;
            }
        }

        System.out.printf(
                "getStatistics() → Occupancy: %.0f%%, Avg Probes: %.2f, Peak Hour: %d-%d\n",
                occupancy, avgProbes, peakHour, peakHour + 1
        );
    }

    public static void main(String[] args) {

        ParkingLotManagement lot = new ParkingLotManagement();

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}