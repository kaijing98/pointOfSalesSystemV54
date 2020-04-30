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
import org.primefaces.event.RowEditEvent;
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
    private List<BigDecimal> subTotal;
    private List<ProductEntity> currentProductEntities;
    private List<Integer> currentProductQuantity;
    private Integer currQuantity;
    
        
    public shoppingCartJSFManagedBean() {
        saleTransactionLineItemEntities = new ArrayList<>();
        currentProductEntities = new ArrayList<>();
        currentProductQuantity = new ArrayList<>();
        totalLineItem = 0;
        totalQuantity = 0;
        totalAmount = new BigDecimal("0.00");
        subTotal = new ArrayList<>();
    }
    
    @PostConstruct
    public void postConstruct(){
        CustomerEntity customer = (CustomerEntity)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentCustomerEntity");        
    }
    
    public void doAddItemToCart(ActionEvent event) {
        productAdded = (ProductEntity)event.getComponent().getAttributes().get("itemAdded");
    }
    
    public void onRowEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Quantity Edited", null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
     
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void addItemToCart(ActionEvent event) {
        subTotal = checkoutSessionBeanLocal.addItem(productAdded, quantity);
        currentProductEntities = checkoutSessionBeanLocal.getCurrentProductEntities();
        currentProductQuantity = checkoutSessionBeanLocal.getCurrentProductQuantity();
        totalLineItem = checkoutSessionBeanLocal.getTotalLineItem();
        totalQuantity = checkoutSessionBeanLocal.getTotalQuantity();
        totalAmount = checkoutSessionBeanLocal.getTotalAmount();
        saleTransactionLineItemEntities = checkoutSessionBeanLocal.getSaleTransactionLineItemEntities();
    }
    
    public Integer currQuantity(ProductEntity currProduct) {
        Integer index = currentProductEntities.indexOf(currProduct);
        return currentProductQuantity.get(index);
    }
    
    public BigDecimal currSubTotal(ProductEntity currProduct) {
        Integer index = currentProductEntities.indexOf(currProduct);
        return subTotal.get(index);
    }
    
    public void checkout(ActionEvent event) {
        CustomerEntity customer = (CustomerEntity)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentCustomerEntity");        
        try {
            checkoutSessionBeanLocal.customerDoCheckout(customer.getCustomerId());
            totalLineItem = checkoutSessionBeanLocal.getTotalLineItem();
            totalQuantity = checkoutSessionBeanLocal.getTotalQuantity();
            totalAmount = checkoutSessionBeanLocal.getTotalAmount();
            subTotal = checkoutSessionBeanLocal.getSubTotalArr();
            currentProductEntities = checkoutSessionBeanLocal.getCurrentProductEntities();
            currentProductQuantity = checkoutSessionBeanLocal.getCurrentProductQuantity();
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
    public List<BigDecimal> getSubTotal() {
        return subTotal;
    }

    /**
     * @param subTotal the subTotal to set
     */
    public void setSubTotal(List<BigDecimal> subTotal) {
        this.subTotal = subTotal;
    }

    /**
     * @return the currentProductEntities
     */
    public List<ProductEntity> getCurrentProductEntities() {
        return currentProductEntities;
    }

    /**
     * @param currentProductEntities the currentProductEntities to set
     */
    public void setCurrentProductEntities(List<ProductEntity> currentProductEntities) {
        this.currentProductEntities = currentProductEntities;
    }

    /**
     * @return the currentProductQuantity
     */
    public List<Integer> getCurrentProductQuantity() {
        return currentProductQuantity;
    }

    /**
     * @param currentProductQuantity the currentProductQuantity to set
     */
    public void setCurrentProductQuantity(List<Integer> currentProductQuantity) {
        this.currentProductQuantity = currentProductQuantity;
    }

    /**
     * @return the currQuantity
     */
    public Integer getCurrQuantity() {
        return currQuantity;
    }

    /**
     * @param currQuantity the currQuantity to set
     */
    public void setCurrQuantity(Integer currQuantity) {
        this.currQuantity = currQuantity;
    }
}
