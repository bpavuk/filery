import com.bpavuk.filery.filery
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
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
            append(secretWords[1].encodeToByteArray())
            val result = readText()
            assertEquals(secretWords.joinToString(separator = ""), result)
            delete()
        }
    }
}