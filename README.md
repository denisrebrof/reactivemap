# ReactiveMap

This is my port from [ReactiveDictionary](https://github.com/neuecc/UniRx/blob/master/Assets/Plugins/UniRx/Scripts/UnityEngineBridge/ReactiveDictionary.cs) to RxJava

I will add tests and publish this project on Maven soon

At the current stage I use this project as a **local Maven repository** in my Android and Kotlin projects

## Steps for local use:
1. Install Maven locally
2. Publish the library with `gradlew publishToMavenLocal`
3. Add `mavenLocal()` to the `repositories` block of your gradle files
4. Include it as a dependency in your gradle project with `implementation("com.denisrebrof:reactivemap:1.0.1")`
5. Sync your project
6. Viola! ✨
