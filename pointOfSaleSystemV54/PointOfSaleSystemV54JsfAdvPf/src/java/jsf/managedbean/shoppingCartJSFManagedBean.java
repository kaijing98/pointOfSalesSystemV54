/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateful.CheckoutSessionBeanLocal;
import entity.CustomerEntity;
import entity.ProductEntity;
import entity.SaleTransactionLineItemEntity;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import util.exception.CreateNewSaleTransactionException;
import util.exception.CustomerNotFoundException;

/**
 *
 * @author Kaijing
 */
@Named(value = "shoppingCartJSFManagedBean")
@SessionScoped
public class shoppingCartJSFManagedBean implements Serializable {

    @EJB(name = "CheckoutSessionBeanLocal")
    private CheckoutSessionBeanLocal checkoutSessionBeanLocal;
    
    private ProductEntity productAdded;
    private Integer quantity;
    private List<SaleTransactionLineItemEntity> saleTransactionLineItemEntities;
    private Integer totalLineItem;    
    private Integer totalQuantity;    
    private BigDecimal totalAmount; 
    private BigDecimal subTotal;
    
        
    public shoppingCartJSFManagedBean() {
        saleTransactionLineItemEntities = new ArrayList<>();
        totalLineItem = 0;
        totalQuantity = 0;
        totalAmount = new BigDecimal("0.00");
        subTotal = new BigDecimal("0.00");
    }
    
    @PostConstruct
    public void postConstruct(){
        CustomerEntity customer = (CustomerEntity)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentCustomerEntity");        
    }
    
    public void doAddItemToCart(ActionEvent event) {
        productAdded = (ProductEntity)event.getComponent().getAttributes().get("itemAdded");
    }
    
    public void addItemToCart(ActionEvent event) {
        setSubTotal(checkoutSessionBeanLocal.addItem(productAdded, quantity));
        totalLineItem = checkoutSessionBeanLocal.getTotalLineItem();
        totalQuantity = checkoutSessionBeanLocal.getTotalQuantity();
        totalAmount = checkoutSessionBeanLocal.getTotalAmount();
        subTotal = getSubTotal();
        saleTransactionLineItemEntities = checkoutSessionBeanLocal.getSaleTransactionLineItemEntities();
    }
    
    public void checkout(ActionEvent event) {
        CustomerEntity customer = (CustomerEntity)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentCustomerEntity");        
        try {
            checkoutSessionBeanLocal.customerDoCheckout(customer.getCustomerId());
            totalLineItem = checkoutSessionBeanLocal.getTotalLineItem();
            totalQuantity = checkoutSessionBeanLocal.getTotalQuantity();
            totalAmount = checkoutSessionBeanLocal.getTotalAmount();
            subTotal = new BigDecimal("0.00");
        } catch (CustomerNotFoundException | CreateNewSaleTransactionException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while checking out: " + ex.getMessage(), null));
        }
    }

    /**
     * @return the quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the saleTransactionLineItemEntities
     */
    public List<SaleTransactionLineItemEntity> getSaleTransactionLineItemEntities() {
        return saleTransactionLineItemEntities;
    }

    /**
     * @param saleTransactionLineItemEntities the saleTransactionLineItemEntities to set
     */
    public void setSaleTransactionLineItemEntities(List<SaleTransactionLineItemEntity> saleTransactionLineItemEntities) {
        this.saleTransactionLineItemEntities = saleTransactionLineItemEntities;
    }

    /**
     * @return the totalLineItem
     */
    public Integer getTotalLineItem() {
        return totalLineItem;
    }

    /**
     * @param totalLineItem the totalLineItem to set
     */
    public void setTotalLineItem(Integer totalLineItem) {
        this.totalLineItem = totalLineItem;
    }

    /**
     * @return the totalQuantity
     */
    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    /**
     * @param totalQuantity the totalQuantity to set
     */
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    /**
     * @return the totalAmount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount the totalAmount to set
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * @return the productAdded
     */
    public ProductEntity getProductAdded() {
        return productAdded;
    }

    /**
     * @param productAdded the productAdded to set
     */
    public void setProductAdded(ProductEntity productAdded) {
        this.productAdded = productAdded;
    }

    /**
     * @return the subTotal
     */
    public BigDecimal getSubTotal() {
        return subTotal;
    }

    /**
     * @param subTotal the subTotal to set
     */
    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
}
