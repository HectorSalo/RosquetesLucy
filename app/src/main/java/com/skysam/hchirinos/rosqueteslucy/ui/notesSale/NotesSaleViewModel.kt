package com.skysam.hchirinos.rosqueteslucy.ui.notesSale

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale
import com.skysam.hchirinos.rosqueteslucy.database.repositories.NoteSaleRepository

class NotesSaleViewModel : ViewModel() {
    val notesSales: LiveData<MutableList<NoteSale>> = NoteSaleRepository.getNotesSale().asLiveData()

    fun deleteNoteSale(noteSale: NoteSale) {
        NoteSaleRepository.deleteNoteSale(noteSale)
    }
}