package com.example.casttotv.ui.fragments.videos

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.media.PlaybackParams
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.databinding.FragmentCustomVideoPlayerBinding
import com.example.casttotv.models.FileModel
import com.example.casttotv.utils.MySingleton.enablingWiFiDisplay
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.viewmodel.SharedViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import android.media.audiofx.Equalizer
import com.example.casttotv.utils.*


class CustomVideoPlayerFragment : Fragment() {

    private lateinit var binding: FragmentCustomVideoPlayerBinding

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val videosModelList: MutableList<FileModel> = ArrayList()
    private var repeat = false
    private var mCountDownTimer: CountDownTimer? = null

    private var mStartTimeInMillis: Long = 0
    private var mTimeLeftInMillis: Long = 0
    private var mTimeNextInMillis: Double = 0.0
    private var locked = false

    var videoSpeed = 100
    private var cropState = 0
    var firstHeight = 0
    var firstWeight = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCustomVideoPlayerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            thisFragment = this@CustomVideoPlayerFragment
            viewModel = sharedViewModel
            lifecycleOwner = viewLifecycleOwner

        }

        observer()
        initFun()
    }

    @SuppressLint("SetTextI18n")
    private fun observer() {
        CoroutineScope(Dispatchers.IO).launch {
            sharedViewModel.videosByBucket(folder_path).collect {
                CoroutineScope(Dispatchers.Main).launch {
                    if (!it.isNullOrEmpty()) {
                        videosModelList.clear()

                        videosModelList.addAll(it)
                    } else {
                        requireContext().toastLong("videos not found.")
                    }
                }
            }
        }

        sharedViewModel.speed.observe(viewLifecycleOwner, {
            videoSpeed = it
            playingFileCurrentPos = binding.videoView.currentPosition
            binding.videoView.setVideoPath(playingFileModel.filePath)
            binding.videoView.setOnPreparedListener { mp ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mp.playbackParams = PlaybackParams().setSpeed(videoSpeed / 100f)
                }
            }
            playVideo()

        })
    }


    private fun initFun() {
        binding.textViewFileName.text = playingFileName
        playVideo(playingFileModel)


        binding.videoView.setOnCompletionListener {
            if (repeat) {
                playingFileCurrentPos = 0
                playVideo(playingFileModel)
            } else {
                binding.imageViewPlayPause.setImageResource(R.drawable.ic_play_circle)
                binding.imageviewPlayPauseMain.setImageResource(R.drawable.ic_play_circle)
                playingFileCurrentPos = 0
            }

        }

        initSeekbar()
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
            binding.videoView.seekTo(((currentProg.times(1000)) + 5000))
            pauseTimer()
            startTimer()
        }
    }

    fun replay() {
        val currentProg = binding.seekbar.progress
        if (currentProg > 5) {
            binding.videoView.seekTo(((currentProg.times(1000)) - 5000))
            pauseTimer()
            startTimer()
        }

    }

    fun resize() {
        // Adjust the size of the video
        // so it fits on the screen
        val videoProportion = getVideoProportion()
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels

        val screenProportion = screenHeight.toFloat() / screenWidth.toFloat()
        val lp: ViewGroup.LayoutParams = binding.llcVideoView.layoutParams

        when (cropState) {
            0 -> {
                firstHeight = lp.height
                firstWeight = lp.width

                lp.height = screenHeight / 2
                lp.width = screenWidth
                cropState++
            }
            1 -> {
                lp.height = (screenHeight / 3)
                lp.width = screenWidth
                cropState++
            }
            2 -> {
                lp.height = screenHeight / 4
                lp.width = screenWidth
                cropState++
            }
            3 -> {
                lp.height = (screenHeight / 5)
                lp.width = screenWidth
                cropState++
            }
            4 -> {
                lp.height = firstHeight
                lp.width = firstWeight
                cropState = 0
            }
        }

        binding.llcVideoView.layoutParams = lp
    }

    // This method gets the proportion of the video that you want to display.
    // I already know this ratio since my video is hardcoded, you can get the
    // height and width of your video and appropriately generate  the proportion
    //    as :height/width
    private fun getVideoProportion(): Float {
        return 2.0f
    }

    private fun setTime(milliseconds: Long) {
        mStartTimeInMillis = milliseconds
        resetTimer()
    }

    private fun startTimer() {
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val mCurrentPosition: Int = binding.videoView.currentPosition / 1000
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
        mTimeLeftInMillis = mStartTimeInMillis
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
        if (orientation == ORIENTATION_LANDSCAPE) {
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
            if (videoSpeed < 400) {
                videoSpeed += 10
                binding.videoView.start()
                sharedViewModel.adjustPlayerSpeed(videoSpeed)

            } else {
                requireContext().toastLong("max speed is 4.0x")
            }
        } else {
            if (videoSpeed > 10) {
                videoSpeed -= 10
                binding.videoView.start()
                sharedViewModel.adjustPlayerSpeed(videoSpeed)

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
                    binding.videoView.seekTo(prog.times(1000))
                } else {
                    val dur = playingFileModel.duration
                    mTimeLeftInMillis = dur - binding.videoView.currentPosition
                    mTimeNextInMillis = (binding.videoView.currentPosition / 1000).toDouble()
                    updateCountDownText()
                }

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                binding.videoView.seekTo(p0!!.progress.times(1000))

            }

        })
    }


    fun playPauseVideo() {
        binding.clSpeed.visibility = View.GONE
        if (!locked) {
            if (binding.videoView.isPlaying) {
                binding.videoView.pause()
                binding.imageViewPlayPause.setImageResource(R.drawable.ic_play_circle)
                binding.imageviewPlayPauseMain.setImageResource(R.drawable.ic_play_circle)
                binding.imageviewPlayPauseMain.visibility = View.VISIBLE
                pauseTimer()
            } else {
                binding.imageviewPlayPauseMain.visibility = View.GONE
                binding.videoView.start()
                restartTimer()
                binding.imageViewPlayPause.setImageResource(R.drawable.ic_pause_circle)
            }

        }

    }

    private fun pauseVideo() {
        pauseTimer()
        if (binding.videoView.isPlaying) {
            binding.imageviewPlayPauseMain.visibility = View.VISIBLE
            binding.videoView.pause()
            binding.imageViewPlayPause.setImageResource(R.drawable.ic_play_circle)
            binding.imageviewPlayPauseMain.setImageResource(R.drawable.ic_play_circle)
            playingFileCurrentPos = binding.videoView.currentPosition
        }
    }


    fun playNextVideo() {
        if (videosModelList.size == videosModelList.indexOf(playingFileModel).plus(1)) {
            requireContext().toastLong("no more video found")
        } else {
            binding.textViewFileName.text = getNextModel().fileName
            playVideo(getNextModel())
        }
    }

    fun playPreviousVideo() {
        if (videosModelList.indexOf(playingFileModel).minus(1) <= -1) {
            requireContext().toastLong("no previous video found")
        } else {
            binding.textViewFileName.text = getPreviousModel().fileName
            playVideo(getPreviousModel())
        }
    }

    private fun playVideo(model: FileModel) {
        setTime(model.duration)
        startTimer()
        playingFileModel = model
        binding.imageviewPlayPauseMain.visibility = View.GONE
        binding.imageViewPlayPause.setImageResource(R.drawable.ic_pause_circle)
        binding.videoView.setVideoPath(model.filePath)
        binding.videoView.start()
        val eq = Equalizer(0, binding.videoView.audioSessionId)

    }

    private fun playVideo() {
        restartTimer()
        binding.imageviewPlayPauseMain.visibility = View.GONE
        binding.imageViewPlayPause.setImageResource(R.drawable.ic_pause_circle)
        binding.videoView.seekTo(playingFileCurrentPos)
    }


    private fun getNextModel() =
        videosModelList[videosModelList.indexOf(playingFileModel).plus(1)]

    private fun getPreviousModel() =
        videosModelList[videosModelList.indexOf(playingFileModel).minus(1)]


    fun back() {
        findNavController().navigate(R.id.action_customVideoPlayerFragment_to_videosFragment)
    }

    /* Ask to connect screen mirror (cast)
    * */
    fun enableWiFiDisplay() {
        requireContext().enablingWiFiDisplay()
    }

    override fun onResume() {
        super.onResume()
        playVideo()
    }

    override fun onPause() {
        super.onPause()
        pauseVideo()
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.adjustPlayerSpeed(100)
    }

}