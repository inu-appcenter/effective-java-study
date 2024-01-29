# 상속보다는 컴포지션을 사용하라 - item18

여기서 상속은 클래스가 다른 클래스를 확장하는 구현 상속을 말한다.

### **하위 클래스가 깨지기 쉬운 이유**

```java
public class InstrumentedHashSet<E> extends HashSet {
    private int addCount = 0;

    public InstrumentedHashSet(){}

    public InstrumentedHashSet(int initCap, float loadFactor){
        super(initCap,loadFactor);
    }

    @Override
    public boolean add(Object o) {
        addCount++;
        return super.add(o);
    }

    @Override
    public boolean addAll(Collection c) {
        addCount+=c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }

}

InstrumentedHashSet<String> s= new InstrumentedHashSet<>();
s.addAll(List.of("a", "b", "c"));
s.getAddCount(); // 6 ???
```

HashSet을 사용하는 프로그램. 성능을 높이려고 생성된 이후 원소가 몇 개 더해졌는지 알 수 있는 addCount 필드가 있다.

addCount를 출력하면 3이 아닌 6을 반환한다.

HashSet의 addAll 메서드는 add메서드를 사용해 구현했기 때문.

즉, InstrumentedHashSet 의 addAll로 3을 더했고 HashSet의 addAll은 각 원소를 add 메서드를 호출해 추가하는데, 이 때 add는 InstrumentedHashSet에서 addCount를 올리도록 재정의 한 메서드라서 중복으로 더해진거다.

```java
public boolean addAll(Collection<? extends E> c) {
    boolean modified = false;
    for (E e : c)
        if (add(e))
            modified = true;
    return modified;
}
```

이처럼 해당 클래스의 내부 구현 방식을 모른다면 어떻게 동작할지 알 수없는 경우가 있다.

### 컴포지션(구성)

확장하는 대신에 새로운 클래스를 만들고 기존 클래스의 인스턴스를 참조하게 하는 것

⇒ 기존 클래스의 내부 구현 방식의 영향에서 벗어나며 새로운 메서드를 추가해도 기존 클래스는 영향을 안받음

```java
public class InstrumentedSet<E> extends ForwardingSet<E> {

 private int addCount = 0;

 public InstrumentedSet(Set<E> s) {
	 super(s);
 }

 @Override public boolean add(E e) {
	 addCount++;
	 return super.add(e);
 }

 @Override public boolean addAll(Collection<? extends E> c) {
	 addCount += c.size();
	 return super.addAll(c);
 }

 public int getAddCount() {
	 return addCount;
 }
}
```

```java
public class ForwardingSet<E> implements Set<E> {

 private final Set<E> s;

 public ForwardingSet(Set<E> s) { this.s = s; }

 public void clear() { s.clear(); }
 public boolean contains(Object o) { return s.contains(o); }
 public boolean isEmpty() { return s.isEmpty(); }
 public int size() { return s.size(); }
 public Iterator<E> iterator() { return s.iterator(); }
 public boolean add(E e) { return s.add(e); }
 public boolean remove(Object o) { return s.remove(o); }
 public boolean containsAll(Collection<?> c)
	 { return s.containsAll(c); }
 public boolean addAll(Collection<? extends E> c)
	 { return s.addAll(c); }
 public boolean removeAll(Collection<?> c)
	 { return s.removeAll(c); }
 public boolean retainAll(Collection<?> c)
	 { return s.retainAll(c); }
 public Object[] toArray() { return s.toArray(); }
 public <T> T[] toArray(T[] a) { return s.toArray(a); }

 @Override public boolean equals(Object o)
 { return s.equals(o); }
 @Override public int hashCode() { return s.hashCode(); }
 @Override public String toString() { return s.toString(); }
}
```

Set의 addAll을 사용하게 돼서, add메서드를 사용해도 Set의 add 메서드를 사용하게 된다.

→ 중복으로 addCount에 더해지지 않는다. 

ForwardingSet의 메서드는 HashSet에 대응하는 메서드를 호출해 그 결과를 반환한다. 

⇒ 전달

위 메서드를 전달 메서드, 전달 메서드로만 이루어진 재사용 가능한 클래스는 전달 클래스

기존 클래스의 대응하는 메서드를 호출 하여 그 결과를 반환(전달, forwarding)하기 때문에, 새로운 클래스는 기존 클래스의 내부 구현 방식의 영향에서 벗어나기 때문에 기존 클래스에 새로운 메서드가 추가되더라도 영향 받지 않는다

InstrumentedSet 같이 다른 인스턴스(Set)를 감싸고 있다는 뜻에서 **래퍼 클래스**라 하며, 다른 Set에 계측 기능을 덧씌운다는 뜻에서 데코레이터 패턴이라고 한다.

### 정리

상속은 강력하지만 캡슐화를 해친다는 문제가 있다. 반드시 하위 클래스가 상위 클래스의 진짜 하위 타입인 상황에서만 사용하자.

is - a 관계일 때만

e.g. 클래스 A를 상속하는 클래스 B를 작성하려 한다면 B가 정말 A인지 자문하자.

확장하려는 클래스의 API에 아무런 결함이 없는지, 있다면 해당 클래스의 API 까지 전파돼도 괜찮은지. 자문하자.

컴포지션으로는 결함을 숨기는 새로운 API를 작성할 수 있지만, 상속은 그 결함까지 그대로 승계하기 때문이다.