package infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;
import java.io.InputStream;
import java.lang.reflect.Type;

public class JacksonJsonMapper implements JsonMapper {
    private final ObjectMapper mapper;

    JacksonJsonMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @NotNull
    @Override
    public String toJsonString(@NotNull Object obj, @NotNull Type type) {
        try {return mapper.writeValueAsString(obj);}
        catch (Exception e) {throw new RuntimeException("JSON serialization failed", e);}
    }

    @NotNull
    @Override
    public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
        try {return mapper.readValue(json, mapper.constructType(targetType));}
        catch (Exception e) {throw new RuntimeException("JSON deserialization failed", e);}
    }

    @NotNull
    public <T> T fromJsonStream(@NotNull InputStream json, @NotNull Type targetType) {
        try {return mapper.readValue(json, mapper.constructType(targetType));}
        catch (Exception e) {throw new RuntimeException("JSON stream deserialization failed", e);}
    }
}