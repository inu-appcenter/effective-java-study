# 배열보다는 리스트를 사용하라 - item28

```java
Object[] objectArray = new Long[1];
objectArray[0] = "타입이 달라 넣을 수 없다."; // ArrayStoreException을 던진다.

List<Object> ol = new ArrayList<Long>(); // 호환되지 않는 타입이다.
ol.add("타입이 달라 넣을 수 없다.");
```

배열에서는 실수를 런타임에야 알게 되지만, 리스트를 사용하면 컴파일할 때 바로 알 수 있다.

### 배열은 실체화가 된다.

⇒ 배열은 런타임에도 자신이 담기로 한 원소의 타입을 인지하고 확인한다

(동일한 데이터 타입의 원소들을 연속적인 메모리 위치에 저장하기 때문)

위 예제처럼 Long배열에 String을 넣으려 하니 Exception이 발생한다.

반면, 제네릭은 타입 정보가 런타임에는 소거된다.

⇒ 원소타입을 컴파일타임에서만 검사한다.

이 주요 차이로 인해 배열과 제네릭은 어우러지지 못한다.

new List<E>[] 이렇게 쓸 수 없다는 뜻이다.

### 왜?

타입 안전하지 않기 때문이다.

```java
List<String>[] stringLists = new List<String>[1]; (1)
List<Integer> intList = List.of(42); 
Object[] objects = stringLists; 
objects[0] = intList; 
String s = stringLists[0].get(0); (5)
```

제네릭 배열을 생성하는 (1)이 허용된다면?

List<String> 들만 담을 stringLists 을 선언했는데, Object[] 배열로 형변환해서 List<Integer>를 담을 수 있게 되었다.

이러고 (5)에서 자동 형변환이 된 첫 배열값을 꺼내려니깐 ClassCastException이 발생한다.

이런일을 방지하려면 제네릭 배열이 생성되지 않도록 (1)에서 컴파일 오류를 내야한다.

### 배열과 제네릭을 섞어 쓰고 싶다면?

```java
public class Chooser {
	private final Object[] choiceArray;

	public Chooser(Collection choices) {
		choiceArray = choices.toArray();
	}

	public Object choose() {
		Random rnd = ThreadLocalRandom.current();
		return choiceArray[rnd.nextInt(choiceArray.length)];
	}
}
```

이 클래스는 컬렉션 안의 원소 중 하나를 무작위로 선택해 반환하는 메서드를 제공한다.

제네릭을 사용하지 않아서, 메서드를 호출할 때마다 반환된 Object를 원하는 타입으로 형변환해야 한다. 혹시나 다른 타입 원소가 들어있으면 형변환 오류가 뜰거다.

```java
public class Chooser<T> {
	private final T[] choiceArray;

	public Chooser(Collection<T> choices) {
		choiceArray = choices.toArray(); // Object[] cannot be converted to T[]
	}

	public Object choose() {
		Random rnd = ThreadLocalRandom.current();
		return choiceArray[rnd.nextInt(choiceArray.length)];
	}
}
```

제네릭으로 바꾸려니 오류가 발생한다.

```java
(T[]) choices.toArray(); // unchecked cast ...
```

형변환을 해줬는데 다른 오류가 발생했다.

T가 무슨 타입인지 알 수 없어서 컴파일러가 런타임에도 안전한지 보장할 수 없다는 메시지다.

→ 제네릭에서는 원소의 타입 정보가 소거되어 런타임에는 무슨 타입인지 알 수 없다.

이 말은 프로그램은 동작한다는 뜻이다. 안전하다고 주석을 달던가, 애너테이션으로 경고를 숨기던가.

### 배열 대신 리스트

비검사 형변환 경고를 제거하려면 다음과 같이 바꾼다.

```java
public class Chooser<T> {
	private final List<T> choiceList;

	public Chooser(Collection<T> choices) {
		choiceList = new ArrayList<>(choices);
	}

	public T choose() {
		Random rnd = ThreadLocalRandom.current();
		return choiceList.get(rnd.nextInt(choiceList.size()));
	}
}
```

코드양이 조금 늘어서 살짝 느릴거지만 ClassCastException이 발생하지는 않으니 가치가 있는 일이다.