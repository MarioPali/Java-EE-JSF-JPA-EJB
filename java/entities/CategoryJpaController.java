/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import entities.exceptions.NonexistentEntityException;
import entities.exceptions.PreexistingEntityException;
import entities.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author mario
 */
public class CategoryJpaController implements Serializable {

    public CategoryJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Category category) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (category.getProductList() == null) {
            category.setProductList(new ArrayList<Product>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Product> attachedProductList = new ArrayList<Product>();
            for (Product productListProductToAttach : category.getProductList()) {
                productListProductToAttach = em.getReference(productListProductToAttach.getClass(), productListProductToAttach.getId());
                attachedProductList.add(productListProductToAttach);
            }
            category.setProductList(attachedProductList);
            em.persist(category);
            for (Product productListProduct : category.getProductList()) {
                Category oldCategoryOfProductListProduct = productListProduct.getCategory();
                productListProduct.setCategory(category);
                productListProduct = em.merge(productListProduct);
                if (oldCategoryOfProductListProduct != null) {
                    oldCategoryOfProductListProduct.getProductList().remove(productListProduct);
                    oldCategoryOfProductListProduct = em.merge(oldCategoryOfProductListProduct);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findCategory(category.getId()) != null) {
                throw new PreexistingEntityException("Category " + category + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Category category) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Category persistentCategory = em.find(Category.class, category.getId());
            List<Product> productListOld = persistentCategory.getProductList();
            List<Product> productListNew = category.getProductList();
            List<Product> attachedProductListNew = new ArrayList<Product>();
            for (Product productListNewProductToAttach : productListNew) {
                productListNewProductToAttach = em.getReference(productListNewProductToAttach.getClass(), productListNewProductToAttach.getId());
                attachedProductListNew.add(productListNewProductToAttach);
            }
            productListNew = attachedProductListNew;
            category.setProductList(productListNew);
            category = em.merge(category);
            for (Product productListOldProduct : productListOld) {
                if (!productListNew.contains(productListOldProduct)) {
                    productListOldProduct.setCategory(null);
                    productListOldProduct = em.merge(productListOldProduct);
                }
            }
            for (Product productListNewProduct : productListNew) {
                if (!productListOld.contains(productListNewProduct)) {
                    Category oldCategoryOfProductListNewProduct = productListNewProduct.getCategory();
                    productListNewProduct.setCategory(category);
                    productListNewProduct = em.merge(productListNewProduct);
                    if (oldCategoryOfProductListNewProduct != null && !oldCategoryOfProductListNewProduct.equals(category)) {
                        oldCategoryOfProductListNewProduct.getProductList().remove(productListNewProduct);
                        oldCategoryOfProductListNewProduct = em.merge(oldCategoryOfProductListNewProduct);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Short id = category.getId();
                if (findCategory(id) == null) {
                    throw new NonexistentEntityException("The category with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Short id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Category category;
            try {
                category = em.getReference(Category.class, id);
                category.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The category with id " + id + " no longer exists.", enfe);
            }
            List<Product> productList = category.getProductList();
            for (Product productListProduct : productList) {
                productListProduct.setCategory(null);
                productListProduct = em.merge(productListProduct);
            }
            em.remove(category);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Category> findCategoryEntities() {
        return findCategoryEntities(true, -1, -1);
    }

    public List<Category> findCategoryEntities(int maxResults, int firstResult) {
        return findCategoryEntities(false, maxResults, firstResult);
    }

    private List<Category> findCategoryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Category as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Category findCategory(Short id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Category.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategoryCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Category as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
