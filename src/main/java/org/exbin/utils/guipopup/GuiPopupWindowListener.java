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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;

import org.jspecify.annotations.NullMarked;

/**
 * Tool window manager listener.
 */
@NullMarked
public class GuiPopupWindowListener implements ToolWindowManagerListener {

    private final Project project;

    public GuiPopupWindowListener(Project project) {
        this.project = project;
    }

    @Override
    public void stateChanged(ToolWindowManager toolWindowManager) {
        // handle the state change
    }
}
