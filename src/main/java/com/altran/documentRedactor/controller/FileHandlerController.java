package com.altran.documentRedactor.controller;

import com.altran.documentRedactor.payload.Response;
import com.altran.documentRedactor.pojo.WordObject;
import com.altran.documentRedactor.service.ImageRedactor;
import com.altran.documentRedactor.service.TextExtractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static org.springframework.util.StringUtils.cleanPath;

@RestController
public class FileHandlerController {

    private TextExtractorService textExtractorService;
    private ImageRedactor imageRedactor;

    public static List<Pattern> PATTERNS = List.of(compile("\\d{11}"), compile("\\d{6} \\d{5}"), compile("\\d{6}_\\d{5}"), compile("\\d{6} _ \\d{5}"), compile("f.nr.\\d{5}"), compile("f.nr. \\d{5}"));

    @Autowired
    public FileHandlerController(TextExtractorService textExtractorService, ImageRedactor imageRedactor) {
        this.textExtractorService = textExtractorService;
        this.imageRedactor = imageRedactor;
    }

    @PostMapping("/extractText")
    public Response extractText(@RequestParam("file") MultipartFile file) {
        validateFile(file);
        String textInFile = textExtractorService.getTextInFile(file);
        return new Response(cleanPath(file.getOriginalFilename()),
                file.getContentType(), file.getSize(), textInFile);
    }

    @PostMapping("/extractPersonNumber")
    public Response extractPersonNumber(@RequestParam("file") MultipartFile file) {
        validateFile(file);
        String textInFile = textExtractorService.getTextInFile(file);
        StringBuilder sb = new StringBuilder();
        PATTERNS.forEach(p -> getMatchingPatterns(textInFile, sb, p));
        return new Response(cleanPath(file.getOriginalFilename()),
                file.getContentType(), file.getSize(), sb.toString());
    }

    @PostMapping("/redactPersonNumber")
    public ResponseEntity<byte[]> redactImage(@RequestParam("file") MultipartFile file) {
        validateFile(file);
        List<WordObject> redactionWordObjects = textExtractorService.getRedactionWordObjects(file);
        byte[] redactedImage = imageRedactor.getRedactedImage(file, redactionWordObjects);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(redactedImage);
    }

    private StringBuilder getMatchingPatterns(String textInFile, StringBuilder sb, Pattern pattern) {
        sb.append("Matches for " + pattern);
        sb.append(textExtractorService.getMatchingPattern(textInFile, pattern)).append("\n");
        return sb;
    }

    @ExceptionHandler(MultipartException.class)
    public void handleException(MultipartException e) {
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public void handleException(HttpClientErrorException e) {
        throw e;
    }

    @ExceptionHandler(RuntimeException.class)
    public void handleException() {
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public void validateFile(MultipartFile file) {
        if(Objects.isNull(file)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "No file");
        }
        if(!file.getContentType().startsWith("image")) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Not an image file");
        }
    }
}
