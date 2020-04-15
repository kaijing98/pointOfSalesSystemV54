package pointofsalesystemv54client;

import javax.ejb.EJB;
import ejb.session.stateful.CheckoutSessionBeanRemote;
import ejb.session.stateless.CategoryEntitySessionBeanRemote;
import ejb.session.stateless.EmailSessionBeanRemote;
import ejb.session.stateless.MessageOfTheDayEntitySessionBeanRemote;
import ejb.session.stateless.ProductEntitySessionBeanRemote;
import ejb.session.stateless.SaleTransactionEntitySessionBeanRemote;
import ejb.session.stateless.StaffEntitySessionBeanRemote;
import ejb.session.stateless.TagEntitySessionBeanRemote;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;



public class Main
{
    @EJB
    private static StaffEntitySessionBeanRemote staffEntitySessionBeanRemote;
    @EJB
    private static ProductEntitySessionBeanRemote productEntitySessionBeanRemote;
    @EJB
    private static CategoryEntitySessionBeanRemote categoryEntitySessionBeanRemote;
    @EJB
    private static TagEntitySessionBeanRemote tagEntitySessionBeanRemote;
    @EJB
    private static SaleTransactionEntitySessionBeanRemote saleTransactionEntitySessionBeanRemote;
    @EJB
    private static CheckoutSessionBeanRemote checkoutBeanRemote;
    @EJB
    private static EmailSessionBeanRemote emailSessionBeanRemote;
    @EJB
    private static MessageOfTheDayEntitySessionBeanRemote messageOfTheDayEntitySessionBeanRemote;
    
    @Resource(mappedName = "jms/queueCheckoutNotification")
    private static Queue queueCheckoutNotification;
    @Resource(mappedName = "jms/queueCheckoutNotificationFactory")
    private static ConnectionFactory queueCheckoutNotificationFactory;
    
    
    
    public static void main(String[] args)
    {
        MainApp mainApp = new MainApp(staffEntitySessionBeanRemote, productEntitySessionBeanRemote, categoryEntitySessionBeanRemote, tagEntitySessionBeanRemote, saleTransactionEntitySessionBeanRemote, checkoutBeanRemote, emailSessionBeanRemote, messageOfTheDayEntitySessionBeanRemote, queueCheckoutNotification, queueCheckoutNotificationFactory);
        mainApp.runApp();
    }
}