
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PHPArray<V> implements Iterable<V> {
    private static final int INIT_CAPACITY = 4;

    private int N; // number of key-value pairs in the symbol table
    private int M; // size of linear probing table
    private Node<V>[] entries; // the table
    private Node<V> head; // head of the linked list
    private Node<V> tail; // tail of the linked list
    private MyIterator<V> iterator;
    // create an empty hash table - use 16 as default size
    public PHPArray() {
        this(INIT_CAPACITY);
    }

    // create a PHPArray of given capacity
    public PHPArray(int capacity) {
        M = capacity;
        @SuppressWarnings("unchecked")
        Node<V>[] temp = (Node<V>[]) new Node[M];
        entries = temp;
        head = tail = null;
        N = 0;
    }

    public Pair<V> each(){
        // if(iterator == null)
        //     return null;     
        return iterator.nextPair(); //Call iterator next pair to iterate thru list
    }
    public void reset()
    {
        iterator = new MyIterator<V>(head); //Reset iterator to point to head
    }

    public Iterator<V> iterator()
    {
        return new MyIterator<V>(head); //Call new iterator for for loop usage
    }
  
    public ArrayList<String> keys() { //Return every key in table
        ArrayList<String> listyBoi = new ArrayList<String>();
        for (Node<V> n = head; n != null; n = n.next) {
            listyBoi.add(n.key);
        }
        return listyBoi;
    }

    public ArrayList<V> values() { //Return every value in table
        ArrayList<V> listyBoibro = new ArrayList<V>();
        for (Node<V> n = head; n != null; n = n.next) {
            listyBoibro.add(n.value);
        }
        return listyBoibro;
    }

    public void showTable() { //Print out table with for loop
        System.out.println("\tRaw Hash Table Contents:");
        for (int i = 0; i < M; i++) {
            System.out.print(i + ": ");
            if (entries[i] == null) {
                System.out.println("null");
            } else {
                System.out.println("Key: " + entries[i].key + " Value: " + entries[i].value);
            }
        }
    }

    public int length() { //How many key value pairs stored in table
        return N;
    }

    public void sort() throws ClassCastException { //Sorted by creating a list of Pairs and then using Collection class's sort method

        ArrayList<Pair<V>> list = new ArrayList<Pair<V>>();
        //if(head.value instanceof Comparable) System.out.println(head.value.getClass());
        for (Node<V> n = head; n != null; n = n.next) {
            if(!(n.value instanceof Comparable)) throw new ClassCastException();    //I tested this and it says StringBuilder is of type comparable so I don't konw why it would throw an exception like the one shown on the output
            list.add(new Pair<V>(n.key, n.value));
        }
        Collections.sort(list);
        empty(); //Clear the original table
        for(int i = 0; i< list.size();i++){
            put(i,list.get(i).value); //Building up the table with sorted elements; replace original keys with ints starting from 0
        }
    }

    public void asort() throws ClassCastException{ //Same as sort but we preserve the keys

        ArrayList<Pair<V>> list = new ArrayList<Pair<V>>();
        for (Node<V> n = head; n != null; n = n.next) {
            list.add(new Pair<V>(n.key, n.value));
        }
        Collections.sort(list);
        empty();
        for(int i = 0; i< list.size();i++){
            put(list.get(i).key,list.get(i).value);
        }
    }

    public void krsort() throws ClassCastException{ //Sort keys in reverse order.
        ArrayList<String> listy = keys(); //Listy is the keys in the table
        Collections.sort(listy); //Sort keys
        ArrayList<Pair<V>> list = new ArrayList<Pair<V>>(); //Pair array
        for (Node<V> n = head; n != null; n = n.next) { //Add every Pair
            if(!(n.value instanceof Comparable)) throw new ClassCastException();    //I tested this and it says StringBuilder is of type comparable so I don't konw why it would throw an exception like the one shown on the output
            list.add(new Pair<V>(n.key, n.value));
        }
        empty(); //clear current table
        int j = 0; //Tracks which key I am on in my sorted key list
        for(int i =0; i < list.size(); i++){ //Iterates thru my key-pairs list
            if(list.get(i).key.equals(listy.get(j))){ //If the key I am iterating through in keypairs is equal to the current sorted key 
                put(listy.get(j),list.get(i).value); //Add key and the value associated with the that original key
                list.remove(i); //Remove that keypair from my original list (This will help with runtime since I won't recheck key-pairs already in my new table
                i=0; //Reset keypair iterator
                j++; //Increment up 1 in my listy list.
            }
        }
       
    }

    public PHPArray<String> array_flip() throws ClassCastException{ //Makes the values the keys and vice versa
        reset();
        PHPArray<String> arry = new PHPArray<String>(M);
        for(Node<V> n = head; n!=null; n = n.next){
            arry.put((String)n.value,n.key);
        }
        return arry;
    }
    public void empty(){ //Empty out the table as easily as possible. Useful for when i wanna sort, asort, or whatevz
        entries =  (Node<V>[])new Node[M];
        N = 0;
        head = tail = null;
    }
    // insert the key-value pair into the symbol table
    public void put(int i, V j){ //Put ints into table
        put(Integer.toString(i),j);
    }

    public void put(String key, V val) { //Put key and val into it
        if (val == null) unset(key); //If my value is null then remove key if it exists in table

        // double table size if 50% full2
        if (N >= M/2) resize(2*M); 

        // linear probing
        int i;
        for (i = hash(key); entries[i] != null; i = (i + 1) % M) {
            // update the value if key already exists
            if (entries[i].key.equals(key)) {
                entries[i].value = val; return;
            }
        }
        // found an empty entry
        entries[i] = new Node<V>(key, val);
        //insert the node into the linked list
        if (head == null && tail == null) //When table empty
        {
            head = entries[i];
            tail = entries[i];
            reset(); //Point iterator to head
        }
        else //When table not empty
        {
            reset(); //I have no idea why but this reset is critical. Marcus and I were very confused.
            tail.next = entries[i];
            entries[i].prev = tail;
            tail = entries[i];
        }
        N++;
    }

    // return the value of the key if of type int
    public V get(int i){ 
        return get(Integer.toString(i));
    }
    public V get(String key) { //Returns value of key or null if not there
        for (int i = hash(key); entries[i] != null; i = (i + 1) % M)
            if (entries[i].key.equals(key))
                return entries[i].value;
        return null;
    }

    // resize the hash table to the given capacity by re-hashing all of the keys
    private void resize(int capacity) {
        PHPArray<V> temp = new PHPArray<V>(capacity);
        System.out.println("\t\tSize: "+N+" -- resizing array from "+capacity/2+" to "+capacity);
        //rehash the entries in the order of insertion
        Node<V> current = head;
        while(current != null)
        {
            temp.put(current.key, current.value);
            current = current.next;
        }
        entries = temp.entries;
        head    = temp.head;
        tail    = temp.tail;
        M       = temp.M;
    }

    // rehash a node while keeping it in place in the linked list
    private void rehash(Node<V> node)
    {
        String holder = node.key;
        System.out.println("\t\tKey "+holder+" rehashed...\n");
        int i;
        for (i = hash(holder); entries[i] != null; i = (i + 1) % M)
        {
          //we wait and wait and wait
        }
        entries[i] = node;
    }

    // delete the key (and associated value) from the symbol table
    public void unset(String key) {
        if (get(key) == null) return;

        // find position i of key
        int i = hash(key);
        while (!key.equals(entries[i].key)) {
            i = (i + 1) % M;
        }

        Node<V> toDelete = entries[i];
        entries[i] = null;
        if ((toDelete == head) && (head == tail))
        {
            head = null;
            tail = null;
        }
        else if (toDelete == head)
        {
            head = head.next;
            head.prev = null;
        }
        else if(toDelete == tail)
        {
            tail = tail.prev;
            tail.next = null;
        }
        else
        {
            toDelete.prev.next = toDelete.next;
            toDelete.next.prev = toDelete.prev;
        }

        // rehash all keys in same cluster
        i = (i + 1) % M;
        while (entries[i] != null) {
            // delete and reinsert
            Node<V> nodeToRehash = entries[i];
            entries[i] = null;
            rehash(nodeToRehash);
            i = (i + 1) % M;
        }

        N--;

        // halves size of array if it's 12.5% full or less
        if (N > 0 && N <= M/8) resize(M/2);
    }
    public void unset(int i){ //If key being removed is an int
        unset(Integer.toString(i));
    }

    // hash function for keys - returns value between 0 and M-1
    private int hash(String key) {
        return (key.hashCode() & 0x7fffffff) % M;
    }

    //An inner class to store nodes of a doubly-linked list
    //Each node contains a (key, value) pair
    private class Node<V> {
        private String key;
        private V value;
        private Node<V> next;
        private Node<V> prev;

        Node(String key, V value){
            this(key, value, null, null);
        }

        Node(String key, V value, Node<V> next, Node<V> prev){
            this.key = key;
            this.value = value;
            this.next = next;
            this.prev = prev;
        }
    }


    public class MyIterator<V> implements Iterator<V> //Iterator class that will be used in program to go thru pairs
    {
        private Node<V> headz;

        public MyIterator(Node<V> head) //Iterator starts at the head of the list (as shown in reset and iterator class)
        {
            this.headz = head;
        }

        public V next() { //Get next pair value
            return nextPair().value;
        }

        public Pair<V> nextPair(){ //Next pairz
            //System.out.println(headz.key + " next is: " + headz.next);
            if (headz==null){ //When i am null reinitialize the iterator
                reset();
                return null;
            }
            Pair<V> pair = new Pair<V>(headz.key,headz.value); //The pair
            headz = headz.next;
            return pair;
        }

        @Override
        public boolean hasNext() { //check if iterator has another pair to go through
            return headz!=null;
        }
    }

    private static <V> void show(PHPArray<V> array){ //Show the linked list
        // print values in order of insertion
        System.out.println("Values in insertion order:");
        System.out.print("\t");

        for (V i : array) {
            System.out.print(i + " ");
        }
        System.out.println();

        for (int i = 0; i < 10; i++) {
            System.out.println("A[\"Key" + i + "\"] = " + array.get("Key" + i));
        }
    }

    public static class Pair<V> implements Comparable<Pair<V>> //Pair class
    {
        public String key;
        public V value;
        public Pair(String k,V v)
        {
            this.key = k;
            this.value = v;
        }
        @Override
        public int compareTo(Pair<V> p) throws ClassCastException
        {
            return ((Comparable) this.value).compareTo((Comparable) p.value); //For comparisons...
        }
    }
    }
