package edu.msudenver.cs3013.project01

import android.app.Dialog
import android.app.ProgressDialog.show
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import edu.msudenver.cs3013.project01.api.NewsApiService
import edu.msudenver.cs3013.project01.model.NewsResponse
import edu.msudenver.cs3013.project01.model.NewsSource
import edu.msudenver.cs3013.project01.model.NewsUiModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val API_KEY = "YOUR API KEY HERE"

//private const val API_KEY = ""
private const val CATEGORY_DEFAULT = ""
private const val COUNTRY = "us"

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter

    // Loading Dialog Object
    private var loadingDialog: Dialog? = null


    // TODO-Requirement: NEWS API Retrofit SETUP ------------------------------------------------------------
    private val retrofitNews by lazy {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
    private val theNewsAPIService by lazy {
        retrofitNews.create(NewsApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // TODO-complete: Initialize the recycler view and the category buttons
        recyclerView = view.findViewById(R.id.recycler_view)

        val categoryButtons = listOf<MaterialButton>(
            view.findViewById(R.id.top_headlines),
            view.findViewById(R.id.business_button),
            view.findViewById(R.id.entertainment_button)
        )

        /*
        * TODO-complete: Create the news adapter with the layout inflater, image loader, and click listener for each news article in the recycler view.
        *  - Pass in the GlideNewsImageLoader to the NewsAdapter to load the images for each news article to show to the user.
        * */
        newsAdapter = NewsAdapter(
            layoutInflater,
            GlideNewsImageLoader(requireContext()),
            object : NewsAdapter.OnClickListener {
                override fun onItemClick(newsData: NewsUiModel) =
                    showArticleContent(newsData)
            }
        )

        /*
         * TODO-complete: Set the adapter and layout manager for the recycler view with touch helper for swipe to save and delete functionality.
         */
        recyclerView.adapter = newsAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val itemTouchHelper = ItemTouchHelper(newsAdapter.swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        /*
         * TODO-complete: Depending on the category button clicked, get the news articles for that category
         *  - sub-task: Set the header text to the category name if not, set to default "Top Headlines".
         *  - sub-task: Show loading dialog while the news articles are being retrieved.
         */

        categoryButtons.forEach { button ->
            button.setOnClickListener {

                showLoadDialog()

                categorySelection(button, view)

                removeLoadDialog()
            }
        }

        /*
         * TODO-completed: Show loading dialog while the news articles are being retrieved while getting the top headlines
         *  by default when the fragment is created.
         *  - After the news articles are retrieved, remove the loading dialog.
         */

        showLoadDialog()

        categorySelection(view.findViewById(R.id.top_headlines), view)

        removeLoadDialog()
    }


    /*
     * TODO-complete:
     *  - 1. Set the header text to "Top Headlines" on the default category and set the constant to the default category.
     *  - 2. If the user business or entertainment category button, set the header text to the category name and set the
     *      constant to the category name.
     *  - 3. If no category is selected, set the header text to "Top Headlines" and set the constant to the default
     *      category and update the news articles.
     */
    private fun categorySelection(button: MaterialButton, view: View) {
        val category = when (button.id) {
            R.id.top_headlines -> {
                view.findViewById<TextView>(R.id.header_text).text = "Top Headlines".uppercase()
                CATEGORY_DEFAULT
            }

            R.id.business_button, R.id.entertainment_button -> {
                view.findViewById<TextView>(R.id.header_text).text =
                    button.text.toString().uppercase()
                button.text.toString().lowercase()
            }

            else -> CATEGORY_DEFAULT
        }

        // TODO-complete: Get the updated news articles based on category parameter passed in.
        getNews(category)
    }

    /*
     * TODO-Requirement:
     *  - 1. Initialize the call to the API service to get the top headlines and by using enqueue to make the call asynchronously.
     *  - 2. On success, get the list of news articles from the response and pass it to the news adapter
     *      (logic housed in getNewsList() method) to update the list of news articles.
     *  - 3. On failure, log the error and show a toast message to the user.
     */
    private fun getNews(category: String) {
        // set the call to the API service to get the top headlines
        val call = theNewsAPIService.getTopHeadlines(API_KEY, COUNTRY, category)
        call.enqueue(object : Callback<NewsResponse> {
            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                handlErrorsWithApi("Failed to get search results")
            }

            override fun onResponse(call: Call<NewsResponse>, res: Response<NewsResponse>) {
                when {
                    /*
                    *  TODO-complete: This method is called when the API call is successful and will call
                    *   the method to get the list of news articles from the response.
                    *       - The let scope function is used to perform a null check on the
                    *         response body and if not null, call the method to get the list
                    *         of news articles from the response
                    *      - If the response body is null, call the method to handle the error
                    */
                    res.isSuccessful -> res.body()?.let { newsResponse ->
                        getNewsList(newsResponse)
                    } ?: handlErrorsWithApi("Error...Failed to get search results")

                    else -> {
                        val errorMsg = res.errorBody()?.string().orEmpty()
                        handlErrorsWithApi("Failed to get search results: $errorMsg")
                    }
                }
            }
        })
    }


    // TODO-complete: This method will get the list of news articles from the response and convert them to a list of NewsUiModel objects to display in the recycler view
    // TODO-complete: Map the list of articles to a list of NewsUiModel objects based on the response data
    // TODO-complete: Convert each article to a NewsUiModel object to display in the recycler view
    // TODO-complete: Set the data in the adapter to the list of NewsUiModel objects
    private fun getNewsList(newsResponse: NewsResponse) {
        val newsUiList = newsResponse.articles.map { article ->
            NewsUiModel(
                author = article.author ?: "Unknown Author",
                title = article.title ?: "Unknown Title",
                source = NewsSource(article.source.name ?: "Unknown Source"),
                content = article.content ?: "",
                urlToImage = article.urlToImage ?: "",
            )
        }
        newsAdapter.setData(newsUiList)
    }

    // TODO-complete: This method will show the article content fragment and pass the article data to it
    private fun showArticleContent(newsData: NewsUiModel) {
        val args = Bundle().apply {
            putString("articleTitle", newsData.title)
            putString("articleAuthor", newsData.author)
            putString("articleSource", newsData.source.name)
            putString("articleImage", newsData.urlToImage)
            putString("articleContent", newsData.content)
        }
        findNavController().navigate(R.id.action_app_home_to_articleContentFragment, args)
    }

    // TODO Above and Beyond: Create instance for showing loading dialog
    private fun showLoadDialog() {
        // Create a new instance of the loading dialog
        loadingDialog = Dialog(requireContext())
        loadingDialog?.setContentView(R.layout.loading)
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()
    }

    // TODO Above and Beyond: Remove loading dialog once the news articles are retrieved
    private fun removeLoadDialog() {
        loadingDialog?.dismiss()
    }

    private fun handlErrorsWithApi(errorMessage: String) {
        showErrorMessage(errorMessage)
        Log.e(TAG, errorMessage)
    }

    // TODO Above and Beyond: Show error message to the user
    private fun showErrorMessage(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }
}
