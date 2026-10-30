package com.example.mediaapp

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class AudioFragment : Fragment() {

    private var player: MediaPlayer? = null
    private var status: TextView? = null
    private var position = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        position = savedInstanceState?.getInt("position") ?: position

        val root = requireContext().screenColumn()
        status = root.addLabel("Tap Play to start the audio.")

        root.addButton("Play") {
            if (player == null) {
                player = MediaPlayer.create(
                    requireContext(),
                    R.raw.sample_audio
                )?.apply {
                    seekTo(position)

                    setOnCompletionListener {
                        position = 0
                        it.seekTo(0)
                        status?.text = "Finished. Tap Play to listen again."
                    }

                    setOnErrorListener { failedPlayer, _, _ ->
                        failedPlayer.release()
                        player = null
                        position = 0
                        status?.text = "Playback failed. Try Play again."
                        true
                    }
                }
            }

            val current = player

            if (current == null) {
                status?.text = "Could not open the audio file."
            } else {
                current.start()
                status?.text = "Playing"
            }
        }

        root.addButton("Pause") {
            player?.let {
                if (it.isPlaying) {
                    it.pause()
                }
                position = it.currentPosition
            }

            status?.text = "Paused"
        }

        root.addButton("Stop") {
            releasePlayer()
            position = 0
            status?.text = "Stopped. Play will start from the beginning."
        }

        return root
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    override fun onPause() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
            }
            position = it.currentPosition
        }

        status?.text = "Paused"
        super.onPause()
    }

    override fun onStop() {
        releasePlayer()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(
            "position",
            player?.currentPosition ?: position
        )
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        releasePlayer()
        status = null
        super.onDestroyView()
    }
}