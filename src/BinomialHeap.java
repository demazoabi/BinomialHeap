
/**
 * BinomialHeap
 * An implementation of binomial heap over non-negative integers.
 * Based on exercise from previous semester.
 */


public class BinomialHeap
{
    public int size;
    public HeapNode last;
    public HeapNode min;

    public BinomialHeap(){//empty constructor
        size = 0;
        last = null;
        min = null;
    }
    public BinomialHeap(HeapNode last, HeapNode min){//"copy" constructor
        size = (int) (Math.pow(2,last.rank+1) - 1);
        this.last = last;
        this.min = min;
    }
    /**
     *
     * pre: key > 0
     * Insert (key,info) into the heap and return the newly generated HeapItem.
     *
     */
    public HeapItem insert(int key, String info) {//inser
        //create nodes
        HeapNode currheapnode = new HeapNode(key,info);
        HeapItem retval = currheapnode.item;
        //if heap is empty
        if(this.empty()){
            last = currheapnode;
            min = currheapnode;
            currheapnode.next = currheapnode;
            size = 1;
        }
        else if ((size % 2 ) == 1){//meld if is the size is odd
            BinomialHeap tmpHeap = new BinomialHeap();
            retval = tmpHeap.insert(key,info);
            this.meld(tmpHeap);

        }
        else if (last.next.rank != 0){// connect the node to last and too last.next if the rank of last node is !=0
            currheapnode.next = last.next;
            last.next= currheapnode;
            size++;
            if(currheapnode.item.key<min.item.key) {//maintaining min
                min=currheapnode;
            }
        }
        return retval;
        //return currheapnode.item;
    }
    /**
     *
     * Delete the minimal item
     *
     */
    public void deleteMin() {
        if(size == 1){//edge case size == 1
            this.size = 0;
            this.min = null;
            this.last = null;
            return;
        }
        if (min.rank == 0){//edge case only to detach last from heap and search new minimum
            last.next = min.next;
            searchMin();// log(n) operations
            size--;//maintaining size
            return;
        }
        if (size == 2){// if size == 2
            this.size = 1;
            last = min.child;
            min.child.parent = null;
            min = min.child;
            return;
        }
        if(min.next == min){// the size = 2^k (k in N)
            min.child.parent = null;
            BinomialHeap binomialHeap = new BinomialHeap(min.child,min.child);
            HeapNode traversed = min.child.next;
            while (traversed != min.child){// search minimum in the new binomial heap
                traversed.parent = null;
                traversed = traversed.next;
            }
            binomialHeap.searchMin();
            this.size = binomialHeap.size;
            this.min = binomialHeap.min;
            this.last = binomialHeap.last;
            return;
        }
        // no edge cases - meld two binomial heaps.
        HeapNode currNode = last;
        while (currNode.next != min){
            currNode = currNode.next;
        }
        if(min == last){
            last = currNode;
        }
        size -= Math.pow(2,min.rank);
        currNode.next = min.next;
        HeapNode NewLast = min.child;
        HeapNode traversed = min.child;
        for(int k = 0 ; k <= NewLast.rank ; k++){
            traversed.parent = null;
            traversed = traversed.next;
        }
        BinomialHeap bin = new BinomialHeap(NewLast,NewLast);
        bin.searchMin();
        this.searchMin();
        meld(bin);
        this.searchMin();// end
    }
    private void searchMin(){// in order to maintain the minimal value.

        HeapNode currNode = last.next;
        HeapNode retval = last;
        while (currNode != last){
            if(currNode.item.key <= retval.item.key){
                if(currNode.parent == null){
                retval = currNode;
                }
            }
            currNode = currNode.next;
        }

        min = retval;
    }
    /**
     *
     * Return the minimal HeapItem
     *
     */
    public HeapItem findMin(){// getter
        if (empty()){return null;}// if Heap is empty
        return min.item;
        // should be replaced by student code
    }

    /**
     *
     * pre: 0<diff<item.key
     *
     * Decrease the key of item by diff and fix the heap.
     *
     */
    public void decreaseKey(HeapItem item, int diff)
    {   item.key = item.key - diff;// decreasing
        HeapNode currParent = item.node.parent;
        HeapNode currentChild = item.node;
        while (currParent != null && item.key < currParent.item.key){// while there's parent, and while the node is indeed smaller.
            HeapItem tmpItem = currParent.item;
            currParent.item = item;
            item.node = currParent;
            currentChild.item = tmpItem;
            tmpItem.node = currentChild;
            currentChild = currParent;
            currParent = currParent.parent;
        }
        if (min.item.key > item.key){
            min = item.node;
        }
    }

