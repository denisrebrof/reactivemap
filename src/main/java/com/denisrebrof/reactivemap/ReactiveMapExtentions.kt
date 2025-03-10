@file:Suppress("unused")

package com.denisrebrof.reactivemap

import io.reactivex.Flowable

fun <K, V> ReactiveMap<K, V>.getItemFlowWithDefaultValue(key: K, default: V): Flowable<V> {
    val itemFlow = getItemFlow(key, default)
    val emptyItemFlow = observeRemove()
        .filter { event -> event.key == key }
        .map { default }
    return Flowable.merge(itemFlow, emptyItemFlow)
}

fun <K, V> ReactiveMap<K, V>.getItemFlow(key: K): Flowable<V> {
    val existingValue = get(key)
    val changesFlow = Flowable.merge(observeAdd(), observeReplace())
        .filter { event -> event.key == key }
        .map(ReactiveMapEvent<K, V>::value)
    return existingValue?.let(changesFlow::startWith) ?: changesFlow
}

fun <K, V> ReactiveMap<K, V>.getItemFlow(key: K, default: V): Flowable<V> {
    if (contains(key))
        return getItemFlow(key)

    return getItemFlow(key).startWith(default)
}