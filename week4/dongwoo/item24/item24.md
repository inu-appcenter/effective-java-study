## 아이템 24 멤버 클래스는 되도록 static으로 만들라

중첩 클래스는 다른 클래스 안에 정의된 클래스를 말한다 중첩 클래스는 자신을 감싼 바깥 클래스에서만 쓰여야 하며 그 외에 쓰임새가 있다면 톱레벨 클래스로 만들어야 한다

종류: 정적 멤버 클래스, 비정적 멤버 클래스, 익명 클래스, 지역 클래스 (정적 멤버 클래스를 제외하고 나머지는 전부 내부 클래스라고 한다)

정적 멤버 클래스

```java
public class Calculator {
		public static class PLUS(int a, int b) {
				public int method() {
						return a + b
				}
		}

		private static class MINUS(int a, int b) {
				public int method() {
						return a - b
				}
		}
}
```

외부 클래스에서는 MINUS에 접근할 수 없다 MINUS, PLUS는 Calculator클래스의 private 멤버에도 접근이 가능하다

정적 멤버 클래스는 흔히 바깥 클래스의 객체의 구성요소일떄 사용한다 중첩 클래스의 인스턴스가 바깥 인스턴스와 독립적으로 존재할 수 있다면 정적 멤버 클래스로 만들어야 한다

비정적 멤버 클래스

정적 멤버 클래스에서 static만 제거한거고 비정적 멤버 클래스의 이스턴스와 바깥 인스턴스의 관계는 멤버 클래스가 인스턴스와 될 때 확립되며 더이상 변경할 수 없다 결국엔 바깥 인스턴스 없이는 생성이 불가하다

```java
class A{
    private int num;
  
    class B{
  	      private int b;  
    }
}

void method(){
    A a = new A();
    A.B b = a.new B();
}
```

멤버 클래스에서 바깥 인스턴스에 접근할 일이 없다면 무조건 static을 붙혀서 정적 멤버 클래스로 만들자

static을 생략하면 바깥 인스턴스로의 숨은 외부 참조를 갖게된다 그러면 가비지 컬렉션이 바깥 클래스의 인스턴스를 수거하지 못해서 메모리 누수가 생기며 참조가 눈에 보이지 않아 문제의 원인을 찾기 힘들어진다

어댑터를 정의할 떄 자주 쓰인다 멤버 클래스에서 바깥 인스턴스에 접근할 일이 없다면 무조건 static을 붙혀서 정적 멤버 클래스로 만들자

익명 클래스

바깥 클래스의 멤버도 아니고 쓰이는 시점에 선언과 동시에 인스턴스가 만들어 진다 오직 비정적인 문맥에서 사용될 대만 바깥 클래스의 인스턴스를 참조할 수 있다. 하지만 제약 사항도 많다

선언한 지점에서만 인스턴스를 만들 수 있고 instanceOf검사나 클래스 이름이 필요한 작업은 수행할 수 없고 여러 인터페이스를 구현할 수 없고 인터페이스를 구현하면서 클래스를 상속할 수 없다 그리고 무엇보다 가독성이 떨어진다

```java
@StepScope
@Bean("studyPostTasklet")
public Tasklet studyPostTasklet() {
    return new Tasklet() {
        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
            studyPostRepository.closeStudyPostsByStartDate(LocalDate.now());
            return RepeatStatus.FINISHED;
        }
    };
}
```

이렇게 즉석으로 작은 함수 객체나 처리 객체를 만드는데 주로 사용했다 하지만 이제는 람다가 그 역할을 대신한다

```java
@StepScope
    @Bean("studyPostTasklet")
    public Tasklet studyPostTasklet() {
        return (contribution, chunkContext) -> {
            studyPostRepository.closeStudyPostsByStartDate(LocalDate.now());
            return RepeatStatus.FINISHED;
        };
    }
```

지역 클래스

는 가장 드물게 사용된다 지역변수를 선언할 수  있는 곳이면 어디든지 선언이 가능하고 유효범위도 지역변수랑 같다