# 비트 필드 대신 EnumSet을 사용하라 - item36

```java
public class Text {
    public static final int STYLE_BOLD = 1 << 0; // 1
    public static final int STYLE_ITALIC = 1 << 1; // 2
    public static final int STYLE_UNDERLINE = 1 << 2; // 4
    public static final int STYLE_STRIKETHROUGH = 1 << 3; // 8

    // 매개변수 styles는 0개 이상의 STYLE_ 상수를 비트별 OR한 값이다.
    public void applyStyles(int styles) {
        // ...
    }

	public static void main(String[] args) {
				Text text = new Text();
        text.applyStyles(STYLE_BOLD | STYLE_ITALIC);
  }
}
```

열거한 값들이 단독이 아닌 집합으로 사용될 경우, 예전에는 각 상수에 서로 다른 2의 거듭제곱 값을 할당한 정수 열거 패턴을 사용했다.

위 처럼 비트별 OR을 사용해 여러 상수를 하나의 집합으로 모을 수 있다.

이렇게 만들어진 집합을 비트 필드라 한다.

비트 필드를 사용하면 정수 열거 상수의 단점 + 추가로 다음 문제까지 안고있다.

비트 필드 값이 그대로 출력되면 단순한 정수 열거 상수를 출력할 때보다 해석하기 훨씬 어렵다.

비트 필드 하나에 녹아 있는 모든 원소를 순회하기도 까다롭다.

마지막으로, 최대 몇 비트가 필요한지를 API 작성 시 미리 예측하여 적절한 타입(보통 int나 long)을 선택해야한다.

### 비트 필드를 대체하는 EnumSet

```java
public class Text {
    public enum Style {BOLD, ITALIC, UNDERLINE, STRIKETHROUGH}

    // 어떤 Set을 넘겨도 되나, EnumSet이 가장 좋다.
    public void applyStyle(Set<Style> styles) {
        // ...
    }

		public static void main(String[] args) {
				Text text = new Text();
        text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));
	  }
}
```

Set 인터페이스를 완벽히 구현, 내부는 비트 백터로 구현되어서 원소가 64개 이하라면, EnumSet 전체를 long 변수 하나로 표현하여 비트 필드에 비견되는 성능을 보여준다.

removeAll과 retainAll 같은 대량 작업은 비트를 효율적으로 처리할 수 있는 산술 연산을 써서 구현했다.

- 난해한 작업들은 EnumSet이 다 처리해줘서 비틀르 직접 다룰 때 겪는 흔한 오류들로부터 해방된다.