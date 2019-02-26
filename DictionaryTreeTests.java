import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

/**
 * @author Kelsey McKenna
 */
public class DictionaryTreeTests {

	@Test
	public void heightOfRootShouldBeZero() {
		DictionaryTree unit = new DictionaryTree();
		Assertions.assertEquals(0, unit.height());
	}

	@Test
	public void heightOfWordShouldBeWordLength() {
		DictionaryTree unit = new DictionaryTree();
		unit.insert("word", 0);
		Assertions.assertEquals("word".length(), unit.height());
	}

	@Test
	public void longestWordShouldHaveALengthEqualToTheHeightOfTheTree() {
		DictionaryTree unit = new DictionaryTree();
		unit.insert("capybara");
		unit.insert("bridge");
		unit.insert("chip");
		Assertions.assertEquals(unit.height(), unit.longestWord().length());
	}

	@Test
	public void lookingForAWordWithAPrefixThatDoesntExistShouldReturnEmpty() {
		DictionaryTree unit = new DictionaryTree();
		unit.insert("capybara");
		unit.insert("bridge");
		unit.insert("chip");
		Assertions.assertEquals(Optional.empty(), unit.predict("tele"));
	}

	@Test
	public void numberOfLeavesShouldBeNumberOfWordsThatArentPrefixesOfOtherWords() {
		DictionaryTree unit = new DictionaryTree();
		unit.insert("word");
		unit.insert("femur");
		unit.insert("yellow");
		unit.insert("yell");
		unit.insert("feature");
		Assertions.assertEquals(4, unit.numLeaves());
	}
	
	@Test
	public void numLeavesShouldBeOneForAnEmptyTree() {
		DictionaryTree unit = new DictionaryTree();
		Assertions.assertEquals(1, unit.numLeaves());
	}

	@Test
	public void removeTest() {
		DictionaryTree unit = new DictionaryTree();
		String word = "word";
		unit.insert(word);
		String worded = "worded";
		unit.insert(worded);
		String wore = "wore";
		unit.insert(wore);
		Assertions.assertTrue(unit.contains(word)); // The tree should contain the word prior to removal
		Assertions.assertFalse(unit.remove(word)); // The nodes should not be able to be deleted
		Assertions.assertFalse(unit.contains(word)); // The tree should not contain the word post the removal
		Assertions.assertTrue(unit.contains(worded)); // The tree should still contain the word worded
		Assertions.assertTrue(unit.contains(wore)); // and wore
	}

	@Test
	public void sizeTest() {
		DictionaryTree unit = new DictionaryTree();
		unit.insert("word");
		unit.insert("yellow");
		unit.insert("yell");
		unit.insert("feature");
		// 1 (Empty root node) + sum of children of each node
		Assertions.assertEquals(18, unit.size());
	}

	@Test
	public void anEmptyStringShouldNotBeAddedToTheTree() {
		DictionaryTree unit = new DictionaryTree();
		unit.insert("");
		Assertions.assertFalse(unit.contains(""));
	}

	@Test(expected = AssertionError.class)
	public void nullValuesShouldNotBeAddedToTheTree() {
		DictionaryTree unit = new DictionaryTree();
		unit.insert(null);
	}

	@Test
	public void predictingUsingAPrefixThatIsNotInTheTreeShouldReturnEmpty() {
		DictionaryTree unit = new DictionaryTree();
		unit.insert("word");
		Assertions.assertEquals(Optional.empty(), unit.predict("tele"));
	}

	@Test
	public void predictShouldReturnTheShortestWordInTheTreeWithThatPrefix() {
		DictionaryTree unit = new DictionaryTree();
		unit.insert("word");
		unit.insert("telepathic");
		unit.insert("teleport");
		unit.insert("telophase");
		unit.insert("telephone");
		unit.insert("telepath");
		// Should return the most recently added, shortest word with specified prefix in
		// the tree
		Assertions.assertEquals(Optional.of("telepath"), unit.predict("tele"));
	}

