package ca.georgiancollege.assignment1comp3025

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ca.georgiancollege.assignment1comp3025.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //declaring variables
    private lateinit var adapter: MovieAdapter
    private val movies = mutableListOf<MovieModel>()
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout and get binding instance
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting up the recyclerview with the adapter and layout manager
        adapter = MovieAdapter(movies) { movie ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("imdbID", movie.imdbID)
                putExtra("title", movie.title)
                putExtra("year", movie.year)
            }
            startActivity(intent)
        }
        binding.recyclerMovies.layoutManager = LinearLayoutManager(this)
        binding.recyclerMovies.adapter = adapter

        // Search button onclicklistener
        binding.buttonSearch.setOnClickListener {
            val query = binding.editSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                //if the query isn't empty when the button is press, call searchmovies (defined below)
                searchMovies(query)
            } else {
                Toast.makeText(this, "Please enter a movie title", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun searchMovies(query: String) {
        Thread {
            //turning the model into a mutable list and storing in variable
            val foundMovies = mutableListOf<MovieModel>()
            //variables for api url and key
            val apiKey = "c4620150"
            val url = "https://www.omdbapi.com/?apikey=$apiKey&s=$query"
            //try catch block for the api call
            try {
                //setting client to okhttp for connection
                val client = OkHttpClient()
                //variable for making a request
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    //storing the returned json string to a variable
                    val body = response.body?.string()
                    if (body != null) {
                        //if it isn't null, convert string to json object
                        val json = JSONObject(body)

                        if (json.optString("Response") == "True") {
                            val array = json.getJSONArray("Search")
                            //iterating through the json object
                            for (i in 0 until array.length()) {
                                val item = array.getJSONObject(i)
                                //type key must have movie value
                                if (item.getString("Type") == "movie") {
                                    foundMovies.add(
                                        MovieModel(
                                            imdbID = item.getString("imdbID"),
                                            title = item.getString("Title"),
                                            year = item.getString("Year"),
                                            type = item.getString("Type"),
                                            poster = item.getString("Poster")
                                        )
                                    )
                                }
                            }
                            //logcat for breakpoint
                            Log.i("OMDb", "Found ${foundMovies.size} results for \"$query\"")
                        } else {
                            Log.i("OMDb", "Search failed: ${json.optString("Error")}")
                        }
                    }
                }
            } catch (e: Exception) {
                //logging error
                Log.i("OMDb", "Exception: ${e.message}")
            }

            mainHandler.post {
                movies.clear()
                movies.addAll(foundMovies)
                adapter.updateData(movies)
                if (movies.isEmpty()) {
                    Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}
