package com.example.mediaapp

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun Context.dp(value: Int): Int =
    (value * resources.displayMetrics.density).toInt()

fun Context.screenColumn(): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(20), dp(20), dp(20), dp(20))
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }
}

fun LinearLayout.addLabel(
    text: String,
    size: Float = 18f
): TextView {
    val label = TextView(context).apply {
        this.text = text
        textSize = size
        setPadding(0, context.dp(8), 0, context.dp(12))
    }

    addView(
        label,
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    )

    return label
}

fun LinearLayout.addButton(
    text: String,
    action: () -> Unit
): Button {
    val button = Button(context).apply {
        this.text = text
        setOnClickListener { action() }
    }

    addView(
        button,
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    )

    return button
}

fun applySystemInsets(view: View) {
    val left = view.paddingLeft
    val top = view.paddingTop
    val right = view.paddingRight
    val bottom = view.paddingBottom

    ViewCompat.setOnApplyWindowInsetsListener(view) { target, insets ->
        val safe = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or
                    WindowInsetsCompat.Type.ime()
        )

        target.setPadding(
            left + safe.left,
            top + safe.top,
            right + safe.right,
            bottom + safe.bottom
        )

        insets
    }

    ViewCompat.requestApplyInsets(view)
}

fun AppCompatActivity.showScreen(title: String): LinearLayout {
    enableEdgeToEdge()
    val root = screenColumn()
    setContentView(root)
    applySystemInsets(root)

    root.addButton("Back") { finish() }
    root.addLabel(title, 24f)

    return root
}
