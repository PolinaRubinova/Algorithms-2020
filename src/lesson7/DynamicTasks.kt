@file:Suppress("UNUSED_PARAMETER")

package lesson7


/**
 * Наибольшая общая подпоследовательность.
 * Средняя
 *
 * Дано две строки, например "nematode knowledge" и "empty bottle".
 * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
 * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
 * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
 * Если общей подпоследовательности нет, вернуть пустую строку.
 * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
 * При сравнении подстрок, регистр символов *имеет* значение.
 */

//T = O(first.length * second.length)
//R = O(first.length * second.length)

fun longestCommonSubSequence(first: String, second: String): String {
    if (first.isEmpty() || second.isEmpty()) return ""

    var longest = ""
    var max = Pair(0, (0 to 0))
    val counter = MutableList(first.length + 1) { IntArray(second.length + 1) }

    for (i in 0..first.length) {
        for (j in 0..second.length) {
            counter[i][j] = when {
                i == 0 || j == 0 -> 0
                first[i - 1] == second[j - 1] -> counter[i - 1][j - 1] + 1
                else -> maxOf(counter[i][j - 1], counter[i - 1][j])
            }
            if (counter[i][j] > max.first) max = Pair(counter[i][j], (i to j))
        }
    }

    var count = max.first
    var i = max.second.first
    var j = max.second.second

    while (i > 0 && j > 0 && count != 0) {
        when {
            counter[i][j] == counter[i - 1][j] -> j++
            counter[i][j - 1] == counter[i][j] -> i++
            else -> {
                longest = first[i - 1] + longest
                count--
            }
        }
        i--
        j--
    }
    return longest
}

/**
 * Наибольшая возрастающая подпоследовательность
 * Сложная
 *
 * Дан список целых чисел, например, [2 8 5 9 12 6].
 * Найти в нём самую длинную возрастающую подпоследовательность.
 * Элементы подпоследовательности не обязаны идти подряд,
 * но должны быть расположены в исходном списке в том же порядке.
 * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
 * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
 * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
 */

//T = O(first.length * second.length)
//R = O(first.length * second.length)

fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    if (list.isEmpty()) return list

    val sortedList = list.sorted()
    val size = list.size
    val result = mutableSetOf<Int>()
    var max = Pair(0, (0 to 0))
    val counter = MutableList(size + 1) { IntArray(size + 1) }

    for (i in 0..size) {
        for (j in 0..size) {
            counter[i][j] = when {
                i == 0 || j == 0 -> 0
                list[i - 1] == sortedList[j - 1] -> counter[i - 1][j - 1] + 1
                else -> maxOf(counter[i][j - 1], counter[i - 1][j])
            }
            if (counter[i][j] > max.first) max = Pair(counter[i][j], (i to j))
        }
    }

    var count = max.first
    var i = max.second.first
    var j = max.second.second

    while (i > 0 && j > 0 && count != 0) {
        when {
            counter[i][j] == counter[i - 1][j] -> j++
            counter[i][j - 1] == counter[i][j] -> i++
            else -> {
                result.add(list[i - 1])
                count--
            }
        }
        i--
        j--
    }
    return result.toList().asReversed()
}

/**
 * Самый короткий маршрут на прямоугольном поле.
 * Средняя
 *
 * В файле с именем inputName задано прямоугольное поле:
 *
 * 0 2 3 2 4 1
 * 1 5 3 4 6 2
 * 2 6 2 5 1 3
 * 1 4 3 2 6 2
 * 4 2 3 1 5 0
 *
 * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
 * В каждой клетке записано некоторое натуральное число или нуль.
 * Необходимо попасть из верхней левой клетки в правую нижнюю.
 * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
 * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
 *
 * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
 */
fun shortestPathOnField(inputName: String): Int {
    TODO()
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5