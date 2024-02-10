# 로 타입은 사용하지 말라 - item26

### 용어정리

제네릭 클래스(인터페이스) : 클래스와 인터페이스 선언에 **타입 매개변수**가 쓰인 것

e.g. List 인터페이스는 원소의 타입을 나타내는 **타입 매개변수 E**를 받는다. 

완전한 이름 : List<E> 

List<String>은 원소의 타입이 String인 리스트를 뜻하는 매개변수화 타입.

String이 정규 타입 매개변수 E에 해당하는 실제 타입 매개변수

제네릭 타입을 하나 정의하면 그에 딸린 raw type도 함께 정의된다.

로 타입 : 제네릭 타입에서 타입 매개변수를 전혀 사용하지 않을 때

e.g. List<E>의 로 타입은 List

### 로 타입 문제점

```java
for (Iterator i = stamps.iteraotr(); i.hasNext(); {
	Stamp stamp = (Stamp) i.next(); // ClassCastException
	...
}
```

컬렉션에 Stamp 타입만 받기로 생각했는데,  다른 타입을 넣은후 꺼내려다가 오류가 발생한다.

오류는 가능한, 이상적으로는 컴파일 오류가 가장 좋다.

제네릭을 활용해서 컴파일 시 확인하자.

### List x / List<Object> o

```java
public static void main(String[] args) {
    List<String> strings = new ArrayList<>();
    unsafeAdd(strings, Integer.valueOf(42));
    String s = strings.get(0); // 컴파일러가 자동으로 형변환 코드를 넣어준다.
}

private static void unsafeAdd(List list, Object o) {
    list.add(o);
}
```

strings.get(0)의 결과를 형변환 할 때 ClassCastException을 던진다.

List를 List<Object>로 선언하면 오류 메시지가 출력돼서 컴파일조차 되지 않는다.

### 제네릭 타입을 쓰고 싶지만 실제 타입 매개변수를 신경쓰기 싫으면

```java
static int numElementsInCommon(Set<?> s1){...}
```

와일드카드를 사용한다. 컬렉션의 불변식을 보장한다. 

Set<String>이 들어오면, String과 null만 들어올 수 있음

### 로 타입을 쓸 수 있는 예외

- class 리터럴

```java
List.class
String[].class ...
```

자바에서 class 리터럴에 제네릭 별로 변수화 하는것을 허용하지 않음

- instanceof 연산자

```java
if (o instanceof Set){ // 로 타입
  Set<?> s = Set<?> o; // 와일드카드 타입
  ...
}
```

런타임에는 제네릭 타입 정보가 지워지므로, 비한정적 와일드 카드(?)를 제외하고는 instanceof 연산자를 사용할 수 없다. 또한 instanceof의 맥락에선 로 타입과 비 한정적 와일드 카드가 동일하게 동작하므로, 코드가 깔끔해진다는 측면에서 차라리 안쓰는 게 낫다.