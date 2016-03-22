package fi.vm.sade.eperusteet.amosaa.service.dokumentti;

import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

/**
 * @author isaul
 */
public class DokumenttiExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        if (throwable instanceof DokumenttiException) {
            // todo: voisi olla jokin hieman kauniimpi virheilmoitus
            throwable.printStackTrace();
        } else {
            new SimpleAsyncUncaughtExceptionHandler().handleUncaughtException(throwable, method, objects);
        }
    }
}
