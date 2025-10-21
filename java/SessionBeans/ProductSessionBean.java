package SessionBeans;

import java.util.List;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import entities.Product;

/**
 *@author mario
 */
@Stateless
public class ProductSessionBean  {

    @PersistenceContext(unitName = "ols1_2169PU")
    private EntityManager em;
    public  List<Product> findProducts(){
        return em.createNamedQuery("Product.findAll").getResultList();
    }
    public void persistProduct(Product product) {
        em.persist(product);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

}
    

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

