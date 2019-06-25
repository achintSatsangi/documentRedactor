package com.altran.documentRedactor.service;

import com.altran.documentRedactor.dao.GoogleVisionApiDao;
import com.altran.documentRedactor.pojo.WordObject;
import com.google.cloud.vision.v1.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.altran.documentRedactor.controller.FileHandlerController.PATTERNS;
import static java.util.List.of;

@Service
public class TextExtractorService {

    private static final Logger LOG = LogManager.getLogger(TextExtractorService.class);

    private GoogleVisionApiDao googleVisionApiDao;

    @Autowired
    public TextExtractorService(GoogleVisionApiDao googleVisionApiDao) {
        this.googleVisionApiDao = googleVisionApiDao;
    }

    public String getTextInFile(MultipartFile file){
        try{
            return getText(googleVisionApiDao.getResponse(file));
        } catch (Exception e) {
            LOG.error("While extracting text", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<WordObject> getRedactionWordObjects(MultipartFile file) {
        try{
        BatchAnnotateImagesResponse response = googleVisionApiDao.getResponse(file);
        List<WordObject> redactedWords = new ArrayList<>();
        int i = 0;
        List<AnnotateImageResponse> responses = response.getResponsesList();
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                LOG.error("Error: {}\n", res.getError().getMessage());
                throw new RuntimeException("Error occurred : " + res.getError().getMessage());
            }

            for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                annotation.getAllFields().forEach((k, v) ->
                        LOG.debug("{} : {}\n", k, v.toString()));
            }
            TextAnnotation annotation = res.getFullTextAnnotation();
            for (Page page: annotation.getPagesList()) {
                for (Block block : page.getBlocksList()) {
                    for (Paragraph para : block.getParagraphsList()) {
                        List<WordObject> words = new ArrayList<>();
                        int j=0;
                        String paraText = "";
                        for (Word word: para.getWordsList()) {
                            StringBuilder wordText = new StringBuilder();
                            for (Symbol symbol: word.getSymbolsList()) {
                                wordText.append(symbol.getText());
                            }
                            WordObject wordObject = new WordObject(j++, wordText.toString(), word);
                            words.add(wordObject);
                            //Word matching completed here.. skipped while looking for multiple word matching
                            if(matchesAnyPattern(wordText.toString())) {
                                redactedWords.add(wordObject);
                            }
                            paraText = String.format("%s %s", paraText, wordText.toString());
                        }
                        LOG.info("Paragraph: \n" + paraText);
                        LOG.info("Paragraph Confidence: {}\n", para.getConfidence());
                        List<String> matchingPatterns = getMatchingPatterns(paraText, PATTERNS);
                        if(!matchingPatterns.isEmpty()) {
                            for(String pattern : matchingPatterns) {
                                if(pattern.contains(" ")) {
                                    List<String> patternSplits = of(pattern.split("\\s+"));
                                    redactedWords.addAll(findMatchingPolysWords(words, patternSplits));
                                }
                            }
                        }
                    }
                }
            }
        }
        return redactedWords;
        } catch (Exception e) {
            LOG.error("While extracting text", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<WordObject> findMatchingPolysWords(List<WordObject> words, List<String> patternSplits) {
        List<WordObject> matches = new ArrayList<>();
        List<WordObject> firstMatchWords = words.stream()
                .filter(word -> word.getValue().equals(patternSplits.get(0)))
                .collect(Collectors.toList());

        for (WordObject word: firstMatchWords) {
            matches.addAll(matchingBoundaryPolies(words, word, patternSplits));
        }
        return matches;
    }

    private List<WordObject> matchingBoundaryPolies(List<WordObject> words, WordObject word, List<String> patternSplits) {
        //If para length is less then the matched word + number of words in pattern match.. no way it will match and
        // also cause ArrayIndexOutOfBounds.. so best skip it
        if(words.size() < word.getIndex() + patternSplits.size()){
            return of();
        }
        List<WordObject> wordSequence = new ArrayList<>();
        for(int i = 0; i < patternSplits.size(); i++) {
            wordSequence.add(words.get(word.getIndex() + i));
        }
        if(wordSequence.stream().map(w -> w.getValue()).collect(Collectors.toList()).containsAll(patternSplits)) {
            return wordSequence;
        }
        return of();
    }

    private boolean matchesAnyPattern(String word) {
        return PATTERNS.stream()
                .anyMatch(p -> p.matcher(word).find());
    }

    public List<String> getMatchingPatterns(String text, List<Pattern> patterns) {
        return patterns.stream()
                .flatMap(p -> this.getMatchingPattern(text, p).stream())
                .collect(Collectors.toList());
    }

    public List<String> getMatchingPattern(String text, Pattern pattern) {
        List<String> allMatches = new ArrayList<>();
        Matcher m = pattern.matcher(text);
        // now try to find at least one match
        while (m.find()) {
            allMatches.add(m.group());
        }
        return allMatches;
    }

    private String getText(BatchAnnotateImagesResponse response) {
        List<AnnotateImageResponse> responses = response.getResponsesList();
        String responseText = "";
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                LOG.error("Error: {}\n", res.getError().getMessage());
                throw new RuntimeException("Error occured : " + res.getError().getMessage());
            }

            for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                annotation.getAllFields().forEach((k, v) ->
                        LOG.debug("{} : {}\n", k, v.toString()));
            }

            // For full list of available annotations, see http://g.co/cloud/vision/docs
            TextAnnotation annotation = res.getFullTextAnnotation();
            for (Page page: annotation.getPagesList()) {
                String pageText = "";
                for (Block block : page.getBlocksList()) {
                    String blockText = "";
                    for (Paragraph para : block.getParagraphsList()) {
                        String paraText = "";
                        for (Word word: para.getWordsList()) {
                            StringBuilder wordText = new StringBuilder();
                            for (Symbol symbol: word.getSymbolsList()) {
                                wordText.append(symbol.getText());
                                LOG.debug("Symbol text: {} (confidence: {})\n", symbol.getText(),
                                        symbol.getConfidence());
                            }
                            LOG.debug("Word text: {} (confidence: {})\n\n", wordText.toString(), word.getConfidence());
                            paraText = String.format("%s %s", paraText, wordText.toString());
                        }
                        // Output Example using Paragraph:
                        LOG.debug("\nParagraph: \n" + paraText);
                        LOG.debug("Paragraph Confidence: {}\n", para.getConfidence());
                        blockText = blockText + paraText;
                    }
                    pageText = pageText + blockText;
                }
            }
            LOG.info("Returning : " + annotation.getText());
            responseText += annotation.getText();
        }
        return responseText;
    }
}
