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

import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.components.JBCheckBoxMenuItem;
import com.intellij.ui.components.JBMenu;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.exbin.jaguif.utils.UiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.jspecify.annotations.NullMarked;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

/**
 * Module installer.
 */
@NullMarked
public class DefaultPopupInstaller implements StartupActivity.DumbAware, AppLifecycleListener, ProjectActivity { /* ApplicationInitializedListener */

    private boolean installed = false;

    @Override
    public void runActivity(Project project) {
        install();
    }

    private void install() {
        if (!installed) {
            IntelliJDefaultPopupMenu.register();
            UiUtils.setMenuBuilder(new UiUtils.MenuBuilder() {
                @NotNull
                @Override
                public JMenu buildMenu() {
                    return new JBMenu();
                }

                @NotNull
                @Override
                public JPopupMenu buildPopupMenu() {
                    return new JBPopupMenu();
                }

                @NotNull
                @Override
                public JMenuItem buildMenuItem() {
                    return new JBMenuItem("");
                }

                @NotNull
                @Override
                public JCheckBoxMenuItem buildCheckBoxMenuItem() {
                    return new JBCheckBoxMenuItem();
                }

                @NotNull
                @Override
                public JRadioButtonMenuItem buildRadioButtonMenuItem() {
                    return new JRadioButtonMenuItem();
                }
            });
            installed = true;
        }
    }

    @Override
    public void appStarted() {
        install();
    }

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        runActivity(project);
        return null;
    }

    //    public void componentsInitialized() {
//        install();
//    }
}
