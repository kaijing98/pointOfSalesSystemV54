package ejb.session.stateless;

import entity.CustomerEntity;
import entity.SaleTransactionEntity;
import entity.SaleTransactionLineItemEntity;
import entity.StaffEntity;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CreateNewSaleTransactionException;
import util.exception.CustomerNotFoundException;
import util.exception.ProductInsufficientQuantityOnHandException;
import util.exception.ProductNotFoundException;
import util.exception.SaleTransactionAlreadyVoidedRefundedException;
import util.exception.SaleTransactionNotFoundException;
import util.exception.StaffNotFoundException;



@Stateless
@Local(SaleTransactionEntitySessionBeanLocal.class)
@Remote(SaleTransactionEntitySessionBeanRemote.class)

public class SaleTransactionEntitySessionBean implements SaleTransactionEntitySessionBeanLocal, SaleTransactionEntitySessionBeanRemote
{       
    @PersistenceContext(unitName = "PointOfSaleSystemV54-ejbPU")
    private EntityManager entityManager;
    @Resource
    private EJBContext eJBContext;
    
    @EJB
    private StaffEntitySessionBeanLocal staffEntitySessionBeanLocal;
    @EJB
    private CustomerEntitySessionBeanLocal customerEntitySessionBeanLocal;
    @EJB
    private ProductEntitySessionBeanLocal productEntitySessionBeanLocal;
    
    
    
    
    public SaleTransactionEntitySessionBean()
    {
    }
    
    
    
    // Updated in v4.1
    
    @Override
    public SaleTransactionEntity createNewSaleTransaction(Long staffId, SaleTransactionEntity newSaleTransactionEntity) throws StaffNotFoundException, CreateNewSaleTransactionException
    {
        if(newSaleTransactionEntity != null)
        {
            try
            {
                StaffEntity staffEntity = staffEntitySessionBeanLocal.retrieveStaffByStaffId(staffId);
                newSaleTransactionEntity.setStaffEntity(staffEntity);
                staffEntity.getSaleTransactionEntities().add(newSaleTransactionEntity);

                entityManager.persist(newSaleTransactionEntity);

                for(SaleTransactionLineItemEntity saleTransactionLineItemEntity:newSaleTransactionEntity.getSaleTransactionLineItemEntities())
                {
                    productEntitySessionBeanLocal.debitQuantityOnHand(saleTransactionLineItemEntity.getProductEntity().getProductId(), saleTransactionLineItemEntity.getQuantity());
                    entityManager.persist(saleTransactionLineItemEntity);
                }

                entityManager.flush();

                return newSaleTransactionEntity;
            }
            catch(ProductNotFoundException | ProductInsufficientQuantityOnHandException ex)
            {
                // The line below rolls back all changes made to the database.
                eJBContext.setRollbackOnly();
                throw new CreateNewSaleTransactionException(ex.getMessage());
            }
        }
        else
        {
            throw new CreateNewSaleTransactionException("Sale transaction information not provided");
        }
    }
    
    @Override
    public SaleTransactionEntity customerCreateNewSaleTransaction(Long customerId, SaleTransactionEntity newSaleTransactionEntity) throws CustomerNotFoundException, CreateNewSaleTransactionException
    {
        if(newSaleTransactionEntity != null)
        {
            try
            {
                CustomerEntity customerEntity = customerEntitySessionBeanLocal.retrieveCustomerById(customerId);
                newSaleTransactionEntity.setCustomerEntity(customerEntity);
                customerEntity.getSaleTransactionEntities().add(newSaleTransactionEntity);

                entityManager.persist(newSaleTransactionEntity);

                for(SaleTransactionLineItemEntity saleTransactionLineItemEntity:newSaleTransactionEntity.getSaleTransactionLineItemEntities())
                {
                    productEntitySessionBeanLocal.debitQuantityOnHand(saleTransactionLineItemEntity.getProductEntity().getProductId(), saleTransactionLineItemEntity.getQuantity());
                    entityManager.persist(saleTransactionLineItemEntity);
                }

                entityManager.flush();

                return newSaleTransactionEntity;
            }
            catch(ProductNotFoundException | ProductInsufficientQuantityOnHandException ex)
            {
                // The line below rolls back all changes made to the database.
                eJBContext.setRollbackOnly();
                throw new CreateNewSaleTransactionException(ex.getMessage());
            }
        }
        else
        {
            throw new CreateNewSaleTransactionException("Sale transaction information not provided");
        }
    }
    
    
    
    @Override
    public List<SaleTransactionEntity> retrieveAllSaleTransactions()
    {
        Query query = entityManager.createQuery("SELECT st FROM SaleTransactionEntity st");
        
        return query.getResultList();
    }
    
    
    
    // Added in v4.1
    
    @Override
    public List<SaleTransactionLineItemEntity> retrieveSaleTransactionLineItemsByProductId(Long productId)
    {
        Query query = entityManager.createNamedQuery("selectAllSaleTransactionLineItemsByProductId");
        query.setParameter("inProductId", productId);
        
        return query.getResultList();
    }
    
    
    
    @Override
    public SaleTransactionEntity retrieveSaleTransactionBySaleTransactionId(Long saleTransactionId) throws SaleTransactionNotFoundException
    {
        SaleTransactionEntity saleTransactionEntity = entityManager.find(SaleTransactionEntity.class, saleTransactionId);
        
        if(saleTransactionEntity != null)
        {
            saleTransactionEntity.getSaleTransactionLineItemEntities().size();
            
            return saleTransactionEntity;
        }
        else
        {
            throw new SaleTransactionNotFoundException("Sale Transaction ID " + saleTransactionId + " does not exist!");
        }                
    }
    
    
    
    @Override
    public void updateSaleTransaction(SaleTransactionEntity saleTransactionEntity)
    {
        entityManager.merge(saleTransactionEntity);
    }
    
    
    
    // Updated in v4.1
    
    @Override
    public void voidRefundSaleTransaction(Long saleTransactionId) throws SaleTransactionNotFoundException, SaleTransactionAlreadyVoidedRefundedException
    {
        SaleTransactionEntity saleTransactionEntity = retrieveSaleTransactionBySaleTransactionId(saleTransactionId);
        
        if(!saleTransactionEntity.getVoidRefund())
        {
            for(SaleTransactionLineItemEntity saleTransactionLineItemEntity:saleTransactionEntity.getSaleTransactionLineItemEntities())
            {
                try
                {
                    productEntitySessionBeanLocal.creditQuantityOnHand(saleTransactionLineItemEntity.getProductEntity().getProductId(), saleTransactionLineItemEntity.getQuantity());
                }
                catch(ProductNotFoundException ex)
                {
                    ex.printStackTrace(); // Ignore exception since this should not happen
                }                
            }
            
            saleTransactionEntity.setVoidRefund(true);
        }
        else
        {
            throw new SaleTransactionAlreadyVoidedRefundedException("The sale transaction has aready been voided/refunded");
        }
    }
    
    
    
    @Override
    public void deleteSaleTransaction(SaleTransactionEntity saleTransactionEntity)
    {
        throw new UnsupportedOperationException();
    }
}
