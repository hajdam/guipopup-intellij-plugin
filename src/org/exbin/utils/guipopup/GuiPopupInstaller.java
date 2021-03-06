/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.utils.guipopup;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

/**
 * Module installer.
 *
 * @version 0.1.0 2019/07/22
 * @author ExBin Project (http://exbin.org)
 */
public class GuiPopupInstaller implements StartupActivity {

    private boolean installed = false;
    @Override
    public void runActivity(@NotNull Project project) {
        if (!installed) {
            ClipboardUtils.registerGuiPopupMenu();
            installed = true;
        }
    }

//    @Override
//    public void preload(@NotNull ProgressIndicator indicator) {
//        WindowManager.getInstance().addListener(new WindowManagerListener() {
//            @Override
//            public void frameCreated(IdeFrame frame) {
//                ClipboardUtils.registerGuiPopupMenu();
//            }
//
//            @Override
//            public void beforeFrameReleased(IdeFrame frame) {
//            }
//        });
//    }
}
