package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author ant
 * Create by Ant on 2021/7/31 2:19 PM
 */
public class Main {
    public static void main(String[] args) throws IOException {
        final var filePath = Files.createFile(Paths.get("/users/ant/zip.txt"));
        final var file = filePath.toFile();
        System.out.println(file.exists());
    }
}
