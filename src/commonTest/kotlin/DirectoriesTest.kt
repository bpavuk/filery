import com.bpavuk.filery.exceptions.NotFileException
import com.bpavuk.filery.filery
import com.bpavuk.filery.types.FileType
import com.bpavuk.filery.types.Path
import com.bpavuk.testing.assertThrowing
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class DirectoriesTest {
    @Test
    fun testDirectories() = runTest {
        filery("/home/bpavuk") {
            assertTrue(create(Path("/home/bpavuk/creature.txt")))
            go(Path("/home/bpavuk/creature.txt"))
            write("fuckery goes here and there")
            delete()
            assertTrue { create(Path("/home/bpavuk/fuckeryDir"), FileType.DIRECTORY) }
            go("/home/bpavuk/fuckeryDir/")
            assertThrowing<NotFileException> {
                write("this must fail")
            }
            delete()
        }
    }
}