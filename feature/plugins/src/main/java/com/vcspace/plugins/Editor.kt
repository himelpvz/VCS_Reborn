/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.vcspace.plugins

import android.content.Context
import com.vcspace.plugins.editor.Position
import com.vcspace.plugins.editor.Range
import java.io.File

/**
 * Represents the editor within the application.
 */
interface Editor {
    val currentFile: File?

    val context: Context

    var cursorPosition: Position

    fun insertText(position: Position, text: String)

    fun replaceText(start: Position, end: Position, text: String)

    fun replaceText(range: Range, text: String) {
        replaceText(range.start, range.end, text)
    }

    fun deleteText(start: Position, end: Position) {
        replaceText(start, end, "")
    }

    fun deleteText(range: Range) {
        replaceText(range, "")
    }

    val selectionRange: Range?

    fun getText(): String

    fun getText(range: Range?): String?
}
