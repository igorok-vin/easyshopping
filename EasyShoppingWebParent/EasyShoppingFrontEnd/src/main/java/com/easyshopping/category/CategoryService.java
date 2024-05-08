package com.easyshopping.category;

import com.easyshopping.common.entity.Category;
import com.easyshopping.common.exception.CategoryNotFoundException;
import com.easyshopping.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> listNoChildrenCategories() {
        List<Category> listEnabledWithNoChildrenCategories = new ArrayList<>();
        List<Category> listEnabledCategories = categoryRepository.findAllByEnabledEqualsTrue();
        listEnabledCategories.forEach(category -> {
            Set<Category> children = category.getChildren();
            if(children == null || children.size()==0){
                listEnabledWithNoChildrenCategories.add(category);
            }
        });
        return listEnabledWithNoChildrenCategories;
    }



    public Category getCategoryByAlias(String alias) throws CategoryNotFoundException {
       Category category = categoryRepository.finByAliasEnabled(alias);
       if (category == null) {
           throw new CategoryNotFoundException("Could not find any categories with alias " + alias);
       }
        return category;
    }

    /*на основі метода створюєм навігаційне меню по продуктам які відносяться до певних категорій які і будуть відображатись як breadcrumbs - навігація від коріної категорій без дочірніх категорій і на верх по ієрархії до самої батьківської*/
    public List<Category>  getCategoryParents (Category child) {
        List<Category> listParents = new ArrayList<>();
        /*firstly, we need to get the parent from the given category object here.*/
        Category parent = child.getParent();
        /*then we use the while loop to and we use a while loop to look up the parent of parent and parent of parent.*/
        while(parent !=null){
            listParents.add(0, parent);
            parent = parent.getParent();
        }
        /*And finally, we add the child category here to the list at the end of the list. List parents add child here.*/
        listParents.add(child);
        return listParents;
    }
}
