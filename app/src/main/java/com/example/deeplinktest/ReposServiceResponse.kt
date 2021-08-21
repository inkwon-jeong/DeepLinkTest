package com.example.deeplinktest


import com.google.gson.annotations.SerializedName

data class ReposServiceResponse(
    @SerializedName("items")
    val repos: List<Repo>
)

data class Repo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("html_url")
    val htmlUrl: String,
)