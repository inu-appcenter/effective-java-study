# finalizer와 cleaner 사용을 피하라 - item8

둘 다 객체 소멸자.

즉시 수행된다는 보장이 없어서, 제때 실행되어야 하는 작업은 절대 할 수 없다.

e.g. 시스템이 동시에 열 수 있는 파일 개수에는 한계가 있다.

파일 닫기를 위에 맡긴다면, 시스템이 finalizer, cleaner 사용을 제 때 실행하지 않아 파일을 계속 열어두면, 새로운 파일을 열지 못해 프로그램이 실패할 수 있다.

⇒ 상태를 영구적으로 수정하는 작업(e.g. 데이터베이스 영구 락 해제)에서 절대 의존하지 말자.

item9에서 try catch로 자원 반납하는 상황을 생각하면 이해가 됨.

아하 가비지컬렉터 대상이 될 때, 발생하게하는 이벤트 같은거구나.

```java
 		@Override
    protected void finalize() throws Throwable {
        System.out.println("clean");
    }
```

가비지 컬렉터에 대상이 언제 될지는 불확실하니깐 쓰지말라 한거고,

이걸 못쓰니깐 개발자가 직접 전통적으로 close()로 닫아주는거고

### 대신해줄 묘안

<aside>
💡 AutoCloseable 인터페이스를 구현한다.

</aside>

```java
public interface AutoCloseable {
    void close() throws Exception;
}
```

소멸시킬 클래스에 이를 상속받게 하면, close() 메서드가 자동으로 수행되는거다.

try-with-resources 사용 (예외가 발생해도 제대로 종료)