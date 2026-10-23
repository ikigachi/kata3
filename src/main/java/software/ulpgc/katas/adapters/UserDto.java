package software.ulpgc.katas.adapters;

import java.time.Instant;

public class UserDto {
    public String gender;
    public Name name;
    public Location location;
    public Dob dob;
    public String email;
    public String phone;
    public String cell;
    public Picture picture;

    public String name() {
        return name.first + " " + name.last;
    }

    public Instant birthday() {
        return Instant.parse(dob.date);
    }

    public static class Name {
        public String title;
        public String first;
        public String last;
    }

    public static class Location {
        public Street street;
        public String city;
        public String state;
        public String country;
        public String postcode;
    }

    public static class Street {
        public int number;
        public String name;
    }

    public static class Dob {
        public String date;
        public int age;
    }

    public static class Picture {
        public String large;
        public String medium;
    }
}
