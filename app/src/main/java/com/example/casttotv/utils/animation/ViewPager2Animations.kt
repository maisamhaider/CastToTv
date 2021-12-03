package com.example.casttotv.utils.animation

import android.graphics.Camera
import android.graphics.Matrix
import android.view.View
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.max
import kotlin.math.abs
import kotlin.math.min


class DepthPageTransformer : ViewPager2.PageTransformer {
    private val MIN_SCALE = 0.75F

    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0F
                }
                position <= 0 -> { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    alpha = 1F
                    translationX = 0F
                    scaleX = 1F
                    scaleY = 1F
                }
                position <= 1 -> { // (0,1]
                    // Fade the page out.
                    alpha = 1 - position

                    // Counteract the default slide transition
                    translationX = pageWidth * -position

                    // Scale the page down (between MIN_SCALE and 1)
                    val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position)))
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0F
                }
            }
        }
    }
}


class AccordionTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.pivotX = when {
            position < 0.0F -> 0.0F
            else -> page.width.toFloat()
        }
        page.scaleX = when {
            position < 0.0F -> 1.0F + position
            else -> 1.0F - position
        }
    }

}
class AntiClockSpinTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width

        when {
           abs(position)  < 0.5 -> {
                page.visibility = View.VISIBLE
                page.scaleX = 1 - abs(position)
                page.scaleY = 1 - abs(position)
            }
            abs(position) > 0.5 -> {
                page.visibility = View.GONE
            }
        }

        when {
            position < -1 -> {  // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1F
                page.rotation = 360 * (1 - abs(position))
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1F
                page.rotation = -360 * (1 - abs(position))
            }
            else -> {  // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }
    }

}
class BackDrawTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width
        when {
            position >= -1.0F && position <= 1.0F -> {
                var v: Float
                if (position <= 0.0F) {
                    page.alpha = 1.0F + position
                    page.translationX = pageWidth.toFloat() * -position
                    v = 0.75F + 0.25F * (1.0F - abs(position))
                    page.scaleX = v
                    page.scaleY = v
                } else if (position.toDouble() > 0.5 && position <= 1.0F) {
                    page.alpha = 0.0F
                    page.translationX = pageWidth.toFloat() * -position
                } else if (position.toDouble() > 0.3 && position.toDouble() <= 0.5) {
                    page.alpha = 1.0F
                    page.translationX = pageWidth.toFloat() * position
                    v = 0.75F
                    page.scaleX = v
                    page.scaleY = v
                } else {
                    if (position.toDouble() <= 0.3) {
                        page.alpha = 1.0F
                        page.translationX = pageWidth.toFloat() * position
                        v = (0.3 - position.toDouble()).toFloat()
                        v = min(v, 0.25F)
                        val scaleFactor = 0.75F + v
                        page.scaleX = scaleFactor
                        page.scaleY = scaleFactor
                    }
                }
            }
            else -> {
                page.alpha = 0.0F
            }
        }
    }

}
class BackgroundToForegroundTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val height = page.height.toFloat()
        val width = page.width.toFloat()
        val scale = min(if (position < 0.0F) 1.0F else abs(1.0F - position), 0.5F)
        page.scaleX = scale
        page.scaleY = scale
        page.pivotX = width * 0.5F
        page.pivotY = height * 0.5F
        page.translationX = if (position < 0.0F) width * position else -width * position * 0.25F
    }

    private fun min(`val`: Float, min: Float): Float {
        return max(`val`, min)
    }


}
class ClockSpinTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width

        if (abs(position) <= 0.5) {
            page.visibility = View.VISIBLE
            page.scaleX = 1 - abs(position)
            page.scaleY = 1 - abs(position)
        } else if (abs(position) > 0.5) {
            page.visibility = View.GONE
        }


        if (position < -1) {  // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.alpha = 0F
        } else if (position <= 0) {   // [-1,0]
            page.alpha = 1F
            page.rotation = 360 * abs(position)
        } else if (position <= 1) {   // (0,1]
            page.alpha = 1F
            page.rotation = -360 * abs(position)
        } else {  // (1,+Infinity]
            // This page is way off-screen to the right.
            page.alpha = 0F
        }
    }

}
class CubeInDepthTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.cameraDistance = 20000F

        when {
            position < -1 -> {
                page.alpha = 0F
            }
            position <= 0 -> {
                page.alpha = 1F
                page.pivotX = page.width.toFloat()
                page.rotationY = 90 * abs(position)
            }
            position <= 1 -> {
                page.alpha = 1F
                page.pivotX = 0F
                page.rotationY = -90 * abs(position)
            }
            else -> {
                page.alpha = 0F
            }
        }



        if (abs(position) <= 0.5) {
            page.scaleY = max(.4F, 1 - abs(position))
        } else if (abs(position) <= 1) {
            page.scaleY = max(.4F, 1 - abs(position))
        }
    }

}
class CubeInScalingTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.cameraDistance = 20000F

        when {
            position < -1 -> {     // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1F
                page.pivotX = page.width.toFloat()
                page.rotationY = 90 * abs(position)
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1F
                page.pivotX = 0F
                page.rotationY = -90 * abs(position)
            }
            else -> {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }

        if (abs(position) <= 0.5) {
            page.scaleY = max(.4F, 1 - abs(position))
        } else if (abs(position) <= 1) {
            page.scaleY = max(.4F, abs(position))
        }
    }

}
class CubeInTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.cameraDistance = 20000F

        when {
            position < -1 -> {     // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1F
                page.pivotX = page.width.toFloat()
                page.rotationY = 90 * abs(position)
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1F
                page.pivotX = 0F
                page.rotationY = -90 * abs(position)
            }
            else -> {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }

    }

}
class CubeOutDepthTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        when {
            position < -1 -> {    // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1F
                page.pivotX = page.width.toFloat()
                page.rotationY = -90 * abs(position)
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1F
                page.pivotX = 0F
                page.rotationY = 90 * abs(position)
            }
            else -> {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }



