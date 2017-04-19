package com.ciandt.d1.rssbabel.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by dviveiros on 4/18/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FeedServicesTest {

    @Autowired
    private FeedServices feedServices;

    @Test
    public void generateUniqueId() throws Exception {
        SyndEntry syndEntry = new SyndEntryImpl();
        syndEntry.setLink("http://feedproxy.google.com/~r/time/topstories/~3/mSNrAa_Ev4g");
        String id = feedServices.generateUniqueId(syndEntry, "pt");
        System.out.println("ID generated = " + id);
        assertNotNull(id);
        assertEquals("9110183587959565069", id);
    }

}