package ca.georgiancollege.assignment1comp3025

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.georgiancollege.assignment1comp3025.databinding.ItemMovieBinding


//Using a recyclerview adapter to show the movie list
//First, declaring the movies as a list and using an onclick listener function
// onclick takes a Movie object and returns unit (i.e., has no return value).
// This is called whenever a movie item is clicked to handle the click event
class MovieAdapter(private var movies: List<MovieModel>, private val onClick: (MovieModel) -> Unit
    // Adapter uses MovieViewHolder to hold individual item views
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    // Inner viewholder class that holds references to the views for each movie item
    // Using viewbinding to access views in the item layout
    //  Then, passing the root view to superclass constructor
    inner class MovieViewHolder(private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        // function to bind the movie data to the UI views
        fun bind(movie: MovieModel) {
            // Set the text of the textTitleYear TextView to show the movie's title and year in the format: "Title (Year)"
            binding.textTitleYear.text = "${movie.title} (${movie.year})"

            // Setting another onclick listener on the entire item view so every item has an onclick listened
            binding.root.setOnClickListener { onClick(movie) }
        }
    }

    // Called when recyclerview needs a new viewholder to represent an item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        // Inflate the item layout using the generated binding class
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Return a new movieviewholder instance to wrap the views
        return MovieViewHolder(binding)
    }

    // Returns the total number of items in the data set held by the adapter.
    override fun getItemCount() = movies.size

    // Called by RecyclerView to display data at the specified position.
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        // Binding the Movie object at the given position to the ViewHolder.
        holder.bind(movies[position])
    }

    // Method to update the adapter's data and refresh the recycler view
    // Replaces the current movie list with the new data and refreshes
    fun updateData(newMovies: List<MovieModel>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}

