/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateful.CheckoutSessionBeanLocal;
import entity.ProductEntity;
import entity.SaleTransactionLineItemEntity;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 *
 * @author Kaijing
 */
@Named(value = "shoppingCartJSFManagedBean")
@SessionScoped
public class shoppingCartJSFManagedBean implements Serializable {

    @EJB
    private CheckoutSessionBeanLocal checkoutSessionBeanLocal;
    
    private ProductEntity itemAdded;
    private Integer quantity;
    private List<SaleTransactionLineItemEntity> saleTransactionLineItemEntities;
    private Integer totalLineItem;    
    private Integer totalQuantity;    
    private BigDecimal totalAmount; 
    
        
    public shoppingCartJSFManagedBean() {
        
    }
    
    @PostConstruct
    public void postConstruct(){
        setSaleTransactionLineItemEntities(checkoutSessionBeanLocal.getSaleTransactionLineItemEntities());
        setTotalLineItem(checkoutSessionBeanLocal.getTotalLineItem());
        setTotalQuantity(checkoutSessionBeanLocal.getTotalQuantity());
        setTotalAmount(checkoutSessionBeanLocal.getTotalAmount());
        
    }
    
    public void addItemToCart(ActionEvent event) {
        ProductEntity productAdded = (ProductEntity)event.getComponent().getAttributes().get("itemAdded");
        System.out.println(productAdded);
        System.out.println(quantity);
        BigDecimal currTotalAmount = checkoutSessionBeanLocal.addItem(productAdded, quantity);
        System.out.println(currTotalAmount);
    }

    /**
     * @return the itemAdded
     */
    public ProductEntity getItemAdded() {
        return itemAdded;
    }

    /**
     * @param itemAdded the itemAdded to set
     */
    public void setItemAdded(ProductEntity itemAdded) {
        this.itemAdded = itemAdded;
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
}
