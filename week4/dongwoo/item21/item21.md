## 아이템 21 인터페이스는 구현하는 쪽을 생각해 설계하라

구현체를 개뜨리지 않고 인터페이스에 메서드를 추가할 방법이 없었다 그래서 자바 8 이후에 디폴트 메서드를 추가했지만 위험이 완전히 사라진건 아니다 왜냐면 디폴트 메서드를 선언하면 그 인터페이스를 구현한 후 디폴트 메서드를 재정의하지 않는 모드 클래스에서 디폴트 구현이 쓰이게 된다 하지만 자바 8에서 핵심 컬렉션 인텊에ㅣ스에 다수의 디폴트 메서드가 추가되었다고 한다 람다를 활용하기 위해서 하지만 생각할 수 잇는 모든 상황에서 불변식을 해치지 않는 디폴트 메서드를 작성하기란 어렵다

자바 8 Collection 인터페이스에 추가된 디폴트 메서드

```java
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
```

잘 구현했지만 모든 Collection구현체와 잘 어우러지는건 아니다

멀티쓰레드에서 removeIf에 Lock이 없어서 에러가 난다 그래서 자바 플랫폼 라이브러리는 해결책으로 구현한 인터페이스의 디폴트 메서드를 재정의하고 다른 메서드에서는 디폴트 메서드를 호출하기 전에 필요한 작업을 수행하기로 했다

```java
@Override
default boolean removeIf(Predicate<? super E> filter) {
		synchronized(lock) {
				...
		}
}
```

이렇게 하면 된다

결론 → 디폴트 메서드는 런타임 오류를 일으킬 수 있다 그니까 인터페이스를 설계할때 세심한 주의를 기울여야 한다 그리고 새로운 인터페이스는 배포전에 반드시 테스트를 쳐라