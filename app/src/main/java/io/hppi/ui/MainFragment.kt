package io.hppi.ui

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import io.hppi.databinding.MainFragmentBinding
import io.hppi.events.EventObserver
import io.hppi.extensions.shareCompat
import io.hppi.repositories.HeadphoneStateRepository
import io.hppi.services.enqueueHPPiWorker
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
            lifecycleOwner = viewLifecycleOwner
            viewModel = vm

            subscribeUi()
        }.root
    }

    private fun MainFragmentBinding.subscribeUi() {
        vm.processDescription()
        vm.onAbort.observe(viewLifecycleOwner, EventObserver {
            requireActivity().finish()
        })
        vm.onDone.observe(requireActivity(), EventObserver {
            requireActivity().enqueueHPPiWorker()
            HeadphoneStateRepository
                .getInstance(requireContext())
                .headphoneStateListener = vm
        })
        vm.onTrying.observe(requireActivity(), EventObserver { testNotifyMsg ->
            Snackbar.make(root, testNotifyMsg, Snackbar.LENGTH_INDEFINITE).show()
        })
        vm.onTestFinished.observe(requireActivity(), EventObserver { testFinishedNotifyMsg ->
            Snackbar.make(root, testFinishedNotifyMsg, Snackbar.LENGTH_INDEFINITE).setAction(
                R.string.ok
            ) {
                requireActivity().finish()
            }.show()
        })
        vm.onShareApp.observe(requireActivity(), EventObserver { shareText ->
            requireActivity().shareCompat(shareText)
        })
    }
}
