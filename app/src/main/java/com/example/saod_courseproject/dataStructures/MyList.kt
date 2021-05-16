package dataStructures

import android.util.Log
import data.SIMInfo
import java.util.*

//https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/
class MyList<T> : MutableList<T> {

    private var first: Node<T>? = null
    private var last: Node<T>? = null
    override var size = 0

    private inner class Node<T> constructor(var element: T, var nextItem: Node<T>?)

    constructor()

    constructor(c: Collection<T>) { addAll(c) }

    override fun contains(element: T): Boolean {
        return indexOf(element) != -1
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        elements.forEach { if (!this.contains(it)) return false }
        return true
    }

    override fun get(index: Int): T {
        if (index > size - 1 || index < 0) throw IndexOutOfBoundsException()

        var item = first
        for (i in 0 until size) {
            if (i == index) break
            item = item?.nextItem
        }
        return item!!.element
    }

    private fun getItemByIndex(index: Int): Node<T>? {
        var item = first
        for (i in 0 until size) {
            if (i == index) break
            item = item?.nextItem
        }
        return item
    }

    override fun set(index: Int, element: T): T {
        var item = first
        for (i in 0 until index)
            item = item!!.nextItem
        val oldElement = item!!.element
        item.element = element
        return oldElement;
    }

    override fun add(element: T): Boolean {
        if (size == 0) {
            first = Node(element, first)
            last = first
        } else {
            last!!.nextItem = Node(element, null)
            last = last!!.nextItem
        }
        size++
        return true
    }

    override fun add(index: Int, element: T) {
        if (index > size - 1 || index < 0) throw IndexOutOfBoundsException()

        var item = first
        if (index == 0) {
            val f = Node(element, first)
            first = f
        }
        else if (index == size - 1) {
            val l = Node(element, null)
            last?.nextItem = l
            last = l
        }
        else {
            for (i in 0 until index - 1)
                item = item?.nextItem
            item!!.nextItem = Node(element, item.nextItem)
        }
        size++
    }

    override fun indexOf(element: T): Int {
        var item = first
        for (i in 0 until size) {
            if (item?.element == element) return i
            item = item?.nextItem
        }
        return -1
    }

    override fun isEmpty() = size == 0
    fun isNotEmpty() = size != 0

    override fun iterator(): MutableIterator<T> {
        return ElementsIterator(0)
    }

    override fun lastIndexOf(element: T): Int {
        var item = first
        var lastIndex = -1
        for (i in 0 until size) {
            if (item?.element == element) lastIndex = i
            item = item?.nextItem
        }
        return lastIndex
    }

    override fun listIterator(): MutableListIterator<T> {
        return ElementsIterator(0)
    }

    override fun listIterator(index: Int): MutableListIterator<T> {
        return ElementsIterator(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        val subList = MyList<T>()
        var item = getItemByIndex(toIndex)
        for (i in fromIndex until toIndex) {
            subList.add(item!!.element)
            item = item.nextItem
        }
        return subList
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        var count = index
        elements.forEach{ this.add(count++, it) }
        return true
    }

    override fun addAll(elements: Collection<T>): Boolean {
        elements.forEach{ this.add(it) }
        return true
    }

    override fun remove(element: T): Boolean {
        if (element == first!!.element) {
            first = first!!.nextItem
            size--
            return true
        }

        var e = first
        for (i in 0 until size - 1) {
            if (e!!.nextItem!!.element == element) {
                e.nextItem = e.nextItem!!.nextItem
                if (i == size - 2) last = e
                size--
                return true
            }
            e = e.nextItem
        }
        return false
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        if (elements === this) clear()
        else elements.forEach { this.remove(it) }
        return true
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        removeIf { item: T -> !elements.contains(item) }
        return true
    }

    override fun removeAt(index: Int): T {
        val item = getItemByIndex(index)
        remove(item)
        return item!!.element
    }

    fun sort(list: MyList<SIMInfo>) {
        for (i in 0 until list.size) {
            for (j in i + 1 until list.size)
                if (list[i].simNumber < list[j].simNumber)
                    list[i] = list[j].also { list[j] = list[i] }
        }
    }

    override fun clear() {
        first = null
        last = null
        size = 0
    }

    override fun toString(): String {
        var str = ""
        forEach { str += it.toString() + " "}
        return str
    }

    //https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list-iterator/
    private inner class ElementsIterator constructor(private var index: Int) : MutableListIterator<T> {

        private var currentItemInIterator: Node<T>? = getItemByIndex(index)
        private var lastReturnedItemFromIterator: Node<T>? = null

        override fun hasNext(): Boolean { return index < size }

        override fun hasPrevious(): Boolean { return index > 0 }

        override fun next(): T {
            if (hasNext()) {
                lastReturnedItemFromIterator = currentItemInIterator
                currentItemInIterator = currentItemInIterator!!.nextItem
                index++
                return lastReturnedItemFromIterator!!.element
            }
            throw NoSuchElementException()
        }

        override fun nextIndex(): Int { return index }

        override fun previous(): T { throw UnsupportedOperationException() }

        override fun previousIndex(): Int { return index - 1 }

        override fun add(element: T) {
            if (lastReturnedItemFromIterator === last)
                this@MyList.add(element)
            else
                add(index, element)
            index++
            lastReturnedItemFromIterator = null
        }

        override fun remove() {
            if (lastReturnedItemFromIterator != null) {
                val lastCur = lastReturnedItemFromIterator!!.nextItem
                remove(lastReturnedItemFromIterator)
                if (currentItemInIterator === lastReturnedItemFromIterator) currentItemInIterator = lastCur else index--
                lastReturnedItemFromIterator = null
            } else throw IllegalStateException()
        }

        override fun set(element: T) {
            if (lastReturnedItemFromIterator != null)
                lastReturnedItemFromIterator!!.element = element
            else throw IllegalStateException()
        }
    }
}