package com.example.deeplinktest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {

    private val api = ReposService.reposApi

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _repos = MutableLiveData<List<Repo>>()
    val repos: LiveData<List<Repo>>
        get() = _repos

    fun searchRepos(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _repos.value = withContext(Dispatchers.IO) {
                try {
                    api.searchRepos(query).repos
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()
            _isLoading.value = false
        }
    }
}