package hs.util.hash;

public class HashList<K> {
  private int bucketMask;
  private int size;
  private int bucketPower;
  private float loadFactor = 0.75f;
  private int max;
  
  private HashEntry<K>[] buckets;
  
  public HashList() {
    bucketPower = 3;
    allocateBuckets();
  }
  
  @SuppressWarnings("unchecked")
  private void allocateBuckets() {
    bucketMask = (1 << bucketPower) - 1;
    HashEntry<K>[] oldBuckets = buckets;
    buckets = new HashEntry[1 << bucketPower];
    //System.err.println("allocateBuckets()");
    if(oldBuckets != null) {
      // Redistribute over buckets
      for(HashEntry<K> entry : oldBuckets) {
        while(entry != null) {
          HashEntry<K> next = entry.getNextEntry();
          addInternal(entry);
          entry = next;
        } 
      }
    }
    max = (int)(loadFactor * buckets.length);
   // System.err.println("new max = " + max);
  }
  
  private int getBucket(HashEntry<K> entry) {
    return entry.getKey().hashCode() & bucketMask;
  }
  
  private void addInternal(HashEntry<K> entry) {
    int bucket = getBucket(entry);

    entry.setNextEntry(buckets[bucket]);
    buckets[bucket] = entry;
  }
  
  public void add(HashEntry<K> entry) {
    if(size > max) {
      bucketPower++;
      allocateBuckets();
    }
    addInternal(entry);
    size++;
  }

  public void clear() {
    buckets = null;
    bucketPower = 3;
    allocateBuckets();
    size = 0;
  }
  
  public boolean contains(Object key) {
    return getEntry(key) != null;
  }
  
  public HashEntry<K> getEntry(Object key) {
    int bucket = key.hashCode() & bucketMask;
    HashEntry<K> current = buckets[bucket];
    
    while(current != null && !current.getKey().equals(key)) {
      current = current.getNextEntry();
    }
    
    return current;
  }
  
  public void remove(HashEntry<K> entry) {
    int bucket = getBucket(entry);
    HashEntry<K> previous = null;
    HashEntry<K> current = buckets[bucket];
    
    while(current != null && current != entry) {
      previous = current;
      current = current.getNextEntry();
    }
    
    if(current == null) {
      throw new IllegalArgumentException("not part of this hashlist: " + entry);
    }
    
    if(previous == null) {
      buckets[bucket] = entry.getNextEntry();
    }
    else {
      previous.setNextEntry(entry.getNextEntry());
    }
    
    entry.setNextEntry(null);
    size--;
  }
}
