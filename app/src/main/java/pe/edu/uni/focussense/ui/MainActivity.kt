package pe.edu.uni.focussense.ui

import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import pe.edu.uni.focussense.context.ContextManager
import pe.edu.uni.focussense.model.AdaptationDecision
import pe.edu.uni.focussense.model.ContextSnapshot
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var contextManager: ContextManager

    private lateinit var root: LinearLayout
    private lateinit var modeLabel: TextView
    private lateinit var timerText: TextView
    private lateinit var startButton: Button
    private lateinit var resetButton: Button
    private lateinit var luxText: TextView
    private lateinit var batteryText: TextView
    private lateinit var chargingText: TextView
    private lateinit var sensorText: TextView
    private lateinit var explanationText: TextView
    private lateinit var tipsCard: LinearLayout
    private lateinit var contextCard: LinearLayout
    private lateinit var adaptationCard: LinearLayout

    private val handler = Handler(Looper.getMainLooper())
    private var remainingSeconds = 25 * 60
    private var timerRunning = false
    private var currentDecision: AdaptationDecision? = null
    private var pulseAnimator: ObjectAnimator? = null

    private val timerTick = object : Runnable {
        override fun run() {
            if (!timerRunning) return
            if (remainingSeconds > 0) {
                remainingSeconds--
                updateTimerText()
                handler.postDelayed(this, 1000)
            } else {
                timerRunning = false
                startButton.text = "Iniciar"
                stopPulse()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contextManager = ContextManager(this)
        buildUi()
        updateTimerText()
    }

    override fun onResume() {
        super.onResume()
        contextManager.start { snapshot, decision ->
            runOnUiThread {
                renderContext(snapshot, decision)
            }
        }
    }

    override fun onPause() {
        contextManager.stop()
        handler.removeCallbacks(timerTick)
        if (timerRunning) {
            timerRunning = false
            startButton.text = "Continuar"
        }
        stopPulse()
        super.onPause()
    }

    private fun buildUi() {
        val scroll = ScrollView(this)
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(22), dp(22), dp(34))
        }
        scroll.addView(root)

        root.addView(text("FocusSense", 30f, true).apply {
            contentDescription = "Nombre de la aplicación FocusSense"
        })
        root.addView(text("Temporizador de estudio que adapta su interfaz al entorno y al estado del dispositivo.", 15f, false).apply {
            setPadding(0, dp(4), 0, dp(16))
        })

        modeLabel = text("Esperando contexto...", 14f, true).apply {
            gravity = Gravity.CENTER
            setPadding(dp(14), dp(10), dp(14), dp(10))
        }
        root.addView(modeLabel, matchWrap(top = 2, bottom = 14))

        timerText = text("25:00", 54f, true).apply {
            gravity = Gravity.CENTER
            typeface = Typeface.MONOSPACE
            setPadding(0, dp(20), 0, dp(20))
        }
        root.addView(timerText, matchWrap(bottom = 10))

        val buttonRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        startButton = Button(this).apply {
            text = "Iniciar"
            isAllCaps = false
            setOnClickListener { toggleTimer() }
        }
        resetButton = Button(this).apply {
            text = "Reiniciar"
            isAllCaps = false
            setOnClickListener { resetTimer() }
        }
        buttonRow.addView(startButton, LinearLayout.LayoutParams(0, dp(52), 1f).apply { marginEnd = dp(7) })
        buttonRow.addView(resetButton, LinearLayout.LayoutParams(0, dp(52), 1f).apply { marginStart = dp(7) })
        root.addView(buttonRow, matchWrap(bottom = 18))

        contextCard = card()
        contextCard.addView(sectionTitle("Contexto detectado"))
        luxText = text("Luminosidad: --", 16f, false)
        batteryText = text("Batería: --", 16f, false)
        chargingText = text("Estado de carga: --", 16f, false)
        sensorText = text("Sensor de luz: --", 13f, false)
        contextCard.addView(luxText)
        contextCard.addView(batteryText)
        contextCard.addView(chargingText)
        contextCard.addView(sensorText)
        root.addView(contextCard, matchWrap(bottom = 14))

        adaptationCard = card()
        adaptationCard.addView(sectionTitle("Decisión automática"))
        explanationText = text("Esperando cambios del contexto.", 15f, false)
        adaptationCard.addView(explanationText)
        root.addView(adaptationCard, matchWrap(bottom = 14))

        tipsCard = card()
        tipsCard.addView(sectionTitle("Consejo de enfoque"))
        tipsCard.addView(text("Mantén el teléfono a una distancia cómoda y usa bloques de 25 minutos con pausas cortas.", 15f, false))
        root.addView(tipsCard, matchWrap(bottom = 14))

        root.addView(text("Pipeline: CONTEXTO → PROCESAMIENTO → DECISIÓN → ADAPTACIÓN", 12f, true).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(6), 0, 0)
        })

        setContentView(scroll)
        applyPalette(Palette.normal())
    }

    private fun renderContext(snapshot: ContextSnapshot, decision: AdaptationDecision) {
        currentDecision = decision

        luxText.text = if (snapshot.lux == null) {
            "Luminosidad: no disponible"
        } else {
            "Luminosidad: ${String.format(Locale.US, "%.1f", snapshot.lux)} lux"
        }
        batteryText.text = "Batería: ${snapshot.batteryLevel}%"
        chargingText.text = "Estado de carga: ${if (snapshot.isCharging) "cargando" else "sin cargar"}"
        sensorText.text = if (contextManager.hasLightSensor) {
            "Sensor de luz: activo"
        } else {
            "Sensor de luz: no disponible (la adaptación por batería sigue activa)"
        }

        modeLabel.text = decision.title
        explanationText.text = decision.explanation
        tipsCard.visibility = if (decision.showTips) View.VISIBLE else View.GONE

        val palette = when {
            decision.useDarkPalette -> Palette.dark()
            decision.highContrast -> Palette.outdoor()
            else -> Palette.normal()
        }
        applyPalette(palette)
        refreshPulse()
    }

    private fun applyPalette(p: Palette) {
        root.setBackgroundColor(p.background)
        window.statusBarColor = p.background
        window.navigationBarColor = p.background

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val appearance = if (p.darkStatusIcons) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0
            window.insetsController?.setSystemBarsAppearance(
                appearance,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        setAllTextColors(root, p.text)
        listOf(contextCard, adaptationCard, tipsCard).forEach {
            it.background = roundedDrawable(p.card, 18f)
        }
        modeLabel.background = roundedDrawable(p.accentSoft, 999f)
        modeLabel.setTextColor(p.accentText)

        startButton.background = roundedDrawable(p.accent, 14f)
        startButton.setTextColor(p.buttonText)
        resetButton.background = roundedDrawable(p.secondaryButton, 14f)
        resetButton.setTextColor(p.text)
    }

    private fun setAllTextColors(view: View, color: Int) {
        when (view) {
            is TextView -> if (view !== modeLabel && view !is Button) view.setTextColor(color)
            is LinearLayout -> for (i in 0 until view.childCount) setAllTextColors(view.getChildAt(i), color)
        }
    }

    private fun toggleTimer() {
        timerRunning = !timerRunning
        startButton.text = if (timerRunning) "Pausar" else "Continuar"
        handler.removeCallbacks(timerTick)
        if (timerRunning) {
            handler.postDelayed(timerTick, 1000)
        }
        refreshPulse()
    }

    private fun resetTimer() {
        timerRunning = false
        remainingSeconds = 25 * 60
        startButton.text = "Iniciar"
        handler.removeCallbacks(timerTick)
        stopPulse()
        updateTimerText()
    }

    private fun updateTimerText() {
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        timerText.text = String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    private fun refreshPulse() {
        val shouldAnimate = timerRunning && (currentDecision?.animationsEnabled != false)
        if (shouldAnimate) startPulse() else stopPulse()
    }

    private fun startPulse() {
        if (pulseAnimator?.isRunning == true) return
        pulseAnimator = ObjectAnimator.ofFloat(timerText, View.ALPHA, 1f, 0.65f, 1f).apply {
            duration = 1500
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
    }

    private fun stopPulse() {
        pulseAnimator?.cancel()
        pulseAnimator = null
        if (::timerText.isInitialized) timerText.alpha = 1f
    }

    private fun card(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(18), dp(16), dp(18), dp(16))
        elevation = dp(2).toFloat()
    }

    private fun sectionTitle(value: String): TextView = text(value, 18f, true).apply {
        setPadding(0, 0, 0, dp(8))
    }

    private fun text(value: String, sizeSp: Float, bold: Boolean): TextView = TextView(this).apply {
        text = value
        textSize = sizeSp
        if (bold) setTypeface(typeface, Typeface.BOLD)
        setLineSpacing(0f, 1.12f)
    }

    private fun matchWrap(top: Int = 0, bottom: Int = 0): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(top)
            bottomMargin = dp(bottom)
        }

    private fun roundedDrawable(color: Int, radiusDp: Float): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(color)
        cornerRadius = dp(radiusDp.toInt()).toFloat()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private data class Palette(
        val background: Int,
        val card: Int,
        val text: Int,
        val accent: Int,
        val accentSoft: Int,
        val accentText: Int,
        val secondaryButton: Int,
        val buttonText: Int,
        val darkStatusIcons: Boolean
    ) {
        companion object {
            fun normal() = Palette(
                background = Color.rgb(244, 247, 251),
                card = Color.WHITE,
                text = Color.rgb(23, 30, 45),
                accent = Color.rgb(79, 70, 229),
                accentSoft = Color.rgb(231, 229, 255),
                accentText = Color.rgb(55, 48, 163),
                secondaryButton = Color.rgb(229, 231, 235),
                buttonText = Color.WHITE,
                darkStatusIcons = true
            )

            fun dark() = Palette(
                background = Color.rgb(17, 24, 39),
                card = Color.rgb(31, 41, 55),
                text = Color.rgb(249, 250, 251),
                accent = Color.rgb(129, 140, 248),
                accentSoft = Color.rgb(49, 46, 129),
                accentText = Color.rgb(238, 242, 255),
                secondaryButton = Color.rgb(55, 65, 81),
                buttonText = Color.rgb(17, 24, 39),
                darkStatusIcons = false
            )

            fun outdoor() = Palette(
                background = Color.WHITE,
                card = Color.rgb(255, 247, 214),
                text = Color.BLACK,
                accent = Color.rgb(0, 0, 0),
                accentSoft = Color.rgb(255, 226, 121),
                accentText = Color.BLACK,
                secondaryButton = Color.rgb(241, 245, 249),
                buttonText = Color.WHITE,
                darkStatusIcons = true
            )
        }
    }
}
