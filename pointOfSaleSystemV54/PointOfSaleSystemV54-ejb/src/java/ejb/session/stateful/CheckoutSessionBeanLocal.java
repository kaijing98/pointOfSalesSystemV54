package ejb.session.stateful;

import entity.ProductEntity;
import entity.SaleTransactionEntity;
import entity.SaleTransactionLineItemEntity;
import java.math.BigDecimal;
import java.util.List;
import util.exception.CreateNewSaleTransactionException;
import util.exception.CustomerNotFoundException;
import util.exception.StaffNotFoundException;



public interface CheckoutSessionBeanLocal
{
    void remove();
    
    
    
    List<BigDecimal> addItem(ProductEntity productEntity, Integer quantity);
    
    SaleTransactionEntity doCheckout(Long staffId) throws StaffNotFoundException, CreateNewSaleTransactionException;

    void clearShoppingCart();

    

    List<SaleTransactionLineItemEntity> getSaleTransactionLineItemEntities();
    
    void setSaleTransactionLineItemEntities(List<SaleTransactionLineItemEntity> saleTransactionLineItemEntities);

    Integer getTotalLineItem();
    
    void setTotalLineItem(Integer totalLineItem);
    
    Integer getTotalQuantity();
    
    void setTotalQuantity(Integer totalQuantity);
    
    BigDecimal getTotalAmount();

    void setTotalAmount(BigDecimal totalAmount);

    public SaleTransactionEntity customerDoCheckout(Long customerId) throws CustomerNotFoundException, CreateNewSaleTransactionException;

    public List<ProductEntity> getCurrentProductEntities();

    public void setCurrentProductEntities(List<ProductEntity> currentProductEntities);

    public List<Integer> getCurrentProductQuantity();

    public void setCurrentProductQuantity(List<Integer> currentProductQuantity);

    public List<BigDecimal> getSubTotalArr();

    public void setSubTotalArr(List<BigDecimal> subTotalArr);
}
