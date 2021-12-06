package com.example.casttotv.ui.fragments.audios

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.databinding.FragmentAudioPlayerBinding
import com.example.casttotv.models.FileModel
import com.example.casttotv.utils.MySingleton
import com.example.casttotv.utils.MySingleton.enablingWiFiDisplay
import com.example.casttotv.utils.MySingleton.playingFileCurrentPos
import com.example.casttotv.utils.MySingleton.playingFileModel
import com.example.casttotv.utils.MySingleton.playingFileName
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class AudioPlayerFragment : Fragment() {

    private val TAG = "AudioPlayerFragment"
    private lateinit var binding: FragmentAudioPlayerBinding

    private val sharedViewModel: SharedViewModel by activityViewModels()

    //    private val videosList: MutableList<String> = ArrayList()
    private val audiosModelList: MutableList<FileModel> = ArrayList()
    private var repeat = false
    private var mCountDownTimer: CountDownTimer? = null

    private var mTimeLeftInMillis: Long = 0
    private var mTimeNextInMillis: Double = 0.0
    private var locked = false

    var audioSpeed = 100

    private var mediaPlayer: MediaPlayer? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAudioPlayerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            thisFragment = this@AudioPlayerFragment

        }

        observer()
        initFun()
    }

    @SuppressLint("SetTextI18n")
    private fun observer() {
        CoroutineScope(Dispatchers.IO).launch {
            sharedViewModel.audiosByBucket(MySingleton.folder_path).collect {
                CoroutineScope(Dispatchers.Main).launch {
                    if (!it.isNullOrEmpty()) {
                        audiosModelList.clear()

                        audiosModelList.addAll(it)
                    } else {
                        requireContext().toastLong("videos not found.")
                    }
                }
            }
        }

        sharedViewModel.speed.observe(viewLifecycleOwner, {
            audioSpeed = it
            binding.textviewSpeed.text = audioSpeed.toString()
            binding.imageViewPlayBackSpeed.text = String.format("%.1fx", audioSpeed / 100f)
            playingFileCurrentPos = mediaPlayer!!.currentPosition

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayer!!.playbackParams = PlaybackParams().setSpeed(audioSpeed / 100f)
            }

            playAudio()

        })
    }


    private fun initFun() {
        binding.textViewFileName.text = playingFileName

        playAudio(playingFileModel)

        mediaPlayer!!.setOnCompletionListener {
            if (repeat) {
                playingFileCurrentPos = 0
                playAudio(playingFileModel)
            } else {
                binding.imageViewPlayPause.setImageResource(R.drawable.ic_play_circle)
                binding.imageviewPlayPauseMain.setImageResource(R.drawable.ic_play_circle)
                playingFileCurrentPos = 0
            }

        }


    }

    fun lock() {
        if (binding.clBellowControls.isVisible) {
            binding.clBellowControls.visibility = View.GONE
            binding.clMiddle.visibility = View.GONE
            binding.clHeader.visibility = View.GONE
            binding.imageviewLockOpen.visibility = View.VISIBLE
            locked = true
        } else {
            binding.clBellowControls.visibility = View.VISIBLE
            binding.clMiddle.visibility = View.VISIBLE
            binding.clHeader.visibility = View.VISIBLE
            binding.imageviewLockOpen.visibility = View.GONE
            locked = false
        }
    }

    fun forward() {
        val currentProg = binding.seekbar.progress
        val currentMax = binding.seekbar.max
        if (currentProg < currentMax - 5) {
            mediaPlayer!!.seekTo(((currentProg.times(1000)) + 5000))
            pauseTimer()
            startTimer()
        }
    }

    fun replay() {
        val currentProg = binding.seekbar.progress
        if (currentProg > 5) {
            mediaPlayer!!.seekTo(((currentProg.times(1000)) - 5000))
            pauseTimer()
            startTimer()
        }

    }

    private fun setTime(milliseconds: Long) {
        mTimeLeftInMillis = milliseconds
        resetTimer()
    }

    private fun startTimer() {
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val mCurrentPosition: Int = mediaPlayer!!.currentPosition / 1000
                binding.seekbar.progress = mCurrentPosition
            }

            override fun onFinish() {

            }
        }.start()
    }

    private fun pauseTimer() {
        mCountDownTimer!!.cancel()
    }

    private fun restartTimer() {
        pauseTimer()
        mCountDownTimer!!.start()
    }


    private fun resetTimer() {
        mTimeNextInMillis = 0.0
        updateCountDownText()
    }

    private fun updateCountDownText() {
        val hours = (mTimeLeftInMillis / 1000).toInt() / 3600
        val minutes = (mTimeLeftInMillis / 1000 % 3600).toInt() / 60
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60

        val timeLeftFormatted: String = if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }

        val rounded = mTimeNextInMillis.roundToInt()

        val secondsEnd = ((rounded % 86400) % 3600) % 60
        val minutesEnd = ((rounded % 86400) % 3600) / 60
        val hoursEnd = ((rounded % 86400) / 3600)
        val timeNextFormatted: String = if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hoursEnd, minutesEnd, secondsEnd)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutesEnd, secondsEnd)
        }

        binding.mTextviewEndTime.text = timeNextFormatted
        binding.mTextviewStartTime.text = timeLeftFormatted
    }


    @SuppressLint("SourceLockedOrientationActivity")
    fun orientation() {

        val orientation: Int = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

    }

    fun repeat() {
        if (repeat) {
            binding.imageViewRepeat.setImageResource(R.drawable.ic_repeat)
        } else {
            binding.imageViewRepeat.setImageResource(R.drawable.ic_repeat_one)
        }
        repeat = !repeat

    }

    fun showSpeedMenu() {
        if (binding.clSpeed.isVisible) {
            binding.clSpeed.visibility = View.GONE
        } else {
            binding.clSpeed.visibility = View.VISIBLE
        }
    }

    fun increaseSpeed(plus: Boolean) {
        if (plus) {
            if (audioSpeed < 400) {
                audioSpeed += 10
                sharedViewModel.adjustPlayerSpeed(audioSpeed)

            } else {
                requireContext().toastLong("max speed is 4.0x")
            }
        } else {
            if (audioSpeed > 10) {
                audioSpeed -= 10
                sharedViewModel.adjustPlayerSpeed(audioSpeed)

            } else {
                requireContext().toastLong("min speed is 0.25x")
            }
        }

    }

    private fun initSeekbar() {
        binding.seekbar.max = playingFileModel.duration.div(1000).toInt()
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, prog: Int, userSeek: Boolean) {
                if (userSeek) {
                    mediaPlayer!!.seekTo(prog.times(1000))
                } else {
                    val dur = playingFileModel.duration
                    mTimeLeftInMillis = dur - mediaPlayer!!.currentPosition
                    mTimeNextInMillis = (mediaPlayer!!.currentPosition / 1000).toDouble()
                    updateCountDownText()
                }

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                mediaPlayer!!.seekTo(p0!!.progress.times(1000))

            }

        })
    }


    fun playPauseVideo() {
        binding.clSpeed.visibility = View.GONE
        if (!locked) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                binding.imageViewPlayPause.setImageResource(R.drawable.ic_play_circle)
                binding.imageviewPlayPauseMain.setImageResource(R.drawable.ic_play_circle)
                binding.imageviewPlayPauseMain.visibility = View.VISIBLE
                pauseTimer()
            } else {
                binding.imageviewPlayPauseMain.visibility = View.GONE
                mediaPlayer!!.start()
                restartTimer()
                binding.imageViewPlayPause.setImageResource(R.drawable.ic_pause_circle)
            }

        }

    }

    private fun pauseAudio() {
        pauseTimer()
        if (mediaPlayer!!.isPlaying) {
            binding.imageviewPlayPauseMain.visibility = View.VISIBLE
            mediaPlayer!!.pause()
            binding.imageViewPlayPause.setImageResource(R.drawable.ic_play_circle)
            binding.imageviewPlayPauseMain.setImageResource(R.drawable.ic_play_circle)
            playingFileCurrentPos = mediaPlayer!!.currentPosition
        }
    }


    fun playNextAudio() {
        if (audiosModelList.size == audiosModelList.indexOf(playingFileModel).plus(1)) {
            requireContext().toastLong("no more video found")
        } else {
            binding.textViewFileName.text = getNextModel().fileName
            playingFileModel = getNextModel()
//            initFun()
            playAudio(playingFileModel)
        }
    }

    fun playPreviousVideo() {
        if (audiosModelList.indexOf(playingFileModel).minus(1) <= -1) {
            requireContext().toastLong("no previous video found")
        } else {
            binding.textViewFileName.text = getPreviousModel().fileName
            playingFileModel = getPreviousModel()
            playAudio(playingFileModel)
        }
    }

    private fun playAudio(model: FileModel) {
        setTime(model.duration)
        startTimer()
        binding.imageviewPlayPauseMain.visibility = View.GONE
        binding.imageViewPlayPause.setImageResource(R.drawable.ic_pause_circle)
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
         }
        try {
            val myUri: Uri = model.filePath.toUri() // initialize Uri here
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(requireContext(), myUri)
                prepare()
                start()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayer!!.playbackParams = PlaybackParams().setSpeed(audioSpeed / 100f)
            }
        } catch (e: Exception) {
            e.stackTrace
            Log.e(TAG, e.message.toString())
        }
         initSeekbar()

    }

    private fun playAudio() {
        restartTimer()
        binding.imageviewPlayPauseMain.visibility = View.GONE
        binding.imageViewPlayPause.setImageResource(R.drawable.ic_pause_circle)
        mediaPlayer!!.seekTo(playingFileCurrentPos)
    }


    private fun getNextModel() =
        audiosModelList[audiosModelList.indexOf(playingFileModel).plus(1)]

    private fun getPreviousModel() =
        audiosModelList[audiosModelList.indexOf(playingFileModel).minus(1)]


    fun back() {
        findNavController().navigate(R.id.action_audioPlayerFragment_to_audiosFragment)
    }

    /* Ask to connect screen mirror (cast)
    * */
    fun enableWiFiDisplay() {
        requireContext().enablingWiFiDisplay()
    }

    override fun onResume() {
        super.onResume()
        playAudio()
    }

    override fun onPause() {
        super.onPause()
        pauseAudio()
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.adjustPlayerSpeed(100)
    }
}