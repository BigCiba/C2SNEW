package com.example.c2snew.ui.notifications

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.c2snew.R
import com.example.c2snew.databinding.FragmentNotificationsBinding
import com.example.c2snew.ui.page.MainPage
import com.example.c2snew.ui.page.SettingPage
import com.example.c2snew.ui.page.Spectrum
import com.example.c2snew.ui.theme.Material3Theme
import kotlinx.coroutines.delay

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val composeView = root.findViewById<ComposeView>(R.id.composeView)
        composeView.setContent {
            Material3Theme {
                var countDown by remember { mutableIntStateOf(3) }
                LaunchedEffect(Unit) {
                    while (countDown > 0) {
                        delay(1000)
                        countDown--
                    }
                    // 自动跳转或者执行其他操作
                    navigateToNextScreen()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { innerPadding->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            ElevatedButton(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .align(Alignment.TopEnd),
                                onClick = { navigateToNextScreen() }
                            ) {
                                if (countDown > 0) {
                                    Text(text = "Skip in $countDown")
                                } else {
                                    Text(text = "Enter")
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(40.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(painter = painterResource(id = R.drawable.logo), contentDescription = "")
                                Text(modifier = Modifier.padding(0.dp,20.dp,0.dp,0.dp),text = "Professional of Micro", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Spectromer", fontSize = 30.sp, fontWeight = FontWeight.Bold)

                                ElevatedButton(modifier = Modifier.padding(0.dp,20.dp,0.dp,0.dp), onClick = {
                                    openBrowserWithUrl("https://spectroships.com")
                                }) {
                                    Text(text = "spectroships.com")
                                }
                            }
                        }
                    },
                )
            }
        }
        return root
    }
    private fun openBrowserWithUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
    private fun navigateToNextScreen() {
        val navController = findNavController()
        navController.navigate( R.id.navigation_home)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}