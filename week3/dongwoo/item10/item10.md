## 아이템 10 equals는 일반 규약을 지켜 재정의하라

일단 equals를 다시 살펴보면 “==” 은 비굘르 위한 연산자고 주소의 값을 비교한다 equals는 객체끼리의 내용을 비교한다

```java
String str1 = "abc";
String str2 = str1;
String str3 = new String("abc");
// == 연산자는 주소를 비교합니다.
System.out.println(str1 == str2); // true
// str2 에 st1 값을 넣었으므로 주소를 같이 공유합니다.
System.out.println(str1 == str3); // false
// str1 과 str3는 각각 생성 되었으므로 주소가 다릅니다.
// equals() 는 내용을 비교합니다.
System.out.println(str1.equals(str2)); // ture
System.out.println(str1.equals(str3)); // true
// 내용을 비교하기떄문에 abc 내용이 같으므로 true 가 반환됩니다.
```

equals 메서드는 곳곳에 함정이 있어서 끔찍한 결과를 초래할 수 있다고 한다

- 각 인스턴스가 본질적으로 고유하다 → 객체 고유 번호를 보면 알 수 있듯이
- 인스턴스의 ’논리적 동치성’을 검사할 일이 없다 예를들어 Pattern의 정규표현식이 같은지 검사하는(논리적 동치성을)방법도 있다

```java
Pattern pattern1 = Pattern.compile("^[\\d]*$");
Pattern pattern2 = Pattern.compile("^[\\d]*$");

pattern1.equals(pattern2); // false
pattern1.pattern().equals(pattern2.pattern()) // true
```

- 상위 클래스에서 재정의한 equals가 하위 클래스에도 딱 들어 맞는다 Set 구현체는 AbstractSet이 구현한 equals를 상속받아 쓴다

```java
Set<String> tree = new HashSet<>();
Set<String> hash = new TreeSet<>();
tree.add("studyhub");
hash.add("studyhub");
tree.equals(tree) // true
```

- 클래스가 private이거나 package-private이고 equals메서드를 호출할 일이 없다 정말 극한으로 피하고 싶다면

```java
@Override
    public boolean equals(Object object) {
				throw new AssertionErrer();
    }
```

그렇다면 equals는 언제 재정의해야 할까? 개발자들은 두 값의 객체를 비교할때 객체가 같은지가 아니라 값이 같은지를 알고 싶을것이다 그리고 정적 메서드 팩터리처럼 인스턴스가 하나만 있는 경우에는 equals를 재정의 하지 않아도된다(당연하지) Enum도 여기에 해당된다

equals의 일반 규약 (동치관계를 만족시키는 요건) 동치란

- 반사성
    - null 이 아니라면 x.equals(x) → true
- 대칭성
    - null 이 아니라면 x.equals(y) → true 면 y.equals(x) → true
- 추이성
    - null 이 아니라면  x.equals(y) → true 고 y.equals(z) → true 면  x.equals(z) → true
- 일관성
    - 반복해서 호출하더라도 일관적으로 같은 결과를 반환함
- null 아님
    - x가 null이 아니라면 x.equals(null) → false

이중에서 대칭성을 위반할 수 있는 코드는

```java
public final class CaseInsensitiveString {

    private final String str;
    
    public CaseInsensitiveString(String str) {
        this.str = Objects.requireNonNull(str);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseInsensitiveString) {
            return str.equalsIgnoreCase(((CaseInsensitiveString) o).str);
        }
    
        if (o instanceof String) { // 한 방향으로만 작동한다.
            return str.equalsIgnoreCase((String) o);
        }
        return false;
    }
}

void symmetryTest() {
    CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
    String s = "Polish";
    System.out.println(cis.equals(s)); // true
    System.out.println(s.equals(cis)); // false
}
```

교재 코드인데  cis.equals(s)는 당연히 true일거다 근데 String의 equals는 CaseInsensitiveStringf를 모른다 그래서 대칭성을 위반한다

추이성

```java
public class Point {

    private final int x;
    private final int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) {
            return false;
        }
        Point p = (Point) o;
        return this.x == p.x && this.y == p.y;
    }
}
public class ColorPoint extends Point {

    private final Color color;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) {
            return false;
        }
// o가 일반 Point이면 색상을 무시하고 x,y만 비교한다.
        if (!(o instanceof ColorPoint)) {
            return o.equals(this);
        }
// o가 ColorPoint이면 색상까지 비교한다.
        return super.equals(o) && this.color == ((ColorPoint) o).color;
    }
}

ColorPoint a = new ColorPoint(2, 3, Color.RED);
Point b = new Point(2, 3);
ColorPoint c = new ColorPoint(2, 3, Color.BLUE);
a.equals(b); // true
b.equals(c); // true
a.equals(c); // false
```

