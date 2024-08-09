package com.cashfulus.cashcarplus.ui.image

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.canhub.cropper.CropImageView
import com.cashfulus.cashcarplus.R
import com.cashfulus.cashcarplus.view.UpgradedToolbar

class ImageCropActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_crop)

        var cropImageView = findViewById<CropImageView>(R.id.cropImageView)
        var toolbarImageCrop = findViewById<UpgradedToolbar>(R.id.toolbarImageCrop)

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