# 8_finalizer와_cleaner_사용을_피하라

## finalizer In Java

- 해당 객체를 가리키는 레퍼런스가 없을 때 가비지 컬렉터에 의해 호출된다.
- 리소스 반납이나 별도의 정리 작업을 수행해야한다.
- Object 클래스의 finalize() 는 그저 비어있는 메소드일 뿐(no-op)이라서 따로 오버라이드 하지 않을 경우 아무 동작도 수행하지 않는다.
- 어떤 스레드가 finalize() 를 호출할지는 모른다.
- finalize() 를 호출하는 스레드는 어떠한 user-visible synchronization lock 도 잡고있지 않는다.
- finalize() 내부에서 발생한 exception 은 무시된다.
- 한 객체에 대해 두 번 이상 호출되지 않는다.

```java
@Deprecated(since="9")
protected void finalize() throws Throwable { }
```

Object 클래스에 존재하는 finalize 메서드, 자바 9 이후로 Deprecated 됐다.

왜냐하면 finalizer(cleaner 포함)는 제때 실행되어야 하는 작업을 할 수 없기 때문이다.

자바 언어 명세는 finalizer, cleaner 의 수행 시점뿐 아니라 수행 여부조차 보장하지 않는다. 

접근할 수 없는 일부 객체에 딸린 종료 작업을 전혀 수행하지 못한 채 프로그램이 중단 될 수도 있다는 얘기다.

## finalizer, cleaner 단점
- 제때 실행되어야 하는 작업을 절대 할 수 없다.
  - 파일 닫기를 맡기게 되면 언제 실행되는지 몰라서 메모리를 다 잡아먹어 다른 파일을 열 수 없게돼 프로그램이 실패할 수 있다.
- 상태를 영구적으로 수정하는 작업에서는 절대 finalizer나 cleaner에 의존해서는 안 된다.
  - DB의 영구 락을 걸어놓았을 때 finalizer를 이용해 락을 해제하려고 한다면, finalizer가 락을 해제하지 않고 계속 가지고 있는 상황이 발생한다.
- 성능이 좋지 않다.