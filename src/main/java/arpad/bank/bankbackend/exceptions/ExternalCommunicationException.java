package arpad.bank.bankbackend.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExternalCommunicationException extends Exception{
	public ExternalCommunicationException(String message){
		super(message);
		log.error(message);
	}
}
