# 명명 패턴보다 애너테이션을 사용하라 - item39

전통적으로 도구나 프레임워크가 특별히 다뤄야 할 프로그램 요소에는 딱 구분되는 명명 패턴을 적용해왔다. 예컨데 JUnit은 버전 3까지 테스트 메서드 이름을 test로 시작하게 끔 했다.

### 명명 패턴의 단점

1. 오타가 나면 메서드를 무시해서 테스트가 통과됐다고 오해할 수 있다.
2. 올바른 프로그램 요소에서만 사용되리라 보증할 방법이 없다.

⇒ JUnit에 메서드가 아닌 클래스를 던져서 내부 메서드를 실행해주길 원했으나 수행되지 않는다.

1. 프로그램 요소를 매개변수로 전달할 마땅한 방법이 없다.

⇒ 특정 예외를 던져야 성공하는 테스트가 있다고 할 때 기대하는 예외 타입을 테스트에 매개변수로 전달해야하는데 방법이 마땅히 없다. 

### 애너테이션 도입

JUnit4 부터 도입하여 위 문제들을 해결했다.

애너테이션의 동작 방식을 보기 위해 테스트 코드를 작성해본다.

```java
import java.lang.annotaion.*;

/** 테스트 메서드임을 선언하는 애너테이션이다.
 * 매개변수 없는 정적 메서드 전용이다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
}
```

메타애너테이션 : 애너테이션 선언에 다는 애너테이션 e.g. @Retention, @Target

@Retention(RetentionPolicy.RUNTIME) : @Test가 런타임에도 유지되어야 한다.

@Target(ElementType.METHOD) : @Test가 반드시 메서드 선언에서만 사용돼야 한다.

“ 매개변수 없는 정적 메서드 전용이다.” 이 제약을 컴파일러가 강제하려면 애너테이션 처리기를 직접 구현해야 한다.

```java
public class Sample{
		@Test public static void m1(){ } //성공해야 한다.
		public static void m2() { }
		@Test public static void m3() { //실패해야 한다.
				throw new RuntimeException("실패");
		}
		@Test public void m5(){ } //정적 메서드가 아니다.
		public static void m6(){ }
		@Test public static void m7(){ //실패해야 한다.
				throw new RuntimeException("실패");
		}
		public static void m8(){ }

}
```

애너테이션이 붙지 않은 m2,6,8 메서드는 무시된다.

m5는 인스턴스 메서드라 잘못 사용했다.

m3, m7은 예외를 발생해 실패한다.

@Test는 코드에 직접적인 영향을 주진 않지만 이 애너테이션에 관심있는 프로그램에게 추가 정보를 제공할 뿐이다. 다음의 RunTest가 바로 그런 도구의 예다.

```java
public class RunTests {
    public static void main(String[] args) throws Exception {
        int tests = 0;
        int passed = 0;
        Class<?> testClass = Class.forName(args[0]);
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.**isAnnotationPresent**(Test.class)) { 
                tests++;
                try {
                    m.invoke(null);
                    passed++;
                } catch (InvocationTargetException wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    System.out.println(m + " 실패: " + exc);
                } catch (Exception exc) {
                    System.out.println("잘못 사용한 @Test: " + m);
                }
            }
        }
        System.out.printf("성공: %d, 실패: %d%n", passed, tests-passed);
    }
}
```

클래스 이름을 받아, 클래스에서 @Test 애너테이션이 달린 메서드를 찾아  차례로 호출하는 코드다.

리플렉션 메커니즘이 예외를 InvocationTargetException 감싸 던져서, 프로그램에서 이를 가져와 원래 예외를 추출해(getCause())출력한다.

InvocationTargetException 외의 예외가 발생하면 @Test 애너테이션을 잘못 사용했다는 뜻

### 매개변수를 받는 애너테이션 타입

```java
import java.lang.annotation.*;

/**
 * 명시한 예외를 던져야만 성공하는 테스트 메서드용 애너테이션
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    Class<? extends Throwable> value();
}
```

이 애너테이션의 매개변수 타입은 Class<? extends Thrawable> 이며(한정적 타입 토큰), 이는 "Throwable을 확장한 클래스의 Class 객체 "라는 뜻이다. 따라서 모든 예외와 오류 타입을 수용한다.

