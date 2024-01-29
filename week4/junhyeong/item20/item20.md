# 추상 클래스보다는 인터페이스를 우선하라 - item20

### 가장 큰 차이점

추상클래스 : 단일 상속만 가능. 구현체는 추상클래스의 하위 클래스.

인터페이스 : 다중 상속 가능. 구현체는 같은 타입으로 취급.

### 인터페이스의 장점 (1) : 믹스인

믹스인 : 대상 타입의 주된 기능에 선택적 기능을 ‘혼합’ 하는 것

- 구현 클래스에 주된 타입 외에 선택적 행위를 제공한다고 선언하는 효과를 준다.

( 추상 클래스는 다중 상속을 지원 안하니 믹스인으로 사용할 수 없다. )

### 인터페이스의 장점 (2) : 계층구조가 없는 타입 프레임워크

```java
public interface Singer {
   AuduioClip Sing(Song s);
}

public interface Songwriter {
   Song compose(int chartPosition);
}

public interface SingerSongWriter extends Singer, Songwriter {
   AudioClip strum();
	 void actSensitive();
}

```

현실에서 계층을 엄격히 구분하기 힘든 개념도 유연하게 정의할 수 있다.

### 인터페이스의 장점 (3) : Wrapper 클래스 (item18)

```java
public class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;
    public ForwardingSet(Set<E> s) { this.s= s;}
    
    public void clear() {s.clear();}
    public boolean contains(Object o) { return s.contains(o);}
    public boolean isEmpty() { return s.isEmpty();}
    public int size() { return s.size();}
    public Iterator<E> iterator() { return s.iterator(); }
    public boolean add(E e) { return s.add(e); }
    public boolean addAll(Collection<? extends E> c) { return s.addAll(c); }
    
    ...

}
```

상속보다는 컴포지션을 이용할 때, 래퍼 클래스를 만들어서 사용한다.

래퍼 클래스 : 인터페이스를 구현한 클래스를 주입받아 기존 구현체에 부가기능을 손쉽게 더할 수 있는 것

### 인터페이스의 장점 (4) : default 메서드

```java
* **@implSpec**
     * The default implementation traverses all elements of the collection using
     * its {@link #iterator}.  Each matching element is removed using
     * {@link Iterator#remove()}.  If the collection's iterator does not
     * support removal then an {@code UnsupportedOperationException} will be
     * thrown on the first matching element.
     *
     * @param filter a predicate which returns {@code true} for elements to be
     *        removed
     * @return {@code true} if any elements were removed
     * @throws NullPointerException if the specified filter is null
     * @throws UnsupportedOperationException if elements cannot be removed
     *         from this collection.  Implementations may throw this exception if a
     *         matching element cannot be removed or if, in general, removal is not
     *         supported.
     * @since 1.8
     */
    default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> each = iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }

// Collection.removeIf()
```

구현 방법이 명백한 것이 있다면 디폴트 메서드로 제공해 일감을 덜어줄 수 있다.

@ImplSpec 를 이용해 상속하려는 사람을 위한 설명을 문서화해두자.

### 템플릿 메서드 패턴(추상 골격 구현 클래스)

인터페이스와 함께 제공해서 추상 클래스의 장점을 모두 취하는 방법

- List 구현체를 반환하는 정적 팩터리 메서드
- 명명 관례 : e.g. AInterface → AbstractAInterface

```java
// 추상 골격 구현 클래스
public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {

	protected AbstractList(){
    
	}

	public boolean add(E e) {
        add(size(), e);
        return true;
  }

	public abstract E get(int index);

	public E set(int index, E element) {
        throw new UnsupportedOperationException();
   
	}
}
```

List 인터페이스를 대부분 구현하고 구현하지 않은 메서드는 추상 메서드로 남겨둔다.

set 메서드는 예외를 던지도록 해뒀는데, set이 필요없는 상황에서,  클라이언트가 구현해야하는 수고를 줄여준것으로 보인다.

```java
// 골격 구현의 구체 클래스
static List<Integer> intArrayAsList(int[] a) {
  Objects.requireNonNull(a);

  return new AbstractList<>() {
    @Override
    public Integer get(int i) {
      return a[i];
    }

    @Override
    public Integer set(int i, Integer val) {
      int oldVal = a[i];
      a[i] = val;
      return oldVal;
    }

    @Override
    public int size() {
      return a.length;
    }
  }
}
```

사용자는 원하는 메서드만 오버라이딩해서 구현할 수 있다.

(추상 클래스의 추상 메서드만 구현이 강제가 되기 때문)