package kz.tamur.server.indexer;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;

public class HighlighterUtil {
	
	public static String getFragmentsWithHighlightedTerms(
			Analyzer analyzer,
			Query query,
            String fieldName,
            String fieldContents,
            int fragmentNumber) throws IOException, InvalidTokenOffsetsException {
		
		//TokenStream stream = TokenSources.getTokenStream(fieldName, fieldContents, analyzer);
		TokenStream stream = analyzer.tokenStream(fieldName, new StringReader(fieldContents));
        QueryScorer scorer = new QueryScorer(query, fieldName);
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"background-color:yellow;font-weight:bold;\">",
        															"</span>");
        
        Highlighter highlighter = new Highlighter(formatter, scorer);
        highlighter.setTextFragmenter(fragmenter);
        //highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
                
        String fragments = highlighter.getBestFragments(stream, fieldContents, fragmentNumber, "...");
                
        return fragments;
		
	}

}
