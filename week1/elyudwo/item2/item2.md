
# 생성자에 매개변수가 많다면 빌더를 고려하라

## 서론
지금까지 롬복 라이브러리에서 제공하는 Builder 어노테이션을 이용해 편하게
빌더 패턴을 적용해왔다. <br></br>

하지만 API를 단순히 사용할 줄만 알고있지 어떤 방식으로 동작하는지 이해하지
못했던 부분들을 이번 학습을 통해 이해하게되었다.


- - - 

## 점층적 생성자 패턴

```java
public class MemberByConstructor {

    private final String name; // 필수
    private final String phoneNumber; // 필수
    private final int age; // 선택
    private final int weight; // 선택
    private final int tall; // 선택


    public MemberByConstructor(String name, String phoneNumber) {
        this(name, phoneNumber, 0);
    }

    public MemberByConstructor(String name, String phoneNumber, int age) {
        this(name, phoneNumber, age, 0);
    }

    public MemberByConstructor(String name, String phoneNumber, int age, int weight) {
        this(name, phoneNumber, age, weight, 0);
    }

    public MemberByConstructor(String name, String phoneNumber, int age, int weight, int tall) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.weight = weight;
        this.tall = tall;
    }
}
```

이 방식은 어떤 생성자를 호출하던 최종적으로 매개변수를 모두 받는 생성자를 이용해 반환되기 때문에
클라이언트 코드를 작성하거나 읽기 어렵다는 단점이 있다.

이러한 단점의 해결법으로 아래와 같은 **자바 빈즈 패턴**을 사용할 수 있다.


## 자바 빈즈 패턴

```java
public class MemberByJavaBeans {

    private String name; // 필수
    private String phoneNumber; // 필수
    private int age = 0; // 선택
    private int weight = 0; // 선택
    private int tall = 0; // 선택

    public MemberByJavaBeans() {}

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setTall(int tall) {
        this.tall = tall;
    }

}
```

위의 방식과 달리 기본 생성자로 객체를 생성한 뒤 필요한 매개변수를 setter 메소드를 이용해 초기화 해 줄 수 있기 때문에
더 읽기 쉬운 코드가 되었다.

하지만 필수적으로 초기화 해주어야하는 필드가 10개 이상이라면 어떻게될까??

객체를 생성하기 위해 setter 메소드 10번을 호출해 줘야 하는 불편함이 생긴다. 또한, 객체가 완전히 생성되기 전까지는 **일관성**이 무너진 상태에 놓이게된다.

~~~
일관성이란??

class C {
  final int a;
  final int b;

  C(int a, int b) {
    this.a = a;
    this.b = b;
  }
}

class C {
  final int a;
  final int b;

  public void setA() { this.a = a }
  public void setB() { this.b = b }
}

첫번째 클래스는 객체를 생성하면서 a와 b의 상태가 모두 초기화 된다. 

하지만 두번째 클래스의 경우 setA() 만 호출하고 setB()를 호출하지 않았을 때 
일시적으로 a변수에는 올바른 값이 있지만 b 변수에는 올바른 값이 없는 상태가 된다.
이러한 상황을 일관성이 무너진 상태라 칭한다.
~~~

위의 두가지 문제점은 해결하고 장점은 살리기위해 만들어진 방식이 빌더패턴이다.


## 빌더 패턴

```java
public class MemberByBuilder {

    private final String name; // 필수
    private final String phoneNumber; // 필수
    private final int age; // 선택
    private final int weight; // 선택
    private final int tall; // 선택

    public static class Builder {
        private final String name;
        private final String phoneNumber;

        private int age;
        private int weight;
        private int tall;

        public Builder(String name, String phoneNumber) {
            this.name = name;
            this.phoneNumber = phoneNumber;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public Builder tall(int tall) {
            this.tall = tall;
            return this;
        }

        public MemberByBuilder build() {
            return new MemberByBuilder(this);
        }
    }
    
    private MemberByBuilder(Builder builder) {
        this.name = builder.name;
        this.phoneNumber = builder.phoneNumber;
        this.age = builder.age;
        this.weight = builder.weight;
        this.tall = builder.tall;
    }
}
```

``` java
@Test
void Builder() {
    /**
     * 정적 내부 클래스의 인스턴스는 외부 클래스를 먼저 생성하지 않아도 생성 가능하다. (Java의 정석 408p)
     * 메소드 체이닝을 이용해 . 으로 연쇄 호출
     */
    MemberByBuilder member = new MemberByBuilder.Builder("이영재", "010")
            .age(25)
            .tall(175)
            .weight(75)
            .build();

    Assertions.assertThat(member.getName()).isEqualTo("이영재");
}
```

빌더패턴은 setter 메서드를 이용하는 자바 빈즈 패턴과 달리 메소드 체이닝을 이용해
.으로 연쇄 호출하기 때문에 가독성이 좋다.

또한 build 메소드가 호출되면서 객체가 반환되기 때문에 객체의 일관성 유지 또한 가능하다.


## 정리

생성자나 정적 팩터리가 처리해야 할 매개변수가 많다면 빌더 패턴을 선택하는게 더 낫다.
매개변수 중 다수가 필수가 아니거나 같은 타입이면 특히 더 그렇다.
빌더는 점층적 생성자보다 클라이언트 코드를 읽고 쓰기가 훨씬 간결하고, 자바 빈즈보다
훨씬 안전하다.


---
## 여담
API를 개발할 때는 보통 Lombok 라이브러리에 있는 @Builder를 사용해 빌더를 구현하는 부담을 줄일 수 있다.

근데 이 Builder를 사용할 때 @NoArgsConstructor와 함께 클래스 레벨에 선언하게 되면 컴파일 에러가 발생한다.

왜냐하면 클래스 레벨에 선언된 @Builder는 생성자가 있다면 기존에 있는 생성자를 활용하며 추가적인 생성자를 선언하지 않기 때문이다.

이렇게 되면 빌더 클래스 내부에서 외부 클래스의 인스턴스를 생성할 때 모든 멤버 변수를 초기화 하는 생성자가 필요한데 매개변수가 없는
기본 생성자만 존재하기 때문에 에러가 발생하는 것 이다.

이를 해결하기 위해 @AllArgsConstructor를 붙일 수 있지만 굳이 이렇게 하지 않고 메소드 레벨에 @Builder를 붙이는 것이 바람직한 것으로 생각된다.
