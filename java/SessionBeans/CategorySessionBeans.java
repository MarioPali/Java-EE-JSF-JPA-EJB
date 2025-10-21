package SessionBeans;


import javax.ejb.Stateless;
import java.util.List;
import entities.Category;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
/**
 *@author mario
 */

@Stateless
public class CategorySessionBeans implements CategorySessionBeansLocal {

    @PersistenceContext(unitName = "ols1_2169PU")
    private EntityManager em;

//    public void persist(Object object) {
//        em.persist(object);
//    }

    public List<Category> findCategories(){
        return em.createNamedQuery("Category.findAll").getResultList();
    }



}