package arpad.bank.bankbackend.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferIllegalException extends Exception{
	public TransferIllegalException(String reason){
		super(reason);
		log.info("Transfer is illegal: " + reason);
	}
}
