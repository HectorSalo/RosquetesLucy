package com.skysam.hchirinos.rosqueteslucy.ui.notesSale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Customer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale
import com.skysam.hchirinos.rosqueteslucy.database.repositories.CostumerRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.NoteSaleRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.SalesRepository

class NotesSaleViewModel : ViewModel() {
    val costumers: LiveData<MutableList<Customer>> = CostumerRepository.getCostumers().asLiveData()
    val notesSales: LiveData<MutableList<NoteSale>> = NoteSaleRepository.getNotesSale().asLiveData()
    val valueWeb: LiveData<String> = SalesRepository.getValueWeb().asLiveData()

    private val _indexPage = MutableLiveData<Int>()
    val indexPage: LiveData<Int> get() = _indexPage

    private val _textSearch = MutableLiveData<String>()
    val textSearch: LiveData<String> get() = _textSearch

    fun changePage(index: Int) {
        _indexPage.value = index
    }

    fun newTextSearch(text: String) {
        _textSearch.value = text
    }

    fun deleteNoteSale(noteSale: NoteSale) {
        NoteSaleRepository.deleteNoteSale(noteSale)
    }

    fun paidNoteSale(noteSale: NoteSale) {
        NoteSaleRepository.paidNoteSale(noteSale)
    }
}