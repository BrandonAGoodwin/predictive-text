import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author Kelsey McKenna
 */
public class CLI {

    /**
     * Loads words (lines) from the given file and inserts them into
     * a dictionary.
     *
     * @param f the file from which the words will be loaded
     * @return the dictionary with the words loaded from the given file
     * @throws IOException if there was a problem opening/reading from the file
     */
    static DictionaryTree loadWords(File f) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String word;
            DictionaryTree d = new DictionaryTree();
            int popularity = 0;
            while ((word = reader.readLine()) != null) {
                d.insert(word, popularity++);   
            }

            return d;
        }catch (FileNotFoundException e) {
        	e.printStackTrace();
			return null;
		}
    }

    public static void main(String[] args) throws IOException {
    	
    	DictionaryTree d = new DictionaryTree();
    	d.insert("femur");
    	d.insert("yellow");
    	d.insert("yell");
    	d.insert("feature");
    	d.insert("telepathic");
    	d.insert("teleport");
    	d.insert("telophase");
    	d.insert("telephone");
    	d.insert("telepath");
    	d.insert("omnipotent");
    	d.insert("omnicient");
    	d.insert("longestwordandonlywordthatbeginswithl");
    	
    	System.out.println("\nTree Size: " + d.size());
    	System.out.println("Number of Leaves: " + d.numLeaves());
    	System.out.println("Maximum Branching: " + d.maximumBranching());
    	System.out.println("Tree Height: " + d.height());
    	System.out.println("All Words");
    	for(String s : d.allWords()) 
    		System.out.println("\t" + s);
    	System.out.println("Longest Word: " + d.longestWord());
    	
    	System.out.println("\nRemoving longestwordandonlywordthatbeginswithl");
    	System.out.println("Removed child nodes: " + d.remove("longestwordandonlywordthatbeginswithl"));
    	System.out.println("Longest Word: " + d.longestWord());
    	
    	
    	System.out.println("Word With Prefix 'tele': " + d.predict("femur").orElse(""));
    	
        System.out.print("Loading dictionary ... ");
        
        DictionaryTree d1 = loadWords(new File(args[0]));
        System.out.println("done");

        System.out.println("Longest Word: " + d1.longestWord());
        System.out.println("Maximum Branching: " + d1.maximumBranching());
        
        System.out.println("\nEnter prefixes for prediction below.");

        try (BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
            	List<String> list = d1.predict(fromUser.readLine(), 5);
            	for(String s : list) {
            		 System.out.println("---> " + s);
            	}  
            }
        }
    }

}
