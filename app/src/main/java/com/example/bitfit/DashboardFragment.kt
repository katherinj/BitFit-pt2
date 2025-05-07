package com.example.bitfit

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bitfit.databinding.FragmentDashboardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())

        loadChartAndAverages()

        return binding.root
    }

    private fun loadChartAndAverages() {
        lifecycleScope.launch(Dispatchers.IO) {
            val logs = database.sleepLogDao().getAllSleepLogs().first().sortedBy { it.date }

            if (logs.isEmpty()) return@launch

            val averageHours = logs.map { it.hours }.average()
            val averageMood = logs.map { it.mood }.average()

            launch(Dispatchers.Main) {
                binding.averageSleepText.text =
                    "Average hours of sleep: %.1f hours".format(averageHours)
                binding.averageMoodText.text = "Average feeling: %.1f / 10".format(averageMood)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
