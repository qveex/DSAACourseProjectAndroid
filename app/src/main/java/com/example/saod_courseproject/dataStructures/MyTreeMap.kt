package dataStructures

import java.util.NoSuchElementException

class MyTreeMap <K: Comparable<K>, V> : MutableMap<K, V> {

    private var root: Entry<K, V>? = null
    override var size = 0

    override fun containsKey(key: K): Boolean = findKey(key) != null

    override fun containsValue(value: V): Boolean = findValue(value)

    override fun get(key: K): V? = findKey(key)?.value

    fun get(index: Int): V {
        val it = TreeIterator(root)
        return it.arrayOfElements[index]!!.value
    }

    override fun isEmpty(): Boolean = size == 0

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            val set = HashSet<Entry<K, V>>()
            val it = TreeIterator(root)
            it.forEach { set.add(it) }
            return set.toMutableSet()
        }
    override val keys: MutableSet<K>
        get() {
            val set = HashSet<K>()
            val it = TreeIterator(root)
            it.forEach { set.add(it.key) }
            return set
        }
    override val values: MutableCollection<V>
        get() {
            val set = HashSet<V>()
            val it = TreeIterator(root)
            it.forEach { set.add(it.value) }
            return set
        }

    override fun clear() {
        root = null
        size = 0
    }

    override fun put(key: K, value: V): V? {
        val oldValue = get(key)
        root = findAndPut(key, value, root)
        return oldValue
    }


    override fun putAll(from: Map<out K, V>) {
        from.forEach{ put(it.key, it.value) }
    }

    override fun remove(key: K): V? {
        val oldValue = get(key)
        root = findAndRemove(key, root)
        return oldValue
    }

    fun print() {
        root?.print()
    }

    private fun iterator(): TreeIterator<K, V> {
        return TreeIterator(root)
    }

    private fun findKey(key: K, e: Entry<K, V>? = root): Entry<K, V>? = when {
        e == null -> null
        e.key == key -> e
        e.key < key -> findKey(key, e.left)
        e.key > key -> findKey(key, e.right)
        else -> null
    }
    private fun findValue(value: V, e: Entry<K, V>? = root): Boolean {
        if (e?.value == value) return true
        else {
            if (e?.left != null) if (findValue(value, e.left)) return true
            if (e?.right != null) if (findValue(value, e.right)) return true
        }
        return false
    }

    private fun findAndPut(key: K, value: V, e: Entry<K, V>?): Entry<K, V>? {
        if (e == null) return Entry(key, value).also { size++ }
        if (e.key == key) {
            e.value = value
            return e
        }
        if (key < e.key) e.left = findAndPut(key, value, e.left)
        else e.right = findAndPut(key, value, e.right)
        return e.balance()
    }
    private fun findAndRemove(key: K, e: Entry<K, V>?): Entry<K, V>? {
        when {
            e == null -> return e
            key < e.key -> e.left = findAndRemove(key, e.left)
            key > e.key -> e.right = findAndRemove(key, e.right)
            else -> {
                size--
                when {
                    e.left == null && e.right == null -> return null
                    e.left == null && e.right != null -> return e.right
                    e.left != null && e.right == null -> return e.left
                    e.left != null && e.right != null -> when {
                        e.left!!.height > e.right!!.height -> {
                            val max: Entry<K, V> = e.left!!.maximum()
                            max.left = e.left!!.removeMax()
                            max.right = max.right
                            return max.balance()
                        }
                        e.left!!.height <= e.right!!.height -> {
                            val min: Entry<K, V> = e.right!!.minimal()
                            min.right = e.right!!.removeMin()
                            min.left = e.left
                            return min.balance()
                        }
                    }
                }
            }
        }
        return e.balance()
    }


    private class Entry<K, V>(
            override val key: K,
            override var value: V,
            var height: Int = 0,
            var left: Entry<K, V>? = null,
            var right: Entry<K, V>? = null
    ) : MutableMap.MutableEntry<K, V> {

        override fun setValue(newValue: V): V {
            val oldValue = value
            value = newValue
            return oldValue
        }

        override fun toString(): String {
            return "$key = $value"
        }

        fun print(space: Int = 0) {
            this.right?.print(space + 5)
            println()
            for(i in 0 until space) print(" ")
            println(value)
            this.left?.print(space + 5)
        }

        fun minimal(): Entry<K, V> = this.left?.minimal()?: this
        fun maximum(): Entry<K, V> = this.right?.maximum()?: this

        fun removeMin(): Entry<K, V>? {
            if (this.left == null) return this.right
            this.left = this.left!!.removeMin()
            return this.balance()
        }
        fun removeMax(): Entry<K, V>? {
            if (this.right == null) return this.left
            this.right = this.right!!.removeMax()
            return this.balance()
        }

        private fun balanceFactor() = (this.right?.height ?: 0) - (this.left?.height ?: 0)

        private fun fixHeight() {
            val leftHeight = this.left?.height ?: 0
            val rightHeight = this.right?.height ?: 0
            this.height = 1 + if (leftHeight > rightHeight) leftHeight else  rightHeight
        }
        private fun rotateRight(): Entry<K, V>? {
            val left = this.left
            this.left = left?.right
            left?.right = this
            this.fixHeight()
            left?.fixHeight()
            return left
        }
        private fun rotateLeft(): Entry<K, V>? {
            val right = this.right
            this.right = right?.left
            right?.left = this
            this.fixHeight()
            right?.fixHeight()
            return right
        }
        fun balance(): Entry<K, V>? {
            this.fixHeight()
            if (this.balanceFactor() == 2) {
                if (this.right!!.balanceFactor() < 0)
                    this.right = this.right!!.rotateRight()
                return this.rotateLeft()
            }
            if (this.balanceFactor() == -2) {
                if (this.left!!.balanceFactor() > 0)
                    this.left = this.left!!.rotateLeft()
                return this.rotateRight()
            }
            return this
        }
    }


    private inner class TreeIterator<K, V>(root: Entry<K, V>?) : Iterator<Entry<K, V>> {

        private var current = 0
        private val tree = root
        val arrayOfElements = arrayOfNulls<Entry<K,V>>(size)

        init {
            this@TreeIterator.put(tree)
            current = 0
        }

        fun put(e: Entry<K, V>?) {
            if (e != null) {
                arrayOfElements[current] = e.also { current++ }
                put(e.left)
                put(e.right)
            }
        }

        override fun hasNext() = current < size
        override fun next(): Entry<K, V> { if (hasNext()) return arrayOfElements[current++]!! else throw NoSuchElementException() }
    }
}