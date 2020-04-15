package jsf.managedbean;

import ejb.session.stateless.CustomerEntitySessionBeanLocal;
import ejb.session.stateless.StaffEntitySessionBeanLocal;
import entity.CustomerEntity;
import entity.StaffEntity;
import java.io.IOException;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import util.exception.InvalidLoginCredentialException;



@Named(value = "loginManagedBean")
@RequestScoped

public class LoginManagedBean 
{
    @EJB (name = "staffEntitySessionBeanLocal")
    private StaffEntitySessionBeanLocal staffEntitySessionBeanLocal;
    
    @EJB (name = "customerEntitySessionBeanLocal")
    private CustomerEntitySessionBeanLocal customerEntitySessionBeanLocal;
    
    private String username;
    private String password;
    
    private String typeOfUser;
    
    public LoginManagedBean() 
    {
    }
    
    public void chooseUser(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        if (typeOfUser.equals("Staff")) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("isStaff", true);
        } else if (typeOfUser.equals("Customer")) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("isStaff", false);
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("/PointOfSaleSystemV54JsfAdvPf");
    }
    
    public void staffLogin(ActionEvent event) throws IOException
    {
            
        try
        {
            StaffEntity currentStaffEntity = staffEntitySessionBeanLocal.staffLogin(username, password);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentStaffEntity", currentStaffEntity);
            FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("isLogin", true);
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/index.xhtml");
        }
        catch(InvalidLoginCredentialException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid login credential: " + ex.getMessage(), null));
        }
    }
    
    public void customerLogin(ActionEvent event) throws IOException
    {
            
        try
        {
            CustomerEntity currentCustomerEntity = customerEntitySessionBeanLocal.customerLogin(username, password);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentCustomerEntity", currentCustomerEntity);
            FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("isLogin", true);
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/index.xhtml");
        }
        catch(InvalidLoginCredentialException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid login credential: " + ex.getMessage(), null));
        }
    }
    
    public void logout(ActionEvent event) throws IOException
    {
        ((HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true)).invalidate();
        FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/index.xhtml");
    }

    
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the typeOfUser
     */
    public String getTypeOfUser() {
        return typeOfUser;
    }

    /**
     * @param typeOfUser the typeOfUser to set
     */
    public void setTypeOfUser(String typeOfUser) {
        this.typeOfUser = typeOfUser;
    }
}
