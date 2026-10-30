package com.example.mediaapp

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment

class VideoFragment : Fragment() {

    private var videoView: VideoView? = null
    private var position = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        position = savedInstanceState?.getInt("position") ?: position

        val root = requireContext().screenColumn()
        val status = root.addLabel("Tap Play to start the video.")

        val video = VideoView(requireContext())
        videoView = video

        root.addView(
            video,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val controller = MediaController(requireContext())
        controller.setAnchorView(video)
        video.setMediaController(controller)

        val play = root.addButton("Play") {
            video.start()
        }

        val pause = root.addButton("Pause") {
            video.pause()
        }

        play.isEnabled = false
        pause.isEnabled = false

        video.setOnPreparedListener {
            video.seekTo(position)
            play.isEnabled = true
            pause.isEnabled = true
            status.text = "Video ready."
        }

        video.setOnCompletionListener {
            position = 0
            video.seekTo(0)
            status.text = "Finished. Tap Play to watch again."
        }

        video.setOnErrorListener { _, _, _ ->
            status.text = "Could not play this video. Check the MP4 format."
            play.isEnabled = false
            pause.isEnabled = false
            true
        }

        val uri = Uri.parse(
            "android.resource://${requireContext().packageName}/" +
                    R.raw.sample_video
        )

        video.setVideoURI(uri)

        return root
    }

    override fun onPause() {
        videoView?.let {
            position = it.currentPosition
            it.pause()
        }

        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(
            "position",
            videoView?.currentPosition ?: position
        )
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        videoView?.stopPlayback()
        videoView = null
        super.onDestroyView()
    }
}