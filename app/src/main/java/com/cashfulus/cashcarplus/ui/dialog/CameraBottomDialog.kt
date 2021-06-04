package com.cashfulus.cashcarplus.ui.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.util.resizeBitmap
import com.cashfulus.cashcarplus.util.rotateBitmap
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_camera.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


interface CameraBottomDialogClickListener {
    fun onClick(imgNum: Int, image: Bitmap)
    fun onError(msg: String)
}

class CameraBottomDialog(imgNum: Int) : BottomSheetDialogFragment() {

    lateinit var clickListener: CameraBottomDialogClickListener
    private lateinit var mCurrentPhotoPath: String

    private val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if(activityResult.resultCode == AppCompatActivity.RESULT_OK) {
            if(Build.VERSION.SDK_INT >= 29) {
                val source = ImageDecoder.createSource(requireContext().contentResolver, Uri.fromFile(File(mCurrentPhotoPath)))
                try {
                    val bitmap = resizeBitmap(ImageDecoder.decodeBitmap(source))
                    val bitmapResult = rotateBitmap(bitmap, mCurrentPhotoPath)
                    if(bitmapResult != null)
                        clickListener.onClick(imgNum, bitmapResult)
                    else
                        clickListener.onClick(imgNum, bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    clickListener.onError("오류 발생 : " + e.localizedMessage)
                }
            } else {
                try {
                    val bitmap = resizeBitmap(MediaStore.Images.Media.getBitmap(requireContext().contentResolver, Uri.fromFile(File(mCurrentPhotoPath))))
                    val bitmapResult = rotateBitmap(bitmap, mCurrentPhotoPath)
                    if(bitmapResult != null)
                        clickListener.onClick(imgNum, bitmapResult)
                    else
                        clickListener.onClick(imgNum, bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    clickListener.onError("오류 발생 : " + e.localizedMessage)
                }
            }
        } else {
            Log.d("Cashcar", "Error occured : " + activityResult.resultCode.toString())
        }

        dismiss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_camera, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            clickListener = context as CameraBottomDialogClickListener
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBottomSheetCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(intent.resolveActivity(requireContext().packageManager) != null) {
                var photoFile: File? = null

                try { photoFile = createImageFile() }
                catch (e: IOException) { clickListener.onError("오류 발생 : " + e.localizedMessage) }

                if(photoFile != null) {
                    val photoURI = FileProvider.getUriForFile(requireContext(), "com.cashfulus.cashcarplus.fileprovider", photoFile)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    requestActivity.launch(intent)
                } else {
                    clickListener.onError("카메라 호출 도중 오류가 발생했습니다. 다시 시도해 주세요.")
                    dismiss()
                }
            }
        }
    }

    private fun createImageFile(): File {
        val imageFileName = "JPEG_"+SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())+"_"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        mCurrentPhotoPath = image.absolutePath
        return image
    }
}