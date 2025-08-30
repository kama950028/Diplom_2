package praktikum.tests.stellarburgers.util;


import com.github.javafaker.Faker;

public class Data {
    private static final Faker FAKER = new Faker();

    public static String email()   { return "aqa_"+System.currentTimeMillis()+"_"+FAKER.bothify("????##")+"@mail.test"; }
    public static String pass()    { return FAKER.bothify("P@ssw0rd##??"); }
    public static String name()    { return FAKER.name().firstName(); }
}
