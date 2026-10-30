package com.example.mediaapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

private const val ACTION_MESSAGE =
    "com.example.mediaapp.ACTION_MESSAGE"

private const val EXTRA_MESSAGE = "message"

// First screen: select the broadcast type.
class BroadcastFragment : Fragment() {

    private var spinner: Spinner? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = requireContext().screenColumn()

        root.addLabel("Select a broadcast type", 22f)

        val selector = Spinner(requireContext())

        selector.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf(
                "Custom broadcast receiver",
                "System battery notification receiver"
            )
        )

        selector.setSelection(
            savedInstanceState?.getInt("selection") ?: 0
        )

        spinner = selector
        root.addView(selector)

        root.addButton("Proceed") {
            val destination = if (selector.selectedItemPosition == 0) {
                MessageInputActivity::class.java
            } else {
                BatteryActivity::class.java
            }

            startActivity(Intent(requireContext(), destination))
        }

        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("selection", spinner?.selectedItemPosition ?: 0)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        spinner = null
        super.onDestroyView()
    }
}

// Second screen for the custom-broadcast option.
class MessageInputActivity : AppCompatActivity() {

    private lateinit var input: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = showScreen("Enter a message")

        input = EditText(this).apply {
            hint = "Type your message"
            minLines = 2
            inputType =
                android.text.InputType.TYPE_CLASS_TEXT or
                        android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE

            setText(savedInstanceState?.getString("draft").orEmpty())
        }

        root.addView(input)

        root.addButton("Continue") {
            val message = input.text.toString().trim()

            if (message.isEmpty()) {
                input.requestFocus()
                input.error = "Please enter a message"
            } else {
                startActivity(
                    Intent(this, MessageReceiverActivity::class.java)
                        .putExtra(EXTRA_MESSAGE, message)
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("draft", input.text.toString())
        super.onSaveInstanceState(outState)
    }
}

// Third screen: receive the custom broadcast.
class MessageReceiverActivity : AppCompatActivity() {

    private lateinit var output: TextView
    private var registered = false

    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_MESSAGE) {
                val message = intent.getStringExtra(EXTRA_MESSAGE).orEmpty()
                output.text = "Received through BroadcastReceiver:\n\n$message"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = showScreen("Custom broadcast receiver")
        output = root.addLabel("Waiting for broadcast...")

        root.addButton("Send broadcast again") {
            sendMessageBroadcast()
        }
    }

    override fun onStart() {
        super.onStart()

        ContextCompat.registerReceiver(
            this,
            messageReceiver,
            IntentFilter(ACTION_MESSAGE),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        registered = true

        // Send only after registration so the receiver cannot miss it.
        sendMessageBroadcast()
    }

    private fun sendMessageBroadcast() {
        val message = intent.getStringExtra(EXTRA_MESSAGE).orEmpty()

        val broadcast = Intent(ACTION_MESSAGE)
            .setPackage(packageName)
            .putExtra(EXTRA_MESSAGE, message)

        sendBroadcast(broadcast)
    }

    override fun onStop() {
        if (registered) {
            unregisterReceiver(messageReceiver)
            registered = false
        }

        super.onStop()
    }
}

// Second screen for the battery option.
class BatteryActivity : AppCompatActivity() {

    private lateinit var output: TextView
    private var registered = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != Intent.ACTION_BATTERY_CHANGED) return

            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            if (level >= 0 && scale > 0) {
                val percentage = (level * 100f / scale).toInt()
                output.text = "Battery level: $percentage%"
            } else {
                output.text = "Battery information unavailable"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = showScreen("Battery receiver")
        output = root.addLabel("Reading battery level...", 26f)
    }

    override fun onStart() {
        super.onStart()

        ContextCompat.registerReceiver(
            this,
            batteryReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED),
            ContextCompat.RECEIVER_EXPORTED
        )

        registered = true
    }

    override fun onStop() {
        if (registered) {
            unregisterReceiver(batteryReceiver)
            registered = false
        }

        super.onStop()
    }
}
