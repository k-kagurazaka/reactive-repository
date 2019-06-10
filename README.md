# Reactive Repository

[![](https://jitpack.io/v/k-kagurazaka/reactive-repository.svg)](https://jitpack.io/#k-kagurazaka/reactive-repository)
[![CircleCI](https://circleci.com/gh/k-kagurazaka/reactive-repository.svg?style=svg)](https://circleci.com/gh/k-kagurazaka/reactive-repository)

Repository code generator with Reactive Programming for Android

## Download

```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.github.k-kagurazaka.reactive-repository:annotation:x.y.z'
    annotationProcessor 'com.github.k-kagurazaka.reactive-repository:processor:x.y.z'
}
```

## Getting Started

### Declare entity

First, declare an entity class with `@InMemoryEntity` or `@PrefsEntity`.
`@InMemoryEntity` will be stored in memory cache and `@PrefsEntity` will be stored in SharedPreferences.

```java
@InMemoryEntity
public class UserStatus {
    public String userId;
}
```

```java
@PrefsEntity
public class ReadFlags {
    @PrefsKey
    public boolean isRead;
}
```

If you want to specify name of SharedPreferences or use default SharedPreferences, you can use `preferencesName` or `useDefaultPreferences = true` in `@PrefsEntity`.
And you can also specify a key of SharedPreferences like `@PrefsKey("is_read")`.

### Declare repository

Next, declare a repository interface with `@InMemoryRepository` or `@PrefsRepository`.

```java
@InMemoryRepository(UserStatus.class)
public interface UserStatusRepository {
    @Nullable
    UserStatus get();

    void save(@Nullable UserStatus userStatus);
}
```

```java
@PrefsRepository(ReadFlags.class)
public interface ReadFlagsRepository {
    @NonNull
    ReadFlags get();

    void save(@Nullable ReadFlags readFlags);

    @NonNull
    Observable<ReadFlags> observe();
}
```

You are done!

Repactive Repository generates implementations of above repository interfaces as `UserStatusRepositoryImpl` and `ReadFlagsRepositoryImpl`.
You can specify the generated class name with `generatedClassName` parameter of repository annotation.

## Supported methods

Reactive Repository supports following methods for code generation;

|            |                                                           |
-------------|------------------------------------------------------------
| getter     | method taking no parameter and returning the entity class |
| setter     | method taking the entity class as the only one parameter and returning void |
| observable | method taking no parameter and returning RxJava 2 Observable or Flowable of the entity class |


## Kotlin data class support

If you use Kotlin, immutable data class is good option for entity declaration.

```kotlin
@PrefsEntity
data class RealdFlags(
    @get:PrefsKey val isRead: Boolean = false
)
```

Note that `@PrefsKey` must be set to getter by `@get:PrefsKey`.

## Limitation

Reactive Repository requires some limitations for entity / repository definitions.
If you violate a following limitation, annotation processing will be failed.

### Both of `@InMemoryRepository` and `@PrefsRepository`

- Getter and setter can be defined only once
- `@Nullable` setter cannot be defined with RxJava 2 integration since all streams of RxJava 2 never accept `null` value

### `@InMemoryRepository`

- `@Nullable` getter can be defined only if the entity class has a constructor with no parameters
- `@NonNull` getter and `@Nullable` setter cannot be defined togeter

### `@PrefsEntity`

- Constructor with no parameters is required and it is used to get default values
- Cannot set `useDefaultPreferences` to `true` when `preferencesName` is set

### `@PrefsRepository`

- `@Nullable` getter cannot be defined since `@PrefsRepository` return the entity instance filled with default values when no data are stored

## License

    Copyright 2019 Keita Kagurazaka

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
