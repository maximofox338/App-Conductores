package com.rideunify.domain.accessibility

data class ScreenNode(
    val text: String?,
    val contentDescription: String?,
    val className: String?,
    val isClickable: Boolean,
    val bounds: NodeBounds?,
    val children: List<ScreenNode> = emptyList(),
) {
    fun visibleLabels(): List<String> =
        listOfNotNull(text, contentDescription)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
}
