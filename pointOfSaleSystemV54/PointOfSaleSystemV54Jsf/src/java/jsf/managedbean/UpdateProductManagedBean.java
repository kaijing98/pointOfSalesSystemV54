package jsf.managedbean;

import ejb.session.stateless.CategoryEntitySessionBeanLocal;
import ejb.session.stateless.ProductEntitySessionBeanLocal;
import ejb.session.stateless.TagEntitySessionBeanLocal;
import entity.CategoryEntity;
import entity.ProductEntity;
import entity.TagEntity;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exception.ProductNotFoundException;



@Named(value = "updateProductManagedBean")
@ViewScoped

public class UpdateProductManagedBean implements Serializable
{
    @EJB
    private ProductEntitySessionBeanLocal productEntitySessionBeanLocal;
    @EJB
    private CategoryEntitySessionBeanLocal categoryEntitySessionBeanLocal;
    @EJB
    private TagEntitySessionBeanLocal tagEntitySessionBeanLocal;
    
    private Long productIdToUpdate;
    private ProductEntity productEntityToUpdate;
    private Long categoryId;
    private List<Long> tagIds;
    private List<CategoryEntity> categoryEntities;
    private List<TagEntity> tagEntities;
    
    
    
    public UpdateProductManagedBean() 
    {        
    }
    
    
    
    @PostConstruct
    public void postConstruct()
    {
        productIdToUpdate = (Long)FacesContext.getCurrentInstance().getExternalContext().getFlash().get("productIdToUpdate");
        
        try
        {            
            productEntityToUpdate = productEntitySessionBeanLocal.retrieveProductByProductId(productIdToUpdate);
            categoryId = productEntityToUpdate.getCategoryEntity().getCategoryId();
            tagIds = new ArrayList<>();
            
            for(TagEntity tagEntity:productEntityToUpdate.getTagEntities())
            {
                tagIds.add(tagEntity.getTagId());
            }
            
            categoryEntities = categoryEntitySessionBeanLocal.retrieveAllLeafCategories();
            tagEntities = tagEntitySessionBeanLocal.retrieveAllTags();
        }
        catch(ProductNotFoundException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while retrieving the product details: " + ex.getMessage(), null));
        }
        catch(Exception ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }
    
    
    
    public void back(ActionEvent event) throws IOException
    {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("productIdToView", productIdToUpdate);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewProductDetails.xhtml");
    }
    
    
    
    public void foo()
    {        
    }
    
    
    
    public void updateProduct(ActionEvent event)
    {
        if(categoryId == 0)
        {
            categoryId = null;
        }                
        
        try
        {
            productEntitySessionBeanLocal.updateProduct(productEntityToUpdate, categoryId, tagIds);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product updated successfully", null));
        }
        catch(ProductNotFoundException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating product: " + ex.getMessage(), null));
        }
        catch(Exception ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }
    
    
    
    public ProductEntity getProductEntityToUpdate() {
        return productEntityToUpdate;
    }

    public void setProductEntityToUpdate(ProductEntity productEntityToUpdate) {
        this.productEntityToUpdate = productEntityToUpdate;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public List<CategoryEntity> getCategoryEntities() {
        return categoryEntities;
    }

    public void setCategoryEntities(List<CategoryEntity> categoryEntities) {
        this.categoryEntities = categoryEntities;
    }

    public List<TagEntity> getTagEntities() {
        return tagEntities;
    }

    public void setTagEntities(List<TagEntity> tagEntities) {
        this.tagEntities = tagEntities;
    }
}
