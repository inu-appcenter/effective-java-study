# 인터페이스는 구현하는 쪽을 생각해 설계하라 - item21

### 모든 상황에 맞는 디폴트 메서드를 작성하기는 어렵다

```java
public interface Collection<E> extends Iterable<E> {
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
}
```

범용적으로 구현된 것 같지만, 아니다.

 SynchronizedCollection 클래스는 모든 메서드를 동기화하여 호출해주는 역할을 하는데, removeIf()를 재정의하고 있지 않다. 이 상황에서 removeIf()를 호출하면, 해당 구현체가 모든 메서드에서 동기화 해주던 역할이 보장되지 못 한다.

- 자바 플랫폼 라이브러리에서는 이런 문제를 예방하기 위해 다음과 같은 조치를 취했다.
    - 구현한 인터페이스의 디폴트 메서드를 재정의하고, 다른 메서드에서는 디폴트 메서드를 호출하기 전에 필요한 작업을 수행하도록 했다.
    
    → SynchronizedCollection이 반환하는 package-private 클래스들은 removeIf를 재정의하고, 다른 메서드에서는 디폴트 메서드를 호출하기 전에 필요한 작업을 수행
    

### 디폴트 메서드는 기존 구현체에 런타임 오류를 일으킬 수 있다.

심사숙고해서 추가하고 가능한 피하자.

디폴트 메서드는 인터페이스로부터 메서드를 제거하거나 수정하는 용도가 아니다.

인터페이스 생성 시, 표준적인 메서드 구현을 제공하는 데 유용한 수단으로 쓰여야한다.