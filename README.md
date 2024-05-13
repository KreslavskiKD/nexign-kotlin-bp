# nexign-kotlin-bp

## How to run

- First of all we need `kotlin` installed. [How to](https://kotlinlang.org/docs/command-line.html)

- Then we ran a following command ```kotlin -cp path/to/engine.jar com.nexign.dsl.engine.Main -s path/to/folder/with/scenario/jars```

The engine currently has 2 working modes:

### Test engine
This is now a default mode. In that mode all interaction with Engine is done by interactive input/output console.

You can use the following commands:
- `start scenario` - after that you have to provide a json on a separate line. An example of such json is shown below in section Test Json. This command starts a specified scenario with provided input.
- `stop` - stops the Engine and all scenarios gracefully (when such mechanism will be implemented)
- `force stop` - stops the Engine and all scenarios ungracefully
- `get description` - after that you have to provide a json on a separate line. An example of such json is shown below in section Test Json. This command generates description of provided scenario in one of three types: text in english, dot notation or png picture.

### REST Service

 TBD https://github.com/KreslavskiKD/nexign-kotlin-bp/issues/33

### Test JSONs for `ArithmeticScenario`
For `start scenario` command
```json
{"scenarioClassName":"com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario","inputClassName":"com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticInput","input":"{\"a\":12.0,\"b\":5.5}"}
```

For `get description` command
```json
{"scenarioClassName":"com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario","descriptionType":"PICTURE","addErrorRouting":"NO"}
```

[comment]: <> (kotlin -cp nexign-kotlin-bp-dsl-engine-1.0-SNAPSHOT.jar com.nexign.dsl.engine.Main -s /mnt/d/_Kira/Higher_School_of_Economics/_Nexign_graduation_work/tryout/jars)

## License 

Code and documentation released under the MIT license. Copyright © 2024-2024, Kreslavski Kirill.