package pl.lewica.lewicapl.android.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshContainer(
  refreshing: Boolean,
  onRefresh: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  PullToRefreshBox(
    isRefreshing = refreshing,
    onRefresh = onRefresh,
    modifier = modifier
  ) {
    content()
  }
}
