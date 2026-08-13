package com.rideunify.core.accessibility

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import com.rideunify.domain.accessibility.NodeBounds
import com.rideunify.domain.accessibility.ScreenNode
import com.rideunify.domain.accessibility.ScreenSnapshot
import java.time.Instant
import javax.inject.Inject

class AccessibilitySnapshotMapper @Inject constructor() {
    fun map(
        packageName: String,
        root: AccessibilityNodeInfo,
    ): ScreenSnapshot =
        ScreenSnapshot(
            packageName = packageName,
            capturedAt = Instant.now(),
            root = root.toScreenNode(depth = 0),
        )

    private fun AccessibilityNodeInfo.toScreenNode(depth: Int): ScreenNode {
        val rect = Rect()
        getBoundsInScreen(rect)
        val children = if (depth >= MAX_DEPTH) {
            emptyList()
        } else {
            (0 until childCount)
                .asSequence()
                .mapNotNull { index -> getChild(index) }
                .take(MAX_CHILDREN_PER_NODE)
                .map { child ->
                    try {
                        child.toScreenNode(depth + 1)
                    } finally {
                        child.recycle()
                    }
                }
                .toList()
        }

        return ScreenNode(
            text = text?.toString(),
            contentDescription = contentDescription?.toString(),
            className = className?.toString(),
            isClickable = isClickable,
            bounds = NodeBounds(
                left = rect.left,
                top = rect.top,
                right = rect.right,
                bottom = rect.bottom,
            ),
            children = children,
        )
    }

    private companion object {
        const val MAX_DEPTH = 8
        const val MAX_CHILDREN_PER_NODE = 40
    }
}
