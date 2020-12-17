import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Synonym {
    public static void getSynonyms() throws IOException {
        Word2Vec w2vModel = WordVectorSerializer.readWord2VecModel("data/model.bin");
        List<Collection<String>> result = new ArrayList<>();
        List<String> words = Files.readAllLines(Paths.get("data/words.txt"));
        for (String word : words) {
            Collection<String> synonyms = w2vModel.wordsNearest(word, Constants.SYNONYMS_COUNT);
            if (!(synonyms == null || synonyms.isEmpty())) {
                synonyms.add(word);
                result.add(synonyms);
            }
        }
        writeToFileByLine(result, "data/synonyms.txt");
    }

    private static void writeToFileByLine(List<Collection<String>> lines, String wordsPath) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(wordsPath))))) {
            for (Collection<String> line : lines) {
                String newLine = String.join(",", line);
                writer.write(newLine);
                writer.newLine();
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

} 