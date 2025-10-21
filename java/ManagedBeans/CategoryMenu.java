/*
 
Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
*/
package ManagedBeans;

import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import entities.Category;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import SessionBeans.CategorySessionBeans;

/**
 *@author mario
 */

@Named(value = "categoryMenu")
@ApplicationScoped
public class CategoryMenu {

    List <Category> categories;
    @EJB private CategorySessionBeans categorySessionBeans;

    @PostConstruct 
    public void loadCategories (){
        categories = categorySessionBeans.findCategories();
    }
    public List <Category> getCategories(){
        return categories;
    }


}