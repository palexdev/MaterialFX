package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.beans.PopupPositionBean;
import io.github.palexdev.materialfx.controls.MFXPopup;
import javafx.animation.Animation;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;

/**
 * This is the skin associated with every {@link MFXPopup}.
 * <p></p>
 * The popup's content is shown in a container (a {@link StackPane}).
 */
public class MFXPopupSkin implements Skin<MFXPopup> {
    //================================================================================
    // Properties
    //================================================================================
    private MFXPopup popup;
    private final StackPane container;
    private final Scale scale;

    private Animation animation;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXPopupSkin(MFXPopup popup) {
        this.popup = popup;

        container = new StackPane(popup.getContent());
        scale = new Scale(0.1, 0.1, 0, 0);
        container.getTransforms().add(scale);
        init();

        if (popup.isAnimated()) {
            animation = popup.getAnimationProvider().apply(container, scale);
            animation.play();
        } else {
            scale.setX(1);
            scale.setY(1);
            container.setOpacity(1);
        }
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Positions the popup.
     */
    protected void init() {
        container.setOpacity(0.0);

        PopupPositionBean position = popup.getPosition();
        if (position == null) return;

        double containerW = container.prefWidth(-1);
        double containerH = container.prefHeight(-1);
        HPos hPos = position.getHPos();
        VPos vPos = position.getVPos();
        double xOffset = position.getXOffset();
        double yOffset = position.getYOffset();

        double tx = 0;
        double ty = 0;
        double px = hPos == HPos.RIGHT ? xOffset : containerW + xOffset;
        double py = vPos == VPos.BOTTOM ? yOffset : containerH + yOffset;

        switch (hPos) {
            case CENTER: {
                tx = -(Math.abs(containerW - position.getOwnerWidth()) / 2) + xOffset;
                break;
            }
            case LEFT: {
                tx = -containerW + xOffset;
                break;
            }
            case RIGHT: {
                tx = xOffset;
                break;
            }
        }
        switch (vPos) {
            case BOTTOM: {
                ty = yOffset;
                break;
            }
            case CENTER: {
                ty = -(Math.abs(containerH - position.getOwnerHeight()) / 2) + yOffset;
                break;
            }
            case TOP: {
                ty = -containerH + yOffset;
                break;
            }
        }

        scale.setPivotX(px);
        scale.setPivotY(py);
        container.setTranslateX(tx);
        container.setTranslateY(ty);
    }

    @Override
    public MFXPopup getSkinnable() {
        return popup;
    }

    @Override
    public Node getNode() {
        return container;
    }

    @Override
    public void dispose() {
        animation.stop();
        animation = null;
        popup = null;
    }
}
