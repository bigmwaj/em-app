package ca.bigmwaj.emapp.as.api.shared.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class WhereClausePatternsConverterTest {

    @Test
    void givenNullTargetType_whenConvert_thenReturnsEmptyList() {
        var converter = new WhereClausePatternsConverter(null, null);
        var result = converter.convert();
        assertNotNull(result, "convert() should not return null");
        assertTrue(result.isEmpty(), "convert() should return an empty list when patterns is null");
    }
}
