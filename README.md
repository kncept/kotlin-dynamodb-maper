# Kotlin Data Class DynamoDB Mapper

AWS has finally released a kotlin sdk, but it does not include a 'nice' way to map things

This project intends to make mapping between dynamo db types and domain types easy.

## Usage

### Build file
In your build.gradle.kts file dependencies section
```kotlin
    implementation("com.kncept.ddb.mapper:mapper:1.0.0")
```
Note you will also need to define dependencies, something like:
```kotlin
    implementation("aws.sdk.kotlin:aws-core:$awsKotlinSdkVersion")
    implementation("aws.sdk.kotlin:dynamodb:$awsKotlinSdkVersion")
```
You _may_ need `implementation(kotlin("reflect"))`, I'm not sure.
This will be tested and docs updated.

### Using the type mapper
Create an instance. It comes pre-configured with a reasonable set of defaults.
```kotlin
    val mapper = DynamoDbTypeMapper()
```

In order to use the results, code similar to the following should be used. 
```kotlin
        
        val attributes: Map<String, AttributeValue> = mapper.toAttributes(item)
        val items: List<Item> = results.items.map { mapper.toItem(Item::class, it)}
```

### Configuration
There is a limited amount of configuration available, and the defaults should be sufficient for most use cases.
That said, there is always the need to do more at times, and

#### Module Config
Some modules have configuration.
For example, in order to disable epoch second resolution on an Instant, set the truncateTypesToEpochSecond variable to 
true and re-register the module.

#### Custom Type Mappers
first create your custom type mapper:
```kotlin
    class MyTypeMapper: TypeMapper<Boolean> { ... }
```
then register it by calling 
```kotlin
    mapper.register(MyTypeMapper())
```
Or you can use the annotation, at the field or class level
```kotlin
    @MappedBy(MyTypeMapper::class)
```
Collections and arrays are supported, but may not work for all cases, due to generics.<br>
If auto detection fails, you can speficy the type with the following annotation
```kotlin
    @MappedCollection(MyType::class) val things: List<*>
```


#### Mapper config
There are some configuration options available for the Mapper. Just update the option and it will take effect.
```kotlin
    mapper.emitNulls = true
```

# Acknowledgements
Inspired by https://github.com/oharaandrew314/dynamodb-kotlin-module 
