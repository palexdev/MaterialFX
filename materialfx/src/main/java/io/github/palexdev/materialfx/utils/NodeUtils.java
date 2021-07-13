/*
 * Copyright (C) 2021 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.FXPermission;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class which provides convenience methods for working with Nodes
 */
public class NodeUtils {
    public static final FXPermission ACCESS_WINDOW_LIST_PERMISSION = new FXPermission("accessWindowList");

    private NodeUtils() {
    }

    /**
     * Changes the background color of a {@code Region} to the desired one.
     *
     * @param region The region to change the background color to
     * @param fill   The desired color
     */
    public static void updateBackground(Region region, Paint fill) {
        final Background background = region.getBackground();
        if (background == null || background.getFills().isEmpty()) {
            return;
        }

        final List<BackgroundFill> fills = new ArrayList<>();
        for (BackgroundFill bf : background.getFills()) {
            fills.add(new BackgroundFill(fill, bf.getRadii(), bf.getInsets()));
        }

        region.setBackground(new Background(fills.toArray(BackgroundFill[]::new)));
    }

    /**
     * Changes the background color of a {@code Region} to the desired one and lets specify the background insets.
     *
     * @param region           The region to change the background color to
     * @param fill             The desired color
     * @param backgroundInsets The background insets to use
     */
    public static void updateBackground(Region region, Paint fill, Insets backgroundInsets) {
        final Background background = region.getBackground();
        if (background == null || background.getFills().isEmpty()) {
            return;
        }

        final List<BackgroundFill> fills = new ArrayList<>();
        for (BackgroundFill bf : background.getFills()) {
            fills.add(new BackgroundFill(fill, bf.getRadii(), backgroundInsets));
        }

        region.setBackground(new Background(fills.toArray(BackgroundFill[]::new)));
    }

    public static void updateBackground(Region region, Paint fill, CornerRadii cornerRadii, Insets backgroundInsets) {
        final Background background = region.getBackground();
        if (background == null || background.getFills().isEmpty()) {
            return;
        }

        final List<BackgroundFill> fills = new ArrayList<>();
        for (BackgroundFill bf : background.getFills()) {
            fills.add(new BackgroundFill(fill, cornerRadii, backgroundInsets));
        }

        region.setBackground(new Background(fills.toArray(BackgroundFill[]::new)));
    }

