package lesson3

import java.util.*
import kotlin.math.max

// attention: Comparable is supported but Comparator is not
class KtBinarySearchTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private class Node<T>(
        val value: T
    ) {
        var left: Node<T>? = null
        var right: Node<T>? = null
    }

    private var root: Node<T>? = null

    override var size = 0
        private set

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    /**
     * Добавление элемента в дерево
     *
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     *
     * Спецификация: [java.util.Set.add] (Ctrl+Click по add)
     *
     * Пример
     */
    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    /**
     * Удаление элемента из дерева
     *
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     * (в Котлине тип параметера изменён с Object на тип хранимых в дереве данных)
     *
     * Средняя
     */

    private fun findParent(start: Node<T>, value: T, parent: Node<T>?): Node<T>? {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> parent
            comparison < 0 -> findParent(start.left!!, value, start)
            else -> findParent(start.right!!, value, start)
        }
    }

    private fun remProcess(parent: Node<T>?, element: T, node: Node<T>?) {
        if (parent != null) {
            val comparison = parent.value.compareTo(element)
            if (comparison > 0) {
                parent.left = node
            } else if (comparison < 0) {
                parent.right = node
            }
        } else root = node
    }

    private fun leftSubtree(remNodeRL: Node<T>, remNodeR: Node<T>): Node<T> {
        var rLNode = remNodeRL
        var rLNodeParent = remNodeR
        while (rLNode.left != null) {
            rLNodeParent = rLNode
            rLNode = rLNode.left!!
        }
        rLNodeParent.left = rLNode.right
        return rLNode
    }

    // T = O(log n) в среднем, O(n) в худшем случае
    // R = O(1)
    override fun remove(element: T): Boolean {
        val remNode = find(element)
        if (remNode == null || remNode.value != element) return false
        val parent = root?.let { findParent(it, element, null) }

        when {
            // 1 случай- когда у удаляемого элемента нет детей или нет детей справа,
            // левый ребенок занимает место удаляемого узла
            remNode.right == null -> remProcess(parent, element, remNode.left)

            // 2 случай- когда у правого ребенка нет детей слева,
            // правый ребенок занимает место удаляемого узла
            remNode.right?.left == null -> {
                remNode.right?.left = remNode.left
                remProcess(parent, element, remNode.right)
            }

            // 3 случай- когда у правого ребенка есть дети слева,
            // крайний левый ребенок из правого поддерева занимает место удаляемого узла
            else -> {
                // Левое поддерево родителя становится правым поддеревом крайнего левого узла
                // Дети текущего узла становится детьми крайнего левого
                val rightLeftNode = leftSubtree(remNode.right!!.left!!, remNode.right!!)

                rightLeftNode.left = remNode.left
                rightLeftNode.right = remNode.right

                remProcess(parent, element, rightLeftNode)
            }
        }
        size--
        return true
    }

    override fun comparator(): Comparator<in T>? =
        null

    override fun iterator(): MutableIterator<T> = BinarySearchTreeIterator()

    inner class BinarySearchTreeIterator internal constructor() : MutableIterator<T> {

        private var stack = Stack<Node<T>>()

        private fun inOrderIterator(node: Node<T>?) {
            var r = node
            while (r != null) {
                stack.push(r)
                r = r.left
            }
        }

        init {
            inOrderIterator(root)
        }

        /**
         * Проверка наличия следующего элемента
         *
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         *
         * Спецификация: [java.util.Iterator.hasNext] (Ctrl+Click по hasNext)
         *
         * Средняя
         */

        // T = O(1)
        // R = O(1)
        override fun hasNext(): Boolean = stack.isNotEmpty()

        /**
         * Получение следующего элемента
         *
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         *
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         *
         * Спецификация: [java.util.Iterator.next] (Ctrl+Click по next)
         *
         * Средняя
         */

        // T = O(log n)
        // R = O(1)

        // Вертикальный обратный обход:
        // Из текущего узла «спускаемся» до самого нижнего левого узла,
        // добавляя в стек все посещенные узлы.
        // Если в текущем узле имеется правое поддерево, начинаем следующую
        // итерацию с правого узла. Если правого узла нет, пропускаем шаг
        // со спуском и переходим к обработке следующего узла из стека.
        private var node: Node<T>? = null

        override fun next(): T {
            if (stack.isEmpty()) throw NoSuchElementException()
            node = stack.pop()
            inOrderIterator(node!!.right)
            return node!!.value
        }

        /**
         * Удаление предыдущего элемента
         *
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         *
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         *
         * Спецификация: [java.util.Iterator.remove] (Ctrl+Click по remove)
         *
         * Сложная
         */

        // T = O(log n) в среднем, O(n) в худшем случае
        // R = O(1)
        override fun remove() {
            if (node == null) throw IllegalStateException()
            remove(node!!.value)
            node = null
        }

    }

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.subSet] (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Подмножество всех элементов строго меньше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.headSet] (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun headSet(toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Подмножество всех элементов нестрого больше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.tailSet] (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        TODO()
    }

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }

    override fun height(): Int =
        height(root)

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

}