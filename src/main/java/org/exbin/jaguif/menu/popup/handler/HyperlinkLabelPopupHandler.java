/*
 * Copyright (C) ExBin Project, https://exbin.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.jaguif.menu.popup.handler;

import com.intellij.ui.HyperlinkLabel;
import org.exbin.jaguif.action.api.clipboard.TextClipboardOperationController;
import org.exbin.jaguif.menu.popup.LinkActionsHandler;
import org.exbin.jaguif.utils.ClipboardUtils;

import org.jspecify.annotations.NullMarked;
import java.awt.datatransfer.StringSelection;

/**
 * Popup handler for hyperlink label.
 */
@NullMarked
public class HyperlinkLabelPopupHandler implements TextClipboardOperationController, LinkActionsHandler {

    private final HyperlinkLabel hyperlinkLabel;

    public HyperlinkLabelPopupHandler(HyperlinkLabel hyperlinkLabel) {
        this.hyperlinkLabel = hyperlinkLabel;
    }

    @Override
    public void performCut() {
        throw new IllegalStateException();
    }

    @Override
    public void performCopy() {
        throw new IllegalStateException();
    }

    @Override
    public void performPaste() {
        throw new IllegalStateException();
    }

    @Override
    public void performDelete() {
        throw new IllegalStateException();
    }

    @Override
    public void performSelectAll() {
        throw new IllegalStateException();
    }

    @Override
    public boolean hasSelection() {
        return false;
    }

    @Override
    public boolean hasDataToCopy() {
        return hasSelection();
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public boolean canSelectAll() {
        return false;
    }

    @Override
    public boolean isValidForPaste() {
        return isEditable();
    }


    @Override
    public boolean canDelete() {
        return false;
    }

    @Override
    public void performCopyLink() {
        StringSelection stringSelection = new StringSelection(hyperlinkLabel.getText());
        ClipboardUtils.getClipboard().setContents(stringSelection, stringSelection);
    }

    @Override
    public void performOpenLink() {
        hyperlinkLabel.doClick();
    }

    @Override
    public boolean isLinkSelected() {
        return true;
    }
}
