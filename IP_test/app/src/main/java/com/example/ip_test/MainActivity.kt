package com.example.ip_test;



import android.annotation.SuppressLint
//import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.common.FirebaseMLException
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.automl.FirebaseAutoMLRemoteModel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException


class MainActivity : AppCompatActivity() {
    lateinit var remoteModel: FirebaseAutoMLRemoteModel
    lateinit var labeler: FirebaseVisionImageLabeler
    lateinit var optionsBuilder: FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder
    //lateinit var progressDialog: ProgressDialog
    lateinit var conditions: FirebaseModelDownloadConditions
    private lateinit var image: FirebaseVisionImage

    /*
    override fun onCreate( savedInstanceState: Bundle? ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectImageBtn.setOnClickListener {
            CropImage.activity().start(this@MainActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri: Uri = result.uri
                val imgView = findViewById<ImageView>(R.id.image)
                imgView.setImageURI(resultUri)
            } /*else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error}*/

        }
    }    */        // if we want only image selection


    override fun onCreate( savedInstanceState: Bundle? ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectImageBtn.setOnClickListener{
            fromRemoteModel()
        }
    }

    private fun fromRemoteModel() {
        //showProgressBar()
        remoteModel = FirebaseAutoMLRemoteModel.Builder("Plants_202033118324").build()
        conditions = FirebaseModelDownloadConditions.Builder().requireWifi().build()
        FirebaseModelManager.getInstance().download(remoteModel,conditions)
            .addOnCompleteListener {
                CropImage.activity().start(this@MainActivity)
            }
    }

    /*
    private fun showProgressBar(){
        progressDialog = ProgressDialog(this@MainActivity)

    }*/


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                if (result != null) {
                    val resultUri = result.uri
                    val imgView = findViewById<ImageView>(R.id.image)
                    imgView.setImageURI(resultUri)
                    flowerType.text = ""
                    setLabelerFromRemoteLabel(resultUri)
                }
            }
        }
    }

    private fun setLabelerFromRemoteLabel(uri: Uri){
        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
            .addOnSuccessListener{
                optionsBuilder = FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(remoteModel)
                val options = optionsBuilder
                    .setConfidenceThreshold(0.0f)
                    .build()
                try {
                    labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options)
                    image = FirebaseVisionImage.fromFilePath(this@MainActivity,uri)
                    progressImageLabeler(labeler,image)
                } catch (e: FirebaseMLException) {}
                catch (e: IOException) {}
            }
    }


    @SuppressLint("SetTextI18n")
    private fun progressImageLabeler(labeler: FirebaseVisionImageLabeler, image: FirebaseVisionImage){
        //labeler.processImage(image).addOnSuccessListener{
            //progressDialog.cancel()
            //for (label in labels)
        //}
        labeler.processImage(image).addOnSuccessListener{ labels ->
            for(label in labels) {
                var each = label.text.toUpperCase()
                var confidence = label.confidence
                //flowerType.text = each + " - " + (confidence * 100)+ "%" + "\n\n"
                flowerType.append(each + " - " + (confidence * 100)+ "%" + "\n")
            }

        }.addOnFailureListener{
            flowerType.text = "error"
        }
    }
}




