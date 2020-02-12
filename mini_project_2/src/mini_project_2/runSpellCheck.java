package mini_project_2;

import java.io.IOException;

public class runSpellCheck {
	
	public static void main(String args[]) throws IOException {

		SpellCheck spellCheck = new SpellCheck("src/mini_project_2/big.txt", "src/mini_project_2/count_2l.txt");
		
		if(args[0].equals("correctBody")){
			System.out.println(spellCheck.correctBody(args[1]));
		} else if(args[0].equals("determineViability")){
			
			if(spellCheck.calculateViability(args[1])){
				System.out.println(args[1] + " is viable.");
			} else {
				System.out.println(args[1] + " is not viable.");
			}
			
		} else if(args[0].equals("provideSuggestion")){
			System.out.println(spellCheck.provideSuggestions(args[1]));
		} else {
			System.out.println("Error: Cannot read command.");
		}
		
	}

}
