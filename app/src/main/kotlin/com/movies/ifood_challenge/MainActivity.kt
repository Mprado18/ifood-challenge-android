package com.movies.ifood_challenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.movies.common.FeatureEntry
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var featureEntries: Set<@JvmSuppressWildcards FeatureEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // MoviesTheme { MoviesRoot(featureEntries) }
        }
    }
}
