import android.os.Build
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import com.example.sensorviewapp.model.Comfort
import com.example.sensorviewapp.model.Measure
import com.example.sensorviewapp.model.Sensor
import com.example.sensorviewapp.ui.screens.SensorsAvailable
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class PredictionUiState (
    var selectedSensor: String = SensorsAvailable.TMPD251_1.value,
    var predictions: List<Measure>? = null,
    var losses: List<Measure>? = null
)