import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import org.example.split.*
import org.junit.jupiter.api.Assertions

class Tests {

    private fun assertFileContent(name: String, expectedContent: String) {
        val file = File(name)
        val content = file.readLines().joinToString("\n")
        assertEquals(expectedContent, content)
    }

    @Test
    fun test1() {
        main(arrayOf("-d", "-l", "4", "-o", "-", "testedFile"))

            assertFileContent(
            "files\\testedFile1.txt",
            """Понедельник
Вторник
Среда
Четверг"""
        )
        assertFileContent(
            "files\\testedFile2.txt",
            """Пятница
Суббота
Воскресенье"""
        )
        File("files\\testedFile1.txt").delete()
        File("files\\testedFile2.txt").delete()
    }

    @Test
    fun test2() {
        main(arrayOf("-c", "28", "-o", "newestFile", "testedFile"))

        assertFileContent(
            "files\\newestFileac.txt",
            """Воскресенье"""
        )
        File("files\\newestFileaa.txt").delete()
        File("files\\newestFileab.txt").delete()
        File("files\\newestFileac.txt").delete()
    }

    @Test
    fun test3() {
        main(arrayOf("-d", "-n", "5", "testedFile"))

        assertFileContent(
            "files\\x4.txt",
            """ятница
Суббо"""
        )
        File("files\\x1.txt").delete()
        File("files\\x2.txt").delete()
        File("files\\x3.txt").delete()
        File("files\\x4.txt").delete()
        File("files\\x5.txt").delete()
    }

    @Test
    fun testExceptions() {
        Assertions.assertThrows(Exception::class.java) {
            main(arrayOf("-d", "-l", "3", "-n", "200", "testedFile"))
        }
        Assertions.assertThrows(java.lang.Exception::class.java) {
            main(arrayOf("-d", "-l", "4", "testedFileses"))
        }
    }
}