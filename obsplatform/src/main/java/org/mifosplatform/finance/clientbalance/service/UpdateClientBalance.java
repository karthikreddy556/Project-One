package org.mifosplatform.finance.clientbalance.service;

import java.math.BigDecimal;

import org.mifosplatform.finance.adjustment.domain.Adjustment;
import org.mifosplatform.finance.clientbalance.domain.ClientBalance;
import org.mifosplatform.finance.clientbalance.domain.ClientBalanceRepository;
import org.mifosplatform.finance.payments.domain.Payment;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateClientBalance {

	private final PlatformSecurityContext context;
	private final ClientBalanceRepository clientBalanceRepository;
	private final ClientRepository clientRepository;


	@Autowired
	public UpdateClientBalance(final PlatformSecurityContext context,final ClientBalanceRepository clientBalanceRepository,final ClientRepository clientRepository) {
		this.context = context;
		this.clientBalanceRepository = clientBalanceRepository;
		this.clientRepository=clientRepository;
	}

	public ClientBalance doUpdateAdjustmentClientBalance(JsonCommand command, ClientBalance clientBalance) {

		clientBalance = calculateUpdateClientBalance(Adjustment.fromJson(command).getAdjustment_type(),
				Adjustment.fromJson(command).getAmount_paid(),clientBalance);

		return clientBalance;

	}
	
	

	public ClientBalance createAdjustmentClientBalance(JsonCommand command,ClientBalance clientBalance) {
		
		clientBalance = calculateCreateClientBalance(Adjustment.fromJson(command).getAdjustment_type(),
				Adjustment.fromJson(command).getAmount_paid(),clientBalance,command.entityId());

		return clientBalance;
	}

	public ClientBalance saveClientBalanceEntity(ClientBalance clientBalance){
		
		Client client=this.clientRepository.findOne(clientBalance.getClientId());
		
		if(client == null){
			throw new  ClientNotFoundException(clientBalance.getClientId());
		}
		
		ClientBalance resultantClientBalance =  this.clientBalanceRepository.save(clientBalance);
		return resultantClientBalance;
	}
	
	
	
	public ClientBalance calculateUpdateClientBalance(String transactionType , BigDecimal amount,ClientBalance clientBalance){
		if(amount==null)
			amount=new BigDecimal(0);
		
		if (transactionType.equalsIgnoreCase("DEBIT")) {

			clientBalance.setBalanceAmount(clientBalance.getBalanceAmount().add(amount));

		} else if (transactionType.equalsIgnoreCase("CREDIT")) {

			clientBalance.setBalanceAmount(clientBalance.getBalanceAmount().subtract(amount));
		}

		
		return clientBalance;
		
	}
	
	public ClientBalance calculateCreateClientBalance(String transactionType , BigDecimal amount,ClientBalance clientBalance,Long clientId){
		
		BigDecimal balanceAmount = BigDecimal.ZERO;
		if (transactionType.equalsIgnoreCase("DEBIT")) {
			balanceAmount = amount;
		} else if (transactionType.equalsIgnoreCase("CREDIT")) {
			balanceAmount = BigDecimal.ZERO.subtract(amount);
		}

		clientBalance = new ClientBalance(clientId, balanceAmount);
		
		return clientBalance;
		
	}

	public ClientBalance doUpdatePaymentClientBalance(Long clientid, JsonCommand command,
			ClientBalance clientBalance) {
		clientBalance = calculateUpdateClientBalance("CREDIT",
				Payment.fromJson(command,command.entityId()).getAmountPaid(),clientBalance);

		return clientBalance;
		
	
	}

	public ClientBalance createPaymentClientBalance(Long clientid, JsonCommand command,
			ClientBalance clientBalance) {
		
		clientBalance = calculateCreateClientBalance("CREDIT",Payment.fromJson(command,command.entityId()).getAmountPaid(),clientBalance, clientid);

		return clientBalance;
	}

}
