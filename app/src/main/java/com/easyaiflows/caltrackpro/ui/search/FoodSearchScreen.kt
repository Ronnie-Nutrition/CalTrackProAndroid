package com.easyaiflows.caltrackpro.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.easyaiflows.caltrackpro.domain.model.SearchedFood
import com.easyaiflows.caltrackpro.ui.search.components.FoodSearchResultItem
import com.easyaiflows.caltrackpro.ui.search.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSearchScreen(
    onNavigateBack: () -> Unit,
    onFoodSelected: (SearchedFood) -> Unit,
    viewModel: FoodSearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Foods") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            SearchBar(
                query = uiState.query,
                onQueryChange = viewModel::updateQuery,
                onClear = viewModel::clearSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Tab row
            PrimaryTabRow(
                selectedTabIndex = uiState.selectedTab.ordinal
            ) {
                Tab(
                    selected = uiState.selectedTab == SearchTab.SEARCH,
                    onClick = { viewModel.selectTab(SearchTab.SEARCH) },
                    text = { Text("Search") }
                )
                Tab(
                    selected = uiState.selectedTab == SearchTab.RECENT,
                    onClick = { viewModel.selectTab(SearchTab.RECENT) },
                    text = { Text("Recent") }
                )
                Tab(
                    selected = uiState.selectedTab == SearchTab.FAVORITES,
                    onClick = { viewModel.selectTab(SearchTab.FAVORITES) },
                    text = { Text("Favorites") }
                )
            }

            // Content based on tab and state
            when (uiState.selectedTab) {
                SearchTab.SEARCH -> SearchContent(
                    uiState = uiState,
                    onFoodSelected = { food ->
                        viewModel.onFoodSelected(food)
                        onFoodSelected(food)
                    },
                    onToggleFavorite = viewModel::toggleFavorite,
                    onRetry = viewModel::retry
                )

                SearchTab.RECENT -> RecentContent(
                    recentSearches = uiState.recentSearches,
                    onFoodSelected = { food ->
                        viewModel.onFoodSelected(food)
                        onFoodSelected(food)
                    },
                    onToggleFavorite = viewModel::toggleFavorite,
                    onClearRecent = viewModel::clearRecentSearches
                )

                SearchTab.FAVORITES -> FavoritesContent(
                    favorites = uiState.favorites,
                    onFoodSelected = { food ->
                        viewModel.onFoodSelected(food)
                        onFoodSelected(food)
                    },
                    onToggleFavorite = viewModel::toggleFavorite
                )
            }
        }
    }
}

@Composable
private fun SearchContent(
    uiState: FoodSearchUiState,
    onFoodSelected: (SearchedFood) -> Unit,
    onToggleFavorite: (SearchedFood) -> Unit,
    onRetry: () -> Unit
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            ErrorContent(
                message = uiState.error!!,
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize()
            )
        }

        uiState.isEmpty -> {
            EmptyContent(
                query = uiState.query,
                modifier = Modifier.fillMaxSize()
            )
        }

        uiState.isInitial -> {
            InitialContent(
                modifier = Modifier.fillMaxSize()
            )
        }

        uiState.hasResults -> {
            FoodList(
                foods = uiState.results,
                favorites = uiState.favorites,
                onFoodSelected = onFoodSelected,
                onToggleFavorite = onToggleFavorite
            )
        }
    }
}

@Composable
private fun RecentContent(
    recentSearches: List<SearchedFood>,
    onFoodSelected: (SearchedFood) -> Unit,
    onToggleFavorite: (SearchedFood) -> Unit,
    onClearRecent: () -> Unit
) {
    if (recentSearches.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No recent searches",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            FoodList(
                foods = recentSearches,
                favorites = emptyList(),
                onFoodSelected = onFoodSelected,
                onToggleFavorite = onToggleFavorite,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FavoritesContent(
    favorites: List<SearchedFood>,
    onFoodSelected: (SearchedFood) -> Unit,
    onToggleFavorite: (SearchedFood) -> Unit
) {
    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No favorite foods yet\nTap the heart icon to add favorites",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    } else {
        FoodList(
            foods = favorites,
            favorites = favorites,
            onFoodSelected = onFoodSelected,
            onToggleFavorite = onToggleFavorite
        )
    }
}

@Composable
private fun FoodList(
    foods: List<SearchedFood>,
    favorites: List<SearchedFood>,
    onFoodSelected: (SearchedFood) -> Unit,
    onToggleFavorite: (SearchedFood) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteIds = favorites.map { it.foodId }.toSet()

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(
            items = foods,
            key = { it.foodId }
        ) { food ->
            FoodSearchResultItem(
                food = food,
                isFavorite = favoriteIds.contains(food.foodId),
                onClick = { onFoodSelected(food) },
                onToggleFavorite = { onToggleFavorite(food) }
            )
        }
    }
}

@Composable
private fun InitialContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Type at least 2 characters to search for foods",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyContent(
    query: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No results found for \"$query\"",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Retry")
            }
        }
    }
}
