package mini_project_2;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class SpellCheck {
	
	private static ArrayList<String> dictionary = new ArrayList<String>();
	private static HashMap<String, Long> bigrams = new HashMap<String, Long>();
	private static String allLetters = "abcdefghijklmnopqrstuvwxyz";
	private static char[] alphabet = allLetters.toCharArray();

	public SpellCheck(String dictionaryPath, String bigramPath){
		
		//Take each word in the file, convert it to lowercase, add to the dictionary
		try{	
			Scanner scan = new Scanner(new File(dictionaryPath));
			
			while (scan.hasNext()) {
	            
				dictionary.add(scan.next().toLowerCase());
	        }
		
		} catch (Exception e){
			e.printStackTrace();
		}
		
		//Read in bigrams from file, add to bigrams hashmap
		try{
			Scanner scan = new Scanner(new File(bigramPath));
			
			while(scan.hasNextLine()){
				
				String[] columns = scan.nextLine().split("\t");

		        bigrams.put(columns[0], Long.parseLong(columns[1]));
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//Returns the dictionary for testing purposes
	public ArrayList<String> get_dictionary(){
		return SpellCheck.dictionary;
	}
	
	public void printBigrams(){
		Set set = SpellCheck.bigrams.entrySet();
	    Iterator iterator = set.iterator();
	    while(iterator.hasNext()) {
	       Map.Entry mentry = (Map.Entry)iterator.next();
	       System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
	       System.out.println(mentry.getValue());
	    }
	}

	public String provideSuggestions(String text){
		ArrayList <String> originalWords = new ArrayList<String>(Arrays.asList(text.toLowerCase().split("\\s+")));
		String returnValue = "";
		int tempSize = originalWords.size() - 1;
		String lastWord = originalWords.get(tempSize);
		int temp = originalWords.indexOf(lastWord);
	
		ArrayList<Integer> indexList = new ArrayList<Integer>();
	    for (int i = 0; i < dictionary.size(); i++) {
	        if(lastWord.equals(dictionary.get(i)))
		        indexList.add(i);
		    }
			String[] suggestions = new String[indexList.size()];
			
		    for(int j = 0; j < indexList.size(); j++) {
		    			suggestions[j] = dictionary.get(indexList.get(j)+1)+" ";
		    			returnValue = suggestions[j];
		    		}
	    	int maxCount = 0;
	    	String maxCountWord = "";
	    	int tempCount = 0;
	    	String tempString = "";
	    	
		    //WORD INSTANCE COUNTING
		    for(int p = 0; p<indexList.size();p++) {
		    	tempString = suggestions[p];
		    	tempCount = 0;
		    		for(int q =0; q <suggestions.length; q++) {
		    			if(suggestions[q].equalsIgnoreCase(tempString)){
		    				tempCount = tempCount + 1;
		    			}
		    			if(tempCount > maxCount) {
		    				maxCount = tempCount;
		    				maxCountWord = tempString;
		    			}else{
		    			
		    			}
		    		}
		    }
		    
		    
		return maxCountWord;
	}

	public String correctBody(String text){
		
		ArrayList <String> originalWords = new ArrayList<String>(Arrays.asList(text.toLowerCase().split("\\s+")));
		String correctedWords = "";
		
		//Now that the body of text is divided into individual words, we need to check if they are spelled correctly.
		for(String word : originalWords){
			if(wordIsSpelledCorrectly(word)){ //if word is spelled correctly, append it to the corrected words
				correctedWords += word + " ";
			} else { // word is not spelled correctly, correct spelling and append to corrected words
				String correctWord = correctWord(word);
				correctedWords += correctWord + " ";
			}

		}
	
		return correctedWords;
	}
	
	//Returns true if word is in the dictionary, false if not
	public static boolean wordIsSpelledCorrectly(String word){
		
		if(dictionary.contains(word)){
			return true;
		} else {
			return false;
		}
	}
	
	public ArrayList<String> suggestCorrections(String misspelledWord){
		ArrayList<String> suggestions = new ArrayList<String>();
		
		if(wordIsSpelledCorrectly(misspelledWord)){
			suggestions.add(misspelledWord);
		} else {
			
			suggestions.addAll(addLetter(misspelledWord));
			suggestions.addAll(swapLetters(misspelledWord));
			suggestions.addAll(deleteLetter(misspelledWord));
			
		}
		
		return suggestions;
	}
	
	public boolean calculateViability(String word){
		char[] fullWord = word.toCharArray();
		char[] twoLetters = new char[2];
		long viabilityScore = 0;
		boolean viable = true;
	
		
		int j = 1;
		for(int i = 0; i < word.length()-1; i++){
			viabilityScore = 0;
			twoLetters[0] = fullWord[i];
			twoLetters[1] = fullWord[j];
			
			String swappedString = new String(twoLetters);
			
			viabilityScore = bigrams.get(swappedString);
			if(viabilityScore < 1355781515){
				viable = false;
			}
			
			j++;
		}	
		
		return viable;
		
	}
	
	//Returns the most likely corrected word
	public String correctWord(String misspelledWord){
		
		ArrayList<String> suggestions = suggestCorrections(misspelledWord);
		
		Random rand = new Random();
		
		if(suggestions.isEmpty()){
			return "CAN'T CORRECT";
		} else {
			return suggestions.get(rand.nextInt(suggestions.size()));
		}
		
	}
	
	
	public ArrayList<String> deleteLetter(String word){
		ArrayList<String> suggestions = new ArrayList<>();
		
		for(int i=0; i < word.length(); i++){
			String temp = removeCharAt(word, i);
			
			if (dictionary.contains(temp)) {
                if(!suggestions.contains(temp)){
				
                	suggestions.add(temp);
                }
            } 	
			
		}
		
		
		
		return suggestions;
	}
	
	//Used in deleteLetter -> referenced from https://www.tutorialspoint.com/javaexamples/string_removing_char.htm
	public static String removeCharAt(String s, int pos) {
	      return s.substring(0, pos) + s.substring(pos + 1);
	}
	
	//Test if adding a letter anywhere in the word will be found in the dictionary
	public ArrayList<String> addLetter(String testWord){
		ArrayList<String> suggestions = new ArrayList<>();
        
		for (char character : alphabet) {
            
			for(int i = 0; i < testWord.length()+1; i++){
				String temp = testWord;
				temp = new StringBuilder(temp).insert(i, character).toString();
				
				if (dictionary.contains(temp)) {
	                if(!suggestions.contains(temp)){
					
	                	suggestions.add(temp);
	                }
	            } 				
			}     
           
        }
		
		return suggestions;
		
	}
	
	
	//Check to see if swapping the order of each adjacent letter will be found in the dictionary
	public ArrayList<String> swapLetters(String testWord){
		ArrayList<String> suggestions = new ArrayList<>();
		
        int j = 1;
        for(int i = 0; i < testWord.length()-1; i++){
			
			String temp = testWord;
			char[] c = temp.toCharArray();

			//System.out.println("Char 1 Before: " + c[i] + " Char 2 Before: " + c[j]);
			
			char tempLetter = c[i];
			c[i] = c[j];
			c[j] = tempLetter;
			
			//System.out.println("Char 1 After: " + c[i] + " Char 2 After: " + c[j]);
			//System.out.println("\n");
			
			String swappedString = new String(c);
			
			//System.out.println(swappedString);
			
			j++;

			if (dictionary.contains(swappedString)) {
				if(!suggestions.contains(swappedString)){
	               	suggestions.add(swappedString);
	            }
		    } 
					
		}     
        
		return suggestions;
		
	}

}