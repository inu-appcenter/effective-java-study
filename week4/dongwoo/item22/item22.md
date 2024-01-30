## 아이템 22 인터페이스는 타입을 정의하는 용도로만 사용하라

```java
public interface SkillDAMAGE {
    
    static final int Q = 145;
    static final int W = 134;
    static final int E = 141;    
    static final int R = 523;
}
```

이거 안됨 왜? 클래스 내부에서 사용하는 상수는 외부 인터페이스강 ㅏ니라 내부 구현에 해당된다 상수 인터페이스는 내부 구현을 클래스의 API로 노출하는 행위이기 때문에 캡슐화가 깨진다 또한 클라이언트 코드가 내부 구현에 해당하는 이 상수들에 종속되게 한다

다음 릴리즈에서 이 상수들을 더는 쓰지 않더라도 바이너리 호환성(클래스를 변경하면 해당 클래스를 사용하는 클래스를 다시 컴파일 x)을 위해 여전히 상수 인터페이스를 구현하고 있어야 한다

```java
public class SkillDAMAGE {
    public static final int Q = 145;
    public static final int W = 134;
    public static final int E = 141;    
    public static final int R = 523;
		private SkillDamage(){}
}
```

이렇게 하자 그리고 이걸 사용할때는 정적 임포트로 상수 이름만 사용하자

결론 → 인터페이스는 타입을 정의하는 용도로만 사용해야 한다. 상수 공개용 수단으로 사용하지 말자