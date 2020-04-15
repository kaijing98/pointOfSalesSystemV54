package pointofsalesystemv54client;

import entity.StaffEntity;
import java.util.Scanner;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;
import ejb.session.stateful.CheckoutSessionBeanRemote;
import ejb.session.stateless.CategoryEntitySessionBeanRemote;
import ejb.session.stateless.EmailSessionBeanRemote;
import ejb.session.stateless.MessageOfTheDayEntitySessionBeanRemote;
import ejb.session.stateless.ProductEntitySessionBeanRemote;
import ejb.session.stateless.SaleTransactionEntitySessionBeanRemote;
import ejb.session.stateless.StaffEntitySessionBeanRemote;
import ejb.session.stateless.TagEntitySessionBeanRemote;
import entity.MessageOfTheDayEntity;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;



public class MainApp
{
    private StaffEntitySessionBeanRemote staffEntitySessionBeanRemote;
    private ProductEntitySessionBeanRemote productEntitySessionBeanRemote;
    private CategoryEntitySessionBeanRemote categoryEntitySessionBeanRemote;
    private TagEntitySessionBeanRemote tagEntitySessionBeanRemote;
    private SaleTransactionEntitySessionBeanRemote saleTransactionEntitySessionBeanRemote;
    private CheckoutSessionBeanRemote checkoutBeanRemote;
    private EmailSessionBeanRemote emailSessionBeanRemote;
    private MessageOfTheDayEntitySessionBeanRemote messageOfTheDayEntitySessionBeanRemote;
    
    private Queue queueCheckoutNotification;
    private ConnectionFactory queueCheckoutNotificationFactory;
    
    private CashierOperationModule cashierOperationModule;
    private SystemAdministrationModule systemAdministrationModule;
    
    private StaffEntity currentStaffEntity;
    
    
    
    public MainApp() 
    {        
    }

    
    
    public MainApp(StaffEntitySessionBeanRemote staffEntitySessionBeanRemote, ProductEntitySessionBeanRemote productEntitySessionBeanRemote, CategoryEntitySessionBeanRemote categoryEntitySessionBeanRemote, TagEntitySessionBeanRemote tagEntitySessionBeanRemote, SaleTransactionEntitySessionBeanRemote saleTransactionEntitySessionBeanRemote, CheckoutSessionBeanRemote checkoutBeanRemote, EmailSessionBeanRemote emailSessionBeanRemote, MessageOfTheDayEntitySessionBeanRemote messageOfTheDayEntitySessionBeanRemote, Queue queueCheckoutNotification, ConnectionFactory queueCheckoutNotificationFactory)
    {
        this.staffEntitySessionBeanRemote = staffEntitySessionBeanRemote;
        this.productEntitySessionBeanRemote = productEntitySessionBeanRemote;
        this.categoryEntitySessionBeanRemote = categoryEntitySessionBeanRemote;
        this.tagEntitySessionBeanRemote = tagEntitySessionBeanRemote;
        this.saleTransactionEntitySessionBeanRemote = saleTransactionEntitySessionBeanRemote;
        this.checkoutBeanRemote = checkoutBeanRemote;
        this.emailSessionBeanRemote = emailSessionBeanRemote;
        this.messageOfTheDayEntitySessionBeanRemote = messageOfTheDayEntitySessionBeanRemote;
        
        this.queueCheckoutNotification = queueCheckoutNotification;
        this.queueCheckoutNotificationFactory = queueCheckoutNotificationFactory;
    }
    
    
    
    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to Point-of-Sale (POS) System (v5.4) ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;
            
            while(response < 1 || response > 2)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    try
                    {
                        doLogin();
                        System.out.println("Login successful!\n");
                        
                        System.out.println("==================================================\n");
                        
                        System.out.println("*** Message Of The Day ***\n");
                        List<MessageOfTheDayEntity> messageOfTheDayEntities = messageOfTheDayEntitySessionBeanRemote.retrieveAllMessagesOfTheDay();
                        
                        for(MessageOfTheDayEntity messageOfTheDayEntity:messageOfTheDayEntities)
                        {
                            System.out.println(outputDateFormat.format(messageOfTheDayEntity.getMessageDate()) + " - " + messageOfTheDayEntity.getTitle());
                            System.out.println(messageOfTheDayEntity.getMessage() + "\n");
                        }
                        
                        System.out.println("==================================================\n");
                        
                        cashierOperationModule = new CashierOperationModule(productEntitySessionBeanRemote, saleTransactionEntitySessionBeanRemote, checkoutBeanRemote, emailSessionBeanRemote, queueCheckoutNotification, queueCheckoutNotificationFactory, currentStaffEntity);
                        systemAdministrationModule = new SystemAdministrationModule(staffEntitySessionBeanRemote, productEntitySessionBeanRemote, categoryEntitySessionBeanRemote, tagEntitySessionBeanRemote, messageOfTheDayEntitySessionBeanRemote, currentStaffEntity);
                        menuMain();
                    }
                    catch(InvalidLoginCredentialException ex) 
                    {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 2)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 2)
            {
                break;
            }
        }
    }
    
    
    
    private void doLogin() throws InvalidLoginCredentialException
    {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** POS System :: Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0)
        {
            currentStaffEntity = staffEntitySessionBeanRemote.staffLogin(username, password);      
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }
    
    
    
    private void menuMain()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Point-of-Sale (POS) System (v5.4) ***\n");
            System.out.println("You are login as " + currentStaffEntity.getFirstName() + " " + currentStaffEntity.getLastName() + " with " + currentStaffEntity.getAccessRightEnum().toString() + " rights\n");
            System.out.println("1: Cashier Operation");
            System.out.println("2: System Administration");
            System.out.println("3: Logout\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    cashierOperationModule.menuCashierOperation();
                }
                else if(response == 2)
                {
                    try
                    {
                        systemAdministrationModule.menuSystemAdministration();
                    }
                    catch (InvalidAccessRightException ex)
                    {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 3)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 3)
            {
                break;
            }
        }
    }
}