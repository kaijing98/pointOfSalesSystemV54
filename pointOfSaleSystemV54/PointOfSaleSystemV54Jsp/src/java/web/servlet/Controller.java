package web.servlet;

import ejb.session.stateless.CategoryEntitySessionBeanLocal;
import ejb.session.stateless.CustomerEntitySessionBeanLocal;
import ejb.session.stateless.MessageOfTheDayEntitySessionBeanLocal;
import ejb.session.stateless.ProductEntitySessionBeanLocal;
import ejb.session.stateless.StaffEntitySessionBeanLocal;
import ejb.session.stateless.TagEntitySessionBeanLocal;
import entity.CategoryEntity;
import entity.MessageOfTheDayEntity;
import entity.ProductEntity;
import entity.StaffEntity;
import entity.TagEntity;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.exception.CategoryNotFoundException;
import util.exception.CreateNewProductException;
import util.exception.DeleteProductException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.ProductNotFoundException;
import util.exception.ProductSkuCodeExistException;
import util.exception.TagNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateProductException;



@WebServlet(name = "Controller", urlPatterns = {"/Controller"})

public class Controller extends HttpServlet 
{
    @EJB
    private StaffEntitySessionBeanLocal staffEntitySessionBeanLocal;
    @EJB
    private CustomerEntitySessionBeanLocal customerEntitySessionBeanLocal;
    @EJB
    private ProductEntitySessionBeanLocal productEntitySessionBeanLocal;
    @EJB
    private CategoryEntitySessionBeanLocal categoryEntitySessionBeanLocal;
    @EJB
    private TagEntitySessionBeanLocal tagEntitySessionBeanLocal;
    @EJB
    private MessageOfTheDayEntitySessionBeanLocal messageOfTheDayEntitySessionBeanLocal;
    
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String action = request.getParameter("action");
        
