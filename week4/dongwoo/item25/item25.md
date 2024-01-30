## 아이템 25 톱 레벨 클래스는 한 파일에 하나만 담아라

```java
class Utensil {
    static final String NAME = "pan";
}

class Dessert {
    static final String NAME = "cake";
}
```

```java
class Utensil {
    static final String NAME = "pot";
}

class Dessert {
    static final String NAME = "pie";
}
```

```java
public class Main {

    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }
}
```

이렇게 했다고 해보자 그러면 클래스를 중복 정의했다고 알려줄 것이다 컴파일러는 가장 먼저 Main을 컴파일 하고 그 안에서 Utensil을 먼저 만나고 그 안에 Utensil과 Dessert를 모두 본다 그 다음 두번째 인자인 Dessert를 실행할때 클래스의 정의가 이미 있음을 알게된다

javac [Main.java](http://Main.java) Utensil.java명령으로 컴파일 하면 pencake 출력

javac [Dessert.java](http://Dessert.java) [Main.java](http://Main.java) 명령으로 컴파일 하면 potpie

해결책은 분리하면 된다 톱레벨 클래스는 한 파일에 쓰지말자