    /**
     *
     * Delete the item from the heap.
     *
     */
    public void delete(HeapItem item)
    {
        decreaseKey( item, item.key + 1);// decrease the key to -1 (we know all nodes are indeed positive
        this.deleteMin();// delete "catches" all edge cases and erases the item
        return; // should be replaced by student code
    }
    private HeapNode returnBigger(HeapNode root1, HeapNode root2){// returns a smaller node, when given two nodes
        if (root1.item.key > root2.item.key){
            return root1;
        }
        return root2;
    }
    private HeapNode returnSmaller(HeapNode root1, HeapNode root2){// returns the "contrary" node compared to returnBigger
        if (root1.item.key > root2.item.key){
            return root2;
        }
        return root1;
    }
    /**
     *
     * Meld the heap with heap2
     *
     */
    public void meld(BinomialHeap heap2)
    {   if (heap2.empty()){//empty heap2 - no work needed
        return;
    }
        if (this.empty()){// if this empty- "inherit" heap2
            last = heap2.last;
            size = heap2.size;
            min = heap2.min;
            return;
        }
        if (heap2.size == 1 && this.size % 2 == 0){// if the work needed is only to join 1 node
            heap2.last.next = last.next;
            last.next = heap2.last;
            size++;
            if(heap2.min.item.key < this.min.item.key){// maintains "min"
                this.min = heap2.min;
            }
            return;
        }
        HeapItem minItem;
        if (this.min.item.key < heap2.min.item.key){
            minItem = this.min.item;
        }
        else {
            minItem = heap2.min.item;
        }
        if (heap2.size == 1){// takes care of "insert" or equivalent cases.
            size++;// size maintenance
            HeapNode carry = heap2.last;
            HeapNode pointer1 = last.next;
            while (carry != null){
                if (pointer1 == last && pointer1.rank == carry.rank){// if "we" reached to the end
                    carry = add(carry, pointer1);
                    carry.next = carry;
                    min = carry;
                    last = carry;
                    while (min.parent != null){// takes care of edge cases if we inserted many values that are the minimum
                        min = min.parent;
                    }
                    return;// we did it boys we melt the tree
                }
                else if(carry.rank== pointer1.rank){// connects the trees and implements the carry
                    HeapNode tmp = pointer1.next;
                    carry = add(carry, pointer1);
                    last.next = pointer1.next;
                    pointer1 = tmp;
                }

                else if (pointer1.rank > carry.rank){
                    carry.next = pointer1;
                    last.next = carry;
                    min = minItem.getNode();
                    carry = null;
                    while (min.parent != null){// takes care of cases when the minimum value has many duplicates within the array
                        min = min.parent;
                    }
                }
            }

            return;// we did it boys we melt the tree
        }
        // creating representing array
        int maxTrees1 = this.last.rank+1;
        int maxTrees2 = heap2.last.rank+1;
        this.size += heap2.size;// maintaining size
        int totalTrees = maxTrees1 + maxTrees2;
        HeapNode[] BinaryRep1 = new HeapNode[totalTrees];
        HeapNode[] BinaryRep2 = new HeapNode[totalTrees];
        HeapNode[] BinaryRepTot = new HeapNode[totalTrees];
        HeapNode pointer1 = this.last;
        for(int i = 0 ; i < maxTrees1 ; i++){
            int Rank = pointer1.rank;
            BinaryRep1[Rank] = pointer1;
            pointer1 = pointer1.next;
        }
        HeapNode pointer2 = heap2.last;
        for (int i = 0 ; i < maxTrees2; i++){
            BinaryRep2[pointer2.rank] = pointer2;
            pointer2 = pointer2.next;
        }
        // end of "creating representing arrays"
        HeapNode carrier = null;
        for (int j = 0 ; j <BinaryRep1.length ; j++) {//performs binary addition
            if (BinaryRep1[j] != null && BinaryRep2[j] == null && carrier == null)
            {// simple addition 1+0
                BinaryRepTot[j] = BinaryRep1[j];
            }
            else if (BinaryRep1[j] == null && BinaryRep2[j] != null && carrier == null)
            {//simple addition 0+1
                BinaryRepTot[j] = BinaryRep2[j];
            }
            else if (BinaryRep1[j] != null && BinaryRep2[j] != null && carrier == null)
            {// 1+1
                carrier = add(BinaryRep1[j], BinaryRep2[j]);
            }
            else if (BinaryRep1[j] == null && BinaryRep2[j] != null && carrier != null)
            { // 0+ 1 + carry
                carrier = add(BinaryRep2[j], carrier);
            }
            else if (BinaryRep1[j] != null && BinaryRep2[j] == null && carrier != null)
            { // 1 + 0 + carry
                    carrier = add(BinaryRep1[j], carrier);
            }
            else if (BinaryRep1[j] != null && BinaryRep2[j] != null && carrier != null)
            {// 1+ 1 +1 + carry
                    BinaryRepTot[j] = carrier;
                    carrier = add(BinaryRep1[j], BinaryRep2[j]);

            }
            else if (BinaryRep1[j] == null && BinaryRep2[j] == null && carrier != null)
            { // 0 + 0 + carry
                    BinaryRepTot[j] = carrier;
                    carrier = null;
            }

        }   //concatenates the array to a cohesive heap
            HeapNode original = null;
            HeapNode current= null;
            boolean hasMet = false;
            int counter = 0;
            for (int k = 0 ; k < BinaryRepTot.length; k++){// loops over roots in order to connect them
                if (!hasMet && BinaryRepTot[k] != null)
                {// the first tree to be added needs special treatmant (as pointers are null
                    hasMet = true;
                    current = BinaryRepTot[k];
                    original = BinaryRepTot[k];
                }
                else if (hasMet && BinaryRepTot[k] != null)
                {
                    current.next = BinaryRepTot[k];
                    current = current.next;
                }
            }
            current.next = original;
            last = current;
            min = minItem.node;
                while (min.parent != null){// if multiple nodes share the same value it may happen that the minimums create a tree.
                    min = min.parent;
                }
        }

