package com.cashfulus.cashcarplus.ui.image

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cashfulus.cashcarplus.R
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_image_crop.*

class ImageCropActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_crop)

        //cropImageView.setImageBitmap(intent.getParcelableExtra("image")!!)
        cropImageView.setAspectRatio(1, 1)

        toolbarImageCrop.setLeftOnClick {
            finish()
        }

        toolbarImageCrop.setRightOnClick {
            val cIntent = Intent()
            cIntent.putExtra("image", cropImageView.croppedImage)
            setResult(RESULT_OK, cIntent)
            finish()
        }
    }
}