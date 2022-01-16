package arpad.bank.bankbackend.dbmodel;

public enum TypeOfMutatie {
	BIJ{
		@Override
		public TypeOfMutatie inverted(){ return TypeOfMutatie.AF; }
	},
	AF{
		@Override
		public TypeOfMutatie inverted(){ return TypeOfMutatie.BIJ; }
	};

	/**
	 * Call this method when you want to invert the enum. This can be useful when switching to the receiving end of a transaction.
	 * @return The inverted value of the enum;
	 */
	public abstract TypeOfMutatie inverted();
}
