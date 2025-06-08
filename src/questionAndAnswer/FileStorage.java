package questionAndAnswer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileStorage {
    private static final String FILE_PATH = "questions.dat";

    public static void save(QuestionList bank) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            out.writeObject(bank);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static QuestionList load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (QuestionList) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new QuestionList(); // return empty if not found
        }
    }
}