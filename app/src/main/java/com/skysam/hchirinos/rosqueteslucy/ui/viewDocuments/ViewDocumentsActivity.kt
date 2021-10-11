package com.skysam.hchirinos.rosqueteslucy.ui.viewDocuments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.databinding.ActivityViewDocumentsBinding

class ViewDocumentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewDocumentsBinding
    private val viewModel: ViewDocumentsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewDocumentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_view_doc) as NavHostFragment
        val navController = navHostFragment.navController

        navView.setupWithNavController(navController)

        val listSorted = mutableListOf<String>()
        val bundle = intent.extras
        if (bundle != null) {
            val costumer = bundle.get(Constants.ID_COSTUMER) as Costumer
            viewModel.addCostumer(costumer)
            supportActionBar?.title = costumer.name
            listSorted.addAll(costumer.locations.sorted())
            listSorted.add(0, Constants.ALL_LOCATIONS)
            val adapterLocations = ArrayAdapter(this, R.layout.layout_spinner, listSorted)
            binding.spinner.adapter = adapterLocations
        }

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.changeLocation(listSorted[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}