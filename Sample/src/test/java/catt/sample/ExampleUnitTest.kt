package catt.sample

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private val list: List<String> by lazy { mutableListOf("A", "B", "C", "D", "E", "F", "G") }


    @Test
    fun addition_isCorrect() {

        for(index in list.indices.reversed()){
            println("index = ${list[index]}")
        }
    }
}
