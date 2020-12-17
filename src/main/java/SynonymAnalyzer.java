import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.*;
import org.apache.lucene.util.CharsRef;

import java.util.List;

public class SynonymAnalyzer extends Analyzer {
    @Override
    protected Analyzer.TokenStreamComponents createComponents(String s) {
        SynonymMap synonymMap = null;
        SynonymMap.Builder builder=new SynonymMap.Builder(true);
        try {
            List<List<String>> synonyms = Reader.readSynonyms("data/synonyms.txt");
            synonyms.forEach(x-> x.forEach(String::trim));
            addSynonyms(builder, synonyms);
            synonymMap = builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Tokenizer tokenizer = new StandardTokenizer();
        assert synonymMap != null;
        TokenStream filter = new SynonymGraphFilter(tokenizer, synonymMap, true);
        return new Analyzer.TokenStreamComponents(tokenizer, filter);
    }

    private void addSynonyms(SynonymMap.Builder builder, List<List<String>> synonyms) {
        for (List<String> words : synonyms) {
            for (String input : words) {
                for (String output : words) {
                    builder.add(new CharsRef(input), new CharsRef(output), false);
                }
            }
        }
    }
}