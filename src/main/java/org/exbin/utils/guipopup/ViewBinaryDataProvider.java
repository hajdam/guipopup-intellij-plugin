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
package org.exbin.utils.guipopup;

import com.intellij.openapi.extensions.PluginDescriptor;
import org.exbin.bined.intellij.api.BinaryViewData;
import org.exbin.bined.intellij.api.BinaryViewHandler;
import org.jetbrains.annotations.NotNull;

import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * View binary data provider.
 */
@NullMarked
public class ViewBinaryDataProvider implements BinaryViewData {

    private static BinaryViewHandler binaryViewHandler = null;

    @Nullable
    public static BinaryViewHandler getBinaryViewHandler() {
        return binaryViewHandler;
    }

    @Override
    public void passHandler(BinaryViewHandler handler) {
        binaryViewHandler = handler;
    }

    @Override
    public void setPluginDescriptor(@NotNull PluginDescriptor pluginDescriptor) {

    }
}
