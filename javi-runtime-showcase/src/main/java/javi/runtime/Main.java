package javi.runtime;

import java.util.Objects;

/**
 * @author VISTALL
 * @since 02/02/2021
 */
public class Main {
    public static void main(String[] args) {
        TestRecord record = new TestRecord(1, Integer.MAX_VALUE);

        System.out.println(Objects.hash(record.x(), record.y()));
        System.out.println(record.hashCode());

        System.out.println(record.x());
        System.out.println(record.y());
        
        System.out.println(record.toString());

        System.out.println(record.equals(null));
        System.out.println(record.equals(record));
        System.out.println(record.equals(new TestRecord(1, 1)));
        System.out.println(record.equals(new TestRecord(1, Integer.MAX_VALUE)));
        System.out.println(record.equals(new TestRecord(2, Integer.MAX_VALUE)));
    }
}
