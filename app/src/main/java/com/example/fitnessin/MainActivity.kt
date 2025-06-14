package com.example.fitnessin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitnessin.navigation.Screen
import com.example.fitnessin.ui.components.NavigationDrawer
import com.example.fitnessin.ui.screen.*
import com.example.fitnessin.ui.theme.FitnessInTheme
import com.example.fitnessin.ui.viewmodel.AuthUiState
import com.example.fitnessin.ui.viewmodel.AuthViewModel
import com.example.fitnessin.ui.viewmodel.DashboardUiState
import com.example.fitnessin.ui.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { FitnessInTheme { FitnessInApp() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessInApp() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()

    when (authUiState) {
        is AuthUiState.Loading -> {
            LoadingScreen()
        }
        is AuthUiState.NotAuthenticated -> {
            AuthScreen(
                    onLoginSuccess = { email -> authViewModel.loginWithEmail(email) },
                    onRegisterSuccess = { email -> authViewModel.registerWithEmail(email) },
                    isLoading = false
            )
        }
        is AuthUiState.RequiresProfileSetup -> {
            val profileSetupState = authUiState as AuthUiState.RequiresProfileSetup
            ProfileSetupScreen(
                    onSaveProfile = { user ->
                        val userWithId =
                                user.copy(
                                        uid = profileSetupState.userId,
                                        email = profileSetupState.email
                                )
                        authViewModel.saveUserProfile(userWithId)
                    }
            )
        }
        is AuthUiState.Authenticated -> {
            currentUser?.let { user ->
                MainAppWithDrawer(user = user, onLogout = { authViewModel.logout() })
            }
        }
        is AuthUiState.Error -> {
            AuthScreen(
                    onLoginSuccess = { email -> authViewModel.loginWithEmail(email) },
                    onRegisterSuccess = { email -> authViewModel.registerWithEmail(email) },
                    isLoading = false,
                    errorMessage = (authUiState as AuthUiState.Error).message
            )
        }
        else -> {
            LoadingScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppWithDrawer(user: com.example.fitnessin.model.User, onLogout: () -> Unit) {
    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                NavigationDrawer(
                        user = user,
                        currentScreen = currentScreen,
                        onScreenSelected = { screen ->
                            currentScreen = screen
                            scope.launch { drawerState.close() }
                        },
                        onLogoutClick = onLogout
                )
            }
    ) {
        Scaffold(
                topBar = {
                    TopAppBar(
                            title = { Text(currentScreen.title) },
                            navigationIcon = {
                                IconButton(
                                        onClick = {
                                            scope.launch {
                                                if (drawerState.isClosed) {
                                                    drawerState.open()
                                                } else {
                                                    drawerState.close()
                                                }
                                            }
                                        }
                                ) {
                                    Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menu"
                                    )
                                }
                            },
                            colors =
                                    TopAppBarDefaults.topAppBarColors(
                                            containerColor =
                                                    MaterialTheme.colorScheme.primaryContainer,
                                            titleContentColor =
                                                    MaterialTheme.colorScheme.onPrimaryContainer,
                                            navigationIconContentColor =
                                                    MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                    )
                }
        ) { paddingValues ->
            Surface(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    color = MaterialTheme.colorScheme.background
            ) {
                when (currentScreen) {
                    Screen.DASHBOARD -> {
                        DashboardContainer(user = user)
                    }
                    Screen.PROFILE -> {
                        ProfileScreen(
                                user = user,
                                onEditProfile = {
                                    // TODO: Navigate to profile edit
                                }
                        )
                    }
                    Screen.NUTRITION -> {
                        NutritionContainer(user = user)
                    }
                    Screen.PROGRESS -> {
                        ProgressContainer(user = user)
                    }
                    Screen.GOALS -> {
                        GoalsContainer(user = user)
                    }
                    Screen.SETTINGS -> {
                        SettingsScreen(
                                user = user,
                                onEditProfile = {
                                    // TODO: Navigate to profile edit
                                },
                                onLogout = onLogout
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardContainer(user: com.example.fitnessin.model.User) {
    val dashboardViewModel: DashboardViewModel = hiltViewModel()
    val dashboardUiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
    val nutritionData by dashboardViewModel.nutritionData.collectAsStateWithLifecycle()
    val foodRecommendation by dashboardViewModel.foodRecommendation.collectAsStateWithLifecycle()
    val weightHistory by dashboardViewModel.weightHistory.collectAsStateWithLifecycle()

    LaunchedEffect(user) { dashboardViewModel.loadDashboardData(user) }

    when (dashboardUiState) {
        is DashboardUiState.Loading -> {
            LoadingScreen()
        }
        is DashboardUiState.Success -> {
            DashboardScreen(
                    nutritionData = nutritionData,
                    foodRecommendation = foodRecommendation,
                    weightHistory = weightHistory,
                    onAddWeight = { weight -> dashboardViewModel.addWeightEntry(user.uid, weight) },
                    onRefreshRecommendation = { dashboardViewModel.refreshFoodRecommendation() }
            )
        }
        is DashboardUiState.Error -> {
            ErrorScreen(
                    message = (dashboardUiState as DashboardUiState.Error).message,
                    onRetry = { dashboardViewModel.loadDashboardData(user) }
            )
        }
    }
}

@Composable
fun NutritionContainer(user: com.example.fitnessin.model.User) {
    val dashboardViewModel: DashboardViewModel = hiltViewModel()
    val nutritionData by dashboardViewModel.nutritionData.collectAsStateWithLifecycle()
    val foodRecommendation by dashboardViewModel.foodRecommendation.collectAsStateWithLifecycle()

    LaunchedEffect(user) { dashboardViewModel.loadDashboardData(user) }

    NutritionScreen(
            user = user,
            nutritionData = nutritionData,
            foodRecommendation = foodRecommendation,
            onRefreshRecommendation = { dashboardViewModel.refreshAllData(user.uid) }
    )
}

@Composable
fun ProgressContainer(user: com.example.fitnessin.model.User) {
    val dashboardViewModel: DashboardViewModel = hiltViewModel()
    val weightHistory by dashboardViewModel.weightHistory.collectAsStateWithLifecycle()

    LaunchedEffect(user) { dashboardViewModel.loadDashboardData(user) }

    ProgressScreen(
            user = user,
            weightHistory = weightHistory,
            onAddWeight = { weight: Double -> dashboardViewModel.addWeightEntry(user.uid, weight) }
    )
}

@Composable
fun GoalsContainer(user: com.example.fitnessin.model.User) {
    val dashboardViewModel: DashboardViewModel = hiltViewModel()
    val weightHistory by dashboardViewModel.weightHistory.collectAsStateWithLifecycle()

    LaunchedEffect(user) { dashboardViewModel.loadDashboardData(user) }

    GoalsScreen(
            user = user,
            weightHistory = weightHistory,
            onUpdateGoal = { newGoal: Double ->
                // TODO: Implement goal update functionality
            }
    )
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
            )
            Text(
                    text = "Memuat...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        Text(
                text = "Terjadi Kesalahan",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
                onClick = onRetry,
                colors =
                        ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                        )
        ) { Text("Coba Lagi") }
    }
}
