package cs321.btree;
import cs321.create.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * BTree class implementing the BTreeInterface.
 */
public class BTree implements BTreeInterface {

    private long size;
    private int degree;
    private int numNodes;
    private int height;
    private BTreeNode root;
    private Cache<BTreeNode> cache;
    private int useCache;
    @SuppressWarnings("unused")
    private int cacheSize;//used if cache is implemented
    private int cacheHit;
    private int cacheRef;
    private int METADATA_SIZE = Long.BYTES;
    private long nextDiskAddress = METADATA_SIZE;
    private FileChannel file;
    private ByteBuffer buffer;
    private int byteSize;
    private File fileName;
    private int subsequenceLength;
    private long rootAddress = METADATA_SIZE; // Offset to the root node

    /**
     * Constructor for the BTree class.
     *
     * @param degree   The degree of the BTree.
     * @param fileName The name of the file to store the BTree.
     * @throws IOException If an I/O error occurs.
     */
    public BTree(int degree, String fileName) throws IOException {
        if (degree == 0) {
            this.degree = 85; //Initializes degree to default if not given
        } else {
            this.degree = degree;
        }
        //Initializes variables
        this.useCache = 0;
        this.cacheSize = 0;
        this.cacheHit = 0;
        this.size = 0;
        this.numNodes = 1;
        this.height = 0;
        this.root = null;
        this.fileName = new File(fileName); // Create a File object with the given fileName
        byteSize = 4096; // Set block size to default, 4096 bytes
        buffer = ByteBuffer.allocateDirect(byteSize); // Allocate a direct ByteBuffer with byteSize

        RandomAccessFile dataFile = null; // Declare dataFile and initialize it to null
        try {
            if (!this.fileName.exists()) {
                // If the file doesn't exist
                this.fileName.createNewFile(); // Create a new file
                dataFile = new RandomAccessFile(fileName, "rw"); // Open the file in read-write mode
                file = dataFile.getChannel(); // Get the file channel
                writeMetaData();
                this.root = new BTreeNode(this.degree, true, true); // Create a new root node
                this.root.address = rootAddress; // Set the address of the root node
                if (useCache == 1) {
                    cacheRef++;
                    cache.addObject(root); // Add the root node to the cache if useCache is 1
                }
            } else {
                // If the file exists
                dataFile = new RandomAccessFile(fileName, "rw"); // Open the file in read-write mode
                file = dataFile.getChannel(); // Get the file channel
                readMetaData();
                root = diskRead(rootAddress); // Read the root node from disk
                if (useCache == 1) {
                    cacheRef++;
                    cache.addObject(root); // Add the root node to the cache if useCache is 1
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(e); // Print the exception if the file is not found
        }
    }

    public BTree(String fileName) throws IOException{
        this(85, fileName);
    }

    public void setSubsequenceLength(int subsequenceLength){
        this.subsequenceLength = subsequenceLength;
    }

    /**
     * Sets the cache usage and cache size for the BTree.
     *
     * @param useCache  Indicates whether to use caching (true) or not (false).
     * @param cacheSize The size of the cache, if caching is enabled.
     */
    public void setUseCacheAndCacheSize(boolean useCache, int cacheSize) {
        if (useCache) {
            this.useCache = 1;
            this.cache = new Cache<BTreeNode>(cacheSize);
            this.cacheSize = cacheSize;
        } else {
            this.useCache = 0;
        }
    }

    /**
     * Returns the size of the BTree.
     *
     * @return The size of the BTree.
     */
    public long getSize() {
        return this.size;
    }

    /**
     * Returns the degree of the BTree.
     *
     * @return The degree of the BTree.
     */
    public int getDegree() {
        return this.degree;
    }

    /**
     * Returns the number of nodes in the BTree.
     *
     * @return The number of nodes in the BTree.
     */
    public int getNumberOfNodes() {
        return this.numNodes;
    }

    /**
     * Returns the height of the BTree.
     *
     * @return The height of the BTree.
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Deletes a key from the BTree.
     *
     * @param key The key to be deleted.
     */
    public void delete(long key) {    // Method not implemented
    }

    /**
     * Returns the cache hit ratio of the BTree.
     *
     * @return The cache hit ratio, calculated as cacheHit / cacheRef.
     */
    public double getCacheHitRatio() {
        return (double) cacheHit / cacheRef;
    }

    /**
     * Searches for a key in the B-tree and returns the corresponding TreeObject.
     *
     * @param key The key to search for.
     * @return The TreeObject associated with the key, or null if the key is not found.
     * @throws IOException If an I/O error occurs during the search.
     */
    public TreeObject search(long key) throws IOException {
            return search(this.root, key);
    }
    /**
     * Recursively searches for a key in the B-tree starting from the given node.
     *
     * @param node The current node being searched.
     * @param key The key to search for.
     * @return The TreeObject associated with the key, or null if the key is not found.
     * @throws IOException If an I/O error occurs during the search.
     */
    private TreeObject search(BTreeNode node, long key) throws IOException {
        int i = 0;

        // Find the index of the first key greater than or equal to the search key
        while (i < node.size && new TreeObject(key).compareTo(node.keys[i]) > 0) {
            i++;
        }

        // Check if the key is found at the current index
        if (i < node.size && new TreeObject(key).compareTo(node.keys[i]) == 0) {
            return node.keys[i];
        } else if (node.isLeaf) {
            // If the current node is a leaf and the key is not found, return null
            return null;
        } else {
            // If the key is not found and the current node is not a leaf,
            // read the child node from disk
            BTreeNode childNode = diskRead(node.children[i]);

            // Recursively search for the key in the child node
            TreeObject result = search(childNode, key);

            return result;
        }
    }

    /**
    * Searches for a node containing the given key in the B-Tree.
    *
    * @param node The root node of the subtree to search in.
    * @param key  The key to search for.
    * @return The node containing the key, or null if the key is not found.
    * @throws IOException If an error occurs while reading from disk.
    */
    protected BTreeNode searchNode(BTreeNode node, long key) throws IOException {
        int i = 0;
        
        // Traverse the keys in the node
        while (i < node.size && new TreeObject(key).compareTo(node.keys[i]) > 0) {
            i++;
        }
        
        // If the key is found in the current node, return the node
        if (i < node.size && new TreeObject(key).compareTo(node.keys[i]) == 0) {
            return node;
        }
        // If the node is a leaf and the key is not found, return null
        else if (node.isLeaf) {
            return null;
        }
        // If the node is not a leaf, search recursively in the child node
        else {
            BTreeNode childNode = diskRead(node.children[i]);
            
            // Recursively search for the key in the child node
            BTreeNode result = searchNode(childNode, key);

            return result;
        }
    }

    /**
    * Inserts a TreeObject into the B-Tree.
    *
    * @param obj The TreeObject to be inserted.
    * @throws IOException If an error occurs while reading from or writing to disk.
    */
    public void insert(TreeObject obj) throws IOException {
        BTreeNode nodeR = this.root;
    
        // Search for a node containing the key
        BTreeNode frequencyNode = searchNode(nodeR, obj.getValue());
    
        // If a node with the key already exists, increment its frequency and write to disk
        if (frequencyNode != null) {
            frequencyNode.getKey(obj).incrementFrequency();
            diskWrite(frequencyNode);
        }
        // If the key is not found
        else {
            // If the root node is full, split the root
            if (nodeR.size == 2 * this.degree - 1) {
                BTreeNode nodeS = splitRoot(this);
                insertNonFull(nodeS, obj);
                this.size++; // Increment size when splitting the root
            }
            // If the root node is not full, insert the key into the root
            else {
                insertNonFull(nodeR, obj);
                this.size++; // Increment size when inserting into a non-full node
            }
        }
    }

    // Split the root of the B-tree
    public BTreeNode splitRoot(BTree tree) throws IOException {
        BTreeNode nodeS = new BTreeNode(tree.degree, false, true);
        nodeS.size = 0;
        nodeS.children[0] = tree.root.address;
        tree.root = nodeS;
        tree.rootAddress = tree.root.address;
        splitChild(nodeS, 0);
        this.numNodes++;
        tree.height++;
        return nodeS;
    }

    /**
    * Splits a child node of a given node in the B-Tree.
    *
    * @param nodeX The node whose child node needs to be split.
    * @param index The index of the child node to be split.
    * @throws IOException If an error occurs while reading from or writing to disk.
    */
    public void splitChild(BTreeNode nodeX, int index) throws IOException {
        // Read the child node to be split from disk
        BTreeNode nodeY = diskRead(nodeX.children[index]);
    
        // Create a new node nodeZ to hold the keys and children after the split
        BTreeNode nodeZ = new BTreeNode(degree, nodeY.isLeaf, true);
        nodeZ.size = degree - 1;
    
        // Copy the keys from nodeY to nodeZ
        for (int i = 0; i < degree - 1; i++) {
            nodeZ.keys[i] = nodeY.keys[i + degree];
        }
    
        // If nodeY is not a leaf, copy the child pointers to nodeZ
        if (!nodeY.isLeaf) {
            for (int i = 0; i < degree; i++) {
                nodeZ.children[i] = nodeY.children[i + degree];
            }
        }
    
        // Adjust the size of nodeY after splitting
        nodeY.size = degree - 1;
    
        // Make space for the new child pointer in nodeX
        for (int i = nodeX.size; i > index; i--) {
            nodeX.children[i + 1] = nodeX.children[i];
        }
    
        // Add the pointer to nodeZ in nodeX
        nodeX.children[index + 1] = nodeZ.address;
    
        // Move the keys in nodeX to make space for the new key
        for (int i = nodeX.size - 1; i >= index; i--) {
            nodeX.keys[i + 1] = nodeX.keys[i];
        }
    
        // Copy the middle key from nodeY to nodeX
        nodeX.keys[index] = nodeY.keys[degree - 1];
    
        // Increment the size of nodeX and the number of nodes
        nodeX.size = nodeX.size + 1;
        this.numNodes++;
    
        // Write the modified nodes back to disk
        diskWrite(nodeY);
        diskWrite(nodeZ);
        diskWrite(nodeX);
    
        // Write the updated metadata to disk
        writeMetaData();
    }

    /**
    * Inserts a TreeObject into a non-full node in the B-Tree.
    *
    * @param node The non-full node where the TreeObject should be inserted.
    * @param key The TreeObject to be inserted.
    * @throws IOException If an error occurs while reading from or writing to disk.
    */
    public void insertNonFull(BTreeNode node, TreeObject key) throws IOException {
        int i = node.size - 1;
    
        // If the node is a leaf node
        if (node.isLeaf) {
            // Find the position where the key should be inserted
            while (i >= 0 && key.compareTo(node.keys[i]) == -1) {
                node.keys[i + 1] = node.keys[i];
                i = i - 1;
            }
    
            // Insert the key at the correct position
            node.keys[i + 1] = key;
            node.size = node.size + 1;
    
            // Write the modified node back to disk
            diskWrite(node);
        }
        // If the node is an internal node
        else {
            // Find the child node where the key should be inserted
            while (i >= 0 && key.compareTo(node.keys[i]) == -1) {
                i = i - 1;
            }
            i = i + 1;
    
            // Read the child node from disk
            BTreeNode child = diskRead(node.children[i]);
    
            // If the child node is full, split it
            if (child.size == 2 * degree - 1) {
                splitChild(node, i);
    
                // After splitting, determine the correct child node for insertion
                if (key.compareTo(node.keys[i]) == 1) {
                    i = i + 1;
                    child = diskRead(node.children[i]);
                }
            }
    
            // Read the child node again to get the updated reference
            child = diskRead(child.address);
    
            // Recursively insert the key into the child node
            insertNonFull(child, key);
        }
    }

    /**
    * Dumps the contents of the B-Tree to a file in an in-order traversal.
    *
    * @param out The PrintWriter object used to write to the file.
    * @throws IOException If an error occurs while reading from disk or writing to the file.
    */
    public void dumpToFile(PrintWriter out) throws IOException {
        // Perform in-order traversal of the B-Tree and write the contents to the file
        inOrderTraversal(root, out);
    
        // Ensure all data is flushed and written to the file
        out.flush();
    }
    
    /**
     * Performs an in-order traversal of the B-Tree and writes the contents to the provided PrintWriter.
     *
     * @param node The root node of the subtree to be traversed.
     * @param out The PrintWriter object used to write the contents to.
     * @throws IOException If an error occurs while reading from disk or writing to the file.
     */
    private void inOrderTraversal(BTreeNode node, PrintWriter out) throws IOException {
        if (node != null) {
            // Recursively traverse the left subtree
            for (int i = 0; i < node.size; i++) {
                if (!node.isLeaf) {
                    // Read the child node from disk and recursively traverse it
                    inOrderTraversal(diskRead(node.children[i]), out);
                }
    
                // Write the key and its frequency to the file
                out.println(SequenceUtils.longToDnaString(node.keys[i].getValue(), this.subsequenceLength) + " " + node.keys[i].getFrequency());
            }
    
            // Traverse the right subtree (if it exists)
            if (!node.isLeaf) {
                // Read the child node from disk and recursively traverse it
                inOrderTraversal(diskRead(node.children[node.size]), out);
            }
        }
    }

    /**
    * Returns a sorted array of all the keys in the B-Tree.
    *
    * @return An array of long values representing the keys in sorted order.
    * @throws IOException If an error occurs while reading from disk.
    */
    public long[] getSortedKeyArray() throws IOException {
        List<Long> keyList = new ArrayList<>();
    
        // Traverse the B-Tree and collect all keys in a list
        getSortedKeyArrayHelper(root, keyList);
    
        // Convert the list to an array
        long[] keyArray = new long[keyList.size()];
        for (int i = 0; i < keyList.size(); i++) {
            keyArray[i] = keyList.get(i);
        }
    
        return keyArray;
    }
    
    /**
     * Helper method to traverse the B-Tree and collect all keys in a list.
     *
     * @param node    The root node of the subtree to be traversed.
     * @param keyList The list to which the keys will be added.
     * @throws IOException If an error occurs while reading from disk.
     */
    private void getSortedKeyArrayHelper(BTreeNode node, List<Long> keyList) throws IOException {
        if (node != null) {
            // Recursively traverse the left subtree
            int i = 0;
            for (i = 0; i < node.size; i++) {
                if (!node.isLeaf) {
                    // Read the child node from disk and recursively traverse it
                    getSortedKeyArrayHelper(diskRead(node.children[i]), keyList);
                }
    
                // Add the key to the list
                keyList.add(node.keys[i].getValue());
            }
    
            // Traverse the right subtree (if it exists)
            if (!node.isLeaf) {
                // Read the child node from disk and recursively traverse it
                getSortedKeyArrayHelper(diskRead(node.children[i]), keyList);
            }
        }
    }

    /**
    * Reads the metadata from the data file.
    *
    * @throws IOException If an error occurs while reading from the file.
    */
    public void readMetaData() throws IOException {
        // Position the file pointer to the beginning of the file
        file.position(0);
    
        // Allocate a direct byte buffer for reading the metadata
        ByteBuffer tmpbuffer = ByteBuffer.allocateDirect(METADATA_SIZE);
        tmpbuffer.clear();
    
        // Read the metadata from the file into the byte buffer
        file.read(tmpbuffer);
        tmpbuffer.flip();
    
        // Get the rootAddress from the byte buffer
        rootAddress = tmpbuffer.getLong();
    }
    
    /**
     * Writes the metadata to the data file.
     *
     * @throws IOException If an error occurs while writing to the file.
     */
    public void writeMetaData() throws IOException {
        // Position the file pointer to the beginning of the file
        file.position(0);
    
        // Allocate a direct byte buffer for writing the metadata
        ByteBuffer tmpbuffer = ByteBuffer.allocateDirect(METADATA_SIZE);
        tmpbuffer.clear();
    
        // Put the rootAddress into the byte buffer
        tmpbuffer.putLong(rootAddress);
        tmpbuffer.flip();
    
        // Write the byte buffer to the file
        file.write(tmpbuffer);
    }

    /**
    * Reads a BTreeNode from disk at the specified disk address.
    *
    * @param diskAddress The disk address of the BTreeNode to read.
    * @return The BTreeNode read from disk, or null if the disk address is 0.
    * @throws IOException If an error occurs while reading from the file.
    */
    public BTreeNode diskRead(long diskAddress) throws IOException {
        if (diskAddress == 0) return null;

        if(useCache == 1){
            cacheRef++;
            BTreeNode cachedNode = cache.getObject(diskAddress);
            if(cachedNode != null){
                cacheHit++;
                return cachedNode;
            }
        }
    
        // Position the file pointer to the disk address
        file.position(diskAddress);
        buffer.clear();
        file.read(buffer);
        buffer.flip();
    
        // Read the size, degree, and leaf flag from the buffer
        int size = buffer.getInt();
        int degree = buffer.getInt();
        byte flag = buffer.get(); // Read a byte
        boolean isLeaf = false;
        if (flag == 1)
            isLeaf = true;
    
        // Read the keys from the buffer
        TreeObject[] keys = new TreeObject[2 * degree - 1];
        for (int i = 0; i < size; i++) {
            keys[i] = new TreeObject(buffer.getLong(), buffer.getLong());
        }
    
        // Read the children addresses from the buffer
        long[] children = new long[2 * degree];
        for (int i = 0; i < (2 * degree); i++) {
            children[i] = buffer.getLong();
        }
    
        // Create a new BTreeNode with the read data
        BTreeNode x = new BTreeNode(degree, isLeaf, false);
        if(useCache == 1){
            cacheRef++;
            cache.addObject(x);
        }
        x.size = size;
        x.children = children;
        x.keys = keys;
        x.address = diskAddress;
    
        return x;
    }
    
    /**
     * Writes a BTreeNode to disk at its specified disk address.
     *
     * @param x The BTreeNode to be written to disk.
     * @throws IOException If an error occurs while writing to the file.
     */
    public void diskWrite(BTreeNode x) throws IOException {

        if(useCache == 1){
            cacheRef++;
            cache.addObject(x);
        }

        // Position the file pointer to the node's disk address
        file.position(x.address);
        buffer.clear();
    
        // Write the size, degree, and leaf flag to the buffer
        buffer.putInt(x.size);
        buffer.putInt(x.degree);
        if (x.isLeaf) {
            buffer.put((byte) 1);
        } else {
            buffer.put((byte) 0);
        }
    
        // Write the keys to the buffer
        for (int i = 0; i < x.size; i++) {
            if (x.keys[i] != null) {
                buffer.putLong(x.keys[i].getValue());
                buffer.putLong(x.keys[i].getFrequency());
            }
        }
    
        // Write the child addresses to the buffer
        for (int i = 0; i < (x.degree * 2); i++) {
            buffer.putLong(x.children[i]);
        }
    
        buffer.flip();
        file.write(buffer);
        // file.force(true);
    }
    
    /**
     * Performs cleanup operations at the end, such as writing the root node and metadata to disk, and closing the file.
     *
     * @throws IOException If an error occurs while writing to the file or closing the file.
     */
    // public void finishUp() throws IOException {
    //     if (useCache == 1) {
    //         for (int i = 0; i < cache.getSize(); i++) {
    //             BTreeNode node = cache.getIndex(i);
    //             diskWrite(node);
    //         }
    //     }    
    //     diskWrite(root);
    //     writeMetaData();
    //     file.close();
    // }

/**
* Represents a node in the B-Tree data structure.
*/
public class BTreeNode {

    // public for cache implementation
    public TreeObject[] keys;
    public long address;
 
    private long[] children;
    private boolean isLeaf;
    private int degree;
    private int size;
 
    /**
     * Constructor for creating a BTreeNode.
     *
     * @param degree The degree of the B-Tree.
     * @param leaf   Flag indicating if the node is a leaf node.
     * @param onDisk Flag indicating if the node should be allocated on disk.
     */
    public BTreeNode(int degree, boolean leaf, boolean onDisk) {
        this.degree = degree;
        this.isLeaf = leaf;
        this.keys = new TreeObject[2 * degree - 1];
        this.children = new long[2 * degree];
        this.size = 0;
 
        // If the node should be allocated on disk
        if (onDisk) {
            // Calculate and assign the disk address for the node
            address = nextDiskAddress;
            int byteSize = Integer.BYTES + 1 + Integer.BYTES; // Size of degree, isLeaf flag, and size
            byteSize += (2 * degree - 1) * (Long.BYTES * 2); // Size of keys array
            byteSize += (2 * degree) * Long.BYTES; // Size of children array
            nextDiskAddress += byteSize;
        }
    }
 
    /**
     * Constructor for creating a BTreeNode (for cache implementation).
     *
     * @param degree The degree of the B-Tree.
     */
    public BTreeNode(int degree) {
        // Use a default degree of 85 if degree is 0
        if (degree == 0) {
            this.degree = 85;
        } else {
            this.degree = degree;
        }
 
        this.keys = new TreeObject[2 * degree - 1];
        this.children = new long[2 * degree];
        this.size = 0;
    }
 
        /**
         * Retrieves the TreeObject associated with the given TreeObject's value.
         *
         * @param obj The TreeObject whose value should be matched.
         * @return The TreeObject associated with the given value, or null if not found.
         */
        public TreeObject getKey(TreeObject obj) {
            TreeObject object = null;
            for (int i = 0; i < size; i++) {
                if (keys[i].getValue() == obj.getValue()) {
                    object = keys[i];
                    break;
                }
            }
            return object;
        }
    
        /**
         * Retrieves the size of the node (number of keys).
         *
         * @return The size of the node.
         */
        public int getSize() {
            return this.size;
        }
    
        /**
         * Increments the size of the node.
         */
        public void incrementSize() {
            size++;
        }
    
        /**
         * Calculates the size of the node as stored on disk (in bytes).
         *
         * @return The size of the node in bytes.
         */
        public int getDiskSize() {
            int size = Integer.BYTES; // degree
            size += 1; // isLeaf (boolean)
            size += Integer.BYTES; // size
    
            // Size of the keys
            for (int i = 0; i < this.size; i++) {
                size += Long.BYTES * 2; // Construct TreeObject
            }
    
            // Size of the child nodes
            size += Long.BYTES * (this.size + 1);
    
            return size;
        }
    }
}
