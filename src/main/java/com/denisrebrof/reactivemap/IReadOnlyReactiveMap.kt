package com.denisrebrof.reactivemap

import com.denisrebrof.reactivemap.ReactiveMapEvent.*
import io.reactivex.Flowable

//Migrated from ReactiveDictionary (C#, UniRx Unity framework)
interface IReadOnlyReactiveMap<K, V> {
    fun observeAdd(): Flowable<Add<K, V>>
    fun observeCountChanged(): Flowable<Int>
    fun observeCountChanged(notifyCurrentCount: Boolean = false): Flowable<Int>
    fun observeRemove(): Flowable<Remove<K, V>>
    fun observeReplace(): Flowable<Replace<K, V>>
    fun observeReset(): Flowable<Unit>
}
