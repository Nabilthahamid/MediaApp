package com.example.mediaapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ImageScaleFragment : Fragment() {

    private var executor = Executors.newSingleThreadExecutor()
    private var download: Future<*>? = null

    // A public demo image endpoint. Replace with your own HTTPS image URL
    // if the service is unavailable on your network.
    private val imageUrl = "https://picsum.photos/800/600"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (executor.isShutdown) {
            executor = Executors.newSingleThreadExecutor()
        }

        val root = requireContext().screenColumn()
        val status = root.addLabel("Load an image, then pinch to zoom.")

        val image = ZoomImageView(requireContext())

        root.addView(
            image,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val loadButton = root.addButton("Load / Retry") {}
        root.addButton("Reset zoom") {
            image.resetZoom()
        }

        fun loadImage() {
            loadButton.isEnabled = false
            status.text = "Loading image..."

            download = executor.submit {
                val result = runCatching {
                    val connection =
                        URL(imageUrl).openConnection() as HttpURLConnection

                    try {
                        connection.connectTimeout = 15_000
                        connection.readTimeout = 15_000
                        connection.instanceFollowRedirects = true

                        check(connection.responseCode in 200..299) {
                            "Server returned ${connection.responseCode}"
                        }

                        connection.inputStream.use { stream ->
                            BitmapFactory.decodeStream(stream)
                                ?: error("The response was not a supported image")
                        }
                    } finally {
                        connection.disconnect()
                    }
                }

                root.post {
                    // Ignore results belonging to a screen that was removed.
                    if (this@ImageScaleFragment.view !== root) {
                        return@post
                    }

                    loadButton.isEnabled = true

                    result.onSuccess { bitmap ->
                        image.setImageBitmap(bitmap)
                        image.resetZoom()
                        status.text = "Pinch with two fingers to zoom."
                    }.onFailure {
                        status.text =
                            "Could not load image. Check your internet and tap Retry."
                    }
                }
            }
        }

        loadButton.setOnClickListener {
            loadImage()
        }

        // Wait until the fragment's view has been installed.
        root.post {
            if (this@ImageScaleFragment.view === root) {
                loadImage()
            }
        }

        return root
    }

    override fun onDestroyView() {
        download?.cancel(true)
        download = null
        executor.shutdownNow()

        super.onDestroyView()
    }
}
