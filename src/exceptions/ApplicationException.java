package exceptions;

import services.LanguageService;

public abstract class ApplicationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ApplicationException(String locale_key) {
		super(LanguageService.translate(locale_key));
	}
}
