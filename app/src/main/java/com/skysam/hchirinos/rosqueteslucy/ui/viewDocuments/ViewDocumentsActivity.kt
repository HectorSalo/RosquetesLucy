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
import com.skysam.hchirinos.rosqueteslucy.common.ClassesCommon
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Customer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Refund
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.ActivityViewDocumentsBinding
import java.util.*

class ViewDocumentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewDocumentsBinding
    private val viewModel: ViewDocumentsViewModel by viewModels()
    private val allSales = mutableListOf<Sale>()
    private val allNotesSale = mutableListOf<NoteSale>()
    private val allRefunds = mutableListOf<Refund>()
    private lateinit var customer: Customer

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
            customer = bundle.get(Constants.ID_COSTUMER) as Customer
            viewModel.addCostumer(customer)
            supportActionBar?.title = customer.name
            listSorted.addAll(customer.locations.sorted())
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
                if (position == 0) findLastDocument()
                else findLastDocumentByLocation(binding.spinner.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        loadViewModel()
    }

    private fun loadViewModel() {
        viewModel.allSales.observe(this, {
            allSales.clear()
            for (sale in it) {
                if (sale.idCostumer == customer.id) allSales.add(sale)
            }
            if (it.isNotEmpty()) {
                findLastDocument()
            }
        })
        viewModel.allNotesSales.observe(this, {
            allNotesSale.clear()
            for (noteSale in it) {
                if (noteSale.idCostumer == customer.id) allNotesSale.add(noteSale)
            }
            if (it.isNotEmpty()) {
                findLastDocument()
            }
        })
        viewModel.allRefunds.observe(this, {
            allRefunds.clear()
            for (refund in it) {
                if (refund.idCostumer == customer.id) allRefunds.add(refund)
            }
            if (it.isNotEmpty()) {
                findLastDocument()
            }
        })
    }

    private fun findLastDocument() {
        var lastSale: Sale? = null
        var lastNoteSale: NoteSale? = null
        var lastRefund: Refund? = null

        if (allSales.isNotEmpty()) lastSale = allSales.first()
        if (allNotesSale.isNotEmpty()) lastNoteSale = allNotesSale.first()
        if (allRefunds.isNotEmpty()) lastRefund = allRefunds.first()

        if (lastSale != null && lastNoteSale != null) {
            if (Date(lastSale.dateDelivery).after(Date(lastNoteSale.dateDelivery)))
                lastNoteSale = null else lastSale = null
        }
        if (lastSale != null && lastRefund != null) {
            if (Date(lastSale.dateDelivery).after(Date(lastRefund.date)))
                lastRefund = null else lastSale = null
        }
        if (lastNoteSale != null && lastRefund != null) {
            if (Date(lastNoteSale.dateDelivery).after(Date(lastRefund.date)))
                lastRefund = null else lastNoteSale = null
        }

        if (lastSale != null) binding.tvInfo.text = getString(R.string.text_last_visit_all,
            lastSale.location, ClassesCommon.convertDateToString(Date(lastSale.dateDelivery)),
            getString(R.string.text_sale_single))
        if (lastNoteSale != null) binding.tvInfo.text = getString(R.string.text_last_visit_all,
            lastNoteSale.location, ClassesCommon.convertDateToString(Date(lastNoteSale.dateDelivery)),
            getString(R.string.text_note_sale_single))
        if (lastRefund != null) binding.tvInfo.text = getString(R.string.text_last_visit_all,
            lastRefund.location, ClassesCommon.convertDateToString(Date(lastRefund.date)),
            getString(R.string.text_refund_single))
        if (lastSale == null && lastNoteSale == null && lastRefund == null)
            binding.tvInfo.text= getString(R.string.text_not_last_visit)
    }

    private fun findLastDocumentByLocation(location: String) {
        var lastSale: Sale? = null
        var lastNoteSale: NoteSale? = null
        var lastRefund: Refund? = null

        val saleFromLocation = mutableListOf<Sale>()
        val noteSaleFromLocation = mutableListOf<NoteSale>()
        val refundFromLocation = mutableListOf<Refund>()

        if (allSales.isNotEmpty()) {
            for (sal in allSales) {
                if (sal.location == location) saleFromLocation.add(sal)
            }
            if (saleFromLocation.isNotEmpty()) {
                lastSale = saleFromLocation.first()
            }
        }
        if (allNotesSale.isNotEmpty()) {
            for (noteS in allNotesSale) {
                if (noteS.location == location) noteSaleFromLocation.add(noteS)
            }
            if (noteSaleFromLocation.isNotEmpty()) {
                lastNoteSale = noteSaleFromLocation.first()
            }
        }
        if (allRefunds.isNotEmpty()) {
            for (refu in allRefunds) {
                if (refu.location == location) refundFromLocation.add(refu)
            }
            if (refundFromLocation.isNotEmpty()) {
                lastRefund = refundFromLocation.first()
            }
        }

        if (lastSale != null && lastNoteSale != null) {
            if (Date(lastSale.dateDelivery).after(Date(lastNoteSale.dateDelivery)))
                lastNoteSale = null else lastSale = null
        }
        if (lastSale != null && lastRefund != null) {
            if (Date(lastSale.dateDelivery).after(Date(lastRefund.date)))
                lastRefund = null else lastSale = null
        }
        if (lastNoteSale != null && lastRefund != null) {
            if (Date(lastNoteSale.dateDelivery).after(Date(lastRefund.date)))
                lastRefund = null else lastNoteSale = null
        }

        if (lastSale != null) binding.tvInfo.text = getString(R.string.text_last_visit_location,
            ClassesCommon.convertDateToString(Date(lastSale.dateDelivery)),
            getString(R.string.text_sale_single))
        if (lastNoteSale != null) binding.tvInfo.text = getString(R.string.text_last_visit_location,
            ClassesCommon.convertDateToString(Date(lastNoteSale.dateDelivery)),
            getString(R.string.text_note_sale_single))
        if (lastRefund != null) binding.tvInfo.text = getString(R.string.text_last_visit_location,
            ClassesCommon.convertDateToString(Date(lastRefund.date)),
            getString(R.string.text_refund_single))
        if (lastSale == null && lastNoteSale == null && lastRefund == null)
            binding.tvInfo.text= getString(R.string.text_not_last_visit)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}