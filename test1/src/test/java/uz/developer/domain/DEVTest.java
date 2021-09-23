package uz.developer.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.developer.web.rest.TestUtil;

class DEVTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DEV.class);
        DEV dEV1 = new DEV();
        dEV1.setId(1L);
        DEV dEV2 = new DEV();
        dEV2.setId(dEV1.getId());
        assertThat(dEV1).isEqualTo(dEV2);
        dEV2.setId(2L);
        assertThat(dEV1).isNotEqualTo(dEV2);
        dEV1.setId(null);
        assertThat(dEV1).isNotEqualTo(dEV2);
    }
}