    /**
     * Sets the background of the given region to the given color.
     */
    public static void setBackground(Region region, Paint fill) {
        region.setBackground(new Background(new BackgroundFill(fill, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    /**
     * Centers the specified node in an {@code AnchorPane}.
     */
    public static void centerNodeInAnchorPane(Node node, double topBottom, double leftRight) {
        AnchorPane.setTopAnchor(node, topBottom);
        AnchorPane.setBottomAnchor(node, topBottom);
        AnchorPane.setLeftAnchor(node, leftRight);
        AnchorPane.setRightAnchor(node, leftRight);
    }

    /**
     * Checks if the specified element is in the hierarchy of the specified node.
     */
    public static boolean inHierarchy(Node node, Node element) {
        if (element == null) {
            return true;
        }
        while (node != null) {
            if (node == element) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    /**
     * Makes the given region circular.
     * <p>
     * <b>Notice: the region's pref width and height must be set and be equals</b>
     *
     * @param region The given region
     */
    public static void makeRegionCircular(Region region) {
        Circle circle = new Circle();
        circle.radiusProperty().bind(region.widthProperty().divide(2.0));
        circle.centerXProperty().bind(region.widthProperty().divide(2.0));
        circle.centerYProperty().bind(region.heightProperty().divide(2.0));
        try {
            region.setClip(circle);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Could not set region's clip to make it circular", ex);
        }
    }

    /**
     * Makes the given region circular with the specified radius.
     * <p>
     * <b>Notice: the region's pref width and height must be set and be equals</b>
     *
     * @param region The given region
     * @param radius The wanted radius
     */
    public static void makeRegionCircular(Region region, double radius) {
        Circle circle = new Circle(radius);
        circle.centerXProperty().bind(region.widthProperty().divide(2.0));
        circle.centerYProperty().bind(region.heightProperty().divide(2.0));
        try {
            region.setClip(circle);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Could not set region's clip to make it circular", ex);
        }
    }

    /**
     * Retrieves the region height if it isn't still laid out.
     *
     * @param region the Region of which to know the height
     * @return the calculated height
     */
    public static double getRegionHeight(Region region) {
        Group group = new Group(region);
        Scene scene = new Scene(group);
        group.applyCss();
        group.layout();

        group.getChildren().clear();
        return region.getHeight();
    }

    /**
     * Retrieves the region width if it isn't still laid out.
     *
     * @param region the Region of which to know the width
     * @return the calculated width
     */
    public static double getRegionWidth(Region region) {
        Group group = new Group(region);
        Scene scene = new Scene(group);
        group.applyCss();
        group.layout();

        group.getChildren().clear();
        return region.getWidth();
    }

    /**
     * Retrieves the node height if it isn't still laid out.
     *
     * @param node the Node of which to know the height
     * @return the calculated height
     */
    public static double getNodeHeight(Node node) {
        Group group = new Group(node);
        Scene scene = new Scene(group);
        group.applyCss();
        group.layout();

        double height = node.prefHeight(-1);
        group.getChildren().clear();
        return height;
    }

    /**
     * Retrieves the node width if it isn't still laid out.
     *
     * @param node the Node of which to know the width
     * @return the calculated width
     */
    public static double getNodeWidth(Node node) {
        Group group = new Group(node);
        Scene scene = new Scene(group);
        group.applyCss();
        group.layout();

        double width = node.prefWidth(-1);
        group.getChildren().clear();
        return width;
    }

    /**
     * Convenience method for adding the desired value to the region's prefWidth
     */
    public static void addPrefWidth(Region region, double value) {
        double prefW = region.getPrefWidth();
        region.setPrefWidth(prefW + value);
    }

    /**
     * Convenience method for adding the desired value to the region's prefHeight
     */
    public static void addPrefHeight(Region region, double value) {
        double prefH = region.getPrefHeight();
        region.setPrefHeight(prefH + value);
    }

    /**
     * Convenience method for programmatically fire a dummy MOUSE_PRESSED event on the desired node.
     */
    public static void fireDummyEvent(Node node) {
        Event.fireEvent(node, new MouseEvent(MouseEvent.MOUSE_PRESSED,
                0, 0, 0, 0, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, false, false, false, null));
    }

    /**
     * Checks if the given alignment is set to RIGHT(any).
     */
    public static boolean isRightAlignment(Pos alignment) {
        return alignment == Pos.BASELINE_RIGHT || alignment == Pos.BOTTOM_RIGHT ||
                alignment == Pos.CENTER_RIGHT || alignment == Pos.TOP_RIGHT;
    }

    /**
     * Recursively gets all nodes that are descendants of the given root.
     */
    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent) node, nodes);
        }
    }

    /**
     * Convenience method to execute a given action after that the given control
     * has been laid out and its skin is not null anymore.
     * <p></p>
     * If the skin is not null when called, the action is executed immediately.
     * <p>
     * The listener is added only if the skin is null or the addListenerIfNotNull parameter is true.
     *
     * @param control              the control to check for skin initialization
     * @param action               the action to perform when the skin is not null
     * @param addListenerIfNotNull to specify if the listener should be added anyway even if the scene is not null
     * @param isOneShot            to specify if the listener added to the skin property
     *                             should be removed after it is not null anymore
     */
    public static void waitForSkin(Control control, Runnable action, boolean addListenerIfNotNull, boolean isOneShot) {
        if (control.getSkin() != null) {
            action.run();
        }

        if (control.getSkin() == null || addListenerIfNotNull) {
            control.skinProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> newValue) {
                    if (newValue != null) {
                        action.run();
                        if (isOneShot) {
                            control.skinProperty().removeListener(this);
                        }
                    }
                }
            });
        }
    }

    /**
     * Convenience method to execute a given action after that the given control
     * has been laid out and its scene is not null anymore.
     * <p></p>
     * If the scene is not null when called, the action is executed immediately.
     * <p>
     * The listener is added only if the scene is null or the addListenerIfNotNull parameter is true.
     *
     * @param control              the control to check for scene initialization
     * @param action               the action to perform when the scene is not null
     * @param addListenerIfNotNull to specify if the listener should be added anyway even if the scene is not null
     * @param isOneShot            to specify if the listener added to the scene property
     *                             should be removed after it is not null anymore
     */
    public static void waitForScene(Control control, Runnable action, boolean addListenerIfNotNull, boolean isOneShot) {
        if (control.getScene() != null) {
            action.run();
        }

        if (control.getScene() == null || addListenerIfNotNull) {
            control.sceneProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                    if (newValue != null) {
                        action.run();
                        if (isOneShot) {
                            control.sceneProperty().removeListener(this);
                        }
                    }
                }
            });
        }
    }

    public static boolean isPseudoClassActive(Control control, PseudoClass pseudoClass) {
        return control.getPseudoClassStates().contains(pseudoClass);
    }

    //================================================================================
    // JavaFX private methods
    //================================================================================
    /* The following methods are copied from com.sun.javafx.scene.control.skin.Utils class
     * It's a private module, so to avoid adding exports and opens I copied them
     */

    public static double computeXOffset(double width, double contentWidth, HPos hpos) {
        switch (hpos) {
            case LEFT:
                return 0;
            case CENTER:
                return (width - contentWidth) / 2;
            case RIGHT:
                return width - contentWidth;
        }
        return 0;
    }

    public static double computeYOffset(double height, double contentHeight, VPos vpos) {

        switch (vpos) {
            case CENTER:
                return (height - contentHeight) / 2;
            case BOTTOM:
                return height - contentHeight;
            case TOP:
            default:
                return 0;
        }
    }

    public static Point2D pointRelativeTo(Node parent, Node node, HPos hpos,
                                          VPos vpos, double dx, double dy, boolean reposition) {
        final double nodeWidth = node.getLayoutBounds().getWidth();
        final double nodeHeight = node.getLayoutBounds().getHeight();
        return pointRelativeTo(parent, nodeWidth, nodeHeight, hpos, vpos, dx, dy, reposition);
    }

    public static Point2D pointRelativeTo(Node parent, double anchorWidth,
                                          double anchorHeight, HPos hpos, VPos vpos, double dx, double dy,
                                          boolean reposition) {
        final Bounds parentBounds = getBounds(parent);
        Scene scene = parent.getScene();
        NodeOrientation orientation = parent.getEffectiveNodeOrientation();

        if (orientation == NodeOrientation.RIGHT_TO_LEFT) {
            if (hpos == HPos.LEFT) {
                hpos = HPos.RIGHT;
            } else if (hpos == HPos.RIGHT) {
                hpos = HPos.LEFT;
            }
            dx *= -1;
        }

        double layoutX = positionX(parentBounds, anchorWidth, hpos) + dx;
        final double layoutY = positionY(parentBounds, anchorHeight, vpos) + dy;

        if (orientation == NodeOrientation.RIGHT_TO_LEFT && hpos == HPos.CENTER) {
            if (scene.getWindow() instanceof Stage) {
                layoutX = layoutX + parentBounds.getWidth() - anchorWidth;
            } else {
                layoutX = layoutX - parentBounds.getWidth() - anchorWidth;
            }
        }

        if (reposition) {
            return pointRelativeTo(parent, anchorWidth, anchorHeight, layoutX, layoutY, hpos, vpos);
        } else {
            return new Point2D(layoutX, layoutY);
        }
    }

    /**
     * This is the fallthrough function that most other functions fall into. It takes
     * care specifically of the repositioning of the item such that it remains onscreen
     * as best it can, given it's unique qualities.
     * <p>
     * As will all other functions, this one returns a Point2D that represents an x,y
     * location that should safely position the item onscreen as best as possible.
     * <p>
     * Note that <code>width</code> and <height> refer to the width and height of the
     * node/popup that is needing to be repositioned, not of the parent.
     * <p>
     * Don't use the BASELINE vpos, it doesn't make sense and would produce wrong result.
     */
    public static Point2D pointRelativeTo(Object parent, double width,
                                          double height, double screenX, double screenY, HPos hpos, VPos vpos) {
        double finalScreenX = screenX;
        double finalScreenY = screenY;
        final Bounds parentBounds = getBounds(parent);

        // ...and then we get the bounds of this screen
        final Screen currentScreen = getScreen(parent);
        final Rectangle2D screenBounds =
                hasFullScreenStage(currentScreen)
                        ? currentScreen.getBounds()
                        : currentScreen.getVisualBounds();

        // test if this layout will force the node to appear outside
        // of the screens bounds. If so, we must reposition the item to a better position.
        // We firstly try to do this intelligently, so as to not overlap the parent if
        // at all possible.
        if (hpos != null) {
            // Firstly we consider going off the right hand side
            if ((finalScreenX + width) > screenBounds.getMaxX()) {
                finalScreenX = positionX(parentBounds, width, getHPosOpposite(hpos, vpos));
            }

            // don't let the node go off to the left of the current screen
            if (finalScreenX < screenBounds.getMinX()) {
                finalScreenX = positionX(parentBounds, width, getHPosOpposite(hpos, vpos));
            }
        }

        if (vpos != null) {
            // don't let the node go off the bottom of the current screen
            if ((finalScreenY + height) > screenBounds.getMaxY()) {
                finalScreenY = positionY(parentBounds, height, getVPosOpposite(hpos, vpos));
            }

            // don't let the node out of the top of the current screen
            if (finalScreenY < screenBounds.getMinY()) {
                finalScreenY = positionY(parentBounds, height, getVPosOpposite(hpos, vpos));
            }
        }

        // --- after all the moving around, we do one last check / rearrange.
        // Unlike the check above, this time we are just fully committed to keeping
        // the item on screen at all costs, regardless of whether or not that results
        /// in overlapping the parent object.
        if ((finalScreenX + width) > screenBounds.getMaxX()) {
            finalScreenX -= (finalScreenX + width - screenBounds.getMaxX());
        }
        if (finalScreenX < screenBounds.getMinX()) {
            finalScreenX = screenBounds.getMinX();
        }
        if ((finalScreenY + height) > screenBounds.getMaxY()) {
            finalScreenY -= (finalScreenY + height - screenBounds.getMaxY());
        }
        if (finalScreenY < screenBounds.getMinY()) {
            finalScreenY = screenBounds.getMinY();
        }

        return new Point2D(finalScreenX, finalScreenY);
    }

    private static double positionX(Bounds parentBounds, double width, HPos hpos) {
        if (hpos == HPos.CENTER) {
            // this isn't right, but it is needed for root menus to show properly
            return parentBounds.getMinX();
        } else if (hpos == HPos.RIGHT) {
            return parentBounds.getMaxX();
        } else if (hpos == HPos.LEFT) {
            return parentBounds.getMinX() - width;
        } else {
            return 0;
        }
    }

    /**
     * Utility function that returns the y-axis position that an object should be positioned at,
     * given the parents screen bounds, the height of the object, and
     * the required VPos.
     * <p>
     * The BASELINE vpos doesn't make sense here, 0 is returned for it.
     */
    private static double positionY(Bounds parentBounds, double height, VPos vpos) {
        if (vpos == VPos.BOTTOM) {
            return parentBounds.getMaxY();
        } else if (vpos == VPos.CENTER) {
            return parentBounds.getMinY();
        } else if (vpos == VPos.TOP) {
            return parentBounds.getMinY() - height;
        } else {
            return 0;
        }
    }

    /**
     * To facilitate multiple types of parent object, we unfortunately must allow for
     * Objects to be passed in. This method handles determining the bounds of the
     * given Object. If the Object type is not supported, a default Bounds will be returned.
     */
    private static Bounds getBounds(Object obj) {
        if (obj instanceof Node) {
            final Node n = (Node) obj;
            Bounds b = n.localToScreen(n.getLayoutBounds());
            return b != null ? b : new BoundingBox(0, 0, 0, 0);
        } else if (obj instanceof Window) {
            final Window window = (Window) obj;
            return new BoundingBox(window.getX(), window.getY(), window.getWidth(), window.getHeight());
        } else {
            return new BoundingBox(0, 0, 0, 0);
        }
    }

    /*
     * Simple utility function to return the 'opposite' value of a given HPos, taking
     * into account the current VPos value. This is used to try and avoid overlapping.
     */
    private static HPos getHPosOpposite(HPos hpos, VPos vpos) {
        if (vpos == VPos.CENTER) {
            if (hpos == HPos.LEFT) {
                return HPos.RIGHT;
            } else if (hpos == HPos.RIGHT) {
                return HPos.LEFT;
            } else if (hpos == HPos.CENTER) {
                return HPos.CENTER;
            } else {
                // by default center for now
                return HPos.CENTER;
            }
        } else {
            return HPos.CENTER;
        }
    }

    /*
     * Simple utitilty function to return the 'opposite' value of a given VPos, taking
     * into account the current HPos value. This is used to try and avoid overlapping.
     */
    private static VPos getVPosOpposite(HPos hpos, VPos vpos) {
        if (hpos == HPos.CENTER) {
            if (vpos == VPos.BASELINE) {
                return VPos.BASELINE;
            } else if (vpos == VPos.BOTTOM) {
                return VPos.TOP;
            } else if (vpos == VPos.CENTER) {
                return VPos.CENTER;
            } else if (vpos == VPos.TOP) {
                return VPos.BOTTOM;
            } else {
                // by default center for now
                return VPos.CENTER;
            }
        } else {
            return VPos.CENTER;
        }
    }

    public static boolean hasFullScreenStage(final Screen screen) {
        final List<Window> allWindows = AccessController.doPrivileged(
                (PrivilegedAction<List<Window>>) Window::getWindows,
                null,
                ACCESS_WINDOW_LIST_PERMISSION);

        for (final Window window : allWindows) {
            if (window instanceof Stage) {
                final Stage stage = (Stage) window;
                if (stage.isFullScreen() && (getScreen(stage) == screen)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Screen getScreen(Object obj) {
        final Bounds parentBounds = getBounds(obj);

        final Rectangle2D rect = new Rectangle2D(
                parentBounds.getMinX(),
                parentBounds.getMinY(),
                parentBounds.getWidth(),
                parentBounds.getHeight());

        return getScreenForRectangle(rect);
    }

    public static Screen getScreenForRectangle(final Rectangle2D rect) {
        final List<Screen> screens = Screen.getScreens();

        final double rectX0 = rect.getMinX();
        final double rectX1 = rect.getMaxX();
        final double rectY0 = rect.getMinY();
        final double rectY1 = rect.getMaxY();

        Screen selectedScreen;

        selectedScreen = null;
        double maxIntersection = 0;
        for (final Screen screen : screens) {
            final Rectangle2D screenBounds = screen.getBounds();
            final double intersection =
                    getIntersectionLength(rectX0, rectX1,
                            screenBounds.getMinX(),
                            screenBounds.getMaxX())
                            * getIntersectionLength(rectY0, rectY1,
                            screenBounds.getMinY(),
                            screenBounds.getMaxY());

            if (maxIntersection < intersection) {
                maxIntersection = intersection;
                selectedScreen = screen;
            }
        }

        if (selectedScreen != null) {
            return selectedScreen;
        }

        selectedScreen = Screen.getPrimary();
        double minDistance = Double.MAX_VALUE;
        for (final Screen screen : screens) {
            final Rectangle2D screenBounds = screen.getBounds();
            final double dx = getOuterDistance(rectX0, rectX1,
                    screenBounds.getMinX(),
                    screenBounds.getMaxX());
            final double dy = getOuterDistance(rectY0, rectY1,
                    screenBounds.getMinY(),
                    screenBounds.getMaxY());
            final double distance = dx * dx + dy * dy;

            if (minDistance > distance) {
                minDistance = distance;
                selectedScreen = screen;
            }
        }

        return selectedScreen;
    }

    private static double getIntersectionLength(
            final double a0, final double a1,
            final double b0, final double b1) {
        // (a0 <= a1) && (b0 <= b1)
        return (a0 <= b0) ? getIntersectionLengthImpl(b0, b1, a1)
                : getIntersectionLengthImpl(a0, a1, b1);
    }

    private static double getIntersectionLengthImpl(
            final double v0, final double v1, final double v) {
        // (v0 <= v1)
        if (v <= v0) {
            return 0;
        }

        return (v <= v1) ? v - v0 : v1 - v0;
    }

    private static double getOuterDistance(
            final double a0, final double a1,
            final double b0, final double b1) {
        // (a0 <= a1) && (b0 <= b1)
        if (a1 <= b0) {
            return b0 - a1;
        }

        if (b1 <= a0) {
            return b1 - a0;
        }

        return 0;
    }
}
