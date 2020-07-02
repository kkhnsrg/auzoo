package edu.kokhan.auzoo.utils

import android.os.Environment
import java.io.File

const val DIR_NAME = "AuzooPhotos"
const val IMG_FORMAT = "png"
const val FILE_PROVIDER_AUTHORITY = "edu.kokhan.auzoofileprovider"

//this path will be used in the save photo on device function
fun getPhotoGalleryPath() =
    "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}${File.separator}${DIR_NAME}${File.separator}"

//simple checker for images .jpg format only because our app save images only in this format
fun File.isImage() = name.contains(".$IMG_FORMAT")

fun File.createDirIfNotExist() {
    if (!this.exists()) {
        this.mkdirs()
    }
}

