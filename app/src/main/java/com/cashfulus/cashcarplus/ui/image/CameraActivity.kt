package com.cashfulus.cashcarplus.ui.image

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.App
import com.cashfulus.cashcarplus.base.BaseActivity
import com.cashfulus.cashcarplus.databinding.ActivityCameraBinding
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Audio
import com.otaliastudios.cameraview.controls.Mode
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val CAMERA_ACTIVITY_FILE_NULL = 3087

class CameraActivity : BaseActivity() {
    private val binding by binding<ActivityCameraBinding>(R.layout.activity_camera)

    private lateinit var mCurrentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore instance state
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@CameraActivity
        }

        /** cameraView 설정 */
        binding.cameraView.setLifecycleOwner(this)
        binding.cameraView.audio = Audio.OFF
        binding.cameraView.mode = Mode.PICTURE

        /** 이미지를 저장할 URI 주소 받아옴 */
        val photoURI = intent.getParcelableExtra<Uri>(MediaStore.EXTRA_OUTPUT)

        /** Toolbar 설정 */
        binding.toolbarCamera.setLeftOnClick {
            finish()
        }

        binding.btnCamera.setOnClickListener {
            binding.cameraView.takePicture()
        }

        binding.cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                // Picture was taken!
                // If planning to show a Bitmap, we will take care of
                // EXIF rotation and background threading for you...
                //result.toBitmap(1000, 1000) { bitmap -> binding.ivTmp22.setImageBitmap(bitmap) }

                // Access the raw data if needed.
                //val data = result.data
                try {
                    val photoFile = createImageFile()
                    // If planning to save a file on a background thread,
                    // just use toFile. Ensure you have permissions.
                    result.toFile(photoFile) {file ->
                        if(file != null) {
                            val resultIntent = Intent()
                            resultIntent.putExtra("mCurrentPhotoPath", mCurrentPhotoPath)
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        } else {
                            showToast("오류 발생 : 사진 파일을 생성하지 못했습니다.")
                            setResult(CAMERA_ACTIVITY_FILE_NULL)
                            finish()
                        }
                    }
                } catch (e: IOException) {
                    showToast("오류 발생 : " + e.localizedMessage)
                    setResult(CAMERA_ACTIVITY_FILE_NULL)
                    finish()
                }
            }
        })
    }

    private fun createImageFile(): File {
        val imageFileName = "JPEG_"+ SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())+"_"
        val storageDir = App().context().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        mCurrentPhotoPath = image.absolutePath
        return image
    }
}