package controllers;

import com.global.weather.GlobalWeatherSoap;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class GlobalWeatherController extends Controller {

    @Inject
    private GlobalWeatherSoap globalWeatherSoapClient;

    @Inject
    HttpExecutionContext httpExecutionContext;

    public CompletionStage<Result> getCities(String countryName) {

        long nanoStart = System.nanoTime();

        CompletableFuture<String> stringCompletableFuture
                = CompletableFuture.supplyAsync(() -> globalWeatherSoapClient.getCitiesByCountry(countryName),
                httpExecutionContext.current());

        return stringCompletableFuture
                .thenApply(cities -> {
                    long delta = System.nanoTime() - nanoStart;
                    double ddelta = ((double) delta)/((double) 1000000000);
                    System.out.println(ddelta + " sec.");
                    return ok(cities).as("text/xml");
                })
                .exceptionally(throwable -> internalServerError(throwable.getMessage()));

    }

    public CompletionStage<Result> getWeather(String countryName, String cityName) {
        return CompletableFuture.supplyAsync(() -> globalWeatherSoapClient.getWeather(cityName, countryName),
                httpExecutionContext.current())
                .thenApply(weather -> ok(weather).as("text/xml"))
                .exceptionally(throwable -> internalServerError(throwable.getMessage()));
    }
}
