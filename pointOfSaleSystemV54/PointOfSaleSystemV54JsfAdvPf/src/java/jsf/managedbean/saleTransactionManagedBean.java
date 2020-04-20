/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.SaleTransactionEntitySessionBeanLocal;
import entity.SaleTransactionEntity;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author Kaijing
 */
@Named(value = "saleTransactionManagedBean")
@ViewScoped
public class saleTransactionManagedBean implements Serializable{

    @EJB
    private SaleTransactionEntitySessionBeanLocal saleTransactionEntitySessionBeanLocal;
    
    private List<SaleTransactionEntity> saleTransactions;
    
    public saleTransactionManagedBean() {
    }
    @PostConstruct
    public void postConstruct()
    {
        setSaleTransactions(saleTransactionEntitySessionBeanLocal.retrieveAllSaleTransactions());
    }

    /**
     * @return the saleTransactions
     */
    public List<SaleTransactionEntity> getSaleTransactions() {
        return saleTransactions;
    }

    /**
     * @param saleTransactions the saleTransactions to set
     */
    public void setSaleTransactions(List<SaleTransactionEntity> saleTransactions) {
        this.saleTransactions = saleTransactions;
    }
    
}
