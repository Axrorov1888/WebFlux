package uz.developer.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.developer.web.rest.TestUtil;

class PatternTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pattern.class);
        Pattern pattern1 = new Pattern();
        pattern1.setId(1L);
        Pattern pattern2 = new Pattern();
        pattern2.setId(pattern1.getId());
        assertThat(pattern1).isEqualTo(pattern2);
        pattern2.setId(2L);
        assertThat(pattern1).isNotEqualTo(pattern2);
        pattern1.setId(null);
        assertThat(pattern1).isNotEqualTo(pattern2);
    }
}
