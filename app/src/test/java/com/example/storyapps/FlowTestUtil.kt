import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object FlowTestUtil {
    fun <T> getOrAwaitValue(flow: Flow<T>): T = runBlocking {
        flow.first()
    }
}
