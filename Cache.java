package cs321.btree;

import java.util.LinkedHashMap;
import java.util.Map;

import cs321.btree.BTree.BTreeNode;

/**
 * The Cache class represents a cache storage mechanism for BTreeNode objects.
 * It uses a LinkedHashMap to store the cached objects, with the disk address as the key.
 * The cache has a maximum capacity, and when the capacity is reached, the least recently used (LRU)
 * object is automatically removed from the cache.
 *
 * @param <T> the type of objects stored in the cache
 */
public class Cache<T> {
    private Map<Long, T> cacheStorage;
    private int cacheSize;

    /**
     * Constructs a new Cache object with the specified capacity.
     *
     * @param capacity the maximum number of objects the cache can store
     */
    public Cache(int capacity) {
        cacheSize = capacity;
        cacheStorage = new LinkedHashMap<Long, T>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, T> eldest) {
                // Remove the least recently used entry when the cache size exceeds the capacity
                return size() > cacheSize;
            }
        };
    }

    /**
     * Checks if the specified object is present in the cache.
     *
     * @param object the object to check for presence in the cache
     * @return true if the object is found in the cache, false otherwise
     */
    public boolean getObject(T object) {
        if (object instanceof BTreeNode) {
            BTreeNode node = (BTreeNode) object;
            return cacheStorage.containsKey(node.address);
        }
        return false;
    }

    /**
     * Retrieves the BTreeNode object associated with the specified disk address from the cache.
     *
     * @param diskAddress the disk address of the object to retrieve
     * @return the BTreeNode object if found in the cache, null otherwise
     */
    public BTreeNode getObject(long diskAddress) {
        return (BTreeNode) cacheStorage.get(diskAddress);
    }

    /**
     * Adds the specified object to the cache.
     *
     * @param object the object to add to the cache
     */
    public void addObject(T object) {
        if (object instanceof BTreeNode) {
            BTreeNode node = (BTreeNode) object;
            cacheStorage.put(node.address, object);
        }
    }

    /**
     * Clears all objects from the cache.
     */
    public void clearCache() {
        cacheStorage.clear();
    }

    /**
     * Returns the current size of the cache.
     *
     * @return the number of objects currently stored in the cache
     */
    public int getSize() {
        return cacheStorage.size();
    }

    /**
     * Removes the specified object from the cache.
     *
     * @param obj the object to remove from the cache
     * @return true if the object was successfully removed, false otherwise
     */
    public boolean removeObject(T obj) {
        if (obj instanceof BTreeNode) {
            BTreeNode node = (BTreeNode) obj;
            return cacheStorage.remove(node.address) != null;
        }
        return false;
    }

    /**
     * Checks if the specified object is present in the cache.
     *
     * @param obj the object to check for presence in the cache
     * @return true if the object is found in the cache, false otherwise
     */
    public boolean cacheContains(T obj) {
        if (obj instanceof BTreeNode) {
            BTreeNode node = (BTreeNode) obj;
            return cacheStorage.containsKey(node.address);
        }
        return false;
    }

    /**
     * Returns the maximum capacity of the cache.
     *
     * @return the maximum number of objects the cache can store
     */
    public int getCapacity() {
        return this.cacheSize;
    }
}