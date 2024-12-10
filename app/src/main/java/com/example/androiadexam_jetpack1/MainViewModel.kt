package com.example.androiadexam_jetpack1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.androidexam_jetpack.CarouselItemGroup
import com.example.androidexam_jetpack.GetImageCarouselData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    private val _carouselData = MutableStateFlow<List<GetImageCarouselData>>(emptyList())
    val carouselData: StateFlow<List<GetImageCarouselData>> get() = _carouselData

    private val _filteredCarouselData = MutableStateFlow<List<CarouselItemGroup>>(emptyList())
    val filteredCarouselData: StateFlow<List<CarouselItemGroup>> get() = _filteredCarouselData

    private val _topCharacters = MutableStateFlow<List<Pair<Char, Int>>>(emptyList())
    val topCharacters: StateFlow<List<Pair<Char, Int>>> get() = _topCharacters

    init {
        loadCarouselData()
    }

    private fun loadCarouselData() {
        val dummyData = listOf(
            GetImageCarouselData(
                imageUrl = R.drawable.dummyimage,
                itemsPerImage = listOf(
                    CarouselItemGroup("List item title 1", "List item subtitle 1",
                        R.drawable.dummyimage
                    ),
                    CarouselItemGroup("List item title 2", "List item subtitle 2", R.drawable.dummyimage)
                )
            ),
            GetImageCarouselData(
                imageUrl = R.drawable.dummyimage,
                itemsPerImage = listOf(
                    CarouselItemGroup("List item title 3", "List item subtitle 3", R.drawable.dummyimage)
                )
            )
        )
        _carouselData.value = dummyData
        _filteredCarouselData.value = dummyData.flatMap { it.itemsPerImage }
    }

    fun searchQuery(query: String) {
        viewModelScope.launch {
            val allItems = _carouselData.value.flatMap { it.itemsPerImage }
            _filteredCarouselData.value = if (query.isBlank()) {
                allItems
            } else {
                allItems.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.subtitle.contains(query, ignoreCase = true)
                }
            }
        }
    }
    fun calculateTopCharacters(items: List<CarouselItemGroup>) {
        val characterFrequency = mutableMapOf<Char, Int>()

        items.forEach { item ->
            (item.title + item.subtitle).forEach { char ->
                if (char.isLetterOrDigit()) {
                    characterFrequency[char] = characterFrequency.getOrDefault(char, 0) + 1
                }
            }
        }

        val topThreeCharacters = characterFrequency.entries
            .sortedByDescending { it.value }
            .take(3)
            .map { it.key to it.value }

        _topCharacters.value = topThreeCharacters
    }
}