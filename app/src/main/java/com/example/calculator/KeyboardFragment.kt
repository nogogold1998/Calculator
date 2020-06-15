package com.example.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.calculator.databinding.FragmentKeyboardBinding

class KeyboardFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var binding: FragmentKeyboardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeyboardBinding.inflate(layoutInflater, container, false)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonBackspace.setOnLongClickListener {
            mainViewModel.onClear()
            true
        }
        super.onViewCreated(view, savedInstanceState)
    }
}