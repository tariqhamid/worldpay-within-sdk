/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.worldpay.innovation;

import com.worldpay.innovation.wpwithin.rpc.WPWithin;
import com.worldpay.innovation.wpwithin.rpc.types.HCECard;
import com.worldpay.innovation.wpwithin.rpc.types.ServiceMessage;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.thrift.TException;

/**
 *
 * @author worldpay
 */
public class ConsumerMenu {
    private static final Logger log = Logger.getLogger( ConsumerMenu.class.getName() );
        
    private final WPWithin.Client sdk;
    
    public ConsumerMenu(WPWithin.Client _client) {
        log.setLevel(Level.FINE);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        log.addHandler(handler);
        this.sdk = _client;
    } 
    
    
    public MenuReturnStruct mDefaultConsumer() {
        
        try {
            mInitDefaultDevice();    
        } catch(Exception e) {
            return new MenuReturnStruct("Error with initiatiing the default device", 0);
        }
        
        try {
            mDefaultHCECredential();    
        } catch(Exception e) {
            return new MenuReturnStruct("Error with setting the default HCE Credentials", 0);
        }

	if(this.sdk == null) {
            return new MenuReturnStruct("ERR_DEVICE_NOT_INITIALISED", 0);
	}

	return new MenuReturnStruct(null, 0);
    }   
    
    
    
    public MenuReturnStruct mNewConsumer() {

        try {
            mInitNewDevice();    
        } catch(Exception e) {
            return new MenuReturnStruct("Error with initiatiing new device", 0);
        }
                
        try {
            mDefaultHCECredential();    
        } catch(Exception e) {
            return new MenuReturnStruct("Error with setting the default HCE Credentials", 0);
        }

	if(this.sdk == null) {
            return new MenuReturnStruct("ERR_DEVICE_NOT_INITIALISED", 0);
	}

	return new MenuReturnStruct(null, 0);
    }

    public MenuReturnStruct mScanService() {

	log.fine("testDiscoveryAndNegotiation");

        try {
            mInitDefaultDevice();    
        } catch(Exception e) {
            return new MenuReturnStruct("Error with initiatiing the default device", 0);
        }
        
        try {
            mDefaultHCECredential();    
        } catch(Exception e) {
            return new MenuReturnStruct("Error with setting the default HCE Credentials", 0);
        }
        
   	if(this.sdk == null) {
            return new MenuReturnStruct("ERR_DEVICE_NOT_INITIALISED", 0);
	}     
        
	log.fine("pre scan for services");
        Set<ServiceMessage> services;
        try {
            services = sdk.serviceDiscovery(20000);
        } catch (TException ex) {
            Logger.getLogger(ConsumerMenu.class.getName()).log(Level.SEVERE, "Service Discovery failed for consumer", ex);
            return new MenuReturnStruct("Service Discovery failed for consumer", 0);
        }
	log.fine("end scan for services");

        for (ServiceMessage svc : services) {
            System.out.println("(" + svc.getHostname() + ":" + svc.getPortNumber() + "/" + svc.getUrlPrefix() + ") - " + svc.getDeviceDescription());
        }
       
        return new MenuReturnStruct(null, 0);
        
    }
    
    public MenuReturnStruct mDefaultHCECredential() {

        HCECard card = new HCECard("Bilbo", "Baggins", 11, 2018, "5555555555554444", "Card", "113");

	if(this.sdk == null) {
            return new MenuReturnStruct("ERR_DEVICE_NOT_INITIALISED", 0);
	}

        try {
            sdk.initHCE(card);
        } catch(TException e) {
            return new MenuReturnStruct("Issue initialising the HCE card", 0);    
        }
        
	return new MenuReturnStruct(null, 0);
    }
        
    public MenuReturnStruct mNewHCECredential() {

	if(this.sdk == null) {
            return new MenuReturnStruct("ERR_DEVICE_NOT_INITIALISED", 0);
	}

        Scanner scanner = new Scanner(System.in);
                
	System.out.print("First Name: ");
	String firstName;
	firstName = scanner.next();

        System.out.print("Last Name: ");
	String lastName;
	lastName = scanner.next();

        System.out.print("Expiry month: ");
	String expMonthStr;
	expMonthStr = scanner.next();
        int expMonth = new Integer(expMonthStr);

        System.out.print("Expiry year: ");
	String expYearStr;
	expYearStr = scanner.next();
        int expYear = new Integer(expYearStr);

        System.out.print("CardNumber: ");
	String cardNumber;
	cardNumber = scanner.next();
        
        System.out.print("Type: ");
	String cardType;
	cardType = scanner.next();

        System.out.print("CVC: ");
	String cvc;
	cvc = scanner.next();

	HCECard card = new HCECard(firstName, lastName, expMonth, expYear, cardNumber, cardType, cvc);
	
        try {
            sdk.initHCE(card);
        } catch(TException e) {
            return new MenuReturnStruct("Failed to initHCE card", 0);
        }

	return new MenuReturnStruct(null, 0);
    }    
    
    public MenuReturnStruct mDiscoverSvcs() {
        return new MenuReturnStruct("Not implemented yet..", 0);
    }
    
    public MenuReturnStruct mGetSvcPrices() {
        return new MenuReturnStruct("Not implemented yet..", 0);
    }
    
    public MenuReturnStruct mSelectService() {
        return new MenuReturnStruct("Not implemented yet..", 0);
    }
    
    public MenuReturnStruct mMakePayment() {
        return new MenuReturnStruct("Not implemented yet..", 0);
    }    
    
    public MenuReturnStruct mConsumerStatus() {
        return new MenuReturnStruct("Not implemented yet..", 0);
    } 

    // Put in super class!!!
    private final String DEFAULT_DEVICE_NAME = "conorhwp-macbook";
    private final String DEFAULT_DEVICE_DESCRIPTION = "Conor H WP - Raspberry Pi";    
    public MenuReturnStruct mInitDefaultDevice() {

            //_sdk, err := wpwithin.Initialise(DEFAULT_DEVICE_NAME, DEFAULT_DEVICE_DESCRIPTION)
            
            try {
                sdk.setup(DEFAULT_DEVICE_NAME, DEFAULT_DEVICE_DESCRIPTION);
            } catch(TException e) {
                return new MenuReturnStruct("SDK setup failed", 1);
            }
        
            return new MenuReturnStruct(null, 0);

    }
    
    public MenuReturnStruct mInitNewDevice()  {

            System.out.println("Name of device: ");
            
            Scanner scanner = new Scanner(System.in);
            String nameOfDevice = scanner.next();
            if(null == nameOfDevice || "".equals(nameOfDevice)) {
                    return new MenuReturnStruct("Name of device not set", 0);
            }

            System.out.println("Description: ");
            String description = scanner.next();
            if(null == description || "".equals(description)) {
                    return new MenuReturnStruct("Description of device not set", 0);
            }
                    
            try {
                sdk.setup(nameOfDevice, description);
            } catch(TException e) {
                return new MenuReturnStruct("Setup of device unsucessful", 0);
            }
            
            return new MenuReturnStruct(null, 0);
            
    }
        
}
