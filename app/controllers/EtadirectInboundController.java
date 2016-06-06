package controllers;

import com.etadirect.api.*;
import play.Configuration;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
import static play.libs.Json.toJson;

/**
 * @author Maks Fastovets.
 */
public class EtadirectInboundController extends Controller {
    private static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssxxx";
    private static String FSC_DEMO_COMPANY = "etadirect.api.ofsc-demo.company";
    private static String FSC_DEMO_LOGIN = "etadirect.api.ofsc-demo.login";
    private static String FSC_DEMO_PASSWORD = "etadirect.api.ofsc-demo.password ";

    @Inject
    HttpExecutionContext httpExecutionContext;

    @Inject
    Configuration config;

    @Inject
    InboundInterfacePort etadirectInboundSoapClient;

    public CompletionStage<Result> inboundInterface(String param) {

        System.out.println(param);

        InboundInterfaceElement inboundInterfaceEl = new InboundInterfaceElement();
        inboundInterfaceEl.setUser(generateUser());
        inboundInterfaceEl.setHead(generateHead());
        inboundInterfaceEl.setData(new DataElement());

        CompletableFuture<InboundInterfaceResponseElement> inboundInterfaceResponseElementCompletableFuture
                = CompletableFuture.supplyAsync(() -> etadirectInboundSoapClient.inboundInterface(inboundInterfaceEl),
                httpExecutionContext.current());

        return inboundInterfaceResponseElementCompletableFuture
                .thenApply(response -> ok(toJson(response)))
                .exceptionally(throwable -> internalServerError(throwable.getMessage()));
    }

    private UserElement generateUser() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
        ZonedDateTime dateTime = ZonedDateTime.now();
        String formattedZonedDateTimeNow = dateTime.format(formatter);

        String hashFromContent = md5Hex(formattedZonedDateTimeNow + md5Hex(config.getString(FSC_DEMO_PASSWORD)));


        UserElement userEl = new UserElement();
        userEl.setNow(formattedZonedDateTimeNow);
        userEl.setCompany(config.getString(FSC_DEMO_COMPANY));
        userEl.setLogin(config.getString(FSC_DEMO_LOGIN));
        userEl.setAuthString(hashFromContent);

        return userEl;
    }

    private HeadElement generateHead() {
        HeadElement headElement = new HeadElement();
        headElement.setPropertiesMode("appointment_only");
        headElement.setUploadType("incremental");

        KeysArray appKeysArray = new KeysArray();
        appKeysArray.getField().add("appt_number");
        AppointmentSettings appointmentSettings =  new AppointmentSettings();
        appointmentSettings.setKeys(appKeysArray);
        headElement.setAppointment(appointmentSettings);

        KeysArray invKeysArray = new KeysArray();
        invKeysArray.getField().add("invsn");
        InventorySettings inventorySettings = new InventorySettings();
        inventorySettings.setKeys(invKeysArray);
        headElement.setInventory(inventorySettings);

        return headElement;
    }

}
