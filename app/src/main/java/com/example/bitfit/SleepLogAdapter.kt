package com.example.bitfit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class SleepLogAdapter(
    private var sleepLogs: MutableList<SleepLogEntity>,
    private val itemClickListener: (SleepLogEntity) -> Unit,
    private val deleteClickListener: (SleepLogEntity) -> Unit
) : RecyclerView.Adapter<SleepLogAdapter.SleepLogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepLogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return SleepLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: SleepLogViewHolder, position: Int) {
        val log = sleepLogs[position]
        holder.bind(log)
    }

    override fun getItemCount() = sleepLogs.size

    inner class SleepLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val hoursTextView: TextView = itemView.findViewById(R.id.hoursTextView)
        private val moodTextView: TextView = itemView.findViewById(R.id.moodTextView)
        private val notesTextView: TextView = itemView.findViewById(R.id.notesTextView)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        fun bind(sleepLog: SleepLogEntity) {
            dateTextView.text = sleepLog.date
            hoursTextView.text = "Slept ${sleepLog.hours} hours"
            moodTextView.text = "Feeling ${sleepLog.mood}/10"
            notesTextView.text = if (sleepLog.notes.isNullOrEmpty()) "No notes" else sleepLog.notes

            itemView.setOnClickListener {
                itemClickListener(sleepLog)
            }

            deleteButton.setOnClickListener {
                deleteClickListener(sleepLog)
            }
        }
    }

    // **NEW METHOD: Updates the data efficiently using DiffUtil**
    fun updateData(newSleepLogs: List<SleepLogEntity>) {
        val diffCallback = SleepLogDiffCallback(sleepLogs, newSleepLogs)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        sleepLogs.clear()
        sleepLogs.addAll(newSleepLogs)
        diffResult.dispatchUpdatesTo(this)
    }

    // **NEW METHOD: Remove an item efficiently**
    fun removeItem(sleepLog: SleepLogEntity) {
        val index = sleepLogs.indexOf(sleepLog)
        if (index != -1) {
            sleepLogs.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}

// **DiffUtil Callback for Better Performance**
class SleepLogDiffCallback(
    private val oldList: List<SleepLogEntity>,
    private val newList: List<SleepLogEntity>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
