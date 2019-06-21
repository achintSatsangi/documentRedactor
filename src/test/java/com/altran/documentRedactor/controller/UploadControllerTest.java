package com.altran.documentRedactor.controller;

import com.altran.documentRedactor.payload.Response;
import com.altran.documentRedactor.service.TextExtractorService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UploadControllerTest {

    private UploadController classtoTest;

    private MultipartFile mockFile;
    private TextExtractorService mockTextExtractorService;

    @Before
    public void setup() {
        mockFile = mock(MultipartFile.class);
        mockTextExtractorService = mock(TextExtractorService.class);
        classtoTest = new UploadController(mockTextExtractorService);
    }

    @Test(expected = HttpClientErrorException.class)
    public void should_throw_bad_exception_if_file_is_null() {
        mockFile = null;
        classtoTest.uploadFile(mockFile);
    }

    @Test(expected = HttpClientErrorException.class)
    public void should_throw_bad_exception_if_file_is_not_image_type() {
        when(mockFile.getContentType()).thenReturn("application/pdf");
        classtoTest.uploadFile(mockFile);
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_runtime_exception_if_service_thrws_the_same() {
        when(mockFile.getContentType()).thenReturn("image/jpg");
        when(mockFile.getOriginalFilename()).thenReturn("image.jpeg");
        when(mockFile.getSize()).thenReturn(1000000L);
        when(mockTextExtractorService.getTextInFile(any(MultipartFile.class))).thenThrow(new RuntimeException("Something bad happened as expected"));

        classtoTest.uploadFile(mockFile);
    }

    @Test
    public void should_return_file_details_response_for_image_file() {
        when(mockFile.getContentType()).thenReturn("image/jpg");
        when(mockFile.getOriginalFilename()).thenReturn("image.jpeg");
        when(mockFile.getSize()).thenReturn(1000000L);
        when(mockTextExtractorService.getTextInFile(any(MultipartFile.class))).thenReturn("File Text");

        Response result = classtoTest.uploadFile(mockFile);

        assertThat(result).extracting("fileName", "fileType", "size", "textInFile").containsExactly("image.jpeg", "image/jpg", 1000000L, "File Text");
    }
}