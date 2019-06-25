package com.altran.documentRedactor.service;

import com.altran.documentRedactor.pojo.WordObject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@Ignore("Do not want to hit the gCloud API on each build, these are for verification of logic")
@RunWith(SpringRunner.class)
@SpringBootTest
public class TextExtractorServiceIT {

    @Autowired
    TextExtractorService sut;

    @Test
    public void should_fetch_text_from_file() throws IOException {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("page1_part.png", Files.readAllBytes(ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX +"page1_part.png").toPath()));
        String result = sut.getTextInFile(mockMultipartFile);
        assertThat(result).contains("310349 37837");
    }

    @Test
    public void should_find_matching_person_number_patterns() throws IOException {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("page1_part.png", Files.readAllBytes(ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX +"page1_part.png").toPath()));
        List<WordObject> result = sut.getRedactionWordObjects(mockMultipartFile);
        assertThat(result).hasSize(5);

        assertThat(result.stream().map(w -> w.getValue()).collect(toList())).containsExactly("23094238056", "221246", "39488", "310349", "37837");

    }
}