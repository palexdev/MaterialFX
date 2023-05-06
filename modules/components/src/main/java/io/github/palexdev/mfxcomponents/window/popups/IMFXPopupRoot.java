package io.github.palexdev.mfxcomponents.window.popups;

import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import io.github.palexdev.mfxcomponents.skins.MFXPopupSkin;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;

/**
 * Useful and minimal API to build skins for MaterialFX popups.
 * <p></p>
 * {@link PopupControl}s use a special 'bridge' Node to make the popup (which is a Window not a Node) styleable.
 * The idea per-se is not bad and if I'd have to implement it myself from scratch I'd probably go the same way.
 * <p>
 * However, the implementation is kinda messy, not very stable when handling style-classes, stylesheets and styleable
 * properties. And of course just like anything in JavaFX, it makes use of internal, private, obscure APIs, screw you Jigsaw!
 * <p>
 * For these reasons, the default skin of MaterialFX popups, {@link MFXPopupSkin}, uses another special root Node,
 * which will contain the popup's content, {@link IMFXPopup#contentProperty()}. This also allows to manage the content
 * as we please, we can animate it, define new CSS properties, allows to implement new features easily (e.g {@link IMFXPopup#hoverProperty()}),
 * add stylesheets directly on the content making styling easy and consistent (see {@link IMFXPopup#getStylesheets()}), and so much more.
 */
public interface IMFXPopupRoot extends MFXStyleable {

    /**
     * The root should convert itself to a Node. Usually this means creating a class that extends one type of Node
     * (could be Node, Region, a Pane,...) and returning itself.
     */
    Region toNode();

    /**
     * Needless to say that the root of a popup depends on it.
     * <p>
     * Implementations of this will often if not always specify a constructor that requires the popup's instance.
     */
    IMFXPopup getPopup();

    /**
     * Since the root may add listeners/bindings/handlers, and it's intended to be used in skins, a popup root should
     * specify the actions to dispose itself when not needed anymore (e.g. skin disposal, {@link Skin#dispose()}).
     */
    void dispose();
}