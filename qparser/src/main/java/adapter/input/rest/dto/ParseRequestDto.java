package adapter.input.rest.dto;

public class ParseRequestDto {
    private String script;
    private String scriptType;

    public ParseRequestDto() {}

    public ParseRequestDto(String script, String scriptType) {
        this.script = script;
        this.scriptType = scriptType;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }
}
