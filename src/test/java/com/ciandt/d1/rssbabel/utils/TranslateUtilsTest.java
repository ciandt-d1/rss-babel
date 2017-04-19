package com.ciandt.d1.rssbabel.utils;

import com.google.cloud.translate.Language;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by dviveiros on 4/18/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TranslateUtilsTest {

    @Autowired
    private TranslateServices translateServices;

    @Test
    @Ignore
    public void translate() throws Exception {
        String text = "This is a sentence in english. I will try to convert it to a different language";
        String targetLanguage = "pt";
        String result = translateServices.translate(text, targetLanguage);
        assertNotNull(result);
        assertNotEquals(result, text);
    }

    @Test
    public void listSupportedLanguages() throws Exception {
        List<Language> languageList = translateServices.listSupportedLanguages();
        assertNotNull(languageList);
    }

}