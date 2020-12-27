package io.github.palexdev.materialfx.utils;

import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
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
     * @param region The region to change the background color to
     * @param fill The desired color
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
     * @param region The region to change the background color to
     * @param fill The desired color
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
            LoggingUtils.logException("Could not set region's clip to make it circular", ex);
        }
    }

    /**
     * Makes the given region circular with the specified radius.
     * <p>
     * <b>Notice: the region's pref width and height must be set and be equals</b>
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
            LoggingUtils.logException("Could not set region's clip to make it circular", ex);
        }
    }

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
            case TOP:
                return 0;
            case CENTER:
                return (height - contentHeight) / 2;
            case BOTTOM:
                return height - contentHeight;
            default:
                return 0;
        }
    }

    public static Point2D pointRelativeTo(Node parent, Node node, HPos hpos,
                                          VPos vpos, double dx, double dy, boolean reposition)
    {
        final double nodeWidth = node.getLayoutBounds().getWidth();
        final double nodeHeight = node.getLayoutBounds().getHeight();
        return pointRelativeTo(parent, nodeWidth, nodeHeight, hpos, vpos, dx, dy, reposition);
    }

    public static Point2D pointRelativeTo(Node parent, double anchorWidth,
                                          double anchorHeight, HPos hpos, VPos vpos, double dx, double dy,
                                          boolean reposition)
    {
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
     *
     * As will all other functions, this one returns a Point2D that represents an x,y
     * location that should safely position the item onscreen as best as possible.
     *
     * Note that <code>width</code> and <height> refer to the width and height of the
     * node/popup that is needing to be repositioned, not of the parent.
     *
     * Don't use the BASELINE vpos, it doesn't make sense and would produce wrong result.
     */
    public static Point2D pointRelativeTo(Object parent, double width,
                                          double height, double screenX, double screenY, HPos hpos, VPos vpos)
    {
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
                finalScreenY = positionY(parentBounds, height, getVPosOpposite(hpos,vpos));
            }

            // don't let the node out of the top of the current screen
            if (finalScreenY < screenBounds.getMinY()) {
                finalScreenY = positionY(parentBounds, height, getVPosOpposite(hpos,vpos));
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
     *
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
            final Node n = (Node)obj;
            Bounds b = n.localToScreen(n.getLayoutBounds());
            return b != null ? b : new BoundingBox(0, 0, 0, 0);
        } else if (obj instanceof Window) {
            final Window window = (Window)obj;
            return new BoundingBox(window.getX(), window.getY(), window.getWidth(), window.getHeight());
        } else {
            return new BoundingBox(0, 0, 0, 0);
        }
    }

    /*
     * Simple utitilty function to return the 'opposite' value of a given HPos, taking
     * into account the current VPos value. This is used to try and avoid overlapping.
     */
    private static HPos getHPosOpposite(HPos hpos, VPos vpos) {
        if (vpos == VPos.CENTER) {
            if (hpos == HPos.LEFT){
                return HPos.RIGHT;
            } else if (hpos == HPos.RIGHT){
                return HPos.LEFT;
            } else if (hpos == HPos.CENTER){
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
            if (vpos == VPos.BASELINE){
                return VPos.BASELINE;
            } else if (vpos == VPos.BOTTOM){
                return VPos.TOP;
            } else if (vpos == VPos.CENTER){
                return VPos.CENTER;
            } else if (vpos == VPos.TOP){
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
        for (final Screen screen: screens) {
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
        for (final Screen screen: screens) {
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
