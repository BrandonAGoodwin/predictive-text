import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;

public class DictionaryTree {

	private Map<Character, DictionaryTree> children = new LinkedHashMap<>();
	public Optional<Integer> popularity = Optional.empty();
	public boolean wordEnd = false;

	/**
	 * Inserts the given word into this dictionary. If the word already exists,
	 * nothing will change.
	 *
	 * @param word
	 *            the word to insert
	 */
	void insert(String word) {
		assert (word != null);

		if (word.isEmpty()) {
			return;
		}

		Character firstChar = getFirstCharacter(word);

		// If the character isn't already in the tree, add it
		children.putIfAbsent(firstChar, new DictionaryTree());

		DictionaryTree d = children.get(firstChar);

		if (word.length() == 1) {
			children.get(firstChar).wordEnd = true;
			return;
		}

		d.insert(word.substring(1));
	}

	/**
	 * Inserts the given word into this dictionary with the given popularity. If the
	 * word already exists, the popularity will be overridden by the given value.
	 *
	 * @param word
	 *            the word to insert
	 * @param popularity
	 *            the popularity of the inserted word
	 */
	void insert(String word, int popularity) {
		assert (word != null);

		if (word.isEmpty())
			return;

		Character firstChar = getFirstCharacter(word);

		if (children.containsKey(firstChar) != true) {
			children.put(firstChar, new DictionaryTree());
		}

		DictionaryTree d = children.get(firstChar);

		if (word.length() == 1) {
			d.popularity = Optional.of(popularity);
			d.wordEnd = true;
			return;
		}

		d.insert(word.substring(1), popularity);
	}

	/**
	 * Removes the specified word from this dictionary. Returns true if the caller
	 * can delete this node without losing part of the dictionary, i.e. if this node
	 * has no children after deleting the specified word.
	 *
	 * @param word
	 *            the word to delete from this dictionary
	 * @return whether or not the parent can delete this node from its children
	 */
	boolean remove(String word) {
		assert (word != null);
		if (word.length() == 0) {
			return false;
		}
		Character firstChar = getFirstCharacter(word);
		if (children.containsKey(firstChar) == false) {
			return false;
		}
		if (word.length() == 1) { // A 0 length string should never be added

			// If height == 1 then
			if (height() == 1) {
				children.remove(firstChar);
			} else {
				children.get(firstChar).wordEnd = false;
				children.get(firstChar).popularity = Optional.empty();
			}

			return true;
		} else {
			if (maximumBranching() < 2 && height() == word.length()) {
				children.remove(firstChar);
				return true;
			} else {
				children.get(firstChar).remove(word.substring(1));
				return false;
			}
		}
	}

	/**
	 * Determines whether or not the specified word is in this dictionary.
	 *
	 * @param word
	 *            the word whose presence will be checked
	 * @return true if the specified word is stored in this tree; false otherwise
	 */
	boolean contains(String word) {
		assert (word != null);

		if (word.isEmpty()) {
			return wordEnd;
		} else if (children.containsKey(getFirstCharacter(word))) {
			if (word.length() == 1) {
				System.out.println(word.substring(1));
			}
			return children.get(getFirstCharacter(word)).contains(word.substring(1));
		} else {
			return false;
		}

	}

	/**
	 * @param prefix
	 *            the prefix of the word returned
	 * @return a word that starts with the given prefix, or an empty optional if no
	 *         such word is found.
	 */
	Optional<String> predict(String prefix) {
		assert (prefix != null);
		if (isPrefix(prefix) != true) {
			return Optional.empty();
		} else {
			return predicting(prefix);
		}
	}

	Optional<String> predicting(String prefix) {
		if (prefix.isEmpty()) {
			if (children.keySet().iterator().hasNext() && wordEnd == false) {
				Character nextChar = children.keySet().iterator().next();
				return Optional.of(nextChar.toString() + children.get(nextChar).predicting(prefix).get());
			} else {
				return Optional.of("");
			}
		} else {
			Character firstChar = getFirstCharacter(prefix);

			return Optional.of(firstChar.toString() + children.get(firstChar).predicting(prefix.substring(1)).get());

		}

	}

	/**
	 * Predicts the (at most) n most popular full English words based on the
	 * specified prefix. If no word with the specified prefix is found, an empty
	 * Optional is returned.
	 *
	 * @param prefix
	 *            the prefix of the words found
	 * @return the (at most) n most popular words with the specified prefix
	 */
	List<String> predict(String prefix, int n) {
		assert (n >= 0);
		assert (prefix != null);

		ArrayList<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>();

		predicting(prefix, "", list);

		list.sort(Entry.comparingByValue());
		int index = 0;
		List<String> sortedList = new LinkedList<String>();
		while (index < n && index < list.size()) {
			// System.out.println("Adding: " + list.get(index).getKey());
			sortedList.add(index, list.get(index).getKey());
			index++;
		}
		return sortedList;
	}

	void predicting(String prefix, String word, List<Entry<String, Integer>> list) {
		assert (prefix != null);

		if (prefix.isEmpty()) { //
			for (Character c : children.keySet()) {
				if (children.get(c).wordEnd == true) {
					list.add(new Word<String, Integer>(new String(word + c.toString()),
							children.get(c).popularity.orElse(0)));
				}
				children.get(c).predicting(prefix, word + c.toString(), list);

			}

		} else if (prefix.length() == 1) {
			if (children.containsKey(getFirstCharacter(prefix))) {
				if (children.get(getFirstCharacter(prefix)).wordEnd == true) {
					list.add(new Word<String, Integer>(word + new String(prefix),
							children.get(getFirstCharacter(prefix)).popularity.orElse(0)));
				}
				children.get(getFirstCharacter(prefix)).predicting("", word + getFirstCharacter(prefix), list);
			}

		} else {
			if (children.containsKey(getFirstCharacter(prefix))) {
				children.get(getFirstCharacter(prefix)).predicting(prefix.substring(1),
						word + getFirstCharacter(prefix), list);
			}
		}
	}

