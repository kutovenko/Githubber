package com.blogspot.alexeykutovenko.githubber.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.blogspot.alexeykutovenko.githubber.BaseActivity
import com.blogspot.alexeykutovenko.githubber.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity(), HomeFragment.OnFragmentInteractionListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle(R.string.app_name)
        setSupportActionBar(toolbar)
        addFragment(HomeFragment(), R.id.cl_main_container)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Вот тут я пока не понял, если честно, как правильно на Kotlin это должно выглядеть. Не срабатывает смена локали.
        return when (item.itemId) {
            R.id.action_english -> {updateLocale(Locale.ENGLISH); true}
            R.id.action_russian -> {updateLocale(Locale("ru", "RU")); true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int){
        supportFragmentManager.inTransaction { add(frameId, fragment) }
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    override fun onFragmentInteraction(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
