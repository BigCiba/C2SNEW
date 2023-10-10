package com.example.c2snew.ui.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.c2snew.R
import com.example.c2snew.databinding.FragmentDashboardBinding
import com.google.android.material.textfield.TextInputEditText

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root = binding.root
        val dashboardViewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]

        val heightInput = root.findViewById<TextInputEditText>(R.id.heightInput)
        val widthInput = root.findViewById<TextInputEditText>(R.id.widthInput)

        // 从 ViewModel 中读取数据并设置到 EditText 中
        val height = dashboardViewModel.getHeight()
        if (height != null) {
            heightInput.setText(height)
        }
        val width = dashboardViewModel.getWidth()
        if (width != null) {
            widthInput.setText(width)
        }

        // 监听 EditText 的文本变化，并将其保存到 ViewModel 中
        heightInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                dashboardViewModel.setHeight(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        widthInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                dashboardViewModel.setWidth(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}