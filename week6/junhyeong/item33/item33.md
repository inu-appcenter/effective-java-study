# 타입 안전 이종 컨테이너를 고려하라 - item33

제네릭은 Set<E> 등의 컬렉션과 ThreadLocal<T> 등의 단일원소 컨테이너에도 흔히 쓰인다. 매개변수화되는 대상은 원소가 아닌 컨테이너 자신이다.

따라서 하나의 컨테이너에서 매개변수화할 수 있는 타입의 수가 제한된다.

Set은 하나의 타입 매개변수만 있으면 되고 Map은 2개가 필요하다는 말이다.

### 유연한 수단이 필요할 때도 종종있다.

데이터베이스에서 행은 임의 개수의 열을 가질 수 있는데 열을 타입 안전하게 이용한다면 멋질 것이다.

쉬운 해법이 있는데, 컨테이너 대신 키를 매개변수화한 다음, 테이너에 값을 넣거나 뺄 때 매개변수화한 키를 함께 제공하면 된다. 이런 설계 방식을 타입 안전 이종 컨테이너 패턴이라 한다.

예제

```java
public class Favorite {
	private Map<Class<?>, Object> favorites = new HashMap<>();

	public <T> void putFavorite(Class<T> type, T instance) {
		favorites.put(Objects.requireNonNull(type), type.cast(instance));
	}
	public <T> T getFavorite(Class<T> type) {
		return type.cast(favorites.get(type));
	}
}

public static void main(String[] args) {
	Favorites f = new Favorites();
	
	f.putFavorite(String.class, "Java");
	f.putFavorite(Integer.class, 0xcafebabe);
	f.putFavorite(Class.class, Favorites.class);

	String favoriteString = f.getFavorite(String.class);
	int favoriteInteger = f.getFavorite(Integer.class);
	Class<?> favoriteClass = f.getFavorite(Class.class);

	System.out.printf("%s %x %s\\n", favoriteString, favoriteInteger, favoriteClass.getName());
}
```

Favorites 인스턴스는 String을 요청했는데 Integer를 반환하는 일은 절대 없고, 모든 키의 타입이 제각각이라 일반적인 Map과 달리 여러 가지 타입의 원소를 담을 수 있다.

따라서, 타입 안전 이종 컨테이너라 할 만하다.

이게 Map이 와일드카드 타입이면 뭐가 들어갈지 몰라서 넣을 수 없겠지만, key가 와일드카드 타입이라 다양한 타입 지원이 가능하다.

### 위 클래스에서 알아두어야 할 제약

1. 악의적인 클라이언트가 Class 객체를 로 타입으로 넘기면 타입 안전성이 깨진다.

```java
f.putFavorite(**(Class) Integer.class**, "Integer의 인스턴스가 아니다.");
int favoriteInteger = f.getFavorite(Integer.class) // ClassCastException
```

이는 컴파일할 때 비검사 경고가 뜰 것이고 타입 불변식을 어기는 일이 없도록 보장하려면 putFavorite 메서드와 같이 instance 타입이 type으로 명시한 타입과 같은지 확인한다.

Collections에 checkedList,Set,Map 같은 메서드가 이 방식을 적용한 컬렉션 래퍼들이다.

1. 실체화 불가 타입(E, List..)에는 사용할 수 없다.

String이나 String[]은 저장할 수 있어도 List<String>은 안된다.

List<String>.class → 이게 안되기 때문. List<String>과 List<Integer>는 List.class 라는 객체를 공유하기 때문이다.

⇒ 이 제약에 대한 완벽한 우회로는 없다. e.g. 슈퍼 타입 토큰

### 허용하는 타입을 제한하고 싶을 때

Favoirtes가 사용하는 타입 토큰은 비한정적이다. 즉, getFavorite과 putFavorite은 어떤 Class 객체든 받아들인다. 때로는 허용하는 타입을 제한하고 싶을때가 있다.

한정적 타입 토큰을 활용한다.

한정적 타입 토큰 : 단순히 한정적 타입 매개변수나 한정적 와일드카드를 사용하여 표현 가능한 타입을 제한하는 타입 토큰

```java
public <T extends Annotation> T getAnnotation(Class<T> annotationType);
```

<T extends Annotation>

annotationType 인수는 애너테이션 타입을 뜻하는 한정적 타입 토큰이다.

Class<?> 타입의 객체를 한정적 타입 토큰을 받는 메서드에 넘기려면?

Class<? extends Annotaion>으로 형변환 할 수 있지만 비검사 경고가 뜰 것이다.

다행이도 Class 클래스가 이런 형변환을 안전하게 수행해주는 asSubClass 메서드를 가지고 있다.

```java
static Annotation getAnnotation(AnnotatedElement element,
																String annotationTypeName) { 
	Class<?> annotationType = null; // 비한정적 타입 토큰
	try {
		annotationType = Class.forName(annotationTypeName);
	} catch (Exception ex) {
		throw new IllegalArgumentException(ex);
	} 
	return element.getAnnotation(
		annotationType.asSubclass(Annotation.class));

```

asSubclass는 호출된 인스턴스 자신의 Class 객체를 인수가 명시한 클래스로 형변환한다.