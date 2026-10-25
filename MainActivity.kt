package com.example.remotearena

import android.app.Activity
import android.os.Bundle
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ScrollView

class MainActivity : Activity() {
    private lateinit var arena: ArenaView
    private lateinit var status: TextView
    private var espEnabled = true
    private var aimAssistEnabled = true
    private var fov = 120

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(9, 13, 24)
        window.navigationBarColor = Color.rgb(9, 13, 24)
        window.decorView.systemUiVisibility = 0

        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(18), dp(16), dp(16))
            setBackgroundColor(Color.rgb(9, 13, 24))
        }

        val title = TextView(this).apply {
            text = "REMOTE ARENA  •  KOTLIN"
            textSize = 22f
            setTextColor(Color.rgb(89, 245, 190))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        page.addView(title)

        val subtitle = TextView(this).apply {
            text = "Laboratório independente de mira e ESP — alvos fictícios"
            textSize = 12f
            setTextColor(Color.LTGRAY)
            setPadding(0, dp(5), 0, dp(12))
        }
        page.addView(subtitle)

        arena = ArenaView(this)
        page.addView(arena, LinearLayout.LayoutParams(-1, dp(340)))

        status = TextView(this).apply {
            text = "Treino pronto • toque na arena para marcar um alvo"
            textSize = 12f
            setTextColor(Color.WHITE)
            setPadding(0, dp(8), 0, dp(8))
        }
        page.addView(status)

        val controls = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.rgb(20, 28, 44))
                cornerRadius = dp(16).toFloat()
                setStroke(dp(1), Color.rgb(42, 65, 82))
            }
        }

        val toggles = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val espButton = makeButton("ESP: ON")
        val aimButton = makeButton("AIM SIM: ON")
        espButton.setOnClickListener {
            espEnabled = !espEnabled
            espButton.text = if (espEnabled) "ESP: ON" else "ESP: OFF"
            arena.espOn = espEnabled
            arena.invalidate()
        }
        aimButton.setOnClickListener {
            aimAssistEnabled = !aimAssistEnabled
            aimButton.text = if (aimAssistEnabled) "AIM SIM: ON" else "AIM SIM: OFF"
            arena.aimOn = aimAssistEnabled
            arena.invalidate()
        }
        toggles.addView(espButton, LinearLayout.LayoutParams(0, dp(46), 1f).apply { marginEnd = dp(6) })
        toggles.addView(aimButton, LinearLayout.LayoutParams(0, dp(46), 1f).apply { marginStart = dp(6) })
        controls.addView(toggles)

        val fovLabel = TextView(this).apply {
            text = "AIM FOV SIMULADO: $fov°"
            setTextColor(Color.WHITE)
            textSize = 13f
            setPadding(0, dp(12), 0, dp(2))
        }
        controls.addView(fovLabel)
        val fovSeek = SeekBar(this).apply {
            max = 240
            progress = fov
        }
        fovSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                fov = progress.coerceAtLeast(20)
                fovLabel.text = "AIM FOV SIMULADO: $fov°"
                arena.fovValue = fov
                arena.invalidate()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        controls.addView(fovSeek)

        val reset = makeButton("REINICIAR ARENA")
        reset.setOnClickListener {
            arena.resetTargets()
            status.text = "Arena reiniciada • alvos fictícios restaurados"
        }
        controls.addView(reset, LinearLayout.LayoutParams(-1, dp(44)).apply { topMargin = dp(8) })
        page.addView(controls, LinearLayout.LayoutParams(-1, LinearLayout.LayoutParams.WRAP_CONTENT))

        arena.onTargetTapped = { index ->
            status.text = if (aimAssistEnabled) {
                "Alvo fictício ${index + 1} selecionado • assistência apenas visual"
            } else {
                "Alvo fictício ${index + 1} tocado • simulação de mira desligada"
            }
        }

        val scroll = ScrollView(this).apply { isFillViewport = true; addView(page) }
        setContentView(scroll)
    }

    private fun makeButton(label: String) = Button(this).apply {
        text = label
        textSize = 12f
        isAllCaps = false
        setTextColor(Color.rgb(9, 13, 24))
        background = android.graphics.drawable.GradientDrawable().apply {
            setColor(Color.rgb(89, 245, 190))
            cornerRadius = dp(10).toFloat()
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}

private class ArenaView(context: android.content.Context) : View(context) {
    var espOn = true
    var aimOn = true
    var fovValue = 120
    var onTargetTapped: ((Int) -> Unit)? = null
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private var selected = 0
    private val targets = mutableListOf(
        RectF(.16f, .22f, .31f, .46f),
        RectF(.62f, .30f, .77f, .54f),
        RectF(.39f, .58f, .54f, .82f)
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        canvas.drawColor(Color.rgb(12, 20, 31))

        p.style = Paint.Style.STROKE
        p.strokeWidth = dp(1).toFloat()
        p.color = Color.rgb(29, 57, 68)
        for (i in 1..5) canvas.drawLine(w * i / 6, 0f, w * i / 6, h, p)
        for (i in 1..7) canvas.drawLine(0f, h * i / 8, w, h * i / 8, p)

        p.style = Paint.Style.FILL
        p.color = Color.rgb(22, 42, 54)
        canvas.drawRoundRect(w * .05f, h * .08f, w * .95f, h * .92f, dp(14).toFloat(), dp(14).toFloat(), p)

        // Arena props: target rectangles are generated locally; no game data is read.
        targets.forEachIndexed { index, t ->
            val r = RectF(t.left * w, t.top * h, t.right * w, t.bottom * h)
            p.style = Paint.Style.FILL
            p.color = if (index == selected) Color.rgb(60, 95, 105) else Color.rgb(34, 58, 70)
            canvas.drawRoundRect(r, dp(8).toFloat(), dp(8).toFloat(), p)
            p.color = Color.rgb(150, 178, 183)
            canvas.drawCircle(r.centerX(), r.top + r.height() * .22f, r.width() * .17f, p)
            p.color = Color.rgb(70, 100, 111)
            canvas.drawRoundRect(r.left + r.width() * .28f, r.top + r.height() * .42f,
                r.right - r.width() * .28f, r.bottom - r.height() * .08f, dp(7).toFloat(), dp(7).toFloat(), p)

            if (espOn) {
                p.style = Paint.Style.STROKE
                p.strokeWidth = dp(2).toFloat()
                p.color = if (index == selected) Color.rgb(89, 245, 190) else Color.rgb(255, 194, 83)
                canvas.drawRect(r, p)
                p.style = Paint.Style.FILL
                p.textSize = dp(11).toFloat()
                p.typeface = android.graphics.Typeface.DEFAULT_BOLD
                canvas.drawText("BOT ${index + 1} • 100%", r.left, r.top - dp(6), p)
                p.color = Color.rgb(89, 245, 190)
                canvas.drawLine(w * .5f, h * .88f, r.centerX(), r.bottom, p)
            }
        }

        if (aimOn) {
            val cx = w / 2f
            val cy = h / 2f
            val radius = (fovValue.coerceIn(20, 240) / 240f) * minOf(w, h) * .42f
            p.style = Paint.Style.STROKE
            p.strokeWidth = dp(2).toFloat()
            p.color = Color.rgb(89, 245, 190)
            canvas.drawCircle(cx, cy, radius, p)
            canvas.drawLine(cx - dp(12), cy, cx + dp(12), cy, p)
            canvas.drawLine(cx, cy - dp(12), cx, cy + dp(12), p)
        }

        p.style = Paint.Style.FILL
        p.textSize = dp(10).toFloat()
        p.color = Color.LTGRAY
        canvas.drawText("SIMULAÇÃO LOCAL • SEM CONEXÃO AO FREE FIRE", dp(12).toFloat(), h - dp(12).toFloat(), p)
    }

    override fun onTouchEvent(event: android.view.MotionEvent): Boolean {
        if (event.action == android.view.MotionEvent.ACTION_UP) {
            val w = width.toFloat()
            val h = height.toFloat()
            val index = targets.indexOfFirst {
                event.x >= it.left * w && event.x <= it.right * w &&
                event.y >= it.top * h && event.y <= it.bottom * h
            }
            if (index >= 0) {
                selected = index
                invalidate()
                onTargetTapped?.invoke(index)
                return true
            }
        }
        return true
    }

    fun resetTargets() {
        selected = 0
        invalidate()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
