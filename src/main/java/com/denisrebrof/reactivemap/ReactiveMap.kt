package com.denisrebrof.reactivemap

import com.denisrebrof.reactivemap.ReactiveMapEvent.*
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor

//MutableMap, observable for common change events
//Migrated from ReactiveDictionary (C#, UniRx Unity framework)
class ReactiveMap<K, V>(
    private val inner: MutableMap<K, V> = hashMapOf(),
) : IReadOnlyReactiveMap<K, V>, MutableMap<K, V> by inner, Disposable {

    private var disposed = false

    //Map changes event processors
    private var countChanged: PublishProcessor<Int>? = null
    private var collectionReset: PublishProcessor<Unit>? = null
    private var mapAdd: PublishProcessor<Add<K, V>>? = null
    private var mapRemove: PublishProcessor<Remove<K, V>>? = null
    private var mapReplace: PublishProcessor<Replace<K, V>>? = null

    //region MutableMap overrides

    override fun put(key: K, value: V): V? {
        val oldValue = get(key)
        inner[key] = value
        if (oldValue != null) {
            mapReplace?.onNext(Replace(key, value, oldValue))
        } else {
            mapAdd?.onNext(Add(key, value))
            countChanged?.onNext(count())
        }
        return oldValue
    }

    override fun remove(key: K): V? {
        val removedValue = inner.remove(key) ?: return null
        mapRemove?.onNext(Remove(key, removedValue))
        countChanged?.onNext(count())
        return removedValue
    }

    override fun remove(key: K, value: V): Boolean {
        val removingValue = get(key) ?: return false
        if (removingValue != value)
            return false

        return remove(key) != null
    }

    override fun clear() {
        val beforeCount = count()
        inner.clear()
        collectionReset?.onNext(Unit)
        if (beforeCount > 0) {
            countChanged?.onNext(count())
        }
    }

    //endregion

    //region IReadOnlyReactiveMap implementation

    override fun observeCountChanged(): Flowable<Int> = observeCountChanged(false)

    override fun observeCountChanged(notifyCurrentCount: Boolean): Flowable<Int> {
        if (disposed)
            return Flowable.empty()

        val processor = countChanged ?: PublishProcessor.create<Int>().also(this::countChanged::set)
        return if (notifyCurrentCount) processor.startWith(count()) else processor
    }

    override fun observeReset(): Flowable<Unit> {
        if (disposed)
            return Flowable.empty()

        return collectionReset ?: PublishProcessor.create<Unit>().also(this::collectionReset::set)
    }

    override fun observeAdd(): Flowable<Add<K, V>> {
        if (disposed)
            return Flowable.empty()

        return mapAdd ?: PublishProcessor
            .create<Add<K, V>>()
            .also(this::mapAdd::set)
    }

    override fun observeRemove(): Flowable<Remove<K, V>> {
        if (disposed)
            return Flowable.empty()

        return mapRemove ?: PublishProcessor
            .create<Remove<K, V>>()
            .also(this::mapRemove::set)
    }

    override fun observeReplace(): Flowable<Replace<K, V>> {
        if (disposed)
            return Flowable.empty()

        return mapReplace ?: PublishProcessor
            .create<Replace<K, V>>()
            .also(this::mapReplace::set)
    }

    //endregion

    //region Disposable implementation

    override fun dispose() {
        if (disposed)
            return

        //Clean processor refs for GC collection

        countChanged?.onComplete()
        countChanged = null

        collectionReset?.onComplete()
        collectionReset = null

        mapAdd?.onComplete()
        mapAdd = null

        mapRemove?.onComplete()
        mapRemove = null

        mapReplace?.onComplete()
        mapReplace = null

        disposed = true
    }

    override fun isDisposed(): Boolean = disposed

    //endregion
}