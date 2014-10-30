package org.mifosplatform.billing.currency.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

/**
 * @author hugo
 *
 */
public interface CountryCurrencyWritePlatformService {

	CommandProcessingResult createCountryCurrency(JsonCommand command);

	CommandProcessingResult updateCountryCurrency(Long entityId,
			JsonCommand command);

	CommandProcessingResult deleteCountryCurrency(Long entityId);

}
