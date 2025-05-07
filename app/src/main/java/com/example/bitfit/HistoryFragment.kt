package com.example.bitfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bitfit.databinding.FragmentHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val sleepLogs = mutableListOf<SleepLogEntity>()
    private lateinit var sleepLogAdapter: SleepLogAdapter
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())

        setupRecyclerView()
        fetchSleepLogs()

        return binding.root
    }

    private fun setupRecyclerView() {
        sleepLogAdapter = SleepLogAdapter(
            sleepLogs,
            itemClickListener = { /* Handle item click if needed */ },
            deleteClickListener = { sleepLog -> deleteSleepLog(sleepLog) }
        )

        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sleepLogAdapter
        }
    }

    private fun fetchSleepLogs() {
        lifecycleScope.launch {
            database.sleepLogDao().getAllSleepLogs().collect { databaseList ->
                sleepLogAdapter.updateData(databaseList)

                binding.emptyStateText.visibility =
                    if (databaseList.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun deleteSleepLog(sleepLog: SleepLogEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            database.sleepLogDao().deleteSleepLog(sleepLog.id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.historyRecyclerView.adapter = null
        _binding = null
    }
}
