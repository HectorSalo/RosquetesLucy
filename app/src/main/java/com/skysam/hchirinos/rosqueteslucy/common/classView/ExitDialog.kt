package com.skysam.hchirinos.rosqueteslucy.common.classView

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.skysam.hchirinos.rosqueteslucy.R

/**
 * Created by Hector Chirinos (Home) on 1/8/2021.
 */
class ExitDialog(private val onClickExit: OnClickExit): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_confirmation_dialog))
            .setMessage(getString(R.string.msg_exit_dialog))
            .setPositiveButton(R.string.btn_exit) { _, _ ->
                onClickExit.onClickExit()
            }
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

        return dialog
    }
}