	/**
	 * @return the number of leaves in this tree, i.e. the number of words which are
	 *         not prefixes of any other word.
	 */
	int numLeaves() {

		return fold(numLeavesFun);

		/*
		 * int numLeaves = 0;
		 * 
		 * for (Character c : children.keySet()) { if (children.get(c).size() > 1) {
		 * numLeaves += children.get(c).numLeaves(); } else { numLeaves += 1; } }
		 * 
		 * return numLeaves;
		 */
	}

	/**
	 * @return the maximum number of children held by any node in this tree
	 */
	int maximumBranching() {
		return fold(maximumBranchingFun);

		/*
		 * int maxBranch = children.size();
		 * 
		 * for (Character c : children.keySet()) { int childBranchNo =
		 * children.get(c).maximumBranching(); if (childBranchNo > maxBranch) {
		 * maxBranch = childBranchNo; } }
		 * 
		 * return maxBranch;
		 */
	}

	/**
	 * @return the height of this tree, i.e. the length of the longest branch
	 */
	int height() {
		return fold(heightFun);

		/*
		 * if (children.size() == 0) { return 0; } int maxHeight = 0; for (Character c :
		 * children.keySet()) { int childHeight = children.get(c).height(); if
		 * (childHeight > maxHeight) { maxHeight = childHeight; } }
		 * 
		 * return maxHeight + 1;
		 */
	}

	/**
	 * @return the number of nodes in this tree
	 */
	int size() {
		return fold(sizeFun);
		/*
		 * int size = 1;
		 * 
		 * for (Character c : children.keySet()) { size += children.get(c).size(); }
		 * 
		 * return size;
		 */
	}

	/**
	 * @return the longest word in this tree
	 */
	String longestWord() {

		if (children.size() == 0) {
			return "";
		} else {
			int maxHeight = 0;
			Character currentChar = new Character(children.keySet().iterator().next());
			for (Character c : children.keySet()) {
				int childHeight = children.get(c).height();
				if (childHeight > maxHeight) {
					maxHeight = childHeight;
					currentChar = c;
				}
			}
			return currentChar.toString() + children.get(currentChar).longestWord();
		}

	}

	/**
	 * @return all words stored in this tree as a list
	 */
	List<String> allWords() {
		LinkedList<String> list = new LinkedList<String>();
		allWords2("", list);
		return list;
	}

	void allWords2(String prefix, LinkedList<String> words) {
		for (Character c : children.keySet()) {
			if (children.get(c).wordEnd == true) {
				words.add(new String(prefix + c.toString()));
			}
			children.get(c).allWords2(new String(prefix + c.toString()), words);
		}
	}

	/**
	 * Folds the tree using the given function. Each of this node's children is
	 * folded with the same function, and these results are stored in a collection,
	 * cResults, say, then the final result is calculated using f.apply(this,
	 * cResults).
	 *
	 * @param f
	 *            the summarising function, which is passed the result of invoking
	 *            the given function
	 * @param <A>
	 *            the type of the folded value
	 * @return the result of folding the tree using f
	 */
	@SuppressWarnings("serial")
	<A> A fold(BiFunction<DictionaryTree, Collection<A>, A> f) {
		return f.apply(this, new LinkedList<A>() {
			{
				for (Character c : children.keySet()) {
					add(children.get(c).fold(f));
				}
			}
		});
	}

	/**
	 * For each node in the tree output 1 + sum of values in the collection
	 */
	BiFunction<DictionaryTree, Collection<Integer>, Integer> sizeFun = (x, y) -> {
		Integer val = y.stream().mapToInt(Integer::intValue).sum() + 1;
		y.clear();
		return val;
	};

	/**
	 * Add 1 to the maximum height calculated so far to get the maximum height
	 */
	BiFunction<DictionaryTree, Collection<Integer>, Integer> heightFun = (x, y) -> {
		return y.size() > 0 ? Collections.max(y) + 1 : 0;
	};

	/**
	 * Get the maximum branch value stored in the collection, if it is less than the
	 * branch factor of the current node, add the branch factor of the current node
	 * to the collection, then output the biggest value in the collection.
	 */
	BiFunction<DictionaryTree, Collection<Integer>, Integer> maximumBranchingFun = (x, y) -> {
		return y.size() > 0 ? Collections.max(y) > x.children.size() ? Collections.max(y) : x.children.size()
				: x.children.size();
	};

	/**
	 * Add 1 to the collection if the current node is a leave node (has no
	 * children), and then output the sum of the collection
	 */
	BiFunction<DictionaryTree, Collection<Integer>, Integer> numLeavesFun = (x, y) -> {
		return y.stream().mapToInt(Integer::intValue).sum() + (x.children.size() == 0 ? 1 : 0);
	};

	Character getFirstCharacter(String word) {
		assert (word != null && !word.isEmpty());
		return new Character(word.charAt(0));
	}

	boolean isPrefix(String word) {
		assert (word != null);

		if (word.isEmpty()) {
			return true;
		} else if (children.containsKey(getFirstCharacter(word))) {
			return children.get(getFirstCharacter(word)).isPrefix(word.substring(1));
		} else {
			return false;
		}
	}

}

class Word<K, V> implements Map.Entry<K, V> {

	private final K key;
	private V value;

	public Word(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		return this.value = value;
	}

}