package com.altran.documentRedactor.service;

import com.altran.documentRedactor.dao.GoogleVisionApiDao;
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
                                LOG.info("Symbol text: {} (confidence: {})\n", symbol.getText(),
                                        symbol.getConfidence());
                            }
                            LOG.info("Word text: {} (confidence: {})\n\n", wordText.toString(), word.getConfidence());
                            paraText = String.format("%s %s", paraText, wordText.toString());
                        }
                        // Output Example using Paragraph:
                        LOG.info("\nParagraph: \n" + paraText);
                        LOG.info("Paragraph Confidence: {}\n", para.getConfidence());
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

    public List<String> getMatchingPattern(String text, String pattern) {
        List<String> allMatches = new ArrayList<>();
        String text1 = text.replaceAll("\n", " ");
        Pattern p = Pattern.compile(pattern);   // the pattern to search for
        Matcher m = p.matcher(text1);
        // now try to find at least one match
        while (m.find()) {
            allMatches.add(m.group());
        }
        return allMatches;
    }

}
