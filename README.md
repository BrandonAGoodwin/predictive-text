# Solution

### Consistencies / Decisions Made
* The minimum possible size that can be retrieved using `size()` is 1. This is because the size is the number of nodes the tree has. (Nodes being `DictionaryTree`s, and edges are the characters)
* The minimum possible height that can be retrieved is 0. This is because the tree will always have at least one node so it can never be less than 0.
* Assert that all inputs are not null.
* An empty string cannot be added to the tree.
* The number of values, `n`, to be returned by running `predict(String prefix, int n)` must be greater than or equal to zero.

### Notable Functionality
* A user can use the predict function with an empty string and it will just return the top `n` most popular words.

### Use of Optional Types
* The `popularity` is stored as an `Optional` type. And it is set to `Optional.empty()` as default.

### Additional Variables
* (boolean)`wordEnd`: Stores the boolean value that represents whether the current node is the end node of a word. (Set to false as default)
* (Optional<Integer>)`popularity`: Stores the popularity of a word if it is given one when it is inserted. (Set to `Optiona.empty()` as default)

## Size
* Size the size of the current node is set to 1 and then for each child node size is called recursively and value returned is added to the size of the current node and returned at the end of the function.

#### Size: BiFunction
* The `BiFunction` for size returns the sum of all values of the collection + 1 in each recursive call, and it is called once for each node, so the number of nodes in the tree is output at the end.

## Height
* If the current node has node children it returns 0.
* Otherwise it gets the `height()` of each of its children and compares it to a stored `maxHeight` value (set to `0`), if it is greater than the `maxHeight` value then `maxHeight` is set to the height of current child being called.
* Once all the children have been looped through it returns `maxHeight + 1`.

#### Height: BiFunction
* The collection stores the height value for each sub-tree.
* Checks that there are values in the collection.
* If there are values in the collection, it returns the max value in the collection + 1, otherwise it returns 0.

## Maximum Branching
* The `maxBranch` is set the number of branches the current node has.
* Then `maxBranch` is compared against the value returned by calling `maximumBranching()` on each of the children nodes.
* If any of the values returned are greater than the current value of `maxBranch`, `maxBranch` is set to this greater value.
* Then `maxBranch` is returned.

#### Maximum Branching: BiFunction
* The collection stores the maximumBranching value for each sub-tree.
* Checks that there are values in the collection.
* If there are values in the collection, then the max value in the selection is compared to the no of branches of the current node and the greater of the two is returned.

## Longest Word
* `longestWord()` calls `height()` on each of the children nodes of the current node, and the subtree that has the greatest height is traversed.
* The corresponding character is returned with the recursive call of `longestWord()` on the subtree with the greatest height is concatenated on the end.
* If the current node has no children an empty string is returned and no more recursive calls are made.

## Num Leaves
* `numLeaves()` returns the value `numLeaves` which is the sum of of the recursive call of `numLeaves()` on each of the children nodes.
* If the child node currently being looked at has no children nodes then 1 is added to `numLeaves`.
* Then `numLeaves` is returned.

#### Num Leaves: BiFunction
* The collection stores the number of leaves of each sub-tree.
* I return the sum of all the values in the collection + 1 it if the current node is a leaf or + 0 otherwise.

## Contains
* First assert that the input `word != null`.
* If `word` is empty (has length 0) then the value of `wordEnd` of the current node is returned. This is because the characters of the word being checked may exist in the tree as a prefix, but not as its own word. In which case the word should be considered to not be contained within the tree.
* If the word isn't empty, then I check that the first character of the word is contained within the key values of the `children` hash map.
* If the character is contained within the hash map then `contains()` is called on `word` with the first character removed.
* If the character isn't contained with the hash map `false` is returned.

## All Words
* `allWords()` creates a list and passes this list to the recursive helper function `allWords2(String prefix, LinkedList<String> words)`, which uses this list as an accumulator of sorts, to store all words found so far.
* Prefix is all the characters that have been previously traversed to each this node.
* Then returns the list created.
* `allWords2()` goes through each child node of current node, and if the child node currently being looked at is the end of a word (`wordEnd == true`) then the `prefix` + the Character which is the key for the child node being looked at is added to the list `words`.
* Then `allWords2()` is called on each of the children nodes with the new prefix = the old prefix + the character which is the key for the child node currently being looked at.
* Once this is complete for all nodes, `list` will contain all the words that are stored within the tree.

## Insert
* Insert without popularity, inserts each character of the word one at a time, if the node for that character doesn't already exist, on is created, and then insert is recursively called on the remainder of the word (`word.substring(1)`).
* If the word has length 1 when insert is called, then the last character is added to the children node and that `DictionaryTree` has it's `wordEnd` value set to true.
* If the word already exists the `popularity` is not changed.

#### Popularity
* If insert with popularity is used, the same process occurs as normal insert, but when then word inserted has length 1, then the `popularity` value of the last node inserted is set to the specified value, regardless of its previous value.

## Remove
### Cases:
The whole word can be removed without removing children nodes of other words:
* At each recursive call of the remove method, I check the maximum branching and the height of the tree (that the next character is the key to). If the height is greater than the length of the word then I know I can't remove the word as it is a prefix of something else and return false.
* And if the maximum branching is greater than 1 then I know that part of the word is a prefix to multiple words so I cannot remove it (at least at this recursive call)
Part of the word can be removed without removing children nodes of other words:
* If one of the later recursive calls gets maximum branching is <= 0 and height is equal to the height of the remaining characters, I know I can remove the rest of the word. I do this by removing the character we are currently looking at from the children hash map.
None of the word can be removed without removing children nodes of other words:
* If the whole word is a prefix for at least one other word, then I do not remove any of the nodes from the tree, but I do set `wordEnd` in the end node of the word to `false` and set the `popularity` to `Optional.empty()`. This is to make sure in the scenario when a word with a `popularity` value is removed and the same word is removed using `insert(String word)`, that the re-insertion of the word does not maintain the `popularity` value of the last insertion.

## Predict
* `predict(String prefix)` asserts that the input `prefix != null`.
* It checks that the input `prefix` is a prefix using a helper method called `isPrefix(String word)`. This method checks if those character are contained within the tree and returns true if they are regardless of whether or not they are a word end.
* If the input `prefix` is a prefix then the helper method `predicting(prefix)` is called.
* `predicting()` cheeks if `prefix` is empty (length = 0). If it is not empty, then we get the first character of `prefix` and return the first character + the recursive call of `predicting` with the `substring(1)` of the prefix as the input, and called on the `DictionaryTree` that corresponds to the first character of the prefix.
* If `prefix` isn't empty (length > 0), then checks if the current node is the end of a word (`wordEnd = true`), if so then it returns `Options.of("")`, as this is a full word now. Otherwise it checks if the current node has any children, if so it uses an iterator to get one of the children characters, then returns the character + the recursive call of prediction on the child DictionaryTree.

#### Popularity
* `predict(String prefix, int n)` asserts that `n >= 0` and that `prefix != null`.
* `predict()` creates an `ArrayList` that stores `Entry<String, Integer>`which is passed to the helper function `predicting(String prefix, String word, List<Entry<String, Integer>> list)`.
* Then `predicting()` runs similarly to normal `prefix()` when  `prefix` isn't empty (length > 0). When prefix is empty then `predicting()` runs similarly to `allWords()`, just instead of traversing one of the children nodes to find a word, all of the children nodes are traversed and when a word end is found it is added to the `list`. The method only stops being called recursively when a node has no children.
* Once `predicting()` is done, the list is sorted according to its popularity value, then the top `n` most popular words are copied into a new `sortedList` which is then returned at the end of the method.
