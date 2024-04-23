# nexign-kotlin-bp

## How to run

- First of all we need `kotlin` installed. [How to](https://kotlinlang.org/docs/command-line.html)

- Then we ran a following command ```kotlin -cp path/to/engine.jar com.nexign.dsl.engine.Main -s path/to/folder/with/scenario/jars```

### Test Json for `ArithmeticScenario`
```json
{"scenarioClassName":"com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario","inputClassName":"com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticInput","input":"{\"a\":12.0,\"b\":5.5}"}
```

[comment]: <> (kotlin -cp nexign-kotlin-bp-dsl-engine-1.0-SNAPSHOT.jar com.nexign.dsl.engine.Main -s /mnt/d/_Kira/Higher_School_of_Economics/_Nexign_graduation_work/tryout/jars)

## License 

MIT License, more info in [LICENSE](/LICENSE) file.