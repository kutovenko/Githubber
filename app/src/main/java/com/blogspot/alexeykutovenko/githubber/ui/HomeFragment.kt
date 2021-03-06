package com.blogspot.alexeykutovenko.githubber.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.alexeykutovenko.githubber.R
import com.blogspot.alexeykutovenko.githubber.network.GithubService
import com.blogspot.alexeykutovenko.githubber.network.RepoItem
import com.blogspot.alexeykutovenko.githubber.network.RetrofitFactory
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import kotlin.system.measureNanoTime


class HomeFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private var data: ArrayList<RepoItem> = ArrayList()
    private var time: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(rv_repo_list){
            layoutManager = LinearLayoutManager(context)
            adapter = HomeAdapter(listOf(), listener)
        }

        //Lambda with a receiver
        val processList = fun List<RepoItem>.() =
            this.filter { it.language == "Java" }
                .filter { it.topics!!.contains("android") || it.topics!!.contains("Android")}
                .sortedByDescending { it.stargazers_count }
                .toList()

        //Lambda with a receiver
        val processSequence = fun List<RepoItem>.() =
            this.asSequence()
                .filter { it.language == "Java" }
                .filter { it.topics!!.contains("android") || it.topics!!.contains("Android")}
                .sortedByDescending { it.stargazers_count }
                .toList()

        btn_go.setOnClickListener{
            val username = et_username.text.toString()

            val service = RetrofitFactory.makeGithubService()
            val pageNumber = 1

            getPagedRepositoryData(service, username, pageNumber)

            tv_sequence_result.text = ""
            tv_list_result.text = ""
        }


        btn_list.setOnClickListener{
            if (data.isEmpty()) toast("No data. Please choose repository")
            else time = doBenchmark {data.processList()}
            (rv_repo_list.adapter as HomeAdapter).setValues(data.processList())
            tv_list_result.text = "$time ms."
        }

        btn_sequence.setOnClickListener{
            if (data.isEmpty()) toast("No data. Please choose repository")
            else time = doBenchmark {data.processSequence()}
            (rv_repo_list.adapter as HomeAdapter).setValues(data.processSequence())
            tv_sequence_result.text = "$time ms."
        }

    }

    //No tailrec in try-catch?
    private fun getPagedRepositoryData(service: GithubService, username: String, pageNumber: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = service.getReposAsync(username, pageNumber.toString())
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()
                    if (response.isSuccessful) {
                        val currentPage = (response.body()!!.asSequence()).toList()
                        if (currentPage.isNotEmpty()){
                            data.addAll(currentPage)
                            getPagedRepositoryData(service, username, pageNumber + 1)
                        } else {
                            (rv_repo_list.adapter as HomeAdapter).setValues(data)
                        }

                    } else {
                        toast("Error: ${response.code()}")
                    }
                } catch (e: HttpException) {
                    toast("Exception ${e.message}")
                } catch (e: Throwable) {
                    toast("Something else went wrong")
                }
            }
        }
    }

    //Higher order function
    private fun doBenchmark(func: List<RepoItem>.() -> List<RepoItem>) = measureNanoTime { func(data)}

    private fun toast(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(url: String)
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
        @JvmStatic
        fun newInstance(columnCount: Int) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
