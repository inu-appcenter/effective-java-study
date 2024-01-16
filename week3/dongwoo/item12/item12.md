## 아이템 12 toString을 항상 재정의하라

Object에다가 .toString메서드 하면 위에 김동우 폰넘버로 생각해보면 PhonNumber@adbbd처럼 클래스 이름을 16진수로 표기한 해시코드를 String으로 반환할 뿐이다 하지만 우리가 알고 싶은거는 01063333099 와 같이 실제 핸드폰 번호를 알고 싶긴하다

toString은 그 객체가 가진 주요 정보를 모두 반환하는게 좋다 내가 필요한 부분만 한다 하기엔 협업을 해야 하니까…

```java
class Person {

    String name;
    int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

		@Override
    public String toString() {
        return String.format("이름 : %s, 나이 : %d세", this.name, this.age);
    }
}

public class Main {
    public static void main(String[] args) {
        Person p1 = new Person("김동우", 26);
        System.out.println(p1);
    }
}
```
