/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package interactive;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.palexdev.mfxcore.input.KeyModifier;
import io.github.palexdev.mfxcore.input.KeyStroke;
import io.github.palexdev.mfxcore.input.ShortcutManager;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static io.github.palexdev.mfxcore.input.KeyStroke.keyStroke;
import static io.github.palexdev.mfxcore.input.ShortcutManager.Action.action;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(ApplicationExtension.class)
public class ShortcutManagerTest {

    private Stage stage;
    private Scene scene;
    private StackPane root;

    @Start
    void start(Stage stage) {
        this.stage = stage;
        root = new StackPane();
        scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    @AfterEach
    void tearDown() throws Exception {
        sceneManagers().clear();
    }

    //================================================================================
    // Install(scene)
    //================================================================================

    @Test
    @DisplayName("installOn(Scene): null scene throws NPE")
    void installOnScene_nullThrows() {
        assertThrows(NullPointerException.class,
            () -> ShortcutManager.installOn((Scene) null, action("A", () -> {})));
    }

    @Test
    @DisplayName("installOn(Scene): fires on matching key")
    void installOnScene_fires() {
        AtomicInteger c = new AtomicInteger();
        ShortcutManager.installOn(scene, action("A", c::incrementAndGet));
        fire(scene, KeyCode.A);
        assertEquals(1, c.get());
    }

    @Test
    @DisplayName("installOn(Scene): fires every time the shortcut is pressed")
    void installOnScene_firesRepeatedly() {
        AtomicInteger c = new AtomicInteger();
        ShortcutManager.installOn(scene, action("A", c::incrementAndGet));

        fire(scene, KeyCode.A);
        fire(scene, KeyCode.A);
        fire(scene, KeyCode.A);
        assertEquals(3, c.get());
    }

    @Test
    @DisplayName("installOn(Scene): wrong key does not fire")
    void installOnScene_wrongKey() {
        AtomicInteger c = new AtomicInteger();
        ShortcutManager.installOn(scene, action("A", c::incrementAndGet));

        fire(scene, KeyCode.B);
        assertEquals(0, c.get());
    }

    @Test
    @DisplayName("installOn(Scene): missing modifier does not fire")
    void installOnScene_missingModifier(FxRobot robot) {
        AtomicInteger c = new AtomicInteger();
        ShortcutManager.installOn(scene, action("Ctrl+S", c::incrementAndGet));

        fire(scene, KeyCode.S);
        assertEquals(0, c.get());
    }

    @Test
    @DisplayName("installOn(Scene): correct modifier fires")
    void installOnScene_correctModifier() {
        AtomicInteger c = new AtomicInteger();
        ShortcutManager.installOn(scene, action("Ctrl+S", c::incrementAndGet));

        fire(scene, KeyCode.S, KeyCode.CONTROL);
        assertEquals(1, c.get());
    }

    @Test
    @DisplayName("installOn(Scene): extra modifier does not fire")
    void installOnScene_extraModifier() {
        AtomicInteger c = new AtomicInteger();
        ShortcutManager.installOn(scene, action("Ctrl+S", c::incrementAndGet));

        fire(scene, KeyCode.S, KeyCode.CONTROL, KeyCode.SHIFT);
        assertEquals(0, c.get());
    }

    @Test
    @DisplayName("installOn(Scene): Shortcut modifier check")
    void installOnScene_shortcutModifier() {
        AtomicInteger c = new AtomicInteger();
        ShortcutManager.installOn(scene, action(keyStroke(KeyCode.SHORTCUT, KeyCode.A), c::incrementAndGet));

        fire(scene, KeyCode.A, KeyModifier.SHORTCUT.keyCode());
        assertEquals(1, c.get());
    }

    @Test
    @DisplayName("installOn(Scene): multiple actions on different shortcuts fire independently")
    void installOnScene_multipleActions() {
        AtomicInteger cA = new AtomicInteger(), cB = new AtomicInteger();
        ShortcutManager.installOn(scene,
            action("A", cA::incrementAndGet),
            action("B", cB::incrementAndGet));

        fire(scene, KeyCode.A);
        fire(scene, KeyCode.B);
        assertEquals(1, cA.get());
        assertEquals(1, cB.get());
    }

    @Test
    @DisplayName("installOn(Scene): two calls on the same scene accumulate actions")
    void installOnScene_accumulatesAcrossCalls() {
        AtomicInteger cA = new AtomicInteger(), cB = new AtomicInteger();
        ShortcutManager.installOn(scene, action("A", cA::incrementAndGet));
        ShortcutManager.installOn(scene, action("B", cB::incrementAndGet));

        fire(scene, KeyCode.A);
        fire(scene, KeyCode.B);
        assertEquals(1, cA.get());
        assertEquals(1, cB.get());
    }

    @Test
    @DisplayName("installOn(Scene): duplicate shortcut on same scene throws")
    void installOnScene_duplicateThrows() {
        ShortcutManager.installOn(scene, action("A", () -> {}));
        assertThrows(Exception.class,
            () -> ShortcutManager.installOn(scene, action("A", () -> {})));
    }

    //================================================================================
    // Install(Node)
    //================================================================================

    @Test
    @DisplayName("installOn(Node): fires when node is already in scene")
    void installOnNode_alreadyInScene() {
        AtomicInteger c = new AtomicInteger();
        ShortcutManager.installOn(root, action("A", c::incrementAndGet));

        fire(scene, KeyCode.A);
        assertEquals(1, c.get());
    }

    @Test
    @DisplayName("installOn(Node): does not fire when node has no scene")
    void installOnNode_noScene() {
        AtomicInteger c = new AtomicInteger();
        ShortcutManager.installOn(new StackPane(), action("A", c::incrementAndGet));

        fire(scene, KeyCode.A);
        assertEquals(0, c.get());
    }

    @Test
    @DisplayName("installOn(Node): registers when node is added to scene after installation")
    void installOnNode_registersOnAdd(FxRobot robot) {
        AtomicInteger c = new AtomicInteger();
        StackPane child = new StackPane();
        ShortcutManager.installOn(child, action("A", c::incrementAndGet));

        robot.interact(() -> root.getChildren().add(child));
        fire(scene, KeyCode.A);
        assertEquals(1, c.get());
    }

    @Test
    @DisplayName("installOn(Node): unregisters when node is removed from scene")
    void installOnNode_unregistersOnRemoval(FxRobot robot) {
        AtomicInteger c = new AtomicInteger();
        StackPane child = new StackPane();
        robot.interact(() -> root.getChildren().add(child));
        ShortcutManager.installOn(child, action("A", c::incrementAndGet));

        robot.interact(() -> root.getChildren().remove(child));
        fire(scene, KeyCode.A);
        assertEquals(0, c.get());
    }

    @Test
    @DisplayName("installOn(Node): re-registers when node is re-added to scene")
    void installOnNode_reRegistersOnReattach(FxRobot robot) {
        AtomicInteger c = new AtomicInteger();
        StackPane child = new StackPane();
        robot.interact(() -> root.getChildren().add(child));
        ShortcutManager.installOn(child, action("A", c::incrementAndGet));

        robot.interact(() -> root.getChildren().remove(child));
        robot.interact(() -> root.getChildren().add(child));
        fire(scene, KeyCode.A);
        assertEquals(1, c.get());
    }

    @Test
    @DisplayName("installOn(Node): does not fire while node is between scenes")
    void installOnNode_noFireWhileDetached(FxRobot robot) {
        AtomicInteger c = new AtomicInteger();
        StackPane child = new StackPane();
        robot.interact(() -> root.getChildren().add(child));
        ShortcutManager.installOn(child, action("A", c::incrementAndGet));

        robot.interact(() -> root.getChildren().remove(child));
        fire(scene, KeyCode.A);
        assertEquals(0, c.get());
    }

    @Test
    @DisplayName("installOn(Node): removal only affects node's own actions, not other scene-level shortcuts")
    void installOnNode_onlyOwnActionsRemoved(FxRobot robot) {
        AtomicInteger cA = new AtomicInteger(), cB = new AtomicInteger();
        StackPane child = new StackPane();
        robot.interact(() -> root.getChildren().add(child));
        ShortcutManager.installOn(child, action("A", cA::incrementAndGet));
        ShortcutManager.installOn(scene, action("B", cB::incrementAndGet));

        robot.interact(() -> root.getChildren().remove(child));
        fire(scene, KeyCode.A);
        fire(scene, KeyCode.B);
        assertEquals(0, cA.get(), "Node's shortcut should be gone");
        assertEquals(1, cB.get(), "Scene-level shortcut should still fire");
    }

    @Test
    @DisplayName("installOn(Node): action migrates to new scene when node is moved")
    void installOnNode_migratesOnSceneChange(FxRobot robot) {
        AtomicInteger c = new AtomicInteger();
        StackPane child = new StackPane();
        robot.interact(() -> root.getChildren().add(child));
        Scene scene2 = new Scene(new StackPane(), 400, 300);
        ShortcutManager.installOn(child, action("A", c::incrementAndGet));

        robot.interact(() -> {
            root.getChildren().remove(child);
            ((StackPane) scene2.getRoot()).getChildren().add(child);
        });

        fire(scene, KeyCode.A);
        assertEquals(0, c.get(), "Should not fire on old scene");

        fire(scene2, KeyCode.A);
        assertEquals(1, c.get(), "Should fire on new scene");
    }

    @Test
    @DisplayName("installOn(Node): multiple nodes can own distinct shortcuts on the same scene")
    void installOnNode_multipleNodesDistinctShortcuts(FxRobot robot) {
        AtomicInteger cA = new AtomicInteger(), cB = new AtomicInteger();
        StackPane c1 = new StackPane(), c2 = new StackPane();
        robot.interact(() -> root.getChildren().addAll(c1, c2));
        ShortcutManager.installOn(c1, action("A", cA::incrementAndGet));
        ShortcutManager.installOn(c2, action("B", cB::incrementAndGet));

        fire(scene, KeyCode.A);
        fire(scene, KeyCode.B);
        assertEquals(1, cA.get());
        assertEquals(1, cB.get());
    }

    //================================================================================
    // Install(MFXMenu)
    //================================================================================

    @Test
    @DisplayName("installFor: throws when root menu is not installed")
    void installFor_notInstalled() {

    }

    @Test
    @DisplayName("installFor: items without shortcut are silently skipped")
    void installFor_skipsNullShortcutItems() {

    }

    @Test
    @DisplayName("installFor: shortcut items fire actions on the owner's scene")
    void installFor_firesShortcutItems() {

    }

    @Test
    @DisplayName("installFor: only items with shortcuts are registered")
    void installFor_mixedItems() {

    }


    //================================================================================
    // Uninstall
    //================================================================================

    @Test
    @DisplayName("uninstall: all actions stop firing")
    void uninstall_stopsActions() {
        AtomicInteger c = new AtomicInteger();
        ShortcutManager.installOn(scene, action("A", c::incrementAndGet));

        ShortcutManager.uninstallFrom(scene);
        fire(scene, KeyCode.A);
        assertEquals(0, c.get());
    }

    @Test
    @DisplayName("uninstall: null scene does not throw")
    void uninstall_null() {
        assertDoesNotThrow(() -> ShortcutManager.uninstallFrom(null));
    }

    @Test
    @DisplayName("uninstall: unknown scene does not throw")
    void uninstall_unknownScene() {
        assertDoesNotThrow(() -> ShortcutManager.uninstallFrom(new Scene(new StackPane())));
    }

    @Test
    @DisplayName("uninstall: can reinstall same shortcut afterwards")
    void uninstall_thenReinstall() {
        AtomicInteger c = new AtomicInteger();
        ShortcutManager.installOn(scene, action("A", c::incrementAndGet));
        ShortcutManager.uninstallFrom(scene);

        ShortcutManager.installOn(scene, action("A", c::incrementAndGet));
        fire(scene, KeyCode.A);
        assertEquals(1, c.get());
    }

    //================================================================================
    // Memory Management
    //================================================================================

    @Test
    @DisplayName("Memory: scene entry is removed from WeakHashMap after GC")
    void memory_sceneGCed() throws Exception {
        Map<?, ?> map = sceneManagers();

        WeakReference<Scene> ref;
        Scene local = new Scene(new StackPane(), 100, 100);
        ShortcutManager.installOn(local, action("A", () -> {}));
        ref = new WeakReference<>(local);
        assertEquals(1, map.size());

        local = null;
        awaitGC(ref);
        assumeTrue(ref.get() == null, "GC did not collect the scene - skipping");

        //noinspection ResultOfMethodCallIgnored
        map.size(); // triggers WeakHashMap internal stale-entry expunge
        assertEquals(0, map.size());
    }

    @Test
    @DisplayName("Memory: node-installed shortcut does not prevent scene from being GC'd")
    void memory_nodeInstallDoesNotRetainScene() throws Exception {
        Map<?, ?> map = sceneManagers();
        StackPane localRoot = new StackPane();

        WeakReference<Scene> ref;
        Scene local = new Scene(localRoot, 100, 100);
        ShortcutManager.installOn(localRoot, action("A", () -> {}));
        ref = new WeakReference<>(local);

        local.setRoot(new Region());
        local = null;
        localRoot = null;
        awaitGC(ref);
        assumeTrue(ref.get() == null, "GC did not collect the scene - skipping");

        //noinspection ResultOfMethodCallIgnored
        map.size();
        assertEquals(0, map.size());
    }

    //================================================================================
    // Helpers
    //================================================================================

    private static void fire(EventTarget target, KeyCode code, KeyCode... modifiers) {
        Set<KeyCode> mods = Set.of(modifiers);
        KeyEvent e = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            code.getName(), code.getName(),
            code,
            mods.contains(KeyCode.SHIFT),
            mods.contains(KeyCode.CONTROL),
            mods.contains(KeyCode.ALT),
            mods.contains(KeyCode.META)
        );
        Event.fireEvent(target, e);
    }

    /// Returns the private static sceneShortcutManagers map via reflection.
    private static Map<?, ?> sceneManagers() throws Exception {
        Field f = ShortcutManager.class.getDeclaredField("sceneShortcutManagers");
        f.setAccessible(true);
        return (Map<?, ?>) f.get(null);
    }

    /// Hints GC in a short loop until the referent is collected or the attempt limit is reached.
    /// GC is non-deterministic; callers must guard with assumeTrue(ref.get() == null).
    private static void awaitGC(WeakReference<?> ref) throws InterruptedException {
        for (int i = 0; i < 50 && ref.get() != null; i++) {
            System.gc();
            Thread.sleep(100);
        }
    }
}
