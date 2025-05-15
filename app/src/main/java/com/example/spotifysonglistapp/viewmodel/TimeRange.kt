package com.example.spotifysonglistapp.viewmodel

enum class TimeRange(val apiValue: String, val label: String) {
    SHORT_TERM("short_term", "Last 4 weeks"),
    MEDIUM_TERM("medium_term", "Last 6 months"),
    LONG_TERM("long_term", "All time")
}
