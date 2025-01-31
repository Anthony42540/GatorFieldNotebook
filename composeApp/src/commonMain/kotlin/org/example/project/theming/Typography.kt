import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import gatorfieldnotebook.composeapp.generated.resources.Res
import gatorfieldnotebook.composeapp.generated.resources.khand_bold
import gatorfieldnotebook.composeapp.generated.resources.khand_light
import gatorfieldnotebook.composeapp.generated.resources.khand_medium
import gatorfieldnotebook.composeapp.generated.resources.khand_regular
import gatorfieldnotebook.composeapp.generated.resources.khand_semibold

@Composable
fun KhandFontFamily() = FontFamily(
    Font(Res.font.khand_bold, weight = FontWeight.Bold),
    Font(Res.font.khand_light, weight = FontWeight.Light),
    Font(Res.font.khand_medium, weight = FontWeight.Medium),
    Font(Res.font.khand_regular, weight = FontWeight.Normal),
    Font(Res.font.khand_semibold, weight = FontWeight.SemiBold)
)
