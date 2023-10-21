import com.bpavuk.filery.filery
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

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
            assertTrue {
                readLine(cutLineEscape = true) == "fuckery"
            }
        }
        filery("/home/bpavuk/fuckery.txt") {
            assertTrue {
                readBytes(3).decodeToString() == "fuc"
            }
            assertTrue { readLine() == "kery" }
        }
        filery("/home/bpavuk/fuckery.txt") {
            assertTrue {
                readBytes().decodeToString() == "fuckery\n"
            }
        }
    }
}