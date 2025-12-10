package adapter.input.rest;

import adapter.input.rest.dto.HealthResponseDto;
import adapter.input.rest.dto.VersionResponseDto;
import io.javalin.http.Context;

public class HealthController {
    private final String version;
    private final String appName;

    public HealthController(String version, String appName) {
        this.version = version;
        this.appName = appName;
    }

    public void health(Context ctx) {ctx.json(new HealthResponseDto("UP", System.currentTimeMillis()));}

    public void version(Context ctx) {ctx.json(new VersionResponseDto(version, appName));}
}