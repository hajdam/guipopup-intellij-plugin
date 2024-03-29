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
package org.exbin.framework.utils;

import com.intellij.openapi.Disposable;
import org.exbin.utils.guipopup.utils.DialogUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

/**
 * Utility static methods usable for windows and dialogs.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public final class WindowUtils {

    public static final String ESC_CANCEL_KEY = "esc-cancel";
    public static final String ENTER_OK_KEY = "enter-ok";

    private WindowUtils() {
    }

    public static void invokeWindow(final Window window) {
        java.awt.EventQueue.invokeLater(() -> {
            if (window instanceof JDialog) {
                ((JDialog) window).setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            }
            window.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            window.setVisible(true);
        });
    }

    @Nonnull
    public static DialogWrapper createDialog(final JComponent component, Component parent, String dialogTitle, Dialog.ModalityType modalityType) {
        final com.intellij.openapi.ui.DialogWrapper dialog = DialogUtils.createDialog(component, dialogTitle);
        dialog.setTitle(dialogTitle);
        return new DialogWrapper() {
            @Override
            public void show() {
                dialog.showAndGet();
            }

            @Override
            public void showCentered(@Nullable Component component) {
                center(component);
                show();
            }

            @Override
            public void close() {
                dialog.close(0);
            }

            @Override
            public void dispose() {
                Disposable disposable = dialog.getDisposable();
                disposable.dispose();
            }

            @Nonnull
            @Override
            public Window getWindow() {
                return dialog.getWindow();
            }

            @Nonnull
            @Override
            public Container getParent() {
                return dialog.getOwner();
            }

            @Override
            public void center(@Nullable Component component) {
                if (component == null) {
                    center();
                } else {
//                    dialog.setLocationRelativeTo(component);
                    dialog.centerRelativeToParent();
                }
            }

            @Override
            public void center() {
                dialog.centerRelativeToParent();
            }
        };
    }

    @Nonnull
    public static JDialog createDialog(final JComponent component) {
        JDialog dialog = new JDialog();
        Dimension size = component.getPreferredSize();
        dialog.add(component);
        dialog.getContentPane().setPreferredSize(new Dimension(size.width, size.height));
        dialog.pack();
        return dialog;
    }

    public static void invokeDialog(final JComponent component) {
        JDialog dialog = createDialog(component);
        invokeWindow(dialog);
    }

    public static void closeWindow(Window window) {
        window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Assign ESCAPE/ENTER key for all focusable components recursively.
     *
     * @param component   target component
     * @param closeButton button which will be used for closing operation
     */
    public static void assignGlobalKeyListener(Component component, final JButton closeButton) {
        assignGlobalKeyListener(component, closeButton, closeButton);
    }

    /**
     * Assign ESCAPE/ENTER key for all focusable components recursively.
     *
     * @param component target component
     * @param okButton button which will be used for default ENTER
     * @param cancelButton button which will be used for closing operation
     */
    public static void assignGlobalKeyListener(Component component, final JButton okButton, final JButton cancelButton) {
        assignGlobalKeyListener(component, new OkCancelListener() {
            @Override
            public void okEvent() {
                UiUtils.doButtonClick(okButton);
            }

            @Override
            public void cancelEvent() {
                UiUtils.doButtonClick(cancelButton);
            }
        });
    }

    /**
     * Assign ESCAPE/ENTER key for all focusable components recursively.
     *
     * @param component target component
     * @param listener ok and cancel event listener
     */
    public static void assignGlobalKeyListener(Component component, @Nullable final OkCancelListener listener) {
        JRootPane rootPane = SwingUtilities.getRootPane(component);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ESC_CANCEL_KEY);
        rootPane.getActionMap().put(ESC_CANCEL_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (listener == null) {
                    return;
                }

                boolean performCancelAction = true;

                Window window = SwingUtilities.getWindowAncestor(event.getSource() instanceof JRootPane ? (JRootPane) event.getSource() : rootPane);
                if (window != null) {
                    Component focusOwner = window.getFocusOwner();
                    if (focusOwner instanceof JComboBox) {
                        performCancelAction = !((JComboBox) focusOwner).isPopupVisible();
                    } else if (focusOwner instanceof JRootPane) {
                        // Ignore in popup menus
                        // performCancelAction = false;
                    }
                }

                if (performCancelAction) {
                    listener.cancelEvent();
                }
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ENTER_OK_KEY);
        rootPane.getActionMap().put(ENTER_OK_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (listener == null) {
                    return;
                }

                boolean performOkAction = true;

                Window window = SwingUtilities.getWindowAncestor(event.getSource() instanceof JRootPane ? (JRootPane) event.getSource() : rootPane);
                if (window != null) {
                    Component focusOwner = window.getFocusOwner();
                    if (focusOwner instanceof JTextArea || focusOwner instanceof JEditorPane) {
                        performOkAction = !((JTextComponent) focusOwner).isEditable();
                    }
                }

                if (performOkAction) {
                    listener.okEvent();
                }
            }
        });
    }

    @ParametersAreNonnullByDefault
    public interface DialogWrapper {

        void show();

        void showCentered(@Nullable Component window);

        void close();

        void dispose();

        @Nonnull
        Window getWindow();

        @Nonnull
        Container getParent();

        void center(@Nullable Component window);

        void center();
    }
}
