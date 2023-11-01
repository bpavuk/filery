
import com.bpavuk.filery.filery
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteTest {
    @Test
    fun testWrite() = runTest {
        val secretWords = listOf("filery", "fuckery")
        filery(
            path = "/home/bpavuk/fileryTest.txt",
            createFileOnAbsence = true
        ) {
            write(secretWords[0])
            val result = readText()
            assertEquals(secretWords[0], result)
        }
        filery(
            path = "/home/bpavuk/fileryTest.txt",
            createFileOnAbsence = true
        ) {
            append(secretWords[1])
            val result = readText()
            assertEquals(secretWords.joinToString(separator = ""), result)
            delete()
        }
    }
}