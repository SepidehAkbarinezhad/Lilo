package com.sepideh.lilo.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val NoteCardIcon: ImageVector
    get() {
        if (_NoteSvgrepoCom != null) return _NoteSvgrepoCom!!
        
        _NoteSvgrepoCom = ImageVector.Builder(
            name = "NoteCardIcon",
            defaultWidth = 800.dp,
            defaultHeight = 800.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(20f, 14f)
                verticalLineTo(7f)
                curveTo(20f, 5.34315f, 18.6569f, 4f, 17f, 4f)
                horizontalLineTo(7f)
                curveTo(5.34315f, 4f, 4f, 5.34315f, 4f, 7f)
                verticalLineTo(17f)
                curveTo(4f, 18.6569f, 5.34315f, 20f, 7f, 20f)
                horizontalLineTo(13.5f)
                moveTo(20f, 14f)
                lineTo(13.5f, 20f)
                moveTo(20f, 14f)
                horizontalLineTo(15.5f)
                curveTo(14.3954f, 14f, 13.5f, 14.8954f, 13.5f, 16f)
                verticalLineTo(20f)
            }
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(8f, 8f)
                horizontalLineTo(16f)
            }
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(8f, 12f)
                horizontalLineTo(12f)
            }
        }.build()
        
        return _NoteSvgrepoCom!!
    }

private var _NoteSvgrepoCom: ImageVector? = null

