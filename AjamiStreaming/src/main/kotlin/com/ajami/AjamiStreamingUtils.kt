import com.lagradost.cloudstream3.mvvm.logError
import java.text.SimpleDateFormat
import java.util.Locale
import java.lang.Throwable

fun isUpcoming(dateString: String?): Boolean {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateTime = dateString?.let { format.parse(it)?.time } ?: return false
        unixTimeMS < dateTime
    } catch (t: Throwable) {
        logError(t)
        false
    }
}
