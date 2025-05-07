package com.example.bitfit

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bitfit.databinding.FragmentLogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LogFragment : Fragment() {
    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    private var selectedLog: SleepLogEntity? = null
    private var selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())

        binding.dateEditText.setText(getCurrentDate())

        binding.dateEditText.setOnClickListener {
            showDatePicker()
        }

        binding.saveButton.setOnClickListener {
            saveSleepData()
        }

        return binding.root
    }

    private fun saveSleepData() {
        val sleepHours = binding.sleepSlider.value
        val moodRating = binding.moodSlider.value.toInt()
        val notes = binding.notesInput.text.toString()
        val selectedDate = binding.dateEditText.text.toString().ifEmpty { getCurrentDate() }

        lifecycleScope.launch(Dispatchers.IO) {
            if (selectedLog != null) {
                val updatedLog = selectedLog!!.copy(
                    date = selectedDate,
                    hours = sleepHours,
                    mood = moodRating,
                    notes = notes
                )
                database.sleepLogDao().updateSleepLog(updatedLog)
            } else {
                val newLog = SleepLogEntity(date = selectedDate, hours = sleepHours, mood = moodRating, notes = notes)
                database.sleepLogDao().insertSleepLog(newLog)
            }

            selectedLog = null

            requireActivity().runOnUiThread { resetInputFields() }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "${selectedMonth + 1}/$selectedDay/$selectedYear"
            binding.dateEditText.setText(formattedDate)
        }, year, month, day)

        datePicker.show()
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun resetInputFields() {
        binding.dateEditText.setText(getCurrentDate())
        binding.sleepSlider.value = 6.0f
        binding.moodSlider.value = 5f
        binding.notesInput.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
