package ru.spbau.group202.cw1;

import javax.xml.bind.DatatypeConverter;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Incorrect usage; specify filepaths.");
            return;
        }

        MD5MultiThread concurrentHasher = new MD5MultiThread(4);
        MD5SingleThread regularHasher = new MD5SingleThread();
        for (String path : args) {
            System.out.println(path + ":");
            try {
                long start = System.currentTimeMillis();
                byte[] res = regularHasher.getHashFromPath(Paths.get(path));
                System.out.println(DatatypeConverter.printHexBinary(res));
                System.out.println("Single-thread computation: " + (System.currentTimeMillis() - start) + " ms.");

                start = System.currentTimeMillis();
                res = concurrentHasher.getHashFromPath(Paths.get(path));
                System.out.println(DatatypeConverter.printHexBinary(res));
                System.out.println("Multi-thread computation: " + (System.currentTimeMillis() - start) + " ms.");
            } catch (Exception e) {
                if (e.getMessage() != null) {
                    System.out.println(e.getMessage());
                } else {
                    System.out.println("Unable to compute md5 of this file");
                }
            }
        }
    }
}
