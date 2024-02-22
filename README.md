# Kotlin Data Class DynamoDB Mapper

AWS has finally released a kotlin sdk, but it does not include a 'nice' way to map things

This project intends to make mapping between dynamo db types and domain types easy.

# Usage

(when released) `implementation("com.kncept.dynamodbmapper:dynamodbmapper:1.0.0")`

```kotlin
        val mapper = DynamoDbTypeMapper()
        ...
        mapper.toItem(results.items())
```

# Registering Type Mappers
first create your custom type mapper:
```kotlin
    class MyTypeMapper: TypeMapper<Boolean> { ... }
```
then register it:
```kotlin
        val mapper = DynamoDbTypeMapper()
        mapper.register(MyTypeMapper())
```

# Customisation

There are several customisation options available
- EmitNulls - emit a null = true rather than omitting the value from the output attributes map

# Acknowledgements
Inspired by https://github.com/oharaandrew314/dynamodb-kotlin-module 
