package com.company;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DeduplicationInterfaceTest2 {

    @Test
    void getWordsShouldReturnMostCommonWords(){
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ala ma psa i kota");
        dd.addSentence("Ala ma psa i lisa");
        dd.addSentence("Grzegorz ma psa i rower");
        dd.addSentence("Lucjan ma psa i ryby");
        assertIterableEquals(dd.getWords(), new HashSet<String>(Collections.singleton("ma psa i")));
    }

    @Test
    void getWordsShouldReturnMostCommonWordsAfterRemovingOneAndAdding(){
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ala ma psa i kota");
        dd.addSentence("Ala ma psa i lisa");
        dd.addSentence("Grzegorz ma psa i rower");
        dd.addSentence("Lucjan ma psa i ryby");
        dd.removeSentence("Grzegorz ma psa i rower");
        dd.addSentence("Grzegorz idzie do pracy");
        assertIterableEquals(dd.getWords(), new HashSet<String>(Collections.singleton("ma psa i")));
    }

    @Test
    void shouldKeepMostCommonWordsAfterRemovingAllSentences(){
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ala ma psa i kota");
        dd.addSentence("Ala ma psa i lisa");
        dd.addSentence("Grzegorz ma psa i rower");
        dd.addSentence("Lucjan ma psa i ryby");
        dd.removeSentence("Ala ma psa i kota");
        dd.removeSentence("Ala ma psa i lisa");
        dd.removeSentence("Grzegorz ma psa i rower");
        dd.removeSentence("Lucjan ma psa i ryby");;
        assertTrue(dd.getDictionary().containsValue("ma psa i"));
        assertEquals(dd.getDictionary().size(), 1);
        assertIterableEquals(dd.getWords(), new HashSet<String>(Collections.singleton("ma psa i")));
    }

    @Test
    void test4(){
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ala ma psa i kota");
        dd.addSentence("Ala ma psa i lisa");
        dd.addSentence("Grzegorz ma psa i rower");
        dd.addSentence("Lucjan ma psa i ryby");
        dd.removeSentence("Ala ma psa i kota");
        dd.removeSentence("Ala ma psa i lisa");
        dd.removeSentence("Grzegorz ma psa i rower");
        dd.removeSentence("Lucjan ma psa i ryby");
        dd.addSentence("Grzegorz idzie do pracy");
        dd.addSentence("Grzegorz ma psa i samochod");
        assertTrue(dd.getDictionary().values().containsAll(Arrays.asList("Grzegorz", "idzie do pracy", "ma psa i samochod")));
        assertEquals(dd.getDictionary().size(), 3);
        assertIterableEquals(dd.getWords(), new HashSet<String>(Collections.singleton("ma psa i")));
    }

    @Test
    void addRepeatingAfterRemovingAll(){
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ala ma psa i kota");
        dd.addSentence("Ala ma psa i lisa");
        dd.addSentence("Grzegorz ma psa i rower");
        dd.addSentence("Lucjan ma psa i ryby");
        dd.removeSentence("Ala ma psa i kota");
        dd.removeSentence("Ala ma psa i lisa");
        dd.removeSentence("Grzegorz ma psa i rower");
        dd.removeSentence("Lucjan ma psa i ryby");
        dd.addSentence("ole, ole ole, ole!");
        assertTrue(dd.getDictionary().containsValue("ole"));
        assertEquals(dd.getDictionary().size(), 1);
        assertIterableEquals(dd.getWords(), new HashSet<String>(Collections.singleton("ole")));
    }

    @Test
    void getWordsShouldReturnEmptySetIfNoSentencesAdded() {
        DeduplicationInterface dd = new Deduplication();
        assertIterableEquals(dd.getWords(), new HashSet<String>());
    }

    @Test
    void shouldReturnMoreWordsIfEquallyCommon(){
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ala ma psa i rower");
        dd.addSentence("Grzegorz ma psa i rower");
        dd.addSentence("Ala idzie do szkoly");
        assertIterableEquals(dd.getWords(), new HashSet<String>(Collections.singleton("ma")));
    }

    @Test
    void shouldSplitAndRecalculateMostCommonWords(){
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ala ma psa i kota");
        dd.addSentence("Ala ma psa i lisa");
        dd.addSentence("Grzegorz ma psa i rower");
        dd.addSentence("Lucjan ma psa i ryby");
        dd.addSentence("Lucjan ma samochod i rower");
        assertIterableEquals(dd.getWords(), new HashSet<String>(Collections.singleton("ma")));
    }



    @Test
    void keepMostCommonWords() {
        DeduplicationInterface dd = new Deduplication();
        dd.addSentence("Ala ma psa i kota");
        dd.addSentence("Ala ma psa i lisa");
        dd.addSentence("Ala pisze list");
        dd.addSentence("Grzegorz ma psa i kota");
        dd.addSentence("Robert ma psa i lisa");
        dd.addSentence("Pawel ma psa i kota?");
        Map<Integer, String> dict = dd.getDictionary();
        for (String s: new String[]{"ma psa i", "Ala", "kota", "lisa", "pisze list", "Robert", "Pawel", "Grzegorz"}) {
            assertTrue(dict.containsValue(s));
        }
        dd.removeSentence("Ala ma psa i kota");
        dd.removeSentence("Ala ma psa i lisa");
        dd.removeSentence("Ala pisze list");
        dd.removeSentence("Grzegorz ma psa i kota");
        dd.removeSentence("Robert ma psa i lisa");
        dd.removeSentence("Pawel ma psa i kota?");
        assertTrue(dict.containsValue("ma psa i"));
    }
}