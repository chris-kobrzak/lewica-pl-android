package pl.lewica.lewicapl.android.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.lewica.lewicapl.android.R

private val antonFont = FontFamily(Font(R.font.anton_regular))

@Composable
fun BrandedTopBar(
  title: String? = null,
  onBack: (() -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {}
) {
  Surface(
    color = Color(0xFFFF0000),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
      ) {
        if (onBack != null) {
          IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
          ) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }
        }

        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp),
          contentAlignment = Alignment.Center
        ) {
          if (title != null) {
            Text(
              text = title,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              textAlign = TextAlign.Center
            )
          } else {
            Text(
              text = "lewica.pl",
              color = Color.White,
              fontFamily = antonFont,
              fontSize = 28.sp,
              textAlign = TextAlign.Center
            )
          }
        }

        Row(
          modifier = Modifier.align(Alignment.CenterEnd),
          verticalAlignment = Alignment.CenterVertically,
          content = actions
        )
      }
    }
  }
}
