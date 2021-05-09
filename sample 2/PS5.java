/**
 * @author Henry Kim
 * Problem Set 5 Implementation
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PS5 {
    protected Set<String> tags;
    protected Map<String, Map<String, Double>> transitionsTrain; // Key: Tag, Value: Map of next state and log probability
    protected Map<String, Map<String, Double>> observationsTrain;// Key: Tag, Value: Map of word and log probability

    protected ArrayList<String> wordsTest;
    protected ArrayList<String> tagsTest;
    protected ArrayList<String> probableTags;
    protected Map<String, Map<String, Double>> viterbi; // Key: Word, Value: Map of Scores

    public PS5(){
        tags = new HashSet<String>();
        transitionsTrain= new HashMap<String, Map<String, Double>>();
        observationsTrain = new HashMap<String, Map<String, Double>>();
        probableTags = new ArrayList<String>();
        viterbi = new HashMap<String, Map<String, Double>>();
    }

    /**
     * Reads file
     * @param fileName file name
     * @return ArrayList of Strings
     * @throws Exception if file not found, IO exception
     */
    public static ArrayList<String> readFile(String fileName) throws Exception{
        ArrayList<String> words = new ArrayList<String>();
        try {
            BufferedReader input = new BufferedReader(new FileReader(fileName));
            String line;
            int lineNum = 0;
            while ((line = input.readLine()) != null) {
                String[]data = line.toLowerCase().split("\\s+"); // convert to lowercase
                for (String s : data)
                    words.add(s);
                lineNum++;
            }
            input.close();
        }
        catch (FileNotFoundException e){
            throw new FileNotFoundException();
        }
        catch (IOException e){
            throw new IOException();
        }
        catch (Exception e){
            throw new Exception();
        }
        return words;
    }

    /**
     * Tally words and tags' occurrence
     * @param tags tag list
     * @param words word list
     */
    public void counts(ArrayList<String> tags, ArrayList<String> words){
        // count the probability of the next word coming out
        for (int i = 0; i < tags.size()-1; i++){
            String currentTag = tags.get(i);
            String nextTag = tags.get(i+1);
            String currentWord = words.get(i);

            // add transition to map if not seen before
            if (! transitionsTrain.containsKey(currentTag)){
                transitionsTrain.put(currentTag, new HashMap<String, Double>());
                observationsTrain.put(currentTag, new HashMap<String, Double>());
            }

            // add possible next state if not seen before
            if (! transitionsTrain.get(currentTag).containsKey(nextTag))
                transitionsTrain.get(currentTag).put(nextTag, 0.0);
            if (! observationsTrain.get(currentTag).containsKey(currentWord))
                observationsTrain.get(currentTag).put(words.get(i), 0.0);

            // add word and next tag counts
            transitionsTrain.get(currentTag).put(nextTag, transitionsTrain.get(currentTag).get(nextTag)+1);
            observationsTrain.get(currentTag).put(currentWord, observationsTrain.get(currentTag).get(currentWord)+1);
        }
    }

    /**
     * convert probabilities to logs
     */
    public void logify(){
        for (String t : transitionsTrain.keySet()){
            int tot = 0;

            // find total occurrences within each tag
            for (String s : transitionsTrain.get(t).keySet()){
                tot += transitionsTrain.get(t).get(s);
            }

            // take log of probabilities - observationsTrain and transitionsTrain
            for (String w : observationsTrain.get(t).keySet())
                observationsTrain.get(t).put(w, Math.log(observationsTrain.get(t).get(w)/tot));

            for (String s : transitionsTrain.get(t).keySet())
                transitionsTrain.get(t).put(s, Math.log(transitionsTrain.get(t).get(s)/tot));
        }
    }

    /**
     * run viterbi algorithm
     */
    public void vit(){
        // backtrack: list of maps, <word, what type of word>
        List<Map<String, String>> backTrack = new ArrayList<>();

        // for initial start
        Set<String> currStates = new HashSet<>();
        currStates.add("#");

        Map<String, Double> currScores = new HashMap<>();
        currScores.put("#", 0.0);

        Map<String, Double> nextScores = null;
        String key = null;
        for (int i = 0; i < tagsTest.size(); i++){
            Set<String> nextStates = new HashSet<>();
            nextScores = new HashMap<>(); // empty map
            Map<String, String> wordBackTrace = new HashMap<>();
            double nextScore;
            String word = wordsTest.get(i);
            for (String s : currStates){
//                System.out.println("CURRENT STATE: " + s);
                for (String t : transitionsTrain.get(s).keySet()){
                    nextStates.add(t);
//                    System.out.println("NEW STATE: "+ t);
//                    System.out.println("Current Score: " + currScores.get(s));
//                    System.out.println("Transition Score between " + s +" and " + t + ": " + transitionsTrain.get(s).get(t));

                    nextScore = currScores.get(s) + transitionsTrain.get(s).get(t); // + observationsTrain
//                    System.out.println("CURRENT WORD: " + word);
//                    System.out.println("OBSERVATIONS TRAIN: " + observationsTrain.get(s));
                    if (! observationsTrain.get(t).containsKey(word)){
                        nextScore -= 100; // unseen word penalty
//                        System.out.println("UNSEEN WORD: -100");
                    }
                    else{
                        nextScore += observationsTrain.get(t).get(word);
//                        System.out.println("OBSERVATION HAS AN OBSERVATION PROBABILITY OF " + observationsTrain.get(t).get(word));
                    }
//                    System.out.println("TOTAL SCORE: " + nextScore);
                    if (!nextScores.containsKey(t) || nextScore > nextScores.get(t)){
                        nextScores.put(t, nextScore);
                        wordBackTrace.put(t, s);
                    }
//                    System.out.println();
                }
            }

            backTrack.add(wordBackTrace);
            currStates = nextStates;
            currScores = nextScores;
        }

        // calc max, set best score
        key = null;
        double maxScore = Integer.MIN_VALUE;
        for (String n : nextScores.keySet()){
            if (nextScores.get(n) > maxScore) {
                key = n;
                maxScore = nextScores.get(n);
            }
        }
        probableTags.add(key);
//        System.out.println("The last word is likely a " + key);
        String current; String previous;
        ArrayList<String>temp = new ArrayList<String>();
        for (int i = tagsTest.size()-1; i >0; i--){
            current = backTrack.get(i).get(key);
//            System.out.println("Backtrack map: " + backTrack.get(i));
//            System.out.println("The predecessor to " + key + " is "+ current);
            probableTags.add(current);
            key = current;
        }
        Stack<String> stack = new Stack<String>(); // use a stack to reverse the order (push/pop)
        for (int i = 0; i < probableTags.size();i++){
            stack.add(probableTags.get(i));
        }
        probableTags = new ArrayList<String>(); // add them back
        for (int i = 0; i < tagsTest.size(); i++){
            probableTags.add(stack.pop());
        }
    }

    /**
     * Calculate accuracy of tagger
     * @return accuracy as a percentage
     */
    public double calcAccuracy(){
        double correct = 0;
        int index = 0;
        for (String s : probableTags){
            if (s.equals(tagsTest.get(index)))
                correct++;
            else
                System.out.println("COMPARING "+ s + " AND "+ tagsTest.get(index) + " FOR THE WORD " + wordsTest.get(index));
            index++;
        }
        System.out.println(correct +" OUT OF "+ probableTags.size());
        correct /= probableTags.size()*0.01;
        return correct;
    }

    public static void main(String[]args){
        Scanner input = new Scanner(System.in);
        String testFile = "simple";
        try {
            PS5 ps5 = new PS5();
            // file driven testing
            ArrayList<String> words = readFile("inputs/" + testFile + "-train-sentences.txt"); // tRaining mode
            ArrayList<String> tags = readFile("inputs/" + testFile + "-train-tags.txt");
            if (words.size() != tags.size()){
                throw new Exception("Make sure to check each word has a tag");
            }
            // for initial start
            words.add(0,"START");
            tags.add(0,"#");

            ps5.tags.addAll(tags); // add all tags to set of possible tags
            ps5.counts(tags, words); // count the probability of the next word coming out
            ps5.logify(); // convert to log probabilities

            System.out.println("Time to put it to the test.");
            ArrayList<String> wordsTest = readFile("inputs/" + testFile + "-test-sentences.txt");
            ArrayList<String> tagsTest = readFile("inputs/" + testFile +"-test-tags.txt");
            ps5.wordsTest = wordsTest;
            ps5.tagsTest = tagsTest;
            System.out.println("VIT LOADED");
            ps5.vit();
            System.out.println(ps5.calcAccuracy());

            // console driven testing
            System.out.println("Now let's give your sentences a try! Type a sentence with the period spaced at the end.");
            System.out.println("Test Case #1: all based on words in the training data but never put together before");
            System.out.println("Try:  we bark for the trains (pro v p det n .)");
            System.out.println("Test Case #2: parallel structure to training data");
            System.out.println("Try: our outfits are fashionable. (pro n v adj .");
            System.out.println("Test Case #3: ambiguous parts of speech");
            System.out.println("Try: I hate to work on work (pro v p v p n .");
            System.out.println("Test Case #4: same sentence as test data");
            System.out.println("Try: the dog saw trains in the night . (DET N V N P DET N .)");
            System.out.println("Test Case #5: multiple sentences");
            System.out.println("Try: you go to dartmouth . i thought dartmouth students were smarter .");
            System.out.println("Type a sentence to begin.");

            String sentence = input.nextLine();
            while (sentence != null){
                if (sentence.indexOf('.') == -1)
                    sentence += " ."; // in case the user forgot to have a period at the end
                String[]sentenceWords = sentence.toLowerCase().split("\\s+"); // words are in lowercase

                System.out.println("Now type each part of speech spaced");
                String pos = input.nextLine();
                if (pos.indexOf('.') == -1)
                    pos += " .";
                String[]posSplit = pos.toLowerCase().split("\\s+");

                if (sentenceWords.length != posSplit.length){
                    System.out.println("Make sure to enter the same number of words as you enter tags");
                }
                else{
                    System.out.println("TESTING");
                    ps5.wordsTest = new ArrayList<String>(); // reset each time
                    // add the words and tags again
                    for (String w: sentenceWords){
                        ps5.wordsTest.add(w.toLowerCase());
                    }
//                    System.out.println(ps5.wordsTest);

                    ps5.tagsTest = new ArrayList<String>();
                    for (String t : posSplit){
                        ps5.tagsTest.add(t.toLowerCase());
                    }
//                    System.out.println(ps5.tagsTest);

                    ps5.vit(); // run viterbi
                    System.out.println("Your sentence correctly identified "+ ps5.calcAccuracy() + "% of the tags.");
                }
                System.out.println("Type another sentence.");
                sentence = input.nextLine();
            }
        }
        catch (FileNotFoundException e){
            System.err.println("FILE NOT FOUND");
        }
        catch (IOException e){
            System.err.println("SOMETHING WENT WRONG DURING THE FILE READING PROCESS");
        }
        catch (ArithmeticException ae) {
            System.err.println("DID YOU MAKE SURE TO HAVE ANY WORDS?");
        }
        catch (NullPointerException n){
            System.err.println("NULL POINTER EXCEPTION");
        }
        catch (Exception e){
            System.err.println("MAKE SURE EACH WORD HAS A TAG.");
        }

    }
}
