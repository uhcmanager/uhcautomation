import lombok.Data;
import org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;
import java.util.Random;

public class Playground {
    public static void main(String[] args)
    {
        ArrayList<Person> people = new ArrayList<Person>();
        int numPeople = 1000;
        for (int i = 0; i < numPeople; i++) {
            people.add(new Person());
        }

        long start = System.nanoTime();
        int copies = 10000;
        for (int i  = 0; i < copies; i++) {
            ArrayList<Person> temp = new ArrayList<Person>(people);
        }
        long elapsedNS = System.nanoTime() - start;
        System.out.println("Time to create " + copies + " copies: " + elapsedNS + " nanoseconds (" + String.format("%.2f seconds)", elapsedNS/1e9));
    }

    @Data
    public static class Person {
        private String name;
        private String address;
        private int age;
        private long id;

        public Person() {
            Random rng = new Random();
            name = RandomStringUtils.random(rng.nextInt(5) + 5, true, false);
            address =  RandomStringUtils.random(rng.nextInt(25) + 10, true, true);
            age = rng.nextInt(100-18) + 18;
            id = rng.nextLong();
        }
    }
}
