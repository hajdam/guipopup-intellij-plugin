/*
 * Copyright (C) ExBin Project
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
package org.exbin.utils.guipopup;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.PluginDescriptor;
import com.intellij.openapi.ui.DialogWrapper;
import org.exbin.bined.intellij.api.BinaryViewData;
import org.exbin.bined.intellij.api.BinaryViewHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * View binary data provider.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ViewBinaryDataProvider implements BinaryViewData {

    private static BinaryViewHandler binaryViewHandler = null;

    @Nullable
    public static BinaryViewHandler getBinaryViewHandler() {
        return binaryViewHandler;
    }

    @Override
    public void passHandler(BinaryViewHandler handler) {
        binaryViewHandler = handler;

//        ApplicationManager.getApplication().invokeLater(() -> {
//            byte[] testData = new byte[] {0x12, 0x34, 0x56};
//            DialogWrapper dialog = handler.createBinaryViewDialog(testData);
//            dialog.show();
//        });
    }

    @Override
    public void setPluginDescriptor(@NotNull PluginDescriptor pluginDescriptor) {

    }
}
