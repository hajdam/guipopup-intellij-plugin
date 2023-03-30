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

import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.impl.IdeGlassPaneImpl;
import com.intellij.ui.HyperlinkLabel;
import org.exbin.framework.popup.DefaultPopupMenu;
import org.exbin.framework.popup.handler.EditorPanePopupHandler;
import org.exbin.framework.popup.handler.HyperlinkLabelPopupHandler;
import org.exbin.framework.popup.handler.ListPopupHandler;
import org.exbin.framework.popup.handler.TablePopupHandler;
import org.exbin.framework.popup.handler.TextComponentPopupHandler;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.utils.guipopup.gui.InspectComponentPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilities for default menu generation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class IntelliJDefaultPopupMenu extends DefaultPopupMenu {

    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("org.exbin.utils.guipopup.GuiPopupMenu");

    private static IntelliJDefaultPopupMenu instance = null;

    private boolean registered = false;
    private boolean inspectMode =false ;
    //    private EventQueue systemEventQueue;
    private IdeEventQueue.EventDispatcher overriddenQueue;
    private IdeEventQueue.EventDispatcher overriddenPostQueue;

    private IntelliJDefaultPopupMenu() {
    }

    @Nonnull
    public static synchronized IntelliJDefaultPopupMenu getInstance() {
        if (instance == null) {
            instance = new IntelliJDefaultPopupMenu();
//            UIManager.addPropertyChangeListener(evt -> {
//                updateUI();
//            });
        }

        return instance;
    }

    /**
     * Registers default popup menu to AWT.
     */
    public static void register() {
        IntelliJDefaultPopupMenu defaultPopupMenu = getInstance();
        if (!defaultPopupMenu.registered) {
            defaultPopupMenu.initDefaultPopupMenu();
            defaultPopupMenu.registerToEventQueue();
        }
    }

    /**
     * Registers default popup menu to AWT.
     *
     * @param resourceBundle resource bundle
     * @param resourceClass  resource class
     */
    public static void register(ResourceBundle resourceBundle, Class<?> resourceClass) {
        IntelliJDefaultPopupMenu defaultPopupMenu = getInstance();
        if (!defaultPopupMenu.registered) {
            defaultPopupMenu.initDefaultPopupMenu(resourceBundle, resourceClass);
            defaultPopupMenu.registerToEventQueue();
        }
    }

//    public static void updateUI() {
//        if (instance != null) {
//            if (instance.defaultPopupMenu != null) {
//                SwingUtilities.updateComponentTreeUI(instance.defaultPopupMenu);
//
//            }
//            if (instance.defaultEditPopupMenu != null) {
//                SwingUtilities.updateComponentTreeUI(instance.defaultEditPopupMenu);
//            }
//        }
//    }

    private void registerToEventQueue() {
        overriddenQueue = new PopupEventQueue();
        overriddenPostQueue = new PopupEventPostQueue();
//        systemEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
//        systemEventQueue.push(overriddenQueue);
        IdeEventQueue instance = IdeEventQueue.getInstance();
        instance.addDispatcher(overriddenQueue, null);
        instance.addPostprocessor(overriddenPostQueue, null);
        registered = true;
    }

    public static void unregister() {
        IntelliJDefaultPopupMenu defaultPopupMenu = getInstance();
        if (defaultPopupMenu.registered) {
            defaultPopupMenu.unregisterQueue();
        }
    }

    private void unregisterQueue() {
        IdeEventQueue instance = IdeEventQueue.getInstance();
        instance.removeDispatcher(overriddenQueue);
        instance.removePostprocessor(overriddenPostQueue);
//        overriddenQueue.push(systemEventQueue);
        registered = false;
    }

    private void initDefaultPopupMenu() {
        initDefaultPopupMenu(resourceBundle, this.getClass());
    }

    @ParametersAreNonnullByDefault
    public class PopupEventQueue implements IdeEventQueue.EventDispatcher {

        @Override
        public boolean dispatch(AWTEvent event) {
            if (event.getID() == MouseEvent.MOUSE_MOVED && inspectMode) {
                inspectMode = false;
                MouseEvent mouseEvent = (MouseEvent) event;
                Component component = getSource(mouseEvent);
                if (component == null) {
                    return false;
                }
                InspectComponentPanel inspectComponentPanel = new InspectComponentPanel();
                inspectComponentPanel.setComponent(component, null);
                Frame mainWindow = WindowManager.getInstance().getFrame(ProjectManager.getInstance().getDefaultProject());
                final WindowUtils.DialogWrapper dialog = WindowUtils.createDialog(inspectComponentPanel, mainWindow, "Inspect Component", Dialog.ModalityType.MODELESS);
                inspectComponentPanel.setCloseActionListener(e -> dialog.close());
                dialog.show();
                return true;
            }

            return false;
        }
    }

    @ParametersAreNonnullByDefault
    public class PopupEventPostQueue implements IdeEventQueue.EventDispatcher {

        @Override
        public boolean dispatch(AWTEvent event) {
            processAWTEvent(event);

            return false;
        }
    }

    protected void processAWTEvent(AWTEvent event) {
        if (event.getID() == MouseEvent.MOUSE_RELEASED || event.getID() == MouseEvent.MOUSE_PRESSED) {
            MouseEvent mouseEvent = (MouseEvent) event;

            if (mouseEvent.isPopupTrigger()) {
                if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0) {
                    // Menu was already created
                    return;
                }

//                for (ComponentPopupEventDispatcher dispatcher : clipboardEventDispatchers) {
//                    if (dispatcher.dispatchMouseEvent(mouseEvent)) {
//                        return;
//                    }
//                }

                Component component = getSource(mouseEvent);
                if (component instanceof JViewport) {
                    component = ((JViewport) component).getView();
                }

                if (component instanceof JEditorPane) {
                    activateMousePopup(mouseEvent, component, new EditorPanePopupHandler((JEditorPane) component));
                } else if (component instanceof JTextComponent) {
                    activateMousePopup(mouseEvent, component, new TextComponentPopupHandler((JTextComponent) component));
                } else if (component instanceof JList) {
                    activateMousePopup(mouseEvent, component, new ListPopupHandler((JList<?>) component));
                } else if (component instanceof JTable) {
                    activateMousePopup(mouseEvent, component, new TablePopupHandler((JTable) component));
                } else if (component instanceof HyperlinkLabel) {
                    activateMousePopup(mouseEvent, component, new HyperlinkLabelPopupHandler((HyperlinkLabel) component));
                }
            }
        } else if (event.getID() == KeyEvent.KEY_PRESSED) {
            KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.getKeyCode() == KeyEvent.VK_F12 && keyEvent.isShiftDown() && keyEvent.isAltDown() && (keyEvent.isControlDown() || keyEvent.isMetaDown())) {
                // Unable to infer component from mouse position, so simulate click instead
                inspectMode = true;
                try {
                    Robot robot = new Robot();
                    Point location = MouseInfo.getPointerInfo().getLocation();
                    robot.mouseMove(location.x, location.y);
                } catch (AWTException ex) {
                    Logger.getLogger(IntelliJDefaultPopupMenu.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_CONTEXT_MENU || (keyEvent.getKeyCode() == KeyEvent.VK_F10 && keyEvent.isShiftDown())) {
                if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0) {
                    // Menu was already created
                    return;
                }

//                for (ComponentPopupEventDispatcher dispatcher : clipboardEventDispatchers) {
//                    if (dispatcher.dispatchKeyEvent(keyEvent)) {
//                        return;
//                    }
//                }

                Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

                if (component instanceof JEditorPane) {
                    Point point;
                    try {
                        Rectangle relativeRect = ((JEditorPane) component).modelToView(((JTextComponent) component).getCaretPosition());
                        point = relativeRect == null ? null : new Point(relativeRect.x + relativeRect.width, relativeRect.y + relativeRect.height);
                    } catch (BadLocationException ex) {
                        point = null;
                    }
                    activateKeyPopup(component, point, new EditorPanePopupHandler((JEditorPane) component));
                } else if (component instanceof JTextComponent) {
                    Point point;
                    try {
                        Rectangle relativeRect = ((JTextComponent) component).modelToView(((JTextComponent) component).getCaretPosition());
                        point = relativeRect == null ? null : new Point(relativeRect.x + relativeRect.width, relativeRect.y + relativeRect.height);
                    } catch (BadLocationException ex) {
                        point = null;
                    }
                    activateKeyPopup(component, point, new TextComponentPopupHandler((JTextComponent) component));
                } else if (component instanceof JList) {
                    Point point = null;
                    int selectedIndex = ((JList<?>) component).getSelectedIndex();
                    if (selectedIndex >= 0) {
                        Rectangle cellBounds = ((JList<?>) component).getCellBounds(selectedIndex, selectedIndex);
                        point = new Point(component.getWidth() / 2, cellBounds.y);
                    }
                    activateKeyPopup(component, point, new ListPopupHandler((JList<?>) component));
                } else if (component instanceof JTable) {
                    Point point = null;
                    int selectedRow = ((JTable) component).getSelectedRow();
                    if (selectedRow >= 0) {
                        int selectedColumn = ((JTable) component).getSelectedColumn();
                        if (selectedColumn < -1) {
                            selectedColumn = 0;
                        }
                        Rectangle cellBounds = ((JTable) component).getCellRect(selectedRow, selectedColumn, false);
                        point = new Point(cellBounds.x, cellBounds.y);
                    }
                    activateKeyPopup(component, point, new TablePopupHandler((JTable) component));
                }
            }
        }
    }

    @Nullable
    @Override
    protected Component getSource(MouseEvent e) {
        return getDeepestComponent(e.getComponent(), e.getX(), e.getY());
    }

    @Nullable
    private static Component getDeepestComponent(Component parentComponent, int x, int y) {
        Component component = SwingUtilities.getDeepestComponentAt(parentComponent, x, y);

        // Workaround for buggy gui
        if (component instanceof IdeGlassPaneImpl) {
            try {
                Field myRootPane = component.getClass().getDeclaredField("myRootPane");
                myRootPane.setAccessible(true);
                JRootPane rootPane = (JRootPane) myRootPane.get(component);
                final Point lpPoint = SwingUtilities.convertPoint(parentComponent, new Point(x, y), component);
                return getDeepestComponent(rootPane.getContentPane(), lpPoint.x, lpPoint.y);
            } catch (NoSuchFieldException | IllegalAccessException e1) {
                // Cannot serve
            }
//                final Point lpPoint = SwingUtilities.convertPoint(parentComponent, e.getPoint(), component);

//                int componentCount = ((IdeGlassPaneEx) component).getComponentCount();
//                for (int i = 0; i < componentCount; i++) {
//                    Component subComponent = ((IdeGlassPaneEx) component).getComponent(i);
//                    if (subComponent.contains(e.getX(), e.getY())) {
//                        return getDeepestComponent(subComponent, e);
//                    }
//                }
//                component = SwingUtilities.getDeepestComponentAt(component, lpPoint.x, lpPoint.y);
        }
        return component;
    }
}