	@Test
	public void insertingTwoWordsShouldNotBeAllowed() {
		DictionaryTree unit = new DictionaryTree();
		unit.insert("word");
		unit.insert("word");
		Assertions.assertEquals(5, unit.size());
	}

	@Test
	public void allWordsTest() {
		DictionaryTree unit = new DictionaryTree();
		unit.insert("femur");
		unit.insert("yellow");
		unit.insert("yell");
		unit.insert("feature");
		unit.insert("telepathic");
		unit.insert("teleport");
		unit.insert("telophase");
		unit.insert("telephone");
		unit.insert("telepath");
		unit.insert("omnipotent");
		unit.insert("omnicient");

		List<String> list = unit.allWords();

		Assertions.assertTrue(list.contains("femur"));
		Assertions.assertTrue(list.contains("yellow"));
		Assertions.assertTrue(list.contains("yell"));
		Assertions.assertTrue(list.contains("feature"));
		Assertions.assertTrue(list.contains("telepathic"));
		Assertions.assertTrue(list.contains("teleport"));
		Assertions.assertTrue(list.contains("telophase"));
		Assertions.assertTrue(list.contains("telephone"));
		Assertions.assertTrue(list.contains("telepath"));
		Assertions.assertTrue(list.contains("omnipotent"));
		Assertions.assertTrue(list.contains("omnicient"));
	}

	@Test
	public void predictWithPopularity() {
		DictionaryTree unit = new DictionaryTree();
		unit.insert("teleport", 100);
		unit.insert("telepath", 32);
		unit.insert("teleportation", 79);
		unit.insert("telekinetic", 89);
		unit.insert("telephone", 378);

		@SuppressWarnings("serial")
		ArrayList<String> correctList = new ArrayList<String>() {
			{
				add("telepath");
				add("teleportation");
				add("telekinetic");
				add("teleport");
				add("telephone");

			}
		};

		int n = 5;
		List<String> list = unit.predict("tele", n);

		for (int i = 0; i < n; i++) {
			Assertions.assertEquals(correctList.get(i), list.get(i));
		}
	}

	@Test
	public void foldSum() {
		DictionaryTree unit = new DictionaryTree();
		unit.insert("word");
		unit.insert("cracker");
		unit.insert("happy");
		System.out.println(unit.fold(unit.sizeFun));
		Assertions.assertTrue(unit.fold(unit.sizeFun) == 17);
	}

	@Test
	public void foldHeight() {
		DictionaryTree unit = new DictionaryTree();
		unit.insert("word");
		unit.insert("fickle");
		unit.insert("financial");
		System.out.println(unit.fold(unit.heightFun));
		Assertions.assertTrue(unit.fold(unit.heightFun) == 9);// ("financial".length(), unit.fold(unit.heightFun));
	}

	@Test
	public void foldMaximumBranching() {
		DictionaryTree unit = new DictionaryTree();
		unit.insert("word");
		unit.insert("woke");
		unit.insert("whale");
		unit.insert("waneing");
		unit.insert("water");
		unit.insert("wait");
		unit.insert("wade");
		unit.insert("wane");
		unit.insert("wanted");
		// Max 4 branches off of "wa" - n - t - i - d
		System.out.println("foldMaxBranch: " + unit.fold(unit.maximumBranchingFun));
		Assertions.assertTrue(unit.fold(unit.maximumBranchingFun) == 4);
	}

	@Test
	public void foldNumLeaves() {
		DictionaryTree unit = new DictionaryTree();
		unit.insert("word");
		unit.insert("femur");
		unit.insert("yellow");
		unit.insert("yell");
		unit.insert("feature");
		System.out.println("foldNumLeaves: " + unit.fold(unit.numLeavesFun));
		Assertions.assertTrue(unit.fold(unit.numLeavesFun) == 4);
	}
	
}
