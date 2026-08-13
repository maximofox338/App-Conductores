package com.rideunify.domain.accessibility

import java.time.Instant

data class ScreenSnapshot(
    val packageName: String,
    val capturedAt: Instant,
    val root: ScreenNode,
) {
    fun flattenedLabels(): List<String> {
        val labels = mutableListOf<String>()

        fun visit(node: ScreenNode) {
            labels += node.visibleLabels()
            node.children.forEach(::visit)
        }

        visit(root)
        return labels.distinct()
    }
}
