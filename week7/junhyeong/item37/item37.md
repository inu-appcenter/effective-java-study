# ordinal 인덱싱 대신 EnumMap을 사용하라 - item37

```java
public class Plant {
    final String name;
    final LifeCycle lifeCycle;

    public Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override
    public String toString() {
        return name;
    }
}

public enum LifeCycle {
    ANNUAL, PERNNIAL, BIENNIAL
}
// 식물을 배열로 관리하고 생애주기를 묶는 메서드
public static void ordinal(List<Plant> garden) {
    Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[LifeCycle.values().length];
    for (int i = 0 ; i < plantsByLifeCycle.length ; i++) {
        plantsByLifeCycle[i] = new HashSet<>();
    }

    for (Plant plant : garden) {
        plantsByLifeCycle[**plant.lifeCycle**.**ordinal**()].add(plant);
    }

    for (int i = 0 ; i < plantsByLifeCycle.length ; i++) {
         System.out.printf("%s : %s%n",
                **LifeCycle.values()[i]**, plantsByLifeCycle[i]);
    }
 }
```

위 코드의 문제점.

- 배열은 제네릭과 호환되지 않아서 비검사 형변환을 수행해야한다.
- 배열은 각 인덱스가 의미하는 바를 알지 못해서 출력 결과에 직접 label을 달아야함.
- 가장 심각한 문제는 정수는 타입 안전하지 않아서 정확한 정숫값을 사용한다는 것을 직접 보증해야 한다.

### EnumMap

위에서 배열은 실질적으로 열거 타입 상수를 값으로 매핑하는 일을 한다.

그러니 Map을 사용할 수도 있다.

```java
public static void enumMap(List<Plant> garden) {
      Map<LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(LifeCycle.class);

      for (LifeCycle lifeCycle : LifeCycle.values()) {
          plantsByLifeCycle.put(lifeCycle,**new HashSet<>()**);
        }

      for (Plant plant : garden) {
          plantsByLifeCycle.get(plant.lifeCycle).add(plant);
      }

// toString 재정의
      System.out.println(plantsByLifeCycle);
  }
```

- 안전하지 않은 형변환 사용하지 않음
- 맵의 키인 열거 타입 그 자체가 출력용 문자열을 제공해 label 달 필요가 없다. ( toString 재정의)
- ordinal을 이용한 배열 인덱스를 사용하지 않아서 인덱스 오류가 날 수 없다.
- EnumMap 내부에서 배열을 사용하기 때문에 Map의 타입안정성과 배열의 성능을 얻어 냄.

### 스트림과 같이 사용

```java
Arrays.stream(garden)
	.collect(groupingBy(p -> p.lifeCycle,
() -> new EnumMap<>(LifeCycle.class), Collectors.toSet()));
```

EnumMap만 사용했을 때는 식물의 생애주기당 하나씩 중첩 맵을 만들었는데 반해, 스트림과 함께 사용하면해당 생애주기에 속하는 식물이 있을 때만 만든다.

### 전이 상태(다차원 관계 enumMap을 사용하라)

```java
public enum Phase {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT(SOLID, LIQUID),
        FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS),
        CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS),
        DEPOSIT(GAS, SOLID);

        private final Phase from;
        private final Phase to;

        Transition(Phase from, Phase to) {
            this.from = from;
            this.to = to;
        }

				// 상전이 맵을 초기화
        private static final Map<Phase, Map<Phase, Transition>> 
					m = Stream.of(values())
                    .collect(Collectors.groupingBy(t -> t.from, // 바깥 Map의 Key
                            () -> new EnumMap<>(Phase.class), // Map<Phase, List<Transition>> // Map<SOLID, List{MELT,SUBLIME}>
                            Collectors.toMap(t -> t.to, // 바깥 Map의 Value(Map으로), 안쪽 Map의 Key // Map<SOLID, {Map<LIQUID,>,..}
                                    t -> t, // 안쪽 Map의 Value // Map<SOLID, {Map<LIQUID,MELT>,..}
                                    (x,y) -> y, // 만약 Key값이 같은게 있으면 새로운 값 y 사용 // MELT(SOLID, LIQUID),  FREEZE(LIQUID, SOLID),
       

                                    () -> new EnumMap<>(Phase.class)))); // 안쪽 Map의 구현체
				// SOLID{(LIQUID,MELT), (GAS=SUBLIME))}
				// LIQUID{(SOLID,FREEZE), (GAS=BOIL)}

        public static Transition from(Phase from, Phase to) {
            return transitionMap.get(from).get(to);
        }
    }
}

public static void main(String[] args) {
        
    // 물질의 상태 변환 확인
    Phase fromPhase = Phase.LIQUID;
    Phase toPhase = Phase.GAS;
    Transition transitionFromTo = Transition.from(fromPhase, toPhase);
    System.out.println("Transition from " + fromPhase + " to " + toPhase + ": " + transitionFromTo);
}

//

Transition from LIQUID to GAS: BOIL
```