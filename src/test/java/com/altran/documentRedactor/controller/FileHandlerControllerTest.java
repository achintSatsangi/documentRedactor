package com.altran.documentRedactor.controller;

import com.altran.documentRedactor.payload.Response;
import com.altran.documentRedactor.service.ImageRedactor;
import com.altran.documentRedactor.service.TextExtractorService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static com.altran.documentRedactor.controller.FileHandlerController.PATTERNS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class FileHandlerControllerTest {

    private FileHandlerController classtoTest;

    private MultipartFile mockFile;
    private TextExtractorService mockTextExtractorService;
    private ImageRedactor mockImageRedactor;

    @Before
    public void setup() {
        mockFile = mock(MultipartFile.class);
        mockTextExtractorService = mock(TextExtractorService.class);
        mockImageRedactor = mock(ImageRedactor.class);
        classtoTest = new FileHandlerController(mockTextExtractorService, mockImageRedactor);
    }

    @Test(expected = HttpClientErrorException.class)
    public void should_throw_bad_exception_if_file_is_null() {
        mockFile = null;
        classtoTest.extractText(mockFile);
        verifyZeroInteractions(mockTextExtractorService);
    }

    @Test(expected = HttpClientErrorException.class)
    public void should_throw_bad_exception_if_file_is_not_image_type() {
        when(mockFile.getContentType()).thenReturn("application/pdf");
        classtoTest.extractText(mockFile);
        verifyZeroInteractions(mockTextExtractorService);
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_runtime_exception_if_service_throws_the_same() {
        when(mockFile.getContentType()).thenReturn("image/jpg");
        when(mockFile.getOriginalFilename()).thenReturn("image.jpeg");
        when(mockFile.getSize()).thenReturn(1000000L);
        when(mockTextExtractorService.getTextInFile(any(MultipartFile.class))).thenThrow(new RuntimeException("Something bad happened as expected"));

        classtoTest.extractText(mockFile);
        verifyZeroInteractions(mockTextExtractorService);
    }

    @Test
    public void should_return_file_details_response_for_image_file() {
        when(mockFile.getContentType()).thenReturn("image/jpg");
        when(mockFile.getOriginalFilename()).thenReturn("image.jpeg");
        when(mockFile.getSize()).thenReturn(1000000L);
        when(mockTextExtractorService.getTextInFile(any(MultipartFile.class))).thenReturn("File Text");

        Response result = classtoTest.extractText(mockFile);

        verify(mockTextExtractorService).getTextInFile(any(MultipartFile.class));
        verifyNoMoreInteractions(mockTextExtractorService);
        assertThat(result).extracting("fileName", "fileType", "size", "textInFile").containsExactly("image.jpeg", "image/jpg", 1000000L, "File Text");
    }

    @Test(expected = HttpClientErrorException.class)
    public void should_throw_bad_exception_if_file_is_null_when_extracting_person_number() {
        mockFile = null;
        classtoTest.extractPersonNumber(mockFile);
        verifyZeroInteractions(mockTextExtractorService);
    }

    @Test(expected = HttpClientErrorException.class)
    public void should_throw_bad_exception_if_file_is_not_image_type_when_extracting_person_number() {
        when(mockFile.getContentType()).thenReturn("application/pdf");
        classtoTest.extractPersonNumber(mockFile);
        verifyZeroInteractions(mockTextExtractorService);
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_runtime_exception_if_service_throws_the_same_when_extracting_person_number() {
        when(mockFile.getContentType()).thenReturn("image/jpg");
        when(mockFile.getOriginalFilename()).thenReturn("image.jpeg");
        when(mockFile.getSize()).thenReturn(1000000L);
        when(mockTextExtractorService.getTextInFile(any(MultipartFile.class))).thenThrow(new RuntimeException("Something bad happened as expected"));

        classtoTest.extractPersonNumber(mockFile);
        verifyZeroInteractions(mockTextExtractorService);
    }

    @Test
    public void should_fetch_matching_text_as_many_patterns_are_configured() {
        when(mockFile.getContentType()).thenReturn("image/jpg");
        when(mockFile.getOriginalFilename()).thenReturn("image.jpeg");
        when(mockFile.getSize()).thenReturn(1000000L);
        when(mockTextExtractorService.getTextInFile(any(MultipartFile.class))).thenReturn("File Text");
        when(mockTextExtractorService.getMatchingPattern(anyString(), any(Pattern.class))).thenReturn(List.of("Text"));

        Response result = classtoTest.extractPersonNumber(mockFile);

        verify(mockTextExtractorService).getTextInFile(any(MultipartFile.class));
        verify(mockTextExtractorService, times(PATTERNS.size())).getMatchingPattern(anyString(), any(Pattern.class));
        verifyNoMoreInteractions(mockTextExtractorService);
        assertThat(result).extracting("fileName", "fileType", "size", "textInFile").containsExactly("image.jpeg", "image/jpg", 1000000L, "Matches for \\d{11}[Text]\n" +
                "Matches for \\d{6} \\d{5}[Text]\n" +
                "Matches for \\d{6}_\\d{5}[Text]\n" +
                "Matches for \\d{6} _ \\d{5}[Text]\n" +
                "Matches for f.nr.\\d{5}[Text]\n" +
                "Matches for f.nr. \\d{5}[Text]\n");
    }

    @Test(expected = HttpClientErrorException.class)
    public void should_throw_bad_exception_if_file_is_null_when_redacting_image() {
        mockFile = null;
        classtoTest.redactImage(mockFile);
        verifyZeroInteractions(mockTextExtractorService);
    }

    @Test(expected = HttpClientErrorException.class)
    public void should_throw_bad_exception_if_file_is_not_image_type_when_redacting_image() {
        when(mockFile.getContentType()).thenReturn("application/pdf");
        classtoTest.redactImage(mockFile);
        verifyZeroInteractions(mockTextExtractorService);
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_runtime_exception_if_service_throws_the_same_when_redacting_image() {
        when(mockFile.getContentType()).thenReturn("image/jpg");
        when(mockFile.getOriginalFilename()).thenReturn("image.jpeg");
        when(mockFile.getSize()).thenReturn(1000000L);
        when(mockTextExtractorService.getRedactionWordObjects(any(MultipartFile.class))).thenThrow(new RuntimeException("Something bad happened as expected"));

        classtoTest.redactImage(mockFile);
        verifyZeroInteractions(mockTextExtractorService);
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_runtime_exception_if_image_redactor_service_throws_the_same_when_redacting_image() {
        when(mockFile.getContentType()).thenReturn("image/jpg");
        when(mockFile.getOriginalFilename()).thenReturn("image.jpeg");
        when(mockFile.getSize()).thenReturn(1000000L);
        when(mockTextExtractorService.getTextInFile(any(MultipartFile.class))).thenReturn("File Text");
        when(mockTextExtractorService.getMatchingPattern(anyString(), any(Pattern.class))).thenReturn(List.of("Text"));
        when(mockImageRedactor.getRedactedImage(any(MultipartFile.class), anyList())).thenThrow(new IOException());

        classtoTest.redactImage(mockFile);

        verify(mockTextExtractorService).getRedactionWordObjects(any(MultipartFile.class));
        verify(mockImageRedactor).getRedactedImage(any(MultipartFile.class), anyList());
        verifyNoMoreInteractions(mockTextExtractorService);

    }
}