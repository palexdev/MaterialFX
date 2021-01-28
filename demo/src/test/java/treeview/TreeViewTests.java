package treeview;

import io.github.palexdev.materialfx.controls.MFXTreeItem;
import io.github.palexdev.materialfx.controls.MFXTreeView;
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeItem;
import io.github.palexdev.materialfx.utils.TreeItemStream;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TreeViewTests extends ApplicationTest {
    private final String desktopPath = System.getProperty("user.home") + "/Desktop";
    private MFXTreeView<String> treeView;
    private MFXTreeView<String> expandedTreeView;
    private MFXTreeView<String> complexTreeView;

    @Override
    public void start(Stage stage) {
        buildTreeViews();
        StackPane stackPane = new StackPane(
                treeView, expandedTreeView, complexTreeView
        );
        Scene scene = new Scene(stackPane, 100, 100);
        stage.setScene(scene);
        stage.show();
        stage.toBack();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testItemsCountRoot() {
        long start = System.nanoTime();
        AbstractMFXTreeItem<String> root = treeView.getRoot();

        long count = root.getItemsCount();
        assertEquals(12, count);

        AbstractMFXTreeItem<String> i1 = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1"))
                .findFirst().orElse(null);
        count = i1.getItemsCount();
        assertEquals(4, count);
        long end = System.nanoTime();
        System.out.println("TimeCountRoot:" + ((double) (end - start) / 1000000) + "ms");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testItemsCountItem() {
        long start = System.nanoTime();
        AbstractMFXTreeItem<String> root = treeView.getRoot();
        AbstractMFXTreeItem<String> i3 = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I3"))
                .findFirst().orElse(null);
        long count = i3.getItemsCount();
        assertEquals(3, count);
        long end = System.nanoTime();
        System.out.println("TimeCountItem:" + ((double) (end - start) / 1000000) + "ms");
    }

    @Test
    public void testItemCountComplex() throws IOException {
        long start = System.nanoTime();
        AbstractMFXTreeItem<String> root = complexTreeView.getRoot();
        long expectedCount = fileCount();
        long count = root.getItemsCount();
        assertEquals(expectedCount, count);
        long end = System.nanoTime();
        System.out.println("TimeCountComplex:" + ((double) (end - start) / 1000000) + "ms");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testItemIndex() {
        long start = System.nanoTime();
        AbstractMFXTreeItem<String> root = treeView.getRoot();
        AbstractMFXTreeItem<String> i1 = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1"))
                .findFirst().orElse(null);
        AbstractMFXTreeItem<String> i1b = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1B"))
                .findFirst().orElse(null);
        AbstractMFXTreeItem<String> i2a = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I2A"))
                .findFirst().orElse(null);
        AbstractMFXTreeItem<String> i4a = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I4A"))
                .findFirst().orElse(null);

        assertEquals(0, root.getIndex());
        assertEquals(1, i1.getIndex());
        assertEquals(4, i1b.getIndex());
        assertEquals(6, i2a.getIndex());
        assertEquals(11, i4a.getIndex());
        long end = System.nanoTime();
        System.out.println("TimeIndex:" + ((double) (end - start) / 1000000) + "ms");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testItemLevel() {
        long start = System.nanoTime();
        AbstractMFXTreeItem<String> root = treeView.getRoot();
        AbstractMFXTreeItem<String> i1 = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1"))
                .findFirst().orElse(null);
        AbstractMFXTreeItem<String> i1b = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1B"))
                .findFirst().orElse(null);
        AbstractMFXTreeItem<String> i2a = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I2A"))
                .findFirst().orElse(null);
        AbstractMFXTreeItem<String> i11a = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I11A"))
                .findFirst().orElse(null);

        assertEquals(0, root.getLevel());
        assertEquals(1, i1.getLevel());
        assertEquals(2, i1b.getLevel());
        assertEquals(2, i2a.getLevel());
        assertEquals(3, i11a.getLevel());
        long end = System.nanoTime();
        System.out.println("TimeLevel:" + ((double) (end - start) / 1000000) + "ms");
    }

    @Test
    public void testTreeViewGet() {
        long start = System.nanoTime();
        AbstractMFXTreeItem<String> root = treeView.getRoot();
        AbstractMFXTreeItem<String> complexRoot = complexTreeView.getRoot();

        TreeItemStream.stream(root).forEach(item -> assertEquals(treeView, item.getTreeView()));
        TreeItemStream.stream(complexRoot).forEach(item -> assertEquals(complexTreeView, item.getTreeView()));
        long end = System.nanoTime();
        System.out.println("TimeTreeViewGet:" + ((double) (end - start) / 1000000) + "ms");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testNextSiblings() {
        long start = System.nanoTime();
        AbstractMFXTreeItem<String> root = treeView.getRoot();
        AbstractMFXTreeItem<String> i1 = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1"))
                .findFirst().orElse(null);
        AbstractMFXTreeItem<String> i1b = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1B"))
                .findFirst().orElse(null);
        AbstractMFXTreeItem<String> i2a = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I2A"))
                .findFirst().orElse(null);
        AbstractMFXTreeItem<String> i3a = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I3A"))
                .findFirst().orElse(null);
        AbstractMFXTreeItem<String> i4 = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I4"))
                .findFirst().orElse(null);

        assertNull(root.getNextSibling());
        assertEquals("I2", i1.getNextSibling().getData());
        assertNull(i1b.getNextSibling());
        assertNull(i2a.getNextSibling());
        assertEquals("I3B", i3a.getNextSibling().getData());
        assertNull(i4.getNextSibling());
        long end = System.nanoTime();
        System.out.println("TimeNextSiblings:" + ((double) (end - start) / 1000000) + "ms");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testPreviousSiblings() {
        long start = System.nanoTime();
        AbstractMFXTreeItem<String> root = treeView.getRoot();
        AbstractMFXTreeItem<String> i1 = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1"))
                .findFirst().orElse(null);
        AbstractMFXTreeItem<String> i1b = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1B"))
                .findFirst().orElse(null);
        AbstractMFXTreeItem<String> i2a = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I2A"))
                .findFirst().orElse(null);
        AbstractMFXTreeItem<String> i3a = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I3A"))
                .findFirst().orElse(null);
        AbstractMFXTreeItem<String> i4 = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I4"))
                .findFirst().orElse(null);

        assertNull(root.getPreviousSibling());
        assertNull(i1.getPreviousSibling());
        assertEquals("I1A", i1b.getPreviousSibling().getData());
        assertNull(i2a.getPreviousSibling());
        assertNull(i3a.getPreviousSibling());
        assertEquals("I3", i4.getPreviousSibling().getData());
        long end = System.nanoTime();
        System.out.println("TimePreviousSiblings:" + ((double) (end - start) / 1000000) + "ms");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testSelected() {
        long start = System.nanoTime();
        MFXTreeItem<String> root = (MFXTreeItem<String>) treeView.getRoot();
        treeView.getSelectionModel().setAllowsMultipleSelection(false);
        MFXTreeItem<String> i1 = (MFXTreeItem<String>) TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1"))
                .findFirst().orElse(null);
        MFXTreeItem<String> i1b = (MFXTreeItem<String>) TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1B"))
                .findFirst().orElse(null);
        MFXTreeItem<String> i2a = (MFXTreeItem<String>) TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I2A"))
                .findFirst().orElse(null);
        MFXTreeItem<String> i3 = (MFXTreeItem<String>) TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I3"))
                .findFirst().orElse(null);

        root.setSelected(true);
        i1.setSelected(true);
        i1b.setSelected(true);
        i2a.setSelected(true);
        i3.setSelected(true);

        assertEquals(1, treeView.getSelectionModel().getSelectedItems().size());
        assertEquals(i3, treeView.getSelectionModel().getSelectedItem());
        long end = System.nanoTime();
        System.out.println("TimeSelected:" + ((double) (end - start) / 1000000) + "ms");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testSelectedMultiple() {
        long start = System.nanoTime();
        MFXTreeItem<String> root = (MFXTreeItem<String>) treeView.getRoot();
        MFXTreeItem<String> i1 = (MFXTreeItem<String>) TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1"))
                .findFirst().orElse(null);
        MFXTreeItem<String> i1b = (MFXTreeItem<String>) TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1B"))
                .findFirst().orElse(null);
        MFXTreeItem<String> i2a = (MFXTreeItem<String>) TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I2A"))
                .findFirst().orElse(null);
        MFXTreeItem<String> i3 = (MFXTreeItem<String>) TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I3"))
                .findFirst().orElse(null);

        root.setSelected(true);
        i1.setSelected(true);
        i1b.setSelected(true);
        i2a.setSelected(true);
        i3.setSelected(true);

        assertEquals(5, treeView.getSelectionModel().getSelectedItems().size());
        long end = System.nanoTime();
        System.out.println("TimeSelectedMultiple:" + ((double) (end - start) / 1000000) + "ms");
    }

    //================================================================================
    // OTHER METHODS
    //================================================================================
    private void createTree(File file, MFXTreeItem<String> parent) {
        if (file.isDirectory()) {
            MFXTreeItem<String> treeItem = new MFXTreeItem<>(file.getName());
            parent.getItems().add(treeItem);
            File[] fileList = file.listFiles();
            if (fileList != null) {
                for (File f : fileList) {
                    createTree(f, treeItem);
                }
            }
        } else {
            parent.getItems().add(new MFXTreeItem<>(file.getName()));
        }
    }

    private long fileCount() throws IOException {
        return Files.walk(Paths.get(desktopPath).toAbsolutePath())
                .parallel()
                .count();
    }

    private void buildTreeViews() {
        MFXTreeItem<String> root = new MFXTreeItem<>("ROOT");
        MFXTreeItem<String> i1 = new MFXTreeItem<>("I1");
        MFXTreeItem<String> i1a = new MFXTreeItem<>("I1A");
        i1a.getItems().add(new MFXTreeItem<>("I11A"));

        MFXTreeItem<String> i1b = new MFXTreeItem<>("I1B");
        i1.getItems().addAll(List.of(i1a, i1b));

        MFXTreeItem<String> i2 = new MFXTreeItem<>("I2");
        MFXTreeItem<String> i2a = new MFXTreeItem<>("I2A");
        i2.getItems().add(i2a);

        MFXTreeItem<String> i3 = new MFXTreeItem<>("I3");
        MFXTreeItem<String> i3a = new MFXTreeItem<>("I3A");
        MFXTreeItem<String> i3b = new MFXTreeItem<>("I3B");
        i3.getItems().addAll(List.of(i3a, i3b));

        MFXTreeItem<String> i4 = new MFXTreeItem<>("I4");
        MFXTreeItem<String> i4a = new MFXTreeItem<>("I4A");
        i4.getItems().add(i4a);

        root.getItems().addAll(List.of(i1, i2, i3, i4));
        treeView = new MFXTreeView<>(root);

        buildExpandedTree();

        Path dir = Paths.get(desktopPath).toAbsolutePath();
        MFXTreeItem<String> complexRoot = new MFXTreeItem<>(desktopPath);
        File[] fileList = dir.toFile().listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                createTree(file, complexRoot);
            }
        }
        complexTreeView = new MFXTreeView<>(complexRoot);
    }

    private void buildExpandedTree() {
        MFXTreeItem<String> root = new MFXTreeItem<>("ROOT");
        MFXTreeItem<String> i1 = new MFXTreeItem<>("I1");
        MFXTreeItem<String> i1a = new MFXTreeItem<>("I1A");
        i1a.getItems().add(new MFXTreeItem<>("I11A"));

        MFXTreeItem<String> i1b = new MFXTreeItem<>("I1B");
        i1.getItems().addAll(List.of(i1a, i1b));

        MFXTreeItem<String> i2 = new MFXTreeItem<>("I2");
        MFXTreeItem<String> i2a = new MFXTreeItem<>("I2A");
        i2.getItems().add(i2a);

        MFXTreeItem<String> i3 = new MFXTreeItem<>("I3");
        MFXTreeItem<String> i3a = new MFXTreeItem<>("I3A");
        MFXTreeItem<String> i3b = new MFXTreeItem<>("I3B");
        i3.getItems().addAll(List.of(i3a, i3b));

        MFXTreeItem<String> i4 = new MFXTreeItem<>("I4");
        MFXTreeItem<String> i4a = new MFXTreeItem<>("I4A");
        i4.getItems().add(i4a);

        root.getItems().addAll(List.of(i1, i2, i3, i4));
        expandedTreeView = new MFXTreeView<>(root);

        root.setStartExpanded(true);
        i1.setStartExpanded(true);
        i1b.setStartExpanded(true);
        i2a.setStartExpanded(true);
        i3.setStartExpanded(true);
    }
}
