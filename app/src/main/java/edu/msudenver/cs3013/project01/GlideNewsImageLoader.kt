package edu.msudenver.cs3013.project01

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

// Extend the ImageLoader interface
class GlideNewsImageLoader(private val context: Context) : NewsImageLoader {
    override fun loadImage(urlToImage: String, imageView: ImageView) {

        // Use Glide to load the image from the url to the imageView specified in the parameters
        Glide.with(context)
            .load(urlToImage)
            .centerCrop()
            .into(imageView)
    }
}
