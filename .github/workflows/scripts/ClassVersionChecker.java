import java.io.*;
import java.util.*;

public class ClassVersionChecker {

    private static final HashMap<String, Integer> majorCodeMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new RuntimeException("Expected exactly 2 arguments");
        }

        //Adapted from https://docs.oracle.com/javase/specs/jvms/se20/html/jvms-4.html#jvms-4.1
        majorCodeMap.put("1.0.2", 45);
        majorCodeMap.put("1.1", 45);
        majorCodeMap.put("1.2", 46);
        majorCodeMap.put("1.3", 47);
        majorCodeMap.put("1.4", 48);
        majorCodeMap.put("5.0", 49);
        majorCodeMap.put("6", 50);
        majorCodeMap.put("7", 51);
        majorCodeMap.put("8", 52);
        majorCodeMap.put("9", 53);
        majorCodeMap.put("10", 54);
        majorCodeMap.put("11", 55);
        majorCodeMap.put("12", 56);
        majorCodeMap.put("13", 57);
        majorCodeMap.put("14", 58);
        majorCodeMap.put("15", 59);
        majorCodeMap.put("16", 60);
        majorCodeMap.put("17", 61);
        majorCodeMap.put("18", 62);
        majorCodeMap.put("19", 63);
        majorCodeMap.put("20", 64);
        majorCodeMap.put("21", 65);
        majorCodeMap.put("22", 66);
        majorCodeMap.put("23", 67);

        String filename = args[0];
        int expected = majorCodeMap.get(args[1]);

        checkClassVersion(filename, expected);
    }

    private static void checkClassVersion(String filename, int expected) throws IOException {
        try ( DataInputStream in = new DataInputStream(new FileInputStream(filename)) ) {
            int magic = in.readInt();

            if(magic != 0xcafebabe) {
                System.out.println(filename + " is not a valid class!");
            }

            int minor = in.readUnsignedShort();
            int major = in.readUnsignedShort();

            System.out.println("Major version: " + major);
            System.out.println("Minor version: " + minor);

            if(expected != major) {
                System.out.println("Expected major version of " + expected + " but got " + major);
                System.exit(1); //Failing execution
            }
        }
    }
}