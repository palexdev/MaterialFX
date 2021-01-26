package treeview;

import io.github.palexdev.materialfx.controls.TreeItem;
import io.github.palexdev.materialfx.controls.TreeView;
import io.github.palexdev.materialfx.controls.base.AbstractTreeItem;
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

import static org.junit.Assert.*;

public class TreeViewTests extends ApplicationTest {
    private final String desktopPath = System.getProperty("user.home") + "/Desktop";
    private TreeView<String> treeView;
    private TreeView<String> expandedTreeView;
    private TreeView<String> complexTreeView;

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
        AbstractTreeItem<String> root = treeView.getRoot();

        long count = root.getItemsCount();
        assertEquals(12, count);

        AbstractTreeItem<String> i1 = TreeItemStream.stream(root)
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
        AbstractTreeItem<String> root = treeView.getRoot();
        AbstractTreeItem<String> i3 = TreeItemStream.stream(root)
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
        AbstractTreeItem<String> root = complexTreeView.getRoot();
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
        AbstractTreeItem<String> root = treeView.getRoot();
        AbstractTreeItem<String> i1 = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1"))
                .findFirst().orElse(null);
        AbstractTreeItem<String> i1b = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1B"))
                .findFirst().orElse(null);
        AbstractTreeItem<String> i2a = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I2A"))
                .findFirst().orElse(null);
        AbstractTreeItem<String> i4a = TreeItemStream.stream(root)
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
        AbstractTreeItem<String> root = treeView.getRoot();
        AbstractTreeItem<String> i1 = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1"))
                .findFirst().orElse(null);
        AbstractTreeItem<String> i1b = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1B"))
                .findFirst().orElse(null);
        AbstractTreeItem<String> i2a = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I2A"))
                .findFirst().orElse(null);
        AbstractTreeItem<String> i11a = TreeItemStream.stream(root)
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
        AbstractTreeItem<String> root = treeView.getRoot();
        AbstractTreeItem<String> complexRoot = complexTreeView.getRoot();

