package pl.lewica.lewicapl.android.ui.editorial

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import pl.lewica.lewicapl.android.data.model.Editor
import pl.lewica.lewicapl.android.ui.common.BrandedTopBar

@Composable
fun EditorialTeamScreen(navController: NavController) {
  val viewModel: EditorsViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsState()

  Scaffold(
    topBar = { BrandedTopBar(title = "redakcja", onBack = { navController.popBackStack() }) },
    contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp)
  ) { padding ->
    when (val state = uiState) {
      is EditorsUiState.Loading -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(padding),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator()
        }
      }
      is EditorsUiState.Failed -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(padding),
          contentAlignment = Alignment.Center
        ) {
          Text(text = state.message)
        }
      }
      is EditorsUiState.Ready -> {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
          items(state.editors) { editor ->
            EditorRow(editor = editor)
          }
        }
      }
    }
  }
}

@Composable
private fun EditorRow(editor: Editor) {
  ListItem(
    headlineContent = { Text(editor.name) },
    supportingContent = {
      Column {
        Text(text = editor.bio)
        if (editor.blogTitle != null) {
          Text(text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
              append("Blog: ")
            }
            append(editor.blogTitle)
          })
        }
      }
    },
    leadingContent = {
      Icon(Icons.Default.People, contentDescription = null)
    }
  )
}
