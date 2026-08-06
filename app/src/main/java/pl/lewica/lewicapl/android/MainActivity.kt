package pl.lewica.lewicapl.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import pl.lewica.lewicapl.android.data.deeplink.DeepLinkDispatcher
import pl.lewica.lewicapl.android.ui.app.LewicaPlTheme
import pl.lewica.lewicapl.android.ui.app.RootComposable

class MainActivity : ComponentActivity() {

  private val deepLinkDispatcher: DeepLinkDispatcher by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      LewicaPlTheme {
        RootComposable()
      }
    }
    handleDeepLink(intent)
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    handleDeepLink(intent)
  }

  private fun handleDeepLink(intent: Intent) {
    val uri = intent.data ?: return
    lifecycleScope.launch { deepLinkDispatcher.handle(uri) }
  }
}
