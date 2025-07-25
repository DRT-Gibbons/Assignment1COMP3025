package ca.georgiancollege.assignment1comp3025

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.georgiancollege.assignment1comp3025.databinding.ActivityDetailBinding
import coil.load
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


// DetailActivity handles the second page and shows additional detail besides just the title
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflating layout and get binding object
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieving the movie info passed from main
        // intent specifies what key we want from the model filled in the main activity
        //getstringextra specifies that we want that key's value in string form
        val imdbID = intent.getStringExtra("imdbID") ?: ""
        val title = intent.getStringExtra("title") ?: ""
        val year = intent.getStringExtra("year") ?: ""

        // Validate imdb ID; close if missing
        if (imdbID.isEmpty()) {
            Toast.makeText(this, "Missing movie ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Displaying title and year while other stuff is fetched
        // since we already have it from the main
        binding.textTitle.text = title
        binding.textYear.text = year

        // fetching movie info asynchronously (function defined below)
        fetchMovieDetails(imdbID)

        // onclicklistener for back button to close the detailactivity
        binding.buttonBack.setOnClickListener { finish() }
    }


    // This function fetches the director, rating, plot and the poster then updates the UI
    private fun fetchMovieDetails(imdbID: String) {
        Thread {
            // place holder variables and checking if poster is null or not
            var director = "N/A"
            var rating = "N/A"
            var plot = "N/A"
            var posterUrlLocal: String? = null

            // api variables
            val apiKey = "c4620150"
            val url = "https://www.omdbapi.com/?apikey=$apiKey&i=$imdbID&plot=full"

            // try catch for api call
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    // returns as string then converts json string to an object
                    val body = response.body?.string()
                    if (body != null) {
                        val json = JSONObject(body)
                        if (json.optString("Response") == "True") {
                            director = json.optString("Director", director)
                            rating = json.optString("imdbRating", rating)
                            plot = json.optString("Plot", plot)
                            posterUrlLocal = json.optString("Poster")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val posterUrlFinal = posterUrlLocal

            // UI update happens on the main thread
            runOnUiThread {
                //changing the text
                binding.textDirector.text = "Director: $director"
                binding.textRating.text = "IMDb Rating: $rating"
                binding.textPlot.text = plot

                // Loading the poster image from URL
                //simple if else to check if it's there or not
                if (!posterUrlFinal.isNullOrBlank() && posterUrlFinal != "N/A") {
                    //loadposterimage defined below
                    loadPosterImage(posterUrlFinal)
                } else {
                    binding.imagePoster.visibility = View.GONE
                }
            }
        }.start()
    }



    // gets the poster and then sets it to the image view. hides the image view if it fails
    private fun loadPosterImage(imageUrl: String) {
        binding.imagePoster.apply {
            visibility = View.VISIBLE
            // loading the url method comes from coil
            load(imageUrl) {
                // If loading the image fails, hide the ImageView
                listener(onError = { _, _ -> visibility = View.GONE })
            }
        }
    }
}
