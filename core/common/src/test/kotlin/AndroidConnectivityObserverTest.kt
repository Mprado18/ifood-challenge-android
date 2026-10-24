import android.content.Context
import android.net.ConnectivityManager
import androidx.test.core.app.ApplicationProvider
import com.movies.common.AndroidConnectivityObserver
import com.movies.common.DefaultDispatcherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AndroidConnectivityObserverTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `when observing connectivity then current validated state is emitted`() = runBlocking {
        // Given
        val manager = context.getSystemService(ConnectivityManager::class.java)
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        val expected = capabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true

        // When
        val online = AndroidConnectivityObserver(context).observeOnline().first()

        // Then
        assertEquals(expected, online)
    }

    @Test
    fun `when creating default dispatchers then io and default are the shared dispatchers`() {
        // When
        val provider = DefaultDispatcherProvider()

        // Then
        assertNotNull(provider.main)
        assertEquals(Dispatchers.IO, provider.io)
        assertEquals(Dispatchers.Default, provider.default)
    }
}
