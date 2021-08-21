package com.example.deeplinktest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.deeplinktest.databinding.ActivitySearchBinding
import com.example.deeplinktest.databinding.ItemRepoBinding
import java.net.URLDecoder

class SearchActivity : AppCompatActivity() {

    private val searchViewModel by viewModels<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySearchBinding.inflate(layoutInflater)
            .apply {
                val adapter = RepoAdapter()
                recyclerViewRepos.adapter = adapter
                searchViewModel.repos.observe(this@SearchActivity, {
                    adapter.submitList(it)
                })

                searchViewModel.isLoading.observe(this@SearchActivity, {
                    if (it) {
                        progressLoading.visibility = View.VISIBLE
                        buttonQuery.isEnabled = false
                    } else {
                        progressLoading.visibility = View.GONE
                        buttonQuery.isEnabled = true
                    }
                })

                buttonQuery.setOnClickListener {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(editTextQuery.windowToken, 0)

                    val query = editTextQuery.text.toString()
                        .trim()
                        .replace(" ", "+")

                    val uri = Uri.Builder()
                        .scheme("repos")
                        .authority("search")
                        .appendPath(query)
                        .build()

                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
            }

        setContentView(binding.root)
        handleIntent()
    }

    private fun handleIntent() {
        val data = intent.data
        val query = data?.pathSegments?.first()
        val decodedQuery =
            query?.let { URLDecoder.decode(query.toString(), "utf-8") }

        decodedQuery?.let { searchViewModel.searchRepos(decodedQuery) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            setIntent(it)
            handleIntent()
        }
    }

    inner class RepoAdapter : ListAdapter<Repo, RepoViewHolder>(RepoDiffUtil()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
            val binding = ItemRepoBinding.inflate(layoutInflater, parent, false)
            return RepoViewHolder(binding)
        }

        override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }

    inner class RepoViewHolder(
        private val binding: ItemRepoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(repo: Repo) = binding.run {
            textViewUrl.text = repo.htmlUrl
            textViewTitle.text = repo.fullName
            textViewDescription.text = repo.description
        }
    }

    class RepoDiffUtil : DiffUtil.ItemCallback<Repo>() {
        override fun areItemsTheSame(oldItem: Repo, newItem: Repo) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Repo, newItem: Repo) =
            oldItem == newItem
    }
}