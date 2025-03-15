package utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class FileUtils {
    public static String readFileToString(String filePath) throws FileNotFoundException {
        StringBuilder content = new StringBuilder();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
        }
        return content.toString();
    }
}