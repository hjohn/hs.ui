package hs.util.rbtree;

/**
 * Represents a node in a red black tree.
 * 
 * @author John Hendrikx
 * @param <E> the element type of the value stored in the node
 * @param <N> the node type
 */
public abstract class AbstractRedBlackNode<E, N> {
  private E value;
  private N parent;
  private N left;
  private N right;
  private boolean isBlack;
  
  public AbstractRedBlackNode(E value, N parent) {
    this.value = value;
    this.parent = parent;
  }
  
  @Override
  public String toString() {
    return "Node(c=" + isBlack() + ",v=" + getValue() + ")";
  }

  public void setParent(N parent) {
    this.parent = parent;
  }

  public N getParent() {
    return parent;
  }

  public void setLeft(N left) {
    this.left = left;
  }

  public N getLeft() {
    return left;
  }

  public void setRight(N right) {
    this.right = right;
  }

  public N getRight() {
    return right;
  }

  public void setBlack(boolean isBlack) {
    this.isBlack = isBlack;
  }
  
  public void makeRed() {
    this.setBlack(false);
  }
  
  public void makeBlack() {
    this.setBlack(true);
  }

  public boolean isBlack() {
    return isBlack;
  }

  public boolean isRed() {
    return !isBlack;
  }
  
  public void setValue(E value) {
    this.value = value;
  }

  public E getValue() {
    return value;
  }
}

// Performance of       |    add(rnd)     add(end)  remove(rnd)  remove(end)          get      indexOf     contains      iterate
//            HTreeList |      2459 ns      1248 ns      2127 ns       881 ns       790 ns      1266 ns       476 ns        61 ns
//            HTreeList |      2297 ns      1186 ns      2140 ns       816 ns       789 ns      1252 ns       476 ns        61 ns

// Making parent + left fields use accessors
//            HTreeList |      2308 ns      1193 ns      2194 ns       814 ns       804 ns      1266 ns       476 ns        60 ns
//            HTreeList |      2805 ns      1413 ns      2122 ns      1036 ns       804 ns      1266 ns       476 ns        60 ns
//            HTreeList |      2500 ns      1259 ns      2177 ns       848 ns       804 ns      1266 ns       476 ns        60 ns
//            HTreeList |      2491 ns      1258 ns      2133 ns       875 ns       790 ns      1266 ns       462 ns        60 ns

// Adding final keyword to Node / IndexedNode
//            HTreeList |      2302 ns      1194 ns      2141 ns       814 ns       789 ns      1267 ns       461 ns        61 ns
//            HTreeList |      2543 ns      1194 ns      2203 ns       823 ns       803 ns      1266 ns       476 ns        60 ns
//            HTreeList |      2312 ns      1191 ns      2138 ns       824 ns       804 ns      1266 ns       476 ns        60 ns
//            HTreeList |      2352 ns      1198 ns      2183 ns       819 ns       789 ns      1251 ns       491 ns        60 ns

//            HTreeList |      2371 ns      1195 ns      2120 ns       818 ns       790 ns      1280 ns       476 ns        61 ns
//            HTreeList |      2456 ns      1196 ns      2131 ns       841 ns       789 ns      1267 ns       461 ns        61 ns
//            HTreeList |      2370 ns      1188 ns      2116 ns       821 ns       789 ns      1251 ns       461 ns        61 ns
//            HTreeList |      2310 ns      1194 ns      2121 ns       818 ns       789 ns      1266 ns       476 ns        61 ns

// Preparations for HashList (Node implements HashEntry)
//            HTreeList |      3498 ns      1257 ns      2079 ns       834 ns       849 ns      1325 ns       461 ns        72 ns
//            HTreeList |      3642 ns      1357 ns      2052 ns       891 ns       849 ns      1325 ns       461 ns        72 ns
//            HTreeList |      3488 ns      1251 ns      2053 ns       836 ns       864 ns      1311 ns       476 ns        72 ns
//            HTreeList |      3796 ns      1267 ns      2050 ns       838 ns       848 ns      1325 ns       461 ns        72 ns

// With new HashList code
//            HTreeList |      2144 ns      1270 ns      1874 ns       661 ns       803 ns      1206 ns       566 ns        69 ns
//            HTreeList |      2153 ns      1275 ns      1886 ns       659 ns       819 ns      1221 ns       551 ns        69 ns
//            HTreeList |      2331 ns      1279 ns      1900 ns       747 ns       834 ns      1206 ns       551 ns        69 ns
//            HTreeList |      2275 ns      1273 ns      1869 ns       653 ns       803 ns      1207 ns       551 ns        69 ns

// Moved value field up...
//            HTreeList |      2081 ns      1238 ns      1859 ns       631 ns       789 ns      1192 ns       535 ns        66 ns
//            HTreeList |      2144 ns      1320 ns      1880 ns       674 ns       804 ns      1192 ns       535 ns        68 ns
//            HTreeList |      2086 ns      1248 ns      1814 ns       635 ns       760 ns      1162 ns       521 ns        66 ns
//            HTreeList |      2102 ns      1315 ns      1842 ns       672 ns       789 ns      1192 ns       521 ns        68 ns