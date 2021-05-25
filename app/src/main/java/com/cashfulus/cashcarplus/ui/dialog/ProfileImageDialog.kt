package com.cashfulus.cashcarplus.ui.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
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
import androidx.fragment.app.DialogFragment
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.base.App
import com.cashfulus.cashcarplus.ui.image.ImageCropActivity
import com.cashfulus.cashcarplus.util.resizeBitmap
import com.cashfulus.cashcarplus.util.rotateBitmap
import kotlinx.android.synthetic.main.dialog_profile_image.*
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.ClassCastException
import java.text.SimpleDateFormat
import java.util.*

interface ProfileImageDialogClickListener {
    fun onClick(image: Bitmap)
    fun onError(msg: String)
}

class ProfileImageDialog : DialogFragment() {
    lateinit var clickListener: ProfileImageDialogClickListener
    lateinit var mCurrentPhotoPath: String

    private val cameraActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if(activityResult.resultCode == AppCompatActivity.RESULT_OK) {
            val intent = Intent(requireActivity(), ImageCropActivity::class.java)

            // 아니 코드 빠져 있었네 ㅁㅊ;;;;;;;
            if(Build.VERSION.SDK_INT >= 29) {
                val source = ImageDecoder.createSource(requireContext().contentResolver, Uri.fromFile(File(mCurrentPhotoPath)))
                try {
                    val bitmap = resizeBitmap(ImageDecoder.decodeBitmap(source))
                    val bitmapResult = rotateBitmap(bitmap, mCurrentPhotoPath)
                    if(bitmapResult != null) {
                        intent.putExtra("image", bitmapResult)
                        cropActivity.launch(intent)
                    } else {
                        intent.putExtra("image", bitmap)
                        cropActivity.launch(intent)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    clickListener.onError("오류 발생 : "+e.localizedMessage)
                    dismiss()
                }
            } else {
                try {
                    val bitmap = resizeBitmap(MediaStore.Images.Media.getBitmap(requireContext().contentResolver, Uri.fromFile(File(mCurrentPhotoPath))))
                    val bitmapResult = rotateBitmap(bitmap, mCurrentPhotoPath)
                    if(bitmapResult != null) {
                        intent.putExtra("image", bitmapResult)
                        cropActivity.launch(intent)
                    } else {
                        intent.putExtra("image", bitmap)
                        cropActivity.launch(intent)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    clickListener.onError("오류 발생 : "+e.localizedMessage)
                    dismiss()
                }
            }
        } else {
            Log.d("Cashcar", "Error occured : "+activityResult.resultCode.toString())
            dismiss()
        }
    }

    val galleryActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if(activityResult.resultCode == AppCompatActivity.RESULT_OK && activityResult.data!!.data != null) {
            try {
                val cropIntent = Intent(requireActivity(), ImageCropActivity::class.java)
                cropIntent.putExtra("image", activityResult.data!!.data)
                cropActivity.launch(cropIntent)
                /*val inputStream: InputStream? = App().context().contentResolver.openInputStream(activityResult.data!!.data!!)
                if(inputStream != null) {
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()

                    val cropIntent = Intent(requireActivity(), ImageCropActivity::class.java)
                    cropIntent.putExtra("image", bitmap)
                    cropActivity.launch(cropIntent)
                } else {
                    clickListener.onError("사진 처리 중 오류가 발생했습니다. 다시 시도해 주세요.")
                    dismiss()
                }*/
            } catch (e: Exception) {
                clickListener.onError("오류 발생 : "+e.localizedMessage)
                e.printStackTrace()
                dismiss()
            }
        } else {
            dismiss()
        }
    }

    val cropActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if(activityResult.resultCode == AppCompatActivity.RESULT_OK && activityResult.data!!.data != null) {
            try {
                val inputStream: InputStream? = App().context().contentResolver.openInputStream(activityResult.data!!.data!!)
                if (inputStream != null) {
                    val bitmap = resizeBitmap(BitmapFactory.decodeStream(inputStream))
                    val bitmapResult = rotateBitmap(bitmap, mCurrentPhotoPath)
                    inputStream.close()

                    if(bitmapResult != null) {
                        clickListener.onClick(bitmapResult)
                    } else {
                        clickListener.onClick(bitmap)
                    }
                } else {
                    clickListener.onError("사진 처리 중 오류가 발생했습니다. 다시 시도해 주세요.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                clickListener.onError("사진 처리 중 오류가 발생했습니다. 다시 시도해 주세요.")
            }
        } else {
            clickListener.onError("사진 처리 중 오류가 발생했습니다. 다시 시도해 주세요.")
        }

        dismiss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_profile_image, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            clickListener = context as ProfileImageDialogClickListener
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*setCanceledOnTouchOutside(true)
        //window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)*/

        tvDialogProfileCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(intent.resolveActivity(requireContext().packageManager) != null) {
                var photoFile: File? = null

                try { photoFile = createImageFile() }
                catch (e: IOException) { clickListener.onError("오류 발생 : " + e.localizedMessage) }

                if(photoFile != null) {
                    val photoURI = FileProvider.getUriForFile(requireContext(), "com.cashfulus.cashcarplus.fileprovider", photoFile)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    cameraActivity.launch(intent)
                } else {
                    clickListener.onError("카메라 호출 도중 오류가 발생했습니다. 다시 시도해 주세요.")
                }
            }
        }

        tvDialogProfileGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            galleryActivity.launch(intent)
        }
    }

    fun createImageFile(): File {
        val imageFileName = "JPEG_"+ SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())+"_"
        val storageDir = App().context().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        mCurrentPhotoPath = image.absolutePath
        return image
    }
}