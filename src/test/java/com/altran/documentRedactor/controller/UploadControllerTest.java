package com.altran.documentRedactor.controller;

import com.altran.documentRedactor.payload.Response;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class UploadControllerTest {

    UploadController classtoTest;

    MultipartFile mockFile;

    @Before
    public void setup() {
        classtoTest = new UploadController();
        mockFile = Mockito.mock(MultipartFile.class);
    }

    @Test(expected = HttpClientErrorException.class)
    public void should_throw_bad_exception_if_file_is_not_image_type() {
        when(mockFile.getContentType()).thenReturn("application/pdf");
        classtoTest.uploadFile(mockFile);
    }

    @Test
    public void should_return_file_details_response_for_image_file() {
        when(mockFile.getContentType()).thenReturn("image/jpg");
        when(mockFile.getOriginalFilename()).thenReturn("image.jpeg");
        when(mockFile.getSize()).thenReturn(1000000L);

        Response result = classtoTest.uploadFile(mockFile);

        assertThat(result).extracting("fileName", "fileType", "size").containsExactly("image.jpeg", "image/jpg", 1000000L);
    }
}