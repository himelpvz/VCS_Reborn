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

package com.teixeira.vcspace.editor;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import com.teixeira.vcspace.editor.completion.CompletionListAdapter;
import com.teixeira.vcspace.editor.completion.CustomCompletionLayout;
import com.teixeira.vcspace.editor.listener.OnExplainCodeListener;
import com.teixeira.vcspace.editor.listener.OnImportComponentListener;
import com.teixeira.vcspace.file.File;

import org.eclipse.tm4e.languageconfiguration.internal.model.CommentRule;

import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function0;

public class VCSpaceEditor extends CodeEditor {

    private TextActionsWindow textActions;
    private File file;
    private boolean modified;
    private OnExplainCodeListener onExplainCodeListener;
    private OnImportComponentListener onImportComponentListener;

    public VCSpaceEditor(Context context) {
        this(context, null);
    }

    public VCSpaceEditor(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VCSpaceEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VCSpaceEditor(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        textActions = new TextActionsWindow(this, null, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return Unit.INSTANCE;
            }
        });
        getComponent(EditorTextActionWindow.class).setEnabled(false);
        getComponent(EditorAutoCompletion.class).setLayout(new CustomCompletionLayout());
        getComponent(EditorAutoCompletion.class).setAdapter(new CompletionListAdapter());
        setInputType(createInputTypeFlags());
    }

    public TextActionsWindow getTextActions() {
        return textActions;
    }

    public void setTextActions(TextActionsWindow textActions) {
        this.textActions = textActions;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean getModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public OnExplainCodeListener getOnExplainCodeListener() {
        return onExplainCodeListener;
    }

    public void setOnExplainCodeListener(OnExplainCodeListener onExplainCodeListener) {
        this.onExplainCodeListener = onExplainCodeListener;
    }

    public OnImportComponentListener getOnImportComponentListener() {
        return onImportComponentListener;
    }

    public void setOnImportComponentListener(OnImportComponentListener onImportComponentListener) {
        this.onImportComponentListener = onImportComponentListener;
    }

    public CommentRule getCommentRule() {
        if (getEditorLanguage() instanceof CommentRuleProvider) {
            return ((CommentRuleProvider) getEditorLanguage()).getCommentRule();
        }
        return null;
    }

    @Override
    public void hideEditorWindows() {
        super.hideEditorWindows();
        if (textActions != null) {
            textActions.dismiss();
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (!gainFocus) {
            hideEditorWindows();
        }
    }

    @Override
    public void release() {
        super.release();
        textActions = null;
        file = null;
    }

    public void setTextActionWindow(Function1<? super VCSpaceEditor, ? extends TextActionsWindow> window) {
        textActions = window.invoke(this);
    }

    public static int createInputTypeFlags() {
        return EditorInfo.TYPE_CLASS_TEXT
            | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
            | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
    }
}
