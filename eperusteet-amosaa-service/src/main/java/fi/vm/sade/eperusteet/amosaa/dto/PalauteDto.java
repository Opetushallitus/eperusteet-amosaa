package fi.vm.sade.eperusteet.amosaa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PalauteDto {
    private Integer stars;
    private String feedback;

    @JsonProperty("user-agent")
    private String userAgent;

    @JsonProperty("created-at")
    private Date createdAt;
    private String key;
    private String data;

    public void setUser_agent(String userAgent) {
        this.userAgent = userAgent;
    }
}
