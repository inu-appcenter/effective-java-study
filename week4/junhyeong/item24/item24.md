# 멤버 클래스는 되도록 static으로 만들라 - item24

**중첩 클래스**

- 다른 클래스 안에 정의된 클래스
- 정적 멤버 클래스, 멤버 클래스, 익명 클래스, 지역 클래스

**내부 클래스**

- ~~정적 멤버 클래스~~, 멤버 클래스, 익명 클래스, 지역 클래스

각 중첩 클래스를 언제 그리고 왜 사용하는지 알아본다.

### 정적 멤버 클래스

- 바깥 클래스의 private 멤버에 접근하는 점만 제외하고는 일반 클래스와 같음
- 바깥 클래스와 함께 쓰일 때만 유용한 public 도우미 클래스로 쓰인다.

e.g. Calculator.Operation.PLUS… 같은 형태로 원하는 연산 참조

### 비정적 멤버 클래스

- 바깥 클래스와 외부 참조(암묵적으로 연결)를 가진다.

→ 관계 정보는 비정적 멤버 클래스의 인스턴스 안에 만들어져 메모리 공간을 차지하며, 생성 시간도 더 걸린다.

- 어댑터를 정의할 때 자주 쓰인다.

→ 어떤 클래스의 인스턴스를 감싸 마치 다른 클래스의 인스턴스처럼 보이게 하는 뷰로 사용

```java
public class MySet<E> extends AbstractSet<E> {
    ... // 생략

    @Override public Iterator<E> iterator() {
    return new MyIterator();
    }

    private class MyIterator implements Iterator<E> {
    ...
    }
}
```

- 멤버 클래스에서 바깥 참조가 필요한게 아니라면 static을 붙여서 메모리 누수를 예방하자.

( 가비지 컬렉션이 바깥 클래스의 인스턴스를 수거하지 못할 수 있는데, 참조가 눈에 보이지 않으니 문제를 찾기 어려움 )

### 익명 클래스

- 멤버와 달리, 쓰이는 시점에 선언과 동시에 인스턴스가 만들어진다.
- 비정적인 문맥에서 사용될 때만 바깥 클래스의 인스턴스를 참조할 수 있다.
- 정적 문맥에서라도 상수 변수 이외의 정적 멤버는 가질 수없다.

→ 상수 표현을 위해 초기화 된 final 기본 타입과 문자열 필드만 가질 수 있다.

- 정적 팩토리 메서드를 구현할 때 쓰인다.

```java
static List<Integer> intArrayAsList<int[] a) {
    Objects.requireNonNulkl(a);

    return new AbstractList<>() {
    @Override public Integer get(int i) {
        return a[i];
    }

    @Override public Integer set(int i, Integer val) {
        int oldVal = a[i];
        a[i] = val;
        return oldVal;
    }

    @Override public int size() {
        return a.length;
    }
}
```

### 지역 클래스

- 블록 안에 선언된 클래스
- 지역 변수를 선언할 수 있는 곳이면 실질적으로 어디서든 선언할 수 있다.
- 익명 클래스처럼 비정적 문맥에서 사용될 때만 바깥 인스턴스를 참조할 수 있으며, 정적 멤버는 가질 수 없으며, 가독성을 위해 짧게 작성해야 한다.

### 정리

1. 메서드 밖에서 사용하거나 안에 메서드 안에 정의하기엔 너무 길면 멤버 클래스로 만든다
2. 멤버 클래스의 인스턴스가 바깥 인스턴스를 참조하지 않으면 정적으로 만들자.
3. 중첩 클래스가 한 메서드 안에서만 쓰이면서 생성하는 지점이 단 한 곳이고, 해당 타입으로 쓰기에 적합한 클래스나 인터페이스가 있다면 익명 클래스로 만들고 그렇지 않다면 지역 클래스로 만들자.