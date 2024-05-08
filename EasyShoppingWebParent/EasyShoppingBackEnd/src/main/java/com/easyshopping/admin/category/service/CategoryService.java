package com.easyshopping.admin.category.service;

import com.easyshopping.admin.category.domain.CategoryPageInfo;
import com.easyshopping.common.exception.CategoryNotFoundException;
import com.easyshopping.admin.category.repository.CategoryRepository;
import com.easyshopping.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class CategoryService {

    public static final int ROOT_CATEGORIES_PER_PAGE = 1;

    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category get(Integer id) throws CategoryNotFoundException {
        try {
            return categoryRepository.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }
    }

    public Category save(Category category) {
        Category parent = category.getParent();
        if(parent !=null){
            String allParentIds = parent.getAllParentIDs()==null ? "-" : parent.getAllParentIDs();
            allParentIds+= String.valueOf(parent.getId()) + "-";
            category.setAllParentIDs(allParentIds);
        }
        return categoryRepository.save(category);
    }

    public List<Category> listByPage(CategoryPageInfo categoryPageInfo, int pageNum,String sortField, String sortDirection, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDirection.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);
        Page<Category> pageCategories = null;
        if (keyword != null && !keyword.isEmpty()) {
            pageCategories = categoryRepository.search(keyword, pageable);
        } else {
            pageCategories = categoryRepository.findRootCategories(pageable);
        }

        List<Category> rootCategories = pageCategories.getContent();

        categoryPageInfo.setTotalElements(pageCategories.getTotalElements());
        categoryPageInfo.setTotalPages(pageCategories.getTotalPages());

        if (keyword != null && !keyword.isEmpty()) {
            List<Category> searchRedult = pageCategories.getContent();
            for (Category category : searchRedult) {
                category.setHasChildren(category.getChildren().size()>0);
            }
            return searchRedult;
        } else {
            return listHierarchicalCategories(rootCategories, sortDirection);
        }
    }

    /************************************************************/
    private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDirection) {
        List<Category> hierarchicalCategories = new ArrayList<>();
        for (Category rootCategory : rootCategories) {
            /*we need to have a copy category objects in the list.*/
            hierarchicalCategories.add(Category.copyFull(rootCategory));
            /*Set<Category> children = rootCategory.getChildren();*/
            Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDirection);
            for (Category subCategory : children) {
                String name = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory, name));
                listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDirection);
            }
        }
        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel, String sortDirection) {
        Set<Category> children = sortSubCategories(parent.getChildren(), sortDirection);
        int newSubLevel = subLevel + 1;
        for (Category subCategory : children) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();
            hierarchicalCategories.add(Category.copyFull(subCategory, name));
            listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDirection);
        }
    }
    /********************************************************************/

    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        Iterable<Category> categoriesInDB = categoryRepository.findRootCategories(Sort.by("name").ascending());
        for (Category category : categoriesInDB) {
            if (category.getParent() == null) {
                categoriesUsedInForm.add(Category.copyIdAndName(category));
                Set<Category> children = sortSubCategories(category.getChildren());
                for (Category subCategory : children) {
                    String name = "--" + subCategory.getName();
                    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
                    listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
                }
            }
        }
        return categoriesUsedInForm;
    }

    private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> categorySet = sortSubCategories(parent.getChildren());
        for (Category subCategory : categorySet) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();
            categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
            listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
        }
    }

    /****************************************************/

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children, "asc");
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDirection) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category category1, Category category2) {
                if (sortDirection.equals("asc")) {
                    return category1.getName().compareTo(category2.getName());
                } else {
                    return category2.getName().compareTo(category1.getName());
                }
            }
        });
        sortedChildren.addAll(children);
        return sortedChildren;
    }

    public List<Category> listCategoriesForExport() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        Iterable<Category> categoriesInDB = categoryRepository.findRootCategories(Sort.by("name").ascending());
        for (Category category : categoriesInDB) {
            categoriesUsedInForm.add(Category.copyForExport(category));
            Set<Category> children = sortSubCategories(category.getChildren());
            for (Category subCategory : children) {
                String name = "--" + subCategory.getName();
                categoriesUsedInForm.add(Category.copyForExport(Category.copyFullSubCategoryForExport(subCategory.getId(), name, subCategory.getAlias(), subCategory.isEnabled())));
                listSubCategoriesUsedForExport(categoriesUsedInForm, subCategory, 1);
            }
        }
        return categoriesUsedInForm;
    }

    private void listSubCategoriesUsedForExport(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> categorySet = sortSubCategories(parent.getChildren());
        for (Category subCategory : categorySet) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();
            categoriesUsedInForm.add(Category.copyForExport(Category.copyFullSubCategoryForExport(subCategory.getId(), name, subCategory.getAlias(), subCategory.isEnabled())));
            listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
        }
    }

    public void delete(Integer id) throws CategoryNotFoundException {
        Long countById = categoryRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }
        categoryRepository.deleteById(id);
    }

    public String checkUniqueCategoryNameAndAlias(Integer id, String name, String alias) {
        /*if id null meaning that it is creating a new category.*/
        boolean isCreatingNew = (id == null || id == 0);
        Category categoryByName = categoryRepository.findByName(name);
        if (isCreatingNew) {
            if (categoryByName != null) {
                return "DuplicateName";
            } else {
                Category categoryByAlias = categoryRepository.findByAlias(alias);
                if (categoryByAlias != null) {
                    return "DuplicateAlias";
                }
            }
        } else {
            if (categoryByName != null && categoryByName.getId() != id) {
                return "DuplicateName";
            } else {
                Category categoryByAlias = categoryRepository.findByAlias(alias);
                if (categoryByAlias != null && categoryByAlias.getId() != id) {
                    return "DuplicateAlias";
                }
            }
        }
        return "OK";
    }

    public void updateCategoryEnabled(Integer id, boolean enabled) {
        Category category = categoryRepository.findById(id).get();
        categoryRepository.updateEnabled(category.getId(), enabled);
        Set<Category> categoryChildren = category.getChildren();
        categoryChildren.forEach(child -> updateCategoryEnabled(child.getId(), enabled));
        for (Category subCategory : categoryChildren) {
            Set<Category> subCategoryChildren = subCategory.getChildren();
            subCategoryChildren.forEach(child -> updateCategoryEnabled(child.getId(), enabled));
        }
    }
}