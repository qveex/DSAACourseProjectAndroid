package dataStructures

import java.util.NoSuchElementException

class LinkedList<T> {
    private var firstInList: Item<T>? = null
    private var lastInList: Item<T>? = null
    private var size = 0

    fun size(): Int { return size }

    fun isEmpty(): Boolean { return size == 0 }

    operator fun contains(o: Any): Boolean { return indexOf(o) != -1 }

    fun iterator(): ListIterator<T> { return ElementsIterator(0) }

    fun toArray(): Array<T> {
        val array: Array<T> = arrayOfNulls<Any>(size) as Array<T>
        var i = 0
        for (element in ElementsIterator(0)) {
            array[i] = element
            i++
        }

        return array
    }

    fun <T1> toArray(a: Array<T1>): Array<T1> {
        if (a == null) throw NullPointerException()
        return if (a.size >= size) {
            System.arraycopy(this.toArray(), 0, a, 0, size)
            a
        } else {
            val newArr = java.lang.reflect.Array.newInstance(a.javaClass.componentType, size) as Array<T1>
            System.arraycopy(this.toArray(), 0, newArr, 0, size)
            newArr
        }
    }

    fun add(newElement: T): Boolean {
        if (size == 0) {
            firstInList = Item(newElement, null, null)
            lastInList = firstInList
        } else {
            lastInList!!.nextItem = Item(newElement, lastInList, null)
            lastInList = lastInList!!.nextItem
        }
        size++
        return true
    }

    fun add(index: Int, element: T) { throw UnsupportedOperationException() }               // DO IT

    fun remove(o: Any): Boolean {
        if (firstInList!!.element == o) {
            remove(firstInList!!)
            return true
        } else if (lastInList!!.element == o) {
            remove(lastInList!!)
            return true
        } else {
            var el = firstInList!!.nextItem
            while (el != null) {
                if (el.element == o) {
                    remove(el)
                    return true
                }
                el = el.nextItem
            }
        }
        return false
    }

    @Throws(IndexOutOfBoundsException::class)
    fun remove(index: Int): T {
        var tmp: T? = null
        if (index > size || index < 0) throw IndexOutOfBoundsException() else if (index == 0) {
            tmp = firstInList!!.element
            remove(firstInList!!)
        } else if (index == size - 1) {
            tmp = lastInList!!.element
            remove(lastInList!!)
        } else {
            var i = 0
            var el = firstInList
            while (i < size) {
                if (i == index) {
                    tmp = el!!.element
                    remove(el)
                    break
                }
                i++
                el = el!!.nextItem
            }
        }
        return tmp!!
    }

    private fun remove(current: Item<T>) {
        if (current === firstInList) {
            if (firstInList!!.nextItem != null)
                firstInList!!.nextItem!!.prevItem = null
            firstInList = firstInList!!.nextItem
        } else if (current === lastInList) {
            lastInList!!.prevItem!!.nextItem = null
            lastInList = lastInList!!.prevItem
        } else {
            current!!.prevItem!!.nextItem = current.nextItem
            current.nextItem!!.prevItem = current.prevItem

            current.prevItem = null
            current.nextItem = null
        }
        size--
    }

    fun containsAll(c: Collection<*>): Boolean {
        for (item in c) if (!this.contains(item!!)) return false
        return true
    }

    fun addAll(c: Collection<T>): Boolean {
        for (item in c) add(item)
        return true
    }

    fun addAll(index: Int, elements: Collection<*>?): Boolean {
        throw UnsupportedOperationException()
    }

    fun removeAll(c: Collection<*>): Boolean {
        for (item in c) remove(item!!)
        return true
    }

    fun retainAll(c: Collection<*>): Boolean {
        for (element in ElementsIterator(0))
            if (!c.contains(element))
                remove(element.toString())
        return true
    }

    fun clear() {
        var el = firstInList
        while (el != null) {
            val next = el.nextItem!!
            el.nextItem = null
            el.prevItem = null
            //el.element = null
            el = next
        }
        size = 0
    }

    //fun subList(start: Int, end: Int): MutableList<T> {
        //return null
    //}

    fun listIterator(): ListIterator<T> {
        return ElementsIterator(0)
    }

    fun listIterator(index: Int): ListIterator<T> {
        return ElementsIterator(index)
    }

    fun lastIndexOf(target: Any): Int {
        throw UnsupportedOperationException()
    }

    fun indexOf(o: Any): Int {
        var i = 0
        var el = firstInList
        while (i < size) {
            if (el!!.element === o) return i
            i++
            el = el!!.nextItem
        }
        return -1
    }

    fun set(index: Int, element: T): T {
        if (index > size || index < 0) throw IndexOutOfBoundsException()
        var temp: T? = null
        var old: Item<T>? = null
        old = getItemByIndex(index)
        temp = old!!.element
        old.element = element
        return temp
    }

    fun get(index: Int): T {
        if (index > size || index < 0) throw IndexOutOfBoundsException()
        return if (index == 0) firstInList!!.element else if (index == size - 1) lastInList!!.element else {
            getItemByIndex(index)!!.element
        }
    }

    private fun getItemByIndex(index: Int): Item<T>? {
        var i = 0
        var el = firstInList
        while (i < size) {
            if (i == index) return el
            i++
            el = el!!.nextItem
        }
        return null
    }

    private inner class ElementsIterator internal constructor(private var index: Int) : ListIterator<T> {
        private var currentItemInIterator: Item<T>?
        private var lastReturnedItemFromIterator: Item<T>?

        override fun hasNext(): Boolean { return index < size }

        override fun next(): T {
            if (hasNext()) {
                lastReturnedItemFromIterator = currentItemInIterator
                currentItemInIterator = currentItemInIterator!!.nextItem
                index++
                return lastReturnedItemFromIterator!!.element
            }
            throw NoSuchElementException()
        }

        override fun hasPrevious(): Boolean { return index > 0 }

        override fun previous(): T {
            if (hasPrevious()) {
                currentItemInIterator =
                    if (currentItemInIterator == null) lastInList else currentItemInIterator!!.prevItem
                lastReturnedItemFromIterator = currentItemInIterator
                index--
                return lastReturnedItemFromIterator!!.element
            }
            throw NoSuchElementException()
        }

        fun add(element: T) { throw UnsupportedOperationException() }

        fun set(element: T) {
            if (lastReturnedItemFromIterator != null) lastReturnedItemFromIterator!!.element =
                element else throw IllegalStateException()
        }

        override fun previousIndex(): Int { return index - 1 }

        override fun nextIndex(): Int { return index }

        fun remove() {
            if (lastReturnedItemFromIterator != null) {
                val lastCur = lastReturnedItemFromIterator!!.nextItem
                this@LinkedList.remove(lastReturnedItemFromIterator!!)
                if (currentItemInIterator === lastReturnedItemFromIterator) currentItemInIterator = lastCur else index--
                lastReturnedItemFromIterator = null
            } else throw IllegalStateException()
        }

        init {
            currentItemInIterator = getItemByIndex(index)
            lastReturnedItemFromIterator = null
        }
    }

    private class Item<T> internal constructor(var element: T, var prevItem: Item<T>?, var nextItem: Item<T>?)
}