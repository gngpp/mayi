package com.zf1976.mayi.auth.endpoint.handler;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
//import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
//import org.springframework.security.oauth2.provider.ClientRegistrationException;
//import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
//import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
//import org.springframework.web.HttpRequestMethodNotSupportedException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
///**
// * @author mac
// * @date 2021/4/9
// */
//@RestControllerAdvice
//public class GlobalAuthenticationExceptionHandler {
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    private final WebResponseExceptionTranslator<OAuth2Exception> providerExceptionHandler = new DefaultWebResponseExceptionTranslator();
//
//    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
//    public ResponseEntity<OAuth2Exception> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) throws Exception {
//        if (this.logger.isInfoEnabled()) {
//            this.logger.info("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
//        }
//        return this.providerExceptionHandler.translate(e);
//    }
//
//    @ExceptionHandler({ClientRegistrationException.class})
//    public ResponseEntity<OAuth2Exception> handleClientRegistrationException(Exception e) throws Exception {
//        if (this.logger.isWarnEnabled()) {
//            this.logger.warn("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
//        }
//        return this.providerExceptionHandler.translate(new BadClientCredentialsException());
//    }
//
//    @ExceptionHandler({Exception.class})
//    public ResponseEntity<OAuth2Exception> handleException(Exception e) throws Exception {
//        if (this.logger.isWarnEnabled()) {
//            this.logger.warn("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
//        }
//        return this.providerExceptionHandler.translate(e);
//    }
//
//    @ExceptionHandler({OAuth2Exception.class})
//    public ResponseEntity<OAuth2Exception> handleException(OAuth2Exception e) throws Exception {
//        if (this.logger.isWarnEnabled()) {
//            this.logger.warn("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
//        }
//        return this.providerExceptionHandler.translate(e);
//    }
//}
