/*      */ package java.util;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InvalidObjectException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.ParameterizedType;
/*      */ import java.lang.reflect.Type;
/*      */ import java.util.function.BiConsumer;
/*      */ import java.util.function.BiFunction;
/*      */ import java.util.function.Consumer;
/*      */ import java.util.function.Function;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class HashMap<K, V>
/*      */   extends AbstractMap<K, V>
/*      */   implements Map<K, V>, Cloneable, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 362498820763181265L;
/*      */   static final int DEFAULT_INITIAL_CAPACITY = 16;
/*      */   static final int MAXIMUM_CAPACITY = 1073741824;
/*      */   static final float DEFAULT_LOAD_FACTOR = 0.75F;
/*      */   static final int TREEIFY_THRESHOLD = 8;
/*      */   static final int UNTREEIFY_THRESHOLD = 6;
/*      */   static final int MIN_TREEIFY_CAPACITY = 64;
/*      */   transient Node<K, V>[] table;
/*      */   transient Set<Map.Entry<K, V>> entrySet;
/*      */   transient int size;
/*      */   transient int modCount;
/*      */   int threshold;
/*      */   final float loadFactor;
/*      */   
/*      */   static class Node<K, V>
/*      */     implements Map.Entry<K, V>
/*      */   {
/*      */     final int hash;
/*      */     final K key;
/*      */     V value;
/*      */     Node<K, V> next;
/*      */     
/*      */     Node(int param1Int, K param1K, V param1V, Node<K, V> param1Node) {
/*  285 */       this.hash = param1Int;
/*  286 */       this.key = param1K;
/*  287 */       this.value = param1V;
/*  288 */       this.next = param1Node;
/*      */     }
/*      */     
/*  291 */     public final K getKey() { return this.key; }
/*  292 */     public final V getValue() { return this.value; } public final String toString() {
/*  293 */       return (new StringBuilder()).append(this.key).append("=").append(this.value).toString();
/*      */     }
/*      */     public final int hashCode() {
/*  296 */       return Objects.hashCode(this.key) ^ Objects.hashCode(this.value);
/*      */     }
/*      */     
/*      */     public final V setValue(V param1V) {
/*  300 */       V v = this.value;
/*  301 */       this.value = param1V;
/*  302 */       return v;
/*      */     }
/*      */     
/*      */     public final boolean equals(Object param1Object) {
/*  306 */       if (param1Object == this)
/*  307 */         return true; 
/*  308 */       if (param1Object instanceof Map.Entry) {
/*  309 */         Map.Entry entry = (Map.Entry)param1Object;
/*  310 */         if (Objects.equals(this.key, entry.getKey()) && 
/*  311 */           Objects.equals(this.value, entry.getValue()))
/*  312 */           return true; 
/*      */       } 
/*  314 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final int hash(Object paramObject) {
/*      */     int i;
/*  338 */     return (paramObject == null) ? 0 : ((i = paramObject.hashCode()) ^ i >>> 16);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static Class<?> comparableClassFor(Object paramObject) {
/*  346 */     if (paramObject instanceof Comparable) {
/*      */       Class<?> clazz;
/*  348 */       if ((clazz = paramObject.getClass()) == String.class)
/*  349 */         return clazz;  Type[] arrayOfType;
/*  350 */       if ((arrayOfType = clazz.getGenericInterfaces()) != null)
/*  351 */         for (byte b = 0; b < arrayOfType.length; b++) {
/*  352 */           Type[] arrayOfType1; Type type; ParameterizedType parameterizedType; if (type = arrayOfType[b] instanceof ParameterizedType && (parameterizedType = (ParameterizedType)type)
/*  353 */             .getRawType() == Comparable.class && (
/*      */             
/*  355 */             arrayOfType1 = parameterizedType.getActualTypeArguments()) != null && arrayOfType1.length == 1 && arrayOfType1[0] == clazz)
/*      */           {
/*  357 */             return clazz;
/*      */           }
/*      */         }  
/*      */     } 
/*  361 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int compareComparables(Class<?> paramClass, Object paramObject1, Object paramObject2) {
/*  370 */     return (paramObject2 == null || paramObject2.getClass() != paramClass) ? 0 : ((Comparable<Object>)paramObject1)
/*  371 */       .compareTo(paramObject2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final int tableSizeFor(int paramInt) {
/*  378 */     int i = paramInt - 1;
/*  379 */     i |= i >>> 1;
/*  380 */     i |= i >>> 2;
/*  381 */     i |= i >>> 4;
/*  382 */     i |= i >>> 8;
/*  383 */     i |= i >>> 16;
/*  384 */     return (i < 0) ? 1 : ((i >= 1073741824) ? 1073741824 : (i + 1));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HashMap(int paramInt, float paramFloat) {
/*  447 */     if (paramInt < 0) {
/*  448 */       throw new IllegalArgumentException("Illegal initial capacity: " + paramInt);
/*      */     }
/*  450 */     if (paramInt > 1073741824)
/*  451 */       paramInt = 1073741824; 
/*  452 */     if (paramFloat <= 0.0F || Float.isNaN(paramFloat)) {
/*  453 */       throw new IllegalArgumentException("Illegal load factor: " + paramFloat);
/*      */     }
/*  455 */     this.loadFactor = paramFloat;
/*  456 */     this.threshold = tableSizeFor(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HashMap(int paramInt) {
/*  467 */     this(paramInt, 0.75F);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HashMap() {
/*  475 */     this.loadFactor = 0.75F;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HashMap(Map<? extends K, ? extends V> paramMap) {
/*  488 */     this.loadFactor = 0.75F;
/*  489 */     putMapEntries(paramMap, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void putMapEntries(Map<? extends K, ? extends V> paramMap, boolean paramBoolean) {
/*  500 */     int i = paramMap.size();
/*  501 */     if (i > 0) {
/*  502 */       if (this.table == null) {
/*  503 */         float f = i / this.loadFactor + 1.0F;
/*  504 */         int j = (f < 1.07374182E9F) ? (int)f : 1073741824;
/*      */         
/*  506 */         if (j > this.threshold) {
/*  507 */           this.threshold = tableSizeFor(j);
/*      */         }
/*  509 */       } else if (i > this.threshold) {
/*  510 */         resize();
/*  511 */       }  for (Map.Entry<? extends K, ? extends V> entry : paramMap.entrySet()) {
/*  512 */         Object object1 = entry.getKey();
/*  513 */         Object object2 = entry.getValue();
/*  514 */         putVal(hash(object1), (K)object1, (V)object2, false, paramBoolean);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int size() {
/*  525 */     return this.size;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEmpty() {
/*  534 */     return (this.size == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public V get(Object paramObject) {
/*      */     Node<K, V> node;
/*  556 */     return ((node = getNode(hash(paramObject), paramObject)) == null) ? null : node.value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final Node<K, V> getNode(int paramInt, Object paramObject) {
/*      */     Node<K, V>[] arrayOfNode;
/*      */     Node<K, V> node;
/*      */     int i;
/*  568 */     if ((arrayOfNode = this.table) != null && (i = arrayOfNode.length) > 0 && (node = arrayOfNode[i - 1 & paramInt]) != null) {
/*      */       K k;
/*  570 */       if (node.hash == paramInt && ((k = node.key) == paramObject || (paramObject != null && paramObject
/*  571 */         .equals(k))))
/*  572 */         return node;  Node<K, V> node1;
/*  573 */       if ((node1 = node.next) != null) {
/*  574 */         if (node instanceof TreeNode)
/*  575 */           return ((TreeNode<K, V>)node).getTreeNode(paramInt, paramObject); 
/*      */         do {
/*  577 */           if (node1.hash == paramInt && ((k = node1.key) == paramObject || (paramObject != null && paramObject
/*  578 */             .equals(k))))
/*  579 */             return node1; 
/*  580 */         } while ((node1 = node1.next) != null);
/*      */       } 
/*      */     } 
/*  583 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean containsKey(Object paramObject) {
/*  595 */     return (getNode(hash(paramObject), paramObject) != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public V put(K paramK, V paramV) {
/*  611 */     return putVal(hash(paramK), paramK, paramV, false, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final V putVal(int paramInt, K paramK, V paramV, boolean paramBoolean1, boolean paramBoolean2) {
/*      */     Node[] arrayOfNode;
/*      */     Node<K, V>[] arrayOfNode1;
/*      */     int i;
/*  627 */     if ((arrayOfNode1 = this.table) == null || (i = arrayOfNode1.length) == 0)
/*  628 */       i = (arrayOfNode = (Node[])resize()).length;  Node<K, V> node; int j;
/*  629 */     if ((node = arrayOfNode[j = i - 1 & paramInt]) == null) {
/*  630 */       arrayOfNode[j] = newNode(paramInt, paramK, paramV, (Node<K, V>)null);
/*      */     } else {
/*      */       Node<K, V> node1; K k;
/*  633 */       if (node.hash == paramInt && ((k = node.key) == paramK || (paramK != null && paramK
/*  634 */         .equals(k)))) {
/*  635 */         node1 = node;
/*  636 */       } else if (node instanceof TreeNode) {
/*  637 */         node1 = ((TreeNode<K, V>)node).putTreeVal(this, (Node<K, V>[])arrayOfNode, paramInt, paramK, paramV);
/*      */       } else {
/*  639 */         for (byte b = 0;; b++) {
/*  640 */           if ((node1 = node.next) == null) {
/*  641 */             node.next = newNode(paramInt, paramK, paramV, (Node<K, V>)null);
/*  642 */             if (b >= 7)
/*  643 */               treeifyBin((Node<K, V>[])arrayOfNode, paramInt); 
/*      */             break;
/*      */           } 
/*  646 */           if (node1.hash == paramInt && ((k = node1.key) == paramK || (paramK != null && paramK
/*  647 */             .equals(k))))
/*      */             break; 
/*  649 */           node = node1;
/*      */         } 
/*      */       } 
/*  652 */       if (node1 != null) {
/*  653 */         V v = node1.value;
/*  654 */         if (!paramBoolean1 || v == null)
/*  655 */           node1.value = paramV; 
/*  656 */         afterNodeAccess(node1);
/*  657 */         return v;
/*      */       } 
/*      */     } 
/*  660 */     this.modCount++;
/*  661 */     if (++this.size > this.threshold)
/*  662 */       resize(); 
/*  663 */     afterNodeInsertion(paramBoolean2);
/*  664 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final Node<K, V>[] resize() {
/*      */     byte b2;
/*  677 */     Node<K, V>[] arrayOfNode = this.table;
/*  678 */     byte b1 = (arrayOfNode == null) ? 0 : arrayOfNode.length;
/*  679 */     int i = this.threshold;
/*  680 */     int j = 0;
/*  681 */     if (b1) {
/*  682 */       if (b1 >= 1073741824) {
/*  683 */         this.threshold = Integer.MAX_VALUE;
/*  684 */         return arrayOfNode;
/*      */       } 
/*  686 */       if ((b2 = b1 << 1) < 1073741824 && b1 >= 16)
/*      */       {
/*  688 */         j = i << 1;
/*      */       }
/*  690 */     } else if (i > 0) {
/*  691 */       b2 = i;
/*      */     } else {
/*  693 */       b2 = 16;
/*  694 */       j = 12;
/*      */     } 
/*  696 */     if (j == 0) {
/*  697 */       float f = b2 * this.loadFactor;
/*  698 */       j = (b2 < 1073741824 && f < 1.07374182E9F) ? (int)f : Integer.MAX_VALUE;
/*      */     } 
/*      */     
/*  701 */     this.threshold = j;
/*      */     
/*  703 */     Node[] arrayOfNode1 = new Node[b2];
/*  704 */     this.table = (Node<K, V>[])arrayOfNode1;
/*  705 */     if (arrayOfNode != null)
/*  706 */       for (byte b = 0; b < b1; b++) {
/*      */         Node<K, V> node;
/*  708 */         if ((node = arrayOfNode[b]) != null) {
/*  709 */           arrayOfNode[b] = null;
/*  710 */           if (node.next == null) {
/*  711 */             arrayOfNode1[node.hash & b2 - 1] = node;
/*  712 */           } else if (node instanceof TreeNode) {
/*  713 */             ((TreeNode)node).split(this, arrayOfNode1, b, b1);
/*      */           } else {
/*  715 */             Node<K, V> node1 = null, node2 = null;
/*  716 */             Node<K, V> node3 = null, node4 = null;
/*      */             
/*      */             while (true) {
/*  719 */               Node<K, V> node5 = node.next;
/*  720 */               if ((node.hash & b1) == 0) {
/*  721 */                 if (node2 == null) {
/*  722 */                   node1 = node;
/*      */                 } else {
/*  724 */                   node2.next = node;
/*  725 */                 }  node2 = node;
/*      */               } else {
/*      */                 
/*  728 */                 if (node4 == null) {
/*  729 */                   node3 = node;
/*      */                 } else {
/*  731 */                   node4.next = node;
/*  732 */                 }  node4 = node;
/*      */               } 
/*  734 */               if ((node = node5) == null) {
/*  735 */                 if (node2 != null) {
/*  736 */                   node2.next = null;
/*  737 */                   arrayOfNode1[b] = node1;
/*      */                 } 
/*  739 */                 if (node4 != null) {
/*  740 */                   node4.next = null;
/*  741 */                   arrayOfNode1[b + b1] = node3;
/*      */                 }  break;
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*  747 */       }   return (Node<K, V>[])arrayOfNode1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void treeifyBin(Node<K, V>[] paramArrayOfNode, int paramInt) {
/*      */     int i;
/*  756 */     if (paramArrayOfNode == null || (i = paramArrayOfNode.length) < 64)
/*  757 */     { resize(); }
/*  758 */     else { int j; Node<K, V> node; if ((node = paramArrayOfNode[j = i - 1 & paramInt]) != null) {
/*  759 */         TreeNode<K, V> treeNode1 = null, treeNode2 = null;
/*      */         while (true) {
/*  761 */           TreeNode<K, V> treeNode = replacementTreeNode(node, (Node<K, V>)null);
/*  762 */           if (treeNode2 == null) {
/*  763 */             treeNode1 = treeNode;
/*      */           } else {
/*  765 */             treeNode.prev = treeNode2;
/*  766 */             treeNode2.next = treeNode;
/*      */           } 
/*  768 */           treeNode2 = treeNode;
/*  769 */           if ((node = node.next) == null) {
/*  770 */             paramArrayOfNode[j] = treeNode1; if (treeNode1 != null) {
/*  771 */               treeNode1.treeify(paramArrayOfNode);
/*      */             }
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       }  }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void putAll(Map<? extends K, ? extends V> paramMap) {
/*  784 */     putMapEntries(paramMap, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public V remove(Object paramObject) {
/*      */     Node<K, V> node;
/*  798 */     return ((node = removeNode(hash(paramObject), paramObject, (Object)null, false, true)) == null) ? null : node.value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final Node<K, V> removeNode(int paramInt, Object paramObject1, Object paramObject2, boolean paramBoolean1, boolean paramBoolean2) {
/*      */     Node<K, V>[] arrayOfNode;
/*      */     Node<K, V> node;
/*      */     int i;
/*      */     int j;
/*  815 */     if ((arrayOfNode = this.table) != null && (i = arrayOfNode.length) > 0 && (node = arrayOfNode[j = i - 1 & paramInt]) != null) {
/*      */       
/*  817 */       Node<K, V> node1 = null; K k;
/*  818 */       if (node.hash == paramInt && ((k = node.key) == paramObject1 || (paramObject1 != null && paramObject1
/*  819 */         .equals(k))))
/*  820 */       { node1 = node; }
/*  821 */       else { Node<K, V> node2; if ((node2 = node.next) != null)
/*  822 */           if (node instanceof TreeNode) {
/*  823 */             node1 = ((TreeNode<K, V>)node).getTreeNode(paramInt, paramObject1);
/*      */           } else {
/*      */             do {
/*  826 */               if (node2.hash == paramInt && ((k = node2.key) == paramObject1 || (paramObject1 != null && paramObject1
/*      */                 
/*  828 */                 .equals(k)))) {
/*  829 */                 node1 = node2;
/*      */                 break;
/*      */               } 
/*  832 */               node = node2;
/*  833 */             } while ((node2 = node2.next) != null);
/*      */           }   }
/*      */        V v;
/*  836 */       if (node1 != null && (!paramBoolean1 || (v = node1.value) == paramObject2 || (paramObject2 != null && paramObject2
/*  837 */         .equals(v)))) {
/*  838 */         if (node1 instanceof TreeNode) {
/*  839 */           ((TreeNode<K, V>)node1).removeTreeNode(this, arrayOfNode, paramBoolean2);
/*  840 */         } else if (node1 == node) {
/*  841 */           arrayOfNode[j] = node1.next;
/*      */         } else {
/*  843 */           node.next = node1.next;
/*  844 */         }  this.modCount++;
/*  845 */         this.size--;
/*  846 */         afterNodeRemoval(node1);
/*  847 */         return node1;
/*      */       } 
/*      */     } 
/*  850 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clear() {
/*  859 */     this.modCount++; Node<K, V>[] arrayOfNode;
/*  860 */     if ((arrayOfNode = this.table) != null && this.size > 0) {
/*  861 */       this.size = 0;
/*  862 */       for (byte b = 0; b < arrayOfNode.length; b++) {
/*  863 */         arrayOfNode[b] = null;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean containsValue(Object paramObject) {
/*      */     Node<K, V>[] arrayOfNode;
/*  877 */     if ((arrayOfNode = this.table) != null && this.size > 0)
/*  878 */       for (byte b = 0; b < arrayOfNode.length; b++) {
/*  879 */         for (Node<K, V> node = arrayOfNode[b]; node != null; node = node.next) {
/*  880 */           V v; if ((v = node.value) == paramObject || (paramObject != null && paramObject
/*  881 */             .equals(v))) {
/*  882 */             return true;
/*      */           }
/*      */         } 
/*      */       }  
/*  886 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set<K> keySet() {
/*      */     Set<K> set;
/*  906 */     return ((set = this.keySet) == null) ? (this.keySet = new KeySet()) : set;
/*      */   }
/*      */   
/*      */   final class KeySet extends AbstractSet<K> {
/*  910 */     public final int size() { return HashMap.this.size; }
/*  911 */     public final void clear() { HashMap.this.clear(); }
/*  912 */     public final Iterator<K> iterator() { return new HashMap.KeyIterator(); } public final boolean contains(Object param1Object) {
/*  913 */       return HashMap.this.containsKey(param1Object);
/*      */     } public final boolean remove(Object param1Object) {
/*  915 */       return (HashMap.this.removeNode(HashMap.hash(param1Object), param1Object, (Object)null, false, true) != null);
/*      */     }
/*      */     public final Spliterator<K> spliterator() {
/*  918 */       return new HashMap.KeySpliterator<>(HashMap.this, 0, -1, 0, 0);
/*      */     }
/*      */     
/*      */     public final void forEach(Consumer<? super K> param1Consumer) {
/*  922 */       if (param1Consumer == null)
/*  923 */         throw new NullPointerException();  HashMap.Node[] arrayOfNode;
/*  924 */       if (HashMap.this.size > 0 && (arrayOfNode = HashMap.this.table) != null) {
/*  925 */         int i = HashMap.this.modCount;
/*  926 */         for (byte b = 0; b < arrayOfNode.length; b++) {
/*  927 */           for (HashMap.Node node = arrayOfNode[b]; node != null; node = node.next)
/*  928 */             param1Consumer.accept(node.key); 
/*      */         } 
/*  930 */         if (HashMap.this.modCount != i) {
/*  931 */           throw new ConcurrentModificationException();
/*      */         }
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Collection<V> values() {
/*      */     Collection<V> collection;
/*  953 */     return ((collection = this.values) == null) ? (this.values = new Values()) : collection;
/*      */   }
/*      */   
/*      */   final class Values extends AbstractCollection<V> {
/*  957 */     public final int size() { return HashMap.this.size; }
/*  958 */     public final void clear() { HashMap.this.clear(); }
/*  959 */     public final Iterator<V> iterator() { return new HashMap.ValueIterator(); } public final boolean contains(Object param1Object) {
/*  960 */       return HashMap.this.containsValue(param1Object);
/*      */     } public final Spliterator<V> spliterator() {
/*  962 */       return new HashMap.ValueSpliterator<>(HashMap.this, 0, -1, 0, 0);
/*      */     }
/*      */     
/*      */     public final void forEach(Consumer<? super V> param1Consumer) {
/*  966 */       if (param1Consumer == null)
/*  967 */         throw new NullPointerException();  HashMap.Node[] arrayOfNode;
/*  968 */       if (HashMap.this.size > 0 && (arrayOfNode = HashMap.this.table) != null) {
/*  969 */         int i = HashMap.this.modCount;
/*  970 */         for (byte b = 0; b < arrayOfNode.length; b++) {
/*  971 */           for (HashMap.Node node = arrayOfNode[b]; node != null; node = node.next)
/*  972 */             param1Consumer.accept(node.value); 
/*      */         } 
/*  974 */         if (HashMap.this.modCount != i) {
/*  975 */           throw new ConcurrentModificationException();
/*      */         }
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set<Map.Entry<K, V>> entrySet() {
/*      */     Set<Map.Entry<K, V>> set;
/*  998 */     return ((set = this.entrySet) == null) ? (this.entrySet = new EntrySet()) : set;
/*      */   }
/*      */   
/*      */   final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
/* 1002 */     public final int size() { return HashMap.this.size; } public final void clear() {
/* 1003 */       HashMap.this.clear();
/*      */     } public final Iterator<Map.Entry<K, V>> iterator() {
/* 1005 */       return new HashMap.EntryIterator();
/*      */     }
/*      */     public final boolean contains(Object param1Object) {
/* 1008 */       if (!(param1Object instanceof Map.Entry))
/* 1009 */         return false; 
/* 1010 */       Map.Entry entry = (Map.Entry)param1Object;
/* 1011 */       Object object = entry.getKey();
/* 1012 */       HashMap.Node node = HashMap.this.getNode(HashMap.hash(object), object);
/* 1013 */       return (node != null && node.equals(entry));
/*      */     }
/*      */     public final boolean remove(Object param1Object) {
/* 1016 */       if (param1Object instanceof Map.Entry) {
/* 1017 */         Map.Entry entry = (Map.Entry)param1Object;
/* 1018 */         Object object1 = entry.getKey();
/* 1019 */         Object object2 = entry.getValue();
/* 1020 */         return (HashMap.this.removeNode(HashMap.hash(object1), object1, object2, true, true) != null);
/*      */       } 
/* 1022 */       return false;
/*      */     }
/*      */     public final Spliterator<Map.Entry<K, V>> spliterator() {
/* 1025 */       return new HashMap.EntrySpliterator<>(HashMap.this, 0, -1, 0, 0);
/*      */     }
/*      */     
/*      */     public final void forEach(Consumer<? super Map.Entry<K, V>> param1Consumer) {
/* 1029 */       if (param1Consumer == null)
/* 1030 */         throw new NullPointerException();  HashMap.Node[] arrayOfNode;
/* 1031 */       if (HashMap.this.size > 0 && (arrayOfNode = HashMap.this.table) != null) {
/* 1032 */         int i = HashMap.this.modCount;
/* 1033 */         for (byte b = 0; b < arrayOfNode.length; b++) {
/* 1034 */           for (HashMap.Node<K, V> node = arrayOfNode[b]; node != null; node = node.next)
/* 1035 */             param1Consumer.accept(node); 
/*      */         } 
/* 1037 */         if (HashMap.this.modCount != i) {
/* 1038 */           throw new ConcurrentModificationException();
/*      */         }
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public V getOrDefault(Object paramObject, V paramV) {
/*      */     Node<K, V> node;
/* 1048 */     return ((node = getNode(hash(paramObject), paramObject)) == null) ? paramV : node.value;
/*      */   }
/*      */ 
/*      */   
/*      */   public V putIfAbsent(K paramK, V paramV) {
/* 1053 */     return putVal(hash(paramK), paramK, paramV, true, true);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean remove(Object paramObject1, Object paramObject2) {
/* 1058 */     return (removeNode(hash(paramObject1), paramObject1, paramObject2, true, true) != null);
/*      */   }
/*      */   
/*      */   public boolean replace(K paramK, V paramV1, V paramV2) {
/*      */     Node<K, V> node;
/*      */     V v;
/* 1064 */     if ((node = getNode(hash(paramK), paramK)) != null && ((v = node.value) == paramV1 || (v != null && v
/* 1065 */       .equals(paramV1)))) {
/* 1066 */       node.value = paramV2;
/* 1067 */       afterNodeAccess(node);
/* 1068 */       return true;
/*      */     } 
/* 1070 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public V replace(K paramK, V paramV) {
/*      */     Node<K, V> node;
/* 1076 */     if ((node = getNode(hash(paramK), paramK)) != null) {
/* 1077 */       V v = node.value;
/* 1078 */       node.value = paramV;
/* 1079 */       afterNodeAccess(node);
/* 1080 */       return v;
/*      */     } 
/* 1082 */     return null;
/*      */   }
/*      */   
/*      */   public V computeIfAbsent(K paramK, Function<? super K, ? extends V> paramFunction) {
/*      */     Node[] arrayOfNode;
/*      */     Node<K, V> node2;
/* 1088 */     if (paramFunction == null)
/* 1089 */       throw new NullPointerException(); 
/* 1090 */     int i = hash(paramK);
/*      */     
/* 1092 */     byte b = 0;
/* 1093 */     TreeNode<?, ?> treeNode1 = null;
/* 1094 */     TreeNode<?, ?> treeNode2 = null; Node<K, V>[] arrayOfNode1; int j;
/* 1095 */     if (this.size > this.threshold || (arrayOfNode1 = this.table) == null || (j = arrayOfNode1.length) == 0)
/*      */     {
/* 1097 */       j = (arrayOfNode = (Node[])resize()).length; }  Node<K, V> node1; int k;
/* 1098 */     if ((node1 = arrayOfNode[k = j - 1 & i]) != null) {
/* 1099 */       if (node1 instanceof TreeNode) {
/* 1100 */         treeNode2 = (treeNode1 = (TreeNode<?, ?>)node1).getTreeNode(i, paramK);
/*      */       } else {
/* 1102 */         Node<K, V> node = node1; do {
/*      */           K k1;
/* 1104 */           if (node.hash == i && ((k1 = node.key) == paramK || (paramK != null && paramK
/* 1105 */             .equals(k1)))) {
/* 1106 */             node2 = node;
/*      */             break;
/*      */           } 
/* 1109 */           ++b;
/* 1110 */         } while ((node = node.next) != null);
/*      */       } 
/*      */       
/* 1113 */       if (node2 != null && (v = node2.value) != null) {
/* 1114 */         afterNodeAccess(node2);
/* 1115 */         return v;
/*      */       } 
/*      */     } 
/* 1118 */     V v = paramFunction.apply(paramK);
/* 1119 */     if (v == null)
/* 1120 */       return null; 
/* 1121 */     if (node2 != null) {
/* 1122 */       node2.value = v;
/* 1123 */       afterNodeAccess(node2);
/* 1124 */       return v;
/*      */     } 
/* 1126 */     if (treeNode1 != null) {
/* 1127 */       treeNode1.putTreeVal(this, (Node<?, ?>[])arrayOfNode, i, paramK, v);
/*      */     } else {
/* 1129 */       arrayOfNode[k] = newNode(i, paramK, v, node1);
/* 1130 */       if (b >= 7)
/* 1131 */         treeifyBin((Node<K, V>[])arrayOfNode, i); 
/*      */     } 
/* 1133 */     this.modCount++;
/* 1134 */     this.size++;
/* 1135 */     afterNodeInsertion(true);
/* 1136 */     return v;
/*      */   }
/*      */ 
/*      */   
/*      */   public V computeIfPresent(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
/* 1141 */     if (paramBiFunction == null) {
/* 1142 */       throw new NullPointerException();
/*      */     }
/* 1144 */     int i = hash(paramK); Node<K, V> node; V v;
/* 1145 */     if ((node = getNode(i, paramK)) != null && (v = node.value) != null) {
/*      */       
/* 1147 */       V v1 = paramBiFunction.apply(paramK, v);
/* 1148 */       if (v1 != null) {
/* 1149 */         node.value = v1;
/* 1150 */         afterNodeAccess(node);
/* 1151 */         return v1;
/*      */       } 
/*      */       
/* 1154 */       removeNode(i, paramK, (Object)null, false, true);
/*      */     } 
/* 1156 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public V compute(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
/*      */     Node[] arrayOfNode;
/* 1162 */     if (paramBiFunction == null)
/* 1163 */       throw new NullPointerException(); 
/* 1164 */     int i = hash(paramK);
/*      */     
/* 1166 */     byte b = 0;
/* 1167 */     TreeNode<?, ?> treeNode = null;
/* 1168 */     Node<K, V> node2 = null; Node<K, V>[] arrayOfNode1; int j;
/* 1169 */     if (this.size > this.threshold || (arrayOfNode1 = this.table) == null || (j = arrayOfNode1.length) == 0)
/*      */     {
/* 1171 */       j = (arrayOfNode = (Node[])resize()).length; }  Node<K, V> node1; int k;
/* 1172 */     if ((node1 = arrayOfNode[k = j - 1 & i]) != null) {
/* 1173 */       if (node1 instanceof TreeNode) {
/* 1174 */         node2 = (Node)(treeNode = (TreeNode<?, ?>)node1).getTreeNode(i, paramK);
/*      */       } else {
/* 1176 */         Node<K, V> node = node1; do {
/*      */           K k1;
/* 1178 */           if (node.hash == i && ((k1 = node.key) == paramK || (paramK != null && paramK
/* 1179 */             .equals(k1)))) {
/* 1180 */             node2 = node;
/*      */             break;
/*      */           } 
/* 1183 */           ++b;
/* 1184 */         } while ((node = node.next) != null);
/*      */       } 
/*      */     }
/* 1187 */     V v1 = (node2 == null) ? null : node2.value;
/* 1188 */     V v2 = paramBiFunction.apply(paramK, v1);
/* 1189 */     if (node2 != null) {
/* 1190 */       if (v2 != null) {
/* 1191 */         node2.value = v2;
/* 1192 */         afterNodeAccess(node2);
/*      */       } else {
/*      */         
/* 1195 */         removeNode(i, paramK, (Object)null, false, true);
/*      */       } 
/* 1197 */     } else if (v2 != null) {
/* 1198 */       if (treeNode != null) {
/* 1199 */         treeNode.putTreeVal(this, (Node<?, ?>[])arrayOfNode, i, paramK, v2);
/*      */       } else {
/* 1201 */         arrayOfNode[k] = newNode(i, paramK, v2, node1);
/* 1202 */         if (b >= 7)
/* 1203 */           treeifyBin((Node<K, V>[])arrayOfNode, i); 
/*      */       } 
/* 1205 */       this.modCount++;
/* 1206 */       this.size++;
/* 1207 */       afterNodeInsertion(true);
/*      */     } 
/* 1209 */     return v2;
/*      */   }
/*      */ 
/*      */   
/*      */   public V merge(K paramK, V paramV, BiFunction<? super V, ? super V, ? extends V> paramBiFunction) {
/*      */     Node[] arrayOfNode;
/* 1215 */     if (paramV == null)
/* 1216 */       throw new NullPointerException(); 
/* 1217 */     if (paramBiFunction == null)
/* 1218 */       throw new NullPointerException(); 
/* 1219 */     int i = hash(paramK);
/*      */     
/* 1221 */     byte b = 0;
/* 1222 */     TreeNode<?, ?> treeNode = null;
/* 1223 */     Node<K, V> node2 = null; Node<K, V>[] arrayOfNode1; int j;
/* 1224 */     if (this.size > this.threshold || (arrayOfNode1 = this.table) == null || (j = arrayOfNode1.length) == 0)
/*      */     {
/* 1226 */       j = (arrayOfNode = (Node[])resize()).length; }  Node<K, V> node1; int k;
/* 1227 */     if ((node1 = arrayOfNode[k = j - 1 & i]) != null) {
/* 1228 */       if (node1 instanceof TreeNode) {
/* 1229 */         node2 = (Node)(treeNode = (TreeNode<?, ?>)node1).getTreeNode(i, paramK);
/*      */       } else {
/* 1231 */         Node<K, V> node = node1; do {
/*      */           K k1;
/* 1233 */           if (node.hash == i && ((k1 = node.key) == paramK || (paramK != null && paramK
/* 1234 */             .equals(k1)))) {
/* 1235 */             node2 = node;
/*      */             break;
/*      */           } 
/* 1238 */           ++b;
/* 1239 */         } while ((node = node.next) != null);
/*      */       } 
/*      */     }
/* 1242 */     if (node2 != null) {
/*      */       V v;
/* 1244 */       if (node2.value != null) {
/* 1245 */         v = paramBiFunction.apply(node2.value, paramV);
/*      */       } else {
/* 1247 */         v = paramV;
/* 1248 */       }  if (v != null) {
/* 1249 */         node2.value = v;
/* 1250 */         afterNodeAccess(node2);
/*      */       } else {
/*      */         
/* 1253 */         removeNode(i, paramK, (Object)null, false, true);
/* 1254 */       }  return v;
/*      */     } 
/* 1256 */     if (paramV != null) {
/* 1257 */       if (treeNode != null) {
/* 1258 */         treeNode.putTreeVal(this, (Node<?, ?>[])arrayOfNode, i, paramK, paramV);
/*      */       } else {
/* 1260 */         arrayOfNode[k] = newNode(i, paramK, paramV, node1);
/* 1261 */         if (b >= 7)
/* 1262 */           treeifyBin((Node<K, V>[])arrayOfNode, i); 
/*      */       } 
/* 1264 */       this.modCount++;
/* 1265 */       this.size++;
/* 1266 */       afterNodeInsertion(true);
/*      */     } 
/* 1268 */     return paramV;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void forEach(BiConsumer<? super K, ? super V> paramBiConsumer) {
/* 1274 */     if (paramBiConsumer == null)
/* 1275 */       throw new NullPointerException();  Node<K, V>[] arrayOfNode;
/* 1276 */     if (this.size > 0 && (arrayOfNode = this.table) != null) {
/* 1277 */       int i = this.modCount;
/* 1278 */       for (byte b = 0; b < arrayOfNode.length; b++) {
/* 1279 */         for (Node<K, V> node = arrayOfNode[b]; node != null; node = node.next)
/* 1280 */           paramBiConsumer.accept(node.key, node.value); 
/*      */       } 
/* 1282 */       if (this.modCount != i) {
/* 1283 */         throw new ConcurrentModificationException();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
/* 1290 */     if (paramBiFunction == null)
/* 1291 */       throw new NullPointerException();  Node<K, V>[] arrayOfNode;
/* 1292 */     if (this.size > 0 && (arrayOfNode = this.table) != null) {
/* 1293 */       int i = this.modCount;
/* 1294 */       for (byte b = 0; b < arrayOfNode.length; b++) {
/* 1295 */         for (Node<K, V> node = arrayOfNode[b]; node != null; node = node.next) {
/* 1296 */           node.value = paramBiFunction.apply(node.key, node.value);
/*      */         }
/*      */       } 
/* 1299 */       if (this.modCount != i) {
/* 1300 */         throw new ConcurrentModificationException();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object clone() {
/*      */     HashMap hashMap;
/*      */     try {
/* 1318 */       hashMap = (HashMap)super.clone();
/* 1319 */     } catch (CloneNotSupportedException cloneNotSupportedException) {
/*      */       
/* 1321 */       throw new InternalError(cloneNotSupportedException);
/*      */     } 
/* 1323 */     hashMap.reinitialize();
/* 1324 */     hashMap.putMapEntries(this, false);
/* 1325 */     return hashMap;
/*      */   }
/*      */   
/*      */   final float loadFactor() {
/* 1329 */     return this.loadFactor;
/*      */   } final int capacity() {
/* 1331 */     return (this.table != null) ? this.table.length : ((this.threshold > 0) ? this.threshold : 16);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
/* 1349 */     int i = capacity();
/*      */     
/* 1351 */     paramObjectOutputStream.defaultWriteObject();
/* 1352 */     paramObjectOutputStream.writeInt(i);
/* 1353 */     paramObjectOutputStream.writeInt(this.size);
/* 1354 */     internalWriteEntries(paramObjectOutputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
/* 1364 */     paramObjectInputStream.defaultReadObject();
/* 1365 */     reinitialize();
/* 1366 */     if (this.loadFactor <= 0.0F || Float.isNaN(this.loadFactor)) {
/* 1367 */       throw new InvalidObjectException("Illegal load factor: " + this.loadFactor);
/*      */     }
/* 1369 */     paramObjectInputStream.readInt();
/* 1370 */     int i = paramObjectInputStream.readInt();
/* 1371 */     if (i < 0) {
/* 1372 */       throw new InvalidObjectException("Illegal mappings count: " + i);
/*      */     }
/* 1374 */     if (i > 0) {
/*      */ 
/*      */       
/* 1377 */       float f1 = Math.min(Math.max(0.25F, this.loadFactor), 4.0F);
/* 1378 */       float f2 = i / f1 + 1.0F;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1383 */       byte b1 = (f2 < 16.0F) ? 16 : ((f2 >= 1.07374182E9F) ? 1073741824 : tableSizeFor((int)f2));
/* 1384 */       float f3 = b1 * f1;
/* 1385 */       this.threshold = (b1 < 1073741824 && f3 < 1.07374182E9F) ? (int)f3 : Integer.MAX_VALUE;
/*      */ 
/*      */       
/* 1388 */       Node[] arrayOfNode = new Node[b1];
/* 1389 */       this.table = (Node<K, V>[])arrayOfNode;
/*      */ 
/*      */       
/* 1392 */       for (byte b2 = 0; b2 < i; b2++) {
/*      */         
/* 1394 */         Object object1 = paramObjectInputStream.readObject();
/*      */         
/* 1396 */         Object object2 = paramObjectInputStream.readObject();
/* 1397 */         putVal(hash(object1), (K)object1, (V)object2, false, false);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   abstract class HashIterator
/*      */   {
/*      */     HashMap.Node<K, V> next;
/*      */ 
/*      */     
/*      */     HashMap.Node<K, V> current;
/*      */ 
/*      */     
/* 1412 */     int expectedModCount = HashMap.this.modCount; int index; HashIterator() {
/* 1413 */       HashMap.Node[] arrayOfNode = HashMap.this.table;
/* 1414 */       this.current = this.next = null;
/* 1415 */       this.index = 0;
/* 1416 */       if (arrayOfNode != null && HashMap.this.size > 0) {
/* 1417 */         do {  } while (this.index < arrayOfNode.length && (this.next = arrayOfNode[this.index++]) == null);
/*      */       }
/*      */     }
/*      */     
/*      */     public final boolean hasNext() {
/* 1422 */       return (this.next != null);
/*      */     }
/*      */ 
/*      */     
/*      */     final HashMap.Node<K, V> nextNode() {
/* 1427 */       HashMap.Node<K, V> node = this.next;
/* 1428 */       if (HashMap.this.modCount != this.expectedModCount)
/* 1429 */         throw new ConcurrentModificationException(); 
/* 1430 */       if (node == null)
/* 1431 */         throw new NoSuchElementException();  HashMap.Node[] arrayOfNode;
/* 1432 */       if ((this.next = (this.current = node).next) == null && (arrayOfNode = HashMap.this.table) != null) {
/* 1433 */         do {  } while (this.index < arrayOfNode.length && (this.next = arrayOfNode[this.index++]) == null);
/*      */       }
/* 1435 */       return node;
/*      */     }
/*      */     
/*      */     public final void remove() {
/* 1439 */       HashMap.Node<K, V> node = this.current;
/* 1440 */       if (node == null)
/* 1441 */         throw new IllegalStateException(); 
/* 1442 */       if (HashMap.this.modCount != this.expectedModCount)
/* 1443 */         throw new ConcurrentModificationException(); 
/* 1444 */       this.current = null;
/* 1445 */       K k = node.key;
/* 1446 */       HashMap.this.removeNode(HashMap.hash(k), k, (Object)null, false, false);
/* 1447 */       this.expectedModCount = HashMap.this.modCount;
/*      */     }
/*      */   }
/*      */   
/*      */   final class KeyIterator extends HashIterator implements Iterator<K> {
/*      */     public final K next() {
/* 1453 */       return (nextNode()).key;
/*      */     }
/*      */   }
/*      */   
/*      */   final class ValueIterator extends HashIterator implements Iterator<V> { public final V next() {
/* 1458 */       return (nextNode()).value;
/*      */     } }
/*      */   
/*      */   final class EntryIterator extends HashIterator implements Iterator<Map.Entry<K, V>> {
/*      */     public final Map.Entry<K, V> next() {
/* 1463 */       return nextNode();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class HashMapSpliterator<K, V>
/*      */   {
/*      */     final HashMap<K, V> map;
/*      */     
/*      */     HashMap.Node<K, V> current;
/*      */     
/*      */     int index;
/*      */     int fence;
/*      */     int est;
/*      */     int expectedModCount;
/*      */     
/*      */     HashMapSpliterator(HashMap<K, V> param1HashMap, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
/* 1480 */       this.map = param1HashMap;
/* 1481 */       this.index = param1Int1;
/* 1482 */       this.fence = param1Int2;
/* 1483 */       this.est = param1Int3;
/* 1484 */       this.expectedModCount = param1Int4;
/*      */     }
/*      */     
/*      */     final int getFence() {
/*      */       int i;
/* 1489 */       if ((i = this.fence) < 0) {
/* 1490 */         HashMap<K, V> hashMap = this.map;
/* 1491 */         this.est = hashMap.size;
/* 1492 */         this.expectedModCount = hashMap.modCount;
/* 1493 */         HashMap.Node<K, V>[] arrayOfNode = hashMap.table;
/* 1494 */         i = this.fence = (arrayOfNode == null) ? 0 : arrayOfNode.length;
/*      */       } 
/* 1496 */       return i;
/*      */     }
/*      */     
/*      */     public final long estimateSize() {
/* 1500 */       getFence();
/* 1501 */       return this.est;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class KeySpliterator<K, V>
/*      */     extends HashMapSpliterator<K, V>
/*      */     implements Spliterator<K>
/*      */   {
/*      */     KeySpliterator(HashMap<K, V> param1HashMap, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
/* 1510 */       super(param1HashMap, param1Int1, param1Int2, param1Int3, param1Int4);
/*      */     }
/*      */     
/*      */     public KeySpliterator<K, V> trySplit() {
/* 1514 */       int i = getFence(), j = this.index, k = j + i >>> 1;
/* 1515 */       return (j >= k || this.current != null) ? null : new KeySpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void forEachRemaining(Consumer<? super K> param1Consumer) {
/*      */       int k;
/* 1522 */       if (param1Consumer == null)
/* 1523 */         throw new NullPointerException(); 
/* 1524 */       HashMap<K, V> hashMap = this.map;
/* 1525 */       HashMap.Node<K, V>[] arrayOfNode = hashMap.table; int j;
/* 1526 */       if ((j = this.fence) < 0) {
/* 1527 */         k = this.expectedModCount = hashMap.modCount;
/* 1528 */         j = this.fence = (arrayOfNode == null) ? 0 : arrayOfNode.length;
/*      */       } else {
/*      */         
/* 1531 */         k = this.expectedModCount;
/* 1532 */       }  int i; if (arrayOfNode != null && arrayOfNode.length >= j && (i = this.index) >= 0 && (i < (this.index = j) || this.current != null)) {
/*      */         
/* 1534 */         HashMap.Node<K, V> node = this.current;
/* 1535 */         this.current = null;
/*      */         while (true) {
/* 1537 */           if (node == null) {
/* 1538 */             node = arrayOfNode[i++];
/*      */           } else {
/* 1540 */             param1Consumer.accept(node.key);
/* 1541 */             node = node.next;
/*      */           } 
/* 1543 */           if (node == null && i >= j) {
/* 1544 */             if (hashMap.modCount != k)
/* 1545 */               throw new ConcurrentModificationException(); 
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } public boolean tryAdvance(Consumer<? super K> param1Consumer) {
/* 1551 */       if (param1Consumer == null)
/* 1552 */         throw new NullPointerException(); 
/* 1553 */       HashMap.Node<K, V>[] arrayOfNode = this.map.table; int i;
/* 1554 */       if (arrayOfNode != null && arrayOfNode.length >= (i = getFence()) && this.index >= 0) {
/* 1555 */         while (this.current != null || this.index < i) {
/* 1556 */           if (this.current == null) {
/* 1557 */             this.current = arrayOfNode[this.index++]; continue;
/*      */           } 
/* 1559 */           K k = this.current.key;
/* 1560 */           this.current = this.current.next;
/* 1561 */           param1Consumer.accept(k);
/* 1562 */           if (this.map.modCount != this.expectedModCount)
/* 1563 */             throw new ConcurrentModificationException(); 
/* 1564 */           return true;
/*      */         } 
/*      */       }
/*      */       
/* 1568 */       return false;
/*      */     }
/*      */     
/*      */     public int characteristics() {
/* 1572 */       return ((this.fence < 0 || this.est == this.map.size) ? 64 : 0) | 0x1;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static final class ValueSpliterator<K, V>
/*      */     extends HashMapSpliterator<K, V>
/*      */     implements Spliterator<V>
/*      */   {
/*      */     ValueSpliterator(HashMap<K, V> param1HashMap, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
/* 1582 */       super(param1HashMap, param1Int1, param1Int2, param1Int3, param1Int4);
/*      */     }
/*      */     
/*      */     public ValueSpliterator<K, V> trySplit() {
/* 1586 */       int i = getFence(), j = this.index, k = j + i >>> 1;
/* 1587 */       return (j >= k || this.current != null) ? null : new ValueSpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void forEachRemaining(Consumer<? super V> param1Consumer) {
/*      */       int k;
/* 1594 */       if (param1Consumer == null)
/* 1595 */         throw new NullPointerException(); 
/* 1596 */       HashMap<K, V> hashMap = this.map;
/* 1597 */       HashMap.Node<K, V>[] arrayOfNode = hashMap.table; int j;
/* 1598 */       if ((j = this.fence) < 0) {
/* 1599 */         k = this.expectedModCount = hashMap.modCount;
/* 1600 */         j = this.fence = (arrayOfNode == null) ? 0 : arrayOfNode.length;
/*      */       } else {
/*      */         
/* 1603 */         k = this.expectedModCount;
/* 1604 */       }  int i; if (arrayOfNode != null && arrayOfNode.length >= j && (i = this.index) >= 0 && (i < (this.index = j) || this.current != null)) {
/*      */         
/* 1606 */         HashMap.Node<K, V> node = this.current;
/* 1607 */         this.current = null;
/*      */         while (true) {
/* 1609 */           if (node == null) {
/* 1610 */             node = arrayOfNode[i++];
/*      */           } else {
/* 1612 */             param1Consumer.accept(node.value);
/* 1613 */             node = node.next;
/*      */           } 
/* 1615 */           if (node == null && i >= j) {
/* 1616 */             if (hashMap.modCount != k)
/* 1617 */               throw new ConcurrentModificationException(); 
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } public boolean tryAdvance(Consumer<? super V> param1Consumer) {
/* 1623 */       if (param1Consumer == null)
/* 1624 */         throw new NullPointerException(); 
/* 1625 */       HashMap.Node<K, V>[] arrayOfNode = this.map.table; int i;
/* 1626 */       if (arrayOfNode != null && arrayOfNode.length >= (i = getFence()) && this.index >= 0) {
/* 1627 */         while (this.current != null || this.index < i) {
/* 1628 */           if (this.current == null) {
/* 1629 */             this.current = arrayOfNode[this.index++]; continue;
/*      */           } 
/* 1631 */           V v = this.current.value;
/* 1632 */           this.current = this.current.next;
/* 1633 */           param1Consumer.accept(v);
/* 1634 */           if (this.map.modCount != this.expectedModCount)
/* 1635 */             throw new ConcurrentModificationException(); 
/* 1636 */           return true;
/*      */         } 
/*      */       }
/*      */       
/* 1640 */       return false;
/*      */     }
/*      */     
/*      */     public int characteristics() {
/* 1644 */       return (this.fence < 0 || this.est == this.map.size) ? 64 : 0;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class EntrySpliterator<K, V>
/*      */     extends HashMapSpliterator<K, V>
/*      */     implements Spliterator<Map.Entry<K, V>>
/*      */   {
/*      */     EntrySpliterator(HashMap<K, V> param1HashMap, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
/* 1653 */       super(param1HashMap, param1Int1, param1Int2, param1Int3, param1Int4);
/*      */     }
/*      */     
/*      */     public EntrySpliterator<K, V> trySplit() {
/* 1657 */       int i = getFence(), j = this.index, k = j + i >>> 1;
/* 1658 */       return (j >= k || this.current != null) ? null : new EntrySpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void forEachRemaining(Consumer<? super Map.Entry<K, V>> param1Consumer) {
/*      */       int k;
/* 1665 */       if (param1Consumer == null)
/* 1666 */         throw new NullPointerException(); 
/* 1667 */       HashMap<K, V> hashMap = this.map;
/* 1668 */       HashMap.Node<K, V>[] arrayOfNode = hashMap.table; int j;
/* 1669 */       if ((j = this.fence) < 0) {
/* 1670 */         k = this.expectedModCount = hashMap.modCount;
/* 1671 */         j = this.fence = (arrayOfNode == null) ? 0 : arrayOfNode.length;
/*      */       } else {
/*      */         
/* 1674 */         k = this.expectedModCount;
/* 1675 */       }  int i; if (arrayOfNode != null && arrayOfNode.length >= j && (i = this.index) >= 0 && (i < (this.index = j) || this.current != null)) {
/*      */         
/* 1677 */         HashMap.Node<K, V> node = this.current;
/* 1678 */         this.current = null;
/*      */         while (true) {
/* 1680 */           if (node == null) {
/* 1681 */             node = arrayOfNode[i++];
/*      */           } else {
/* 1683 */             param1Consumer.accept(node);
/* 1684 */             node = node.next;
/*      */           } 
/* 1686 */           if (node == null && i >= j) {
/* 1687 */             if (hashMap.modCount != k)
/* 1688 */               throw new ConcurrentModificationException(); 
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> param1Consumer) {
/* 1694 */       if (param1Consumer == null)
/* 1695 */         throw new NullPointerException(); 
/* 1696 */       HashMap.Node<K, V>[] arrayOfNode = this.map.table; int i;
/* 1697 */       if (arrayOfNode != null && arrayOfNode.length >= (i = getFence()) && this.index >= 0) {
/* 1698 */         while (this.current != null || this.index < i) {
/* 1699 */           if (this.current == null) {
/* 1700 */             this.current = arrayOfNode[this.index++]; continue;
/*      */           } 
/* 1702 */           HashMap.Node<K, V> node = this.current;
/* 1703 */           this.current = this.current.next;
/* 1704 */           param1Consumer.accept(node);
/* 1705 */           if (this.map.modCount != this.expectedModCount)
/* 1706 */             throw new ConcurrentModificationException(); 
/* 1707 */           return true;
/*      */         } 
/*      */       }
/*      */       
/* 1711 */       return false;
/*      */     }
/*      */     
/*      */     public int characteristics() {
/* 1715 */       return ((this.fence < 0 || this.est == this.map.size) ? 64 : 0) | 0x1;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Node<K, V> newNode(int paramInt, K paramK, V paramV, Node<K, V> paramNode) {
/* 1734 */     return new Node<>(paramInt, paramK, paramV, paramNode);
/*      */   }
/*      */ 
/*      */   
/*      */   Node<K, V> replacementNode(Node<K, V> paramNode1, Node<K, V> paramNode2) {
/* 1739 */     return new Node<>(paramNode1.hash, paramNode1.key, paramNode1.value, paramNode2);
/*      */   }
/*      */ 
/*      */   
/*      */   TreeNode<K, V> newTreeNode(int paramInt, K paramK, V paramV, Node<K, V> paramNode) {
/* 1744 */     return new TreeNode<>(paramInt, paramK, paramV, paramNode);
/*      */   }
/*      */ 
/*      */   
/*      */   TreeNode<K, V> replacementTreeNode(Node<K, V> paramNode1, Node<K, V> paramNode2) {
/* 1749 */     return new TreeNode<>(paramNode1.hash, paramNode1.key, paramNode1.value, paramNode2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void reinitialize() {
/* 1756 */     this.table = null;
/* 1757 */     this.entrySet = null;
/* 1758 */     this.keySet = null;
/* 1759 */     this.values = null;
/* 1760 */     this.modCount = 0;
/* 1761 */     this.threshold = 0;
/* 1762 */     this.size = 0;
/*      */   }
/*      */   
/*      */   void afterNodeAccess(Node<K, V> paramNode) {}
/*      */   
/*      */   void afterNodeInsertion(boolean paramBoolean) {}
/*      */   
/*      */   void afterNodeRemoval(Node<K, V> paramNode) {}
/*      */   
/*      */   void internalWriteEntries(ObjectOutputStream paramObjectOutputStream) throws IOException {
/*      */     Node<K, V>[] arrayOfNode;
/* 1773 */     if (this.size > 0 && (arrayOfNode = this.table) != null) {
/* 1774 */       for (byte b = 0; b < arrayOfNode.length; b++) {
/* 1775 */         for (Node<K, V> node = arrayOfNode[b]; node != null; node = node.next) {
/* 1776 */           paramObjectOutputStream.writeObject(node.key);
/* 1777 */           paramObjectOutputStream.writeObject(node.value);
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static final class TreeNode<K, V>
/*      */     extends LinkedHashMap.Entry<K, V>
/*      */   {
/*      */     TreeNode<K, V> parent;
/*      */     
/*      */     TreeNode<K, V> left;
/*      */     
/*      */     TreeNode<K, V> right;
/*      */     
/*      */     TreeNode<K, V> prev;
/*      */     
/*      */     boolean red;
/*      */     
/*      */     TreeNode(int param1Int, K param1K, V param1V, HashMap.Node<K, V> param1Node) {
/* 1798 */       super(param1Int, param1K, param1V, param1Node);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final TreeNode<K, V> root() {
/* 1805 */       TreeNode<K, V> treeNode = this; while (true) {
/* 1806 */         TreeNode<K, V> treeNode1; if ((treeNode1 = treeNode.parent) == null)
/* 1807 */           return treeNode; 
/* 1808 */         treeNode = treeNode1;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     static <K, V> void moveRootToFront(HashMap.Node<K, V>[] param1ArrayOfNode, TreeNode<K, V> param1TreeNode) {
/*      */       int i;
/* 1817 */       if (param1TreeNode != null && param1ArrayOfNode != null && (i = param1ArrayOfNode.length) > 0) {
/* 1818 */         int j = i - 1 & param1TreeNode.hash;
/* 1819 */         TreeNode<K, V> treeNode = (TreeNode)param1ArrayOfNode[j];
/* 1820 */         if (param1TreeNode != treeNode) {
/*      */           
/* 1822 */           param1ArrayOfNode[j] = param1TreeNode;
/* 1823 */           TreeNode<K, V> treeNode1 = param1TreeNode.prev; HashMap.Node<K, V> node;
/* 1824 */           if ((node = param1TreeNode.next) != null)
/* 1825 */             ((TreeNode)node).prev = treeNode1; 
/* 1826 */           if (treeNode1 != null)
/* 1827 */             treeNode1.next = node; 
/* 1828 */           if (treeNode != null)
/* 1829 */             treeNode.prev = param1TreeNode; 
/* 1830 */           param1TreeNode.next = treeNode;
/* 1831 */           param1TreeNode.prev = null;
/*      */         } 
/* 1833 */         assert checkInvariants(param1TreeNode);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final TreeNode<K, V> find(int param1Int, Object param1Object, Class<?> param1Class) {
/* 1843 */       TreeNode<K, V> treeNode = this;
/*      */       
/*      */       while (true) {
/* 1846 */         TreeNode<K, V> treeNode1 = treeNode.left, treeNode2 = treeNode.right; int i;
/* 1847 */         if ((i = treeNode.hash) > param1Int)
/* 1848 */         { treeNode = treeNode1; }
/* 1849 */         else if (i < param1Int)
/* 1850 */         { treeNode = treeNode2; }
/* 1851 */         else { K k; if ((k = treeNode.key) == param1Object || (param1Object != null && param1Object.equals(k)))
/* 1852 */             return treeNode; 
/* 1853 */           if (treeNode1 == null)
/* 1854 */           { treeNode = treeNode2; }
/* 1855 */           else if (treeNode2 == null)
/* 1856 */           { treeNode = treeNode1; }
/* 1857 */           else { int j; if ((param1Class != null || (
/* 1858 */               param1Class = HashMap.comparableClassFor(param1Object)) != null) && (
/* 1859 */               j = HashMap.compareComparables(param1Class, param1Object, k)) != 0)
/* 1860 */             { treeNode = (j < 0) ? treeNode1 : treeNode2; }
/* 1861 */             else { TreeNode<K, V> treeNode3; if ((treeNode3 = treeNode2.find(param1Int, param1Object, param1Class)) != null) {
/* 1862 */                 return treeNode3;
/*      */               }
/* 1864 */               treeNode = treeNode1; }  }  }
/* 1865 */          if (treeNode == null) {
/* 1866 */           return null;
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     final TreeNode<K, V> getTreeNode(int param1Int, Object param1Object) {
/* 1873 */       return ((this.parent != null) ? root() : this).find(param1Int, param1Object, (Class<?>)null);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     static int tieBreakOrder(Object param1Object1, Object param1Object2) {
/*      */       int i;
/* 1885 */       if (param1Object1 == null || param1Object2 == null || (
/*      */         
/* 1887 */         i = param1Object1.getClass().getName().compareTo(param1Object2.getClass().getName())) == 0) {
/* 1888 */         i = (System.identityHashCode(param1Object1) <= System.identityHashCode(param1Object2)) ? -1 : 1;
/*      */       }
/* 1890 */       return i;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final void treeify(HashMap.Node<K, V>[] param1ArrayOfNode) {
/* 1898 */       TreeNode<K, V> treeNode1 = null;
/* 1899 */       for (TreeNode<K, V> treeNode2 = this; treeNode2 != null; treeNode2 = treeNode) {
/* 1900 */         TreeNode<K, V> treeNode = (TreeNode)treeNode2.next;
/* 1901 */         treeNode2.left = treeNode2.right = null;
/* 1902 */         if (treeNode1 == null) {
/* 1903 */           treeNode2.parent = null;
/* 1904 */           treeNode2.red = false;
/* 1905 */           treeNode1 = treeNode2;
/*      */         } else {
/*      */           
/* 1908 */           K k = treeNode2.key;
/* 1909 */           int i = treeNode2.hash;
/* 1910 */           Class<?> clazz = null;
/* 1911 */           TreeNode<K, V> treeNode3 = treeNode1; while (true) {
/*      */             int j;
/* 1913 */             K k1 = treeNode3.key; int m;
/* 1914 */             if ((m = treeNode3.hash) > i) {
/* 1915 */               j = -1;
/* 1916 */             } else if (m < i) {
/* 1917 */               j = 1;
/* 1918 */             } else if ((clazz == null && (
/* 1919 */               clazz = HashMap.comparableClassFor(k)) == null) || (
/* 1920 */               j = HashMap.compareComparables(clazz, k, k1)) == 0) {
/* 1921 */               j = tieBreakOrder(k, k1);
/*      */             } 
/* 1923 */             TreeNode<K, V> treeNode4 = treeNode3;
/* 1924 */             if ((treeNode3 = (TreeNode<K, V>)((j <= 0) ? treeNode3.left : treeNode3.right)) == null) {
/* 1925 */               treeNode2.parent = treeNode4;
/* 1926 */               if (j <= 0) {
/* 1927 */                 treeNode4.left = treeNode2;
/*      */               } else {
/* 1929 */                 treeNode4.right = treeNode2;
/* 1930 */               }  treeNode1 = balanceInsertion(treeNode1, treeNode2);
/*      */               break;
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/* 1936 */       moveRootToFront(param1ArrayOfNode, treeNode1);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final HashMap.Node<K, V> untreeify(HashMap<K, V> param1HashMap) {
/* 1944 */       HashMap.Node<K, V> node1 = null, node2 = null;
/* 1945 */       for (TreeNode<K, V> treeNode = this; treeNode != null; node3 = treeNode.next) {
/* 1946 */         HashMap.Node<K, V> node3, node4 = param1HashMap.replacementNode(treeNode, (HashMap.Node<K, V>)null);
/* 1947 */         if (node2 == null) {
/* 1948 */           node1 = node4;
/*      */         } else {
/* 1950 */           node2.next = node4;
/* 1951 */         }  node2 = node4;
/*      */       } 
/* 1953 */       return node1;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final TreeNode<K, V> putTreeVal(HashMap<K, V> param1HashMap, HashMap.Node<K, V>[] param1ArrayOfNode, int param1Int, K param1K, V param1V) {
/* 1961 */       Class<?> clazz = null;
/* 1962 */       boolean bool = false;
/* 1963 */       TreeNode<K, V> treeNode1 = (this.parent != null) ? root() : this;
/* 1964 */       TreeNode<K, V> treeNode2 = treeNode1; while (true) {
/*      */         int i, j;
/* 1966 */         if ((j = treeNode2.hash) > param1Int)
/* 1967 */         { i = -1; }
/* 1968 */         else if (j < param1Int)
/* 1969 */         { i = 1; }
/* 1970 */         else { K k; if ((k = treeNode2.key) == param1K || (param1K != null && param1K.equals(k)))
/* 1971 */             return treeNode2; 
/* 1972 */           if ((clazz == null && (
/* 1973 */             clazz = HashMap.comparableClassFor(param1K)) == null) || (
/* 1974 */             i = HashMap.compareComparables(clazz, param1K, k)) == 0) {
/* 1975 */             if (!bool) {
/*      */               
/* 1977 */               bool = true; TreeNode<K, V> treeNode3, treeNode4;
/* 1978 */               if (((treeNode4 = treeNode2.left) != null && (
/* 1979 */                 treeNode3 = treeNode4.find(param1Int, param1K, clazz)) != null) || ((treeNode4 = treeNode2.right) != null && (
/*      */                 
/* 1981 */                 treeNode3 = treeNode4.find(param1Int, param1K, clazz)) != null))
/* 1982 */                 return treeNode3; 
/*      */             } 
/* 1984 */             i = tieBreakOrder(param1K, k);
/*      */           }  }
/*      */         
/* 1987 */         TreeNode<K, V> treeNode = treeNode2;
/* 1988 */         if ((treeNode2 = (TreeNode<K, V>)((i <= 0) ? treeNode2.left : treeNode2.right)) == null) {
/* 1989 */           HashMap.Node<K, V> node = treeNode.next;
/* 1990 */           TreeNode<K, V> treeNode3 = param1HashMap.newTreeNode(param1Int, param1K, param1V, node);
/* 1991 */           if (i <= 0) {
/* 1992 */             treeNode.left = treeNode3;
/*      */           } else {
/* 1994 */             treeNode.right = treeNode3;
/* 1995 */           }  treeNode.next = treeNode3;
/* 1996 */           treeNode3.parent = treeNode3.prev = treeNode;
/* 1997 */           if (node != null)
/* 1998 */             ((TreeNode)node).prev = treeNode3; 
/* 1999 */           moveRootToFront(param1ArrayOfNode, balanceInsertion(treeNode1, treeNode3));
/* 2000 */           return null;
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final void removeTreeNode(HashMap<K, V> param1HashMap, HashMap.Node<K, V>[] param1ArrayOfNode, boolean param1Boolean) {
/*      */       TreeNode<K, V> treeNode9;
/*      */       int i;
/* 2018 */       if (param1ArrayOfNode == null || (i = param1ArrayOfNode.length) == 0)
/*      */         return; 
/* 2020 */       int j = i - 1 & this.hash;
/* 2021 */       TreeNode<K, V> treeNode1 = (TreeNode)param1ArrayOfNode[j], treeNode2 = treeNode1;
/* 2022 */       TreeNode<K, V> treeNode4 = (TreeNode)this.next, treeNode5 = this.prev;
/* 2023 */       if (treeNode5 == null) {
/* 2024 */         param1ArrayOfNode[j] = treeNode1 = treeNode4;
/*      */       } else {
/* 2026 */         treeNode5.next = treeNode4;
/* 2027 */       }  if (treeNode4 != null)
/* 2028 */         treeNode4.prev = treeNode5; 
/* 2029 */       if (treeNode1 == null)
/*      */         return; 
/* 2031 */       if (treeNode2.parent != null)
/* 2032 */         treeNode2 = treeNode2.root();  TreeNode<K, V> treeNode3;
/* 2033 */       if (treeNode2 == null || treeNode2.right == null || (treeNode3 = treeNode2.left) == null || treeNode3.left == null) {
/*      */         
/* 2035 */         param1ArrayOfNode[j] = treeNode1.untreeify(param1HashMap);
/*      */         return;
/*      */       } 
/* 2038 */       TreeNode<K, V> treeNode6 = this, treeNode7 = this.left, treeNode8 = this.right;
/* 2039 */       if (treeNode7 != null && treeNode8 != null) {
/* 2040 */         TreeNode<K, V> treeNode11 = treeNode8; TreeNode<K, V> treeNode12;
/* 2041 */         while ((treeNode12 = treeNode11.left) != null)
/* 2042 */           treeNode11 = treeNode12; 
/* 2043 */         boolean bool = treeNode11.red; treeNode11.red = treeNode6.red; treeNode6.red = bool;
/* 2044 */         TreeNode<K, V> treeNode13 = treeNode11.right;
/* 2045 */         TreeNode<K, V> treeNode14 = treeNode6.parent;
/* 2046 */         if (treeNode11 == treeNode8) {
/* 2047 */           treeNode6.parent = treeNode11;
/* 2048 */           treeNode11.right = treeNode6;
/*      */         } else {
/*      */           
/* 2051 */           TreeNode<K, V> treeNode = treeNode11.parent;
/* 2052 */           if ((treeNode6.parent = treeNode) != null)
/* 2053 */             if (treeNode11 == treeNode.left) {
/* 2054 */               treeNode.left = treeNode6;
/*      */             } else {
/* 2056 */               treeNode.right = treeNode6;
/*      */             }  
/* 2058 */           if ((treeNode11.right = treeNode8) != null)
/* 2059 */             treeNode8.parent = treeNode11; 
/*      */         } 
/* 2061 */         treeNode6.left = null;
/* 2062 */         if ((treeNode6.right = treeNode13) != null)
/* 2063 */           treeNode13.parent = treeNode6; 
/* 2064 */         if ((treeNode11.left = treeNode7) != null)
/* 2065 */           treeNode7.parent = treeNode11; 
/* 2066 */         if ((treeNode11.parent = treeNode14) == null) {
/* 2067 */           treeNode2 = treeNode11;
/* 2068 */         } else if (treeNode6 == treeNode14.left) {
/* 2069 */           treeNode14.left = treeNode11;
/*      */         } else {
/* 2071 */           treeNode14.right = treeNode11;
/* 2072 */         }  if (treeNode13 != null) {
/* 2073 */           treeNode9 = treeNode13;
/*      */         } else {
/* 2075 */           treeNode9 = treeNode6;
/*      */         } 
/* 2077 */       } else if (treeNode7 != null) {
/* 2078 */         treeNode9 = treeNode7;
/* 2079 */       } else if (treeNode8 != null) {
/* 2080 */         treeNode9 = treeNode8;
/*      */       } else {
/* 2082 */         treeNode9 = treeNode6;
/* 2083 */       }  if (treeNode9 != treeNode6) {
/* 2084 */         TreeNode<K, V> treeNode = treeNode9.parent = treeNode6.parent;
/* 2085 */         if (treeNode == null) {
/* 2086 */           treeNode2 = treeNode9;
/* 2087 */         } else if (treeNode6 == treeNode.left) {
/* 2088 */           treeNode.left = treeNode9;
/*      */         } else {
/* 2090 */           treeNode.right = treeNode9;
/* 2091 */         }  treeNode6.left = treeNode6.right = treeNode6.parent = null;
/*      */       } 
/*      */       
/* 2094 */       TreeNode<K, V> treeNode10 = treeNode6.red ? treeNode2 : balanceDeletion(treeNode2, treeNode9);
/*      */       
/* 2096 */       if (treeNode9 == treeNode6) {
/* 2097 */         TreeNode<K, V> treeNode = treeNode6.parent;
/* 2098 */         treeNode6.parent = null;
/* 2099 */         if (treeNode != null)
/* 2100 */           if (treeNode6 == treeNode.left) {
/* 2101 */             treeNode.left = null;
/* 2102 */           } else if (treeNode6 == treeNode.right) {
/* 2103 */             treeNode.right = null;
/*      */           }  
/*      */       } 
/* 2106 */       if (param1Boolean) {
/* 2107 */         moveRootToFront(param1ArrayOfNode, treeNode10);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final void split(HashMap<K, V> param1HashMap, HashMap.Node<K, V>[] param1ArrayOfNode, int param1Int1, int param1Int2) {
/* 2121 */       TreeNode<K, V> treeNode1 = this;
/*      */       
/* 2123 */       TreeNode<K, V> treeNode2 = null, treeNode3 = null;
/* 2124 */       TreeNode<K, V> treeNode4 = null, treeNode5 = null;
/* 2125 */       byte b1 = 0, b2 = 0;
/* 2126 */       for (TreeNode<K, V> treeNode6 = treeNode1; treeNode6 != null; treeNode6 = treeNode) {
/* 2127 */         TreeNode<K, V> treeNode = (TreeNode)treeNode6.next;
/* 2128 */         treeNode6.next = null;
/* 2129 */         if ((treeNode6.hash & param1Int2) == 0) {
/* 2130 */           if ((treeNode6.prev = treeNode3) == null) {
/* 2131 */             treeNode2 = treeNode6;
/*      */           } else {
/* 2133 */             treeNode3.next = treeNode6;
/* 2134 */           }  treeNode3 = treeNode6;
/* 2135 */           b1++;
/*      */         } else {
/*      */           
/* 2138 */           if ((treeNode6.prev = treeNode5) == null) {
/* 2139 */             treeNode4 = treeNode6;
/*      */           } else {
/* 2141 */             treeNode5.next = treeNode6;
/* 2142 */           }  treeNode5 = treeNode6;
/* 2143 */           b2++;
/*      */         } 
/*      */       } 
/*      */       
/* 2147 */       if (treeNode2 != null)
/* 2148 */         if (b1 <= 6) {
/* 2149 */           param1ArrayOfNode[param1Int1] = treeNode2.untreeify(param1HashMap);
/*      */         } else {
/* 2151 */           param1ArrayOfNode[param1Int1] = treeNode2;
/* 2152 */           if (treeNode4 != null) {
/* 2153 */             treeNode2.treeify(param1ArrayOfNode);
/*      */           }
/*      */         }  
/* 2156 */       if (treeNode4 != null) {
/* 2157 */         if (b2 <= 6) {
/* 2158 */           param1ArrayOfNode[param1Int1 + param1Int2] = treeNode4.untreeify(param1HashMap);
/*      */         } else {
/* 2160 */           param1ArrayOfNode[param1Int1 + param1Int2] = treeNode4;
/* 2161 */           if (treeNode2 != null) {
/* 2162 */             treeNode4.treeify(param1ArrayOfNode);
/*      */           }
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     static <K, V> TreeNode<K, V> rotateLeft(TreeNode<K, V> param1TreeNode1, TreeNode<K, V> param1TreeNode2) {
/*      */       TreeNode<K, V> treeNode;
/* 2173 */       if (param1TreeNode2 != null && (treeNode = param1TreeNode2.right) != null) {
/* 2174 */         TreeNode<K, V> treeNode2; if ((treeNode2 = param1TreeNode2.right = treeNode.left) != null)
/* 2175 */           treeNode2.parent = param1TreeNode2;  TreeNode<K, V> treeNode1;
/* 2176 */         if ((treeNode1 = treeNode.parent = param1TreeNode2.parent) == null) {
/* 2177 */           (param1TreeNode1 = treeNode).red = false;
/* 2178 */         } else if (treeNode1.left == param1TreeNode2) {
/* 2179 */           treeNode1.left = treeNode;
/*      */         } else {
/* 2181 */           treeNode1.right = treeNode;
/* 2182 */         }  treeNode.left = param1TreeNode2;
/* 2183 */         param1TreeNode2.parent = treeNode;
/*      */       } 
/* 2185 */       return param1TreeNode1;
/*      */     }
/*      */ 
/*      */     
/*      */     static <K, V> TreeNode<K, V> rotateRight(TreeNode<K, V> param1TreeNode1, TreeNode<K, V> param1TreeNode2) {
/*      */       TreeNode<K, V> treeNode;
/* 2191 */       if (param1TreeNode2 != null && (treeNode = param1TreeNode2.left) != null) {
/* 2192 */         TreeNode<K, V> treeNode2; if ((treeNode2 = param1TreeNode2.left = treeNode.right) != null)
/* 2193 */           treeNode2.parent = param1TreeNode2;  TreeNode<K, V> treeNode1;
/* 2194 */         if ((treeNode1 = treeNode.parent = param1TreeNode2.parent) == null) {
/* 2195 */           (param1TreeNode1 = treeNode).red = false;
/* 2196 */         } else if (treeNode1.right == param1TreeNode2) {
/* 2197 */           treeNode1.right = treeNode;
/*      */         } else {
/* 2199 */           treeNode1.left = treeNode;
/* 2200 */         }  treeNode.right = param1TreeNode2;
/* 2201 */         param1TreeNode2.parent = treeNode;
/*      */       } 
/* 2203 */       return param1TreeNode1;
/*      */     }
/*      */ 
/*      */     
/*      */     static <K, V> TreeNode<K, V> balanceInsertion(TreeNode<K, V> param1TreeNode1, TreeNode<K, V> param1TreeNode2) {
/* 2208 */       param1TreeNode2.red = true; while (true) {
/*      */         TreeNode<K, V> treeNode1;
/* 2210 */         if ((treeNode1 = param1TreeNode2.parent) == null) {
/* 2211 */           param1TreeNode2.red = false;
/* 2212 */           return param1TreeNode2;
/*      */         }  TreeNode<K, V> treeNode2;
/* 2214 */         if (!treeNode1.red || (treeNode2 = treeNode1.parent) == null)
/* 2215 */           return param1TreeNode1;  TreeNode<K, V> treeNode3;
/* 2216 */         if (treeNode1 == (treeNode3 = treeNode2.left)) {
/* 2217 */           TreeNode<K, V> treeNode; if ((treeNode = treeNode2.right) != null && treeNode.red) {
/* 2218 */             treeNode.red = false;
/* 2219 */             treeNode1.red = false;
/* 2220 */             treeNode2.red = true;
/* 2221 */             param1TreeNode2 = treeNode2;
/*      */             continue;
/*      */           } 
/* 2224 */           if (param1TreeNode2 == treeNode1.right) {
/* 2225 */             param1TreeNode1 = rotateLeft(param1TreeNode1, param1TreeNode2 = treeNode1);
/* 2226 */             treeNode2 = ((treeNode1 = param1TreeNode2.parent) == null) ? null : treeNode1.parent;
/*      */           } 
/* 2228 */           if (treeNode1 != null) {
/* 2229 */             treeNode1.red = false;
/* 2230 */             if (treeNode2 != null) {
/* 2231 */               treeNode2.red = true;
/* 2232 */               param1TreeNode1 = rotateRight(param1TreeNode1, treeNode2);
/*      */             } 
/*      */           } 
/*      */           
/*      */           continue;
/*      */         } 
/* 2238 */         if (treeNode3 != null && treeNode3.red) {
/* 2239 */           treeNode3.red = false;
/* 2240 */           treeNode1.red = false;
/* 2241 */           treeNode2.red = true;
/* 2242 */           param1TreeNode2 = treeNode2;
/*      */           continue;
/*      */         } 
/* 2245 */         if (param1TreeNode2 == treeNode1.left) {
/* 2246 */           param1TreeNode1 = rotateRight(param1TreeNode1, param1TreeNode2 = treeNode1);
/* 2247 */           treeNode2 = ((treeNode1 = param1TreeNode2.parent) == null) ? null : treeNode1.parent;
/*      */         } 
/* 2249 */         if (treeNode1 != null) {
/* 2250 */           treeNode1.red = false;
/* 2251 */           if (treeNode2 != null) {
/* 2252 */             treeNode2.red = true;
/* 2253 */             param1TreeNode1 = rotateLeft(param1TreeNode1, treeNode2);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     static <K, V> TreeNode<K, V> balanceDeletion(TreeNode<K, V> param1TreeNode1, TreeNode<K, V> param1TreeNode2) {
/*      */       while (true) {
/* 2264 */         if (param1TreeNode2 == null || param1TreeNode2 == param1TreeNode1)
/* 2265 */           return param1TreeNode1;  TreeNode<K, V> treeNode1;
/* 2266 */         if ((treeNode1 = param1TreeNode2.parent) == null) {
/* 2267 */           param1TreeNode2.red = false;
/* 2268 */           return param1TreeNode2;
/*      */         } 
/* 2270 */         if (param1TreeNode2.red) {
/* 2271 */           param1TreeNode2.red = false;
/* 2272 */           return param1TreeNode1;
/*      */         }  TreeNode<K, V> treeNode2;
/* 2274 */         if ((treeNode2 = treeNode1.left) == param1TreeNode2) {
/* 2275 */           TreeNode<K, V> treeNode5; if ((treeNode5 = treeNode1.right) != null && treeNode5.red) {
/* 2276 */             treeNode5.red = false;
/* 2277 */             treeNode1.red = true;
/* 2278 */             param1TreeNode1 = rotateLeft(param1TreeNode1, treeNode1);
/* 2279 */             treeNode5 = ((treeNode1 = param1TreeNode2.parent) == null) ? null : treeNode1.right;
/*      */           } 
/* 2281 */           if (treeNode5 == null) {
/* 2282 */             param1TreeNode2 = treeNode1; continue;
/*      */           } 
/* 2284 */           TreeNode<K, V> treeNode6 = treeNode5.left, treeNode7 = treeNode5.right;
/* 2285 */           if ((treeNode7 == null || !treeNode7.red) && (treeNode6 == null || !treeNode6.red)) {
/*      */             
/* 2287 */             treeNode5.red = true;
/* 2288 */             param1TreeNode2 = treeNode1;
/*      */             continue;
/*      */           } 
/* 2291 */           if (treeNode7 == null || !treeNode7.red) {
/* 2292 */             if (treeNode6 != null)
/* 2293 */               treeNode6.red = false; 
/* 2294 */             treeNode5.red = true;
/* 2295 */             param1TreeNode1 = rotateRight(param1TreeNode1, treeNode5);
/* 2296 */             treeNode5 = ((treeNode1 = param1TreeNode2.parent) == null) ? null : treeNode1.right;
/*      */           } 
/*      */           
/* 2299 */           if (treeNode5 != null) {
/* 2300 */             treeNode5.red = (treeNode1 == null) ? false : treeNode1.red;
/* 2301 */             if ((treeNode7 = treeNode5.right) != null)
/* 2302 */               treeNode7.red = false; 
/*      */           } 
/* 2304 */           if (treeNode1 != null) {
/* 2305 */             treeNode1.red = false;
/* 2306 */             param1TreeNode1 = rotateLeft(param1TreeNode1, treeNode1);
/*      */           } 
/* 2308 */           param1TreeNode2 = param1TreeNode1;
/*      */           
/*      */           continue;
/*      */         } 
/*      */         
/* 2313 */         if (treeNode2 != null && treeNode2.red) {
/* 2314 */           treeNode2.red = false;
/* 2315 */           treeNode1.red = true;
/* 2316 */           param1TreeNode1 = rotateRight(param1TreeNode1, treeNode1);
/* 2317 */           treeNode2 = ((treeNode1 = param1TreeNode2.parent) == null) ? null : treeNode1.left;
/*      */         } 
/* 2319 */         if (treeNode2 == null) {
/* 2320 */           param1TreeNode2 = treeNode1; continue;
/*      */         } 
/* 2322 */         TreeNode<K, V> treeNode3 = treeNode2.left, treeNode4 = treeNode2.right;
/* 2323 */         if ((treeNode3 == null || !treeNode3.red) && (treeNode4 == null || !treeNode4.red)) {
/*      */           
/* 2325 */           treeNode2.red = true;
/* 2326 */           param1TreeNode2 = treeNode1;
/*      */           continue;
/*      */         } 
/* 2329 */         if (treeNode3 == null || !treeNode3.red) {
/* 2330 */           if (treeNode4 != null)
/* 2331 */             treeNode4.red = false; 
/* 2332 */           treeNode2.red = true;
/* 2333 */           param1TreeNode1 = rotateLeft(param1TreeNode1, treeNode2);
/* 2334 */           treeNode2 = ((treeNode1 = param1TreeNode2.parent) == null) ? null : treeNode1.left;
/*      */         } 
/*      */         
/* 2337 */         if (treeNode2 != null) {
/* 2338 */           treeNode2.red = (treeNode1 == null) ? false : treeNode1.red;
/* 2339 */           if ((treeNode3 = treeNode2.left) != null)
/* 2340 */             treeNode3.red = false; 
/*      */         } 
/* 2342 */         if (treeNode1 != null) {
/* 2343 */           treeNode1.red = false;
/* 2344 */           param1TreeNode1 = rotateRight(param1TreeNode1, treeNode1);
/*      */         } 
/* 2346 */         param1TreeNode2 = param1TreeNode1;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     static <K, V> boolean checkInvariants(TreeNode<K, V> param1TreeNode) {
/* 2357 */       TreeNode<K, V> treeNode1 = param1TreeNode.parent, treeNode2 = param1TreeNode.left, treeNode3 = param1TreeNode.right;
/* 2358 */       TreeNode<K, V> treeNode4 = param1TreeNode.prev; TreeNode treeNode = (TreeNode)param1TreeNode.next;
/* 2359 */       if (treeNode4 != null && treeNode4.next != param1TreeNode)
/* 2360 */         return false; 
/* 2361 */       if (treeNode != null && treeNode.prev != param1TreeNode)
/* 2362 */         return false; 
/* 2363 */       if (treeNode1 != null && param1TreeNode != treeNode1.left && param1TreeNode != treeNode1.right)
/* 2364 */         return false; 
/* 2365 */       if (treeNode2 != null && (treeNode2.parent != param1TreeNode || treeNode2.hash > param1TreeNode.hash))
/* 2366 */         return false; 
/* 2367 */       if (treeNode3 != null && (treeNode3.parent != param1TreeNode || treeNode3.hash < param1TreeNode.hash))
/* 2368 */         return false; 
/* 2369 */       if (param1TreeNode.red && treeNode2 != null && treeNode2.red && treeNode3 != null && treeNode3.red)
/* 2370 */         return false; 
/* 2371 */       if (treeNode2 != null && !checkInvariants(treeNode2))
/* 2372 */         return false; 
/* 2373 */       if (treeNode3 != null && !checkInvariants(treeNode3))
/* 2374 */         return false; 
/* 2375 */       return true;
/*      */     }
/*      */   }
/*      */ }


/* Location:              D:\毕设研究\java\rt.jar!\jav\\util\HashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */