# nexign-kotlin-bp

## How to run

- First of all we need `kotlin` installed. [How to](https://kotlinlang.org/docs/command-line.html)

- Then we ran a following command ```kotlin -cp path/to/engine.jar com.nexign.dsl.engine.Main -s path/to/folder/with/scenario/jars```

```bash
Usage: engine [<options>]

Starts the scenarios DSL engine.

Options:
-s, --scenarios-dir=<path>  Directory where scenarios JARs are stored.
                            Default is ./scenarios
-t, --test-engine           Starts engine in Testing mode with interactive
                            console instead of REST.
-r, --rest-port=<int>       Specifies port to run REST interface at. Default
                            is 8080
-h, --help                  Show this message and exit
```

The engine currently has 2 working modes:

## Test engine
This is now a default mode. In that mode all interaction with Engine is done by interactive input/output console.

You can use the following commands:
- `start scenario` - after that you have to provide a json on a separate line. An example of such json is shown below in section Test Json. This command starts a specified scenario with provided input.
- `stop` - stops the Engine and all scenarios gracefully (when such mechanism will be implemented)
- `force stop` - stops the Engine and all scenarios ungracefully
- `get description` - after that you have to provide a json on a separate line. An example of such json is shown below in section Test Json. This command generates description of provided scenario in one of three types: text in english, dot notation or png picture.

## REST Service

Benchmarks: For now it handles about 2M RPM of `/start` requests. I don't know when the denial of service happens, because when I tried to make such a benchmark on the same PC where the server ran the JMeter I used for benchmarking died first... After 95 seconds and about 3.5M requests. Max latency was about 1350 ms, but I can't guarantee, because the results can't be saved - the JMeter dies.

Common route prefix: `/scenarios`

### Start scenario: `/start`
Method: POST

Content-type: `application/json`

Accepts following schema:
```json
{
  "scenarioClassName": "string",
  "inputClassName":"string",
  "input":"string"
}
```

First parameter is fully-qualified class name for scenario class. Second is fully-qualified class name for scenario input class. The third one is encoded JSON string of input.

Returns plain text if scenario returns any text as result and none otherwise. 

Returns `400 Bad Request` if JSON is malformed, `OK 200` if everything is OK and scenario started.

### Get or prepare description: `/description`
Method: POST

Content-type: `application/json`

Accepts following schema:
```json
{
  "scenarioClassName":"string",
  "descriptionType":"string",
  "addErrorRouting":"string"
}
```
First parameter is fully-qualified class name for scenario class. 

Second one can only be of three types: `"TEXT"`, `"DOT_FILE"`, `"PICTURE"`. Depending on that parameter you will get as a response either text description, description in DOT notation or an URI path to get description as a picture.

Third one can only be of three types: `"YES"`, `"NO"`, `"YES_WITHOUT_DEFAULT"`. Depending on that parameter you will get your description with error routing, without, or without only default error routing.

Returns `400 Bad Request` if JSON is malformed, `OK 200` if everything is OK and description as plain text.

### Get image: `/image/{path}`
Method: GET

No parameters, only path. This method is used to get generated images from `/description` request.

Returns `404 NOT FOUND` if path is incorrect or `OK 200` and PNG image if everything is correct.

### Example for PowerShell

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/scenarios/start/" -Method Post -ContentType "application/json" -Body '{"scenarioClassName":"com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario","inputClassName":"com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticInput","input":"{\"a\":12.0,\"b\":5.5}"}'
```

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/scenarios/description/" -Method Get -ContentType "application/json" -Body '{"scenarioClassName":"com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario","inputClassName":"com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticInput","dummyInput":"{\"a\":12.0,\"b\":5.5}","descriptionType":"PICTURE","addErrorRouting":"NO"}'
```

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