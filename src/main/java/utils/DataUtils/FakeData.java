package utils.DataUtils;

import com.github.javafaker.Faker;

import java.util.Random;

public class FakeData {

    private static final Faker faker = new Faker();
    private static final Random random = new Random();
    static String[] vietnamPrefixes = {"032", "056", "078", "083", "093"};
    static String[] suffixes = {"1234", "4321"};
    private static final String specialCharacters = "[^a-zA-Z0-9]";
    static int min = 100;
    static int max = 1000;

    public static String getUsername() {
        return faker.name().username().replaceAll(specialCharacters, "") + random.nextInt((max - min) + 1) + min;
    }

    public static String getPassword() {
        return faker.internet().password();
    }

    public static String getPhoneNumber() {
        String prefix = vietnamPrefixes[random.nextInt(vietnamPrefixes.length)];
        String suffix = suffixes[random.nextInt(suffixes.length)];
        String digits = faker.number().digits(3);
        return prefix + digits + suffix;
    }
}
