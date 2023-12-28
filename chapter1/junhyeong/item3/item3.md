# private 생성자나 열거 타입으로 싱글톤임을 보증하자 - item3

# 싱글톤을 만드는 3가지 방법

## 1. public static 멤버가 final로 선언된 경우

```java
public class King {

    public static final King instance = new King();

    private King() {}

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        King king = King.instance;
        Constructor<King> constructor = (Constructor<King>) king.getClass().getDeclaredConstructor();
        constructor.setAccessible(true);
        King king1 = constructor.newInstance();
        
        // king == king1

    }
}
```

장점 : public 필드 방식을 쓰면 해당 클래스가 싱글톤임이 API에 명백히 드러난다.

단점 : 리플렉션 API를 쓰면 private 생성자를 호출할 수 있어서 새로운 인스턴스를 만들 수 있다.

```java
public class King {
    
    public static final King instance = new King();

    private King() {
        if (instance != null) {
            throw new AssertionError();
        }
    }
}
```

대안으로 생성자에 예외처리를 해둔다.

## 2. 정적 팩토리 메서드를 public static 멤버로 제공

```java
public class Master {

    private static Master master;
    private Master() {
    }

    public static synchronized Master getInstance() {
        if (master == null) {
            master = new Master();
        }
        return master;
    }
}
```

장점 

- API를 바꾸지 않고도 싱글톤이 아니게 바꿀 수 있다.
- 제네릭 싱글톤 팩토리로 만들 수 있다.
- 정적 팩토리의 메서드 참조를 공급자로 사용할 수 있다.

이런 장점들이 필요하지 않다면 public 필드 방식을 쓰자.

## 3. 원소가 하나인 열거 타입 선언

1번과 2번으로는 역직렬화 할 때마다 새로운 객체가 생성되어서 싱글톤이 깨진다.

이를 방지하려면 모든 필드를 transient로 선언하고 readResolve 메서드를 추가하는 등의 귀찮은 작업이 필요하다.

```java
public enum King {
    INSTANCE;

    public void method() {
        System.out.println("kong");
    }

    public static void main(String[] args) {
        King king = King.INSTANCE;
        king.method();
    }
}
```

ENUM 타입으로 만들어서 해결한다.

태생적으로 싱글톤이 보장되기 때문( == 비교가 가능한 이유가 싱글톤이라서)

근데 스프링을 쓰면서 빈이 아닌 싱글톤을 만들일이 있을까?