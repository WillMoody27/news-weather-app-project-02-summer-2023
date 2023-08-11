package edu.msudenver.cs3013.project01

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.msudenver.cs3013.project01.model.NewsUiModel

// This is the ViewHolder that is used by the Adapter This will be used to bind the data to the view
class NewsViewHolder(
    private val containerView: View,
    private val newsImageLoader: NewsImageLoader,
    private val onClickListener: OnClickListener

) : RecyclerView.ViewHolder(containerView) {

    // Author Text
    private val authorText: TextView
            by lazy { containerView.findViewById(R.id.item_list_author) }

    // Header Text
    private val headerText: TextView
            by lazy { containerView.findViewById(R.id.item_list_header) }

    // Source Text
    private val sourceText: TextView
            by lazy { containerView.findViewById(R.id.item_list_source) }

    //  7/15/23  Image View "newsImageView"
    private val imgText: ImageView
            by lazy { containerView.findViewById(R.id.newsImageView) }


    fun bindData(newsData: NewsUiModel) {

        // 7/15/23 Set the onClickListener for the view
        containerView.setOnClickListener { onClickListener.onClick(newsData) }
        // Load the image
        newsImageLoader.loadImage(newsData.urlToImage, imgText)

        // TODO-complete: Set the text of the author, header, and source
        authorText.text = newsData.author
        headerText.text = newsData.title
        sourceText.text = newsData.source.name
    }

    // 7/15/23 Interface Added for the onClickListener
    interface OnClickListener {
        fun onClick(newsData: NewsUiModel)
    }
}
