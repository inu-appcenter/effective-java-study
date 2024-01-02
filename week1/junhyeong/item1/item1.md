# 정적 팩토리 메서드를 고려하자 - item1

# 장점 1 : 이름을 가질 수 있다.

```java

    // 네이밍 : 매개변수 1개(from) 2개(of)
    public static User from(String name) {
        return new User(name,"MAN", "USER");
    }

    public static User of(String name, String gender) {
        return new User(name,gender, "USER");
    }

    public static User managerOf(String name, String gender) {
        return new User(name,gender, "MANAGER");
    }

    public static User admin() {
        return new User("관리자","남성", "ADMIN");
    }

}
```

평소에는 유저를 만들때는 userOf만 호출하고

테스트할때는 userManFrom 호출하고

특수한 유저를 만들때는 그에 맞는걸 호출하는 등 목적에 맞게 객체명을 지을 수 있다.

# 장점 2 : 호출할 때마다 인스턴스를 새로 생성 x

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

사이트 내에 운영자는 꼭 하나만 필요하다. 싱글톤패턴으로 하나의 인스턴스를 보장할 수 있는데,

싱글톤패턴을 구현하는 방식으로 정적 팩토리 메서드가 사용된다.

- synchronized : 동시성 보장
- getInstance() : 이전에 반환했던거와 같을 수 있다.

# 장점 3 : 하위 자료형 객체 반환

```java
interface SmartPhone {}

class Galaxy implements SmartPhone {}
class Iphone implements SmartPhone {}
class Huawei implements SmartPhone {}

class SmartPhones {
    public static SmartPhone getSamsungPhone() {
        return new Galaxy();
    }

    public static SmartPhone getApplePhone() {
        return new Iphone();
    }

    public static SmartPhone getChinesePhone() {
        return new Huawei();
    }
}
```

[https://inpa.tistory.com/entry/GOF-💠-정적-팩토리-메서드-생성자-대신-사용하자](https://inpa.tistory.com/entry/GOF-%F0%9F%92%A0-%EC%A0%95%EC%A0%81-%ED%8C%A9%ED%86%A0%EB%A6%AC-%EB%A9%94%EC%84%9C%EB%93%9C-%EC%83%9D%EC%84%B1%EC%9E%90-%EB%8C%80%EC%8B%A0-%EC%82%AC%EC%9A%A9%ED%95%98%EC%9E%90)

자바 8 전에는 인터페이스에 정적 메서드를 선언할 수 없어서 이런식으로 썼었다.

Collections 클래스와 Collection 인터페이스가 이 경우에 해당한다.

- Collections 클래스(Collection 인터페이스의 동반 클래스)
- 

```java
interface SmartPhone {
    static SmartPhone getSamsungPhone() {
        return new Galaxy();
    }

    static SmartPhone getApplePhone() {
        return new Iphone();
    }

    static SmartPhone getChinesePhone() {
        return new Huawei();
    }
}

class Galaxy implements SmartPhone {}
class Iphone implements SmartPhone {}
class Huawei implements SmartPhone {}
```

# 장점 4 : 매개변수에 따라 다른 객체 반환

```java
class G104 implements Mouse{}
class G204 implements Mouse{}
class G304 implements Mouse{}
public interface Mouse {
    public static Mouse getMouse(int price) {
        if (price > 10000) {
            return new G104();
        }

        if (price > 20000) {
            return new G204();
        }

        return new G304();
    }
}
```

3번을 확장하여 분기문을 통해서 여러 자식 타입의 인스턴스를 반환할 수 있다.