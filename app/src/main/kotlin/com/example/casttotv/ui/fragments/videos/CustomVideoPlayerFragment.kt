package com.example.casttotv.ui.fragments.videos

import android.annotation.SuppressLint
import android.media.PlaybackParams
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.SeekBar
import android.widget.VideoView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.databinding.FragmentCustomVideoPlayerBinding
import com.example.casttotv.dataclasses.FileModel
import com.example.casttotv.utils.*
import com.example.casttotv.utils.MySingleton.enablingWiFiDisplay
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.viewmodel.SharedViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class CustomVideoPlayerFragment : Fragment() {

    private lateinit var binding: FragmentCustomVideoPlayerBinding

    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModel.SharedViewModelFactory(requireContext())
    }

    private val videosModelList: MutableList<FileModel> = ArrayList()
    private var repeat = false
    private var mCountDownTimer: CountDownTimer? = null

    private var playingVideoCurrentPos: Int = 0
    private var playingVideoCurrentPosBeforeDestroy: Int = 0
    private var mStartTimeInMillis: Long = 0
    private var mTimeLeftInMillis: Long = 0
    private var mTimeNextInMillis: Double = 0.0
    private var locked = false

    private var videoSpeed = 100
    private var cropState = 0
    private var firstHeight = 0
    private var firstWeight = 0
    lateinit var videoPlayer: VideoView


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
            videoPlayer = videoView

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
                    }
                }
            }
        }
        sharedViewModel.wifiConnection.observe(viewLifecycleOwner) {
            if (requireContext().getPrefs(AUTO_STOP, false)) {
                if (!it) {

                    if (videoPlayer.isPlaying) {
                        playPauseVideo()
                    }
                }
            }
        }
        sharedViewModel.mTimeLeftInMillis.observe(viewLifecycleOwner, {
            mTimeLeftInMillis = it
        })
        sharedViewModel.playingVideoCurrentPos.observe(viewLifecycleOwner, {
            playingVideoCurrentPos = it
        })
        sharedViewModel.playingVideoCurrentPosBeforeDestroy.observe(viewLifecycleOwner, {
            playingVideoCurrentPosBeforeDestroy = it
        })

        sharedViewModel.speed.observe(viewLifecycleOwner, {
            videoSpeed = it
            sharedViewModel.setPlayingVideoCurrentPos(videoPlayer.currentPosition)
            videoPlayer.setOnPreparedListener { mp ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mp.playbackParams = PlaybackParams().setSpeed(videoSpeed / 100f)
                }
            }
            playVideo(playingFileModel)
        })
    }


    private fun initFun() {
        binding.textviewFileName.text = playingFileName

        videoPlayer.setOnCompletionListener {
            if (repeat) {
                sharedViewModel.setPlayingVideoCurrentPos(0)
                playVideo(playingFileModel)
            } else {
                binding.imageViewPlayPause.setImageResource(R.drawable.ic_play_circle)
                binding.imageviewPlayPauseMain.setImageResource(R.drawable.ic_play_circle)
                sharedViewModel.setPlayingVideoCurrentPos(0)
            }

        }

        initSeekbar()

    }

    fun lock() {
        if (binding.clBellowControls.isVisible) {
            binding.clBellowControls.visibility = View.GONE
            binding.clMiddle.visibility = View.GONE
            binding.clHeader.visibility = View.GONE
            binding.clSeekbar.visibility = View.GONE
            binding.imageviewLock2.visibility = View.VISIBLE
            binding.clLockCrop?.visibility = View.GONE
            locked = true

        } else {
            binding.clSeekbar.visibility = View.VISIBLE
            binding.clBellowControls.visibility = View.VISIBLE
            binding.clMiddle.visibility = View.VISIBLE
            binding.clHeader.visibility = View.VISIBLE
            binding.clLockCrop?.visibility = View.VISIBLE
            binding.imageviewLock2.visibility = View.GONE
            locked = false
        }
    }

    fun forward() {
        val currentProg = binding.seekbar.progress
        val currentMax = binding.seekbar.max
        if (currentProg < currentMax - 5) {
            videoPlayer.seekTo(((currentProg.times(1000)) + 5000))
            pauseTimer()
            startTimer()
        }
    }

    fun replay() {
        val currentProg = binding.seekbar.progress
        if (currentProg > 5) {
            videoPlayer.seekTo(((currentProg.times(1000)) - 5000))
            pauseTimer()
            startTimer()
        }

    }

    fun resize() {
        // Adjust the size of the video
        // so it fits on the screen
        val screenWidth = binding.llcVideoView.width

//        val videoProportion = getVideoProportion()
//        val screenProportion = screenHeight.toFloat() / screenWidth.toFloat()
        val lp: ViewGroup.LayoutParams = binding.llcVideoView.layoutParams

        when (cropState) {
            0 -> {
                firstHeight = lp.height
                firstWeight = lp.width
                val screenHeight = binding.llcVideoView.height
                lp.height = screenHeight / 2
                lp.width = screenWidth

                cropState++
            }
            1 -> {
                val screenHeight = binding.llcVideoView.height * 2
                lp.height = (screenHeight / 3)
                lp.width = screenWidth

                cropState++
            }
            2 -> {
                val screenHeight = binding.llcVideoView.height * 3
                lp.height = screenHeight / 4
                lp.width = screenWidth
                cropState++
            }
            3 -> {
                val screenHeight = binding.llcVideoView.height * 4
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

    private fun setTime(milliseconds: Long) {
        mStartTimeInMillis = milliseconds
        resetTimer()
    }

    private fun startTimer() {
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 100) {
            override fun onTick(millisUntilFinished: Long) {
                binding.seekbar.progress = videoPlayer.currentPosition / 1000
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
        sharedViewModel.setTimeLeftInMillis(mStartTimeInMillis)
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
        sharedViewModel.setOrientation(requireActivity())
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
                videoPlayer.pause()
                sharedViewModel.adjustPlayerSpeed(videoSpeed)

            }
        } else {
            if (videoSpeed > 10) {
                videoSpeed -= 10
                videoPlayer.pause()
                sharedViewModel.adjustPlayerSpeed(videoSpeed)
            }
        }
        sharedViewModel.playingVideoCurrentPosBeforeDestroy(videoPlayer.currentPosition)

    }

    private fun initSeekbar() {
        binding.seekbar.max = playingFileModel.duration.div(1000).toInt()
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, prog: Int, userSeek: Boolean) {
                if (userSeek) {
                    videoPlayer.seekTo(prog.times(1000))
                } else {
                    val dur = playingFileModel.duration
                    sharedViewModel.setTimeLeftInMillis(dur - videoPlayer.currentPosition)
                    mTimeNextInMillis = (videoPlayer.currentPosition / 1000).toDouble()
                    updateCountDownText()
                }

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                videoPlayer.seekTo(p0!!.progress.times(1000))

            }

        })
    }


    fun playPauseVideo() {

        binding.clSpeed.visibility = View.GONE
        if (!locked) {
            if (videoPlayer.isPlaying) {
                videoPlayer.pause()
                binding.imageViewPlayPause.setImageResource(R.drawable.ic_play_circle)
                binding.imageviewPlayPauseMain.setImageResource(R.drawable.ic_play_circle)
                binding.imageviewPlayPauseMain.visibility = View.VISIBLE
                pauseTimer()
            } else {
                binding.imageviewPlayPauseMain.visibility = View.GONE
                videoPlayer.start()
                restartTimer()
                binding.imageViewPlayPause.setImageResource(R.drawable.ic_pause
                )/*pause*/
            }

        }

    }

    private fun pauseVideo() {
        pauseTimer()
        if (videoPlayer.isPlaying) {
            binding.imageviewPlayPauseMain.visibility = View.VISIBLE
            videoPlayer.pause()
            binding.imageViewPlayPause.setImageResource(R.drawable.ic_play_circle)
            binding.imageviewPlayPauseMain.setImageResource(R.drawable.ic_play_circle)
            sharedViewModel.setPlayingVideoCurrentPos(videoPlayer.currentPosition)
            sharedViewModel.playingVideoCurrentPosBeforeDestroy(videoPlayer.currentPosition)
        }
    }


    fun playNextVideo() {
        if (videosModelList.size == videosModelList.indexOf(playingFileModel).plus(1)) {
            requireContext().toastLong("no more video found")
        } else {
            binding.textviewFileName.text = getNextModel().fileName
            playVideo(getNextModel())
        }
    }

    fun playPreviousVideo() {
        if (videosModelList.indexOf(playingFileModel).minus(1) <= -1) {
            requireContext().toastLong("no previous video found")
        } else {
            binding.textviewFileName.text = getPreviousModel().fileName
            playVideo(getPreviousModel())
        }
    }

    private fun playVideo(model: FileModel) {
        setTime(model.duration)
        startTimer()
        playingFileModel = model
        binding.imageviewPlayPauseMain.visibility = View.GONE
        binding.imageViewPlayPause.setImageResource(R.drawable.ic_pause)/*pause*/
        videoPlayer.setVideoPath(model.filePath)
        videoPlayer.start()
        videoPlayer.seekTo(playingVideoCurrentPosBeforeDestroy)
//        val eq = Equalizer(0, videoPlayer.audioSessionId)

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
        playVideo(playingFileModel)
        if (requireContext().getPrefs(AUTO_ROTATION, false)) {
            sharedViewModel.setActivityOrientation(requireActivity())
            binding.imageViewRotate.visibility = View.GONE
        } else {
            binding.imageViewRotate.visibility = View.VISIBLE
        }

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