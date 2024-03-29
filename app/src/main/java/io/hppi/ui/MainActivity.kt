package io.hppi.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import io.hppi.R
import io.hppi.databinding.MainActivityBinding
import io.hppi.viewmodels.MainViewModel
import io.hppi.viewmodels.MainViewModelFactory

class MainActivity : AppCompatActivity() {
    private val vm: MainViewModel by viewModels {
        MainViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<MainActivityBinding>(this, R.layout.main_activity)
            .apply {
                lifecycleOwner = this@MainActivity
                viewModel = vm
            }
    }
}
