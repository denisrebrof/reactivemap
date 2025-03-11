package com.denisrebrof.reactivemap

import io.reactivex.subscribers.TestSubscriber
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ReactiveMapExtensionsTest {

    private lateinit var map: ReactiveMap<Long, String>

    private val testKey = 0L

    @BeforeEach
    fun initEmptyMap() = ReactiveMap<Long, String>().let(::map::set)

    @AfterEach
    fun disposeMap() = map.dispose()

    @Test
    fun `test getItemFlow() by non-existing key awaits for addition and then emits added value`() {
        val subscriber = TestSubscriber<String>()
        map.getItemFlow(testKey).subscribe(subscriber)

        //Assert flow with undefined item awaits for addition
        subscriber.assertNoErrors()
        subscriber.assertValueCount(0)

        val testValue = "Value"

        //Assert entry addition adds next element to flow
        map[testKey] = testValue
        subscriber.assertNoErrors()
        subscriber.assertValueCount(1)
        subscriber.assertValue(testValue)
    }

    @Test
    fun `test getItemFlow() by existing key immediately returns current value by this key`() {
        val subscriber = TestSubscriber<String>()
        //Assert flow with defined by testKey item gives current value right on subscription
        val testValue = "Value"
        map[testKey] = testValue
        map.getItemFlow(testKey).subscribe(subscriber)
        subscriber.assertNoErrors()
        subscriber.assertValueCount(1)
        subscriber.assertValue(testValue)
    }

    @Test
    fun `test getItemFlow() on replace, remove, clear & dispose map actions works correctly`() {
        val subscriber = TestSubscriber<String>()
        var testValue = "Value"
        map[testKey] = testValue
        map.getItemFlow(testKey).subscribe(subscriber)

        //Assert value replacement adds next element to flow
        testValue = "ValueReplacement"
        map[testKey] = testValue
        subscriber.assertNoErrors()
        subscriber.assertValueCount(2)
        subscriber.assertValueAt(1, testValue)

        //Assert key remove invokes nothing
        map.remove(testKey)
        subscriber.assertNoErrors()
        subscriber.assertNotComplete()
        subscriber.assertValueCount(2)
        subscriber.assertValueAt(1, testValue)

        //Assert map clear invokes nothing
        map.clear()
        subscriber.assertNoErrors()
        subscriber.assertNotComplete()
        subscriber.assertValueCount(2)
        subscriber.assertValueAt(1, testValue)

        //Assert map dispose completes flow
        map.dispose()
        subscriber.assertNoErrors()
        subscriber.assertComplete()
    }

    @Test
    fun `test getItemFlow(key, default) returns default value when there are no item by key`() {
        val subscriber = TestSubscriber<String>()
        val defaultValue = "DefaultValue"
        map.getItemFlow(testKey, defaultValue).subscribe(subscriber)

        //Assert map returns default entry on no item by key
        subscriber.assertNoErrors()
        subscriber.assertValueCount(1)
        subscriber.assertValue(defaultValue)
    }

    @Test
    fun `test getItemFlow(key, default) returns single existing item`() {
        val subscriber = TestSubscriber<String>()
        val existingValue = "ExistingValue"
        val defaultValue = "DefaultValue"
        map[testKey] = existingValue
        map.getItemFlow(testKey, defaultValue).subscribe(subscriber)

        //Assert map returns only existing entry
        subscriber.assertNoErrors()
        subscriber.assertValueCount(1)
        subscriber.assertValue(existingValue)
    }

    @Test
    fun `test getItemFlowWithDefaultValue(key, default) returns default value when empty`() {
        val subscriber = TestSubscriber<String>()
        val defaultValue = "DefaultValue"
        map.getItemFlowWithDefaultValue(testKey, defaultValue).subscribe(subscriber)

        //Assert map returns only default value
        subscriber.assertNoErrors()
        subscriber.assertValueCount(1)
        subscriber.assertValue(defaultValue)
    }

    @Test
    fun `test getItemFlowWithDefaultValue(key, default) returns default value after remove`() {
        val subscriber = TestSubscriber<String>()
        val testValue = "TestValue"
        val defaultValue = "DefaultValue"
        map[testKey] = testValue
        map.getItemFlowWithDefaultValue(testKey, defaultValue).subscribe(subscriber)

        map.remove(testKey)

        //Assert map returns existing value -> default value
        subscriber.assertNoErrors()
        subscriber.assertValueSequence(listOf(testValue, defaultValue))
    }

    @Test
    fun `test getItemFlowWithDefaultValue(key, default) returns existing value after remove and add`() {
        val subscriber = TestSubscriber<String>()
        val defaultValue = "DefaultValue"
        val testValue1 = "TestValue1"
        val testValue2 = "TestValue2"
        map.getItemFlowWithDefaultValue(testKey, defaultValue).subscribe(subscriber)
        map[testKey] = testValue1
        map.remove(testKey)
        map[testKey] = testValue2

        //Assert map returns correct values
        subscriber.assertNoErrors()
        subscriber.assertValueSequence(listOf(defaultValue, testValue1, defaultValue, testValue2))
    }
}