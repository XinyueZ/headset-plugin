package io.hppi.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.hppi.databinding.MainFragmentBinding
import io.hppi.viewmodels.MainViewModel
import io.hppi.viewmodels.MainViewModelFactory

class MainFragment : Fragment() {

    private val vm: MainViewModel by viewModels({ requireActivity() }) {
        MainViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return MainFragmentBinding.inflate(inflater).apply {
            viewModel = vm
        }.root
    }
}
