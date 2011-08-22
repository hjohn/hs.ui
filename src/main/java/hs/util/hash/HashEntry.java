package hs.util.hash;

public interface HashEntry<K> {
  public HashEntry<K> getNextEntry();
  public void setNextEntry(HashEntry<K> entry);
  public K getKey();
}
