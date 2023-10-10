import com.bpavuk.filery.filery
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ReadTest {
    @Test
    fun testRead() = runTest {
        filery("/home/bpavuk/fuckery.txt") {
            assertTrue {
                readLine(cutLineEscape = false) == "fuckery\n"
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