package arpad.bank.bankbackend.controllers.apiDTOs;

import lombok.Data;

@Data
public class TransferResponse {
	private boolean transferSuccessful;
	private String transferNotSuccessfulReason;
}
