package com.origindev.bglwallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.origindev.bglwallet.ui.wallet.dialogs.FoundBackupFilesDialogFragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class BackupFilesAdapterRecyclerView(
    private val files: Array<File>,
    private val listener: FoundBackupFilesDialogFragment.OnFileBackupSelected
) : RecyclerView.Adapter<BackupFilesAdapterRecyclerView.FileViewHolder>() {
    init {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_describe_item, parent, false)
        return FileViewHolder(view)
    }

    private val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.ENGLISH)
    private fun getDateString(time: Long): String = simpleDateFormat.format(time)

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val item = files[position]
        holder.nameView.text = item.name
        holder.dateView.text = getDateString(item.lastModified())
        holder.pathView.text = item.absolutePath
        holder.itemView.setOnClickListener {
            listener.onSelectedFile(item)
        }
    }

    override fun getItemCount(): Int = files.size


    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView = itemView.findViewById<TextView>(R.id.filename_files_backup_item)
        val dateView = itemView.findViewById<TextView>(R.id.last_modified_files_backup_item)
        val pathView = itemView.findViewById<TextView>(R.id.path_file_files_backup_item)
    }
}