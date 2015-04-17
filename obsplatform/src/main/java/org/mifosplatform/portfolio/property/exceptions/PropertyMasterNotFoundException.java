package org.mifosplatform.portfolio.property.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when a code is not found.
 */
@SuppressWarnings("serial")
public class PropertyMasterNotFoundException extends AbstractPlatformResourceNotFoundException {

    /**
     * @param propertyId
     */
    public PropertyMasterNotFoundException(final Long propertyId) {
        super("error.msg.property.not.found", "Property Code with this id"+propertyId+"not exist",propertyId);
        
    }
    
    public PropertyMasterNotFoundException(final String propertyCode) {
        super("error.msg.please.free.this.property.from assigned client", "please free this " +propertyCode+ "property from assigned client",propertyCode);
    }
    
    public PropertyMasterNotFoundException(final Long clientId,final String propertyCode) {
    	 super("error.msg.client.address.details.not found.with "+propertyCode, "Client address details  not found", clientId);
        
    }
    
   
}