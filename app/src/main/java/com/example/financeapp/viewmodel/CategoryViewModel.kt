package com.example.financeapp.viewmodel

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.local.entity.CategoryEntity
import com.example.financeapp.data.repository.CategoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {

    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCategory(name: String, type: String, color: Int) {
        viewModelScope.launch {
            repository.insert(CategoryEntity(name = name, type = type, color = color))
        }
    }

    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.update(category)
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.delete(category)
        }
    }

    fun getCategoriesByType(type: String): StateFlow<List<CategoryEntity>> {
        return repository.getCategoriesByType(type)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}

class CategoryViewModelFactory(private val repository: CategoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}