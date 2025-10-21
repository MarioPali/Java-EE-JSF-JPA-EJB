package ManagedBeans;

import entities.Category;
import entities.Product;
import java.io.Serializable;
import java.util.List;
import jakarta.ejb.EJB;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import SessionBeans.ProductSessionBean;

/**
 *@author mario
 */

@Named(value = "productManagedBean")
@SessionScoped
public class ProductManagedBean implements Serializable
{
    private Product product;
    private Short categoryId;
    
    @EJB private ProductSessionBean productSessionBean;
    List<Product>products;
    
    public List<Product> getProducts(){
        return products;
    }
    
    public Product getProduct(){
        return product;
    }

    public String prepareCreateProduct(){
        product =new Product();
        categoryId=null;
        return "NewApplication";
    }
 
    public void loadProductsPreRender()
    {
     products=productSessionBean.findProducts();
    }


    public Short getCategoryId(){
        return categoryId;
    }

    public void setCategoryId(Short categoryId){
        this.categoryId =categoryId;
    }


    public String createProduct(){
        Category category =new Category();
        category.setId(categoryId);
        product.setCategory(category);

        productSessionBean.persistProduct(product);
        product=null;
        categoryId=null;
        return "productCatalog";
    }
}
