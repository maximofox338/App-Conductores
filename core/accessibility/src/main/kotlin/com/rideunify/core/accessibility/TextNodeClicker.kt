package com.rideunify.core.accessibility

import android.view.accessibility.AccessibilityNodeInfo
import javax.inject.Inject

class TextNodeClicker @Inject constructor() {
    fun clickFirstMatching(
        root: AccessibilityNodeInfo?,
        labels: List<String>,
    ): Boolean {
        if (root == null) return false
        val normalizedLabels = labels.map { it.normalized() }
        val target = findFirst(root) { node ->
            val nodeLabels = listOfNotNull(node.text, node.contentDescription)
                .map { it.toString().normalized() }
            nodeLabels.any { nodeLabel ->
                normalizedLabels.any { expected -> nodeLabel.contains(expected) }
            }
        } ?: return false

        return performClick(target)
    }

    private fun findFirst(
        root: AccessibilityNodeInfo,
        predicate: (AccessibilityNodeInfo) -> Boolean,
    ): AccessibilityNodeInfo? {
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(root)
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            if (predicate(node)) return node
            for (index in 0 until node.childCount) {
                node.getChild(index)?.let(queue::add)
            }
        }
        return null
    }

    private fun performClick(node: AccessibilityNodeInfo): Boolean {
        var current: AccessibilityNodeInfo? = node
        while (current != null) {
            if (current.isClickable && current.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                return true
            }
            current = current.parent
        }
        return false
    }

    private fun String.normalized(): String =
        lowercase()
            .replace(Regex("\\s+"), " ")
            .trim()
}
