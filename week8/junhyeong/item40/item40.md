# @Override 애너테이션 일관되게 사용하라 - item40

```java
public class Bigram {
    private final char first;
    private final char second;

    public Bigram(char first, char second) {
        this.first  = first;
        this.second = second;
    }

    public boolean equals(Bigram b) {
        return b.first == first && b.second == second;
    }

    public int hashCode() {
        return 31 * first + second;
    }

    public static void main(String[] args) {
        Set<Bigram> s = new HashSet<>();
        for (int i = 0; i < 10; i++)
            for (char ch = 'a'; ch <= 'z'; ch++)
                s.add(new Bigram(ch, ch));
        System.out.println(s.size());
    }
}
```

똑같은 소문자 2개로 구성된 클래스를 10번 반복해 Set에 추가하는 코드다.

Set은 중복을 허용하지 않으므로 26이 출력이 되야할 것 같지만, 실제로는 260이 출력된다. equals를 재정의 한 게 아니라 overloading했기 때문이다.(매개변수 타입 Object)

@Override 애너테이션을 달면 컴파일 오류로 잘못된 부분을 알 수 있다.

<aside>
💡 상위 클래스의 메서드를 재정의하려는 모든 메서드에 @Override 애너테이션을 달자.

</aside>

### 예외

상위 클래스의 추상 메서드를 재정의할 때는 굳이 안달아줘도 구현하지 않은 추상 메서드가 남아있다면 컴파일러가 알려준다.

그래도, 사실 다는게 좋은게, Set 인터페이스는 Collection 인터페이스를 확장했지만 새로 추가한 메서드가 없어서 모든 메서드 선언에 @Override를 달아 실수로 추가한 메서드가 없음을 보장했다.