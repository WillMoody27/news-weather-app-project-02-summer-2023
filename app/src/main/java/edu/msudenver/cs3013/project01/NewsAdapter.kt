package edu.msudenver.cs3013.project01

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import edu.msudenver.cs3013.project01.model.NewsUiModel

class NewsAdapter(

    // This is the layoutInflater that is passed in from the Activity
    private val layoutInflater: LayoutInflater,

    // 7/15/23 NewsImageLoader is used to load the images from the network as a parameter to the adapter
    private val newsImageLoader: NewsImageLoader,

    // 7/15/23 OnClickListener is used to handle the click events on the items in the RecyclerView
    private val onClickListener: OnClickListener

) : RecyclerView.Adapter<NewsViewHolder>() {

    // TODO-d: 3. Swipe delete functionality - define read-only variable within our adapter
    val swipeToDeleteCallback = SwipeToDeleteCallback()

    // List of data that is passed in from the ViewModel
    private val newsData = mutableListOf<NewsUiModel>()


    // SetData is called by the ViewModel to update the data in the adapter
    fun setData(newListData: List<NewsUiModel>) {
        newsData.clear()
        newsData.addAll(newListData)
        notifyDataSetChanged()
    }

    // TODO-d: 1. Swipe delete functionality
    fun removeItem(position: Int) {
        newsData.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = layoutInflater.inflate(R.layout.item_news, parent, false)

        return NewsViewHolder(
            view,
            newsImageLoader,
            // 7/15/23 This allows us to pass the onClickListener to the CatViewHolder to select a category item
            object : NewsViewHolder.OnClickListener {
                override fun onClick(newsData: NewsUiModel) =
                    onClickListener.onItemClick(newsData)
            }
        )
    }

    override fun getItemCount() = newsData.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

        // TODO-Above and Beyond:  Check if it's the first item - If it is, apply special styling
        if (position == 0) {
            holder.itemView.findViewById<LinearLayout>(R.id.card_layout).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            }

            holder.itemView.findViewById<ImageView>(R.id.newsImageView).apply {
                layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
                layoutParams.height = 500
            }

            holder.itemView.findViewById<CardView>(R.id.card_home_1).apply {
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                cardElevation = 10F
            }
        }

        holder.bindData(newsData[position])
    }

    // TODO-d: 2. Swipe delete functionality onSwipeToDeleteCallback
    //  - Innerclass declaration
    inner class SwipeToDeleteCallback :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        // TODO-d: 4. Swipe delete functionality - Override getMovementFlags to allow for swipe left and right
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ) = if (viewHolder is NewsViewHolder) {
            makeMovementFlags(
                ItemTouchHelper.ACTION_STATE_IDLE,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) or makeMovementFlags(
                ItemTouchHelper.ACTION_STATE_SWIPE,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            )
        } else {
            0
        }

        // TODO-Above and Beyond: Swipe delete functionality - Override onSwiped to handle the swipe action of saving or deleting an article
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            // Get the position of the item that was swiped
            val position = viewHolder.adapterPosition

            // Determine the direction of the swipe
            val message = when (direction) {
                ItemTouchHelper.LEFT -> "Article Deleted"
                ItemTouchHelper.RIGHT -> "Article Saved"
                else -> "Unknown action"
            }

            // Temporarily save the item in a variable before removing it
            val item = getItem(position)
            removeItem(position)

            // Use snack-bar to display a message to the user instead of a toast
            val snackbar = Snackbar.make(
                viewHolder.itemView,
                message,
                Snackbar.LENGTH_LONG
            )

            /*
            * TODO-Above and Beyond: Allows the user to undo the swipe delete action
            * */
            snackbar.setAction("Undo") {
                // Restore the item at its previous position
                addItem(position, item)
            }
            snackbar.show()
        }

        // TODO-Above and Beyond: getItem will return the item at the specified position from which the item was swiped
        private fun getItem(position: Int): NewsUiModel {
            return newsData[position]
        }

        // removeItem will remove the item at the specified position and notify when it is removed

        private fun removeItem(position: Int) {
            // Remove the item from your list
            newsData.removeAt(position)
            // Notify the adapter about the item removal
            notifyItemRemoved(position)
        }

        // addItem will add the item at the specified position and notify when it is added
        private fun addItem(position: Int, item: NewsUiModel) {
            // Insert the item back into your list at the specified position
            newsData.add(position, item)
            // Notify the adapter about the item insertion
            notifyItemInserted(position)
        }

        // TODO-Above and Beyond:  Implement an onChildDraw method to allow users to see the background color as they swipe both left and right
        override fun onChildDraw(
            canvas: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {

            // Get the itemView from the viewHolder and create a ColorDrawable for the background
            val itemView = viewHolder.itemView
            val background = ColorDrawable()

            if (dX < 0) {
                // Swipe left (delete) and add icon
                background.color = Color.rgb(255, 0, 0)
            } else if (dX > 0) {
                // Swipe right (save)
                background.color = Color.rgb(2,164,255)
            }

            // Draw the red delete background
            background.setBounds(
                itemView.left,
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            background.draw(canvas)

            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    // 7/15/23 Interface for handling click events on the items in the RecyclerView
    interface OnClickListener {
        fun onItemClick(newsData: NewsUiModel)
    }
}