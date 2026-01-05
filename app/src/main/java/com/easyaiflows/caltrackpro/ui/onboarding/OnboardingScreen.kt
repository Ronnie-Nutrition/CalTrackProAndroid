package com.easyaiflows.caltrackpro.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.easyaiflows.caltrackpro.ui.onboarding.components.ActivityLevelPage
import com.easyaiflows.caltrackpro.ui.onboarding.components.BodyMetricsPage
import com.easyaiflows.caltrackpro.ui.onboarding.components.CompletionPage
import com.easyaiflows.caltrackpro.ui.onboarding.components.MacroPresetPage
import com.easyaiflows.caltrackpro.ui.onboarding.components.PersonalInfoPage
import com.easyaiflows.caltrackpro.ui.onboarding.components.ReviewPage
import com.easyaiflows.caltrackpro.ui.onboarding.components.WeightGoalPage
import com.easyaiflows.caltrackpro.ui.onboarding.components.WelcomePage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { OnboardingUiState.TOTAL_PAGES })

    // Sync pager with view model state
    LaunchedEffect(uiState.currentPage) {
        if (pagerState.currentPage != uiState.currentPage.ordinal) {
            pagerState.animateScrollToPage(uiState.currentPage.ordinal)
        }
    }

    // Sync view model with pager
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (page != uiState.currentPage.ordinal) {
                viewModel.goToPage(OnboardingPage.entries[page])
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    if (uiState.currentPage != OnboardingPage.WELCOME) {
                        IconButton(onClick = { viewModel.previousPage() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    if (uiState.currentPage != OnboardingPage.WELCOME &&
                        uiState.currentPage != OnboardingPage.COMPLETION) {
                        TextButton(onClick = {
                            // Skip to completion
                            viewModel.goToPage(OnboardingPage.COMPLETION)
                        }) {
                            Text("Skip")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            OnboardingBottomBar(
                currentPage = uiState.currentPage,
                canProceed = uiState.canProceed(),
                isSaving = uiState.isSaving,
                onNext = { viewModel.nextPage() },
                onComplete = {
                    viewModel.saveProfile(onComplete)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Page indicator
            PageIndicator(
                currentPage = uiState.currentPage.ordinal,
                totalPages = OnboardingUiState.TOTAL_PAGES,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )

            // Pager content
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false, // Disable swipe, use buttons only
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (OnboardingPage.entries[page]) {
                    OnboardingPage.WELCOME -> WelcomePage()
                    OnboardingPage.PERSONAL_INFO -> PersonalInfoPage(
                        age = uiState.age,
                        sex = uiState.sex,
                        ageError = uiState.ageError,
                        onAgeChange = viewModel::updateAge,
                        onSexChange = viewModel::updateSex
                    )
                    OnboardingPage.BODY_METRICS -> BodyMetricsPage(
                        weightKg = uiState.weightKg,
                        heightCm = uiState.heightCm,
                        unitSystem = uiState.unitSystem,
                        weightError = uiState.weightError,
                        heightError = uiState.heightError,
                        onWeightChange = { viewModel.updateWeight(it, inUserUnits = false) },
                        onHeightChange = { viewModel.updateHeight(it, inUserUnits = false) },
                        onHeightFeetInchesChange = viewModel::updateHeightFeetInches,
                        onUnitSystemChange = viewModel::updateUnitSystem
                    )
                    OnboardingPage.ACTIVITY_LEVEL -> ActivityLevelPage(
                        activityLevel = uiState.activityLevel,
                        onActivityLevelChange = viewModel::updateActivityLevel
                    )
                    OnboardingPage.WEIGHT_GOAL -> WeightGoalPage(
                        weightGoal = uiState.weightGoal,
                        calculatedTDEE = uiState.calculatedTDEE,
                        onWeightGoalChange = viewModel::updateWeightGoal
                    )
                    OnboardingPage.MACRO_PRESET -> MacroPresetPage(
                        macroPreset = uiState.macroPreset,
                        onMacroPresetChange = viewModel::updateMacroPreset
                    )
                    OnboardingPage.REVIEW -> ReviewPage(
                        state = uiState,
                        onCalorieOverrideChange = viewModel::setCalorieOverride
                    )
                    OnboardingPage.COMPLETION -> CompletionPage()
                }
            }
        }
    }
}

@Composable
private fun PageIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(totalPages) { index ->
            val isCurrentPage = index == currentPage
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (isCurrentPage) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCurrentPage) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
            )
        }
    }
}

@Composable
private fun OnboardingBottomBar(
    currentPage: OnboardingPage,
    canProceed: Boolean,
    isSaving: Boolean,
    onNext: () -> Unit,
    onComplete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        when (currentPage) {
            OnboardingPage.WELCOME -> {
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Get Started")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }
            }
            OnboardingPage.COMPLETION -> {
                Button(
                    onClick = onComplete,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Start Tracking")
                    }
                }
            }
            else -> {
                Button(
                    onClick = onNext,
                    enabled = canProceed,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        }
    }
}
