# ReactiveMap

This is my port from [ReactiveDictionary](https://github.com/neuecc/UniRx/blob/master/Assets/Plugins/UniRx/Scripts/UnityEngineBridge/ReactiveDictionary.cs) to [RxJava](https://mvnrepository.com/artifact/io.reactivex.rxjava2/rxjava)

I'll publish this project on Maven soon

Now I use this project as a [local Maven repository](https://maven.apache.org/repositories/local.html) in my Android and Kotlin projects

## Steps for local use:
1. Install Maven locally
2. Publish the library with `gradlew publishToMavenLocal`
3. Add `mavenLocal()` to the `repositories` block of your gradle files
4. Include it as a dependency in your gradle project with `implementation("com.denisrebrof:reactivemap:1.0.1")`
5. Sync your project
6. Viola! ✨
