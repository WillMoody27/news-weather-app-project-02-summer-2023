package edu.msudenver.cs3013.project01

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ArticleContentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArticleContentFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO-complete: Get references to the views in the fragment_article_content layout
        val titleText = view.findViewById<TextView>(R.id.article_title)
        val authorText = view.findViewById<TextView>(R.id.article_author_and_Source)
        // TODO-complete: Show image using GlideNewsImageLoader make image (imageUrl) fit size of ImageView (imgContent)
        val imgContent = view.findViewById<ImageView>(R.id.imageUrl)
        // TODO-complete: Show image using GlideNewsImageLoader make image (imageUrl) fit size of ImageView (imgContent)
        val newsImageLoader = GlideNewsImageLoader(requireContext())
        val contentText = view.findViewById<TextView>(R.id.article_content)

        articleContent(titleText, authorText, imgContent, newsImageLoader, contentText)
    }

    // Suppressing Lint because we are using the same layout for both fragments
    @SuppressLint("SetTextI18n")
    private fun articleContent(
        titleText: TextView,
        authorText: TextView,
        imgContent: ImageView,
        newsImageLoader: GlideNewsImageLoader,
        contentText: TextView
    ) {
        // TODO-complete: Get arguments from bundle to set the title, author, source, image, and content
        val title = arguments?.getString("articleTitle")
        val author = arguments?.getString("articleAuthor")
        val source = arguments?.getString("articleSource")
        val imgUrl = arguments?.getString("articleImage")
        val content = arguments?.getString("articleContent")


        // TODO-complete: Check if imgUrl is null, if so, set imgContent to a default image
        //  - Note To Self: it is the same as saying if imgUrl == null then set imgContent to a default image
        when (imgUrl) {
            null -> imgContent.let { newsImageLoader.loadImage("No Image Available", it) }
            else -> imgContent.let { newsImageLoader.loadImage(imgUrl, it) }
        }

        titleText.text = title
        // TODO-complete: Set author in format "author | source"
        authorText.text = "$author | $source"

        // TODO-complete: Check if content is null, if so, set contentText to "No Content"
        contentText.text = content ?: getString(R.string.no_content)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_article_content, container, false)

//        view.findViewById<MaterialButton>(R.id.back_button).setOnClickListener {
//            findNavController().navigate(R.id.app_home)
//        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ArticleContentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ArticleContentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}