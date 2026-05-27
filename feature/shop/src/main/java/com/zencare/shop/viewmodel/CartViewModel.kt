package com.zencare.shop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zencare.data.datastore.entity.CartItemEntity
import com.zencare.data.repository.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val items: List<CartItemEntity> = emptyList(),
    val total: Double = 0.0
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: ShopRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CartUiState())
    val state: StateFlow<CartUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getCartFlow().collect { items ->
                val total = items.sumOf { it.price * it.quantity }
                _state.update { CartUiState(items = items, total = total) }
            }
        }
    }

    fun removeItem(productId: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }
}
