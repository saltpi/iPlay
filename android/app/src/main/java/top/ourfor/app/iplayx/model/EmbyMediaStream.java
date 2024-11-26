package top.ourfor.app.iplayx.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EmbyMediaStream {
    @JsonProperty("Codec")
    String codec;
    @JsonProperty("Language")
    String language;
    @JsonProperty("DisplayTitle")
    String displayTitle;
    @JsonProperty("DisplayLanguage")
    String displayLanguage;
    @JsonProperty("IsInterlaced")
    Boolean isInterlaced;
    @JsonProperty("IsDefault")
    Boolean isDefault;
    @JsonProperty("IsForced")
    Boolean isForced;
    @JsonProperty("IsHearingImpaired")
    Boolean isHearingImpaired;
    @JsonProperty("Type")
    String type;
    @JsonProperty("DeliveryMethod")
    String deliveryMethod;
    @JsonProperty("DeliveryUrl")
    String deliveryUrl;
    @JsonProperty("IsExternal")
    Boolean isExternal;
    @JsonProperty("Path")
    String path;

    public void buildUrl(String url) {
        if (deliveryUrl != null && !deliveryUrl.startsWith("http")) {
            deliveryUrl = url + deliveryUrl;
        }
    }
}