```java
public class Sample2 {
    @ExceptionTest(ArithmeticException.class)
    public static void m1() {  // 성공해야 한다.
        int i = 0;
        i = i / i;
    }
    @ExceptionTest(ArithmeticException.class)
    public static void m2() {  // 실패해야 한다. (다른 예외 발생)
        int[] a = new int[0];
        int i = a[1];
    }
    @ExceptionTest(ArithmeticException.class)
    public static void m3() { }  // 실패해야 한다. (예외가 발생하지 않음)
}
```

```java
if (m.isAnnotationPresent(ExceptionTest.class)) {
                tests++;
         try {
               m.invoke(null);
               System.out.printf("테스트 %s 실패: 예외를 던지지 않음%n", m);
         } catch (InvocationTargetException wrappedEx) {
               Throwable exc = wrappedEx.getCause();
               Class<? extends Throwable> excType =
               m.getAnnotation(ExceptionTest.class)**.value();**
                if (excType.isInstance(exc)) {
                     passed++;
                } else {
                     System.out.printf(
                                "테스트 %s 실패: 기대한 예외 %s, 발생한 예외 %s%n",
                                m, excType.getName(), exc);
                }
           } catch (Exception exc) {
                System.out.println("잘못 사용한 @ExceptionTest: " + m);
           }
}
```

@ExceptionTest()을 다루도록 수정한 코드다.

@Test 애너테이션용 코드와 비슷해 보이는데, 차이점은 애너테이션 매개변수의 값을 추출하여 테스트 메서드가  올바른 예외를 던지는지 확인하는데 사용한 것

### **다수의 예외를 명시하는 애너테이션 - @Repeatable**

배열 매개변수를 받는 애너테이션 타입

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface ExceptionTest {
    Class<? extends Throwable>[] value();
}

@ExceptionTest({IndexOutOfBoundsException.class,
NullPointerException.class})
```

여러 개의 값을 받는 애너테이션을 만들 때 배열 매개변수 대신에 @Repeatable 메타애너테이션을 다는 방식을 쓸 수 있다.

@Repeatable을 단 애너테이션은 하나의 프로그램 요소에서 여러번 달 수 있다.

주의할 점

- @Repeatable을 단 애너테이션을 반환하는 '컨테이너 애너테이션'을 하나 더 정의하고, @Repeatable에 이 컨테이너 애너테이션의 class 객체를 매개변수로 전달해야 한다.

⇒ @TestContainer 애너테이션을 함께 정의해서 안에 Test[] @Test 애너테이션을 배열로 담을 수 있게 하라는 것.

- 컨테이너 애너테이션은 내부 애너테이션 타입의 배열을 반환하는 value 메서드를 정의해야 한다
- 컨테이너 애너테이션 타입에는 적절한 보존 정책(@Retention)과 적용 대상(@Target)을 명시해야 한다. 그렇지 않으면 컴파일 되지 않는다.

### @Repeatable 적용

반복 가능한 애너테이션 타입

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionTestContainer.class) 
public @interface ExceptionTest {
    Class<? extends Throwable> value();
}
```

반복 가능한 애너테이션의 컨테이너 애너테이션

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTestContainer {
    ExceptionTest[] value();     // value 메서드 정의
}
```

반복 가능한 애너테이션을 두 번 단 코드

```java
@ExceptionTest(IndexOutOfBoundsException.class)
@ExceptionTest(NullPointerException.class)
public static void doublyBad() {
	...
}
```

### 반복 가능 애너테이션 주의점

반복 가능 애너테이션을 여러 개 달면 하나만 달았을 때와 구분하기 위해 해당 컨테이너 애너테이션 타입이 적용된다. 

getAnnotationByType메서드는 이 둘을 구분하지 않기 때문에 반복 가능 애너테이션과 컨테이너 애너테이션을 모두 가져오지만, isAnnotationPresent 메서드는 둘을 구분하기에 이전과 같이 단순히 isAnnotationPresent로 반복 가능 애너테이션이 달렸는지 검사하면 의도와 다른 결과를 반환하다.

구분없이 모두 검사하려면 둘을 따로따로 확인해야한다.

```java
if (m.isAnnotationPresent(ExceptionTest.class)
|| m.isAnnotationPresent(ExceptionTestContainer.class)){
		//....
}
```