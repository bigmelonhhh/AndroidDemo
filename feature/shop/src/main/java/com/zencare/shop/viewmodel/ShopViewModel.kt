package com.zencare.shop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zencare.common.result.AppResult
import com.zencare.data.repository.ShopRepository
import com.zencare.model.dto.Product
import com.zencare.model.dto.ProductCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShopUiState(
    val products: List<Product> = emptyList(),
    val selectedCategory: ProductCategory? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val repository: ShopRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ShopUiState())
    val state: StateFlow<ShopUiState> = _state.asStateFlow()

    fun loadProducts(category: ProductCategory? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, selectedCategory = category) }
            when (val result = repository.getProducts(category = category)) {
                is AppResult.Success -> _state.update {
                    it.copy(isLoading = false, products = result.data.products)
                }
                is AppResult.Error -> _state.update {
                    it.copy(isLoading = false, error = result.message)
                }
                else -> {}
            }
        }
    }
}