        if(action != null)
        {
            if(action.equals("accessRightError"))
            {
                request.setAttribute("protectedResource", request.getParameter("protectedResource"));
                request.getRequestDispatcher("/accessRightError.jsp").forward(request, response);
            }
            else if(action.equals("login_post"))
            {
                try
                {
                    StaffEntity currentStaffEntity = staffEntitySessionBeanLocal.staffLogin(request.getParameter("username"), request.getParameter("password"));
                    List<MessageOfTheDayEntity> messageOfTheDayEntities = messageOfTheDayEntitySessionBeanLocal.retrieveAllMessagesOfTheDay();
                    request.getSession(true).setAttribute("isLogin", true);
                    request.getSession(true).setAttribute("currentStaffEntity", currentStaffEntity);
                    request.getSession(true).setAttribute("messageOfTheDayEntities", messageOfTheDayEntities);
                }
                catch(InvalidLoginCredentialException ex)
                {
                    request.setAttribute("loginError", "Invalid login credential: " + ex.getMessage());                    
                }
                finally
                {
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                }
            }
            else if(action.equals("logout_post"))
            {
                request.getSession(true).invalidate();
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
            else if(action.equals("checkout"))
            {
                request.getRequestDispatcher("/cashierOperation/checkout.jsp").forward(request, response);
            }
            else if(action.equals("voidRefund"))
            {
                request.getRequestDispatcher("/cashierOperation/voidRefund.jsp").forward(request, response);
            }
            else if(action.equals("viewMySaleTransactions"))
            {
                request.getRequestDispatcher("/cashierOperation/viewMySaleTransactions.jsp").forward(request, response);
            }            
            else if(action.equals("viewAllStaffs"))
            {
                request.getRequestDispatcher("/systemAdministration/viewAllStaffs.jsp").forward(request, response);
            }
            else if(action.equals("createNewStaff"))
            {
                request.getRequestDispatcher("/systemAdministration/createNewStaff.jsp").forward(request, response);
            }
            else if(action.equals("viewAllProducts"))
            {
                List<ProductEntity> productEntities = productEntitySessionBeanLocal.retrieveAllProducts();
                request.setAttribute("productEntities", productEntities);
                request.getRequestDispatcher("/systemAdministration/viewAllProducts.jsp").forward(request, response);
            }
            else if(action.equals("createNewProduct"))
            {
                List<CategoryEntity> categoryEntities = categoryEntitySessionBeanLocal.retrieveAllLeafCategories();
                List<TagEntity> tagEntities = tagEntitySessionBeanLocal.retrieveAllTags();
                request.setAttribute("categoryEntities", categoryEntities);
                request.setAttribute("tagEntities", tagEntities);
                request.getRequestDispatcher("/systemAdministration/createNewProduct.jsp").forward(request, response);
            }
            else if(action.equals("createNewProduct_post"))
            {
                ProductEntity newProductEntity = new ProductEntity(request.getParameter("skuCode"), request.getParameter("name"), request.getParameter("description"), Integer.valueOf(request.getParameter("quantityOnHand")), Integer.valueOf(request.getParameter("reorderQuantity")), new BigDecimal(request.getParameter("unitPrice")), Integer.valueOf(request.getParameter("productRating")));
                Long categoryId = Long.valueOf(request.getParameter("categoryId"));
                List<Long> tagIds = null;
                String[] tagIdsArray = request.getParameterValues("tagIds");
                
                if(tagIdsArray != null)
                {
                    tagIds = new ArrayList<>();
                    
                    for(int i = 0; i < tagIdsArray.length; i++)
                    {
                        tagIds.add(Long.valueOf(tagIdsArray[i]));
                    }
                }
                                
                try 
                {
                    newProductEntity = productEntitySessionBeanLocal.createNewProduct(newProductEntity, categoryId, tagIds);
                    
                    request.setAttribute("message", "New product created successfully!: " + newProductEntity.getProductId());
                } 
                catch (ProductSkuCodeExistException ex)
                {
                    request.setAttribute("error", true);
                    request.setAttribute("message", "An error has occurred while creating the new product: The product SKU code already exist");
                }
                catch (InputDataValidationException | CreateNewProductException | UnknownPersistenceException ex) 
                {
                    request.setAttribute("error", true);
                    request.setAttribute("message", "An error has occurred while creating the new product: " + ex.getMessage());
                }
                
                List<CategoryEntity> categoryEntities = categoryEntitySessionBeanLocal.retrieveAllLeafCategories();
                List<TagEntity> tagEntities = tagEntitySessionBeanLocal.retrieveAllTags();
                request.setAttribute("categoryEntities", categoryEntities);
                request.setAttribute("tagEntities", tagEntities);
                
                request.getRequestDispatcher("/systemAdministration/createNewProduct.jsp").forward(request, response);
            }
            else if(action.equals("viewProductDetails"))
            {
                try
                {
                    ProductEntity productEntityToView = productEntitySessionBeanLocal.retrieveProductByProductId(Long.valueOf(request.getParameter("productId")));
                    request.setAttribute("productEntityToView", productEntityToView);
                }
                catch(ProductNotFoundException ex)
                {
                    request.setAttribute("productNotFoundException", ex.getMessage());
                }
                
                request.getRequestDispatcher("/systemAdministration/viewProductDetails.jsp").forward(request, response);
            }
            else if(action.equals("updateProduct"))
            {
                try
                {
                    ProductEntity productEntityToUpdate = productEntitySessionBeanLocal.retrieveProductByProductId(Long.valueOf(request.getParameter("productId")));
                    request.setAttribute("productEntityToUpdate", productEntityToUpdate);                                    
                }
                catch(ProductNotFoundException ex)
                {
                    request.setAttribute("productNotFoundException", ex.getMessage());
                }
                
                List<CategoryEntity> categoryEntities = categoryEntitySessionBeanLocal.retrieveAllLeafCategories();
                List<TagEntity> tagEntities = tagEntitySessionBeanLocal.retrieveAllTags();
                request.setAttribute("categoryEntities", categoryEntities);
                request.setAttribute("tagEntities", tagEntities);    
                
                request.getRequestDispatcher("/systemAdministration/updateProduct.jsp").forward(request, response);
            }
            else if(action.equals("updateProduct_post"))
            {
                ProductEntity productEntity = new ProductEntity(request.getParameter("skuCode"), request.getParameter("name"), request.getParameter("description"), Integer.valueOf(request.getParameter("quantityOnHand")), Integer.valueOf(request.getParameter("reorderQuantity")), new BigDecimal(request.getParameter("unitPrice")), Integer.valueOf(request.getParameter("productRating")));
                productEntity.setProductId(Long.valueOf(request.getParameter("productId")));                        
                Long categoryId = Long.valueOf(request.getParameter("categoryId"));
                List<Long> tagIds = null;
                String[] tagIdsArray = request.getParameterValues("tagIds");
                
                if(tagIdsArray != null)
                {
                    tagIds = new ArrayList<>();
                    
                    for(int i = 0; i < tagIdsArray.length; i++)
                    {
                        tagIds.add(Long.valueOf(tagIdsArray[i]));
                    }
                }
                                
                try 
                {
                    productEntitySessionBeanLocal.updateProduct(productEntity, categoryId, tagIds);
                    
                    request.setAttribute("message", "Product updated successfully!: " + productEntity.getProductId());
                } 
                catch (InputDataValidationException | ProductNotFoundException | CategoryNotFoundException | TagNotFoundException | UpdateProductException ex) 
                {
                    request.setAttribute("error", true);
                    request.setAttribute("message", "An error has occurred while updating the product: " + ex.getMessage());
                }
                
                List<CategoryEntity> categoryEntities = categoryEntitySessionBeanLocal.retrieveAllLeafCategories();
                List<TagEntity> tagEntities = tagEntitySessionBeanLocal.retrieveAllTags();
                request.setAttribute("categoryEntities", categoryEntities);
                request.setAttribute("tagEntities", tagEntities);
                
                try
                {
                    ProductEntity productEntityToUpdate = productEntitySessionBeanLocal.retrieveProductByProductId(Long.valueOf(request.getParameter("productId")));
                    request.setAttribute("productEntityToUpdate", productEntityToUpdate);
                }
                catch(ProductNotFoundException ex)
                {
                    request.setAttribute("productNotFoundException", ex.getMessage());
                }
                
                request.getRequestDispatcher("/systemAdministration/updateProduct.jsp").forward(request, response);
            }
            else if(action.equals("deleteProduct"))
            {
                try
                {
                    ProductEntity productEntityToDelete = productEntitySessionBeanLocal.retrieveProductByProductId(Long.valueOf(request.getParameter("productId")));
                    request.setAttribute("productEntityToDelete", productEntityToDelete);                    
                }
                catch(ProductNotFoundException ex)
                {
                    request.setAttribute("productNotFoundException", ex.getMessage());
                }
                
                request.getRequestDispatcher("/systemAdministration/deleteProduct.jsp").forward(request, response);
            }
            else if(action.equals("deleteProduct_post"))
            {                
                Long productIdToDelete = Long.valueOf(request.getParameter("productId"));                
                
                try
                {
                    productEntitySessionBeanLocal.deleteProduct(productIdToDelete);
                    request.setAttribute("message", "Product deleted successfully!: " + productIdToDelete);
                }
                catch(ProductNotFoundException | DeleteProductException ex)
                {
                    try
                    {
                        ProductEntity productEntityToDelete = productEntitySessionBeanLocal.retrieveProductByProductId(productIdToDelete);
                        request.setAttribute("productEntityToDelete", productEntityToDelete);
                    }
                    catch(ProductNotFoundException pnfe)
                    {
                        request.setAttribute("productNotFoundException", pnfe.getMessage());
                    }
                    
                    request.setAttribute("error", true);
                    request.setAttribute("message", "An error has occurred while deleting the product: " + ex.getMessage());
                }                               
                
                request.getRequestDispatcher("/systemAdministration/deleteProduct.jsp").forward(request, response);
            }
        }
        else
        {
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
    
    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        processRequest(request, response);
    }

    
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        processRequest(request, response);
    }

    
    
    @Override
    public String getServletInfo() 
    {
        return "Default controller for Point-of-Sale System (v5.4)";
    }
}
