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
import com.blogspot.alexeykutovenko.githubber.network.RepoItem
import com.blogspot.alexeykutovenko.githubber.network.RetrofitFactory
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import kotlin.system.measureTimeMillis

class HomeFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private var data: List<RepoItem> = ArrayList()
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

        btn_go.setOnClickListener{
//            val username = et_username.text.toString()
            val username = "square"

            val service = RetrofitFactory.makeGithubService()
            CoroutineScope(Dispatchers.IO).launch {
                val request = service.getReposAsync(username)
                withContext(Dispatchers.Main) {
                    try {
                        val response = request.await()
                        if (response.isSuccessful) {
                            data = (response.body()!!.asSequence()).toList() //И это всё? Где еще использовать sequence?
                            (rv_repo_list.adapter as HomeAdapter).setValues(data)

                        } else {
                            toast("Error: ${response.code()}")
                        }
                    } catch (e: HttpException) {
                        toast("Exception ${e.message}")
                    } catch (e: Throwable) {
                        toast("Ooops: Something else went wrong")
                    }
                }
            }
        }

        btn_list.setOnClickListener{
            if (data.isEmpty()) toast("No data. Please choose repository")
            else doBenchmark {processWithList(data)}
        }

        btn_sequence.setOnClickListener{
            if (data.isEmpty()) toast("No data. Please choose repository")
            else doBenchmark {processWithSequence(data)}
        }

    }

    private fun doBenchmark(doProcess: (List<RepoItem>) -> List<RepoItem>){
            val newArray: ArrayList<RepoItem> = ArrayList()
            time = measureTimeMillis { newArray.addAll(doProcess(data)) }
            (rv_repo_list.adapter as HomeAdapter).setValues(newArray)
            tv_sequence_result.text = "$time ms."
    }

    private fun processWithList (data: List<RepoItem>): List<RepoItem> {
        return data.filter { it.language == "Java" }
            .filter { it.topics!!.contains("Android") }
            .sortedByDescending { it.stargazers_count }
            .toList()
    }

    private fun processWithSequence(data: List<RepoItem>): List<RepoItem> {
        return data.asSequence()
            .filter { it.language == "Java" }
            .filter { it.topics!!.contains("Android") }
            .sortedByDescending { it.stargazers_count }
            .toList()
    }


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

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
