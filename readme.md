### Config Parser

During working on that task, I'm a bit optimized syntax for Scala,
because original examples was created for dynamically typed language.

Major change is that now, to parse a config, customer should provide a case class with schema.
In general usage example looking like that:
```
  final case class TestConfig(common: CommonConfig,
                              ftp: FtpConfig,
                              http: HttpConfig)
                            
  Config.load[TestConfig]("path/to/config.ini", Nil)
``` 
For more concrete example please check `ConfigIT.scala`.

Due to that, if config loading is succeed, there is no way to crash when you read
the config value in code. All the checks will be done in compile time.

#### Things to improve
If I will spend more time on that task, probably I will improve error handling.
Currently there is no way to understand what goes wrong during the conversion from Map to Object.

Also, currently I'm using `Map[String, Any]` as a file parser output, that is not the state of the art solution.
It should be much better to use some ADT like:
```
  sealed trait ConfigADT
  final case class StringValue(value: String) extends ConfigADT
  .....
```
In that case we will have more control on input of customer to provide better error handling.

One more major point is handling of different number types, currently its working quite straightforward.
If number doesn't fit to Int then its Long or Double, because of that numeric type in schema should be
the same as file parser will recognize. I'm sure there should be better solution, probably its possible to
use HList generated from Config class to understand a type.

#### PS
All project formatted by `scalafmt`, it have quite specific way to express some syntax constructions.

#### PPS
I didn't measured time that I spent on that parser, but I'm done it in one evening that is around 3-4 hours.
It was a luck that I had shapeless Map to Obj converter from some of my code playgrounds, so I just needed to
extend it to support nested classes. In real world service, I probably will prefer to use `pureconfig` library
instead :)