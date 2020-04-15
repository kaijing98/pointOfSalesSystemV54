/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.CustomerEntitySessionBeanLocal;
import entity.CustomerEntity;
import java.io.IOException;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exception.CustomerNotFoundException;
import util.exception.CustomerUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Kaijing
 */
@Named(value = "customerJSFManagedBean")
@ViewScoped
public class customerJSFManagedBean implements Serializable {
    
    @EJB (name = "CustomerSessionBeanLocal")
    private CustomerEntitySessionBeanLocal customerSessionBeanLocal;

    private CustomerEntity newCustomer;
    
    public customerJSFManagedBean() {
        newCustomer = new CustomerEntity();
    }
    
    public void createNewCustomer(ActionEvent event) throws IOException  {
        try
            {
                Long customerId = customerSessionBeanLocal.createNewCustomer(getNewCustomer());
                CustomerEntity createdCustomer = customerSessionBeanLocal.retrieveCustomerById(customerId);
                
                setNewCustomer(new CustomerEntity());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New customer created successfully (Customer Name: " + createdCustomer.getFirstName() + ")", null));
//                FacesContext.getCurrentInstance().getExternalContext().redirect("/tailoredJsf-war/index.xhtml");
            }
            catch(InputDataValidationException | CustomerUsernameExistException  | CustomerNotFoundException | UnknownPersistenceException ex)
            {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new customer: " + ex.getMessage(), null));
            }
    }

    /**
     * @return the newCustomer
     */
    public CustomerEntity getNewCustomer() {
        return newCustomer;
    }

    /**
     * @param newCustomer the newCustomer to set
     */
    public void setNewCustomer(CustomerEntity newCustomer) {
        this.newCustomer = newCustomer;
    }
    
}
