## 아이템20 추상 클래스보다는 인터페이스를 우선하라

추상 클래스: 클래스 내 추상메서드가 하나 이상 포함되거나 abstract로 정의된 경우
인터페이스: 모든 메서드가 추상 메서드인 경우

결국엔 둘다 구현을 하는거 아닌가?

추상 클래스는 추상 클래스를 상속받아서 기능을 이용하고 확장시키는게 목적이라면 인터페이스는 함수의 껍데기만 있다 왜? 함수의 구현을 강제하려고

인터페이스는 mixin(주된 타입 외에 선택적 행위를 제공)정의에 안성맞춤이다

```java
public class T implements Comparable<T>, Serializable {
    
    @Override
    public int compareTo(T o) {
				...
    }
}
```

이렇게 대상 타입의 주된 기능에 선택적 기능을 혼합(mixed in) 한다고 해서 믹스인이라고 부른다 하지만 추상 클래스는 믹스인을 정의할 수 없다 기존 클래슬에 덧씌울 수 없기 때문이다 클래스는 두 부모를 섬길 수 없으니께

또한 인터페이스로는 계층구조가 없는 타입 프레임워크를 만들 수 있다

```java
public interface Singer {
		AutoClip sing(song s);
}

public interface SongWriter {
		Song compose(int chartPosition);
}

public interface SingerSongWriter extends Singer, Songwriter {
		AucoClip strum();
		void actSensitive();
}
```

이렇게 Singer와 SongWriter를 모두 구현해도 되고, 모두 확장하고 새로운 메서드까지 추가한 제 3의 인터페이스를 정의할 수 있다

이걸 추상 클래스로 해보면

```java
public abstract Singer {
		AutoClip sing(song s);
}

public abstract SongWriter {
		Song compose(int chartPosition);
}

public abstract SingerSongWriter extends Singer, Songwriter {
		AutoClip sing(song s);
		Song compose(int chartPosition);
		AucoClip strum();
		void actSensitive();
}
```

고도 비만 계층구조 조합 폭발

인터페이스는 기능을 향상 시키는 안전하고 강력한 수단이 된다 타입을 추상 클래스로 정의해두면 그 타입에 기능을 추가하는건 상속 뿐이다, 상속해서 만든 클래스는 래퍼 클래스보다 활용도가 떨어지고 꺠지기는 더 쉽다

또한 인터페이스의 메서드 중 구현 방법이 명백한 것이 있다면, 그 구현을 디폴트 메서드로 제공해 프로그래머들의 일감을 덜어줄 수 있다.

```java
public interface Iterator<E> {
    default void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
```

사람들이 잘 사용하지 않기 때문에 사용하지 않는 메서드를 디폴트 메서드로 선언하여 구현해서 사용하는 클래스는 굳이 구현을 할 필요가 없어져서 불필요한 코드를 줄일 수 있다 하지만 제약조건이 있다

1. 자바 독 태그로 문서화 해야함
2. equals와 hashCode를 디폴트 메서드로 제공 x
3. 인터페이스는 인스턴스 필드를 가질 수 없고 pulbic이 아닌 정적 메서드를 가질 수 없음
4. 본인이 만든 인터페이스가 아니면 디폴트 메서드를 추가 불가능

인터페이스는 추상 골격 구현클래스를 함께 제공하는 식으로 인터페이스와 추상 클래스의 장점을 모두 취하는 방법이 있음

```java
public interface Jungle {
    void 강타();
    void Q();
    void W();
    void E();
    void R();
    void 풀콤();
}

public class Leesin implements Jungle {
    @Override
    public void 강타() {
        System.out.println("강타 사용");
    }
    @Override
    public void Q() {
        System.out.println("음파 사용");

    }
    @Override
    public void W() {
        System.out.println("방호 사용");

    }
    @Override
    public void E() {
        System.out.println("폭풍 사용");

    }
    @Override
    public void R() {
        System.out.println("용의 분노 사용");
    }
    @Override
    public void 풀콤() {
        R();
        Q();
        E();
        강타();
        W();
    }
}

public class Kazics implements Jungle {
    ...
}
```

카직스도 중복된 코드를 다 overriding해야한다 하지만 추상 골격 클래스를 함께 이용하면

```java
public interface Jungle {
    void 강타();
    void Q();
    void W();
    void E();
    void R();
    void 풀콤();
}

public abstract class SmiteJungle implements Jungle {

    @Override
    public void 강타() {
        System.out.println("강타 사용");
    }

    @Override
    public void 풀콤() {
        R();
        Q();
        E();
        강타();
        W();
    }
}

public class Leesin extends SmiteJungle implements Jungle {
    @Override
    public void Q() {
        System.out.println("음파 사용");

    }
    @Override
    public void W() {
        System.out.println("방호 사용");

    }
    @Override
    public void E() {
        System.out.println("폭풍 사용");

    }
    @Override
    public void R() {
        System.out.println("용의 분노 사용");
    }
}

public class Kazics extends SmiteJungle implements Jungle {
    ...
}
```

디폴트 메서드 없이 중복되는 강타, 풀콤을 중복제거할 수 있다

결론 → 일반적으로 다중 구현용 타입으로는 인터페이스가 가장 적합, 복잡한 인터페이스라면 구현의수고를 덜어주는 골격 구현을 함께 제공하는 방법을 고려햐보자