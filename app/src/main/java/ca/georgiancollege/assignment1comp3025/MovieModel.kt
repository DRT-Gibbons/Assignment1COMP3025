package ca.georgiancollege.assignment1comp3025

//Basic movie model with getters/setters used by the activities

class MovieModel(imdbID: String, title: String, year: String, type: String, poster: String, director: String = "", imdbRating: String = "", plot: String = "") {
    var imdbID: String = imdbID
        private set

    var title: String = title
        private set

    var year: String = year
        private set

    var type: String = type
        private set

    var poster: String = poster
        private set

    var director: String = director
        private set

    var imdbRating: String = imdbRating
        private set

    var plot: String = plot
        private set
}
