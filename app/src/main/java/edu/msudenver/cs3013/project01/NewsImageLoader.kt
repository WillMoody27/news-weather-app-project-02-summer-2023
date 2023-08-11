package edu.msudenver.cs3013.project01

import android.widget.ImageView

interface NewsImageLoader {
    // This function will takes the parameters of the urlToImage and the imageView to get the image from the url
    fun loadImage(urlToImage: String, imageView: ImageView)
}