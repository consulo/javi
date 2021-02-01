import java.time.DayOfWeek;

public class SwitchYieldSource {
    public static void main(String[] args) {
        DayOfWeek day = DayOfWeek.FRIDAY;

        switch (day) {
            case MONDAY, FRIDAY, SUNDAY ->System.out.println(6);
            case TUESDAY                ->System.out.println(7);
            case THURSDAY, SATURDAY     ->System.out.println(8);
            case WEDNESDAY              ->System.out.println(9);
        }
    }
}