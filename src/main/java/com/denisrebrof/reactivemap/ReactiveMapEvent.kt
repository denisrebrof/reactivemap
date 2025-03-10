package com.denisrebrof.reactivemap

//Migrated from ReactiveDictionary (C#, UniRx Unity framework)
sealed class ReactiveMapEvent<K, V>(open val key: K, open val value: V) {

    override fun toString(): String = "Key:$key Value:$value"

    override fun hashCode(): Int = key.hashCode() xor value.hashCode() shl 2

    override fun equals(other: Any?): Boolean {
        if (other !is ReactiveMapEvent<*, *>)
            return false

        val keysEquals = other.key?.equals(key) ?: false
        val valuesEquals = other.value?.equals(value) ?: false
        return keysEquals && valuesEquals
    }

    data class Add<K, V>(
        override val key: K,
        override val value: V,
    ) : ReactiveMapEvent<K, V>(key, value) {

        override fun hashCode() = super.hashCode()

        override fun equals(other: Any?): Boolean {
            if (other !is Add<*, *>)
                return false

            val keysEquals = other.key?.equals(key) ?: false
            val valuesEquals = other.value?.equals(value) ?: false
            return keysEquals && valuesEquals
        }
    }

    data class Remove<K, V>(
        override val key: K,
        override val value: V,
    ) : ReactiveMapEvent<K, V>(key, value) {

        override fun hashCode() = super.hashCode()

        override fun equals(other: Any?): Boolean {
            if (other !is Remove<*, *>)
                return false

            val keysEquals = other.key?.equals(key) ?: false
            val valuesEquals = other.value?.equals(value) ?: false
            return keysEquals && valuesEquals
        }

    }

    data class Replace<K, V>(
        override val key: K,
        override val value: V,
        val oldValue: V,
    ) : ReactiveMapEvent<K, V>(key, value) {

        override fun toString(): String = super.toString() + " OldValue: $oldValue"

        override fun hashCode(): Int {
            return key.hashCode() xor oldValue.hashCode() shl 2 xor value.hashCode() shr 2
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Replace<*, *>)
                return false

            val keysEquals = other.key?.equals(key) ?: false
            val valuesEquals = other.value?.equals(value) ?: false
            val oldValuesEquals = other.oldValue?.equals(oldValue) ?: false
            return keysEquals && valuesEquals && oldValuesEquals
        }
    }
}
