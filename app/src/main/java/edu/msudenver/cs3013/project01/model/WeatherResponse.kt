package edu.msudenver.cs3013.project01.model

data class WeatherResponse(
    val location: Location,
    val current: Current,
    val temp_f: Double,
)

// TODO-complete: Set Model to match API response to get the data
data class Location(
    val name: String,
)

data class Current(
    val temp_f: Double,
    val condition: Condition
)

data class Condition(
    val text: String,

    // TODO: API response has icon as a String but doesn't work
    val icon: String
)
