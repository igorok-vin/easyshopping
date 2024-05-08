package com.easyshopping.common.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "brands")
public class Brand extends MainEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45, unique = true)
    private String name;

    @Column(length = 128)
    private String logo;

    /*many to many relationship between category and brand.
    * the brand entity class has a preference to category via a set collection of category here, but in the category class, it doesn't have any information about brand. So this relationship is called unidirectional many to many.*/
    @ManyToMany
    @JoinTable(name = "brands_categories",
            joinColumns = @JoinColumn(name = "brand_id"),
    inverseJoinColumns = @JoinColumn(name = "catergory_id"))
    private Set<Category> categories = new HashSet<>();

    @Transient
    public String getLogoPath() {
        /*код для відображення діфолтної картінки в формі створення категорії коли категорії ще самої не існує і вона в процесі створення, або в режимі редагування і картинки немає. Або вертає місце де лежить картінка якщо вже категорія існує*/
        if (id == null || logo == null) return "/icon/entity-default.png";
        return "/brand-logo/" + this.id + "/" + this.logo;
    }

    public Brand() {
    }

    public Brand(String name, Set<Category> categories) {
        this.name = name;
        this.categories = categories;
    }

    public Brand(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Brand(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "Brand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categories=" + categories +
                '}';
    }
}
