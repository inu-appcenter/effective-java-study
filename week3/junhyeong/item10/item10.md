# equals는 일반 규약을 지켜 재정의하라 - item10

```java
@Override
public boolean equals(Object o) {
     if (this == o) return true;
     if (o == null || getClass() != o.getClass()) return false;
     Item item = (Item) o;
     return price == item.price && quantity == item.quantity && Objects.equals(name, item.name);
}
```

equals 를 재정의 할때?

<aside>
💡 객체를 물리적이 아닌 논리적 동치성을 확인할 때,
상위 클래스의 equals가 논리적 동치성을 비교하도록 재정의되지 않을때
e.g.  값 클래스(Integer, String)

</aside>

단, 값이 같은 인스턴스가 둘 이상 만들어지지 않음을 보장하는 클래스는 어차피 객체 식별성 == 논리적 동치성 이므로 재정의 할 필요가 없다.

e.g. 싱글톤 , Enum

### Object 명세에 적힌 규약

<aside>
💡 equals 메서드는 동치관계(반사성, 대칭성, 추이성)을 구현한다

전제 : null이 아닌 모든 참조 값 x,y,z에 대해
- 반사성 :  x.equals(x) == true
- 대칭성 :  x.equals(y) == true → y.equals(x) == true
- 추이성 :  x.equals(y) == true , y.equals(z) == true → x.equals(z) == true

- 일관성 : x.equals(y)를 반복 호출하면 그 값은 항상 true or 항상 false
- null아님 : x.equals(null) == false

</aside>

### 대칭성을 위배한 예 1

```java
public final class CaseInsensitiveString{
  private final String s;

  public CaseInsensitiveString(String s){
    this.s = Obejcts.requireNonNull(s);
  }

  // 대칭성 위배!
  @Override 
	public boolean equals(Object o){
    if(o instanceof CaseInsensitiveString)
      return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
    if(o instanceof String) // 한방향으로만 작동한다.
      return s.equalsIgnoreCase((String) o);
    return false;
  }
}
```

다음은 대소문자에 상관없이 문자열을 비교하도록 재정의한 코드다.

```java
CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
String s = "polish"

cis.equals(s)는 true를 반환하지만 String의 equals는 CaseInsensitiveString
를 모르기 때문에 false를 반환할거고 이는 대칭성을 위반하는 예시다.
```

### 해결방법

```java
 @Override 
 public boolean equals(Object o){
    return o instanceof CaseInsensitiveString &&
			((CaseInsensitiveString) o).s.equalsIgnoreCase(s);
  }
```

String eqauls와 연동하겠다는 생각을 버린다.

### 대칭성을 위반한 예 2

```java
public class Point { 
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Point))
            return false;
        Point p = (Point)o;
        return p.x == x && p.y == y;
    }
}
public class ColorPoint extends Point { 
	  private final Color color;
    
    public ColorPoint(int x, int y, Color color){
    	super(x,y);
        this.color = color;
    }
    
    @Override public boolean equals(Object o) {
        if (!(o instanceof ColorPoint))
             return false;
        **return super.equals(o) && ((ColorPoint) o).color == color;**
    }
}
```

클래스를 확장한 경우 비교할 때 대칭성을 위반할 수 있다.

```java
Point p = new Point(1, 2);
ColorPoint cp = new ColorPoint(1, 2, Color.RED);

cp.equals(p) // false;
```

### 추이성을 위배

```java
@Override public boolean equals(Object o) {
				if (!(o instanceof Point))
             return false;
        if (!(o instanceof ColorPoint)) // Point면 색상 무시 비교
             return o.equals(this);
        **return super.equals(o) && ((ColorPoint) o).color == color;**
    }
```

```java
ColorPoint x= new ColorPoint(1, 2, Color.RED);
Point y = new Point(1, 2);
ColorPoint z = new ColorPoint(1, 2, Color.BLUE);

x.equals(y) // true
y.equals(z) // true
x.equals(z) // false
```

대칭성을 지키려고했지만 추이성이 깨진다.

### 구체 클래스를 확장해 새로운 값을 추가하면서 equals 규약을 만족시킬 방법은 존재하지 않는다.

```java

public class Point{
  @Override 
	public boolean equals(Object o) {
     if (o == null || o.getClass() != getClass())
       return false;
     Point p = (Point) o;
     return p.x == x && p.y == y;
  }
}
```

instanceOf 대신에 getClass로 비교하면 클래스가 일치하는 객체만 true를 반환하는거라 리스코프 치환원칙이 깨진다.

→ Point와 ColorPoint을 비교하면 false

⇒ . ColorPoint 클래스는 Point 클래스를 상속하고 있기 때문에, Point 클래스의 인스턴스로 대체될 수 있어야한다.

<aside>
💡 리스코프 치환원칙
 - 해당 타입의 모든메서드가 하위 타입에서도 똑같이 잘 동작해야한다.

</aside>

### 해결방법

상속 대신 컴포지션을 활용한다.

```java
public class ColorPoint {
	**private final Point point;
	private final Color color;**

	public ColorPoint(int x, int y, Color color) {
		point = new Point(x, y);
		this.color = Objects.requireNonNull(color);
	}

	**public Point asPoint() {
		return point;
	}**

	@Override public boolean equals(Object o) {
		if (!(o instanceof ColorPoint))
			return false;

		ColorPoint cp = (ColorPoint) o;
		return cp.point.equals(point) && cp.color.equals(color);
	}

}
```

비교할 때, 해당객체와 같은 타입인지 체크하고 각 상위 타입의 equals를 수행하면 리스코프 치환원칙을 지키면서 추이성, 대칭성도 지킬 수 있게 되었다.

### 일관성

1. 불변객체는 equals 결과가 항상 같도록 설계하자.
2. equals의 판단에 신뢰할 수 없는 자원이 끼게하지 말자.

e.g. java.net.URL 은 주어진 URL과 매핑된 호스트의 IP 주소를 이용해 비교한다.

→ 호스트 이름을 IP 주소로 바꾸려면 네트워크를 통하므로 항상 같다고 보장할 수 없다.

⇒ DNS 서버 이상, 호스트 이름 변경

### Null 아님

명시적으로 if (o == null) return false; 이렇게 체크해줘도 되지만

```java
if (!(o instanceof MyType))
		return false;
```

instanceof 연산으로 형변환하면서 null 일시 false를 반환하니 이렇게 쓰자.

### equals 구현 방법 정리

1. == 연산자로 자기 자신 참조인지 확인(성능 최적화)
2. instanceof 연산자로 입력이 올바른 타입인지 확인
3. 2번이 참이면 올바른 타입으로 형변환
4. 입력 객체와 자기 자신의 대응되는 '핵심' 필드들이 모두 일치하는지 하나씩 검사
- 이 때 float과 double이 아닌 기본 필드는 ==을 통해 비교하고, 참조 타입은 equals로, float과 double은 Float.compare(), Double.compare() 메서드를 이용
- null 값을 정상적이라고 취급하는 객체라면, NPE를 방지하기 위해 Objects.equals(a,b); 메서드를 이용
1. 대칭성, 추이성, 일관성을 지키는지 확인

### 주의사항

1. equals를 재정의 할 때는 hashcode도 반드시 재정의
2. Object 외의 타입을 매개변수로 받는 equals메서드를 정의하지 말자

```java
@Override // 오류
public boolean equals(MyClass o) {

}
```

이건 오버라이딩이 아니라 오버로딩임.