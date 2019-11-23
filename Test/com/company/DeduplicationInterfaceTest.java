package com.company;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DeduplicationInterfaceTest {

    @Test
    void addSentenceShouldAddToDict() {
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ala ma kota");
        assertNotNull(dd.getDictionary());
        assertTrue(dd.getDictionary().containsValue("Ala ma kota"));
    }

    @Test
    void addingMoreSentencesShouldUpdateDict() {
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ala ma kota");
        dd.addSentence("Ala ma psa");
        assertNotNull(dd.getDictionary());
        Map<Integer, String> dict = dd.getDictionary();
        for (String s: new String[]{"Ala ma", "kota", "psa"}) {
            assertTrue(dict.containsValue(s));
        }
        assertFalse(dict.containsValue("Ala ma kota"));
    }

    @Test
    void shouldSplitAlreadySplitDictWordsOnUpdates() {
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ala ma kota");
        dd.addSentence("Ala ma psa");
        dd.addSentence("Ala idzie na spacer");
        assertNotNull(dd.getDictionary());
        Map<Integer, String> dict = dd.getDictionary();
        for (String s: new String[]{"Ala", "ma", "kota", "psa", "idzie na spacer"}) {
            assertTrue(dict.containsValue(s));
        }
    }

    @Test
    void removingSentencesShouldUpdateDict() {
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ala ma kota");
        dd.addSentence("Ala ma psa");
        dd.removeSentence("Ala ma kota");
        assertNotNull(dd.getDictionary());
        Map<Integer, String> dict = dd.getDictionary();
        for (String s: new String[]{"Ala ma", "kota", "psa"}) {
            assertFalse(dict.containsValue(s));
        }
        assertTrue(dict.containsValue("Ala ma psa"));
    }

    @Test
    void dictShouldSplitOnPunctuation() {
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ala ma.kota");
        dd.addSentence("Ala ma,psa");
        assertNotNull(dd.getDictionary());
        Map<Integer, String> dict = dd.getDictionary();
        for (String s: new String[]{"Ala ma", "kota", "psa"}) {
            assertTrue(dict.containsValue(s));
        }
    }

    @Test
    void dictShouldNotContainPunctuationWordsEvenIfRepeats() {
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ala ma...kota");
        dd.addSentence("Ala ma...psa");
        assertNotNull(dd.getDictionary());
        Map<Integer, String> dict = dd.getDictionary();
        for (String s: new String[]{"Ala ma", "kota", "psa"}) {
            assertTrue(dict.containsValue(s));
        }
        assertFalse(dict.containsValue("."));
    }

    @Test
    void shouldNotSplitWithoutWhitespaceOrPunctuation() {
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Alamakota");
        dd.addSentence("Alamapsa");
        assertNotNull(dd.getDictionary());
        Map<Integer, String> dict = dd.getDictionary();
        for (String s: new String[]{"Alama", "kota", "psa"}) {
            assertFalse(dict.containsValue(s));
        }
        for (String s: new String[]{"Alamakota", "Alamapsa"}) {
            assertTrue(dict.containsValue(s));
        }
    }

    @Test
    void shouldFindRepeatingWordsInSingleSentence() {
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ala ma kota i ma kota ma kota oraz ma kota");
        assertNotNull(dd.getDictionary());
        Map<Integer, String> dict = dd.getDictionary();
        for (String s: new String[]{"Ala", "ma kota", "oraz", "i"}) {
            assertTrue(dict.containsValue(s));
        }
    }

    @Test
    void getSentenceShouldReturnListIfSentenceIsSaved() {
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ryszard jedzie samochodem");
        dd.addSentence("Ola jedzie na wakacje");
        Map<Integer, String> dict = dd.getDictionary();
        try {
            List<Integer> l = dd.getSentence("Ryszard jedzie samochodem");
            assertEquals(dict.get(l.get(0)), "Ryszard");
            assertEquals(dict.get(l.get(1)), "jedzie");
            assertEquals(dict.get(l.get(2)), "samochodem");
            l = dd.getSentence("Ola jedzie na wakacje");
            assertEquals(dict.get(l.get(0)), "Ola");
            assertEquals(dict.get(l.get(1)), "jedzie");
            assertEquals(dict.get(l.get(2)), "na wakacje");
        } catch (DeduplicationInterface.NonExistentSentence nonExistentSentence) {
            fail("getSentence threw an error");
        }
    }

    @Test
    void getSentenceShouldThrowIfSentenceDoesNotExist() {
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ryszard jedzie samochodem");
        dd.addSentence("Ola jedzie na wakacje");

        assertThrows(DeduplicationInterface.NonExistentSentence.class, () -> dd.getSentence("Ryszard jedzie na wakacje"));
    }
}