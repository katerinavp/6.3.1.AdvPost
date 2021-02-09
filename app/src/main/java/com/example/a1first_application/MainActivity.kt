package com.example.a1first_application

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a1first_application.databinding.ActivityMainBinding
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val urlSimplePost = "https://raw.githubusercontent.com/katerinavp/6.3.1.Json_v2/master/posts_simple.json"
    private val urlAdvPost = "https://raw.githubusercontent.com/katerinavp/6.3.1.Json_v2/master/posts_Adv.json"
    lateinit var adapter: AdapterPost

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdapterPost()
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            binding.progressBar.isVisible = true
            getResultFromGit()
        }

    }

    private suspend fun getResultFromGit() {
        delay(5000)
        val client = HttpClient {
            install(JsonFeature) {
                acceptContentTypes = listOf(
                        ContentType.Text.Plain,
                        ContentType.Application.Json
                )
                serializer = GsonSerializer()
            }
        }

        // тестовый ответ будет десериализован в List<Post>
        val responseSimple = client.get<List<Post>>(urlSimplePost)
        println("Десериализация + ${responseSimple}")
        client.close()

        val responseAdvPost = client.get<List<Post>>(urlAdvPost)
        println("Десериализация + ${responseAdvPost}")
        client.close()
        setResponse(responseSimple, responseAdvPost)
        binding.progressBar.isInvisible = true
    }

    private fun setResponse(listSimple: List<Post>, listAdv: List<Post>) {

        adapter.submitList(mixPosts(listSimple, listAdv))
    }

    private fun mixPosts(
            listSimple: List<Post>,
            listAdv: List<Post>,
            every: Int = 3,
    ): MutableList<Post> =
            listSimple.foldIndexed(mutableListOf()) { index, acc, post ->
                val postIndex = index / every
                if (index != 0 && index % every == 0 && postIndex in listAdv.indices) {
                    acc.add(listAdv[postIndex])
                }
                acc.add(post)
                acc
            }
}
































