package com.altran.documentRedactor.pojo;

import com.google.cloud.vision.v1.Word;

public class WordObject {

    private final Integer index;
    private final String value;
    private final Word word;

    public WordObject(Integer index, String value, Word word) {
        this.index = index;
        this.value = value;
        this.word = word;
    }

    public Integer getIndex() {
        return index;
    }

    public String getValue() {
        return value;
    }

    public Word getWord() {
        return word;
    }
}