        TreeItemStream.stream(root).forEach(item -> assertEquals(treeView, item.getTreeView()));
        TreeItemStream.stream(complexRoot).forEach(item -> assertEquals(complexTreeView, item.getTreeView()));
        long end = System.nanoTime();
        System.out.println("TimeTreeViewGet:" + ((double) (end - start) / 1000000) + "ms");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testNextSiblings() {
        long start = System.nanoTime();
        AbstractTreeItem<String> root = treeView.getRoot();
        AbstractTreeItem<String> i1 = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1"))
                .findFirst().orElse(null);
        AbstractTreeItem<String> i1b = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1B"))
                .findFirst().orElse(null);
        AbstractTreeItem<String> i2a = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I2A"))
                .findFirst().orElse(null);
        AbstractTreeItem<String> i3a = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I3A"))
                .findFirst().orElse(null);
        AbstractTreeItem<String> i4 = TreeItemStream.stream(root)
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
        AbstractTreeItem<String> root = treeView.getRoot();
        AbstractTreeItem<String> i1 = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1"))
                .findFirst().orElse(null);
        AbstractTreeItem<String> i1b = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1B"))
                .findFirst().orElse(null);
        AbstractTreeItem<String> i2a = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I2A"))
                .findFirst().orElse(null);
        AbstractTreeItem<String> i3a = TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I3A"))
                .findFirst().orElse(null);
        AbstractTreeItem<String> i4 = TreeItemStream.stream(root)
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
    public void testStartExpanded() {
        long start = System.nanoTime();
        TreeItem<String> root = (TreeItem<String>) expandedTreeView.getRoot();
        TreeItem<String> i1 = (TreeItem<String>) TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1"))
                .findFirst().orElse(null);
        TreeItem<String> i1b = (TreeItem<String>) TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I1B"))
                .findFirst().orElse(null);
        TreeItem<String> i2a = (TreeItem<String>) TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I2A"))
                .findFirst().orElse(null);
        TreeItem<String> i3 = (TreeItem<String>) TreeItemStream.stream(root)
                .filter(i -> i.getData().equals("I3"))
                .findFirst().orElse(null);

        assertTrue(i1.isExpanded());
        assertFalse(i1b.isExpanded());
        assertFalse(i2a.isExpanded());
        assertTrue(i3.isExpanded());
        long end = System.nanoTime();
        System.out.println("TimeStartExpanded:" + ((double) (end - start) / 1000000) + "ms");
    }

    //================================================================================
    // OTHER METHODS
    //================================================================================
    private void createTree(File file, TreeItem<String> parent) {
        if (file.isDirectory()) {
            TreeItem<String> treeItem = new TreeItem<>(file.getName());
            parent.getItems().add(treeItem);
            File[] fileList = file.listFiles();
            if (fileList != null) {
                for (File f : fileList) {
                    createTree(f, treeItem);
                }
            }
        } else {
            parent.getItems().add(new TreeItem<>(file.getName()));
        }
    }

    private long fileCount() throws IOException {
        return Files.walk(Paths.get(desktopPath).toAbsolutePath())
                .parallel()
                .count();
    }

    private void buildTreeViews() {
        TreeItem<String> root = new TreeItem<>("ROOT");
        TreeItem<String> i1 = new TreeItem<>("I1");
        TreeItem<String> i1a = new TreeItem<>("I1A");
        i1a.getItems().add(new TreeItem<>("I11A"));

        TreeItem<String> i1b = new TreeItem<>("I1B");
        i1.getItems().addAll(List.of(i1a, i1b));

        TreeItem<String> i2 = new TreeItem<>("I2");
        TreeItem<String> i2a = new TreeItem<>("I2A");
        i2.getItems().add(i2a);

        TreeItem<String> i3 = new TreeItem<>("I3");
        TreeItem<String> i3a = new TreeItem<>("I3A");
        TreeItem<String> i3b = new TreeItem<>("I3B");
        i3.getItems().addAll(List.of(i3a, i3b));

        TreeItem<String> i4 = new TreeItem<>("I4");
        TreeItem<String> i4a = new TreeItem<>("I4A");
        i4.getItems().add(i4a);

        root.getItems().addAll(List.of(i1, i2, i3, i4));
        treeView = new TreeView<>(root);

        buildExpandedTree();

        Path dir = Paths.get(desktopPath).toAbsolutePath();
        TreeItem<String> complexRoot = new TreeItem<>(desktopPath);
        File[] fileList = dir.toFile().listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                createTree(file, complexRoot);
            }
        }
        complexTreeView = new TreeView<>(complexRoot);
    }

    private void buildExpandedTree() {
        TreeItem<String> root = new TreeItem<>("ROOT");
        TreeItem<String> i1 = new TreeItem<>("I1");
        TreeItem<String> i1a = new TreeItem<>("I1A");
        i1a.getItems().add(new TreeItem<>("I11A"));

        TreeItem<String> i1b = new TreeItem<>("I1B");
        i1.getItems().addAll(List.of(i1a, i1b));

        TreeItem<String> i2 = new TreeItem<>("I2");
        TreeItem<String> i2a = new TreeItem<>("I2A");
        i2.getItems().add(i2a);

        TreeItem<String> i3 = new TreeItem<>("I3");
        TreeItem<String> i3a = new TreeItem<>("I3A");
        TreeItem<String> i3b = new TreeItem<>("I3B");
        i3.getItems().addAll(List.of(i3a, i3b));

        TreeItem<String> i4 = new TreeItem<>("I4");
        TreeItem<String> i4a = new TreeItem<>("I4A");
        i4.getItems().add(i4a);

        root.getItems().addAll(List.of(i1, i2, i3, i4));
        expandedTreeView = new TreeView<>(root);

        root.setStartExpanded(true);
        i1.setStartExpanded(true);
        i1b.setStartExpanded(true);
        i2a.setStartExpanded(true);
        i3.setStartExpanded(true);
    }
}
