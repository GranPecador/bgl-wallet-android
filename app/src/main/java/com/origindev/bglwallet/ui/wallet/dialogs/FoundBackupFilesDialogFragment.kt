package com.origindev.bglwallet.ui.wallet.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.origindev.bglwallet.BackupFilesAdapterRecyclerView
import com.origindev.bglwallet.R
import java.io.File

class FoundBackupFilesDialogFragment(val adapterFiles: BackupFilesAdapterRecyclerView) :
    DialogFragment() {

    var listener: OnFileBackupSelected? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val customView =
            activity!!.layoutInflater.inflate(R.layout.fragment_found_backup_files_dialog, null)
        customView.findViewById<RecyclerView>(R.id.files_backup_recycler).apply {
            adapter = adapterFiles
            layoutManager = LinearLayoutManager(context)
        }
        customView.findViewById<Button>(R.id.not_selected_file_backup_button).setOnClickListener {
            listener?.onNotFileBackupSelected()
            dismiss()
        }
        return AlertDialog.Builder(requireContext(), R.style.Theme_Walletv1_Dialog)
            .setView(customView)
            .create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnFileBackupSelected
        if (listener == null) {
            throw ClassCastException("$context must implement OnArticleSelectedListener")
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFileBackupSelected {
        fun onSelectedFile(file: File)
        fun onNotFileBackupSelected()
    }
}