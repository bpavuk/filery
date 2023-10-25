import com.bpavuk.filery.filery
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadTest {
    @Test
    fun testRead() = runTest {
        filery(
            path = "/home/bpavuk/fuckery.txt",
            createFileOnAbsence = true
        ) {
            write("fuckery\n")
        }
        filery("/home/bpavuk/fuckery.txt") {
            assertEquals(readLine(cutLineEscape = true), "fuckery")
        }
        filery("/home/bpavuk/fuckery.txt") {
            assertEquals(readBytes(3).decodeToString(), "fuc")
            assertEquals(readLine(), "kery")
        }
        filery("/home/bpavuk/fuckery.txt") {
            assertEquals(readBytes().toList(), "fuckery\n".encodeToByteArray().toList())
        }
    }
}