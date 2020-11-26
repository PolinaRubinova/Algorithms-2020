package lesson4

import java.util.*


/**
 * Префиксное дерево для строк
 */
class KtTrie : AbstractMutableSet<String>(), MutableSet<String> {

    private class Node {
        val children: MutableMap<Char, Node> = linkedMapOf()
    }

    private var root = Node()

    override var size: Int = 0
        private set

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + 0.toChar()

    private fun findNode(element: String): Node? {
        var current = root
        for (char in element) {
            current = current.children[char] ?: return null
        }
        return current
    }

    override fun contains(element: String): Boolean =
        findNode(element.withZero()) != null

    override fun add(element: String): Boolean {
        var current = root
        var modified = false
        for (char in element.withZero()) {
            val child = current.children[char]
            if (child != null) {
                current = child
            } else {
                modified = true
                val newChild = Node()
                current.children[char] = newChild
                current = newChild
            }
        }
        if (modified) {
            size++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        val current = findNode(element) ?: return false
        if (current.children.remove(0.toChar()) != null) {
            size--
            return true
        }
        return false
    }

    /**
     * Итератор для префиксного дерева
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Сложная
     */
    override fun iterator(): MutableIterator<String> = TrieIterator()

    inner class TrieIterator internal constructor() : MutableIterator<String> {

        private var stack = Stack<String>()
        private var nextStr = ""

        //Кладем все слова в стек
        private fun inOrderIterator(str: String, node: Node) {
            for ((key, value) in node.children) {
                if (key == 0.toChar()) {
                    //если конец слова -> пушим в стек
                    //и переходим к следующему потомку
                    stack.push(str)
                } else {
                    //доходим до конца слова
                    inOrderIterator(str + key, value)
                }
            }
        }

        init {
            inOrderIterator(nextStr, root)
        }

        // T = O(1)
        // R = O(1)
        override fun hasNext(): Boolean = stack.isNotEmpty()

        // T = O(1)
        // R = O(1)
        override fun next(): String {
            if (stack.isEmpty()) throw NoSuchElementException()
            nextStr = stack.pop()
            return nextStr
        }

        // T = O(log n)
        // R = O(1)
        override fun remove() {
            if (nextStr == "") throw IllegalStateException()
            remove(nextStr)
            nextStr = ""
        }
    }
}