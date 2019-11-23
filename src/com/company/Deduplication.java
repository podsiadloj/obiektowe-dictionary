package com.company;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Deduplication implements DeduplicationInterface {
    private final String splitRx = "[^[a-zA-Z0-9]]+";
    private TreeMap<Integer, String> dictionary = new TreeMap<>();
    private ArrayList<List<Integer>> sentences = new ArrayList<>();

    @Override
    public void addSentence(String sentence) {
        List<String> newWords = new ArrayList<>();
        List<String> pieces = Arrays.asList(sentence.split(splitRx));
        for (int i = 0; i < pieces.size(); i++) {
            if (pieces.get(i).equals("*")) {continue;}
            List<Integer> repeats = new ArrayList<>();
            repeats.add(i);
            for (int j = (i + 1); j < pieces.size(); j++) {
                if(pieces.get(i).equals(pieces.get(j))){
                    repeats.add(j);
                }
            }
            if(repeats.size() > 1){
                int maxWordLength = pieces.size() - repeats.get(repeats.size() - 1);
                int wordLength = 1;
                while(wordLength <= maxWordLength){
                    int offset = wordLength;
                    if (pieces.get(repeats.get(0) + offset) == null) {break;}
                    boolean nextEqual = repeats.stream()
                            .map(index -> pieces.get(index + offset))
                            .distinct()
                            .limit(2)
                            .count() == 1;
                    if(nextEqual){
                        wordLength++;
                    } else break;
                }
                List<String> resultWord = new ArrayList<>();
                for (int j = repeats.get(0); j < repeats.get(0) + wordLength; j++) {
                    resultWord.add(pieces.get(j));
                }
                newWords.add(String.join(" ", resultWord));
                for (int index : repeats) {
                    for (int j = index; j < index + wordLength; j++) {
                        pieces.set(j, "*");
                    }
                }
            }
        }
        newWords.addAll(remainingWords(pieces));
        for (String word : newWords) {
            addWord(word);
        }
        sentences.add(rebuild(sentence, new ArrayList<>(dictionary.keySet())));
    }

    private List<String> remainingWords(List<String> pieces) {
        List<String> remainingWords = new ArrayList<>();
        List<String> newWord = new ArrayList<>();
        for (String piece : pieces) {
            if (piece.equals("*")) {
                if (newWord.size() > 0) {
                    remainingWords.add(String.join(" ", newWord));
                    newWord = new ArrayList<>();
                }
            } else newWord.add(piece);
        }
        if(newWord.size() > 0){
            remainingWords.add(String.join(" ", newWord));
        }
        return remainingWords;
    }

    private void addWord(String word){
        List<String> pieces = Arrays.asList(word.split(splitRx));
        List<Integer> keys = new ArrayList<>(dictionary.keySet());
        for (int key : keys) {
            List<String> dictPieces = Arrays.asList(dictionary.get(key).split(splitRx));
            List<String> newWords = new ArrayList<>();
            for (int i = 0; i < pieces.size(); i++) {
                if (!pieces.get(i).equals("*")) {
                    for (int j = 0; j < dictPieces.size(); j++) {
                        if (!dictPieces.get(j).equals("*")) {
                            if (dictPieces.get(j).equals(pieces.get(i))) {
                                int length = 1;
                                while (i + length < pieces.size()
                                        && j + length < dictPieces.size()
                                        && !(dictPieces.get(j + length).equals("*"))
                                        && dictPieces.get(j + length).equals(pieces.get(i + length))
                                ) {
                                    length++;
                                }
                                List<String> newWordPieces = new ArrayList<>();
                                for (int k = 0; k < length; k++) {
                                    newWordPieces.add(pieces.get(i + k));
                                    pieces.set(i + k, "*");
                                    dictPieces.set(j + k, "*");
                                }
                                newWords.add(String.join(" ", newWordPieces));
                            }
                        }
                    }
                }
            }
            newWords.addAll(remainingWords(dictPieces));
            registerSplit(key, rebuild(dictionary.get(key), registerWords(newWords)));
        }
        registerWords(remainingWords(pieces));
    }

    private List<Integer> rebuild(String sentence, List<Integer> wordIndexes){
        String workStr = String.join(" ", sentence.split(splitRx)).trim();
        List<Integer> results = new ArrayList<>();
        while(workStr.length() > 0){
            for (int index : wordIndexes) {
                if(dictionary.containsKey(index)){
                    String word = dictionary.get(index);
                    if(workStr.startsWith(word)){
                        results.add(index);
                        workStr = workStr.substring(word.length()).trim();
                    }
                }
            }
        }
        return results;
    }

    private List<Integer> registerSplit(Integer sourceIndex, List<Integer> wordIndexes){
        for (List<Integer> sentence : sentences) {
            int end = sentence.size();
            for (int i = 0; i < end; i++) {
                if(sentence.get(i).equals(sourceIndex)){
                    sentence.remove(i);
                    sentence.addAll(i, wordIndexes);
                    end += (wordIndexes.size() - 1);
                }
            }
        }
        if(!wordIndexes.contains(sourceIndex)){ dictionary.remove(sourceIndex); }
        return wordIndexes;
    }

    private List<Integer> registerWords(List<String> words) {
        List<Integer> results = new ArrayList<>();
        for(String word : words) {
            if (!dictionary.containsValue(word)) {
                Integer key = dictionary.size() > 0 ? dictionary.lastKey() + 1 : 0;
                dictionary.put(key, word);
                results.add(key);
            } else {
                Integer key = dictionary.entrySet().stream().filter(entry -> entry.getValue().equals(word)).map(Map.Entry::getKey).findFirst().get();
                results.add(key);
            }
        }
        return results;
    }

    @Override
    public void removeSentence(String sentence)  {
        try {
            List<Integer> l = getSentence(sentence);
            sentences.removeIf(s -> s.equals(l));
            for (int i = 0; i < sentences.size(); i++) {
                List<Integer> currentSentence = sentences.get(i);
                int csLength = currentSentence.size();
                for (int j = 0; j < csLength; j++) {
                    List<Integer> currentWord = new ArrayList<>();
                    currentWord.add(currentSentence.get(j));
                    int length = 1;
                    boolean allEqual = true;
                    while(j + length < currentSentence.size() && allEqual){
                        Integer expected = currentSentence.get(j + length);
                        for(List<Integer> checkSentence: sentences){
                            for (int k = 0; k + length < checkSentence.size(); k++) {
                                if (checkSentence.get(k).equals(currentWord.get(0)) && !checkSentence.get(k + length).equals(expected)) {
                                    allEqual = false;
                                    break;
                                }
                            }
                        }
                        if(allEqual){
                            currentWord.add(j + length);
                            length++;
                        }
                    }
                    if(length > 1){
                        csLength -= (length - 1);
                        String newWord = IntStream.range(j, j+length)
                                .map(currentSentence::get)
                                .mapToObj(key -> dictionary.get(key))
                                .collect(Collectors.joining(" "));
                        int newKey = registerWords(Collections.singletonList(newWord)).get(0);
                        for(List<Integer> checkSentence: sentences){
                            int checkIndex = 0;
                            while(true){
                                if (checkIndex + length > checkSentence.size()) break;
                                if (checkSentence.get(checkIndex).equals(currentWord.get(0))) {
                                    for (int m = length-1; m >= 0; m--) {
                                        checkSentence.remove(checkIndex + m);
                                    }
                                    checkSentence.add(checkIndex, newKey);
                                }
                                checkIndex++;
                            }
                        }
                    }
                }
            }
            List<Integer> keys = new ArrayList<>(dictionary.keySet());
            for (int key : keys) {
                boolean used = false;
                for (List<Integer> checkSentence : sentences) {
                    if (checkSentence.contains(key)) {
                        used = true;
                        break;
                    }
                }
                if(!used){
                    dictionary.remove(key);
                }
            }
        } catch (NonExistentSentence nonExistentSentence) {
            System.out.println("No such sentence");
        }
    }

    @Override
    public Map<Integer, String> getDictionary() {
        return this.dictionary;
    }

    @Override
    public List<Integer> getSentence(String sentence) throws NonExistentSentence {
        String workstr = String.join(" ", sentence.split(splitRx));
        for (List<Integer> s : sentences){
            String savedStr = s.stream().map(key -> dictionary.get(key)).collect(Collectors.joining(" "));
            if(savedStr.equals(workstr)){
                return s;
            }
        }
        throw new NonExistentSentence();
    }

    @Override
    public Set<String> getWords() {
        return null;
    }
}
