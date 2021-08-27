package com.skysam.hchirinos.rosqueteslucy.ui.expenses

import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Expense

/**
 * Created by Hector Chirinos on 27/08/2021.
 */
interface OnClick {
    fun edit(expense: Expense)
    fun delete(expense: Expense)
}