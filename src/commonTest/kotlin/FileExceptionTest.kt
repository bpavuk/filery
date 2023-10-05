
import com.bpavuk.filery.filery
import com.bpavuk.testing.assertThrowing
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

object FuckeryException : Exception("Some random fuckery happened")

class FileExceptionTest {
    @Test
    fun exceptionTest() = runBlocking {
        assertThrowing<FuckeryException> {
            filery("/home/") {
                throw FuckeryException
            }
        }
        println("Exception test passed")
    }
}