o가 일반 Point이면 color을 무시하고 x,y 정보만 비교함, o가 ColorPoint이면 color까지 비교한다. 그리고 이런 코드는 무한 재귀에 빠질 수 있다

그러면

```java
@Override
public boolean equals(Object o) {
    // getClass
    if (o == null || o.getClass() != this.getClass()) {
        return false;
    }

    Point p = (Point) o;
    return this.x == p.x && this.y = p.y;
}
```

만약 추이성을 지키기 위해서 Point의 equals를 각 클래스들을 getClass를 통해서 같은 구체 클래스일 경우에만 비교하도록 하면 어떨까? 동작은 하지만 리스코프 치환 원칙(서브 타입은 언제나 기반 타입으로 교체할 수 있어야 한다) 위반이다

그럼 어떻게 할까?

```java
public class ColorPoint {

    private Point point;
    private Color color;

    public ColorPoint2(int x, int y, Color color) {
        this.point = new Point(x, y);
        this.color = Objects.requireNonNull(color);
    }
		// ColorPoint의 Point 뷰 를 반환
    public Point asPoint() {
        return this.point;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColorPoint)) {
            return false;
        }
        ColorPoint cp = (ColorPoint) o;
        return this.point.equals(cp) && this.color.equals(cp.color);
    }
}
```

“상속 대신 컴포지션을 사용하라” Point를 상속하는 대신 Point를 ColorPoint의 private 필드로 두고 ColorPoint와 같은 위치의 일반 Point를 반환하는 view메서드를 pulbic으로 추가하는 방식

또한 추상클래스를 사용하면 추상클래스의 하위 클래스에서라면 equals의 규약을 지키면서 값을 추가할 수 있다

일관성

```java
URL url1 = new URL("study-hub.site");
URL url2 = new URL("study-hub.site");
url1.equals(url2);
```

위에 네이버의 ip는 네트워크를 통해야 하는데 그 결과가 항상 같다고 보장할 수 없다 때문에 왜 와이? 우리 스터디 헙은 오토 스케일링으로 ec2를 두대까지 늘리기 때문에 그래서 equals의 판단에 신뢰할 수 없는 자원이 끼어들게 해서는 안된다 메모리에 존재하는 객체만을 사용해서 수행하자

null-아님

o.equasl(null) 이 true를 반환하는건 상상하기 어렵지만 실수로 NPE를 던지는 코드는 흔할것이다 일반 규약은 이런 경우도 허용하지 않는다

```java
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColorPoint)) {
            return false;
        }
        ColorPoint cp = (ColorPoint) o;
        return this.point.equals(cp) && this.color.equals(cp.color);
    }
```

물론 if (o == null) return false; 로 명시적으로 검사할 수 있지만 필요없다 위에 코드처럼 instanceof를 사용하면 첫번째 피연산자가 null이면 false를 반환한다

최종 정리

1. == 연산자를 이용해 입력이 자기 자신의 참조인지 확인한다

    ```java
    if (this == o) return true;
    ```

2. instanceof 연산자로 입력이 올바른 타입인지 확인한다

    ```java
    if(!(o instanceof Point)){return false;}
    ```

3. 입력을 올바른 타입으로 형 변환한다

    ```java
            ColorPoint cp = (ColorPoint) o;
    
    ```

4. 입력 객체와 자기 자신의 대응되는 ‘핵심’ 필드들이 모두 일치하는지 하나씩 검사한다
5. float 와 double을 제외한 기본 타입 필드는 ==연산자로 비교하고 참조 타입의 필드는 equals메서드로, float와 double은 compare로 비교한다 → Float.NaN, -0.0f, 특수한 부동소수 값을 다뤄야 하기 떄문에
6. 때론 null도 정산값으로 취급하는 참조타입 필드도 있다 이런 필드는 정적 메서드인 Object.equals(Object, Object)로 비교해 NPE발생을 예방하자
7. 최상의 성능을 바란하면 다를 가능성이 더 크거나 비교하는 비용이 싼 필드를 먼저 비교하자
8. equals를 다 구현했다면 세가지만 자문해보자 대칭적, 추이성, 일관적
9. 꼭 필요한 경우가 아니라면 equals를 재정의하지말자 대부분의 경우에 Object의 equals가 원하는 비교를 정확히 수행해준다