    /**
     * "rewires" two at O(1)
     * @param Heapptr1 - first node to be merged
     * @param Heapptr2- second Node to be merged
     * @return - the node that was selected to stand at the top
     */
    private HeapNode add(HeapNode Heapptr1 ,HeapNode Heapptr2){// adds two binomial trees with "rewiring" O(1)
        HeapNode smaller = returnSmaller(Heapptr1,Heapptr2);
        HeapNode bigger = returnBigger(Heapptr1,Heapptr2);
        if (smaller.rank == 0){// they are of rank 0 - Edge case
            smaller.child = bigger;
            bigger.parent = smaller;
            bigger.next = bigger;
        }
        else{//else, they are rewired differently
           bigger.next = smaller.child.next;
           smaller.child.next = bigger;
           bigger.parent = smaller;
           smaller.child = bigger;
        }
        smaller.rank++;//maintaining rank
        return smaller;
    }

    private void swap (HeapNode node1,HeapNode node2){//swaps two nodes and items O(1)
        HeapItem tmpItem = node1.item;
        node1.item = node2.item;
        node1.item.node = node1;
        node2.item = tmpItem;
        node2.item.node = node2;
    }
    /**
     *
     * Return the number of elements in the heap
     *
     */
    public int size()
    {
        return size; // should be replaced by student code
    }

    /**
     *
     * The method returns true if and only if the heap
     * is empty.
     *
     */
    public boolean empty()
    {
        return size == 0; // should be replaced by student code
    }

    /**
     *
     * Return the number of trees in the heap.
     *
     */

    public int numTrees()
    {	//we know that the amount of trees is exactly the amount of 1's in the binary representation

        String StrSize = Integer.toBinaryString(size);
        int Retval = 0;
        for (char ch : StrSize.toCharArray()){
            if (ch == '1'){
                Retval++;
            }
        }
        return Retval; // should be replaced by student code
    }



    /**
     * Class implementing a node in a Binomial Heap.
     *
     */
    public class HeapNode{
        public HeapItem item;
        public HeapNode child;
        public HeapNode next;
        public HeapNode parent;
        public int rank;
        public HeapNode(HeapItem item){// inheritance "constructor"
            this.item = item;
            child = null;
            next = null;
            parent = null;
            rank = 0;
            this.item.node = this;
        }

        /**
         *
         * @param key-
         * @param info-
         */
        public HeapNode(int key, String info){// HeapNode
            this.item = new HeapItem(this,key,info);
            this.parent = null;
            this.rank = 0;
            this.next = null;
            this.child = null;
        }

        public HeapItem getItem() {
            return item;
        }

        public HeapNode getChild() {
            return child;
        }

        public HeapNode getNext() {
            return next;
        }

        public HeapNode getParent() {
            return parent;
        }

        public int getRank() {
            return rank;
        }
        public void setRank(int rank) {
            this.rank = rank;
        }

        public void setChild(HeapNode child) {
            this.child = child;
        }

        public void setItem(HeapItem item) {
            this.item = item;
        }

        public void setNext(HeapNode next) {
            this.next = next;
        }

        public void setParent(HeapNode parent) {
            this.parent = parent;
        }
    }
    /**
     * Class implementing an item in a Binomial Heap.
     *
     */
    public class HeapItem{
        public HeapNode node;
        public int key;
        public String info;

        public HeapItem(HeapNode node, int key,String info){
            this.key = key;
            this.node = node;
            this.info = info;
        }

        public HeapNode getNode() {
            return node;
        }

        public String getInfo() {
            return info;
        }
        public int getKey() {
            return key;
        }

    }


}

