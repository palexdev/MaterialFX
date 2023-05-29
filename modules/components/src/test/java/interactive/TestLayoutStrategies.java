/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.controls.fab.MFXFab;
import io.github.palexdev.mfxcomponents.layout.LayoutStrategy;
import io.github.palexdev.mfxcomponents.layout.LayoutStrategy.Defaults;
import io.github.palexdev.mfxcomponents.layout.MFXResizable;
import io.github.palexdev.mfxcomponents.theming.MaterialThemes;
import io.github.palexdev.mfxcore.base.properties.NodeProperty;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static io.github.palexdev.mfxcomponents.theming.enums.FABVariants.*;
import static io.github.palexdev.mfxresources.fonts.IconsProviders.FONTAWESOME_SOLID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(ApplicationExtension.class)
public class TestLayoutStrategies {
    private static Stage stage;

    @Start
    void start(Stage stage) {
        TestLayoutStrategies.stage = stage;
        stage.show();
    }

    @Test
    void testDefaultStrategy(FxRobot robot) {
        StackPane root = setupStage();
        ResControl rc = new ResControl();
        robot.interact(() -> root.getChildren().setAll(rc));

        assertEquals(root.getWidth(), rc.getWidth());
        assertEquals(root.getHeight(), rc.getHeight());
    }

    @Test
    void testStrategyMin(FxRobot robot) {
        StackPane root = setupStage();
        ResControl rc = new ResControl();
        robot.interact(() -> root.getChildren().setAll(rc));

        // Exactly as above because in a StackPane...
        // Let's set the max to use the pref...
        assertEquals(root.getWidth(), rc.getWidth());
        assertEquals(root.getHeight(), rc.getHeight());

        robot.interact(() -> rc.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE));
        assertEquals(0, Math.abs(rc.getWidth()));
        assertEquals(0, Math.abs(rc.getWidth()));
        // 0 because there's no content

        robot.interact(() -> rc.setContent(new MFXFontIcon("fas-circle", 24)));
        assertEquals(24, rc.getWidth());
        assertEquals(24, rc.getHeight());