        when {
            abs(position) <= 0.5 -> {
                page.scaleY = max(0.4F, 1 - abs(position))
            }
            abs(position) <= 1 -> {
                page.scaleY = max(0.4F, 1 - abs(position))
            }
        }

    }

}
class CubeOutScalingTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        when {
            position < -1 -> {    // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1F
                page.pivotX = page.width.toFloat()
                page.rotationY = -90 * abs(position)
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1F
                page.pivotX = 0F
                page.rotationY = 90 * abs(position)
            }
            else -> {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }

        when {
            abs(position) <= 0.5 -> {
                page.scaleY = max(0.4F, 1 - abs(position))
            }
            abs(position) <= 1 -> {
                page.scaleY = max(0.4F, abs(position))
            }
        }
    }

}
class CubeOutTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        when {
            position < -1 -> {    // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1F
                page.pivotX = page.width.toFloat()
                page.rotationY = -90 * abs(position)
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1F
                page.pivotX = 0F
                page.rotationY = 90 * abs(position)
            }
            else -> {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }
    }

}
class DepthTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        when {
            position < -1 -> {    // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1F
                page.translationX = 0F
                page.scaleX = 1F
                page.scaleY = 1F
            }
            position <= 1 -> {    // (0,1]
                page.translationX = -position * page.width
                page.alpha = 1 - abs(position)
                page.scaleX = 1 - abs(position)
                page.scaleY = 1 - abs(position)
            }
            else -> {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }
    }

}
class FadeOutTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width

        page.alpha = 1 - abs(position)
    }

}
class FanTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width
        page.pivotX = 0F
        page.pivotY = (page.height / 2).toFloat()
        page.cameraDistance = 20000F

        when {
            position < -1 -> {     // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1F
                page.rotationY = -120 * abs(position)
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1F
                page.rotationY = 120 * abs(position)
            }
            else -> {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }
    }

}
class FidgetSpinTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width

        when {
            abs(position) < 0.5 -> {
                page.visibility = View.VISIBLE
                page.scaleX = 1 - abs(position)
                page.scaleY = 1 - abs(position)
            }
            abs(position) > 0.5 -> {
                page.visibility = View.GONE
            }
        }

        when {
            position < -1 -> {     // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1F
                page.rotation =
                    36000 * (abs(position) * abs(position) * abs(position) * abs(
                        position) * abs(position) * abs(position) * abs(position))
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1F
                page.rotation =
                    -36000 * (abs(position) * abs(position) * abs(position) * abs(
                        position) * abs(position) * abs(position) * abs(position))
            }
            else -> {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }

    }

}
class ForegroundToBackgroundTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val height = page.height.toFloat()
        val width = page.width.toFloat()
        val scale = min(if (position > 0.0F) 1.0F else abs(1.0F + position), 0.5F)
        page.scaleX = scale
        page.scaleY = scale
        page.pivotX = width * 0.5F
        page.pivotY = height * 0.5F
        page.translationX = if (position > 0.0F) width * position else -width * position * 0.25F
    }

    private fun min(`val`: Float, min: Float): Float {
        return max(`val`, min)

    }

}
class GateTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width

        when {
            position < -1 -> {    // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1F
                page.pivotX = 0F
                page.rotationY = 90 * abs(position)
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1F
                page.pivotX = page.width.toFloat()
                page.rotationY = -90 * abs(position)
            }
            else -> {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }
    }

}
class HingeTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width
        page.pivotX = 0F
        page.pivotY = 0F

        when {
            position < -1 -> {    // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 0 -> {    // [-1,0]
                page.rotation = 90 * abs(position)
                page.alpha = 1 - abs(position)
            }
            position <= 1 -> {    // (0,1]
                page.rotation = 0F
                page.alpha = 1F
            }
            else -> {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }
    }

}
class HorizontalFlipTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width
        page.cameraDistance = 12000F

        when {
            position < 0.5 && position > -0.5 -> {
                page.visibility = View.VISIBLE
            }
            else -> {
                page.visibility = View.INVISIBLE
            }
        }

        when {
            position < -1 -> {     // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1F
                page.rotationY = 180 * (1 - abs(position) + 1)
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1F
                page.rotationY = -180 * (1 - abs(position) + 1)
            }
            else -> {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }
    }

}
 class PopTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width
        if (abs(position) < 0.5) {
            page.visibility = View.VISIBLE
            page.scaleX = 1 - abs(position)
            page.scaleY = 1 - abs(position)
        } else if (abs(position) > 0.5) {
            page.visibility = View.GONE
        }
    }
}
class RotateDownTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val width = page.width.toFloat()
        val height = page.height.toFloat()
        val rotation = -15.0F * position * -1.25F
        page.pivotX = width * 0.5F
        page.pivotY = height
        page.rotation = rotation
    }
}
class RotateUpTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val width = page.width.toFloat()
        val rotation = -15.0F * position
        page.pivotX = width * 0.5F
        page.pivotY = 0.0F
        page.translationX = 0.0F
        page.rotation = rotation
    }
}
class SpinnerTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width
        page.cameraDistance = 12000F
        when {
            position < 0.5 && position > -0.5 -> {
                page.visibility = View.VISIBLE
            }
            else -> {
                page.visibility = View.INVISIBLE
            }
        }
        when {
            position < -1 -> {     // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1F
                page.rotationY = 900 * (1 - abs(position) + 1)
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1F
                page.rotationY = -900 * (1 - abs(position) + 1)
            }
            else -> {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }
    }
}
class StackTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = if (position < 0.0F) 0.0F else (-page.width).toFloat() * position
    }
}
class TabletTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val rotation = (if (position < 0.0F) 30.0F else -30.0F) * abs(position)
        page.translationX =
            getOffsetXForRotation(rotation, page.width, page.height)
        page.pivotX = page.width.toFloat() * 0.5F
        page.pivotY = 0.0F
        page.rotationY = rotation
    }

    companion object {
        private val OFFSET_MATRIX = Matrix()
        private val OFFSET_CAMERA = Camera()
        private val OFFSET_TEMP_FLOAT = FloatArray(2)
          fun getOffsetXForRotation(degrees: Float, width: Int, height: Int): Float {
            OFFSET_MATRIX.reset()
            OFFSET_CAMERA.save()
            OFFSET_CAMERA.rotateY(abs(degrees))
            OFFSET_CAMERA.getMatrix(OFFSET_MATRIX)
            OFFSET_CAMERA.restore()
            OFFSET_MATRIX.preTranslate((-width).toFloat() * 0.5F, (-height).toFloat() * 0.5F)
            OFFSET_MATRIX.postTranslate(width.toFloat() * 0.5F, height.toFloat() * 0.5F)
            OFFSET_TEMP_FLOAT[0] = width.toFloat()
            OFFSET_TEMP_FLOAT[1] = height.toFloat()
            OFFSET_MATRIX.mapPoints(OFFSET_TEMP_FLOAT)
            return (width.toFloat() - OFFSET_TEMP_FLOAT[0]) * if (degrees > 0.0F) 1.0F else -1.0F
        }
    }
}
class TossTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width
        page.cameraDistance = 20000F
        when {
            position < 0.5 && position > -0.5 -> {
                page.visibility = View.VISIBLE
            }
            else -> {
                page.visibility = View.INVISIBLE
            }
        }
        when {
            position < -1 -> {     // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1F
                page.scaleX = Math.max(0.4F, 1 - abs(position))
                page.scaleY = Math.max(0.4F, 1 - abs(position))
                page.rotationX = 1080 * (1 - abs(position) + 1)
                page.translationY = -1000 * abs(position)
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1F
                page.scaleX = Math.max(0.4F, 1 - abs(position))
                page.scaleY = Math.max(0.4F, 1 - abs(position))
                page.rotationX = -1080 * (1 - abs(position) + 1)
                page.translationY = -1000 * abs(position)
            }
            else -> {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }
    }
}
class VerticalFlipTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width
        page.cameraDistance = 20000F
        when {
            position < 0.5 && position > -0.5 -> {
                page.visibility = View.VISIBLE
            }
            else -> {
                page.visibility = View.INVISIBLE
            }
        }
        when {
            position < -1 -> {     // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1F
                page.rotationX = 180 * (1 - abs(position) + 1)
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1F
                page.rotationX = -180 * (1 - abs(position) + 1)
            }
            else -> {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }
    }
}
class VerticalShutTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width
        page.cameraDistance = 999999999F
        when {
            position < 0.5 && position > -0.5 -> {
                page.visibility = View.VISIBLE
            }
            else -> {
                page.visibility = View.INVISIBLE
            }
        }
        when {
            position < -1 -> {// [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1F
                page.rotationX = 180 * (1 - abs(position) + 1)
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1F
                page.rotationX = -180 * (1 - abs(position) + 1)
            }
            else -> {// (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }
    }
}
class ZoomInTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val scale = when {
            position < 0.0F -> position + 1.0F
            else -> abs(1.0F - position)
        }
        page.scaleX = scale
        page.scaleY = scale
        page.pivotX = page.width.toFloat() * 0.5F
        page.pivotY = page.height.toFloat() * 0.5F
        page.alpha = if (position >= -1.0F && position <= 1.0F) 1.0F - (scale - 1.0F) else 0.0F
    }
}
class ZoomOutSlideTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        when {
            position >= -1.0F || position <= 1.0F -> {
                val height = page.height.toFloat()
                val scaleFactor = max(0.85F, 1.0F - abs(position))
                val vertMargin = height * (1.0F - scaleFactor) / 2.0F
                val horzMargin = page.width.toFloat() * (1.0F - scaleFactor) / 2.0F
                page.pivotY = 0.5F * height
                if (position < 0.0F) {
                    page.translationX = horzMargin - vertMargin / 2.0F
                } else {
                    page.translationX = -horzMargin + vertMargin / 2.0F
                }
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
                page.alpha = 0.5F + (scaleFactor - 0.85F) / 0.14999998F * 0.5F
            }
        }
    }
}
class ZoomOutTransformer : ViewPager2.PageTransformer {
    companion object {
        private const val MIN_SCALE = 0.65F
        private const val MIN_ALPHA = 0.3F
    }

    override fun transformPage(page: View, position: Float) {
        when {
            position < -1 -> {//[-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0F
            }
            position <= 1 -> { //[-1,1]
                page.scaleX = max(MIN_SCALE, 1 - abs(position))
                page.scaleY = max(MIN_SCALE, 1 - abs(position))
                page.alpha = max(MIN_ALPHA, 1 - abs(position))
            }
            else -> {  //(1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0F
            }
        }
    }
}
