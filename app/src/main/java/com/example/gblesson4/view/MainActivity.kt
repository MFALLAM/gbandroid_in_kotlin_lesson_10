package com.example.gblesson4.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.gblesson4.R
import com.example.gblesson4.camera.ContactsFragment
import com.example.gblesson4.view.cities.CitiesListFragment
import com.example.gblesson4.view.history.HistoryListFragment
import com.example.gblesson4.view.map.MapsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, CitiesListFragment.newInstance())
                .commitNow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.history_menu_item -> {
                navigateTo(HistoryListFragment.newInstance())
            }

            R.id.map_menu_item -> {
                navigateTo(MapsFragment.newInstance())
            }

            R.id.contacts_menu_item -> {
                navigateTo(ContactsFragment.newInstance())
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }

        return true
    }

    private fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack("")
            .commit()
    }

}