        // Finally, let's test the strategy
        LayoutStrategy strategy = LayoutStrategy.defaultStrategy()
            .setMinWidthFunction(Defaults.DEF_MIN_WIDTH_FUNCTION.andThen(r -> Math.max(r, 48)))
            .setMinHeightFunction(Defaults.DEF_MIN_HEIGHT_FUNCTION.andThen(r -> Math.max(r, 48)));
        robot.interact(() -> {
            rc.setLayoutStrategy(strategy);
            rc.requestLayout();
        });
        assertEquals(48, rc.getWidth());
        assertEquals(48, rc.getHeight());
    }

    @Test
    void testStrategyPref(FxRobot robot) {
        StackPane root = setupStage();
        ResControl rc = new ResControl();
        rc.setContent(new MFXFontIcon("fas-circle", 24));
        rc.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        robot.interact(() -> root.getChildren().setAll(rc));

        assertEquals(24.0, rc.getWidth());
        assertEquals(24.0, rc.getHeight());

        LayoutStrategy strategy = LayoutStrategy.defaultStrategy()
            .setPrefWidthFunction(Defaults.DEF_PREF_WIDTH_FUNCTION.andThen(r -> Math.max(r, 64.0)))
            .setPrefHeightFunction(Defaults.DEF_PREF_HEIGHT_FUNCTION.andThen(r -> Math.max(r, 64.0)));
        robot.interact(() -> {
            rc.setLayoutStrategy(strategy);
            rc.requestLayout();
        });

        assertEquals(64.0, rc.getWidth());
        assertEquals(64.0, rc.getHeight());

        robot.interact(() -> rc.setMaxSize(30, 30));
        assertEquals(30.0, rc.getWidth());
        assertEquals(30.0, rc.getHeight());

        robot.interact(() -> rc.setMinSize(45, 45));
        assertEquals(45.0, rc.getWidth());
        assertEquals(45.0, rc.getHeight());
    }

    @Test
    void testStrategyMax(FxRobot robot) {
        StackPane root = setupStage();
        ResControl rc = new ResControl();
        rc.setContent(new MFXFontIcon("fas-circle", 40.0));
        robot.interact(() -> root.getChildren().setAll(rc));

        LayoutStrategy strategy = LayoutStrategy.defaultStrategy()
            .setMaxWidthFunction(Defaults.DEF_MAX_WIDTH_FUNCTION.andThen(r -> Math.min(r, 50.0)))
            .setMaxHeightFunction(Defaults.DEF_MAX_HEIGHT_FUNCTION.andThen(r -> Math.min(r, 50.0)));
        robot.interact(() -> {
            rc.setLayoutStrategy(strategy);
            rc.requestLayout();
        });

        // Notice the difference between the other tests, the strategy is using Math.min
        assertEquals(50.0, rc.getWidth());
        assertEquals(50.0, rc.getHeight());

        robot.interact(() -> rc.setPrefSize(100, 100));
        assertEquals(50.0, rc.getWidth());
        assertEquals(50.0, rc.getHeight());

        // Remember, min sizes always prevail on max
        robot.interact(() -> rc.setMinSize(70, 70));
        assertEquals(70.0, rc.getWidth());
        assertEquals(70.0, rc.getHeight());
    }

    @Test
    void testFABDefaultStrategy(FxRobot robot) {
        StackPane root = setupStage();
        MFXFab fab = MFXFab.extended();
        fab.setIcon(FONTAWESOME_SOLID.randomIcon());
        robot.interact(() -> {
            fab.setJavaFXLayoutStrategy();
            root.getChildren().setAll(fab);
        });

        // Default strategy, same as JavaFX
        // Do not count insets and GTG for the sanity of the below 'comparison'
        assertEquals(fab.getIcon().getLayoutBounds().getWidth(), fab.getWidth() - getLRInsets(fab) - fab.getGraphicTextGap());
        assertEquals(fab.getIcon().getLayoutBounds().getHeight(), fab.getHeight() - getTBInsets(fab));

        // Double check with a similar container setup...
        StackPane sp = new StackPane(new MFXFontIcon(fab.getIcon().getDescription(), fab.getIcon().getSize()));
        sp.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        robot.interact(() -> root.getChildren().add(sp));
        assertEquals(fab.getIcon().getLayoutBounds().getWidth(), sp.getWidth());
        assertEquals(fab.getIcon().getLayoutBounds().getHeight(), sp.getHeight());
    }

    @Test
    void testFABStrategyMin(FxRobot robot) {
        StackPane root = setupStage();
        MFXFab fab = MFXFab.extended();
        fab.setIcon(new MFXFontIcon("fas-circle"));
        robot.interact(() -> root.getChildren().setAll(fab));

        // Define a strategy with minimum sizes
        LayoutStrategy strategy = LayoutStrategy.defaultStrategy()
            .setMinWidthFunction(Defaults.DEF_MIN_WIDTH_FUNCTION.andThen(r -> Math.max(r, 72.0)))
            .setMinHeightFunction(Defaults.DEF_MIN_HEIGHT_FUNCTION.andThen(r -> Math.max(r, 72.0)));
        robot.interact(() -> {
            fab.setLayoutStrategy(strategy);
            //fab.requestLayout();
        });

        LayoutUtils.boundWidth(fab);
        assertEquals(72.0, fab.getWidth());
        assertEquals(72.0, fab.getHeight());

        // What happens if I set the pref?
        robot.interact(() -> fab.setPrefSize(100, 100));
        assertEquals(100.0, fab.getWidth());
        assertEquals(100.0, fab.getHeight());

        // What happens if I also set the max?
        robot.interact(() -> fab.setMaxSize(40, 40));
        assertEquals(72.0, fab.getWidth());
        assertEquals(72.0, fab.getHeight());
        // Still 72.0, does this happen with JavaFX nodes too?

        StackPane sp = new StackPane() {
            @Override
            protected double computeMinWidth(double height) {
                return Math.max(super.computeMinWidth(height), 64.0);
            }

            @Override
            protected double computeMinHeight(double width) {
                return Math.max(super.computeMinHeight(width), 64.0);
            }
        };
        sp.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        robot.interact(() -> root.getChildren().setAll(sp));

        assertEquals(64.0, sp.getWidth());
        assertEquals(64.0, sp.getHeight());

        robot.interact(() -> sp.setPrefSize(100, 100));
        assertEquals(100.0, sp.getWidth());
        assertEquals(100.0, sp.getHeight());

        robot.interact(() -> sp.setMaxSize(40, 40));
        assertEquals(64.0, sp.getWidth());
        assertEquals(64.0, sp.getHeight());

        /*
         * Yes, so, let me get this straight. Layout:
         * Min > Pref > Max
         * Which means that even if Max is lesser than Min, the latter will prevail
         * But first the minimum between pref and max is computed
         *
         * The formula seems to be:
         * 1) Maximum between Pref and Min
         * 2) Maximum between Min and Max
         * 3) Minimum between these two results
         */
    }

    @Test
    void testFABStrategyPref(FxRobot robot) {
        StackPane root = setupStage();
        MFXFab fab = MFXFab.extended();
        fab.setIcon(new MFXFontIcon("fas-circle"));
        robot.interact(() -> root.getChildren().setAll(fab));

        /*
         * At this point the layout strategy is still not set and the extend() method
         * in the FAB behavior is called causing the pref width to be overridden through
         * the setPrefWidth method
         */
        assertNotEquals(64.0, fab.getWidth());

        // Define a strategy with pref sizes
        LayoutStrategy strategy = LayoutStrategy.defaultStrategy()
            .setPrefWidthFunction(Defaults.DEF_PREF_WIDTH_FUNCTION.andThen(r -> Math.max(r, 72.0)))
            .setPrefHeightFunction(Defaults.DEF_PREF_HEIGHT_FUNCTION.andThen(r -> Math.max(r, 72.0)));
        robot.interact(() -> fab.setLayoutStrategy(strategy));

        /*
         * This is different from using setPrefSize(...)!
         * And different from setting a minimum size strategy, so...
         */
        assertEquals(72.0, fab.getWidth());
        assertEquals(72.0, fab.getHeight());

        // What happens if I set a max?
        robot.interact(() -> fab.setMaxSize(30, 30));
        assertEquals(30.0, fab.getWidth());
        assertEquals(56.0, fab.getHeight()); // Icon is 24, total padding is 32, sum is 56, for the aforementioned formula 56 wins

        // What happens if I set a min?
        robot.interact(() -> fab.setMinSize(45, 45));
        assertEquals(45.0, fab.getWidth());
        assertEquals(45.0, fab.getHeight());
    }

    @Test
    void testFABStrategyMax(FxRobot robot) {
        StackPane root = setupStage();
        MFXFab fab = MFXFab.extended();
        fab.setIcon(new MFXFontIcon("fas-circle"));
        robot.interact(() -> root.getChildren().setAll(fab));

        // Define a strategy with max sizes
        LayoutStrategy strategy = LayoutStrategy.defaultStrategy()
            .setMaxWidthFunction(Defaults.DEF_MAX_WIDTH_FUNCTION.andThen(r -> Math.max(r, 72.0)))
            .setMaxHeightFunction(Defaults.DEF_MAX_HEIGHT_FUNCTION.andThen(r -> Math.max(r, 72.0)));
        robot.interact(() -> fab.setLayoutStrategy(strategy));

        /*
         * Here we have a special case. We set a strategy that constraints the node to be at max 72px
         * But remember that the node is inside a StackPane, so it will occupy all the space possible within its constraints
         * So we have to check: 1) the pref size 2) the actual size
         *
         * We don't check for the prefHeight because only the pref width is overridden by the
         * extend() method in the FAB behavior
         */
        assertEquals(64.0, fab.getPrefWidth());
        assertEquals(72.0, fab.getWidth());
        assertEquals(72.0, fab.getHeight());

        // What happens if I set pref?
        robot.interact(() -> fab.setPrefSize(100, 100));
        assertEquals(100.0, fab.getWidth());
        assertEquals(100.0, fab.getHeight());
        /*
         * Why 100?
         * Buttons by default use the prefSize for the computation of the maxSize methods
         * This means that the above layout strategy is ignored because at the end of the
         * algorithm the Math.min operation is evaluated between the same value (comes from pref size)
         * Let's see if the strategy changes what happens...
         */

        LayoutStrategy newStrategy = LayoutStrategy.defaultStrategy()
            .setMaxWidthFunction(r -> 64.0)
            .setMaxHeightFunction(r -> 64.0);
        robot.interact(() -> fab.setLayoutStrategy(newStrategy));
        assertEquals(64.0, fab.getWidth());
        assertEquals(64.0, fab.getHeight());
        // Exactly as expected...
    }

    @Test
    void testSmallFabs(FxRobot robot) {
        StackPane root = setupStage();
        MFXFab fab = new MFXFab().surface().addVariants(LOWERED, SMALL);
        fab.setIcon(new MFXFontIcon("fas-circle"));
        robot.interact(() -> root.getChildren().add(fab));

        assertEquals(40, fab.getWidth());
        assertEquals(40, fab.getHeight());
    }

    @Test
    void testLargeFabs(FxRobot robot) {
        StackPane root = setupStage();
        MFXFab fab = new MFXFab().tertiary().addVariants(LOWERED, LARGE);
        fab.setIcon(new MFXFontIcon("fas-circle"));
        robot.interact(() -> root.getChildren().add(fab));

        assertEquals(96, fab.getWidth());
        assertEquals(96, fab.getHeight());
    }

    private double getLRInsets(Region region) {
        return region.snappedLeftInset() + region.snappedRightInset();
    }

    private double getTBInsets(Region region) {
        return region.snappedTopInset() + region.snappedBottomInset();
    }

    private StackPane setupStage() {
        return setupStage(StackPane::new);
    }

    @SuppressWarnings("unchecked")
    private <T extends Pane> T setupStage(Supplier<T> paneFactory) {
        try {
            Scene scene = new Scene(paneFactory.get(), 200, 200);
            MaterialThemes.PURPLE_LIGHT.applyOn(scene);
            FxToolkit.setupStage(s -> s.setScene(scene));
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        return (T) stage.getScene().getRoot();
    }

    private static class ResControl extends Control implements MFXResizable {
        private final ObjectProperty<LayoutStrategy> layoutStrategy = new SimpleObjectProperty<>(defaultLayoutStrategy());
        private final NodeProperty content = new NodeProperty();

        @Override
        public LayoutStrategy getLayoutStrategy() {
            return layoutStrategy.get();
        }

        @Override
        public ObjectProperty<LayoutStrategy> layoutStrategyProperty() {
            return layoutStrategy;
        }

        @Override
        public void setLayoutStrategy(LayoutStrategy strategy) {
            layoutStrategy.set(strategy);
        }

        @Override
        public double computeMinWidth(double height) {
            return getLayoutStrategy().computeMinWidth(this);
        }

        @Override
        public double computeMinHeight(double width) {
            return getLayoutStrategy().computeMinWidth(this);
        }

        @Override
        public double computePrefWidth(double height) {
            return getLayoutStrategy().computePrefWidth(this);
        }

        @Override
        public double computePrefHeight(double width) {
            return getLayoutStrategy().computePrefHeight(this);
        }

        @Override
        public double computeMaxWidth(double height) {
            return getLayoutStrategy().computeMaxWidth(this);
        }

        @Override
        public double computeMaxHeight(double width) {
            return getLayoutStrategy().computeMaxHeight(this);
        }

        public Node getContent() {
            return content.get();
        }

        public NodeProperty contentProperty() {
            return content;
        }

        public void setContent(Node content) {
            this.content.set(content);
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        protected Skin<?> createDefaultSkin() {
            return new MFXSkinBase(this) {
                @Override
                protected void initBehavior(BehaviorBase behavior) {
                }

                final Pane pane = new Pane();
                final When<Node> cdWhen;

                {
                    cdWhen = When.onChanged(contentProperty())
                        .then((o, n) -> {
                            if (n == null) {
                                pane.getChildren().clear();
                                return;
                            }
                            pane.getChildren().setAll(n);
                        })
                        .executeNow()
                        .listen();
                    getChildren().add(pane);
                }

                @Override
                public void dispose() {
                    cdWhen.dispose();
                    super.dispose();
                }
            };
        }
    }
}
