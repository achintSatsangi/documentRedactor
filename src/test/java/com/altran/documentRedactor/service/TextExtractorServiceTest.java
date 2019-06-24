package com.altran.documentRedactor.service;

import com.altran.documentRedactor.dao.GoogleVisionApiDao;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Covers unit tests only... more in the integration test class
 */
public class TextExtractorServiceTest {

    private GoogleVisionApiDao mockGoogleVisionApiDao;
    private MultipartFile mockFile;

    private TextExtractorService classToTest;

    @Before
    public void setup() {
        mockFile = mock(MultipartFile.class);
        mockGoogleVisionApiDao = mock(GoogleVisionApiDao.class);
        classToTest = new TextExtractorService(mockGoogleVisionApiDao);
    }

    @Test(expected = RuntimeException.class)
    public void should_encapsulate_all_exceptions_to_runtime_exception() throws Exception {
        when(mockGoogleVisionApiDao.getResponse(mockFile)).thenThrow(new Exception("Expected exception"));
        classToTest.getTextInFile(mockFile);
    }

    @Test
    public void should_match_pattern() {
        List<String> result = classToTest.getMatchingPattern("ABCD 123 DFHG 9876", "\\d{4}");
        Assertions.assertThat(result).hasSize(1).containsExactly("9876");
    }
}