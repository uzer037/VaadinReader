package ru.ntlk.corpelib.views.categories;

import ru.ntlk.corpelib.Category;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;

public class CategoriesView extends VerticalLayout {
    TreeGrid<Category> treeGrid;
    TreeDataProvider<Category> categories;
    public CategoriesView(TreeDataProvider <Category> categories) {
        treeGrid = new TreeGrid<>();
        this.categories = categories;
        treeGrid.setDataProvider(this.categories);
        treeGrid.addHierarchyColumn(Category::getName).setHeader("Категории");
        // TODO: think of a cleaner way to prevent tree from collapsing
        expand();
        treeGrid.addCollapseListener(e->expand());
        add(treeGrid);
    }

    private void expand() {
        /*
         TODO: find a way to find max tree search depth in reasonable time
          (currently depth is set to big most-likely-unachievable number)
         */
        treeGrid.expandRecursively(categories.getTreeData().getRootItems(),10);
    }
}
