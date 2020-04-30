package ejb.session.stateful;

import entity.ProductEntity;
import entity.SaleTransactionEntity;
import entity.SaleTransactionLineItemEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import ejb.session.stateless.SaleTransactionEntitySessionBeanLocal;
import javax.ejb.Remove;
import util.exception.CreateNewSaleTransactionException;
import util.exception.CustomerNotFoundException;
import util.exception.StaffNotFoundException;



@Stateful
@Local(CheckoutSessionBeanLocal.class)
@Remote(CheckoutSessionBeanRemote.class)

public class CheckoutSessionBean implements CheckoutSessionBeanLocal, CheckoutSessionBeanRemote
{
    @EJB
    private SaleTransactionEntitySessionBeanLocal saleTransactionEntitySessionBeanLocal;
    
    private List<SaleTransactionLineItemEntity> saleTransactionLineItemEntities;
    private List<ProductEntity> currentProductEntities;
    private List<Integer> currentProductQuantity;
    private List<BigDecimal> subTotalArr;
    private Integer totalLineItem;    
    private Integer totalQuantity;    
    private BigDecimal totalAmount;   

    
    
    public CheckoutSessionBean() 
    {
        initialiseState();
    }
    
    
    
    @Remove
    public void remove()
    {        
    }
    
    
    
    private void initialiseState()
    {
        currentProductEntities = new ArrayList<>();
        currentProductQuantity = new ArrayList<>();
        subTotalArr = new ArrayList<>();
        saleTransactionLineItemEntities = new ArrayList<>();
        totalLineItem = 0;
        totalQuantity = 0;
        totalAmount = new BigDecimal("0.00");
    }
    
    
    @Override
    //returns updated list of subtotal for product entities in checkout
    public List<BigDecimal> addItem(ProductEntity productEntity, Integer quantity)
    {
        BigDecimal subTotal = new BigDecimal("0.00");
        if (!currentProductEntities.contains(productEntity)) {
            currentProductEntities.add(productEntity);
            currentProductQuantity.add(quantity);
            subTotal = productEntity.getUnitPrice().multiply(new BigDecimal(quantity));
            subTotalArr.add(subTotal);
            ++totalLineItem;
        }
        else {
            Integer index = currentProductEntities.indexOf(productEntity);
            currentProductQuantity.set(index, currentProductQuantity.get(index) + quantity);
            subTotal = productEntity.getUnitPrice().multiply(new BigDecimal(currentProductQuantity.get(index)));
            subTotalArr.set(index, subTotal);
        }
        saleTransactionLineItemEntities.add(new SaleTransactionLineItemEntity(totalLineItem, productEntity, quantity, productEntity.getUnitPrice(), subTotal));
        totalQuantity += quantity;
        totalAmount = totalAmount.add(subTotal);
        
        return subTotalArr;
    }
    
    
    
    @Override
    public SaleTransactionEntity doCheckout(Long staffId) throws StaffNotFoundException, CreateNewSaleTransactionException
    {
        SaleTransactionEntity newSaleTransactionEntity = saleTransactionEntitySessionBeanLocal.createNewSaleTransaction(staffId, new SaleTransactionEntity(totalLineItem, totalQuantity, totalAmount, new Date(), saleTransactionLineItemEntities, false));
        initialiseState();
        
        return newSaleTransactionEntity;
    }
    
    @Override
    public SaleTransactionEntity customerDoCheckout(Long customerId) throws CustomerNotFoundException, CreateNewSaleTransactionException
    {
        Boolean sufficient = true;
        for (int i = 0; i<saleTransactionLineItemEntities.size(); i++) {
            ProductEntity currProduct = saleTransactionLineItemEntities.get(i).getProductEntity();
            if (currProduct.getQuantityOnHand() < saleTransactionLineItemEntities.get(i).getQuantity()) {
                sufficient = false; 
                break;
            }
        }
        if (sufficient) {
            SaleTransactionEntity newSaleTransactionEntity = saleTransactionEntitySessionBeanLocal.customerCreateNewSaleTransaction(customerId, new SaleTransactionEntity(totalLineItem, totalQuantity, totalAmount, new Date(), saleTransactionLineItemEntities, false));
            initialiseState();
            return newSaleTransactionEntity;
        } 
        else {
            throw new CreateNewSaleTransactionException("Unable to checkout due to insufficient inventory");
        }
    }
    
    
    
    @Override
    public void clearShoppingCart()
    {
        initialiseState();
    }

    
    
    @Override
    public List<SaleTransactionLineItemEntity> getSaleTransactionLineItemEntities() {
        return saleTransactionLineItemEntities;
    }

    @Override
    public void setSaleTransactionLineItemEntities(List<SaleTransactionLineItemEntity> saleTransactionLineItemEntities) {
        this.saleTransactionLineItemEntities = saleTransactionLineItemEntities;
    }

    @Override
    public Integer getTotalLineItem() {
        return totalLineItem;
    }

    @Override
    public void setTotalLineItem(Integer totalLineItem) {
        this.totalLineItem = totalLineItem;
    }

    @Override
    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    @Override
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    @Override
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @Override
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public List<ProductEntity> getCurrentProductEntities() {
        return currentProductEntities;
    }

    @Override
    public void setCurrentProductEntities(List<ProductEntity> currentProductEntities) {
        this.currentProductEntities = currentProductEntities;
    }

    @Override
    public List<Integer> getCurrentProductQuantity() {
        return currentProductQuantity;
    }

    @Override
    public void setCurrentProductQuantity(List<Integer> currentProductQuantity) {
        this.currentProductQuantity = currentProductQuantity;
    }

    @Override
    public List<BigDecimal> getSubTotalArr() {
        return subTotalArr;
    }

    @Override
    public void setSubTotalArr(List<BigDecimal> subTotalArr) {
        this.subTotalArr = subTotalArr;
    }
}
