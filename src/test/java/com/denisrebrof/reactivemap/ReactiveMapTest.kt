package com.denisrebrof.reactivemap

import io.reactivex.subscribers.TestSubscriber
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ReactiveMapTest {

    private lateinit var map: ReactiveMap<Long, String>

    private val testPairs = arrayOf(0L to "Value0", 1L to "Value1")

    @BeforeEach
    fun initEmptyMap() = ReactiveMap<Long, String>().let(::map::set)

    @AfterEach
    fun disposeMap() = map.dispose()

    @Test
    fun `test observeCountChanged() handles common map operations correctly`() {
        val subscriber = TestSubscriber<Int>()
        map.observeCountChanged().subscribe(subscriber)

        val (firstKey, firstValue) = testPairs[0]
        //Assert entry addition invokes count change
        map[firstKey] = firstValue
        subscriber.assertNoErrors()
        subscriber.assertValueCount(1)
        subscriber.assertValue(1)
        //Assert entry reassign doesn't invoke count change
        map[firstKey] = firstValue
        subscriber.assertNoErrors()
        subscriber.assertValueCount(1)
        //Assert addition entry assign invokes count change
        val (secondKey, secondValue) = testPairs[1]
        map[secondKey] = secondValue
        subscriber.assertNoErrors()
        subscriber.assertValueCount(2)
        subscriber.assertValueAt(1, 2)
        //Assert remove(...) invokes count change
        map.remove(secondKey)
        subscriber.assertNoErrors()
        subscriber.assertValueCount(3)
        subscriber.assertValueAt(2, 1)
        //Assert non-present entry remove(...) doesn't invoke count change
        map.remove(secondKey)
        subscriber.assertNoErrors()
        subscriber.assertValueCount(3)
        //Assert cleaning invoke count change - and it becomes zero
        map.clear()
        subscriber.assertNoErrors()
        subscriber.assertValueCount(4)
        subscriber.assertValueAt(3, 0)
    }

    @Test
    fun `test observeReset() handles reset event correctly`() {
        val subscriber = TestSubscriber<Unit>()
        map.observeReset().subscribe(subscriber)
        //Assert empty map clear also invokes reset event
        map.clear()
        subscriber.assertNoErrors()
        subscriber.assertValueCount(1)
        //Assert non-empty map clear invokes reset event
        val (firstKey, firstValue) = testPairs[0]
        map[firstKey] = firstValue
        map.clear()
        subscriber.assertNoErrors()
        subscriber.assertValueCount(2)
    }

    @Test
    fun `test observeAdd() handles add event correctly`() {
        val subscriber = TestSubscriber<ReactiveMapEvent.Add<Long, String>>()
        map.observeAdd().subscribe(subscriber)
        val (firstKey, firstValue) = testPairs[0]
        map[firstKey] = firstValue
        subscriber.assertNoErrors()
        subscriber.assertValueCount(1)
        subscriber.assertValueAt(0) { addEvent ->
            addEvent.key == firstKey && addEvent.value == firstValue
        }
    }

    @Test
    fun `test observeRemove() handles remove event correctly`() {
        val subscriber = TestSubscriber<ReactiveMapEvent.Remove<Long, String>>()
        map = ReactiveMap(hashMapOf(testPairs[0], testPairs[1]))
        map.observeRemove().subscribe(subscriber)

        //Assert KEY-BASED remove(...) invokes correct remove event
        map.remove(testPairs[0].first)
        subscriber.assertNoErrors()
        subscriber.assertValueCount(1)
        subscriber.assertValueAt(0) { removeEvent ->
            val (firstKey, firstValue) = testPairs[0]
            removeEvent.key == firstKey && removeEvent.value == firstValue
        }

        val (secondKey, secondValue) = testPairs[1]
        //Assert KEY-VALUE-BASED remove(...) invokes correct remove event
        map.remove(secondKey, secondValue)
        subscriber.assertNoErrors()
        subscriber.assertValueCount(2)
        subscriber.assertValueAt(1) { removeEvent ->
            removeEvent.key == secondKey && removeEvent.value == secondValue
        }
    }

    @Test
    fun `test observeReplace() handles replace event correctly`() {
        val subscriber = TestSubscriber<ReactiveMapEvent.Replace<Long, String>>()
        map = ReactiveMap(hashMapOf(testPairs[0]))
        map.observeReplace().subscribe(subscriber)

        val (firstKey, firstValue) = testPairs[0]
        val (_, secondValue) = testPairs[1]

        //Assert entry replacement invokes proper remove event
        map[firstKey] = secondValue
        subscriber.assertNoErrors()
        subscriber.assertValueCount(1)
        subscriber.assertValueAt(0) { replaceEvent ->
            replaceEvent.key == firstKey && replaceEvent.value == secondValue && replaceEvent.oldValue == firstValue
        }
    }
}