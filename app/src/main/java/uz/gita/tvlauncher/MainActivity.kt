
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import uz.gita.tvlauncher.ui.theme.TVLauncherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TVLauncherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppListScreen()
                }
            }
        }
    }
}

@Composable
fun AppListScreen() {
    val context = LocalContext.current
    var apps by remember { mutableStateOf<List<ResolveInfo>>(emptyList()) }

    // Load installed apps
    LaunchedEffect(Unit) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val pm = context.packageManager
        val installed = pm.queryIntentActivities(intent, 0)
        apps = installed.sortedBy { it.loadLabel(pm).toString().lowercase() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "📺 My TV Launcher",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(apps) { app ->
                AppItem(app) {
                    val activityInfo = app.activityInfo
                    val launchIntent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_LAUNCHER)
                        setClassName(activityInfo.packageName, activityInfo.name)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    }
                    context.startActivity(launchIntent)
                }
            }
        }
    }
}

@Composable
fun AppItem(app: ResolveInfo, onClick: () -> Unit) {
    val context = LocalContext.current
    val pm = context.packageManager
    val icon = remember { app.loadIcon(pm) }
    val label = remember { app.loadLabel(pm).toString() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .focusable(true)
            .padding(horizontal = 4.dp)
            .wrapContentHeight(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                bitmap = icon.toBitmap().asImageBitmap(),
                contentDescription = label,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
